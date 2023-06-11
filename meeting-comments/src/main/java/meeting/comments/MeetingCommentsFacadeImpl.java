package meeting.comments;

import commons.active.subscribers.ActiveSubscribersFinder;
import commons.dto.*;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.comments.dto.*;
import meeting.comments.query.dto.CommentDetails;
import meeting.comments.query.dto.ReplyDetails;

import java.util.List;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static io.vavr.control.Option.of;
import static java.util.function.Function.identity;
import static meeting.comments.dto.DeleteCommentFailure.COMMENT_DOESNT_EXIST;
import static meeting.comments.dto.DeleteCommentFailure.USER_IS_NOT_COMMENT_AUTHOR;
import static meeting.comments.dto.DeleteCommentFailure.USER_IS_NOT_GROUP_MEMBER;
import static meeting.comments.dto.LeaveCommentFailure.*;

@AllArgsConstructor
@Slf4j
class MeetingCommentsFacadeImpl implements MeetingCommentsFacade {
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final MeetingRepository meetingRepository;
    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final ActiveSubscribersFinder activeSubscribersFinder;

    @Override
    public Either<LeaveCommentFailure, CommentDetails> leaveComment(CommentAuthorId commentAuthorId, GroupMeetingId groupMeetingId, CommentContent commentContent) {
        if (!isSubscribed(commentAuthorId))
            return left(USER_IS_NOT_SUBSCRIBED);
        if (!meetingExists(groupMeetingId))
            return left(MEETING_DOESNT_EXIST);
        if (!commentAuthorIsGroupMember(commentAuthorId, groupMeetingId) && !commentAuthorIsGroupOrganizer(commentAuthorId, groupMeetingId))
            return left(LeaveCommentFailure.USER_IS_NOT_GROUP_MEMBER);
        if (commentContent.getContent().isBlank())
            return left(COMMENT_CONTENT_CANNOT_BE_BLANK);
        var comment = Comment.create(commentAuthorId, groupMeetingId, commentContent);
        commentRepository.save(comment);
        return right(comment.toDto());
    }

    private boolean isSubscribed(CommentAuthorId commentAuthorId) {
        return activeSubscribersFinder.contains(new UserId(commentAuthorId.getId()));
    }

    private boolean meetingExists(GroupMeetingId groupMeetingId) {
        return meetingRepository.existsById(groupMeetingId);
    }

    private boolean commentAuthorIsGroupMember(CommentAuthorId commentAuthorId, GroupMeetingId groupMeetingId) {
        return meetingRepository
                .findById(groupMeetingId)
                .map(Meeting::getMeetingGroupId)
                .map(meetingGroupId -> commentAuthorIsGroupMember(commentAuthorId, meetingGroupId))
                .getOrElse(false);
    }

    private boolean commentAuthorIsGroupMember(CommentAuthorId commentAuthorId, MeetingGroupId meetingGroupId) {
        return groupMembershipRepository
                .existsByGroupMemberIdAndMeetingGroupId(asGroupMember(commentAuthorId), meetingGroupId);
    }

    private GroupMemberId asGroupMember(CommentAuthorId commentAuthorId) {
        return new GroupMemberId(commentAuthorId.getId());
    }

    private boolean commentAuthorIsGroupOrganizer(CommentAuthorId commentAuthorId, GroupMeetingId groupMeetingId) {
        return meetingRepository
                .findById(groupMeetingId)
                .map(meeting -> meeting.isGroupOrganizer(commentAuthorId))
                .getOrElse(false);
    }

    @Override
    public Option<DeleteCommentFailure> deleteComment(CommentAuthorId commentAuthorId, CommentId commentId) {
        return commentRepository
                .findById(commentId)
                .toEither(COMMENT_DOESNT_EXIST)
                .map(comment -> remove(comment, commentAuthorId))
                .fold(Option::of, identity());
    }

