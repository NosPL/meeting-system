package meetings.wait.list.sign;

import commons.dto.*;
import meetings.commons.TestSetup;
import meetings.dto.AttendeesLimit;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class WaitListTestSetup extends TestSetup {

    protected List<AttendeeId> fillMeetingWithAttendees(GroupMeetingId groupMeetingId, MeetingGroupId meetingGroupId, AttendeesLimit attendeesLimit) {
        int limit = attendeesLimit.getLimit();
        List<AttendeeId> attendees = new LinkedList<>();
        for (int i = 0; i < limit; i++) {
            var groupMemberId = randomGroupMemberId();
            subscriptionRenewed(groupMemberId);
            meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
            assert meetingsFacade.signUpForMeeting(groupMemberId, groupMeetingId).isEmpty();
            attendees.add(asAttendee(groupMemberId));
        }
        return attendees;
    }

    protected GroupMeetingId randomGroupMeetingId() {
        return new GroupMeetingId(UUID.randomUUID().toString());
    }

    protected void subscriptionExpired(GroupMemberId groupMemberId) {
        activeSubscribers.remove(new UserId(groupMemberId.getId()));
    }

    protected GroupMemberId asMember(AttendeeId attendeeId) {
        return new GroupMemberId(attendeeId.getId());
    }
}