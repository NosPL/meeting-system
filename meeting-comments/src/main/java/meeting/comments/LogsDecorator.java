package meeting.comments;

import commons.dto.GroupMeetingId;
import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.comments.dto.*;
import meeting.comments.query.dto.CommentDetails;
import meeting.comments.query.dto.ReplyDetails;

import java.util.List;

@AllArgsConstructor
@Slf4j
class LogsDecorator implements MeetingCommentsFacade {
    private final MeetingCommentsFacade meetingCommentsFacade;

    @Override
    public Either<LeaveCommentFailure, CommentDetails> leaveComment(CommentAuthorId commentAuthorId, GroupMeetingId groupMeetingId, CommentContent commentContent) {
        return meetingCommentsFacade
                .leaveComment(commentAuthorId, groupMeetingId, commentContent)
                .peek(commentDetails -> log.info("comment author left the comment for meeting, comment author id {}, group meeting id {}, comment id {}", commentAuthorId.getId(), groupMeetingId.getId(), commentDetails.getCommentId().getId()))
                .peekLeft(failure -> log.info("comment author failed to leave the comment for meeting, comment author id {}, group meeting id {}, reason: {}", commentAuthorId.getId(), groupMeetingId.getId(), failure));
    }

    @Override
    public Option<DeleteCommentFailure> deleteComment(CommentAuthorId commentAuthorId, CommentId commentId) {
        return meetingCommentsFacade
                .deleteComment(commentAuthorId, commentId)
                .peek(failure -> log.info("comment author failed to delete the comment, comment author id {}, reason: {}", commentAuthorId.getId(), failure))
                .onEmpty(() -> log.info("comment author deleted the comment, comment author id {}, comment id {}", commentAuthorId.getId(), commentId.getId()));
    }

    @Override
    public Option<DeleteCommentFailure> deleteComment(GroupOrganizerId groupOrganizerId, CommentId commentId) {
        return meetingCommentsFacade
                .deleteComment(groupOrganizerId, commentId)
                .peek(failure -> log.info("group organizer failed to delete comment, group organizer id {}, comment id {}, reason: {} ", groupOrganizerId.getId(), commentId.getId(), failure))
                .onEmpty(() -> log.info("group organizer deleted comment, group organizer id {}, comment id {}", groupOrganizerId.getId(), commentId.getId()));
    }

    @Override
    public Option<LikeCommentFailure> likeComment(LikeAuthorId likeAuthorId, CommentId commentId) {
        return meetingCommentsFacade
                .likeComment(likeAuthorId, commentId)
                .peek(failure -> log.info("like author failed to like the comment, like author id {}, comment id {}, reason: {}", likeAuthorId.getId(), commentId.getId(), failure))
                .onEmpty(() -> log.info("like author liked the comment, like author id {}, comment id {}", likeAuthorId.getId(), commentId.getId()));
    }

    @Override
    public Either<ReplyToCommentFailure, ReplyDetails> replyToComment(ReplyAuthorId replyAuthorId, CommentId commentId, ReplyContent replyContent) {
        return meetingCommentsFacade
                .replyToComment(replyAuthorId, commentId, replyContent)
                .peek(replyDetails -> log.info("reply author replied to comment, reply author id {}, reply id {}, comment id {}", replyAuthorId.getId(), replyDetails.getReplyId(), commentId.getId()))
                .peekLeft(failure -> log.info("reply author failed to reply to comment reply author id {}, comment id {}, reason: {}", replyAuthorId.getId(), commentId.getId(), failure));
    }

    @Override
    public Option<DeleteReplyFailure> deleteReply(ReplyAuthorId replyAuthorId, ReplyId replyId) {
        return meetingCommentsFacade
                .deleteReply(replyAuthorId, replyId)
                .peek(failure -> log.info("reply author failed delete reply, reply author id {}, reply id {}, reason: {}", replyAuthorId.getId(), replyId.getId(), failure))
                .onEmpty(() -> log.info("reply author deleted reply, reply author id {}, reply id {}", replyAuthorId.getId(), replyId.getId()));
    }

    @Override
    public Option<DeleteReplyFailure> deleteReply(GroupOrganizerId groupOrganizerId, ReplyId replyId) {
        return meetingCommentsFacade
                .deleteReply(groupOrganizerId, replyId)
                .peek(failure -> log.info("group organizer failed to delete reply, group organizer id {}, reply id {}, reason: {}", groupOrganizerId.getId(), replyId.getId(), failure))
                .onEmpty(() -> log.info("group organizer deleted reply, group organizer id {}, reply id {}", groupOrganizerId.getId(), replyId.getId()));
    }

    @Override
    public Option<LikeReplyFailure> likeReply(LikeAuthorId likeAuthorId, ReplyId replyId) {
        return meetingCommentsFacade
                .likeReply(likeAuthorId, replyId)
                .peek(failure -> log.info("like author failed to like reply, like author id {}, reply id {}, reason: {}", likeAuthorId.getId(), replyId.getId(), failure))
                .onEmpty(() -> log.info("like author liked reply, like author id {}, reply id {}", likeAuthorId.getId(), replyId.getId()));
    }

    @Override
    public void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        log.info("new meeting group was created, group organizer id {}, meeting group id {}", groupOrganizerId.getId(), meetingGroupId.getId());
        meetingCommentsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
    }

    @Override
    public void meetingGroupWasRemoved(MeetingGroupId meetingGroupId) {
        log.info("meeting group was removed, meeting group id {}", meetingGroupId.getId());
        meetingCommentsFacade.meetingGroupWasRemoved(meetingGroupId);
    }

    @Override
    public void newMemberJoinedGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        log.info("new member joined group, group member id {}, meeting group id {}", groupMemberId.getId(), meetingGroupId.getId());
        meetingCommentsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
    }

    @Override
    public void memberLeftTheMeetingGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        log.info("member left the group, group member id {}, meeting group id {}", groupMemberId.getId(), meetingGroupId.getId());
        meetingCommentsFacade.memberLeftTheMeetingGroup(groupMemberId, meetingGroupId);
    }

    @Override
    public void newMeetingScheduled(GroupMeetingId groupMeetingId, MeetingGroupId meetingGroupId) {
        log.info("new meeting scheduled, group meeting id {}, meeting group id {}", groupMeetingId.getId(), meetingGroupId.getId());
        meetingCommentsFacade.newMeetingScheduled(groupMeetingId, meetingGroupId);
    }

    @Override
    public List<CommentDetails> findCommentsByMeetingId(GroupMeetingId groupMeetingId) {
        return meetingCommentsFacade.findCommentsByMeetingId(groupMeetingId);
    }

    @Override
    public List<CommentDetails> findCommentsByAuthorId(CommentAuthorId commentAuthorId) {
        return meetingCommentsFacade.findCommentsByAuthorId(commentAuthorId);
    }

    @Override
    public List<ReplyDetails> findRepliesByCommentId(CommentId commentId) {
        return meetingCommentsFacade.findRepliesByCommentId(commentId);
    }

    @Override
    public List<ReplyDetails> findRepliesByAuthorId(ReplyAuthorId replyAuthorId) {
        return meetingCommentsFacade.findRepliesByAuthorId(replyAuthorId);
    }
}