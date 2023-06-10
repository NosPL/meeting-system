package meeting.comments;

import commons.dto.MeetingGroupId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;

import java.util.List;
import java.util.function.Function;

interface GroupRepository extends Repository<Group, MeetingGroupId> {

    class InMemory extends InMemoryRepository<Group, MeetingGroupId> implements GroupRepository {

        public InMemory(List<Group> entities, Function<Group, MeetingGroupId> idGetter) {
            super(entities, idGetter);
        }
    }
}