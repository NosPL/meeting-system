package meeting.groups;

import commons.active.subscribers.ActiveSubscribersFinder;
import commons.dto.GroupMemberId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import commons.event.publisher.EventPublisher;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import meeting.groups.dto.JoinGroupFailure;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.of;
import static meeting.groups.dto.JoinGroupFailure.*;
import static meeting.groups.dto.JoinGroupFailure.USER_IS_GROUP_ORGANIZER;

@AllArgsConstructor
class GroupJoiner {
    private final ActiveSubscribersFinder activeSubscribersFinder;
    private final GroupMembershipRepository groupMembershipRepository;
    private final MeetingGroupRepository meetingGroupRepository;
    private final EventPublisher eventPublisher;

    Option<JoinGroupFailure> joinGroup(UserId userId, MeetingGroupId meetingGroupId) {
        if (!activeSubscribersFinder.contains(userId))
            return of(USER_SUBSCRIPTION_IS_NOT_ACTIVE);
        if (userIsMemberOfMeetingGroup(userId, meetingGroupId))
            return of(USER_ALREADY_JOINED_GROUP);
        if (!meetingGroupExists(meetingGroupId))
            return of(MEETING_GROUP_DOES_NOT_EXIST);
        if (userIsGroupOrganizer(userId, meetingGroupId))
            return of(USER_IS_GROUP_ORGANIZER);
        var groupMemberId = new GroupMemberId(userId.getId());
        var groupMembership = GroupMembership.create(groupMemberId, meetingGroupId);
        groupMembershipRepository.save(groupMembership);
        eventPublisher.newMemberJoinedMeetingGroup(groupMemberId, meetingGroupId);
        return none();
    }



    private boolean userIsMemberOfMeetingGroup(UserId userId, MeetingGroupId meetingGroupId) {
        GroupMemberId groupMemberId = new GroupMemberId(userId.getId());
        return groupMembershipRepository
                .findByGroupMemberIdAndMeetingGroupId(groupMemberId, meetingGroupId)
                .isDefined();
    }

    private boolean userIsGroupOrganizer(UserId userId, MeetingGroupId meetingGroupId) {
        return meetingGroupRepository
                .findById(meetingGroupId)
                .map(meetingGroup -> meetingGroup.isOrganizer(userId))
                .getOrElse(false);
    }

    private boolean meetingGroupExists(MeetingGroupId meetingGroupId) {
        return meetingGroupRepository.existsById(meetingGroupId);
    }
}