package meeting.comments;

import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class Group {
    private MeetingGroupId meetingGroupId;
    private GroupOrganizerId groupOrganizerId;
}