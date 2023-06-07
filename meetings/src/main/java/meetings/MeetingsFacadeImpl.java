package meetings;

import commons.active.subscribers.ActiveSubscribersFinder;
import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import meetings.dto.GroupMeetingId;
import meetings.dto.MeetingDraft;
import meetings.dto.ScheduleMeetingFailure;

import java.util.HashSet;

@AllArgsConstructor
class MeetingsFacadeImpl implements MeetingsFacade {
    private final MeetingGroupRepository meetingGroupRepository;
    private final MeetingsScheduler meetingsScheduler;

    @Override
    public void newMeetingGroupCreated(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId) {
        meetingGroupRepository.save(new MeetingGroup(meetingGroupId, groupOrganizerId, new HashSet<>()));
    }

    @Override
    public void newMemberJoinedGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId) {
        meetingGroupRepository
                .findById(meetingGroupId)
                .peek(meetingGroup -> meetingGroup.add(groupMemberId));
    }

    @Override
    public Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(GroupOrganizerId groupOrganizerId, MeetingDraft meetingDraft) {
        return meetingsScheduler.scheduleNewMeeting(groupOrganizerId, meetingDraft);
    }
}