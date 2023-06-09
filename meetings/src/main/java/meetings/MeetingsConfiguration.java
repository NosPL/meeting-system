package meetings;

import commons.active.subscribers.ActiveSubscribersFinder;
import commons.event.publisher.EventPublisher;
import meetings.notifications.MeetingsNotificationsFacade;
import meetings.ports.Calendar;

import java.util.LinkedList;

public class MeetingsConfiguration {
    private final MeetingRepository meetingRepository = new MeetingRepository.InMemory(new LinkedList<>(), Meeting::getGroupMeetingId);
    private final MeetingGroupRepository meetingGroupRepository = new MeetingGroupRepository.InMemory(new LinkedList<>(), MeetingGroup::getMeetingGroupId);

    public MeetingsFacade inMemoryMeetingsFacade(
            ActiveSubscribersFinder activeSubscribersFinder,
            EventPublisher eventPublisher,
            MeetingsNotificationsFacade meetingsNotificationsFacade,
            Calendar calendar) {
        var meetingsScheduler = new MeetingsScheduler(activeSubscribersFinder, meetingRepository, meetingGroupRepository, eventPublisher, calendar);
        return new LogsDecorator(new MeetingsFacadeImpl(
                activeSubscribersFinder,
                meetingGroupRepository,
                meetingRepository,
                meetingsScheduler,
                meetingsNotificationsFacade,
                eventPublisher));
    }
}