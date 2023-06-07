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
    public Option<CancelMeetingFailure> cancelMeeting(GroupOrganizerId groupOrganizerId, GroupMeetingId groupMeetingIdId) {
        return meetingsFacade
                .cancelMeeting(groupOrganizerId, groupMeetingIdId)
                .peek(failure -> log.info("user failed to cancel meeting, user id {}, meeting group id {}, reason: {}", groupOrganizerId.getId(), groupMeetingIdId.getId(), failure))
                .onEmpty(() -> log.info("meeting"));
    }

    @Override
    public Option<SignUpForMeetingFailure> signUpForMeeting(GroupMemberId groupMemberId, GroupMeetingId groupMeetingId) {
        return meetingsFacade
                .signUpForMeeting(groupMemberId, groupMeetingId)
                .peek(failure -> log.info("group member failed to sign up for the meeting, reason: {}, group member id {}, meeting id {}", failure, groupMemberId.getId(), groupMeetingId.getId()))
                .onEmpty(() -> log.info("group member signed up for meeting, group member id {}, meeting id {}", groupMemberId.getId(), groupMeetingId.getId()));
    }

    @Override
    public Option<SignOutFailure> signOutFromMeeting(GroupMemberId groupMemberId, GroupMeetingId groupMeetingId) {
        return null;
    }

    @Override
    public void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
        log.info("new meeting group created, group organizer id {}, meeting group id {}", groupOrganizerId.getId(), meetingGroupId.getId());

    }

    @Override
    public void meetingGroupWasRemoved(MeetingGroupId meetingGroupId) {

    }

    @Override
    public void newMemberJoinedGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
        log.info("new member joined group, group member id {}, meeting group id {}", groupMemberId.getId(), meetingGroupId.getId());
    }

    @Override
    public void memberLeftTheMeetingGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        meetingsFacade.memberLeftTheMeetingGroup(groupMemberId, meetingGroupId);
    }

}