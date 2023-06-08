package meeting.groups;


import io.vavr.control.Option;
import meeting.groups.commons.TestSetup;
import org.junit.Test;

import static meeting.groups.dto.RemoveProposalFailure.*;
import static org.junit.Assert.assertEquals;

public class RemovingWaitingProposals extends TestSetup {

    @Test
    public void userShouldFailToRemoveNotExistingProposal() {
//        when user tries to remove not existing proposal
        var result = meetingGroupsFacade.removeWaitingProposal(randomGroupOrganizerId(), randomProposalId());
//        then failure
        assertEquals(Option.of(PROPOSAL_DOES_NOT_EXIST), result);
    }

    @Test
    public void userThatIsNotGroupOrganizerShouldFailToRemoveWaitingProposal() {
//        given that proposal was submitted
        var proposalId = submitRandomProposal();
//        when user that is not group organizer tries to remove proposal
        var result = meetingGroupsFacade.removeWaitingProposal(randomGroupOrganizerId(), proposalId);
//        then failure
        assertEquals(Option.of(USER_IS_NOT_GROUP_ORGANIZER), result);
    }

    @Test
    public void groupOrganizerShouldFailToRemoveAcceptedProposal() {
//        given that proposal was submitted
        var proposalId = submitProposal(groupOrganizer);
//        and proposal was accepted
        assert meetingGroupsFacade.acceptProposal(administrator, proposalId).isRight();
//        when group organizer tries to remove proposal
        var result = meetingGroupsFacade.removeWaitingProposal(groupOrganizer, proposalId);
//        then failure
        assertEquals(Option.of(PROPOSAL_ALREADY_PROCESSED), result);
    }

    @Test
    public void groupOrganizerShouldFailToRemoveRejectedProposal() {
//        given that proposal was submitted
        var proposalId = submitProposal(groupOrganizer);
//        and proposal was rejected
        assert meetingGroupsFacade.rejectProposal(administrator, proposalId).isEmpty();
//        when group organizer tries to remove proposal
        var result = meetingGroupsFacade.removeWaitingProposal(groupOrganizer, proposalId);
//        then failure
        assertEquals(Option.of(PROPOSAL_ALREADY_PROCESSED), result);
    }

    @Test
    public void groupOrganizerShouldSuccessfullyRemoveWaitingProposal() {
//        given that proposal was submitted
        var proposalId = submitProposal(groupOrganizer);
//        when group organizer tries to remove proposal
        var result = meetingGroupsFacade.removeWaitingProposal(groupOrganizer, proposalId);
//        then he succeeds
        assertEquals(Option.none(), result);
    }
}