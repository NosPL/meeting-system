package meetings;

import commons.calendar.Calendar;

import java.util.LinkedList;

public class MeetingsConfiguration {
    private final MeetingRepository meetingRepository = new MeetingRepository.InMemory(new LinkedList<>(), Meeting::getGroupMeetingId);
    private final MeetingGroupRepository meetingGroupRepository = new MeetingGroupRepository.InMemory(new LinkedList<>(), MeetingGroup::getMeetingGroupId);
    private final ActiveSubscriptions activeSubscriptions = new ActiveSubscriptions();

    public MeetingsFacade inMemoryMeetingsFacade(Calendar calendar) {
        var meetingsScheduler = new MeetingsScheduler(activeSubscriptions, meetingRepository, meetingGroupRepository, calendar);
        return new LogsDecorator(new MeetingsFacadeImpl(meetingGroupRepository, activeSubscriptions, meetingsScheduler));
    }
}