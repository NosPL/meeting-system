package meetings.scheduling.meetings;

import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Option;
import meetings.commons.TestSetup;
import meetings.dto.GroupMeetingHostId;
import meetings.dto.MeetingDraft;
import org.junit.Test;

import static org.mockito.Mockito.verify;

public class ScheduleNewMeetingHappyPath extends TestSetup {
    protected final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group-id");
    protected final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer-id");
    protected final GroupMeetingHostId groupMeetingHostId = new GroupMeetingHostId("group-meeting-host-id");

    @Test
    public void scheduleGroupMeetingHappyPath() {
//        given that meeting group was created
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
//        and group organizer is subscribed
        subscriptionRenewed(groupOrganizerId);
//        and meeting date is 3 days in advance
        var date3DaysFromNow = calendar.getCurrentDate().plusDays(3);
//        and proposed host is subscribed
        subscriptionRenewed(groupMeetingHostId);
//        and proposed host is a group member
        newMemberJoinedGroup(groupMeetingHostId, meetingGroupId);
//        and meeting name is unique and not blank
        var groupMeetingName = uniqueNotBlankMeetingName();
//        when group organizer tries to schedule new meeting
        var meetingDraft = new MeetingDraft(meetingGroupId, date3DaysFromNow, groupMeetingHostId, groupMeetingName, Option.none());
        var result = meetingsFacade.scheduleNewMeeting(groupOrganizerId, meetingDraft);
//        then he succeeds
        assert result.isRight();
//        and even was emitted
        var groupMeetingId = result.get();
        verify(eventPublisher).newMeetingWasScheduled(meetingGroupId, groupMeetingId);
    }
}