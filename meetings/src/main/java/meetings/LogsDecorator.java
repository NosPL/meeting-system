package meetings;

import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import meetings.dto.GroupMeetingId;
import meetings.dto.MeetingDraft;
import meetings.dto.ScheduleMeetingFailure;

@AllArgsConstructor
class LogsDecorator implements MeetingsFacade {
    private final MeetingsFacade meetingsFacade;

    @Override
    public void subscriptionRenewed(UserId userId) {
        meetingsFacade.subscriptionRenewed(userId);
    }

    @Override
    public void subscriptionExpired(UserId userId) {
        meetingsFacade.subscriptionExpired(userId);
    }

    @Override
    public void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
    }

    @Override
    public void newMemberJoinedGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
    }

    @Override
    public Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(GroupOrganizerId groupOrganizerId, MeetingDraft meetingDraft) {
        return meetingsFacade.scheduleNewMeeting(groupOrganizerId, meetingDraft);
    }
}