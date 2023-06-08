package meeting.groups;

import commons.active.subscribers.ActiveSubscribersFinder;
import commons.dto.*;
import commons.event.publisher.EventPublisher;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.groups.dto.*;
import meeting.groups.query.dto.MeetingGroupDetails;
import meeting.groups.query.dto.ProposalDto;

import java.util.List;

import static io.vavr.control.Either.left;
import static io.vavr.control.Option.of;
import static java.util.function.Function.identity;
import static meeting.groups.dto.FailedToRejectProposal.PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST;
import static meeting.groups.dto.JoinGroupFailure.MEETING_GROUP_DOES_NOT_EXIST;
import static meeting.groups.dto.JoinGroupFailure.USER_SUBSCRIPTION_IS_NOT_ACTIVE;
import static meeting.groups.dto.LeaveGroupFailure.GROUP_DOES_NOT_EXIST;
import static meeting.groups.dto.ProposalAcceptanceRejected.PROPOSAL_WITH_GIVEN_ID_DOES_NOT_EXIST;
import static meeting.groups.dto.ProposalAcceptanceRejected.USER_IS_NOT_ADMINISTRATOR;
import static meeting.groups.dto.RemoveGroupFailure.*;
import static meeting.groups.dto.RemoveProposalFailure.*;
import static meeting.groups.dto.RemoveProposalFailure.USER_IS_NOT_GROUP_ORGANIZER;

@AllArgsConstructor
@Slf4j
class MeetingGroupsFacadeImpl implements MeetingGroupsFacade {
    private final AdministratorRepository administratorRepository;
    private final ProposalRepository proposalRepository;
    private final MeetingGroupRepository meetingGroupRepository;
    private final ProposalSubmitter proposalSubmitter;
    private final ActiveSubscribersFinder activeSubscribersFinder;
    private final EventPublisher eventPublisher;

    @Override
    public Option<JoinGroupFailure> joinGroup(UserId userId, MeetingGroupId meetingGroupId) {
        if (!activeSubscribersFinder.contains(userId))
            return of(USER_SUBSCRIPTION_IS_NOT_ACTIVE);
        return meetingGroupRepository
                .findById(meetingGroupId)
                .toEither(MEETING_GROUP_DOES_NOT_EXIST)
                .flatMap(meetingGroup -> meetingGroup.join(userId))
                .peek(groupMemberId -> eventPublisher.newMemberJoinedMeetingGroup(groupMemberId, meetingGroupId))
                .swap().toOption();
    }

