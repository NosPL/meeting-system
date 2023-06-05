package meeting.groups;

import commons.dto.UserId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

interface ProposalRepository extends Repository<Proposal, String> {
    boolean existsByGroupName(String groupName);

    Collection<Proposal> findByUserId(UserId userId);

    class InMemory extends InMemoryRepository<Proposal, String> implements ProposalRepository {

        InMemory(List<Proposal> entities, Function<Proposal, String> idGetter) {
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
}