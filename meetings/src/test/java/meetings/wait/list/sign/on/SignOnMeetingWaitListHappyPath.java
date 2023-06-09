package meetings.wait.list.sign.on;

import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Option;
import meetings.dto.AttendeesLimit;
import meetings.dto.GroupMeetingName;
import meetings.dto.MeetingDraft;
import meetings.dto.WaitList;
import meetings.wait.list.sign.WaitListTestSetup;
import org.junit.Test;

import static meetings.dto.WaitList.WAIT_LIST_AVAILABLE;
import static org.junit.Assert.assertEquals;

public class SignOnMeetingWaitListHappyPath extends WaitListTestSetup {
    private final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer");
    private final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group-id");
    private final AttendeesLimit attendeesLimit = new AttendeesLimit(3);
    private final GroupMemberId groupMemberId = new GroupMemberId("group-member");

    @Test
    public void test() {
//        given that meeting group was created
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
//        and meeting with attendees limit and wait list allowed was scheduled
        var groupMeetingId = scheduleMeeting(groupOrganizerId, meetingDraft(attendeesLimit, WAIT_LIST_AVAILABLE));
//        and attendee limit was reached
        fillMeetingWithAttendees(groupMeetingId, meetingGroupId, attendeesLimit);
//        and user is subscribed
        subscriptionRenewed(groupMemberId);
//        and user joined the group
        meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
//        when user tries to sign on wait list
        var result = meetingsFacade.signOnMeetingWaitList(groupMemberId, groupMeetingId);
//        then he succeeds
        assertEquals(Option.none(), result);
    }

    protected MeetingDraft meetingDraft(AttendeesLimit attendeesLimit, WaitList waitList) {
        return new MeetingDraft(
                meetingGroupId,
                calendar.getCurrentDate().plusDays(3),
                asHost(groupOrganizerId),
                new GroupMeetingName("some-name"),
                Option.of(attendeesLimit),
                waitList);
    }
}