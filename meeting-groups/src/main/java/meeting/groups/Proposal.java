package meeting.groups;


import commons.dto.UserId;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import meeting.groups.dto.FailedToRejectProposal;
import meeting.groups.dto.ProposalAcceptanceRejected;
import meeting.groups.dto.ProposalDto;

import java.util.UUID;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static io.vavr.control.Option.of;
import static lombok.AccessLevel.PRIVATE;
import static meeting.groups.dto.FailedToRejectProposal.PROPOSAL_IS_ALREADY_ACCEPTED;
import static meeting.groups.dto.FailedToRejectProposal.PROPOSAL_IS_ALREADY_REJECTED;
import static meeting.groups.dto.ProposalAcceptanceRejected.PROPOSAL_WAS_ALREADY_ACCEPTED;
import static meeting.groups.dto.ProposalAcceptanceRejected.PROPOSAL_WAS_ALREADY_REJECTED;

@AllArgsConstructor(access = PRIVATE)
@Getter
class Proposal {
    private String id;
    private String organizerId;
    private String groupName;
    private State state;

    Either<ProposalAcceptanceRejected, ProposalAccepted> accept() {
        if (state == State.PROPOSAL_ACCEPTED)
            return left(PROPOSAL_WAS_ALREADY_ACCEPTED);
        if (state == State.PROPOSAL_REJECTED)
            return left(PROPOSAL_WAS_ALREADY_REJECTED);
        this.state = State.PROPOSAL_ACCEPTED;
        return right(new ProposalAccepted(id, organizerId, groupName));
    }

    Option<FailedToRejectProposal> reject() {
        if (state == State.PROPOSAL_ACCEPTED)
            return of(PROPOSAL_IS_ALREADY_ACCEPTED);
        if (state == State.PROPOSAL_REJECTED)
            return of(PROPOSAL_IS_ALREADY_REJECTED);
        this.state = State.PROPOSAL_REJECTED;
        return Option.none();
    }

    boolean isWaitingForAdministratorDecision() {
        return state == State.WAITING_FOR_ADMIN_DECISION;
    }

    static Proposal createFrom(UserId userId, ProposalDto proposalDto) {
        return new Proposal(UUID.randomUUID().toString(), userId.getId(), proposalDto.getGroupName(), State.WAITING_FOR_ADMIN_DECISION);
    }

    private enum State {
        WAITING_FOR_ADMIN_DECISION,
        PROPOSAL_ACCEPTED,
        PROPOSAL_REJECTED
    }

    @Value
    static class ProposalAccepted {
        String id;
        String organizerId;
        String groupName;
    }
}