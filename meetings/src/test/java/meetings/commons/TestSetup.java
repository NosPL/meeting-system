package meetings.commons;

import commons.calendar.Calendar;
import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import meetings.MeetingsConfiguration;
import meetings.MeetingsFacade;
import meetings.dto.GroupMeetingHostId;
import meetings.dto.GroupMeetingName;
import org.junit.Before;

import java.time.LocalDate;
import java.util.UUID;

public class TestSetup {
    protected MeetingsFacade meetingsFacade;
    protected Calendar calendar;

    @Before
    public void testSetup() {
        calendar = new FixedDateCalendar(LocalDate.now());
        meetingsFacade = new MeetingsConfiguration().inMemoryMeetingsFacade(calendar);
    }

    protected void subscriptionRenewed(GroupMeetingHostId groupMeetingHostId) {
        meetingsFacade.subscriptionRenewed(new UserId(groupMeetingHostId.getId()));
    }

    protected void subscriptionRenewed(GroupOrganizerId groupOrganizerId) {
        meetingsFacade.subscriptionRenewed(new UserId(groupOrganizerId.getId()));
    }

    protected void newMemberJoinedGroup(GroupMeetingHostId groupMeetingHostId, MeetingGroupId meetingGroupId) {
        meetingsFacade.newMemberJoinedGroup(new GroupMemberId(groupMeetingHostId.getId()), meetingGroupId);
    }

    protected void subscriptionExpired(GroupOrganizerId groupOrganizerId) {
        meetingsFacade.subscriptionExpired(new UserId(groupOrganizerId.getId()));
    }

    protected GroupMeetingName uniqueNotBlankMeetingName() {
        return new GroupMeetingName(UUID.randomUUID().toString());
    }
}