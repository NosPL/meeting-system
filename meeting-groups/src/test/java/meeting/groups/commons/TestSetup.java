package meeting.groups.commons;

import commons.active.subscribers.InMemoryActiveSubscribers;
import commons.dto.AdministratorId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import meeting.groups.MeetingGroupsConfiguration;
import meeting.groups.MeetingGroupsFacade;
import meeting.groups.dto.ProposalDraft;
import meeting.groups.dto.ProposalId;
import org.junit.Before;

import java.util.UUID;

public class TestSetup {
    protected final UserId subscribedUser = new UserId("subscribed-user");
    protected final UserId notSubscribedUser = new UserId("not-subscribed-user");
    protected final GroupOrganizerId subscribedGroupOrganizer = new GroupOrganizerId("subscribed-group-organizer");
    protected final AdministratorId administrator = new AdministratorId("administrator");
    protected final AdministratorId notAdministrator = new AdministratorId("not-administrator");
    protected InMemoryActiveSubscribers activeSubscribers;
    protected EventPublisherMock eventPublisherMock;
    protected MeetingGroupsFacade meetingGroupsFacade;

    @Before
    public void setup() {
        activeSubscribers = new InMemoryActiveSubscribers();
        eventPublisherMock = new EventPublisherMock();
        meetingGroupsFacade = new MeetingGroupsConfiguration().inMemoryMeetingGroupsFacade(activeSubscribers, eventPublisherMock);
        activeSubscribers.add(subscribedUser);
        activeSubscribers.add(new UserId(subscribedGroupOrganizer.getId()));
        meetingGroupsFacade.addAdministrator(administrator);
    }

    protected MeetingGroupId createMeetingGroup() {
        return createMeetingGroup(subscribedGroupOrganizer, randomGroupName());
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
        return meetingGroupsFacade.submitMeetingGroupProposal(subscribedGroupOrganizer, proposalDraft).get();
    }

    protected ProposalId submitProposal(GroupOrganizerId groupOrganizerId, ProposalDraft proposalDraft) {
        return meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId, proposalDraft).get();
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
}