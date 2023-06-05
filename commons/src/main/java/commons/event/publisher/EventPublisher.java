package commons.event.publisher;

import commons.dto.MeetingGroupId;
import commons.dto.UserId;

public interface EventPublisher {

    void newMeetingGroupCreated(UserId userId, MeetingGroupId meetingGroupId);

    void newMemberJoinedMeetingGroup(UserId userId, MeetingGroupId meetingGroupId);
}