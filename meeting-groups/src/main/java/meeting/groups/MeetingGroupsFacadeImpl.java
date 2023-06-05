package meeting.groups;

import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import commons.event.publisher.EventPublisher;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.groups.Proposal.ProposalAccepted;
import meeting.groups.dto.*;

import java.util.function.Function;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static meeting.groups.dto.FailedToRejectProposal.PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST;
import static meeting.groups.dto.JoinGroupFailure.*;
import static meeting.groups.dto.ProposalAcceptanceRejected.USER_IS_NOT_ADMINISTRATOR;
import static meeting.groups.dto.ProposalRejected.*;

@AllArgsConstructor
@Slf4j
class MeetingGroupsFacadeImpl implements MeetingGroupsFacade {
    private final ProposalRepository proposalRepository;
    private final MeetingGroupRepository meetingGroupRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final ActiveUserSubscriptions activeUserSubscriptions;
    private final AdministratorsRepository administratorsRepository;
    private final EventPublisher eventPublisher;
    private static final int GROUPS_PER_USER_LIMIT = 3;

    @Override
    public void subscriptionRenewed(UserId userId) {
        activeUserSubscriptions.add(userId);
    }

    @Override
    public void subscriptionExpired(UserId userId) {
        activeUserSubscriptions.remove(userId);
    }

    @Override
    public Option<JoinGroupFailure> joinGroup(UserId newMemberId, MeetingGroupId meetingGroupId) {
        if (!activeUserSubscriptions.contains(newMemberId))
            return Option.of(USER_SUBSCRIPTION_IS_NOT_ACTIVE);
        if (groupMembershipRepository.findByMemberIdAndGroupId(newMemberId.getId(), meetingGroupId.getId()).isDefined())
            return Option.of(USER_ALREADY_JOINED_GROUP);
        if (!meetingGroupExists(meetingGroupId))
            return Option.of(MEETING_GROUP_DOES_NOT_EXIST);
        if (userIsGroupOrganizer(newMemberId, meetingGroupId))
            return Option.of(USER_IS_GROUP_ORGANIZER);
        groupMembershipRepository.save(new GroupMembership(newMemberId.getId(), meetingGroupId.getId()));
        return Option.none();
    }

    private boolean userIsGroupOrganizer(UserId userId, MeetingGroupId meetingGroupId) {
        return meetingGroupRepository
                .findById(meetingGroupId.getId())
                .map(meetingGroup -> meetingGroup.isOrganizer(userId))
                .getOrElse(false);
    }

    @Override
    public Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(UserId userId, ProposalDto proposalDto) {
        if (!activeUserSubscriptions.contains(userId))
            return Either.left(SUBSCRIPTION_NOT_ACTIVE);
        if (meetingGroupsPerUserLimitExceeded(userId))
            return Either.left(GROUP_LIMIT_PER_USER_EXCEEDED);
        if (groupWithNameAlreadyExists(proposalDto.getGroupName()))
            return Either.left(MEETING_GROUP_WITH_PROPOSED_NAME_ALREADY_EXISTS);
        if (proposalWithSameGroupNameIsAlreadySubmitted(proposalDto.getGroupName()))
            return Either.left(PROPOSAL_WITH_THE_SAME_GROUP_NAME_ALREADY_EXISTS);
        Proposal proposal = Proposal.createFrom(userId, proposalDto);
        String proposalId = proposalRepository.save(proposal);
        return right(new ProposalId(proposalId));
    }

    @Override
    public Either<ProposalAcceptanceRejected, MeetingGroupId> acceptProposal(UserId userId, ProposalId proposalId) {
        if (!userIsAdministrator(userId))
            return left(USER_IS_NOT_ADMINISTRATOR);
        return proposalRepository
                .findById(proposalId.getId())
                .toEither(ProposalAcceptanceRejected.PROPOSAL_WITH_GIVEN_ID_DOES_NOT_EXIST)
                .flatMap(Proposal::accept)
                .map(this::createMeetingGroup)
                .peek(eventPublisher::newMeetingGroupCreated);
    }

    private MeetingGroupId createMeetingGroup(ProposalAccepted proposalAccepted) {
        MeetingGroup meetingGroup = MeetingGroup.createFromProposal(proposalAccepted);
        String meetingGroupId = meetingGroupRepository.save(meetingGroup);
        return new MeetingGroupId(meetingGroupId);
    }

    @Override
    public Option<FailedToRejectProposal> rejectProposal(UserId userId, ProposalId proposalId) {
        if (!userIsAdministrator(userId))
            return Option.of(FailedToRejectProposal.USER_IS_NOT_ADMINISTRATOR);
        return proposalRepository
                .findById(proposalId.getId())
                .toEither(PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST)
                .map(Proposal::reject)
                .fold(Option::of, Function.identity());
    }

    private boolean userIsAdministrator(UserId userId) {
        return administratorsRepository.existsById(userId.getId());
    }

    @Override
    public void addAdministrator(UserId administratorId) {
        administratorsRepository.save(new Administrator(administratorId.getId()));
    }

    @Override
    public void removeAdministrator(UserId administratorId) {
        administratorsRepository.removeById(administratorId.getId());
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
        return meetingGroupRepository.findByCreatorId(userId).size();
    }

    private long getWaitingProposalsCountOfUser(UserId userId) {
        return proposalRepository
                .findByUserId(userId)
                .stream()
                .filter(Proposal::isWaitingForAdministratorDecision)
                .count();
    }

    private boolean meetingGroupExists(MeetingGroupId meetingGroupId) {
        return meetingGroupRepository.existsById(meetingGroupId.getId());
    }
}