    private Option<DeleteCommentFailure> remove(Comment comment, CommentAuthorId commentAuthorId) {
        if (!comment.getCommentAuthorId().equals(commentAuthorId))
            return of(USER_IS_NOT_COMMENT_AUTHOR);
        if (!commentAuthorIsGroupMember(commentAuthorId, comment.getGroupMeetingId()))
            return of(USER_IS_NOT_GROUP_MEMBER);
        commentRepository.removeById(comment.getCommentId());
        return Option.none();
    }

    @Override
    public Option<DeleteCommentFailure> deleteComment(GroupOrganizerId groupOrganizerId, CommentId commentId) {
        return null;
    }

    @Override
    public Option<LikeCommentFailure> likeComment(LikeAuthorId likeAuthorId, CommentId commentId) {
        return null;
    }

    @Override
    public Either<ReplyToCommentFailure, ReplyDetails> replyToComment(ReplyAuthorId replyAuthorId, CommentId commentId, ReplyContent replyContent) {
        return null;
    }

    @Override
    public Option<DeleteReplyFailure> deleteReply(ReplyAuthorId replyAuthorId, ReplyId replyId) {
        return null;
    }

    @Override
    public Option<DeleteReplyFailure> deleteReply(GroupOrganizerId groupOrganizerId, ReplyId replyId) {
        return null;
    }

    @Override
    public Option<LikeReplyFailure> likeReply(LikeAuthorId likeAuthorId, ReplyId replyId) {
        return null;
    }

    @Override
    public void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        groupRepository.save(new Group(meetingGroupId, groupOrganizerId));
    }

    @Override
    public void meetingGroupWasRemoved(MeetingGroupId meetingGroupId) {
        groupRepository.removeById(meetingGroupId);
        groupMembershipRepository.removeByMeetingGroupId(meetingGroupId);
        meetingRepository
                .findByMeetingGroupId(meetingGroupId)
                .stream()
                .map(Meeting::getGroupMeetingId)
                .peek(commentRepository::removeByGroupMeetingId)
                .peek(replyRepository::removeByGroupMeetingId)
                .forEach(meetingRepository::removeById);
    }

    @Override
    public void newMemberJoinedGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        groupMembershipRepository.save(GroupMembership.create(groupMemberId, meetingGroupId));
    }

    @Override
    public void memberLeftTheMeetingGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        groupMembershipRepository.removeByGroupMemberIdAndMeetingGroupId(groupMemberId, meetingGroupId);
    }

    @Override
    public void newMeetingScheduled(GroupMeetingId groupMeetingId, MeetingGroupId meetingGroupId) {
        groupRepository
                .findById(meetingGroupId)
                .map(Group::getGroupOrganizerId)
                .map(groupOrganizerId -> new Meeting(groupMeetingId, meetingGroupId, groupOrganizerId))
                .map(meetingRepository::save)
                .onEmpty(() -> log.error("failed to find meeting group with id {}, Meeting entity will be created without group organizer id, so organizer will not be able to commit any actions in this module", meetingGroupId.getId()))
                .onEmpty(() -> meetingRepository.save(new Meeting(groupMeetingId, meetingGroupId, null)));
    }

    @Override
    public List<CommentDetails> findCommentsByMeetingId(GroupMeetingId groupMeetingId) {
        return commentRepository
                .findByGroupMeetingId(groupMeetingId)
                .stream().map(Comment::toDto)
                .toList();
    }

    @Override
    public List<CommentDetails> findCommentsByAuthorId(CommentAuthorId commentAuthorId) {
        return commentRepository
                .findByCommentAuthorId(commentAuthorId)
                .stream().map(Comment::toDto)
                .toList();
    }

    @Override
    public List<ReplyDetails> findRepliesByCommentId(CommentId commentId) {
        return replyRepository
                .findByCommentId(commentId)
                .stream().map(Reply::toDto)
                .toList();
    }

    @Override
    public List<ReplyDetails> findRepliesByAuthorId(ReplyAuthorId replyAuthorId) {
        return replyRepository
                .findByReplyAuthorId(replyAuthorId)
                .stream().map(Reply::toDto)
                .toList();
    }
}