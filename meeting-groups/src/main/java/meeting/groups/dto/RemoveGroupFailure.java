package meeting.groups.dto;

public enum RemoveGroupFailure {
    USER_IS_NOT_GROUP_ORGANIZER,
    GROUP_DOESNT_EXIST,
    GROUP_HAS_SCHEDULED_MEETINGS
}