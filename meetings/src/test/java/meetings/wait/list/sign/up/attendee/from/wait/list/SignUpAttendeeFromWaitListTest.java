package meetings.wait.list.sign.up.attendee.from.wait.list;

import commons.dto.*;
import io.vavr.control.Option;
import meetings.dto.AttendeesLimit;
import meetings.dto.GroupMeetingName;
import meetings.dto.MeetingDraft;
import meetings.dto.WaitList;
import meetings.query.dto.MeetingDetails;
import meetings.wait.list.sign.WaitListTestSetup;
import org.junit.Before;
import org.junit.Test;

import static meetings.dto.WaitList.WAIT_LIST_AVAILABLE;
import static org.mockito.Mockito.verify;

public class SignUpAttendeeFromWaitListTest extends WaitListTestSetup {
    private final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer");
    private final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group");
    private final GroupMemberId waitListGroupMemberId = new GroupMemberId("wait-list-group-member");
    private final AttendeesLimit attendeesLimit = new AttendeesLimit(4);

    @Before
    public void signUpAttendeeFromWaitListInit() {
        subscriptionRenewed(groupOrganizerId);
        subscriptionRenewed(waitListGroupMemberId);
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
        meetingsFacade.newMemberJoinedGroup(waitListGroupMemberId, meetingGroupId);
    }

    @Test
    public void afterAttendeeLeft() {
//        given that group was created
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
//        given that meeting was scheduled
        var groupMeetingId = meetingsFacade.scheduleNewMeeting(groupOrganizerId, meetingDraft(attendeesLimit, WAIT_LIST_AVAILABLE)).get();
//        and meeting attendee limit was reached
        var attendees = fillMeetingWithAttendees(groupMeetingId, meetingGroupId, attendeesLimit);
//        and group member signed on wait list
        assert meetingsFacade.signOnMeetingWaitList(waitListGroupMemberId, groupMeetingId).isEmpty();
//        when one of the attendees signs out from meeting
        var signedOutAttendeeId = attendees.get(0);
        assert meetingsFacade.signOutFromMeeting(signedOutAttendeeId, groupMeetingId).isEmpty();
//        then group member from wait list is notified about being sign up for meeting
        verify(meetingsNotificationsFacade).notifyAttendeeAboutBeingSignedUpFromWaitingList(asAttendee(waitListGroupMemberId));
//        and
        var meetingDetails = meetingsFacade.findMeetingDetails(groupMeetingId).get();
        assert meetingDetails.getAttendees().contains(asAttendee(waitListGroupMemberId));
//        and
        assert !meetingDetails.getAttendees().contains(signedOutAttendeeId);
//        and
        assert meetingDetails.getWaitListDetails().getGroupMembers().isEmpty();
    }

    @Test
    public void afterMemberLeftGroup() {
//        given that meeting was scheduled
        var groupMeetingId = meetingsFacade.scheduleNewMeeting(groupOrganizerId, meetingDraft(attendeesLimit, WAIT_LIST_AVAILABLE)).get();
//        and meeting attendee limit was reached
        var attendees = fillMeetingWithAttendees(groupMeetingId, meetingGroupId, attendeesLimit);
//        and some group member signed on wait list
        assert meetingsFacade.signOnMeetingWaitList(waitListGroupMemberId, groupMeetingId).isEmpty();
//        when one of the attendees leaves meeting group
        var attendeeWhoLeftGroup = attendees.get(0);
        meetingsFacade.memberLeftTheMeetingGroup(asMember(attendeeWhoLeftGroup), meetingGroupId);
//        then group member from wait list is notified about being sign up for meeting
        verify(meetingsNotificationsFacade).notifyAttendeeAboutBeingSignedUpFromWaitingList(asAttendee(waitListGroupMemberId));
//        and
        var meetingDetails = meetingsFacade.findMeetingDetails(groupMeetingId).get();
        assert meetingDetails.getAttendees().contains(asAttendee(waitListGroupMemberId));
//        and
        assert !meetingDetails.getAttendees().contains(attendeeWhoLeftGroup);
//        and
        assert meetingDetails.getWaitListDetails().getGroupMembers().isEmpty();
    }

    private MeetingDraft meetingDraft(AttendeesLimit attendeesLimit, WaitList waitList) {
        return new MeetingDraft(
                meetingGroupId,
                calendar.getCurrentDate().plusDays(3),
                asHost(groupOrganizerId),
                new GroupMeetingName("some-name"),
                Option.of(attendeesLimit),
                waitList);
    }
}