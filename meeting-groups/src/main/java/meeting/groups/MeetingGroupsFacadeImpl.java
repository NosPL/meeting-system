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
import java.util.function.Function;

import static io.vavr.control.Either.left;
import static io.vavr.control.Option.of;
import static meeting.groups.dto.FailedToRejectProposal.PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST;
import static meeting.groups.dto.JoinGroupFailure.MEETING_GROUP_DOES_NOT_EXIST;
import static meeting.groups.dto.JoinGroupFailure.USER_SUBSCRIPTION_IS_NOT_ACTIVE;
import static meeting.groups.dto.ProposalAcceptanceRejected.PROPOSAL_WITH_GIVEN_ID_DOES_NOT_EXIST;
import static meeting.groups.dto.ProposalAcceptanceRejected.USER_IS_NOT_ADMINISTRATOR;

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
    public Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(GroupOrganizerId groupOrganizerId, ProposalDraft proposalDraft) {
        return proposalSubmitter.submitMeetingGroupProposal(groupOrganizerId, proposalDraft);
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
                .fold(Option::of, Function.identity());
    }

    @Override
    public void addAdministrator(AdministratorId administratorId) {
        administratorRepository.save(new Administrator(administratorId));
    }

    @Override
    public void removeAdministrator(AdministratorId administratorId) {
        administratorRepository.removeById(administratorId);
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