package meetings;

import commons.calendar.Calendar;
import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import meetings.dto.*;

import java.time.LocalDate;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static meetings.dto.ScheduleMeetingFailure.*;

@AllArgsConstructor
class MeetingsScheduler {
    private final ActiveSubscriptions activeSubscriptions;
    private final MeetingRepository meetingRepository;
    private final MeetingGroupRepository meetingGroupRepository;
    private final Calendar calendar;

    Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(GroupOrganizerId groupOrganizerId, MeetingDraft meetingDraft) {
        if (!meetingGroupExists(meetingDraft.getMeetingGroupId()))
            return left(MEETING_GROUP_DOES_NOT_EXIST);
        if (!isGroupOrganizer(meetingDraft.getMeetingGroupId()))
            return left(USER_IS_NOT_GROUP_ORGANIZER);
        if (!isSubscribed(groupOrganizerId))
            return left(GROUP_ORGANIZER_DOES_NOT_HAVE_ACTIVE_SUBSCRIPTION);
        if (!dateIs3DaysInAdvance(meetingDraft.getMeetingDate()))
            return left(MEETING_DATE_IS_NOT_3_DAYS_IN_ADVANCE);
        if (!isSubscribed(meetingDraft.getGroupMeetingHostId()))
            return left(PROPOSED_MEETING_HOST_DOES_NOT_HAVE_ACTIVE_SUBSCRIPTION);
        if (!isGroupMember(meetingDraft.getMeetingGroupId(), meetingDraft.getGroupMeetingHostId()))
            return left(PROPOSED_MEETING_HOST_IS_NOT_MEETING_GROUP_MEMBER);
        if (meetingNameIsAlreadyUsed(meetingDraft.getGroupMeetingName()))
            return left(MEETING_NAME_IS_NOT_UNIQUE);
        if (meetingNameIsBlank(meetingDraft.getGroupMeetingName()))
            return left(MEETING_NAME_CANNOT_BE_BLANK);
        var groupMeeting = Meeting.create(groupOrganizerId, meetingDraft);
        GroupMeetingId groupMeetingId = meetingRepository.save(groupMeeting);
        return right(groupMeetingId);
    }

    private boolean meetingGroupExists(MeetingGroupId meetingGroupId) {
        return meetingGroupRepository.existsById(meetingGroupId);
    }

    private boolean isGroupOrganizer(MeetingGroupId meetingGroupId) {
        return meetingGroupRepository
                .findById(meetingGroupId)
                .map(meetingGroup -> meetingGroup.getMeetingGroupId().equals(meetingGroupId))
                .getOrElse(false);
    }

    private boolean isSubscribed(GroupOrganizerId groupOrganizerId) {
        return activeSubscriptions.contains(new UserId(groupOrganizerId.getId()));
    }

    private boolean meetingNameIsAlreadyUsed(GroupMeetingName groupMeetingName) {
        return meetingRepository.existsByMeetingName(groupMeetingName);
    }

    private boolean meetingNameIsBlank(GroupMeetingName groupMeetingName) {
        return groupMeetingName.getName().isBlank();
    }

    private boolean isGroupMember(MeetingGroupId meetingGroupId, GroupMeetingHostId groupMeetingHostId) {
        var groupMemberId = new GroupMemberId(groupMeetingHostId.getId());
        return meetingGroupRepository
                .findById(meetingGroupId)
                .map(meetingGroup -> meetingGroup.contains(groupMemberId))
                .getOrElse(false);
    }

    private boolean isSubscribed(GroupMeetingHostId groupMeetingHostId) {
        return activeSubscriptions.contains(new UserId(groupMeetingHostId.getId()));
    }

    private boolean dateIs3DaysInAdvance(LocalDate meetingDate) {
        return calendar.getCurrentDate().plusDays(3).isBefore(meetingDate) ||
                calendar.getCurrentDate().plusDays(3).isEqual(meetingDate);
    }
}