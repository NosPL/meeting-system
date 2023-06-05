package meeting.groups.commons;

import commons.dto.MeetingGroupId;
import commons.dto.OrganizerId;
import commons.dto.UserId;
import commons.event.publisher.EventPublisher;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class EventPublisherMock implements EventPublisher {
    private final LinkedList<Tuple2<UserId, MeetingGroupId>> groupCreatedInvocations = new LinkedList<>();
    private final LinkedList<Tuple2<UserId, MeetingGroupId>> memberJoinedGroupInvocations = new LinkedList<>();

    @Override
    public void newMeetingGroupCreated(UserId userId, MeetingGroupId meetingGroupId) {
        log.info("'new meeting group created' event was emitted, meeting group id {}", meetingGroupId.getId());
        groupCreatedInvocations.addLast(Tuple.of(userId, meetingGroupId));
    }

    @Override
    public void newMemberJoinedMeetingGroup(UserId userId, MeetingGroupId meetingGroupId) {
        log.info("'new member joined group' event was emitted, user id {}, meeting group id {}", userId.getId(), meetingGroupId.getId());
        memberJoinedGroupInvocations.addLast(Tuple.of(userId, meetingGroupId));
    }

    public boolean groupCreatedEventInvoked(List<Tuple2<UserId, MeetingGroupId>> invocations) {
        return groupCreatedInvocations.equals(invocations);
    }

    public boolean newMemberJoinedGroupEventInvoked(List<Tuple2<UserId, MeetingGroupId>> invocations) {
        return memberJoinedGroupInvocations.equals(invocations);
    }
}