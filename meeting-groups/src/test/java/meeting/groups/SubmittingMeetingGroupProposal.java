package meeting.groups;

import commons.dto.GroupOrganizerId;
import commons.dto.UserId;
import meeting.groups.commons.TestSetup;
import org.junit.Before;
import org.junit.Test;

import static io.vavr.control.Either.left;
import static meeting.groups.dto.ProposalRejected.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubmittingMeetingGroupProposal extends TestSetup {
    private final GroupOrganizerId notSubscribedGroupOrganizer = new GroupOrganizerId("not-subscribed-group-organizer");

    @Test
    public void submittingProposalByGroupOrganizerWith3GroupsShouldFail() {
//        given that subscribed group organizer created 3 meeting groups
        create3MeetingGroups(subscribedGroupOrganizer);
//        when subscribed group organizer tries to submit proposal for the 4th group
        var result = meetingGroupsFacade.submitMeetingGroupProposal(subscribedGroupOrganizer, randomProposalDraft());
//        then user fails because of groups per user limit got exceeded
        assertEquals(left(GROUP_LIMIT_PER_USER_EXCEEDED), result);
    }

    @Test
    public void submittingProposalWithGroupNameOccupiedByExistingMeetingGroupShouldFail() {
//        given that group with given name is already created
        var groupNameUsedTwice = randomGroupName();
        createMeetingGroup(subscribedGroupOrganizer, groupNameUsedTwice);
//        when subscribed group organizer tries to submit proposal with the same name
        var result = meetingGroupsFacade.submitMeetingGroupProposal(subscribedGroupOrganizer, proposalDraftWithName(groupNameUsedTwice));
//        then user fails because of groups per user limit got exceeded
        assertEquals(left(MEETING_GROUP_WITH_PROPOSED_NAME_ALREADY_EXISTS), result);
    }

    @Test
    public void submittingProposalWithNameOccupiedByOtherProposalShouldFail() {
//        given that proposal with given name is already submitted
        String nameUsedTwice = randomGroupName();
        assert meetingGroupsFacade.submitMeetingGroupProposal(subscribedGroupOrganizer, proposalDraftWithName(nameUsedTwice)).isRight();
//        when subscribed group organizer tries to submit proposal with the same name
        var result = meetingGroupsFacade.submitMeetingGroupProposal(subscribedGroupOrganizer, proposalDraftWithName(nameUsedTwice));
//        then user fails because of groups per user limit got exceeded
        assertEquals(left(PROPOSAL_WITH_THE_SAME_GROUP_NAME_ALREADY_EXISTS), result);
    }

    @Test
    public void submittingProposalByGroupOrganizerWithoutActiveSubscriptionShouldFail() {
//        when group organizer without active subscription tries to submit proposal
        var result = meetingGroupsFacade.submitMeetingGroupProposal(notSubscribedGroupOrganizer, randomProposalDraft());
//        then user fails because of groups per user limit got exceeded
        assertEquals(left(SUBSCRIPTION_NOT_ACTIVE), result);
    }

    @Test
    public void submittingProposalByGroupOrganizerWithLessThan3GroupsAndActiveSubscriptionShouldSucceed() {
//        when subscribed group organizer tries to submit proposal
        var result = meetingGroupsFacade.submitMeetingGroupProposal(subscribedGroupOrganizer, randomProposalDraft());
//        then he succeeds
        assertTrue(result.isRight());
    }

    private void create3MeetingGroups(GroupOrganizerId groupOrganizerId) {
        for (int i = 0; i < 3; i++)
            createMeetingGroup(groupOrganizerId);
    }
}