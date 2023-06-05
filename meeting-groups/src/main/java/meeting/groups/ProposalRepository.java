package meeting.groups;

import commons.dto.UserId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;
import meeting.groups.dto.ProposalDto;
import meeting.groups.dto.ProposalId;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

interface ProposalRepository extends Repository<Proposal, String> {
    boolean existsByGroupName(String groupName);

    Collection<Proposal> findByUserId(UserId userId);

    class InMemory extends InMemoryRepository<Proposal, String> implements ProposalRepository {

        public InMemory(List<Proposal> entities, Function<Proposal, String> idGetter) {
            super(entities, idGetter);
        }

        @Override
        public boolean existsByGroupName(String groupName) {
            return entities
                    .stream()
                    .anyMatch(proposal -> proposal.getGroupName().equals(groupName));
        }

        @Override
        public Collection<Proposal> findByUserId(UserId userId) {
            return entities
                    .stream()
                    .filter(meetingGroup -> meetingGroup.getCreatorId().equals(userId.getId()))
                    .toList();
        }
    }

    static void main(String[] args) {
        InMemory proposalRepository = new InMemory(new LinkedList<>(), Proposal::getId);
        UserId userId = userId();
        proposalRepository.save(Proposal.createFrom(userId, proposal()));
        proposalRepository.save(Proposal.createFrom(userId, proposal()));
        proposalRepository.save(Proposal.createFrom(userId, proposal()));
        System.out.println(proposalRepository.findByUserId(userId).size());
    }

    static UserId userId() {
        return new UserId("user id");
    }

    static ProposalDto proposal() {
        return new ProposalDto(UUID.randomUUID().toString());
    }
}