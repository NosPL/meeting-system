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
class MeetingGroupsFacadeImpl implements MeetingGroupsFacade {
    private final ActiveUserSubscriptions activeUserSubscriptions;
    private final AdministratorRepository administratorRepository;
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
    public Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(UserId userId, ProposalDto proposalDto) {
        return proposalSubmitter.submitMeetingGroupProposal(userId, proposalDto);
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
}