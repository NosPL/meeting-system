package meetings;

import commons.dto.*;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import meetings.dto.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.vavr.control.Either.left;
import static io.vavr.control.Option.of;
import static lombok.AccessLevel.PRIVATE;
import static meetings.dto.SignOutFailure.USER_WAS_NOT_SIGN_IN;
import static meetings.dto.SignUpForMeetingFailure.*;

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
    private WaitingList waitingList;

    Either<SignOutFailure, Option<AttendeeSignedUpFromWaitList>> signOut(AttendeeId attendeeId) {
        if (!attendees.contains(attendeeId))
            return left(USER_WAS_NOT_SIGN_IN);
        var groupMemberId = new GroupMemberId(attendeeId.getId());
        return Either.right(remove(groupMemberId));
    }

    Option<AttendeeSignedUpFromWaitList> remove(GroupMemberId groupMemberId) {
        waitingList.remove(groupMemberId);
        attendees.remove(new AttendeeId(groupMemberId.getId()));
        if (attendeesLimitIsReached())
            return Option.none();
        return waitingList
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
                new WaitingList());
    }

    @Value
    static class AttendeeSignedUpFromWaitList {
        AttendeeId attendeeId;
    }
}