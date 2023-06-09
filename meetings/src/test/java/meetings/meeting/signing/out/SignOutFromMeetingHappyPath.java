package meetings.meeting.signing.out;

import commons.dto.AttendeeId;
import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Option;
import meetings.commons.TestSetup;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SignOutFromMeetingHappyPath extends TestSetup {
    private final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer");
    private final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group-id");
    private final GroupMemberId groupMemberId = new GroupMemberId("group-member-id");

    @Test
    public void test() {
//        given that meeting group was created
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
//        and meeting was scheduled
        var groupMeetingId = scheduleMeeting(groupOrganizerId, meetingGroupId);
//        and user joined the meeting group
        meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
//        and user signed up for meeting
        subscriptionRenewed(groupMemberId);
        assert meetingsFacade.signUpForMeeting(groupMemberId, groupMeetingId).isEmpty();
//        when he tries to sign out from meeting
        var result = meetingsFacade.signOutFromMeeting(asAttendee(groupMemberId), groupMeetingId);
//        then he succeeds
        assertEquals(Option.none(), result);
    }
}