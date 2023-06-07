package meetings.signing.up;

import commons.dto.*;
import io.vavr.control.Option;
import meetings.commons.TestSetup;
import meetings.dto.*;

import java.util.UUID;

public class SigningUpSetup extends TestSetup {

    protected GroupMeetingId scheduleMeeting(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        var groupMeetingHostId = new GroupMeetingHostId("group-meeting-host");
        return scheduleMeeting(groupOrganizerId, meetingGroupId, groupMeetingHostId, Option.none());
    }

    protected GroupMeetingId scheduleMeeting(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId, GroupMeetingHostId groupMeetingHostId, Option<AttendeesLimit> attendeesLimit) {
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
        meetingsFacade.newMemberJoinedGroup(asGroupMember(groupMeetingHostId), meetingGroupId);
        subscriptionRenewed(groupMeetingHostId);
        subscriptionRenewed(groupOrganizerId);
        var meetingDraft = meetingDraft(groupMeetingHostId, meetingGroupId, attendeesLimit);
        return meetingsFacade.scheduleNewMeeting(groupOrganizerId, meetingDraft).get();
    }

    protected GroupMeetingHostId asHost(GroupMemberId groupMemberId) {
        return new GroupMeetingHostId(groupMemberId.getId());
    }

    private GroupMemberId asGroupMember(GroupMeetingHostId groupMeetingHostId) {
        return new GroupMemberId(groupMeetingHostId.getId());
    }

    protected MeetingDraft meetingDraft(GroupMeetingHostId groupMeetingHostId, MeetingGroupId meetingGroupId, Option<AttendeesLimit> attendeesLimit) {
        return new MeetingDraft(
                meetingGroupId,
                calendar.getCurrentDate().plusDays(4),
                groupMeetingHostId,
                new GroupMeetingName("random-name"),
                attendeesLimit);
    }

    protected void subscriptionExpired(GroupMemberId groupMemberId) {
        activeSubscribers.remove(new UserId(groupMemberId.getId()));
    }

    protected void subscriptionRenewed(GroupMemberId groupMemberId) {
        activeSubscribers.add(new UserId(groupMemberId.getId()));
    }

    protected GroupMemberId createGroupMemberId() {
        return new GroupMemberId(UUID.randomUUID().toString());
    }

    protected GroupMeetingId randomMeetingId() {
        return new GroupMeetingId(UUID.randomUUID().toString());
    }
}