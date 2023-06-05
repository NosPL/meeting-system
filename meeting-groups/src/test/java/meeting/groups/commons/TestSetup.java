package meeting.groups.commons;

import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import meeting.groups.MeetingGroupsFacade;
import meeting.groups.MeetingGroupsConfiguration;
import meeting.groups.dto.ProposalDraft;
import meeting.groups.dto.ProposalId;
import org.junit.Before;

import java.util.UUID;

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
}