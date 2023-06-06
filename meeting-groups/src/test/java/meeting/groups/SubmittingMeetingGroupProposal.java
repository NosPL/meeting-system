package meeting.groups;

import meeting.groups.commons.TestSetup;
import org.junit.Test;

import static io.vavr.control.Either.left;
import static meeting.groups.dto.ProposalRejected.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubmittingMeetingGroupProposal extends TestSetup {

    @Test
    public void submittingProposalByUserWith3GroupsShouldFail() {
//        given that user has active subscription
        meetingGroupsFacade.subscriptionRenewed(userId());
//        and user has already created 3 groups
        create3randomGroupsForUser(userId());
//        when user tries to submit proposal for the 4th group
        var result = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId(), randomProposal());
//        then user fails because of groups per user limit got exceeded
        assertEquals(left(GROUP_LIMIT_PER_USER_EXCEEDED), result);
    }

    @Test
    public void submittingProposalWithNameOccupiedByExistingMeetingGroupShouldFail() {
//        given that user has active subscription
        meetingGroupsFacade.subscriptionRenewed(userId());
//        and group with given name is already created
        String nameUsedTwice = randomGroupName();
        createGroup(userId(), proposalWithName(nameUsedTwice));
//        when user tries to submit proposal with the same name
        var result = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId(), proposalWithName(nameUsedTwice));
//        then user fails because of groups per user limit got exceeded
        assertEquals(left(MEETING_GROUP_WITH_PROPOSED_NAME_ALREADY_EXISTS), result);
    }

    @Test
    public void submittingProposalWithNameOccupiedByOtherProposalShouldFail() {
//        given that user has active subscription
        meetingGroupsFacade.subscriptionRenewed(userId());
//        and proposal with given name is already submitted
        String nameUsedTwice = randomGroupName();
        assert meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId(), proposalWithName(nameUsedTwice)).isRight();
//        when user tries to submit proposal with occupied name
        var result = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId(), proposalWithName(nameUsedTwice));
//        then user fails because of groups per user limit got exceeded
        assertEquals(left(PROPOSAL_WITH_THE_SAME_GROUP_NAME_ALREADY_EXISTS), result);
    }

    @Test
    public void submittingProposalByUserWithoutActiveSubscriptionShouldFail() {
//        given that user has active subscription
        meetingGroupsFacade.subscriptionExpired(userId());
//        when user tries to submit proposal
        var result = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId(), randomProposal());
//        then user fails because of groups per user limit got exceeded
        assertEquals(left(SUBSCRIPTION_NOT_ACTIVE), result);
    }

    @Test
    public void submittingProposalByUserWithLessThan3GroupsAndActiveSubscriptionShouldSucceed() {
//        given that user has active subscription
        meetingGroupsFacade.subscriptionRenewed(userId());
//        when user tries to submit proposal with occupied name
        var result = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId(), randomProposal());
//        then user fails because of groups per user limit got exceeded
        assertTrue(result.isRight());
    }
}