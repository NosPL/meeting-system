package meetings;

import commons.dto.GroupOrganizerId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import meetings.dto.GroupMeetingHostId;
import meetings.dto.GroupMeetingId;
import meetings.dto.GroupMeetingName;
import meetings.dto.MeetingDraft;

import java.time.LocalDate;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@Getter
class Meeting {
    private GroupMeetingId groupMeetingId;
    private GroupOrganizerId groupOrganizerId;
    private GroupMeetingHostId groupMeetingHostId;
    private LocalDate meetingDate;
    private GroupMeetingName groupMeetingName;

    static Meeting create(GroupOrganizerId groupOrganizerId, MeetingDraft meetingDraft) {
        var groupMeetingId = new GroupMeetingId(UUID.randomUUID().toString());
        return new Meeting(
                groupMeetingId,
                groupOrganizerId,
                meetingDraft.getGroupMeetingHostId(),
                meetingDraft.getMeetingDate(),
                meetingDraft.getGroupMeetingName());
    }
}