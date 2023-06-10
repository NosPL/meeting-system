package meeting.comments;

import commons.dto.GroupMemberId;
import commons.dto.MeetingGroupId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;

import java.util.List;
import java.util.function.Function;

interface GroupMembershipRepository extends Repository<GroupMembership, GroupMembershipId> {

    boolean existsByGroupMemberIdAndMeetingGroupId(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId);

    void removeByGroupMemberIdAndMeetingGroupId(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId);

    void removeByMeetingGroupId(MeetingGroupId meetingGroupId);

    class InMemory extends InMemoryRepository<GroupMembership, GroupMembershipId> implements GroupMembershipRepository {

        public InMemory(List<GroupMembership> entities, Function<GroupMembership, GroupMembershipId> idGetter) {
            super(entities, idGetter);
        }

        @Override
        public boolean existsByGroupMemberIdAndMeetingGroupId(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
            return entities
                    .stream()
                    .filter(groupMembership -> groupMembership.getGroupMemberId().equals(groupMemberId))
                    .anyMatch(groupMembership -> groupMembership.getMeetingGroupId().equals(meetingGroupId));
        }

        @Override
        public void removeByGroupMemberIdAndMeetingGroupId(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
            entities
                    .stream()
                    .filter(groupMembership -> groupMembership.getGroupMemberId().equals(groupMemberId))
                    .filter(groupMembership -> groupMembership.getMeetingGroupId().equals(meetingGroupId))
                    .findFirst()
                    .ifPresent(groupMembership -> removeById(groupMembership.getGroupMembershipId()));
        }

        @Override
        public void removeByMeetingGroupId(MeetingGroupId meetingGroupId) {
            entities = entities
                    .stream()
                    .filter(groupMembership -> groupMembership.getMeetingGroupId().equals(meetingGroupId))
                    .toList();
        }
    }
}