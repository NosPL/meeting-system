package meeting.groups;

import commons.event.publisher.EventPublisher;

import java.util.LinkedHashSet;
import java.util.LinkedList;

public class MeetingGroupsConfiguration {
    private final ProposalRepository.InMemory proposalRepositoryInMemory = new ProposalRepository.InMemory(new LinkedList<>(), Proposal::getProposalId);
    private final MeetingGroupRepository.InMemory meetingGroupRepositoryInMemory = new MeetingGroupRepository.InMemory(new LinkedList<>(), MeetingGroup::getMeetingGroupId);
    private final GroupMembershipRepository.InMemory groupMembershipRepositoryInMemory = new GroupMembershipRepository.InMemory(new LinkedList<>(), GroupMembership::getGroupMembershipId);
    private final ActiveSubscriptions activeSubscriptionsInMemory = new ActiveSubscriptions(new LinkedHashSet<>());
    private final AdministratorRepository.InMemory administratorRepositoryInMemory = new AdministratorRepository.InMemory(new LinkedList<>(), Administrator::getAdministratorId);

    public MeetingGroupsFacade meetingGroupsFacade(
            ProposalRepository proposalRepository,
            MeetingGroupRepository meetingGroupRepository,
            GroupMembershipRepository groupMembershipRepository,
            ActiveSubscriptions activeSubscriptions,
            AdministratorRepository administratorRepository,
            EventPublisher eventPublisher) {
        var proposalSubmitter = new ProposalSubmitter(proposalRepository, meetingGroupRepository, activeSubscriptions);
        var proposalAccepter = new ProposalAccepter(proposalRepository, meetingGroupRepository, administratorRepository, eventPublisher);
        var proposalRejecter = new ProposalRejecter(proposalRepository, administratorRepository);
        var groupJoiner = new GroupJoiner(activeSubscriptions, groupMembershipRepository, meetingGroupRepository, eventPublisher);
        var meetingGroups = new MeetingGroupsFacadeImpl(
                activeSubscriptions,
                administratorRepository,
                proposalRepository,
                meetingGroupRepository,
                groupMembershipRepository,
                proposalSubmitter,
                proposalAccepter,
                proposalRejecter,
                groupJoiner);
        return new LogsDecorator(meetingGroups);
    }

    public MeetingGroupsFacade inMemoryMeetingGroupsFacade(EventPublisher eventPublisher) {
        return meetingGroupsFacade(
                proposalRepositoryInMemory,
                meetingGroupRepositoryInMemory,
                groupMembershipRepositoryInMemory,
                activeSubscriptionsInMemory,
                administratorRepositoryInMemory,
                eventPublisher);
    }
}