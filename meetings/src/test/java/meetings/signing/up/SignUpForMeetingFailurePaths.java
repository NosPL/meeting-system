package meetings.signing.up;

import commons.dto.*;
import io.vavr.control.Option;
import meetings.dto.AttendeesLimit;
import meetings.dto.GroupMeetingName;
import meetings.dto.MeetingDraft;
import meetings.dto.WaitList;
import org.junit.Before;
import org.junit.Test;

import static meetings.dto.SignUpForMeetingFailure.*;
import static meetings.dto.WaitList.WAIT_LIST_AVAILABLE;
import static org.junit.Assert.assertEquals;

public class SignUpForMeetingFailurePaths extends SigningUpSetup {
    private final GroupMemberId groupMemberId = new GroupMemberId("group-member-id");
    private final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer");
    private final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group");
    private final GroupMemberId groupMemberAndHost = new GroupMemberId("group-member-and-host");
    private final Option<AttendeesLimit> attendeesLimit = Option.of(new AttendeesLimit(5));
    private GroupMeetingId groupMeetingId;

    @Before
    public void init() {
        subscriptionRenewed(groupMemberId);
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
        meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
        MeetingDraft meetingDraft = getMeetingDraft();
        groupMeetingId = scheduleMeeting(groupOrganizerId, meetingDraft);
    }

    @Test
    public void unsubscribedGroupMemberShouldFailToSignUpForMeeting() {
//        given that meeting group member is not subscribed
        subscriptionExpired(groupMemberId);
//        when he tries to sign up for meeting
        var result = meetingsFacade.signUpForMeeting(groupMemberId, groupMeetingId);
//        then failure
        assertEquals(Option.of(GROUP_MEMBER_IS_NOT_SUBSCRIBED), result);
    }

    @Test
    public void groupMemberShouldFailToSignUpForNotExistingMeeting() {
//        when group member tries to sign up for not existing meeting
        var result = meetingsFacade.signUpForMeeting(groupMemberId, randomMeetingId());
//        then failure
        assertEquals(Option.of(MEETING_DOES_NOT_EXIST), result);
    }

    @Test
    public void userThatIsNotGroupMemberShouldFailToSignUpForMeeting() {
//        given that group member left the group
        meetingsFacade.memberLeftTheMeetingGroup(groupMemberId, meetingGroupId);
//        when he tries to sign up for meeting
        var result = meetingsFacade.signUpForMeeting(groupMemberId, groupMeetingId);
//        then failure
        assertEquals(Option.of(USER_IS_NOT_GROUP_MEMBER), result);
    }

    @Test
    public void groupMemberShouldFailToSignUpForMeetingWithoutFreeSlots() {
//        given that attendees limit is x
        var x = getAttendeesLimitInt();
//        and x users signed up for meeting
        signUpXUsersForMeeting(groupMeetingId, x);
//        when group member tries to sign up for meeting
        var result = meetingsFacade.signUpForMeeting(groupMemberId, groupMeetingId);
//        then failure
        assertEquals(Option.of(NO_FREE_ATTENDEE_SLOTS), result);
    }

    @Test
    public void groupMemberShouldFailToSignUpForMeetingIfHeIsMeetingHost() {
//        when group member, that is also meeting host, tries to sign up for meeting
        var result = meetingsFacade.signUpForMeeting(groupMemberAndHost, groupMeetingId);
//        then failure
        assertEquals(Option.of(MEETING_HOST_CANNOT_SIGN_UP_FOR_MEETING), result);
    }

    private void signUpXUsersForMeeting(GroupMeetingId groupMeetingId, Integer usersCount) {
        for (int i = 0; i < usersCount; i++) {
            var groupMemberId = createGroupMemberId();
            meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
            subscriptionRenewed(groupMemberId);
            assert meetingsFacade.signUpForMeeting(groupMemberId, groupMeetingId).isEmpty();
        }
    }

    private Integer getAttendeesLimitInt() {
        return attendeesLimit.map(AttendeesLimit::getLimit).get();
    }

    private MeetingDraft getMeetingDraft() {
        return new MeetingDraft(
                meetingGroupId,
                calendar.getCurrentDate().plusDays(3),
                asHost(groupMemberAndHost),
                new GroupMeetingName("some-name"),
                attendeesLimit,
                WAIT_LIST_AVAILABLE);
    }
}