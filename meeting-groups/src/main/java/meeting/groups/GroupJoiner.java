package meeting.groups;

import commons.active.subscribers.ActiveSubscribersFinder;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import commons.event.publisher.EventPublisher;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import meeting.groups.dto.JoinGroupFailure;

import static io.vavr.control.Option.of;
import static meeting.groups.dto.JoinGroupFailure.MEETING_GROUP_DOES_NOT_EXIST;
import static meeting.groups.dto.JoinGroupFailure.USER_SUBSCRIPTION_IS_NOT_ACTIVE;

@AllArgsConstructor
class GroupJoiner {
    private final ActiveSubscribersFinder activeSubscribersFinder;
    private final MeetingGroupRepository meetingGroupRepository;
    private final EventPublisher eventPublisher;

    Option<JoinGroupFailure> joinGroup(UserId userId, MeetingGroupId meetingGroupId) {
        if (!activeSubscribersFinder.contains(userId))
            return of(USER_SUBSCRIPTION_IS_NOT_ACTIVE);
        return meetingGroupRepository
                .findById(meetingGroupId)
                .toEither(MEETING_GROUP_DOES_NOT_EXIST)
                .flatMap(meetingGroup -> meetingGroup.join(userId))
                .peek(groupMemberId -> eventPublisher.newMemberJoinedMeetingGroup(groupMemberId, meetingGroupId))
                .swap().toOption();
    }
}