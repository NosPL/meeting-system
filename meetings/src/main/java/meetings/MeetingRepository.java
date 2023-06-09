package meetings;

import commons.dto.MeetingGroupId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;
import commons.dto.GroupMeetingId;
import meetings.dto.GroupMeetingName;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

interface MeetingRepository extends Repository<Meeting, GroupMeetingId> {

    boolean existsByMeetingName(GroupMeetingName groupMeetingName);

    List<Meeting> findByMeetingGroupId(MeetingGroupId meetingGroupId);

    class InMemory extends InMemoryRepository<Meeting, GroupMeetingId> implements MeetingRepository {

        InMemory(List<Meeting> entities, Function<Meeting, GroupMeetingId> idGetter) {
            super(entities, idGetter);
        }

        @Override
        public boolean existsByMeetingName(GroupMeetingName groupMeetingName) {
            return entities
                    .stream()
                    .anyMatch(meeting -> meeting.getGroupMeetingName().equals(groupMeetingName));
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