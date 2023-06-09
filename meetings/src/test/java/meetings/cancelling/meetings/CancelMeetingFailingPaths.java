package meetings.cancelling.meetings;

import commons.dto.GroupMeetingId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Option;
import meetings.commons.TestSetup;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

import static meetings.dto.CancelMeetingFailure.MEETING_DOESNT_EXIST;
import static meetings.dto.CancelMeetingFailure.USER_IS_NOT_GROUP_ORGANIZER;

public class CancelMeetingFailingPaths extends TestSetup {
    private final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer");
    private final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group");

    @Test
    public void groupOrganizerShouldFailToCancelNotExistingMeeting() {
//        given that organizer created meeting group
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
//        when he tries to cancel different meeting
        var result = meetingsFacade.cancelMeeting(groupOrganizerId, randomGroupMeetingId());
//        then he fails
        Assert.assertEquals(Option.of(MEETING_DOESNT_EXIST), result);
    }

    @Test
    public void userThatIsNotGroupOrganizerShouldFailToCancelMeeting() {
//        given that meeting was scheduled
        var groupMeetingId = scheduleMeeting(groupOrganizerId, meetingGroupId);
//        when user, that is not group organizer, tries to cancel meeting
        var result = meetingsFacade.cancelMeeting(randomGroupOrganizerId(), groupMeetingId);
//        then he fails
        Assert.assertEquals(Option.of(USER_IS_NOT_GROUP_ORGANIZER), result);
    }

    private GroupOrganizerId randomGroupOrganizerId() {
        return new GroupOrganizerId(UUID.randomUUID().toString());
    }

    private GroupMeetingId randomGroupMeetingId() {
        return new GroupMeetingId(UUID.randomUUID().toString());
    }
}