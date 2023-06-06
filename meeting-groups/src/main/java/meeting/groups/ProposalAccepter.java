package meeting.groups;

import commons.dto.AdministratorId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import commons.event.publisher.EventPublisher;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import meeting.groups.dto.ProposalAcceptanceRejected;
import meeting.groups.dto.ProposalId;

import static io.vavr.control.Either.left;
import static meeting.groups.dto.ProposalAcceptanceRejected.PROPOSAL_WITH_GIVEN_ID_DOES_NOT_EXIST;
import static meeting.groups.dto.ProposalAcceptanceRejected.USER_IS_NOT_ADMINISTRATOR;

@AllArgsConstructor
class ProposalAccepter {
    private final ProposalRepository proposalRepository;
    private final MeetingGroupRepository meetingGroupRepository;
    private final AdministratorRepository administratorRepository;
    private final EventPublisher eventPublisher;

    Either<ProposalAcceptanceRejected, MeetingGroupId> acceptProposal(AdministratorId administratorId, ProposalId proposalId) {
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
}