package meeting.groups;

import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

interface MeetingGroupRepository extends Repository<MeetingGroup, MeetingGroupId> {
    boolean existsByGroupName(String groupName);

    Collection<MeetingGroup> findByOrganizerId(GroupOrganizerId groupOrganizerId);

    class InMemory extends InMemoryRepository<MeetingGroup, MeetingGroupId> implements MeetingGroupRepository {

        InMemory(List<MeetingGroup> entities, Function<MeetingGroup, MeetingGroupId> idGetter) {
            super(entities, idGetter);
        }

        @Override
        public boolean existsByGroupName(String groupName) {
            return entities
                    .stream()
                    .anyMatch(meetingGroup -> meetingGroup.getName().equals(groupName));
        }

        @Override
        public Collection<MeetingGroup> findByOrganizerId(GroupOrganizerId groupOrganizerId) {
            return entities
                    .stream()
                    .filter(meetingGroup -> meetingGroup.getGroupOrganizerId().equals(groupOrganizerId))
                    .toList();
        }
    }
}