package meetings;

import commons.dto.MeetingGroupId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;

import java.util.List;
import java.util.function.Function;

interface MeetingGroupRepository extends Repository<MeetingGroup, MeetingGroupId> {

    class InMemory extends InMemoryRepository<MeetingGroup, MeetingGroupId> implements MeetingGroupRepository {

        InMemory(List<MeetingGroup> entities, Function<MeetingGroup, MeetingGroupId> idGetter) {
            super(entities, idGetter);
        }
    }
}