package meetings.wait.list.sign.on;

import commons.dto.*;
import io.vavr.control.Option;
import meetings.dto.AttendeesLimit;
import meetings.dto.MeetingDraft;
import meetings.dto.WaitList;
import meetings.wait.list.sign.WaitListTestSetup;
import org.junit.Before;
import org.junit.Test;

import static meetings.dto.SignOnWaitListFailure.*;
import static meetings.dto.WaitList.WAIT_LIST_AVAILABLE;
import static meetings.dto.WaitList.WAIT_LIST_NOT_AVAILABLE;
import static org.junit.Assert.assertEquals;

public class SignOnMeetingWaitListFailingPaths extends WaitListTestSetup {
    private final GroupMemberId groupMemberId = new GroupMemberId("group-member-id");
    private GroupMeetingId groupMeetingId;
    private final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer-id");
    private final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group-id");
    private final AttendeesLimit attendeesLimit = new AttendeesLimit(3);

    @Before
    public void signOnWaitListSetup() {
        subscriptionRenewed(groupMemberId);
        subscriptionRenewed(groupOrganizerId);
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
        meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
        groupMeetingId = meetingsFacade
                .scheduleNewMeeting(groupOrganizerId, meetingDraft(WAIT_LIST_AVAILABLE, attendeesLimit))
                .get();
    }

    @Test
    public void userShouldFailToSignOnWaitListOfNotExistingMeeting() {
//        when user tries to sign on wait list of not existing meeting
        var result = meetingsFacade.signOnMeetingWaitList(groupMemberId, randomGroupMeetingId());
//        then he fails
        assertEquals(Option.of(GROUP_MEETING_DOESNT_EXIST), result);
    }

    @Test
    public void unsubscribedUserShouldFailToSignOnWaitList() {
//        given that user is not subscribed
        subscriptionExpired(groupMemberId);
//        and meeting attendee limit was reached
        fillMeetingWithAttendees(groupMeetingId, meetingGroupId, attendeesLimit);
//         when user tries to sign on wait list
        var result = meetingsFacade.signOnMeetingWaitList(groupMemberId, groupMeetingId);
//        then he fails
        assertEquals(Option.of(USER_IS_NOT_SUBSCRIBED), result);
    }

    @Test
    public void userShouldFailToSignOnWaitListIfIfItIsNotAvailable() {
//        given that for scheduled meeting wait list is not available
        groupMeetingId = scheduleMeeting(groupOrganizerId, meetingDraft(WAIT_LIST_NOT_AVAILABLE, attendeesLimit));
//        and meeting attendee limit was reached
        fillMeetingWithAttendees(groupMeetingId, meetingGroupId, attendeesLimit);
//        when user tries to sign on wait list
        var result = meetingsFacade.signOnMeetingWaitList(groupMemberId, groupMeetingId);
//        then he fails
        assertEquals(Option.of(WAIT_LIST_IS_NOT_AVAILABLE), result);
    }

    @Test
    public void userThatIsNotGroupMemberShouldFailToSignOnWaitList() {
//        given that member left the group
        meetingsFacade.memberLeftTheMeetingGroup(groupMemberId, meetingGroupId);
//        when he tries to sign on wait list
        var result = meetingsFacade.signOnMeetingWaitList(groupMemberId, groupMeetingId);
//        then he fails
        assertEquals(Option.of(USER_IS_NOT_GROUP_MEMBER), result);
    }

    @Test
    public void userShouldFailToSignOnWaitListIfAttendeeLimitIsNotReached() {
//        when user tries to sign on wait list
        var result = meetingsFacade.signOnMeetingWaitList(groupMemberId, groupMeetingId);
//        then he fails
        assertEquals(Option.of(ATTENDEES_LIMIT_IS_NOT_REACHED), result);
    }

    protected MeetingDraft meetingDraft(WaitList waitList) {
        return new MeetingDraft(
                meetingGroupId,
                calendar.getCurrentDate().plusDays(4),
                asHost(groupOrganizerId),
                uniqueNotBlankMeetingName(),
                Option.none(),
                waitList);
    }

    protected MeetingDraft meetingDraft(WaitList waitList, AttendeesLimit attendeesLimit) {
        return new MeetingDraft(
                meetingGroupId,
                calendar.getCurrentDate().plusDays(4),
                asHost(groupOrganizerId),
                uniqueNotBlankMeetingName(),
                Option.of(attendeesLimit),
                waitList);
    }
}