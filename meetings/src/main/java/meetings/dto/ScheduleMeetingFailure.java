package meetings.dto;

public enum ScheduleMeetingFailure {
    MEETING_DATE_IS_NOT_3_DAYS_IN_ADVANCE,
    USER_IS_NOT_GROUP_ORGANIZER,
    GROUP_ORGANIZER_DOES_NOT_HAVE_ACTIVE_SUBSCRIPTION,
    PROPOSED_MEETING_HOST_DOES_NOT_HAVE_ACTIVE_SUBSCRIPTION,
    PROPOSED_MEETING_HOST_IS_NOT_MEETING_GROUP_MEMBER,
    MEETING_GROUP_DOES_NOT_EXIST,
    MEETING_NAME_CANNOT_BE_BLANK,
    MEETING_NAME_IS_NOT_UNIQUE
}