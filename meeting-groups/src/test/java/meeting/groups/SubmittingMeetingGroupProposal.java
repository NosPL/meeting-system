package meeting.groups;

import meeting.groups.commons.TestSetup;
import org.junit.Test;

import static io.vavr.control.Either.left;
import static meeting.groups.dto.ProposalRejected.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubmittingMeetingGroupProposal extends TestSetup {

    @Test
    public void submittingProposalByGroupOrganizerWith3GroupsShouldFail() {
//        given that group organizer created 3 meeting groups
        createMeetingGroup(groupOrganizer);
        createMeetingGroup(groupOrganizer);
        createMeetingGroup(groupOrganizer);
//        when group organizer tries to submit proposal for the 4th group
        var result = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizer, randomProposalDraft());
//        then user fails because of groups per user limit got exceeded
        assertEquals(left(GROUP_LIMIT_PER_USER_EXCEEDED), result);
    }

    @Test
    public void submittingProposalByGroupOrganizerWith2GroupsAnd1WaitingProposalShouldFail() {
//        given that group organizer created 2 meeting groups
        createMeetingGroup(groupOrganizer);
        createMeetingGroup(groupOrganizer);
//        and group organizer submitted 1 proposal
        assert meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizer, randomProposalDraft()).isRight();
//        when group organizer tries to submit 2nd proposal
        var result = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizer, randomProposalDraft());
//        then user fails because of groups per user limit got exceeded
        assertEquals(left(GROUP_LIMIT_PER_USER_EXCEEDED), result);
    }

    @Test
    public void submittingProposalWithGroupNameOccupiedByExistingMeetingGroupShouldFail() {
//        given that group with given name is already created
        var groupNameUsedTwice = randomGroupName();
        createMeetingGroup(groupOrganizer, groupNameUsedTwice);
//        when group organizer tries to submit proposal with the same name
        var result = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizer, proposalDraftWithName(groupNameUsedTwice));
//        then user fails because of groups per user limit got exceeded
        assertEquals(left(MEETING_GROUP_WITH_PROPOSED_NAME_ALREADY_EXISTS), result);
    }

    @Test
    public void submittingProposalWithNameOccupiedByOtherProposalShouldFail() {
//        given that proposal with given name is already submitted
        String nameUsedTwice = randomGroupName();
        assert meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizer, proposalDraftWithName(nameUsedTwice)).isRight();
//        when group organizer tries to submit proposal with the same name
        var result = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizer, proposalDraftWithName(nameUsedTwice));
//        then user fails because of groups per user limit got exceeded
        assertEquals(left(PROPOSAL_WITH_THE_SAME_GROUP_NAME_ALREADY_EXISTS), result);
    }

    @Test
    public void submittingProposalByGroupOrganizerWithoutActiveSubscriptionShouldFail() {
//        given that group organizer is not subscribed
        activeSubscribers.remove(user(groupOrganizer));
//        when he tries to submit proposal
        var result = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizer, randomProposalDraft());
//        then user fails because of groups per user limit got exceeded
        assertEquals(left(SUBSCRIPTION_NOT_ACTIVE), result);
    }

    @Test
    public void submittingProposalByGroupOrganizerWithLessThan3GroupsAndActiveSubscriptionShouldSucceed() {
//        when group organizer tries to submit proposal
        var result = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizer, randomProposalDraft());
//        then he succeeds
        assertTrue(result.isRight());
    }
}