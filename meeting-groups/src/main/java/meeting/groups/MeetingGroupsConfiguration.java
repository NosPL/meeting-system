package meeting.groups;

import commons.event.publisher.EventPublisher;

import java.util.LinkedHashSet;
import java.util.LinkedList;

public class MeetingGroupsConfiguration {
    private final ProposalRepository.InMemory proposalRepositoryInMemory = new ProposalRepository.InMemory(new LinkedList<>(), Proposal::getId);
    private final MeetingGroupRepository.InMemory meetingGroupRepositoryInMemory = new MeetingGroupRepository.InMemory(new LinkedList<>(), MeetingGroup::getId);
    private final GroupMembershipRepository.InMemory groupMembershipRepositoryInMemory = new GroupMembershipRepository.InMemory(new LinkedList<>(), GroupMembership::getMemberId);
    private final ActiveUserSubscriptions activeUserSubscriptionsInMemory = new ActiveUserSubscriptions(new LinkedHashSet<>());
    private final AdministratorRepository.InMemory administratorRepositoryInMemory = new AdministratorRepository.InMemory(new LinkedList<>(), Administrator::getId);

    public MeetingGroupsFacade meetingGroupsFacade(
            ProposalRepository proposalRepository,
            MeetingGroupRepository meetingGroupRepository,
            GroupMembershipRepository groupMembershipRepository,
            ActiveUserSubscriptions activeUserSubscriptions,
            AdministratorRepository administratorRepository,
            EventPublisher eventPublisher) {
        var proposalSubmitter = new ProposalSubmitter(proposalRepository, meetingGroupRepository, activeUserSubscriptions);
        var proposalAccepter = new ProposalAccepter(proposalRepository, meetingGroupRepository, administratorRepository, eventPublisher);
        var proposalRejecter = new ProposalRejecter(proposalRepository, administratorRepository);
        var groupJoiner = new GroupJoiner(activeUserSubscriptions, groupMembershipRepository, meetingGroupRepository, eventPublisher);
        var meetingGroups = new MeetingGroupsFacadeImpl(
                activeUserSubscriptions,
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
                activeUserSubscriptionsInMemory,
                administratorRepositoryInMemory,
                eventPublisher);
    }
}