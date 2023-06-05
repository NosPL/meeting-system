package meeting.groups;

import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.groups.dto.*;

@AllArgsConstructor
@Slf4j
class LogsDecorator implements MeetingGroupsFacade {
    private final MeetingGroupsFacade meetingGroups;

    @Override
    public void subscriptionRenewed(UserId userId) {
        meetingGroups.subscriptionRenewed(userId);
        log.info("subscription renewed for user with id {}", userId.getId());
    }

    @Override
    public void subscriptionExpired(UserId userId) {
        meetingGroups.subscriptionExpired(userId);
        log.info("subscription expired for user with id {}", userId.getId());
    }

    @Override
    public Option<JoinGroupFailure> joinGroup(UserId userId, MeetingGroupId meetingGroupId) {
        return meetingGroups
                .joinGroup(userId, meetingGroupId)
                .peek(failure -> log.info("user failed to join meeting group, user id {}, reason {}", userId.getId(), failure));
    }

    @Override
    public Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(UserId userId, ProposalDraft proposalDraft) {
        return meetingGroups
                .submitMeetingGroupProposal(userId, proposalDraft)
                .peek(proposalId -> log.info("user submitted proposal, user id {}, proposal name {}, proposal id {}", userId.getId(), proposalDraft.getGroupName(), proposalId.getId()))
                .peekLeft(proposalRejected -> log.info("failed to submit proposal, user id {}, reason {}", userId.getId(), proposalRejected));
    }

    @Override
    public Either<ProposalAcceptanceRejected, MeetingGroupId> acceptProposal(UserId userId, ProposalId proposalId) {
        return meetingGroups
                .acceptProposal(userId, proposalId)
                .peek(meetingGroupId -> log.info("admin accepted proposal, admin id {}, meeting group id {}", userId.getId(), meetingGroupId.getId()))
                .peekLeft(proposalAcceptanceRejected -> log.info("failed to accept proposal, user id {}, reason {}", userId.getId(), proposalAcceptanceRejected));
    }

    @Override
    public Option<FailedToRejectProposal> rejectProposal(UserId userId, ProposalId proposalId) {
        return meetingGroups
                .rejectProposal(userId, proposalId)
                .peek(failure -> log.info("failed to reject proposal, user id {}, proposal id {}, reason: {}", userId.getId(), proposalId.getId(), failure))
                .onEmpty(() -> log.info("user rejected proposal, user id {}, proposal id {}", userId.getId(), proposalId.getId()));
    }

    @Override
    public void addAdministrator(UserId administratorId) {
        meetingGroups.addAdministrator(administratorId);
        log.info("added new administrator with id {}", administratorId.getId());
    }

    @Override
    public void removeAdministrator(UserId userId) {
        meetingGroups.removeAdministrator(userId);
        log.info("removed user from administration, user id {}", userId.getId());
    }
}