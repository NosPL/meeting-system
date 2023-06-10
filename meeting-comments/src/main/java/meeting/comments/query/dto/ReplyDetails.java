package meeting.comments.query.dto;

import commons.dto.GroupMeetingId;
import lombok.Value;
import meeting.comments.dto.CommentId;
import meeting.comments.dto.ReplyAuthorId;
import meeting.comments.dto.ReplyContent;
import meeting.comments.dto.ReplyId;

@Value
public class ReplyDetails {
    ReplyId replyId;
    ReplyAuthorId replyAuthorId;
    CommentId commentId;
    ReplyContent replyContent;
    LikesCount likesCount;
}