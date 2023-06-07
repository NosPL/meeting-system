package meeting.groups;

import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import io.vavr.control.Option;
import meeting.groups.commons.TestSetup;
import meeting.groups.dto.ProposalDraft;
import meeting.groups.dto.ProposalId;
import meeting.groups.query.dto.MeetingGroupDetails;
import meeting.groups.query.dto.ProposalDto;
import meeting.groups.query.dto.ProposalDto.State;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;

import static meeting.groups.query.dto.ProposalDto.State.*;
import static org.junit.Assert.assertEquals;

public class Queries extends TestSetup {

    @Test
    public void queryAllWaitingProposalsOfGivenUser() {
//        given that proposal was submitted
        var proposalDraft = randomProposalDraft();
        var proposalId = submitProposal(subscribedGroupOrganizer, proposalDraft);
//        when all proposals are queried
        var result = meetingGroupsFacade.findAllProposalsOfGroupOrganizer(subscribedGroupOrganizer);
//        then
        var proposalDto = proposalDto(proposalId, subscribedGroupOrganizer, proposalDraft, WAITING);
        var expected = List.of(proposalDto);
        assertEquals(expected, result);
    }

    @Test
    public void queryAllAcceptedProposalsOfGivenUser() {
//        given that meeting group was created
        var proposalDraft = randomProposalDraft();
        var proposalId = meetingGroupsFacade.submitMeetingGroupProposal(subscribedGroupOrganizer, proposalDraft).get();
        var meetingGroupId = meetingGroupsFacade.acceptProposal(administrator, proposalId).get();
//        when all proposals for organizers are queried
        var result = meetingGroupsFacade.findAllProposalsOfGroupOrganizer(subscribedGroupOrganizer);
//        then
        var expected = List.of(proposalDto(proposalId, subscribedGroupOrganizer, proposalDraft, ACCEPTED));
        assertEquals(expected, result);
    }

    @Test
    public void queryAllRejectedProposalsOfGivenUser() {
//        given that proposal was rejected
        var proposalDraft = randomProposalDraft();
        var proposalId = meetingGroupsFacade.submitMeetingGroupProposal(subscribedGroupOrganizer, proposalDraft).get();
        assert meetingGroupsFacade.rejectProposal(administrator, proposalId).isEmpty();
//        when all proposals get queried
        var result = meetingGroupsFacade.findAllProposalsOfGroupOrganizer(subscribedGroupOrganizer);
//        then he gets proposal
        var expected = List.of(proposalDto(proposalId, subscribedGroupOrganizer, proposalDraft, REJECTED));
        assertEquals(expected, result);
    }

    @Test
    public void queryAllMeetingGroupsWithMembers() {
//        given that meeting group was created
        String groupName = randomGroupName();
        var meetingGroupId = createMeetingGroup(subscribedGroupOrganizer, proposalDraftWithName(groupName));
//        and 3 users joined
        var groupMembers = _3usersJoined(meetingGroupId);
//        when meeting group details by id gets queried
        var result = meetingGroupsFacade.findMeetingGroupDetails(meetingGroupId);
//        then
        var expected = new MeetingGroupDetails(meetingGroupId, groupName, subscribedGroupOrganizer, groupMembers);
        assertEquals(Option.of(expected), result);
    }

    private Set<GroupMemberId> _3usersJoined(MeetingGroupId meetingGroupId) {
        Set<GroupMemberId> groupMembers = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            groupMembers.add(joinGroup(meetingGroupId));
        }
        return groupMembers;
    }

    private ProposalDto proposalDto(ProposalId proposalId, GroupOrganizerId groupOrganizerId, ProposalDraft proposalDraft, State state) {
        return new ProposalDto(proposalId, groupOrganizerId, proposalDraft.getGroupName(), state);
    }

    private GroupMemberId joinGroup(MeetingGroupId meetingGroupId) {
        var userId = new UserId(UUID.randomUUID().toString());
        activeSubscribers.add(userId);
        assert meetingGroupsFacade.joinGroup(userId, meetingGroupId).isEmpty();
        return new GroupMemberId(userId.getId());
    }
}