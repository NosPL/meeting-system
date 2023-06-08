package meeting.groups;

import io.vavr.control.Option;
import meeting.groups.commons.TestSetup;
import meeting.groups.dto.ProposalId;
import org.junit.Test;

import static io.vavr.control.Option.of;
import static meeting.groups.dto.FailedToRejectProposal.*;
import static org.junit.Assert.assertEquals;

public class RejectingMeetingGroupProposal extends TestSetup {

    @Test
    public void userThatIsNotAdministratorShouldFailToRejectProposal() {
//        given that proposal was submitted
        var proposalId = submitRandomProposal();
//        and user is not administrator
        meetingGroupsFacade.administratorRemoved(administrator);
//        when user tries to reject proposal
        var result = meetingGroupsFacade.rejectProposal(administrator, proposalId);
//        then he fails
        assertEquals(Option.of(USER_IS_NOT_ADMINISTRATOR), result);
    }

    @Test
    public void administratorShouldFailToRejectProposalThatDoesNotExist() {
//        when administrator tries reject not existing proposal
        var result = meetingGroupsFacade.rejectProposal(administrator, randomProposalId());
//        then he fails
        assertEquals(Option.of(PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST), result);
    }

    @Test
    public void administratorShouldFailToRejectProposalThatAlreadyGotRejected() {
//        given that proposal was submitted
        var proposalId = submitRandomProposal();
//        and proposal was rejected
        assert meetingGroupsFacade.rejectProposal(administrator, proposalId).isEmpty();
//        when administrator tries to reject the same proposal again
        var result = meetingGroupsFacade.rejectProposal(administrator, proposalId);
//        then he fails because proposal was already rejected
        assertEquals(Option.of(PROPOSAL_IS_ALREADY_REJECTED), result);
    }

    @Test
    public void administratorShouldFailToRejectProposalThatAlreadyGotAccepted() {
//        given that proposal was submitted
        var proposalId = submitRandomProposal();
//        and proposal was accepted
        assert meetingGroupsFacade.acceptProposal(administrator, proposalId).isRight();
//        when administrator tries to reject the same proposal
        var result = meetingGroupsFacade.rejectProposal(administrator, proposalId);
//        then he fails because proposal was already accepted
        assertEquals(Option.of(PROPOSAL_IS_ALREADY_ACCEPTED), result);
    }

    @Test
    public void administratorShouldSuccessfullyRejectSubmittedProposal() {
//        given that proposal was submitted
        var proposalId = submitRandomProposal();
//        when administrator tries to reject proposal
        var result = meetingGroupsFacade.rejectProposal(administrator, proposalId);
//        then he succeeds
        assertEquals(Option.none(), result);
    }
}