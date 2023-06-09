package meetings.wait.list.sign;

import commons.dto.GroupMeetingId;
import commons.dto.GroupMemberId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import meetings.commons.TestSetup;
import meetings.dto.AttendeesLimit;

import java.util.UUID;

public class WaitListTestSetup extends TestSetup {

    protected void fillMeetingWithAttendees(GroupMeetingId groupMeetingId, MeetingGroupId meetingGroupId, AttendeesLimit attendeesLimit) {
        int limit = attendeesLimit.getLimit();
        for (int i = 0; i < limit; i++) {
            var groupMemberId = randomGroupMemberId();
            subscriptionRenewed(groupMemberId);
            meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
            assert meetingsFacade.signUpForMeeting(groupMemberId, groupMeetingId).isEmpty();
        }
    }

    protected GroupMeetingId randomGroupMeetingId() {
        return new GroupMeetingId(UUID.randomUUID().toString());
    }

    protected void subscriptionExpired(GroupMemberId groupMemberId) {
        activeSubscribers.remove(new UserId(groupMemberId.getId()));
    }
}