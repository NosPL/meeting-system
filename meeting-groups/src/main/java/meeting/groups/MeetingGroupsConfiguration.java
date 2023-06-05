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
            AdministratorsRepository administratorsRepository,
            EventPublisher eventPublisher) {
        MeetingGroupsFacadeImpl meetingGroups = new MeetingGroupsFacadeImpl(
                proposalRepository,
                meetingGroupRepository,
                groupMembershipRepository,
                activeUserSubscriptions,
                administratorsRepository,
                eventPublisher);
        return new LogsDecorator(meetingGroups);
    }

    public MeetingGroupsFacade inMemoryMeetingGroupsFacade(EventPublisher eventPublisher) {
        return meetingGroupsFacade(
                new ProposalRepository.InMemory(new LinkedList<>(), Proposal::getId),
                new MeetingGroupRepository.InMemory(new LinkedList<>(), MeetingGroup::getId),
                new GroupMembershipRepository.InMemory(new LinkedList<>(), GroupMembership::getMemberId),
                new ActiveUserSubscriptions(new LinkedHashSet<>()),
                new AdministratorsRepository.InMemory(new LinkedList<>(), Administrator::getId),
                eventPublisher);
    }
}