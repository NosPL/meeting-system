package meeting.groups;

import commons.dto.*;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import meeting.groups.Proposal.ProposalAccepted;
import meeting.groups.dto.JoinGroupFailure;
import meeting.groups.dto.LeaveGroupFailure;
import meeting.groups.query.dto.MeetingGroupDetails;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static lombok.AccessLevel.PRIVATE;
import static meeting.groups.dto.JoinGroupFailure.USER_ALREADY_JOINED_GROUP;
import static meeting.groups.dto.JoinGroupFailure.USER_IS_GROUP_ORGANIZER;
import static meeting.groups.dto.LeaveGroupFailure.USER_IS_NOT_GROUP_MEMBER;

@AllArgsConstructor(access = PRIVATE)
@Getter
class MeetingGroup {
    private MeetingGroupId meetingGroupId;
    private String name;
    private GroupOrganizerId groupOrganizerId;
    private Set<GroupMemberId> groupMemberIds;
    private Set<GroupMeetingId> groupMeetingIds;

    Either<JoinGroupFailure, GroupMemberId> join(UserId userId) {
        if (isGroupOrganizer(userId))
            return left(USER_IS_GROUP_ORGANIZER);
        var groupMemberId = new GroupMemberId(userId.getId());
        if (groupMemberIds.add(groupMemberId))
            return right(groupMemberId);
        return left(USER_ALREADY_JOINED_GROUP);
    }

    private boolean isGroupOrganizer(UserId userId) {
        return groupOrganizerId.equals(new GroupOrganizerId(userId.getId()));
    }

    Option<LeaveGroupFailure> leave(GroupMemberId groupMemberId) {
        if (groupMemberIds.remove(groupMemberId))
            return Option.none();
        return Option.of(USER_IS_NOT_GROUP_MEMBER);
    }

    void newMeetingWasScheduled(GroupMeetingId groupMeetingId) {
        groupMeetingIds.add(groupMeetingId);
    }

    void meetingHeld(GroupMeetingId groupMeetingId) {
        groupMeetingIds.remove(groupMeetingId);
    }

    void meetingCancelled(GroupMeetingId groupMeetingId) {
        groupMeetingIds.remove(groupMeetingId);
    }

    boolean hasScheduledMeetings() {
        return !groupMeetingIds.isEmpty();
    }

    MeetingGroupDetails toDto() {
        return new MeetingGroupDetails(meetingGroupId, name, groupOrganizerId, new HashSet<>(groupMemberIds));
    }

    static MeetingGroup createFromProposal(ProposalAccepted proposalAccepted) {
        String id = UUID.randomUUID().toString();
        return new MeetingGroup(new MeetingGroupId(id), proposalAccepted.getGroupName(), proposalAccepted.getGroupOrganizerId(), new HashSet<>(), new HashSet<>());
    }
}