    @Override
    public Option<LeaveGroupFailure> leaveGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        return meetingGroupRepository
                .findById(meetingGroupId)
                .toEither(GROUP_DOES_NOT_EXIST)
                .map(meetingGroup -> meetingGroup.leave(groupMemberId))
                .peek(failure -> failure.onEmpty(() -> eventPublisher.groupMemberLeftGroup(groupMemberId, meetingGroupId)))
                .fold(Option::of, identity());
    }

    @Override
    public Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(GroupOrganizerId groupOrganizerId, ProposalDraft proposalDraft) {
        return proposalSubmitter.submitMeetingGroupProposal(groupOrganizerId, proposalDraft);
    }

    @Override
    public Option<RemoveProposalFailure> removeWaitingProposal(GroupOrganizerId groupOrganizerId, ProposalId proposalId) {
        return proposalRepository
                .findById(proposalId)
                .toEither(PROPOSAL_DOES_NOT_EXIST)
                .map(proposal -> remove(groupOrganizerId, proposal))
                .fold(Option::of, identity());
    }

    private Option<RemoveProposalFailure> remove(GroupOrganizerId groupOrganizerId, Proposal proposal) {
        if (!proposal.getGroupOrganizerId().equals(groupOrganizerId))
            return of(USER_IS_NOT_GROUP_ORGANIZER);
        if (!proposal.isWaitingForAdministratorDecision())
            return of(PROPOSAL_ALREADY_PROCESSED);
        proposalRepository.removeById(proposal.getProposalId());
        return Option.none();
    }

    @Override
    public Either<ProposalAcceptanceRejected, MeetingGroupId> acceptProposal(AdministratorId administratorId, ProposalId proposalId) {
        if (!isAdministrator(administratorId))
            return left(USER_IS_NOT_ADMINISTRATOR);
        return proposalRepository
                .findById(proposalId)
                .toEither(PROPOSAL_WITH_GIVEN_ID_DOES_NOT_EXIST)
                .flatMap(Proposal::accept)
                .map(MeetingGroup::createFromProposal)
                .peek(meetingGroupRepository::save)
                .peek(this::publishNewMeetingGroupCreated)
                .map(MeetingGroup::getMeetingGroupId);
    }

    private void publishNewMeetingGroupCreated(MeetingGroup meetingGroup) {
        eventPublisher.newMeetingGroupCreated(meetingGroup.getGroupOrganizerId(), meetingGroup.getMeetingGroupId());
    }

    private boolean isAdministrator(AdministratorId administratorId) {
        return administratorRepository.existsById(administratorId);
    }

    @Override
    public Option<FailedToRejectProposal> rejectProposal(AdministratorId administratorId, ProposalId proposalId) {
        if (!isAdministrator(administratorId))
            return of(FailedToRejectProposal.USER_IS_NOT_ADMINISTRATOR);
        return proposalRepository
                .findById(proposalId)
                .toEither(PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST)
                .map(Proposal::reject)
                .fold(Option::of, identity());
    }

    @Override
    public Option<RemoveGroupFailure> removeGroup(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        return meetingGroupRepository
                .findById(meetingGroupId)
                .toEither(GROUP_DOESNT_EXIST)
                .map(meetingGroup -> remove(meetingGroup, groupOrganizerId))
                .fold(Option::of, identity());
    }

    private Option<RemoveGroupFailure> remove(MeetingGroup meetingGroup, GroupOrganizerId groupOrganizerId) {
        if (meetingGroup.hasScheduledMeetings())
            return of(GROUP_HAS_SCHEDULED_MEETINGS);
        if (!meetingGroup.getGroupOrganizerId().equals(groupOrganizerId))
            return of(RemoveGroupFailure.USER_IS_NOT_GROUP_ORGANIZER);
        var meetingGroupId = meetingGroup.getMeetingGroupId();
        meetingGroupRepository.removeById(meetingGroupId);
        eventPublisher.meetingGroupWasRemoved(meetingGroupId);
        return Option.none();
    }

    @Override
    public void administratorAdded(AdministratorId administratorId) {
        administratorRepository.save(new Administrator(administratorId));
    }

    @Override
    public void administratorRemoved(AdministratorId administratorId) {
        administratorRepository.removeById(administratorId);
    }

    @Override
    public void newMeetingScheduled(MeetingGroupId meetingGroupId, GroupMeetingId groupMeetingId) {
        meetingGroupRepository
                .findById(meetingGroupId)
                .peek(meetingGroup -> meetingGroup.newMeetingWasScheduled(groupMeetingId));
    }

    @Override
    public void meetingWasHeld(MeetingGroupId meetingGroupId, GroupMeetingId groupMeetingId) {
        meetingGroupRepository
                .findById(meetingGroupId)
                .peek(meetingGroup -> meetingGroup.meetingHeld(groupMeetingId));
    }

    @Override
    public void meetingCancelled(MeetingGroupId meetingGroupId, GroupMeetingId groupMeetingId) {
        meetingGroupRepository
                .findById(meetingGroupId)
                .peek(meetingGroup -> meetingGroup.meetingCancelled(groupMeetingId));
    }

    @Override
    public List<ProposalDto> findAllProposalsOfGroupOrganizer(GroupOrganizerId groupOrganizerId) {
        return proposalRepository
                .findByOrganizerId(groupOrganizerId)
                .stream()
                .map(Proposal::toDto)
                .toList();
    }

    @Override
    public Option<MeetingGroupDetails> findMeetingGroupDetails(MeetingGroupId meetingGroupId) {
        return meetingGroupRepository
                .findById(meetingGroupId)
                .map(MeetingGroup::toDto);
    }
}