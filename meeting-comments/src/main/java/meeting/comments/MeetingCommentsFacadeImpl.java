package meeting.comments;

import commons.active.subscribers.ActiveSubscribersFinder;
import commons.dto.GroupMeetingId;
import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import meeting.comments.dto.*;
import meeting.comments.query.dto.CommentDetails;
import meeting.comments.query.dto.ReplyDetails;

import java.util.List;

@AllArgsConstructor
class MeetingCommentsFacadeImpl implements MeetingCommentsFacade {
    private final ActiveSubscribersFinder activeSubscribersFinder;

    @Override
    public Either<LeaveCommentFailure, CommentDetails> leaveComment(CommentAuthorId commentAuthorId, GroupMeetingId groupMeetingId, CommentContent commentContent) {
        return null;
    }

    @Override
    public Option<DeleteCommentFailure> deleteComment(CommentAuthorId commentAuthorId, CommentId commentId) {
        return null;
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

    }

    @Override
    public void meetingGroupWasRemoved(MeetingGroupId meetingGroupId) {

    }

    @Override
    public void newMemberJoinedGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {

    }

    @Override
    public void memberLeftTheMeetingGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {

    }

    @Override
    public void newMeetingScheduled(GroupMeetingId groupMeetingId, MeetingGroupId meetingGroupId) {

    }

    @Override
    public List<CommentDetails> findCommentsByMeetingId(GroupMeetingId groupMeetingId) {
        return null;
    }

    @Override
    public List<CommentDetails> findCommentsByAuthorId(CommentAuthorId commentAuthorId) {
        return null;
    }

    @Override
    public List<ReplyDetails> findRepliesByCommentId(CommentId commentId) {
        return null;
    }

    @Override
    public List<ReplyDetails> findRepliesByAuthorId(ReplyAuthorId replyAuthorId) {
        return null;
    }
}