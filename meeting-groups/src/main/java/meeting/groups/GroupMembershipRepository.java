package meeting.groups;

import commons.repository.InMemoryRepository;
import commons.repository.Repository;
import io.vavr.control.Option;

import java.util.List;
import java.util.function.Function;

interface GroupMembershipRepository extends Repository<GroupMembership, String> {

    Option<GroupMembership> findByMemberIdAndGroupId(String memberId, String groupId);

    class InMemory extends InMemoryRepository<GroupMembership, String> implements GroupMembershipRepository {

        public InMemory(List<GroupMembership> entities, Function<GroupMembership, String> idGetter) {
            super(entities, idGetter);
        }

        @Override
        public Option<GroupMembership> findByMemberIdAndGroupId(String memberId, String groupId) {
            return entities
                    .stream()
                    .filter(groupMembership -> groupMembership.getMemberId().equals(memberId))
                    .filter(groupMembership -> groupMembership.getMeetingGroupId().equals(groupId))
                    .findAny()
                    .map(Option::of)
                    .orElse(Option.none());
        }
    }
}