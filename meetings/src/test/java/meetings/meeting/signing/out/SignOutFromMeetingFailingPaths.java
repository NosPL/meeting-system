package meetings.meeting.signing.out;

import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Option;
import meetings.commons.TestSetup;
import org.junit.Test;

import static meetings.dto.SignOutFailure.MEETING_DOES_NOT_EXIST;
import static meetings.dto.SignOutFailure.USER_WAS_NOT_SIGN_IN;
import static org.junit.Assert.assertEquals;

public class SignOutFromMeetingFailingPaths extends TestSetup {
    private final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer-id");
    private final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group-id");
    private final GroupMemberId groupMemberId = new GroupMemberId("group-member-id");

    @Test
    public void userShouldFailToSignOutFromMeetingWithoutBeingSignedIn() {
//        given that meeting group was created
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
//        and meeting was scheduled
        var groupMeetingId = scheduleMeeting(groupOrganizerId, meetingGroupId);
//        and user joined the group
        meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
//        when user tries to sign out from meeting
        var result = meetingsFacade.signOutFromMeeting(asAttendee(groupMemberId), groupMeetingId);
//        then he fails
        assertEquals(Option.of(USER_WAS_NOT_SIGN_IN), result);
    }

    @Test
    public void userShouldFailToSignOutFromNotExistingMeeting() {
//        given that meeting group was created
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
//        and meeting was scheduled
        var groupMeetingId = scheduleMeeting(groupOrganizerId, meetingGroupId);
//        and user joined the group
        meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
//        and user signed up for the meeting
        subscriptionRenewed(groupMemberId);
        assert meetingsFacade.signUpForMeeting(groupMemberId, groupMeetingId).isEmpty();
//        when user tries to sign out from other meeting
        var result = meetingsFacade.signOutFromMeeting(asAttendee(groupMemberId), randomGroupMeetingId());
//        then he fails
        assertEquals(Option.of(MEETING_DOES_NOT_EXIST), result);
    }
}