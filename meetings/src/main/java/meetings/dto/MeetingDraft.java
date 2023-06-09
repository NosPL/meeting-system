package meetings.dto;

import commons.dto.MeetingGroupId;
import io.vavr.control.Option;
import lombok.Value;

import java.time.LocalDate;

@Value
public class MeetingDraft {
    MeetingGroupId meetingGroupId;
    LocalDate meetingDate;
    GroupMeetingHostId groupMeetingHostId;
    GroupMeetingName groupMeetingName;
    Option<AttendeesLimit> attendeesLimit;
    WaitList waitList;
}