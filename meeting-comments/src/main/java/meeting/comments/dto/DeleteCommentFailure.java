package meeting.comments.dto;

public enum DeleteCommentFailure {
    USER_IS_NOT_COMMENT_AUTHOR,
    USER_IS_NOT_GROUP_MEMBER,
    COMMENT_DOESNT_EXIST,
    MEETING_DOESNT_EXIST
}