package meeting.groups;

import commons.dto.UserId;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import meeting.groups.dto.FailedToRejectProposal;
import meeting.groups.dto.ProposalId;

import java.util.function.Function;

import static meeting.groups.dto.FailedToRejectProposal.PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST;

@AllArgsConstructor
class ProposalRejecter {
    private final ProposalRepository proposalRepository;
    private final AdministratorRepository administratorRepository;

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
        return administratorRepository.existsById(userId.getId());
    }
}
