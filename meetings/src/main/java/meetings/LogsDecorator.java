package meetings;

import commons.dto.*;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meetings.dto.*;

@AllArgsConstructor
@Slf4j
class LogsDecorator implements MeetingsFacade {
    private final MeetingsFacade meetingsFacade;

    @Override
    public Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(GroupOrganizerId groupOrganizerId, MeetingDraft meetingDraft) {
        return meetingsFacade
                .scheduleNewMeeting(groupOrganizerId, meetingDraft)
                .peek(groupMeetingId -> log.info("new meeting scheduled, meeting id {}", groupMeetingId.getId()))
                .peekLeft(failure -> log.info("failed to schedule meeting, reason: " + failure));
    }

    @Override
    public Option<CancelMeetingFailure> cancelMeeting(GroupOrganizerId groupOrganizerId, GroupMeetingId groupMeetingId) {
        return meetingsFacade
                .cancelMeeting(groupOrganizerId, groupMeetingId)
                .peek(failure -> log.info("user failed to cancel meeting, user id {}, meeting group id {}, reason: {}", groupOrganizerId.getId(), groupMeetingId.getId(), failure))
                .onEmpty(() -> log.info("group organizer cancelled meeting, group organizer id {}, meeting id {}", groupOrganizerId.getId(), groupMeetingId.getId()));
    }

    @Override
    public Option<SignUpForMeetingFailure> signUpForMeeting(GroupMemberId groupMemberId, GroupMeetingId groupMeetingId) {
        return meetingsFacade
                .signUpForMeeting(groupMemberId, groupMeetingId)
                .peek(failure -> log.info("group member failed to sign up for the meeting, reason: {}, group member id {}, meeting id {}", failure, groupMemberId.getId(), groupMeetingId.getId()))
                .onEmpty(() -> log.info("group member signed up for meeting, group member id {}, meeting id {}", groupMemberId.getId(), groupMeetingId.getId()));
    }

    @Override
    public Option<SignOutFailure> signOutFromMeeting(AttendeeId attendeeId, GroupMeetingId groupMeetingId) {
        return meetingsFacade
                .signOutFromMeeting(attendeeId, groupMeetingId)
                .peek(failure -> log.info("attendee failed to sign out from meeting, attendee id {}, meeting id {}, reason: {}", attendeeId.getId(), groupMeetingId.getId(), failure))
                .onEmpty(() -> log.info("attendee signed out from meeting, attendee id {}, meeting id {}", attendeeId.getId(), groupMeetingId.getId()));
    }

    @Override
    public void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
        log.info("new meeting group created, group organizer id {}, meeting group id {}", groupOrganizerId.getId(), meetingGroupId.getId());

    }

    @Override
    public void meetingGroupWasRemoved(MeetingGroupId meetingGroupId) {
        meetingsFacade.meetingGroupWasRemoved(meetingGroupId);
        log.info("meeting group was removed, meeting group id {}", meetingGroupId.getId());
    }

    @Override
    public void newMemberJoinedGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
        log.info("new member joined group, group member id {}, meeting group id {}", groupMemberId.getId(), meetingGroupId.getId());
    }

    @Override
    public void memberLeftTheMeetingGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        meetingsFacade.memberLeftTheMeetingGroup(groupMemberId, meetingGroupId);
        log.info("member left meeting group, member id {}, group id {}", groupMemberId.getId(), meetingGroupId.getId());
    }
}