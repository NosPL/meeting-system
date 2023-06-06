package meeting.groups;

import commons.dto.GroupMemberId;
import commons.dto.MeetingGroupId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;
import io.vavr.control.Option;
import commons.dto.GroupMembershipId;

import java.util.List;
import java.util.function.Function;

interface GroupMembershipRepository extends Repository<GroupMembership, GroupMembershipId> {

    Option<GroupMembership> findByGroupMemberIdAndMeetingGroupId(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId);

    List<GroupMembership> findByMeetingGroupId(MeetingGroupId meetingGroupId);

    class InMemory extends InMemoryRepository<GroupMembership, GroupMembershipId> implements GroupMembershipRepository {

        InMemory(List<GroupMembership> entities, Function<GroupMembership, GroupMembershipId> idGetter) {
            super(entities, idGetter);
        }

        @Override
        public Option<GroupMembership> findByGroupMemberIdAndMeetingGroupId(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
            return entities
                    .stream()
                    .filter(groupMembership -> groupMembership.getGroupMemberId().equals(groupMemberId))
                    .filter(groupMembership -> groupMembership.getMeetingGroupId().equals(meetingGroupId))
                    .findAny()
                    .map(Option::of)
                    .orElse(Option.none());
        }

        @Override
        public List<GroupMembership> findByMeetingGroupId(MeetingGroupId meetingGroupId) {
            return entities
                    .stream()
                    .filter(groupMembership -> groupMembership.getMeetingGroupId().equals(meetingGroupId))
                    .toList();
        }
    }
}