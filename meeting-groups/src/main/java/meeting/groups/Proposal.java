package meeting.groups;


import commons.dto.GroupOrganizerId;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import meeting.groups.dto.FailedToRejectProposal;
import meeting.groups.dto.ProposalAcceptanceRejected;
import meeting.groups.dto.ProposalDraft;
import meeting.groups.dto.ProposalId;
import meeting.groups.query.dto.ProposalDto;

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
    private ProposalId proposalId;
    private GroupOrganizerId groupOrganizerId;
    private String groupName;
    private State state;

    Either<ProposalAcceptanceRejected, ProposalAccepted> accept() {
        if (state == State.PROPOSAL_ACCEPTED)
            return left(PROPOSAL_WAS_ALREADY_ACCEPTED);
        if (state == State.PROPOSAL_REJECTED)
            return left(PROPOSAL_WAS_ALREADY_REJECTED);
        this.state = State.PROPOSAL_ACCEPTED;
        return right(new ProposalAccepted(proposalId, groupOrganizerId, groupName));
    }

    Option<FailedToRejectProposal> reject() {
        if (state == State.PROPOSAL_ACCEPTED)
            return of(PROPOSAL_IS_ALREADY_ACCEPTED);
        if (state == State.PROPOSAL_REJECTED)
            return of(PROPOSAL_IS_ALREADY_REJECTED);
        this.state = State.PROPOSAL_REJECTED;
        return Option.none();
    }

    ProposalDto toDto() {
        return new ProposalDto(proposalId, groupOrganizerId, groupName, toDto(state));
    }

    private ProposalDto.State toDto(State state) {
        if (state == State.WAITING_FOR_ADMIN_DECISION)
            return ProposalDto.State.WAITING;
        if (state == State.PROPOSAL_ACCEPTED)
            return ProposalDto.State.ACCEPTED;
        else
            return ProposalDto.State.REJECTED;
    }

    boolean isWaitingForAdministratorDecision() {
        return state == State.WAITING_FOR_ADMIN_DECISION;
    }

    static Proposal createFrom(GroupOrganizerId groupOrganizerId, ProposalDraft proposalDraft) {
        String id = UUID.randomUUID().toString();
        return new Proposal(new ProposalId(id), groupOrganizerId, proposalDraft.getGroupName(), State.WAITING_FOR_ADMIN_DECISION);
    }

    private enum State {
        WAITING_FOR_ADMIN_DECISION,
        PROPOSAL_ACCEPTED,
        PROPOSAL_REJECTED
    }

    @Value
    static class ProposalAccepted {
        ProposalId proposalId;
        GroupOrganizerId groupOrganizerId;
        String groupName;
    }
}