package meeting.groups;

import commons.dto.GroupMemberId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import io.vavr.control.Option;
import meeting.groups.commons.TestSetup;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static io.vavr.Tuple.of;
import static meeting.groups.dto.JoinGroupFailure.*;
import static org.junit.Assert.assertEquals;

public class JoiningMeetingGroup extends TestSetup {

    @Test
    public void joiningGroupThatDoesNotExistShouldFail() {
//        when subscribed user tries to join non-existent random meting group
        var result = meetingGroupsFacade.joinGroup(subscribedUser, randomMeetingGroupId());
//        then he fails because group does not exist
        assertEquals(Option.of(MEETING_GROUP_DOES_NOT_EXIST), result);
    }

    @Test
    public void joiningGroupWithoutActiveSubscriptionShouldFail() {
//        given that meeting group got created
        var meetingGroupId = createMeetingGroup();
//        when not subscribed user tries to join created meeting group
        var result = meetingGroupsFacade.joinGroup(notSubscribedUser, meetingGroupId);
//        then he fails because user does not have active subscription
        assertEquals(Option.of(USER_SUBSCRIPTION_IS_NOT_ACTIVE), result);
    }

    @Test
    public void joiningSameGroupTwiceShouldFail() {
//        given that meeting group got created
        var meetingGroupId = createMeetingGroup();
//        and subscribed user already joined group
        assert meetingGroupsFacade.joinGroup(subscribedUser, meetingGroupId).isEmpty();
//        when he tries to join this group again
        var result = meetingGroupsFacade.joinGroup(subscribedUser, meetingGroupId);
        assertEquals(Option.of(USER_ALREADY_JOINED_GROUP), result);
    }

    @Test
    public void userWithActiveSubscriptionShouldBeAbleToJoinMeetingGroup() {
//        given that meeting group was created
        var meetingGroupId = createMeetingGroup();
//        when subscribed user tries to join the meeting group
        var result = meetingGroupsFacade.joinGroup(subscribedUser, meetingGroupId);
//        then he succeeds
        assertEquals(Option.none(), result);
//        and 'new member joined meeting group' event was emitted
        newMemberJoinedGroupEventInvoked(subscribedUser, meetingGroupId);
    }

    private void newMemberJoinedGroupEventInvoked(UserId userId, MeetingGroupId meetingGroupId) {
        var groupMemberId = new GroupMemberId(userId.getId());
        var expectedInvocations = List.of(of(groupMemberId, meetingGroupId));
        assert eventPublisherMock.newMemberJoinedGroupEventInvoked(expectedInvocations);
    }
}