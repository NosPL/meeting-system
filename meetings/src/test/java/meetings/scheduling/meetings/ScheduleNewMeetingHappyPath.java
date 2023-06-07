package meetings.scheduling.meetings;

import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import meetings.MeetingsConfiguration;
import meetings.commons.FixedDateCalendar;
import meetings.commons.TestSetup;
import meetings.dto.GroupMeetingHostId;
import meetings.dto.GroupMeetingName;
import meetings.dto.MeetingDraft;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.UUID;

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
        var meetingDraft = new MeetingDraft(meetingGroupId, date3DaysFromNow, groupMeetingHostId, groupMeetingName);
        var result = meetingsFacade.scheduleNewMeeting(groupOrganizerId, meetingDraft);
//        then he succeeds
        assert result.isRight();
    }
}