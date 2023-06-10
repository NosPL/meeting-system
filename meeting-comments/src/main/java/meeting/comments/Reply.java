package meeting.comments;


import commons.dto.GroupMeetingId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import meeting.comments.dto.CommentId;
import meeting.comments.dto.ReplyAuthorId;
import meeting.comments.dto.ReplyContent;
import meeting.comments.dto.ReplyId;
import meeting.comments.query.dto.LikesCount;
import meeting.comments.query.dto.ReplyDetails;

@AllArgsConstructor
@Getter
class Reply {
    private ReplyId replyId;
    private ReplyAuthorId replyAuthorId;
    private CommentId commentId;
    private GroupMeetingId groupMeetingId;
    private ReplyContent replyContent;
    private LikesCount likesCount;

    ReplyDetails toDto() {
        return new ReplyDetails(replyId, replyAuthorId, commentId, replyContent, likesCount);
    }
}