package meeting.comments;

import commons.dto.GroupMeetingId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import meeting.comments.dto.CommentAuthorId;

@AllArgsConstructor
@Getter
class Meeting {
    private GroupMeetingId groupMeetingId;
    private MeetingGroupId meetingGroupId;
    private GroupOrganizerId groupOrganizerId;

    boolean isGroupOrganizer(CommentAuthorId commentAuthorId) {
        return groupOrganizerId.equals(new GroupOrganizerId(commentAuthorId.getId()));
    }
}