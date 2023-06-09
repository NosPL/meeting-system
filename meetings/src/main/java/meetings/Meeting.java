package meetings;

import commons.dto.*;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import meetings.dto.*;
import meetings.query.dto.MeetingDetails;
import meetings.query.dto.MeetingDetails.WaitListDetails;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

import static io.vavr.control.Either.left;
import static io.vavr.control.Option.of;
import static lombok.AccessLevel.PRIVATE;
import static meetings.dto.SignOnWaitListFailure.ATTENDEES_LIMIT_IS_NOT_REACHED;
import static meetings.dto.SignOnWaitListFailure.WAIT_LIST_IS_NOT_AVAILABLE;
import static meetings.dto.SignOutFailure.USER_WAS_NOT_SIGN_IN;
import static meetings.dto.SignUpForMeetingFailure.*;
import static meetings.dto.WaitList.WAIT_LIST_AVAILABLE;
import static meetings.dto.WaitList.WAIT_LIST_NOT_AVAILABLE;

@AllArgsConstructor(access = PRIVATE)
@Getter
class Meeting {
    private GroupMeetingId groupMeetingId;
    private MeetingGroupId meetingGroupId;
    private GroupOrganizerId groupOrganizerId;
    private GroupMeetingHostId groupMeetingHostId;
    private LocalDate meetingDate;
    private GroupMeetingName groupMeetingName;
    private Option<Integer> attendeesLimit;
    private Set<AttendeeId> attendees;
    private WaitList waitList;

    Either<SignOutFailure, Option<AttendeeSignedUpFromWaitList>> signOut(AttendeeId attendeeId) {
        if (!attendees.contains(attendeeId))
            return left(USER_WAS_NOT_SIGN_IN);
        var groupMemberId = new GroupMemberId(attendeeId.getId());
        return Either.right(remove(groupMemberId));
    }

    Option<AttendeeSignedUpFromWaitList> remove(GroupMemberId groupMemberId) {
        waitList.remove(groupMemberId);
        attendees.remove(new AttendeeId(groupMemberId.getId()));
        if (attendeesLimitIsReached())
            return Option.none();
        return waitList
                .pop()
                .peek(attendees::add)
                .map(AttendeeSignedUpFromWaitList::new);
    }

    Option<SignUpForMeetingFailure> signUp(GroupMemberId groupMemberId) {
        if (idsAreTheSame(groupMeetingHostId, groupMemberId))
            return of(MEETING_HOST_CANNOT_SIGN_UP_FOR_MEETING);
        if (attendeesLimitIsReached())
            return of(NO_FREE_ATTENDEE_SLOTS);
        var attendeeId = new AttendeeId(groupMemberId.getId());
        attendees.add(attendeeId);
        return Option.none();
    }

    Option<SignOnWaitListFailure> signOnWaitList(GroupMemberId groupMemberId) {
        if (!attendeesLimitIsReached())
            return Option.of(ATTENDEES_LIMIT_IS_NOT_REACHED);
        return waitList.signOn(groupMemberId);
    }

    void signOutFromWaitList(GroupMemberId groupMemberId) {
        waitList.remove(groupMemberId);
    }

    private boolean attendeesLimitIsReached() {
        return attendeesLimit
                .map(limit -> attendees.size() >= limit)
                .getOrElse(false);
    }

    private boolean idsAreTheSame(GroupMeetingHostId groupMeetingHostId, GroupMemberId groupMemberId) {
        return groupMeetingHostId.getId().equals(groupMemberId.getId());
    }

    static Meeting create(GroupOrganizerId groupOrganizerId, MeetingDraft meetingDraft) {
        var groupMeetingId = new GroupMeetingId(UUID.randomUUID().toString());
        return new Meeting(
                groupMeetingId,
                meetingDraft.getMeetingGroupId(),
                groupOrganizerId,
                meetingDraft.getGroupMeetingHostId(),
                meetingDraft.getMeetingDate(),
                meetingDraft.getGroupMeetingName(),
                meetingDraft.getAttendeesLimit().map(AttendeesLimit::getLimit),
                new HashSet<>(),
                WaitList.crateFrom(meetingDraft.getWaitList()));
    }

    MeetingDetails toDto() {
        return new MeetingDetails(
                groupMeetingId,
                meetingGroupId,
                groupMeetingHostId,
                groupMeetingName,
                attendeesLimit.map(AttendeesLimit::new),
                new HashSet<>(attendees),
                waitList.toDto());
    }

    @Value
    static class AttendeeSignedUpFromWaitList {
        AttendeeId attendeeId;
    }

    @AllArgsConstructor(access = PRIVATE)
    private static class WaitList {
        @Getter
        private final meetings.dto.WaitList waitList;
        private final LinkedList<GroupMemberId> groupMembers;

        private Option<AttendeeId> pop() {
            return of(groupMembers.pollFirst())
                    .map(GroupMemberId::getId)
                    .map(AttendeeId::new);
        }

        private void remove(GroupMemberId groupMemberId) {
            groupMembers.remove(groupMemberId);
        }

        private Option<SignOnWaitListFailure> signOn(GroupMemberId groupMemberId) {
            if (waitList == WAIT_LIST_NOT_AVAILABLE)
                return of(WAIT_LIST_IS_NOT_AVAILABLE);
            groupMembers.addLast(groupMemberId);
            return Option.none();
        }

        private WaitListDetails toDto() {
            boolean allowed = waitList == WAIT_LIST_AVAILABLE;
            return new WaitListDetails(allowed, new LinkedList<>(groupMembers));
        }

        private static WaitList crateFrom(meetings.dto.WaitList waitList) {
            return new WaitList(waitList, new LinkedList<>());
        }
    }
}