package meetings.query.dto;

import commons.dto.AttendeeId;
import commons.dto.GroupMeetingId;
import commons.dto.GroupMemberId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Option;
import lombok.Value;
import meetings.dto.AttendeesLimit;
import meetings.dto.GroupMeetingHostId;
import meetings.dto.GroupMeetingName;

import java.util.List;
import java.util.Set;

@Value
public class MeetingDetails {
    GroupMeetingId groupMeetingId;
    MeetingGroupId meetingGroupId;
    GroupMeetingHostId groupMeetingHostId;
    GroupMeetingName groupMeetingName;
    Option<AttendeesLimit> attendeesLimit;
    Set<AttendeeId> attendees;
    WaitListDetails waitListDetails;

    @Value
    public static class WaitListDetails {
        boolean allowed;
        List<GroupMemberId> groupMembers;
    }
}