package meeting.comments;

import commons.dto.GroupMeetingId;
import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Either;
import io.vavr.control.Option;
import meeting.comments.dto.*;
import meeting.comments.query.dto.CommentDetails;
import meeting.comments.query.dto.ReplyDetails;

import java.util.List;

public interface MeetingCommentsFacade {
    
    Either<LeaveCommentFailure, CommentDetails> leaveComment(CommentAuthorId commentAuthorId, GroupMeetingId groupMeetingId, CommentContent commentContent);

    Option<DeleteCommentFailure> deleteComment(CommentAuthorId commentAuthorId, CommentId commentId);

    Option<DeleteCommentFailure> deleteComment(GroupOrganizerId groupOrganizerId, CommentId commentId);

    Option<LikeCommentFailure> likeComment(LikeAuthorId likeAuthorId, CommentId commentId);

    Either<ReplyToCommentFailure, ReplyDetails> replyToComment(ReplyAuthorId replyAuthorId, CommentId commentId, ReplyContent replyContent);

    Option<DeleteReplyFailure> deleteReply(ReplyAuthorId replyAuthorId, ReplyId replyId);

    Option<DeleteReplyFailure> deleteReply(GroupOrganizerId groupOrganizerId, ReplyId replyId);

    Option<LikeReplyFailure> likeReply(LikeAuthorId likeAuthorId, ReplyId replyId);

    void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId);

    void meetingGroupWasRemoved(MeetingGroupId meetingGroupId);

    void newMemberJoinedGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId);

    void memberLeftTheMeetingGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId);

    void newMeetingScheduled(GroupMeetingId groupMeetingId, MeetingGroupId meetingGroupId);

    List<CommentDetails> findCommentsByMeetingId(GroupMeetingId groupMeetingId);

    List<CommentDetails> findCommentsByAuthorId(CommentAuthorId commentAuthorId);

    List<ReplyDetails> findRepliesByCommentId(CommentId commentId);

    List<ReplyDetails> findRepliesByAuthorId(ReplyAuthorId replyAuthorId);
}