package meeting.groups.commons;

import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.event.publisher.EventPublisher;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class EventPublisherMock implements EventPublisher {
    private final LinkedList<Tuple2<GroupOrganizerId, MeetingGroupId>> groupCreatedInvocations = new LinkedList<>();
    private final LinkedList<Tuple2<GroupMemberId, MeetingGroupId>> memberJoinedGroupInvocations = new LinkedList<>();
    private final LinkedList<Tuple2<GroupMemberId, MeetingGroupId>> memberLeftGroupInvocations = new LinkedList<>();
    private final LinkedList<MeetingGroupId> meetingGroupWasRemovedInvocations = new LinkedList<>();

    @Override
    public void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        log.info("'new meeting group created' event was emitted, meeting group id {}", meetingGroupId.getId());
        groupCreatedInvocations.addLast(Tuple.of(groupOrganizerId, meetingGroupId));
    }

    @Override
    public void newMemberJoinedMeetingGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        log.info("'new member joined group' event was emitted, group member id {}, meeting group id {}", groupMemberId.getId(), meetingGroupId.getId());
        memberJoinedGroupInvocations.addLast(Tuple.of(groupMemberId, meetingGroupId));
    }

    @Override
    public void meetingGroupWasRemoved(MeetingGroupId meetingGroupId) {
        meetingGroupWasRemovedInvocations.add(meetingGroupId);
    }

    @Override
    public void groupMemberLeftGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        memberLeftGroupInvocations.add(Tuple.of(groupMemberId, meetingGroupId));
    }

    public boolean groupCreatedEventInvoked(List<Tuple2<GroupOrganizerId, MeetingGroupId>> invocations) {
        return groupCreatedInvocations.equals(invocations);
    }

    public boolean newMemberJoinedGroupEventInvoked(List<Tuple2<GroupMemberId, MeetingGroupId>> invocations) {
        return memberJoinedGroupInvocations.equals(invocations);
    }

    public boolean memberLeftGroupEventInvoked(List<Tuple2<GroupMemberId, MeetingGroupId>> invocations) {
        return memberJoinedGroupInvocations.equals(invocations);
    }

    public boolean meetingGroupWasRemovedInvoked(MeetingGroupId...meetingGroupIds) {
        return meetingGroupWasRemovedInvocations.equals(List.of(meetingGroupIds));
    }
}