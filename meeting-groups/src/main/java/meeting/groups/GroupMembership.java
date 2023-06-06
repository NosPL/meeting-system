package meeting.groups;

import commons.dto.GroupMemberId;
import commons.dto.MeetingGroupId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import commons.dto.GroupMembershipId;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@Getter
class GroupMembership {
    private GroupMembershipId groupMembershipId;
    private GroupMemberId groupMemberId;
    private MeetingGroupId meetingGroupId;

    static GroupMembership create(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        String id = UUID.randomUUID().toString();
        return new GroupMembership(new GroupMembershipId(id), groupMemberId, meetingGroupId);
    }
}