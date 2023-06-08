package meeting.groups.commons;

import commons.active.subscribers.InMemoryActiveSubscribers;
import commons.dto.*;
import commons.event.publisher.EventPublisher;
import meeting.groups.MeetingGroupsConfiguration;
import meeting.groups.MeetingGroupsFacade;
import meeting.groups.dto.ProposalDraft;
import meeting.groups.dto.ProposalId;
import org.junit.Before;
import org.mockito.Mockito;

import java.util.UUID;

public class TestSetup {
    protected final UserId user = new UserId("subscribed-user");
    protected final GroupOrganizerId groupOrganizer = new GroupOrganizerId("subscribed-group-organizer");
    protected final AdministratorId administrator = new AdministratorId("administrator");
    protected InMemoryActiveSubscribers activeSubscribers;
    protected EventPublisher eventPublisher;
    protected MeetingGroupsFacade meetingGroupsFacade;

    @Before
    public void setup() {
        activeSubscribers = new InMemoryActiveSubscribers();
        eventPublisher = Mockito.mock(EventPublisher.class);
        meetingGroupsFacade = new MeetingGroupsConfiguration().inMemoryMeetingGroupsFacade(activeSubscribers, eventPublisher);
        activeSubscribers.add(user);
        activeSubscribers.add(new UserId(groupOrganizer.getId()));
        meetingGroupsFacade.administratorAdded(administrator);
    }

    protected MeetingGroupId createMeetingGroup() {
        return createMeetingGroup(groupOrganizer, randomGroupName());
    }

    protected MeetingGroupId createMeetingGroup(GroupOrganizerId groupOrganizerId) {
        return createMeetingGroup(groupOrganizerId, randomGroupName());
    }

    protected MeetingGroupId createMeetingGroup(GroupOrganizerId groupOrganizerId, String meetingGroupName) {
        return createMeetingGroup(groupOrganizerId, proposalDraftWithName(meetingGroupName));
    }

    protected MeetingGroupId createMeetingGroup(GroupOrganizerId groupOrganizerId, ProposalDraft proposalDraft) {
        var proposalId = meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId, proposalDraft).get();
        return meetingGroupsFacade.acceptProposal(administrator, proposalId).get();
    }

    protected ProposalId submitRandomProposal() {
        return submitProposal(randomProposalDraft());
    }

    protected ProposalId submitProposal(ProposalDraft proposalDraft) {
        return meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizer, proposalDraft).get();
    }

    protected ProposalId submitProposal(GroupOrganizerId groupOrganizerId, ProposalDraft proposalDraft) {
        return meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId, proposalDraft).get();
    }

    protected ProposalId submitProposal(GroupOrganizerId groupOrganizerId) {
        return submitProposal(groupOrganizerId, randomProposalDraft());
    }

    protected ProposalDraft randomProposalDraft() {
        return new ProposalDraft(randomGroupName());
    }

    protected ProposalDraft proposalDraftWithName(String groupName) {
        return new ProposalDraft(groupName);
    }

    protected ProposalId randomProposalId() {
        return new ProposalId(UUID.randomUUID().toString());
    }

    protected String randomGroupName() {
        return UUID.randomUUID().toString();
    }

    protected MeetingGroupId randomMeetingGroupId() {
        return new MeetingGroupId(UUID.randomUUID().toString());
    }

    protected GroupOrganizerId randomGroupOrganizerId() {
        return new GroupOrganizerId(UUID.randomUUID().toString());
    }

    protected GroupMeetingId randomGroupMeetingId() {
        return new GroupMeetingId(UUID.randomUUID().toString());
    }

    protected GroupMemberId groupMember(UserId userId) {
        return new GroupMemberId(userId.getId());
    }

    protected UserId user(GroupOrganizerId groupOrganizerId) {
        return new UserId(groupOrganizerId.getId());
    }
}