package meetings;

import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import io.vavr.control.Either;
import meetings.dto.GroupMeetingId;
import meetings.dto.MeetingDraft;
import meetings.dto.ScheduleMeetingFailure;

public interface MeetingsFacade {

    void subscriptionRenewed(UserId userId);

    void subscriptionExpired(UserId userId);

    void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId);

    void newMemberJoinedGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId);

    Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(GroupOrganizerId groupOrganizerId, MeetingDraft meetingDraft);
}