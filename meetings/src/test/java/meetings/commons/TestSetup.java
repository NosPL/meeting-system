package meetings.commons;

import commons.active.subscribers.InMemoryActiveSubscribers;
import commons.calendar.Calendar;
import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import commons.event.publisher.EventPublisher;
import meetings.MeetingsConfiguration;
import meetings.MeetingsFacade;
import meetings.dto.GroupMeetingHostId;
import meetings.dto.GroupMeetingName;
import org.junit.Before;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.UUID;

public class TestSetup {
    protected InMemoryActiveSubscribers activeSubscribers;
    protected MeetingsFacade meetingsFacade;
    protected Calendar calendar;
    protected EventPublisher eventPublisher;

    @Before
    public void testSetup() {
        activeSubscribers = new InMemoryActiveSubscribers();
        calendar = new FixedDateCalendar(LocalDate.now());
        eventPublisher = Mockito.mock(EventPublisher.class);
        meetingsFacade = new MeetingsConfiguration().inMemoryMeetingsFacade(activeSubscribers, eventPublisher, calendar);
    }

    protected void subscriptionRenewed(GroupMeetingHostId groupMeetingHostId) {
        activeSubscribers.add(new UserId(groupMeetingHostId.getId()));
    }

    protected void subscriptionRenewed(GroupOrganizerId groupOrganizerId) {
        activeSubscribers.add(new UserId(groupOrganizerId.getId()));
    }

    protected void newMemberJoinedGroup(GroupMeetingHostId groupMeetingHostId, MeetingGroupId meetingGroupId) {
        meetingsFacade.newMemberJoinedGroup(new GroupMemberId(groupMeetingHostId.getId()), meetingGroupId);
    }

    protected void subscriptionExpired(GroupOrganizerId groupOrganizerId) {
        activeSubscribers.remove(new UserId(groupOrganizerId.getId()));
    }

    protected void subscriptionExpired(GroupMeetingHostId groupMeetingHostId) {
        activeSubscribers.remove(new UserId(groupMeetingHostId.getId()));
    }

    protected GroupMeetingName uniqueNotBlankMeetingName() {
        return new GroupMeetingName(UUID.randomUUID().toString());
    }
}