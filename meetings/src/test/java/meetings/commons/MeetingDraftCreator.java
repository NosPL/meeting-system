package meetings.commons;

import commons.dto.MeetingGroupId;
import io.vavr.control.Option;
import meetings.dto.GroupMeetingHostId;
import meetings.dto.GroupMeetingName;
import meetings.dto.MeetingDraft;
import meetings.dto.WaitList;

import java.time.LocalDate;

import static meetings.dto.WaitList.WAIT_LIST_AVAILABLE;

public class MeetingDraftCreator {

    public static MeetingDraft meetingWithWaitingList() {
        return null;
    }

    public static MeetingDraft meetingWithoutWaitingListAndAttendeeLimit(
            MeetingGroupId meetingGroupId,
            LocalDate localDate,
            GroupMeetingHostId groupMeetingHostId,
            GroupMeetingName meetingName) {
        return new MeetingDraft(meetingGroupId, localDate, groupMeetingHostId, meetingName, Option.none(), WAIT_LIST_AVAILABLE);
    }

    public static MeetingDraft meetingWithAttendeeLimit() {
        return null;
    }
}