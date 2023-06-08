package meeting.groups;

import meeting.groups.commons.TestSetup;
import org.junit.Test;

import static io.vavr.Tuple.of;
import static io.vavr.control.Either.left;
import static meeting.groups.dto.ProposalAcceptanceRejected.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

public class AcceptingMeetingGroupProposal extends TestSetup {

    @Test
    public void userThatIsNotAdministratorShouldFailToAcceptMeetingGroupProposal() {
//        given that group proposal was submitted
        var proposalId = submitRandomProposal();
//        and user is not administrator
        meetingGroupsFacade.administratorRemoved(administrator);
//        when he tries to accept group proposal
        var result = meetingGroupsFacade.acceptProposal(administrator, proposalId);
//        then he fails
        assertEquals(left(USER_IS_NOT_ADMINISTRATOR), result);
    }

    @Test
    public void administratorShouldFailToAcceptProposalThatWasAlreadyAccepted() {
//        given that group proposal was submitted
        var proposalId = submitRandomProposal();
//        and proposal got accepted
        assert meetingGroupsFacade.acceptProposal(administrator, proposalId).isRight();
//        when administrator tries to accept the same proposal again
        var result = meetingGroupsFacade.acceptProposal(administrator, proposalId);
//        then he fails
        assertEquals(left(PROPOSAL_WAS_ALREADY_ACCEPTED), result);
    }

    @Test
    public void administratorShouldFailToAcceptProposalThatAlreadyGotRejected() {
//        given that group proposal was submitted
        var proposalId = submitRandomProposal();
//        and proposal got rejected
        assert meetingGroupsFacade.rejectProposal(administrator, proposalId).isEmpty();
//        when administrator tries to accept the same proposal
        var result = meetingGroupsFacade.acceptProposal(administrator, proposalId);
//        then he fails
        assertEquals(left(PROPOSAL_WAS_ALREADY_REJECTED), result);
    }

    @Test
    public void administratorShouldFailToAcceptProposalThatDoesNotExist() {
//        when administrator tries to accept not existing proposal
        var result = meetingGroupsFacade.acceptProposal(administrator, randomProposalId());
//        then he fails
        assertEquals(left(PROPOSAL_WITH_GIVEN_ID_DOES_NOT_EXIST), result);
    }

    @Test
    public void administratorShouldSuccessfullyAcceptSubmittedMeetingGroupProposal() {
//        given that group proposal was submitted
        var proposalId = submitRandomProposal();
//        when administrator tries to accept group proposal
        var result = meetingGroupsFacade.acceptProposal(administrator, proposalId);
//        then he succeeds
        assertTrue(result.isRight());
//        and 'new meeting group was created' event got emitted
        var meetingGroupId = result.get();
        verify(eventPublisher).newMeetingGroupCreated(groupOrganizer, meetingGroupId);
    }
}