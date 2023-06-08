package commons.event.publisher;

import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;

public interface EventPublisher {

    void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId);

    void newMemberJoinedMeetingGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId);

    void meetingGroupWasRemoved(MeetingGroupId meetingGroupId);

    void groupMemberLeftGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId);
}