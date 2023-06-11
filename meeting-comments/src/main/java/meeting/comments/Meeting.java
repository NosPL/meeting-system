package meeting.comments;

import commons.dto.GroupMeetingId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import meeting.comments.dto.CommentAuthorId;

@AllArgsConstructor
class Meeting {
    @Getter
    private GroupMeetingId groupMeetingId;
    @Getter
    private MeetingGroupId meetingGroupId;
    private GroupOrganizerId groupOrganizerId;

    boolean isGroupOrganizer(CommentAuthorId commentAuthorId) {
        GroupOrganizerId commentAuthor = new GroupOrganizerId(commentAuthorId.getId());
        return groupOrganizerId.equals(commentAuthor);
    }
}