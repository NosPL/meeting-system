package meeting.groups;

import io.vavr.Tuple;
import io.vavr.control.Option;
import meeting.groups.commons.TestSetup;
import org.junit.Test;

import java.util.List;

import static io.vavr.Tuple.of;
import static meeting.groups.dto.JoinGroupFailure.*;
import static org.junit.Assert.assertEquals;

public class JoiningMeetingGroup extends TestSetup {

    @Test
    public void joiningGroupThatDoesNotExistShouldFail() {
//        given that user subscription got renewed
        meetingGroupsFacade.subscriptionRenewed(userId());
//        and some group got created
        var existingGroupId = createGroup(randomProposal());
//        when user tries to join group with different group id
        var result = meetingGroupsFacade.joinGroup(userId(), idDifferentThan(existingGroupId));
//        then he fails because group does not exist
        assertEquals(Option.of(MEETING_GROUP_DOES_NOT_EXIST), result);
    }

    @Test
    public void joiningGroupWithoutActiveSubscriptionShouldFail() {
//        given that meeting group got created
        var meetingGroupId = createGroup(randomProposal());
//        and user subscription expired
        meetingGroupsFacade.subscriptionExpired(userId());
//        when user tries to join created meeting group
        var result = meetingGroupsFacade.joinGroup(userId(), meetingGroupId);
//        then he fails because user does not have active subscription
        assertEquals(Option.of(USER_SUBSCRIPTION_IS_NOT_ACTIVE), result);
    }

    @Test
    public void joiningSameGroupTwiceShouldFail() {
//        given that meeting group got created
        var meetingGroupId = createGroup(randomProposal());
//        and user subscription got renewed
        meetingGroupsFacade.subscriptionRenewed(userId());
//        and user already joined group
        assert meetingGroupsFacade.joinGroup(userId(), meetingGroupId).isEmpty();
//        when he tries to join this group again
        var result = meetingGroupsFacade.joinGroup(userId(), meetingGroupId);
        assertEquals(Option.of(USER_ALREADY_JOINED_GROUP), result);
    }

    @Test
    public void userWithActiveSubscriptionShouldBeAbleToJoinMultipleGroups() {
//        given that multiple meeting groups got created
        var meetingGroupId1 = createGroup(randomProposal());
        var meetingGroupId2 = createGroup(randomProposal());
        var meetingGroupId3 = createGroup(randomProposal());
//        and user subscription got renewed
        meetingGroupsFacade.subscriptionRenewed(userId());
//        when user tries to join all these groups
//        then he succeeds every time
        assert meetingGroupsFacade.joinGroup(userId(), meetingGroupId1).isEmpty();
        assert meetingGroupsFacade.joinGroup(userId(), meetingGroupId2).isEmpty();
        assert meetingGroupsFacade.joinGroup(userId(), meetingGroupId3).isEmpty();
//        and 'new member joined meeting group' event should be emitted 3 times
        var expectedInvocations = List.of(of(userId(), meetingGroupId1), of(userId(), meetingGroupId2), of(userId(), meetingGroupId3));
        assert eventPublisherMock.newMemberJoinedGroupEventInvoked(expectedInvocations);
    }
}