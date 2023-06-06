package meeting.groups;

import commons.dto.AdministratorId;
import commons.dto.UserId;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import meeting.groups.dto.FailedToRejectProposal;
import meeting.groups.dto.ProposalId;

import java.util.function.Function;

import static io.vavr.control.Option.of;
import static meeting.groups.dto.FailedToRejectProposal.PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST;
import static meeting.groups.dto.FailedToRejectProposal.USER_IS_NOT_ADMINISTRATOR;

@AllArgsConstructor
class ProposalRejecter {
    private final ProposalRepository proposalRepository;
    private final AdministratorRepository administratorRepository;

    Option<FailedToRejectProposal> rejectProposal(AdministratorId administratorId, ProposalId proposalId) {
        if (!isAdministrator(administratorId))
            return of(USER_IS_NOT_ADMINISTRATOR);
        return proposalRepository
                .findById(proposalId)
                .toEither(PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST)
                .map(Proposal::reject)
                .fold(Option::of, Function.identity());
    }

    private boolean isAdministrator(AdministratorId administratorId) {
        return administratorRepository.existsById(administratorId);
    }
}
