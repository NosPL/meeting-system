package meeting.groups;

import commons.dto.UserId;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import meeting.groups.dto.ProposalDraft;
import meeting.groups.dto.ProposalId;
import meeting.groups.dto.ProposalRejected;

import static io.vavr.control.Either.right;
import static meeting.groups.dto.ProposalRejected.*;
import static meeting.groups.dto.ProposalRejected.PROPOSAL_WITH_THE_SAME_GROUP_NAME_ALREADY_EXISTS;

@AllArgsConstructor
class ProposalSubmitter {
    private static final int GROUPS_PER_USER_LIMIT = 3;
    private final ProposalRepository proposalRepository;
    private final MeetingGroupRepository meetingGroupRepository;
    private final ActiveUserSubscriptions activeUserSubscriptions;

    Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(UserId userId, ProposalDraft proposalDraft) {
        if (!activeUserSubscriptions.contains(userId))
            return Either.left(SUBSCRIPTION_NOT_ACTIVE);
        if (meetingGroupsPerUserLimitExceeded(userId))
            return Either.left(GROUP_LIMIT_PER_USER_EXCEEDED);
        if (groupWithNameAlreadyExists(proposalDraft.getGroupName()))
            return Either.left(MEETING_GROUP_WITH_PROPOSED_NAME_ALREADY_EXISTS);
        if (proposalWithSameGroupNameIsAlreadySubmitted(proposalDraft.getGroupName()))
            return Either.left(PROPOSAL_WITH_THE_SAME_GROUP_NAME_ALREADY_EXISTS);
        Proposal proposal = Proposal.createFrom(userId, proposalDraft);
        String proposalId = proposalRepository.save(proposal);
        return right(new ProposalId(proposalId));
    }

    private boolean meetingGroupsPerUserLimitExceeded(UserId userId) {
        int meetingGroupsCount = getMeetingGroupsCountOfUser(userId);
        long waitingProposalsCount = getWaitingProposalsCountOfUser(userId);
        return meetingGroupsCount + waitingProposalsCount >= GROUPS_PER_USER_LIMIT;
    }

    private boolean groupWithNameAlreadyExists(String groupName) {
        return meetingGroupRepository.existsByGroupName(groupName);
    }

    private boolean proposalWithSameGroupNameIsAlreadySubmitted(String groupName) {
        return proposalRepository.existsByGroupName(groupName);
    }

    private int getMeetingGroupsCountOfUser(UserId userId) {
        return meetingGroupRepository.findByOrganizerId(userId).size();
    }

    private long getWaitingProposalsCountOfUser(UserId userId) {
        return proposalRepository
                .findByOrganizerId(userId)
                .stream()
                .filter(Proposal::isWaitingForAdministratorDecision)
                .count();
    }
}