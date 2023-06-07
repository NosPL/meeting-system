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
class MeetingGroupsFacadeImpl implements MeetingGroupsFacade {
    private final AdministratorRepository administratorRepository;
    private final ProposalRepository proposalRepository;
    private final MeetingGroupRepository meetingGroupRepository;
    private final ProposalSubmitter proposalSubmitter;
    private final ProposalAccepter proposalAccepter;
    private final ProposalRejecter proposalRejecter;
    private final GroupJoiner groupJoiner;

    @Override
    public Option<JoinGroupFailure> joinGroup(UserId userId, MeetingGroupId meetingGroupId) {
        return groupJoiner.joinGroup(userId, meetingGroupId);
    }

    @Override
    public Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(GroupOrganizerId groupOrganizerId, ProposalDraft proposalDraft) {
        return proposalSubmitter.submitMeetingGroupProposal(groupOrganizerId, proposalDraft);
    }

    @Override
    public Either<ProposalAcceptanceRejected, MeetingGroupId> acceptProposal(AdministratorId administratorId, ProposalId proposalId) {
        return proposalAccepter.acceptProposal(administratorId, proposalId);
    }

    @Override
    public Option<FailedToRejectProposal> rejectProposal(AdministratorId administratorId, ProposalId proposalId) {
        return proposalRejecter.rejectProposal(administratorId, proposalId);
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