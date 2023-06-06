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
    public void subscriptionRenewed(UserId userId) {
        meetingGroupsFacade.subscriptionRenewed(userId);
        log.info("subscription renewed for user with id {}", userId.getId());
    }

    @Override
    public void subscriptionExpired(UserId userId) {
        meetingGroupsFacade.subscriptionExpired(userId);
        log.info("subscription expired for user with id {}", userId.getId());
    }

    @Override
    public Option<JoinGroupFailure> joinGroup(UserId userId, MeetingGroupId meetingGroupId) {
        return meetingGroupsFacade
                .joinGroup(userId, meetingGroupId)
                .peek(failure -> log.info("user failed to join meeting group, user id {}, reason {}", meetingGroupId.getId(), failure));
    }

    @Override
    public Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(GroupOrganizerId groupOrganizerId, ProposalDraft proposalDraft) {
        return meetingGroupsFacade
                .submitMeetingGroupProposal(groupOrganizerId, proposalDraft)
                .peek(proposalId -> log.info("group organizer submitted proposal, organizer id {}, proposal name {}, proposal id {}", groupOrganizerId.getId(), proposalDraft.getGroupName(), proposalId.getId()))
                .peekLeft(proposalRejected -> log.info("failed to submit proposal, organizer id {}, reason {}", groupOrganizerId.getId(), proposalRejected));
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
                .onEmpty(() -> log.info("user rejected proposal, administrator id {}, proposal id {}", administratorId.getId(), proposalId.getId()));
    }

    @Override
    public void addAdministrator(AdministratorId administratorId) {
        meetingGroupsFacade.addAdministrator(administratorId);
        log.info("added new administrator with id {}", administratorId.getId());
    }

    @Override
    public void removeAdministrator(AdministratorId userId) {
        meetingGroupsFacade.removeAdministrator(userId);
        log.info("removed administrator, user id {}", userId.getId());
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