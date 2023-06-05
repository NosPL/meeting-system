package commons.event.publisher;

import commons.dto.MeetingGroupId;

public interface EventPublisher {
    void newMeetingGroupCreated(MeetingGroupId meetingGroupId);
}