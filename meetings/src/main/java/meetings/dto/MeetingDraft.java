package meetings.dto;

import commons.dto.MeetingGroupId;
import lombok.Value;

import java.time.LocalDate;

@Value
public class MeetingDraft {
    MeetingGroupId meetingGroupId;
    LocalDate localDate;
    GroupMeetingHostId groupMeetingHostId;
    GroupMeetingName groupMeetingName;
}