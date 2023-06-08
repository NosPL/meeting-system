package meeting.groups;

import io.vavr.control.Option;
import meeting.groups.commons.TestSetup;
import org.junit.Test;

import static meeting.groups.dto.RemoveGroupFailure.*;
import static org.junit.Assert.assertEquals;

public class RemovingGroup extends TestSetup {

    @Test
    public void shouldFailBecauseGroupDoesNotExist() {
//        given that group was created
        createMeetingGroup();
//        when group organizer tries group with random id
        var result = meetingGroupsFacade.removeGroup(subscribedGroupOrganizer, randomMeetingGroupId());
//        then failure
        assertEquals(Option.of(GROUP_DOESNT_EXIST), result);
    }

    @Test
    public void shouldFailBecauseUserIsNotGroupOrganizer() {
//        given that group was created
        var meetingGroupId = createMeetingGroup();
//        when user that is not group organizer tries to remove group
        var result = meetingGroupsFacade.removeGroup(randomGroupOrganizerId(), meetingGroupId);
//        then failure
        assertEquals(Option.of(USER_IS_NOT_GROUP_ORGANIZER), result);
    }

    @Test
    public void shouldFailBecauseMeetingGroupHasScheduledMeetings() {
//        given that group was created
        var meetingGroupId = createMeetingGroup();
//        and meeting for this group was scheduled
        meetingGroupsFacade.newMeetingScheduled(meetingGroupId, randomGroupMeetingId());
//        when group organizer tries to remove group
        var result = meetingGroupsFacade.removeGroup(subscribedGroupOrganizer, meetingGroupId);
//        then failure
        assertEquals(Option.of(GROUP_HAS_SCHEDULED_MEETINGS), result);
//
    }

    @Test
    public void successfulRemoval() {
//        given that group was created
        var meetingGroupId = createMeetingGroup();
//        when group organizer tries to remove group
        var result = meetingGroupsFacade.removeGroup(subscribedGroupOrganizer, meetingGroupId);
//        then success
        assertEquals(Option.none(), result);
//        and
        eventPublisherMock.meetingGroupWasRemovedInvoked(meetingGroupId);
    }

    @Test
    public void successfulRemovalAfterMeetingWasCancelled() {
//        given that group was created
        var meetingGroupId = createMeetingGroup();
//        and meeting was scheduled
        var groupMeetingId = randomGroupMeetingId();
        meetingGroupsFacade.newMeetingScheduled(meetingGroupId, groupMeetingId);
//        and meeting was cancelled
        meetingGroupsFacade.meetingCancelled(meetingGroupId, groupMeetingId);
//        when group organizer tries to remove group
        var result = meetingGroupsFacade.removeGroup(subscribedGroupOrganizer, meetingGroupId);
//        then success
        assertEquals(Option.none(), result);
//        and
        eventPublisherMock.meetingGroupWasRemovedInvoked(meetingGroupId);
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
        var result = meetingGroupsFacade.removeGroup(subscribedGroupOrganizer, meetingGroupId);
//        then success
        assertEquals(Option.none(), result);
//        and
        eventPublisherMock.meetingGroupWasRemovedInvoked(meetingGroupId);
    }
}