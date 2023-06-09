package meetings.commons;

import commons.active.subscribers.InMemoryActiveSubscribers;
import commons.calendar.Calendar;
import commons.dto.*;
import commons.event.publisher.EventPublisher;
import io.vavr.control.Option;
import meetings.MeetingsConfiguration;
import meetings.MeetingsFacade;
import meetings.dto.AttendeesLimit;
import meetings.dto.GroupMeetingHostId;
import meetings.dto.GroupMeetingName;
import meetings.dto.MeetingDraft;
import meetings.notifications.MeetingsNotificationsFacade;
import org.junit.Before;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.mock;

public class TestSetup {
    protected InMemoryActiveSubscribers activeSubscribers;
    protected MeetingsFacade meetingsFacade;
    protected Calendar calendar;
    protected EventPublisher eventPublisher;
    protected MeetingsNotificationsFacade meetingsNotificationsFacade;

    @Before
    public void testSetup() {
        activeSubscribers = new InMemoryActiveSubscribers();
        calendar = new FixedDateCalendar(LocalDate.now());
        eventPublisher = mock(EventPublisher.class);
        meetingsNotificationsFacade = mock(MeetingsNotificationsFacade.class);
        meetingsFacade = new MeetingsConfiguration()
                .inMemoryMeetingsFacade(activeSubscribers, eventPublisher, meetingsNotificationsFacade, calendar);
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

    protected AttendeeId signUpForMeeting(GroupMeetingId groupMeetingId, MeetingGroupId meetingGroupId) {
        var groupMemberId = randomGroupMemberId();
        subscriptionRenewed(groupMemberId);
        meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
        assert meetingsFacade.signUpForMeeting(groupMemberId, groupMeetingId).isEmpty();
        return new AttendeeId(groupMemberId.getId());
    }

    protected void subscriptionRenewed(GroupMemberId groupMemberId) {
        activeSubscribers.add(new UserId(groupMemberId.getId()));
    }

    protected GroupMemberId randomGroupMemberId() {
        return new GroupMemberId(UUID.randomUUID().toString());
    }

    protected GroupMeetingId scheduleMeeting(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        var groupMeetingHostId = new GroupMeetingHostId("group-meeting-host");
        return scheduleMeeting(groupOrganizerId, meetingGroupId, groupMeetingHostId, Option.none());
    }

    protected GroupMeetingId scheduleMeeting(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId, GroupMeetingHostId groupMeetingHostId, Option<AttendeesLimit> attendeesLimit) {
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
        meetingsFacade.newMemberJoinedGroup(asGroupMember(groupMeetingHostId), meetingGroupId);
        subscriptionRenewed(groupMeetingHostId);
        subscriptionRenewed(groupOrganizerId);
        var meetingDraft = meetingDraft(groupMeetingHostId, meetingGroupId, attendeesLimit);
        return meetingsFacade.scheduleNewMeeting(groupOrganizerId, meetingDraft).get();
    }

    protected GroupMemberId asGroupMember(GroupMeetingHostId groupMeetingHostId) {
        return new GroupMemberId(groupMeetingHostId.getId());
    }

    protected MeetingDraft meetingDraft(GroupMeetingHostId groupMeetingHostId, MeetingGroupId meetingGroupId, Option<AttendeesLimit> attendeesLimit) {
        return new MeetingDraft(
                meetingGroupId,
                calendar.getCurrentDate().plusDays(4),
                groupMeetingHostId,
                new GroupMeetingName("random-name"),
                attendeesLimit);
    }
}