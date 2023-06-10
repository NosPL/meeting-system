package meeting.comments.query.dto;

import commons.dto.GroupMeetingId;
import lombok.Value;
import meeting.comments.dto.CommentAuthorId;
import meeting.comments.dto.CommentContent;
import meeting.comments.dto.CommentId;

@Value
public class CommentDetails {
    CommentId commentId;
    GroupMeetingId groupMeetingId;
    CommentAuthorId commentAuthorId;
    CommentContent commentContent;
    RepliesCount repliesCount;
    LikesCount likesCount;
}