package meetings.commons;

import commons.active.subscribers.InMemoryActiveSubscribers;
import commons.calendar.Calendar;
import commons.dto.*;
import commons.event.publisher.EventPublisher;
import io.vavr.control.Option;
import meetings.MeetingsConfiguration;
import meetings.MeetingsFacade;
import meetings.dto.*;
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
        MeetingDraft meetingDraft =
                new MeetingDraft(meetingGroupId, calendar.getCurrentDate().plusDays(4), groupMeetingHostId, new GroupMeetingName("some-name"), Option.none(), WaitList.WAIT_LIST_AVAILABLE);
        return scheduleMeeting(groupOrganizerId, meetingDraft);
    }

    protected GroupMeetingId scheduleMeeting(GroupOrganizerId groupOrganizerId, MeetingDraft meetingDraft) {
        var groupMeetingHostId = meetingDraft.getGroupMeetingHostId();
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingDraft.getMeetingGroupId());
        meetingsFacade.newMemberJoinedGroup(asGroupMember(meetingDraft.getGroupMeetingHostId()), meetingDraft.getMeetingGroupId());
        subscriptionRenewed(groupMeetingHostId);
        subscriptionRenewed(groupOrganizerId);
        return meetingsFacade.scheduleNewMeeting(groupOrganizerId, meetingDraft).get();
    }

    protected GroupMemberId asGroupMember(GroupMeetingHostId groupMeetingHostId) {
        return new GroupMemberId(groupMeetingHostId.getId());
    }

    protected AttendeeId asAttendee(GroupMemberId groupMemberId) {
        return new AttendeeId(groupMemberId.getId());
    }

    protected GroupMeetingHostId asHost(GroupOrganizerId groupOrganizerId) {
        return new GroupMeetingHostId(groupOrganizerId.getId());
    }

    protected GroupMeetingId randomGroupMeetingId() {
        return new GroupMeetingId(UUID.randomUUID().toString());
    }
}