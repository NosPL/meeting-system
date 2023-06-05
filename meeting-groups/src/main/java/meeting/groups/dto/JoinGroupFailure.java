package meeting.groups.dto;

public enum JoinGroupFailure {
    USER_SUBSCRIPTION_IS_NOT_ACTIVE,
    USER_ALREADY_JOINED_GROUP,
    MEETING_GROUP_DOES_NOT_EXIST,
    USER_IS_GROUP_ORGANIZER
}