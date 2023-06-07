package meetings;

import commons.active.subscribers.ActiveSubscribersFinder;
import commons.dto.*;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import meetings.dto.*;

import java.util.HashSet;

import static io.vavr.control.Option.of;
import static java.util.function.Function.identity;
import static meetings.dto.SignUpForMeetingFailure.*;

@AllArgsConstructor
class MeetingsFacadeImpl implements MeetingsFacade {
    private final ActiveSubscribersFinder activeSubscribersFinder;
    private final MeetingGroupRepository meetingGroupRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingsScheduler meetingsScheduler;

    @Override
    public Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(GroupOrganizerId groupOrganizerId, MeetingDraft meetingDraft) {
        return meetingsScheduler.scheduleNewMeeting(groupOrganizerId, meetingDraft);
    }

    @Override
    public Option<CancelMeetingFailure> cancelMeeting(GroupOrganizerId groupOrganizerId, GroupMeetingId groupMeetingIdId) {
        return null;
    }

    @Override
    public Option<SignUpForMeetingFailure> signUpForMeeting(GroupMemberId groupMemberId, GroupMeetingId groupMeetingId) {
        if (!isSubscribed(groupMemberId))
            return of(GROUP_MEMBER_IS_NOT_SUBSCRIBED);
        return meetingRepository
                .findById(groupMeetingId)
                .toEither(MEETING_DOES_NOT_EXIST)
                .map(meeting -> signUp(meeting, groupMemberId))
                .fold(Option::of, identity());
    }

    @Override
    public Option<SignOutFailure> signOutFromMeeting(GroupMemberId groupMemberId, GroupMeetingId groupMeetingId) {
        return null;
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
                .findAll()
                .stream()
                .map(meeting -> meeting.remove(groupMemberId))
                .forEach(optionalEvent -> optionalEvent.peek(this::notifyAttendee));
    }

    private void notifyAttendee(Meeting.AttendeeAddedFromWaitList event) {

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