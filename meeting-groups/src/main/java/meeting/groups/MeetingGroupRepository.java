package meeting.groups;

import commons.dto.UserId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

interface MeetingGroupRepository extends Repository<MeetingGroup, String> {
    boolean existsByGroupName(String groupName);

    Collection<MeetingGroup> findByOrganizerId(UserId userId);

    class InMemory extends InMemoryRepository<MeetingGroup, String> implements MeetingGroupRepository {

        InMemory(List<MeetingGroup> entities, Function<MeetingGroup, String> idGetter) {
            super(entities, idGetter);
        }

        @Override
        public boolean existsByGroupName(String groupName) {
            return entities
                    .stream()
                    .anyMatch(meetingGroup -> meetingGroup.getName().equals(groupName));
        }

        @Override
        public Collection<MeetingGroup> findByOrganizerId(UserId userId) {
            return entities
                    .stream()
                    .filter(meetingGroup -> meetingGroup.getOrganizerId().equals(userId.getId()))
                    .toList();
        }
    }
}