package meeting.groups;

import commons.dto.MeetingGroupId;
import commons.dto.UserId;
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
class MeetingGroupsFacadeImpl implements MeetingGroupsFacade {
    private final ActiveUserSubscriptions activeUserSubscriptions;
    private final AdministratorRepository administratorRepository;
    private final ProposalRepository proposalRepository;
    private final MeetingGroupRepository meetingGroupRepository;
    private final GroupMembershipRepository membershipRepository;
    private final ProposalSubmitter proposalSubmitter;
    private final ProposalAccepter proposalAccepter;
    private final ProposalRejecter proposalRejecter;
    private final GroupJoiner groupJoiner;

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
        return groupJoiner.joinGroup(newMemberId, meetingGroupId);
    }

    @Override
    public Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(UserId userId, ProposalDraft proposalDraft) {
        return proposalSubmitter.submitMeetingGroupProposal(userId, proposalDraft);
    }

    @Override
    public Either<ProposalAcceptanceRejected, MeetingGroupId> acceptProposal(UserId userId, ProposalId proposalId) {
        return proposalAccepter.acceptProposal(userId, proposalId);
    }

    @Override
    public Option<FailedToRejectProposal> rejectProposal(UserId userId, ProposalId proposalId) {
        return proposalRejecter.rejectProposal(userId, proposalId);
    }

    @Override
    public void addAdministrator(UserId administratorId) {
        administratorRepository.save(new Administrator(administratorId.getId()));
    }

    @Override
    public void removeAdministrator(UserId administratorId) {
        administratorRepository.removeById(administratorId.getId());
    }

    @Override
    public List<ProposalDto> findAllProposalsByOrganizer(UserId userId) {
        return proposalRepository
                .findByOrganizerId(userId)
                .stream()
                .map(Proposal::toDto)
                .toList();
    }

    @Override
    public Option<MeetingGroupDetails> findMeetingGroupDetails(MeetingGroupId meetingGroupId) {
        List<String> groupMembers = getGroupMembers(meetingGroupId);
        return meetingGroupRepository
                .findById(meetingGroupId.getId())
                .map(meetingGroup -> toDto(meetingGroup, groupMembers));
    }

    private List<String> getGroupMembers(MeetingGroupId meetingGroupId) {
        return membershipRepository
                .findByGroupId(meetingGroupId.getId())
                .stream()
                .map(GroupMembership::getMemberId)
                .toList();
    }

    private MeetingGroupDetails toDto(MeetingGroup meetingGroup, List<String> groupMembers) {
        return new MeetingGroupDetails(meetingGroup.getId(), meetingGroup.getName(), meetingGroup.getOrganizerId(), groupMembers);
    }
}