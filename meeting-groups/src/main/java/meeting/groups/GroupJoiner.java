package meeting.groups;

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
    private final ActiveUserSubscriptions activeUserSubscriptions;
    private final GroupMembershipRepository groupMembershipRepository;
    private final MeetingGroupRepository meetingGroupRepository;
    private final EventPublisher eventPublisher;

    Option<JoinGroupFailure> joinGroup(UserId newMemberId, MeetingGroupId meetingGroupId) {
        if (!activeUserSubscriptions.contains(newMemberId))
            return of(USER_SUBSCRIPTION_IS_NOT_ACTIVE);
        if (userIsMemberOfMeetingGroup(newMemberId, meetingGroupId))
            return of(USER_ALREADY_JOINED_GROUP);
        if (!meetingGroupExists(meetingGroupId))
            return of(MEETING_GROUP_DOES_NOT_EXIST);
        if (userIsGroupOrganizer(newMemberId, meetingGroupId))
            return of(USER_IS_GROUP_ORGANIZER);
        groupMembershipRepository.save(new GroupMembership(newMemberId.getId(), meetingGroupId.getId()));
        eventPublisher.newMemberJoinedMeetingGroup(newMemberId, meetingGroupId);
        return none();
    }

    private boolean userIsMemberOfMeetingGroup(UserId newMemberId, MeetingGroupId meetingGroupId) {
        return groupMembershipRepository.findByMemberIdAndGroupId(newMemberId.getId(), meetingGroupId.getId()).isDefined();
    }

    private boolean userIsGroupOrganizer(UserId userId, MeetingGroupId meetingGroupId) {
        return meetingGroupRepository
                .findById(meetingGroupId.getId())
                .map(meetingGroup -> meetingGroup.isOrganizer(userId))
                .getOrElse(false);
    }

    private boolean meetingGroupExists(MeetingGroupId meetingGroupId) {
        return meetingGroupRepository.existsById(meetingGroupId.getId());
    }
}