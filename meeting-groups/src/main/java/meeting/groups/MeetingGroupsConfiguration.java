package meeting.groups;

import commons.active.subscribers.ActiveSubscribersFinder;
import commons.event.publisher.EventPublisher;

import java.util.LinkedList;

public class MeetingGroupsConfiguration {
    private final ProposalRepository.InMemory proposalRepositoryInMemory = new ProposalRepository.InMemory(new LinkedList<>(), Proposal::getProposalId);
    private final MeetingGroupRepository.InMemory meetingGroupRepositoryInMemory = new MeetingGroupRepository.InMemory(new LinkedList<>(), MeetingGroup::getMeetingGroupId);
    private final AdministratorRepository.InMemory administratorRepositoryInMemory = new AdministratorRepository.InMemory(new LinkedList<>(), Administrator::getAdministratorId);

    public MeetingGroupsFacade meetingGroupsFacade(
            ProposalRepository proposalRepository,
            MeetingGroupRepository meetingGroupRepository,
            ActiveSubscribersFinder activeSubscribersFinder,
            AdministratorRepository administratorRepository,
            EventPublisher eventPublisher) {
        var proposalSubmitter = new ProposalSubmitter(proposalRepository, meetingGroupRepository, activeSubscribersFinder);
        var proposalAccepter = new ProposalAccepter(proposalRepository, meetingGroupRepository, administratorRepository, eventPublisher);
        var proposalRejecter = new ProposalRejecter(proposalRepository, administratorRepository);
        var groupJoiner = new GroupJoiner(activeSubscribersFinder, meetingGroupRepository, eventPublisher);
        var meetingGroups = new MeetingGroupsFacadeImpl(
                administratorRepository,
                proposalRepository,
                meetingGroupRepository,
                proposalSubmitter,
                proposalAccepter,
                proposalRejecter,
                groupJoiner);
        return new LogsDecorator(meetingGroups);
    }

    public MeetingGroupsFacade inMemoryMeetingGroupsFacade(ActiveSubscribersFinder activeSubscribersFinder, EventPublisher eventPublisher) {
        return meetingGroupsFacade(
                proposalRepositoryInMemory,
                meetingGroupRepositoryInMemory,
                activeSubscribersFinder,
                administratorRepositoryInMemory,
                eventPublisher);
    }
}