package meetings;

import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
class MeetingGroup {
    private MeetingGroupId meetingGroupId;
    private GroupOrganizerId groupOrganizerId;
    private Set<GroupMemberId> groupMemberIds;

    boolean contains(GroupMemberId groupMemberId) {
        return groupMemberIds.contains(groupMemberId);
    }

    public void add(GroupMemberId groupMemberId) {
        groupMemberIds.add(groupMemberId);
    }

    public void remove(GroupMemberId groupMemberId) {
        groupMemberIds.remove(groupMemberId);
    }
}