package meeting.comments.dto;

public enum LeaveCommentFailure {
    USER_IS_NOT_SUBSCRIBED,
    USER_IS_NOT_GROUP_MEMBER,
    MEETING_DOESNT_EXIST,
    COMMENT_CONTENT_CANNOT_BE_BLANK
}