package meeting.groups.commons;

import commons.dto.*;
import meeting.groups.MeetingGroupsFacade;
import meeting.groups.MeetingGroupsConfiguration;
import meeting.groups.dto.ProposalDraft;
import meeting.groups.dto.ProposalId;
import meeting.groups.query.dto.MeetingGroupDetails;
import meeting.groups.query.dto.ProposalDto;
import org.junit.Before;

import java.util.List;
import java.util.UUID;

import static meeting.groups.query.dto.ProposalDto.State.*;

public class TestSetup {
    private UserId userId;
    protected EventPublisherMock eventPublisherMock;
    protected MeetingGroupsFacade meetingGroupsFacade;

    @Before
    public void setup() {
        userId = new UserId("user-id");
        eventPublisherMock = new EventPublisherMock();
        meetingGroupsFacade = new MeetingGroupsConfiguration().inMemoryMeetingGroupsFacade(eventPublisherMock);
    }

    protected AdministratorId administratorId() {
        return new AdministratorId(userId().getId());
    }

    protected void create3randomGroupsForUser(UserId userId) {
        for (int i = 0; i < 3; i++) {
            createGroup(userId, randomProposal());
        }
    }

    protected MeetingGroupId createGroup(UserId userId, ProposalDraft proposal) {
        return AutomatedProcesses.createGroup(meetingGroupsFacade, userId, proposal);
    }

    protected MeetingGroupId createGroup(ProposalDraft proposal) {
        UserId userId = new UserId(UUID.randomUUID().toString());
        return AutomatedProcesses.createGroup(meetingGroupsFacade, userId, proposal);
    }

    protected ProposalDraft randomProposal() {
        return new ProposalDraft(UUID.randomUUID().toString());
    }

    protected UserId userId() {
        return userId;
    }

    protected ProposalDraft proposalWithName(String groupName) {
        return new ProposalDraft(groupName);
    }

    protected String randomGroupName() {
        return UUID.randomUUID().toString();
    }

    protected MeetingGroupId idDifferentThan(MeetingGroupId meetingGroupId) {
        var newMeetingGroupId = new MeetingGroupId(UUID.randomUUID().toString());
        assert !meetingGroupId.equals(newMeetingGroupId);
        return newMeetingGroupId;
    }

    protected ProposalId submitRandomProposal() {
        return AutomatedProcesses.submitProposal(meetingGroupsFacade, randomProposal());
    }

    protected ProposalId randomProposalId() {
        return new ProposalId(UUID.randomUUID().toString());
    }

    protected MeetingGroupDetails meetingGroupDetails(MeetingGroupId meetingGroupId, ProposalDraft proposalDraft, List<GroupMemberId> groupMembers) {
        var groupOrganizerId = new GroupOrganizerId(userId().getId());
        return new MeetingGroupDetails(meetingGroupId, proposalDraft.getGroupName(), groupOrganizerId, groupMembers);
    }

    protected GroupMemberId joinGroup(MeetingGroupId meetingGroupId) {
        var userId = new UserId(UUID.randomUUID().toString());
        meetingGroupsFacade.subscriptionRenewed(userId);
        assert meetingGroupsFacade.joinGroup(userId, meetingGroupId).isEmpty();
        return new GroupMemberId(userId.getId());
    }

    protected ProposalDto submitProposal() {
        var proposalDraft = randomProposal();
        var groupOrganizerId = new GroupOrganizerId(userId().getId());
        var proposalId = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId, proposalDraft).get();
        return new ProposalDto(proposalId, groupOrganizerId, proposalDraft.getGroupName(), WAITING);
    }

    protected ProposalDto submitAndAcceptProposal() {
        var proposalDraft = randomProposal();
        var groupOrganizerId = new GroupOrganizerId(userId().getId());
        var proposalId = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId, proposalDraft).get();
        var administratorId = new AdministratorId(userId().getId());
        meetingGroupsFacade.acceptProposal(administratorId, proposalId).get();
        return new ProposalDto(proposalId, groupOrganizerId, proposalDraft.getGroupName(), ACCEPTED);
    }

    protected ProposalDto submitAndRejectProposal() {
        var proposalDraft = randomProposal();
        var groupOrganizerId = new GroupOrganizerId(userId().getId());
        var proposalId = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId, proposalDraft).get();
        var administratorId = new AdministratorId(userId().getId());
        meetingGroupsFacade.rejectProposal(administratorId, proposalId);
        return new ProposalDto(proposalId, groupOrganizerId, proposalDraft.getGroupName(), REJECTED);
    }

    protected GroupOrganizerId groupOrganizerId() {
        return new GroupOrganizerId(userId().getId());
    }

    protected GroupMemberId groupMemberId() {
        return new GroupMemberId(userId().getId());
    }
}