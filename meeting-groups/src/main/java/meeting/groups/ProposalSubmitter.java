package meeting.groups;

import commons.dto.GroupOrganizerId;
import commons.dto.UserId;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import meeting.groups.dto.ProposalDraft;
import meeting.groups.dto.ProposalId;
import meeting.groups.dto.ProposalRejected;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static meeting.groups.dto.ProposalRejected.*;
import static meeting.groups.dto.ProposalRejected.PROPOSAL_WITH_THE_SAME_GROUP_NAME_ALREADY_EXISTS;

@AllArgsConstructor
class ProposalSubmitter {
    private static final int GROUPS_PER_USER_LIMIT = 3;
    private final ProposalRepository proposalRepository;
    private final MeetingGroupRepository meetingGroupRepository;
    private final ActiveUserSubscriptions activeUserSubscriptions;

    Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(GroupOrganizerId groupOrganizerId, ProposalDraft proposalDraft) {
        if (!activeUserSubscriptions.contains(groupOrganizerId))
            return left(SUBSCRIPTION_NOT_ACTIVE);
        if (meetingGroupsPerUserLimitExceeded(groupOrganizerId))
            return left(GROUP_LIMIT_PER_USER_EXCEEDED);
        if (groupWithNameAlreadyExists(proposalDraft.getGroupName()))
            return left(MEETING_GROUP_WITH_PROPOSED_NAME_ALREADY_EXISTS);
        if (proposalWithSameGroupNameIsAlreadySubmitted(proposalDraft.getGroupName()))
            return left(PROPOSAL_WITH_THE_SAME_GROUP_NAME_ALREADY_EXISTS);
        var proposal = Proposal.createFrom(groupOrganizerId, proposalDraft);
        return right(proposalRepository.save(proposal));
    }

    private boolean meetingGroupsPerUserLimitExceeded(GroupOrganizerId groupOrganizerId) {
        int meetingGroupsCount = countAllGroupsOfOrganizer(groupOrganizerId);
        long waitingProposalsCount = countAllWaitingProposalsOfOrganizer(groupOrganizerId);
        return meetingGroupsCount + waitingProposalsCount >= GROUPS_PER_USER_LIMIT;
    }

    private boolean groupWithNameAlreadyExists(String groupName) {
        return meetingGroupRepository.existsByGroupName(groupName);
    }

    private boolean proposalWithSameGroupNameIsAlreadySubmitted(String groupName) {
        return proposalRepository.existsByGroupName(groupName);
    }

    private int countAllGroupsOfOrganizer(GroupOrganizerId groupOrganizerId) {
        return meetingGroupRepository.findByOrganizerId(groupOrganizerId).size();
    }

    private long countAllWaitingProposalsOfOrganizer(GroupOrganizerId groupOrganizerId) {
        return proposalRepository
                .findByOrganizerId(groupOrganizerId)
                .stream()
                .filter(Proposal::isWaitingForAdministratorDecision)
                .count();
    }
}