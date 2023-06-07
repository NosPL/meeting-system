package meetings;

import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meetings.dto.GroupMeetingId;
import meetings.dto.MeetingDraft;
import meetings.dto.ScheduleMeetingFailure;

@AllArgsConstructor
@Slf4j
class LogsDecorator implements MeetingsFacade {
    private final MeetingsFacade meetingsFacade;

    @Override
    public void subscriptionRenewed(UserId userId) {
        meetingsFacade.subscriptionRenewed(userId);
        log.info("subscription renewed, user id {}", userId.getId());
    }

    @Override
    public void subscriptionExpired(UserId userId) {
        meetingsFacade.subscriptionExpired(userId);
        log.info("subscription expired, user id {}", userId.getId());
    }

    @Override
    public void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
        log.info("new meeting group created, group organizer id {}, meeting group id {}", groupOrganizerId.getId(), meetingGroupId.getId());

    }

    @Override
    public void newMemberJoinedGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
        log.info("new member joined group, group member id {}, meeting group id {}", groupMemberId.getId(), meetingGroupId.getId());
    }

    @Override
    public Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(GroupOrganizerId groupOrganizerId, MeetingDraft meetingDraft) {
        return meetingsFacade
                .scheduleNewMeeting(groupOrganizerId, meetingDraft)
                .peek(groupMeetingId -> log.info("new meeting scheduled, meeting id {}", groupMeetingId.getId()))
                .peekLeft(failure -> log.info("failed to schedule meeting, reason: " + failure));
    }
}