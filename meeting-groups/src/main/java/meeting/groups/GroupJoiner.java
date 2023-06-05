package meeting.groups;

import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import meeting.groups.dto.JoinGroupFailure;

import static meeting.groups.dto.JoinGroupFailure.*;
import static meeting.groups.dto.JoinGroupFailure.USER_IS_GROUP_ORGANIZER;

@AllArgsConstructor
class GroupJoiner {
    private final ActiveUserSubscriptions activeUserSubscriptions;
    private final GroupMembershipRepository groupMembershipRepository;
    private final MeetingGroupRepository meetingGroupRepository;

    Option<JoinGroupFailure> joinGroup(UserId newMemberId, MeetingGroupId meetingGroupId) {
        if (!activeUserSubscriptions.contains(newMemberId))
            return Option.of(USER_SUBSCRIPTION_IS_NOT_ACTIVE);
        if (groupMembershipRepository.findByMemberIdAndGroupId(newMemberId.getId(), meetingGroupId.getId()).isDefined())
            return Option.of(USER_ALREADY_JOINED_GROUP);
        if (!meetingGroupExists(meetingGroupId))
            return Option.of(MEETING_GROUP_DOES_NOT_EXIST);
        if (userIsGroupOrganizer(newMemberId, meetingGroupId))
            return Option.of(USER_IS_GROUP_ORGANIZER);
        groupMembershipRepository.save(new GroupMembership(newMemberId.getId(), meetingGroupId.getId()));
        return Option.none();
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