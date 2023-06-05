package meeting.groups;

import commons.event.publisher.EventPublisher;

import java.util.LinkedHashSet;
import java.util.LinkedList;

public class MeetingGroupsConfiguration {

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
        var groupJoiner = new GroupJoiner(activeUserSubscriptions, groupMembershipRepository, meetingGroupRepository);
        var meetingGroups = new MeetingGroupsFacadeImpl(activeUserSubscriptions, administratorRepository, proposalSubmitter, proposalAccepter, proposalRejecter, groupJoiner);
        return new LogsDecorator(meetingGroups);
    }

    public MeetingGroupsFacade inMemoryMeetingGroupsFacade(EventPublisher eventPublisher) {
        return meetingGroupsFacade(
                new ProposalRepository.InMemory(new LinkedList<>(), Proposal::getId),
                new MeetingGroupRepository.InMemory(new LinkedList<>(), MeetingGroup::getId),
                new GroupMembershipRepository.InMemory(new LinkedList<>(), GroupMembership::getMemberId),
                new ActiveUserSubscriptions(new LinkedHashSet<>()),
                new AdministratorRepository.InMemory(new LinkedList<>(), Administrator::getId),
                eventPublisher);
    }
}