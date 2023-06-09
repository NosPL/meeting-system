package commons.event.publisher;

import commons.dto.*;

public interface EventPublisher {

    void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId);

    void newMemberJoinedMeetingGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId);

    void meetingGroupWasRemoved(MeetingGroupId meetingGroupId);

    void groupMemberLeftGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId);

    void newMeetingWasScheduled(MeetingGroupId meetingGroupId, GroupMeetingId groupMeetingId);

    void meetingWasHeld(MeetingGroupId meetingGroupId, GroupMeetingId groupMeetingId);

    void meetingWasCancelled(MeetingGroupId meetingGroupId, GroupMeetingId groupMeetingId);

}