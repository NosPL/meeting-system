package meeting.groups;

import io.vavr.control.Option;
import meeting.groups.commons.TestSetup;
import org.junit.Test;

import static meeting.groups.dto.RemoveGroupFailure.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class RemovingGroup extends TestSetup {

    @Test
    public void shouldFailBecauseGroupDoesNotExist() {
//        given that group was created
        createMeetingGroup(groupOrganizer);
//        when group organizer tries to remove not existing group
        var result = meetingGroupsFacade.removeGroup(groupOrganizer, randomMeetingGroupId());
//        then he fails
        assertEquals(Option.of(GROUP_DOESNT_EXIST), result);
    }

    @Test
    public void shouldFailBecauseUserIsNotGroupOrganizer() {
//        given that group was created
        var meetingGroupId = createMeetingGroup(groupOrganizer);
//        when user that is not group organizer tries to remove group
        var result = meetingGroupsFacade.removeGroup(randomGroupOrganizerId(), meetingGroupId);
//        then he fails
        assertEquals(Option.of(USER_IS_NOT_GROUP_ORGANIZER), result);
    }

    @Test
    public void shouldFailBecauseMeetingGroupHasScheduledMeetings() {
//        given that group was created
        var meetingGroupId = createMeetingGroup(groupOrganizer);
//        and meeting for this group was scheduled
        meetingGroupsFacade.newMeetingScheduled(meetingGroupId, randomGroupMeetingId());
//        when group organizer tries to remove group
        var result = meetingGroupsFacade.removeGroup(groupOrganizer, meetingGroupId);
//        then he fails
        assertEquals(Option.of(GROUP_HAS_SCHEDULED_MEETINGS), result);
//
    }

    @Test
    public void successfulRemoval() {
//        given that group was created
        var meetingGroupId = createMeetingGroup(groupOrganizer);
//        when group organizer tries to remove group
        var result = meetingGroupsFacade.removeGroup(groupOrganizer, meetingGroupId);
//        then success
        assertEquals(Option.none(), result);
//        and
        verify(eventPublisher).meetingGroupWasRemoved(meetingGroupId);
    }

    @Test
    public void successfulRemovalAfterMeetingWasCancelled() {
//        given that group was created
        var meetingGroupId = createMeetingGroup(groupOrganizer);
//        and meeting was scheduled
        var groupMeetingId = randomGroupMeetingId();
        meetingGroupsFacade.newMeetingScheduled(meetingGroupId, groupMeetingId);
//        and meeting was cancelled
        meetingGroupsFacade.meetingCancelled(meetingGroupId, groupMeetingId);
//        when group organizer tries to remove group
        var result = meetingGroupsFacade.removeGroup(groupOrganizer, meetingGroupId);
//        then success
        assertEquals(Option.none(), result);
//        and 'meeting group was removed' was emitted
        verify(eventPublisher).meetingGroupWasRemoved(meetingGroupId);
    }

    @Test
    public void successfulRemovalAfterMeetingWasHeld() {
//        given that group was created
        var meetingGroupId = createMeetingGroup();
//        and meeting was scheduled
        var groupMeetingId = randomGroupMeetingId();
        meetingGroupsFacade.newMeetingScheduled(meetingGroupId, groupMeetingId);
//        and meeting was held
        meetingGroupsFacade.meetingWasHeld(meetingGroupId, groupMeetingId);
//        when group organizer tries to remove group
        var result = meetingGroupsFacade.removeGroup(groupOrganizer, meetingGroupId);
//        then success
        assertEquals(Option.none(), result);
//        and  'meeting group was removed' was emitted
        verify(eventPublisher).meetingGroupWasRemoved(meetingGroupId);
    }
}