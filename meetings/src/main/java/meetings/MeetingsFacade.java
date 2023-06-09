package meetings;

import commons.dto.*;
import io.vavr.control.Either;
import io.vavr.control.Option;
import meetings.dto.*;

public interface MeetingsFacade {

    Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(GroupOrganizerId groupOrganizerId, MeetingDraft meetingDraft);

    Option<CancelMeetingFailure> cancelMeeting(GroupOrganizerId groupOrganizerId, GroupMeetingId groupMeetingId);

    Option<SignUpForMeetingFailure> signUpForMeeting(GroupMemberId groupMemberId, GroupMeetingId groupMeetingId);

    Option<SignOutFailure> signOutFromMeeting(AttendeeId attendeeId, GroupMeetingId groupMeetingId);

    void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId);

    void meetingGroupWasRemoved(MeetingGroupId meetingGroupId);

    void newMemberJoinedGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId);

    void memberLeftTheMeetingGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId);
}