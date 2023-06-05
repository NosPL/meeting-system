package meeting.groups;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class GroupMembership {
    private String memberId;
    private String meetingGroupId;
}