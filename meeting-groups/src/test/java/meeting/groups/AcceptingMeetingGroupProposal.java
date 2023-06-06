package meeting.groups;

import meeting.groups.commons.TestSetup;
import org.junit.Test;

import java.util.List;

import static io.vavr.Tuple.of;
import static io.vavr.control.Either.left;
import static meeting.groups.dto.ProposalAcceptanceRejected.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AcceptingMeetingGroupProposal extends TestSetup {

    @Test
    public void userThatIsNotAdministratorShouldFailToAcceptMeetingGroupProposal() {
//        given that user is not the administrator
        meetingGroupsFacade.removeAdministrator(administratorId());
//        and group proposal was submitted
        var proposalId = submitRandomProposal();
//        when user tries to accept group proposal
        var result = meetingGroupsFacade.acceptProposal(administratorId(), proposalId);
//        then he fails
        assertEquals(left(USER_IS_NOT_ADMINISTRATOR), result);
    }

    @Test
    public void administratorShouldFailToAcceptProposalThatWasAlreadyAccepted() {
//        given that user is the administrator
        meetingGroupsFacade.addAdministrator(administratorId());
//        and group proposal was submitted
        var proposalId = submitRandomProposal();
//        and proposal got accepted
        assert meetingGroupsFacade.acceptProposal(administratorId(), proposalId).isRight();
//        when user tries to accept the same proposal again
        var result = meetingGroupsFacade.acceptProposal(administratorId(), proposalId);
//        then he fails at second attempt
        assertEquals(left(PROPOSAL_WAS_ALREADY_ACCEPTED), result);
    }

    @Test
    public void administratorShouldFailToAcceptProposalThatAlreadyGotRejected() {
//        given that user is the administrator
        meetingGroupsFacade.addAdministrator(administratorId());
//        and group proposal was submitted
        var proposalId = submitRandomProposal();
//        and proposal got rejected
        assert meetingGroupsFacade.rejectProposal(administratorId(), proposalId).isEmpty();
//        when user tries to accept the same proposal
        var result = meetingGroupsFacade.acceptProposal(administratorId(), proposalId);
//        then he fails because proposal was already rejected
        assertEquals(left(PROPOSAL_WAS_ALREADY_REJECTED), result);
    }

    @Test
    public void administratorShouldFailToAcceptProposalThatDoesNotExist() {
//        given that user is the administrator
        meetingGroupsFacade.addAdministrator(administratorId());
//        when user tries to accept not existing proposal
        var result = meetingGroupsFacade.acceptProposal(administratorId(), randomProposalId());
//        then he fails at second attempt
        assertEquals(left(PROPOSAL_WITH_GIVEN_ID_DOES_NOT_EXIST), result);
    }

    @Test
    public void administratorShouldSuccessfullyAcceptSubmittedMeetingGroupProposal() {
//        given that user is the administrator
        meetingGroupsFacade.addAdministrator(administratorId());
//        and user subscription got renewed
        meetingGroupsFacade.subscriptionRenewed(userId());
//        and group proposal was submitted
        var proposalId = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId(), randomProposal()).get();
//        when user tries to accept group proposal
        var result = meetingGroupsFacade.acceptProposal(administratorId(), proposalId);
//        then he succeeds
        assertTrue(result.isRight());
//        and 'new meeting group was created' event got emitted
        assert eventPublisherMock.groupCreatedEventInvoked(List.of(of(groupOrganizerId(), result.get())));
    }
}