package meetings;

import commons.active.subscribers.ActiveSubscribersFinder;
import commons.dto.*;
import commons.event.publisher.EventPublisher;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import meetings.dto.*;
import meetings.ports.Calendar;

import java.time.LocalDate;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static meetings.dto.ScheduleMeetingFailure.*;

@AllArgsConstructor
class MeetingsScheduler {
    private final ActiveSubscribersFinder activeSubscribersFinder;
    private final MeetingRepository meetingRepository;
    private final MeetingGroupRepository meetingGroupRepository;
    private final EventPublisher eventPublisher;
    private final Calendar calendar;

    Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(GroupOrganizerId groupOrganizerId, MeetingDraft meetingDraft) {
        if (!isSubscribed(groupOrganizerId))
            return left(GROUP_ORGANIZER_IS_NOT_SUBSCRIBED);
        if (!isSubscribed(meetingDraft.getGroupMeetingHostId()))
            return left(PROPOSED_MEETING_HOST_IS_NOT_SUBSCRIBED);
        if (!meetingGroupExists(meetingDraft.getMeetingGroupId()))
            return left(MEETING_GROUP_DOES_NOT_EXIST);
        if (!isGroupOrganizer(groupOrganizerId, meetingDraft.getMeetingGroupId()))
            return left(USER_IS_NOT_GROUP_ORGANIZER);
        if (!dateIs3DaysInAdvance(meetingDraft.getMeetingDate()))
            return left(MEETING_DATE_IS_NOT_3_DAYS_IN_ADVANCE);
        if (!isHostGroupMemberOrOrganizer(meetingDraft.getMeetingGroupId(), meetingDraft.getGroupMeetingHostId()))
            return left(PROPOSED_MEETING_HOST_IS_NOT_MEETING_GROUP_MEMBER);
        if (meetingNameIsAlreadyUsed(meetingDraft.getGroupMeetingName()))
            return left(MEETING_NAME_IS_NOT_UNIQUE);
        if (meetingNameIsBlank(meetingDraft.getGroupMeetingName()))
            return left(MEETING_NAME_IS_BLANK);
        var meeting = Meeting.create(groupOrganizerId, meetingDraft);
        var groupMeetingId = meetingRepository.save(meeting);
        eventPublisher.newMeetingWasScheduled(meeting.getMeetingGroupId(), groupMeetingId);
        return right(groupMeetingId);
    }

    private boolean meetingGroupExists(MeetingGroupId meetingGroupId) {
        return meetingGroupRepository.existsById(meetingGroupId);
    }

    private boolean isGroupOrganizer(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        return meetingGroupRepository
                .findById(meetingGroupId)
                .map(meetingGroup -> meetingGroup.getGroupOrganizerId().equals(groupOrganizerId))
                .getOrElse(false);
    }

    private boolean isSubscribed(GroupOrganizerId groupOrganizerId) {
        return activeSubscribersFinder.contains(new UserId(groupOrganizerId.getId()));
    }

    private boolean meetingNameIsAlreadyUsed(GroupMeetingName groupMeetingName) {
        return meetingRepository.existsByMeetingName(groupMeetingName);
    }

    private boolean meetingNameIsBlank(GroupMeetingName groupMeetingName) {
        return groupMeetingName.getName().isBlank();
    }

    private boolean isHostGroupMemberOrOrganizer(MeetingGroupId meetingGroupId, GroupMeetingHostId groupMeetingHostId) {
        var groupMemberId = new GroupMemberId(groupMeetingHostId.getId());
        return meetingGroupRepository
                .findById(meetingGroupId)
                .map(meetingGroup -> meetingGroup.contains(groupMemberId) || meetingGroup.isOrganizer(groupMemberId))
                .getOrElse(false);
    }

    private boolean isSubscribed(GroupMeetingHostId groupMeetingHostId) {
        return activeSubscribersFinder.contains(new UserId(groupMeetingHostId.getId()));
    }

    private boolean dateIs3DaysInAdvance(LocalDate meetingDate) {
        return calendar.getCurrentDate().plusDays(3).isBefore(meetingDate) ||
                calendar.getCurrentDate().plusDays(3).isEqual(meetingDate);
    }
}