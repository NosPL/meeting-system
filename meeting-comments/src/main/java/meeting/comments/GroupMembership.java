package meeting.comments;


import commons.dto.GroupMemberId;
import commons.dto.MeetingGroupId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
class GroupMembership {
    private GroupMembershipId groupMembershipId;
    private GroupMemberId groupMemberId;
    private MeetingGroupId meetingGroupId;

    static GroupMembership create(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        var groupMembershipId = new GroupMembershipId(UUID.randomUUID().toString());
        return new GroupMembership(groupMembershipId, groupMemberId, meetingGroupId);
    }

    boolean equalsBoth(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        return this.groupMemberId.equals(groupMemberId) &&
                this.meetingGroupId.equals(meetingGroupId);
    }
}