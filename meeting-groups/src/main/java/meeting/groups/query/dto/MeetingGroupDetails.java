package meeting.groups.query.dto;

import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import lombok.Value;

import java.util.List;

@Value
public class MeetingGroupDetails {
    MeetingGroupId meetingGroupId;
    String groupName;
    GroupOrganizerId groupOrganizerId;
    List<GroupMemberId> groupMemberIds;
}