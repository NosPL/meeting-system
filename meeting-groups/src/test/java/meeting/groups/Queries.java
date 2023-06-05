package meeting.groups;

import io.vavr.control.Option;
import meeting.groups.commons.TestSetup;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Queries extends TestSetup {

    @Before
    public void queriesSetup() {
        meetingGroupsFacade.subscriptionRenewed(userId());
        meetingGroupsFacade.addAdministrator(userId());
    }

    @Test
    public void queryAllWaitingProposalsOfGivenUser() {
//        given that user submitted proposal
        var proposalDto = submitProposal();
//        when user queries all proposals
        var result = meetingGroupsFacade.findAllProposalsByOrganizer(userId());
//        then he gets submitted proposal
        var expected = List.of(proposalDto);
        assertEquals(expected, result);
    }

    @Test
    public void queryAllAcceptedProposalsOfGivenUser() {
//        given that proposal was accepted
        var proposalDto = submitAndAcceptProposal();
//        when user queries all proposals
        var result = meetingGroupsFacade.findAllProposalsByOrganizer(userId());
//        then he gets proposal
        var expected = List.of(proposalDto);
        assertEquals(expected, result);
    }

    @Test
    public void queryAllRejectedProposalsOfGivenUser() {
//        given that proposal was rejected
        var proposalDto = submitAndRejectProposal();
//        when user queries all proposals
        var result = meetingGroupsFacade.findAllProposalsByOrganizer(userId());
//        then he gets proposal
        var expected = List.of(proposalDto);
        assertEquals(expected, result);
    }

    @Test
    public void queryAllMeetingGroupsWithMembers() {
//        given that meeting group was created
        var proposalDraft = randomProposal();
        var meetingGroupId = createGroup(userId(), proposalDraft);
//        and 3 users joined
        var userId1 = joinGroup(meetingGroupId);
        var userId2 = joinGroup(meetingGroupId);
        var userId3 = joinGroup(meetingGroupId);
//        when user queries meeting group details with given id
        var result = meetingGroupsFacade.findMeetingGroupDetails(meetingGroupId);
//        then he gets correct meeting group details
        var expected = Option.of(meetingGroupDetails(meetingGroupId, proposalDraft, List.of(userId1.getId(), userId2.getId(), userId3.getId())));
        assertEquals(expected, result);
    }
}