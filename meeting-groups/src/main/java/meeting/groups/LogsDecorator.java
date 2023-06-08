package meeting.groups;

import commons.dto.*;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.groups.dto.*;
import meeting.groups.query.dto.MeetingGroupDetails;
import meeting.groups.query.dto.ProposalDto;

import java.util.List;

@AllArgsConstructor
@Slf4j
class LogsDecorator implements MeetingGroupsFacade {
    private final MeetingGroupsFacade meetingGroupsFacade;

    @Override
    public Option<JoinGroupFailure> joinGroup(UserId userId, MeetingGroupId meetingGroupId) {
        return meetingGroupsFacade
                .joinGroup(userId, meetingGroupId)
                .peek(failure -> log.info("user failed to join meeting group, user id {}, reason {}", meetingGroupId.getId(), failure))
                .onEmpty(() -> log.info("user joined group, user id {}", userId.getId()));
    }

    @Override
    public Option<LeaveGroupFailure> leaveGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        return meetingGroupsFacade
                .leaveGroup(groupMemberId, meetingGroupId)
                .peek(failure -> log.info("group member failed to leave the group, group member id{}, reason: {}", groupMemberId.getId(), failure))
                .onEmpty(() -> log.info("member left the group, member id {}, group id {}", groupMemberId.getId(), meetingGroupId.getId()));
    }

    @Override
    public Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(GroupOrganizerId groupOrganizerId, ProposalDraft proposalDraft) {
        return meetingGroupsFacade
                .submitMeetingGroupProposal(groupOrganizerId, proposalDraft)
                .peek(proposalId -> log.info("group organizer submitted proposal, organizer id {}, proposal name {}, proposal id {}", groupOrganizerId.getId(), proposalDraft.getGroupName(), proposalId.getId()))
                .peekLeft(proposalRejected -> log.info("failed to submit proposal, organizer id {}, reason {}", groupOrganizerId.getId(), proposalRejected));
    }

    @Override
    public Option<RemoveProposalFailure> removeWaitingProposal(GroupOrganizerId groupOrganizerId, ProposalId proposalId) {
        return meetingGroupsFacade
                .removeWaitingProposal(groupOrganizerId, proposalId)
                .peek(failure -> log.info("failed to remove waiting proposal, user id {}, proposal id {}, reason: {}", groupOrganizerId.getId(), proposalId.getId(), failure))
                .onEmpty(() -> log.info("group organizer removed waiting proposal, group organizer id {}, proposal id {}", groupOrganizerId.getId(), proposalId.getId()));
    }

    @Override
    public Either<ProposalAcceptanceRejected, MeetingGroupId> acceptProposal(AdministratorId administratorId, ProposalId proposalId) {
        return meetingGroupsFacade
                .acceptProposal(administratorId, proposalId)
                .peek(meetingGroupId -> log.info("administrator accepted proposal, administrator id {}, meeting group id {}", administratorId.getId(), meetingGroupId.getId()))
                .peekLeft(proposalAcceptanceRejected -> log.info("failed to accept proposal, administrator id {}, reason {}", administratorId.getId(), proposalAcceptanceRejected));
    }

    @Override
    public Option<FailedToRejectProposal> rejectProposal(AdministratorId administratorId, ProposalId proposalId) {
        return meetingGroupsFacade
                .rejectProposal(administratorId, proposalId)
                .peek(failure -> log.info("failed to reject proposal, administrator id {}, proposal id {}, reason: {}", administratorId.getId(), proposalId.getId(), failure))
                .onEmpty(() -> log.info("proposal got rejected, administrator id {}, proposal id {}", administratorId.getId(), proposalId.getId()));
    }

    @Override
    public Option<RemoveGroupFailure> removeGroup(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        return meetingGroupsFacade
                .removeGroup(groupOrganizerId, meetingGroupId)
                .peek(failure -> log.info("failed to remove group, user id {}, reason: {}", groupOrganizerId.getId(), meetingGroupId.getId()))
                .onEmpty(() -> log.info("group organizer removed group, organizer id {}, group id {}", groupOrganizerId.getId(), meetingGroupId.getId()));
    }

    @Override
    public void administratorAdded(AdministratorId administratorId) {
        meetingGroupsFacade.administratorAdded(administratorId);
        log.info("added new administrator with id {}", administratorId.getId());
    }

    @Override
    public void administratorRemoved(AdministratorId userId) {
        meetingGroupsFacade.administratorRemoved(userId);
        log.info("removed administrator, user id {}", userId.getId());
    }

    @Override
    public void newMeetingScheduled(MeetingGroupId meetingGroupId, GroupMeetingId groupMeetingId) {
        meetingGroupsFacade.newMeetingScheduled(meetingGroupId, groupMeetingId);
        log.info("new group meeting got scheduled, group id {}, meeting id {}", meetingGroupId.getId(), groupMeetingId.getId());
    }

    @Override
    public void meetingWasHeld(MeetingGroupId meetingGroupId, GroupMeetingId groupMeetingId) {
        meetingGroupsFacade.meetingWasHeld(meetingGroupId, groupMeetingId);
        log.info("group meeting was held, group id {}, meeting id {}", meetingGroupId.getId(), groupMeetingId.getId());
    }

    @Override
    public void meetingCancelled(MeetingGroupId meetingGroupId, GroupMeetingId groupMeetingId) {
        meetingGroupsFacade.meetingCancelled(meetingGroupId, groupMeetingId);
        log.info("group meeting was cancelled, group id {}, meeting id {}", meetingGroupId.getId(), groupMeetingId.getId());
    }

    @Override
    public List<ProposalDto> findAllProposalsOfGroupOrganizer(GroupOrganizerId groupOrganizerId) {
        return meetingGroupsFacade.findAllProposalsOfGroupOrganizer(groupOrganizerId);
    }

    @Override
    public Option<MeetingGroupDetails> findMeetingGroupDetails(MeetingGroupId meetingGroupId) {
        return meetingGroupsFacade.findMeetingGroupDetails(meetingGroupId);
    }
}