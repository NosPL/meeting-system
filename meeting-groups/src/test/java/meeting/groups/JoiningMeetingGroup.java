package meeting.groups;

import io.vavr.control.Option;
import meeting.groups.commons.TestSetup;
import org.junit.Test;

import static io.vavr.Tuple.of;
import static meeting.groups.dto.JoinGroupFailure.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class JoiningMeetingGroup extends TestSetup {

    @Test
    public void joiningGroupThatDoesNotExistShouldFail() {
//        when user tries to join not existing meting group
        var result = meetingGroupsFacade.joinGroup(user, randomMeetingGroupId());
//        then he fails
        assertEquals(Option.of(MEETING_GROUP_DOES_NOT_EXIST), result);
    }

    @Test
    public void joiningGroupWithoutActiveSubscriptionShouldFail() {
//        given that meeting group got created
        var meetingGroupId = createMeetingGroup();
//        and user is not subscribed
        activeSubscribers.remove(user);
//        when he tries to join meeting group
        var result = meetingGroupsFacade.joinGroup(user, meetingGroupId);
//        then he fails
        assertEquals(Option.of(USER_SUBSCRIPTION_IS_NOT_ACTIVE), result);
    }

    @Test
    public void joiningSameGroupTwiceShouldFail() {
//        given that meeting group got created
        var meetingGroupId = createMeetingGroup();
//        and user already joined group
        assert meetingGroupsFacade.joinGroup(user, meetingGroupId).isEmpty();
//        when he tries to join meeting group again
        var result = meetingGroupsFacade.joinGroup(user, meetingGroupId);
//        then he fails
        assertEquals(Option.of(USER_ALREADY_JOINED_GROUP), result);
    }

    @Test
    public void userWithActiveSubscriptionShouldBeAbleToJoinMeetingGroup() {
//        given that meeting group was created
        var meetingGroupId = createMeetingGroup();
//        when user tries to join the meeting group
        var result = meetingGroupsFacade.joinGroup(user, meetingGroupId);
//        then he succeeds
        assertEquals(Option.none(), result);
//        and 'new member joined meeting group' event was emitted
        verify(eventPublisher).newMemberJoinedMeetingGroup(groupMember(user), meetingGroupId);
    }
}