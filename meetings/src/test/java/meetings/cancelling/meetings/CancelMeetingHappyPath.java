package meetings.cancelling.meetings;

import commons.dto.*;
import io.vavr.control.Option;
import meetings.commons.TestSetup;
import org.junit.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class CancelMeetingHappyPath extends TestSetup {
    private final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer");
    private final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group-id");

    @Test
    public void test() {
//        given that meeting group was created
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
//        and meeting was scheduled
        var groupMeetingId = scheduleMeeting(groupOrganizerId, meetingGroupId);
//        and few group members signed up for meeting
        var attendeeId1 = signUpForMeeting(groupMeetingId, meetingGroupId);
        var attendeeId2 = signUpForMeeting(groupMeetingId, meetingGroupId);
        var attendeeId3 = signUpForMeeting(groupMeetingId, meetingGroupId);
//        when user tries to cancel meeting
        var result = meetingsFacade.cancelMeeting(groupOrganizerId, groupMeetingId);
//        then he succeeds
        assertEquals(Option.none(), result);
//        and meeting attendees were notified about meeting cancellation
        verify(meetingsNotificationsFacade).notifyAttendeesAboutMeetingCancellation(Set.of(attendeeId1, attendeeId2, attendeeId3));
//        and 'meeting was cancelled' event was emitted
        verify(eventPublisher).meetingWasCancelled(meetingGroupId, groupMeetingId);
    }
}