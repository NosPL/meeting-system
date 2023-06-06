package meeting.groups;

import commons.dto.GroupOrganizerId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;
import meeting.groups.dto.ProposalId;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

interface ProposalRepository extends Repository<Proposal, ProposalId> {
    boolean existsByGroupName(String groupName);

    Collection<Proposal> findByOrganizerId(GroupOrganizerId groupOrganizerId);

    class InMemory extends InMemoryRepository<Proposal, ProposalId> implements ProposalRepository {

        InMemory(List<Proposal> entities, Function<Proposal, ProposalId> idGetter) {
            super(entities, idGetter);
        }

        @Override
        public boolean existsByGroupName(String groupName) {
            return entities
                    .stream()
                    .anyMatch(proposal -> proposal.getGroupName().equals(groupName));
        }

        @Override
        public Collection<Proposal> findByOrganizerId(GroupOrganizerId groupOrganizerId) {
            return entities
                    .stream()
                    .filter(meetingGroup -> meetingGroup.getGroupOrganizerId().equals(groupOrganizerId))
                    .toList();
        }
    }
}