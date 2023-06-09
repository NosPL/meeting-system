package meetings;

import commons.active.subscribers.ActiveSubscribersFinder;
import commons.dto.*;
import commons.event.publisher.EventPublisher;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import meetings.dto.*;
import meetings.notifications.MeetingsNotificationsFacade;

import java.util.HashSet;

import static io.vavr.control.Option.of;
import static java.util.function.Function.identity;
import static meetings.dto.CancelMeetingFailure.MEETING_DOESNT_EXIST;
import static meetings.dto.CancelMeetingFailure.USER_IS_NOT_GROUP_ORGANIZER;
import static meetings.dto.SignOutFailure.MEETING_DOES_NOT_EXIST;
import static meetings.dto.SignUpForMeetingFailure.*;

@AllArgsConstructor
class MeetingsFacadeImpl implements MeetingsFacade {
    private final ActiveSubscribersFinder activeSubscribersFinder;
    private final MeetingGroupRepository meetingGroupRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingsScheduler meetingsScheduler;
    private final MeetingsNotificationsFacade meetingsNotificationsFacade;
    private final EventPublisher eventPublisher;

    @Override
    public Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(GroupOrganizerId groupOrganizerId, MeetingDraft meetingDraft) {
        return meetingsScheduler.scheduleNewMeeting(groupOrganizerId, meetingDraft);
    }

    @Override
    public Option<CancelMeetingFailure> cancelMeeting(GroupOrganizerId groupOrganizerId, GroupMeetingId groupMeetingId) {
        return meetingRepository
                .findById(groupMeetingId)
                .toEither(MEETING_DOESNT_EXIST)
                .map(meeting -> cancelMeeting(groupOrganizerId, meeting))
                .fold(Option::of, identity());
    }

    private Option<CancelMeetingFailure> cancelMeeting(GroupOrganizerId groupOrganizerId, Meeting meeting) {
        if (!meeting.getGroupOrganizerId().equals(groupOrganizerId))
            return Option.of(USER_IS_NOT_GROUP_ORGANIZER);
        meetingRepository.removeById(meeting.getGroupMeetingId());
        var attendees = meeting.getAttendees();
        eventPublisher.meetingWasCancelled(meeting.getMeetingGroupId(), meeting.getGroupMeetingId());
        meetingsNotificationsFacade.notifyAttendeesAboutMeetingCancellation(attendees);
        return Option.none();
    }

    @Override
    public Option<SignUpForMeetingFailure> signUpForMeeting(GroupMemberId groupMemberId, GroupMeetingId groupMeetingId) {
        if (!isSubscribed(groupMemberId))
            return of(GROUP_MEMBER_IS_NOT_SUBSCRIBED);
        return meetingRepository
                .findById(groupMeetingId)
                .toEither(SignUpForMeetingFailure.MEETING_DOES_NOT_EXIST)
                .map(meeting -> signUp(meeting, groupMemberId))
                .fold(Option::of, identity());
    }

    @Override
    public Option<SignOutFailure> signOutFromMeeting(AttendeeId attendeeId, GroupMeetingId groupMeetingId) {
        return meetingRepository
                .findById(groupMeetingId)
                .toEither(MEETING_DOES_NOT_EXIST)
                .flatMap(meeting -> meeting.signOut(attendeeId))
                .peek(optionalEvent -> optionalEvent.peek(this::notifyAttendee))
                .swap().toOption();
    }

    private void notifyAttendee(Meeting.AttendeeSignedUpFromWaitList event) {
        meetingsNotificationsFacade.notifyAttendeeAboutBeingSignedUpFromWaitingList(event.getAttendeeId());
    }

    @Override
    public void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        meetingGroupRepository.save(new MeetingGroup(meetingGroupId, groupOrganizerId, new HashSet<>()));
    }

    @Override
    public void meetingGroupWasRemoved(MeetingGroupId meetingGroupId) {
        meetingGroupRepository.removeById(meetingGroupId);
    }

    @Override
    public void newMemberJoinedGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        meetingGroupRepository
                .findById(meetingGroupId)
                .peek(meetingGroup -> meetingGroup.add(groupMemberId));
    }

    @Override
    public void memberLeftTheMeetingGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        meetingGroupRepository
                .findById(meetingGroupId)
                .peek(meetingGroup -> meetingGroup.remove(groupMemberId));
        meetingRepository
                .findByMeetingGroupId(meetingGroupId)
                .stream()
                .map(meeting -> meeting.remove(groupMemberId))
                .forEach(optionalEvent -> optionalEvent.peek(this::notifyAttendee));
    }

    private Option<SignUpForMeetingFailure> signUp(Meeting meeting, GroupMemberId groupMemberId) {
        var meetingGroupId = meeting.getMeetingGroupId();
        if (!isGroupMember(groupMemberId, meetingGroupId))
            return of(USER_IS_NOT_GROUP_MEMBER);
        return meeting.signUp(groupMemberId);
    }

    private boolean isGroupMember(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        return meetingGroupRepository
                .findById(meetingGroupId)
                .map(meetingGroup -> meetingGroup.contains(groupMemberId))
                .getOrElse(false);
    }

    private boolean isSubscribed(GroupMemberId groupMemberId) {
        return activeSubscribersFinder.contains(new UserId(groupMemberId.getId()));
    }
}