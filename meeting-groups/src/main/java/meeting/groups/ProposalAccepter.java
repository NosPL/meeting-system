package meeting.groups;

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

    Either<ProposalAcceptanceRejected, MeetingGroupId> acceptProposal(UserId userId, ProposalId proposalId) {
        if (!userIsAdministrator(userId))
            return left(USER_IS_NOT_ADMINISTRATOR);
        return proposalRepository
                .findById(proposalId.getId())
                .toEither(PROPOSAL_WITH_GIVEN_ID_DOES_NOT_EXIST)
                .flatMap(Proposal::accept)
                .map(MeetingGroup::createFromProposal)
                .peek(meetingGroupRepository::save)
                .map(meetingGroup -> {
                    UserId organizerId = new UserId(meetingGroup.getOrganizerId());
                    MeetingGroupId meetingGroupId = new MeetingGroupId(meetingGroup.getId());
                    eventPublisher.newMeetingGroupCreated(organizerId, meetingGroupId);
                    return meetingGroupId;
                });
    }

    private boolean userIsAdministrator(UserId userId) {
        return administratorRepository.existsById(userId.getId());
    }
}