package meeting.comments;

import commons.dto.GroupMeetingId;
import commons.dto.MeetingGroupId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;

import java.util.List;
import java.util.function.Function;

interface MeetingRepository extends Repository<Meeting, GroupMeetingId> {

    List<Meeting> findByMeetingGroupId(MeetingGroupId meetingGroupId);

    class InMemory extends InMemoryRepository<Meeting, GroupMeetingId> implements MeetingRepository {

        public InMemory(List<Meeting> entities, Function<Meeting, GroupMeetingId> idGetter) {
            super(entities, idGetter);
        }

        @Override
        public List<Meeting> findByMeetingGroupId(MeetingGroupId meetingGroupId) {
            return entities
                    .stream()
                    .filter(meeting -> meeting.getMeetingGroupId().equals(meetingGroupId))
                    .toList();
        }
    }
}