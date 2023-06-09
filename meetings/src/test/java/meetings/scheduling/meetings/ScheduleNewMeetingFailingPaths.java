package meetings.scheduling.meetings;

import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Option;
import meetings.commons.TestSetup;
import meetings.dto.GroupMeetingHostId;
import meetings.dto.GroupMeetingName;
import meetings.dto.MeetingDraft;
import meetings.dto.WaitList;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static io.vavr.control.Either.left;
import static meetings.dto.ScheduleMeetingFailure.*;
import static meetings.dto.WaitList.WAIT_LIST_AVAILABLE;
import static org.junit.Assert.assertEquals;

public class ScheduleNewMeetingFailingPaths extends TestSetup {
    private final GroupOrganizerId groupOrganizer = new GroupOrganizerId("subscribed-group-organizer");
    private final GroupOrganizerId notGroupOrganizer = new GroupOrganizerId("subscribed-not-group-organizer");
    private final MeetingGroupId existingMeetingGroup = new MeetingGroupId("existing-meeting-group");
    private final MeetingGroupId notExistingMeetingGroup = new MeetingGroupId("not-existing-meeting-group");
    private final GroupMeetingHostId hostThatIsGroupMember = new GroupMeetingHostId("subscribed-host_that-is-group-member");
    private final GroupMeetingHostId hostThatIsNotGroupMember = new GroupMeetingHostId("subscribed-host_that-is-not-group-member");

    @Before
    public void init() {
        subscriptionRenewed(groupOrganizer);
        subscriptionRenewed(notGroupOrganizer);
        subscriptionRenewed(hostThatIsGroupMember);
        subscriptionRenewed(hostThatIsNotGroupMember);
        meetingsFacade.newMeetingGroupCreated(groupOrganizer, existingMeetingGroup);
        newMemberJoinedGroup(hostThatIsGroupMember, existingMeetingGroup);
        assert meetingsFacade.scheduleNewMeeting(groupOrganizer, correctMeetingDraft(notUniqueName())).isRight();
    }

    @Test
    public void unsubscribedOrganizerShouldFailToScheduleNewMeeting() {
//        given that group organizer is not subscribed
        subscriptionExpired(groupOrganizer);
//        when he tries to schedule new meeting
        var result = meetingsFacade.scheduleNewMeeting(groupOrganizer, correctMeetingDraft());
//        then failure
        assertEquals(left(GROUP_ORGANIZER_IS_NOT_SUBSCRIBED), result);
    }

    @Test
    public void schedulingMeetingWithDateThatIsLessThan3DaysInAdvanceShouldFail() {
//        when organizer tries to schedule meeting with date that is less than 3 days in advance
        var meetingDate = calendar.getCurrentDate().plusDays(2);
        var result = meetingsFacade.scheduleNewMeeting(groupOrganizer, correctMeetingDraft(meetingDate));
//        then failure
        assertEquals(left(MEETING_DATE_IS_NOT_3_DAYS_IN_ADVANCE), result);
    }

    @Test
    public void schedulingMeetingWithHostThatIsNotGroupMemberShouldFail() {
//        when organizer tries to schedule meeting with host that is not the group member
        var result = meetingsFacade.scheduleNewMeeting(groupOrganizer, correctMeetingDraft(hostThatIsNotGroupMember));
//        then failure
        assertEquals(left(PROPOSED_MEETING_HOST_IS_NOT_MEETING_GROUP_MEMBER), result);
    }

    @Test
    public void schedulingMeetingWithUnsubscribedHostShouldFail() {
//        given that host is unsubscribed
        subscriptionExpired(hostThatIsGroupMember);
//        when organizer tries to schedule meeting with this host
        var result = meetingsFacade.scheduleNewMeeting(groupOrganizer, correctMeetingDraft(hostThatIsGroupMember));
//        then failure
        assertEquals(left(PROPOSED_MEETING_HOST_IS_NOT_SUBSCRIBED), result);
    }

    @Test
    public void schedulingMeetingForNotExistingGroupShouldFail() {
//        when organizer tries  to schedule meeting in not existing group
        var result = meetingsFacade.scheduleNewMeeting(groupOrganizer, correctMeetingDraft(notExistingMeetingGroup));
//        then failure
        assertEquals(left(MEETING_GROUP_DOES_NOT_EXIST), result);
    }

    @Test
    public void schedulingMeetingByUserThatIsNotGroupOrganizerShouldFail() {
//        when meeting gets proposed by subscribed user that is not the group organizer
        var result = meetingsFacade.scheduleNewMeeting(notGroupOrganizer, correctMeetingDraft());
//        then failure
        assertEquals(left(USER_IS_NOT_GROUP_ORGANIZER), result);
    }

    @Test
    public void schedulingMeetingWithBlankNameShouldFail() {
//        when organizer tries to schedule meeting with blank name
        var result = meetingsFacade.scheduleNewMeeting(groupOrganizer, correctMeetingDraft(blankName()));
//        then failure
        assertEquals(left(MEETING_NAME_IS_BLANK), result);
    }

    @Test
    public void schedulingMeetingWithNameThatIsNotUniqueInParticularGroupShouldFail() {
//        when organizer tries to schedule meeting with not unique name
        var result = meetingsFacade.scheduleNewMeeting(groupOrganizer, correctMeetingDraft(notUniqueName()));
//        then failure
        assertEquals(left(MEETING_NAME_IS_NOT_UNIQUE), result);
    }

    private MeetingDraft correctMeetingDraft(MeetingGroupId meetingGroupId) {
        return new MeetingDraft(meetingGroupId, date3DaysFromNow(), hostThatIsGroupMember, uniqueNotBlankMeetingName(), Option.none(), WAIT_LIST_AVAILABLE);
    }

    private MeetingDraft correctMeetingDraft(LocalDate meetingDate) {
        return new MeetingDraft(existingMeetingGroup, meetingDate, hostThatIsGroupMember, uniqueNotBlankMeetingName(), Option.none(), WAIT_LIST_AVAILABLE);
    }

    private MeetingDraft correctMeetingDraft(GroupMeetingHostId groupMeetingHostId) {
        return new MeetingDraft(existingMeetingGroup, date3DaysFromNow(), groupMeetingHostId, uniqueNotBlankMeetingName(), Option.none(), WAIT_LIST_AVAILABLE);
    }

    private MeetingDraft correctMeetingDraft(GroupMeetingName groupMeetingName) {
        return new MeetingDraft(existingMeetingGroup, date3DaysFromNow(), hostThatIsGroupMember, groupMeetingName, Option.none(), WAIT_LIST_AVAILABLE);
    }

    private MeetingDraft correctMeetingDraft() {
        return new MeetingDraft(
                existingMeetingGroup,
                date3DaysFromNow(),
                hostThatIsGroupMember,
                uniqueNotBlankMeetingName(),
                Option.none(),
                WAIT_LIST_AVAILABLE);
    }

    private LocalDate date3DaysFromNow() {
        return calendar.getCurrentDate().plusDays(3);
    }

    private GroupMeetingName blankName() {
        return new GroupMeetingName("    ");
    }

    private GroupMeetingName notUniqueName() {
        return new GroupMeetingName("not-unique-name");
    }
}