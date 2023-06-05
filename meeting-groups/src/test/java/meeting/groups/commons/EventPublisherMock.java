package meeting.groups.commons;

import commons.dto.MeetingGroupId;
import commons.event.publisher.EventPublisher;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class EventPublisherMock implements EventPublisher {
    private final List<MeetingGroupId> groupCreatedInvocations = new LinkedList<>();

    @Override
    public void newMeetingGroupCreated(MeetingGroupId meetingGroupId) {
        log.info("new meeting group created' event was emitted, meeting group id {}", meetingGroupId.getId());
    }

    public boolean groupCreatedEventInvoked(MeetingGroupId...meetingGroupId) {
        return groupCreatedInvocations.equals(List.of(meetingGroupId));
    }
}