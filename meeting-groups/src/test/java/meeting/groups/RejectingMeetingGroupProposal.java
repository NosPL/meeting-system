package meeting.groups;

import meeting.groups.commons.TestSetup;
import meeting.groups.dto.ProposalId;
import org.junit.Test;

import static io.vavr.control.Option.of;
import static meeting.groups.dto.FailedToRejectProposal.*;
import static org.junit.Assert.assertEquals;

public class RejectingMeetingGroupProposal extends TestSetup {

    @Test
    public void userThatIsNotAdministratorShouldFailToRejectProposal() {
//        given that user is not administrator
        meetingGroupsFacade.removeAdministrator(userId());
//        and proposal was submitted
        var proposalId = submitRandomProposal();
//        when user tries to reject proposal
        var result = meetingGroupsFacade.rejectProposal(userId(), proposalId);
//        then he fails because he is not administrator
        assertEquals(of(USER_IS_NOT_ADMINISTRATOR), result);
    }

    @Test
    public void administratorShouldFailToRejectProposalThatDoesNotExist() {
//        given that user is administrator
        meetingGroupsFacade.addAdministrator(userId());
//        when user tries reject with proposal random id
        var result = meetingGroupsFacade.rejectProposal(userId(), randomProposalId());
//        then he fails because proposal doesn't exist
        assertEquals(of(PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST), result);
    }

    @Test
    public void administratorShouldFailToRejectProposalThatAlreadyGotRejected() {
//        given that user is administrator
        meetingGroupsFacade.addAdministrator(userId());
//        and proposal was submitted
        var proposalId = submitRandomProposal();
//        and proposal was rejected
        assert meetingGroupsFacade.rejectProposal(userId(), proposalId).isEmpty();
//        when user tries to reject the same proposal again
        var result = meetingGroupsFacade.rejectProposal(userId(), proposalId);
//        then he fails because proposal was already rejected
        assertEquals(of(PROPOSAL_IS_ALREADY_REJECTED), result);
    }

    @Test
    public void administratorShouldFailToRejectProposalThatAlreadyGotAccepted() {
//        given that user is administrator
        meetingGroupsFacade.addAdministrator(userId());
//        and proposal was submitted
        var proposalId = submitRandomProposal();
//        and proposal was accepted
        assert meetingGroupsFacade.acceptProposal(userId(), proposalId).isRight();
//        when user tries to reject the same proposal
        var result = meetingGroupsFacade.rejectProposal(userId(), proposalId);
//        then he fails because proposal was already accepted
        assertEquals(of(PROPOSAL_IS_ALREADY_ACCEPTED), result);
    }

    @Test
    public void administratorShouldSuccessfullyRejectSubmittedProposal() {
//        given that user is administrator
        meetingGroupsFacade.addAdministrator(userId());
//        and proposal was submitted
        var proposalId = submitRandomProposal();
//        when user tries to reject proposal
        var result = meetingGroupsFacade.rejectProposal(userId(), proposalId);
//        then he succeeds
        assert result.isEmpty();
    }
}