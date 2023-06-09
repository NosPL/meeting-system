package meetings.wait.list.sign.out;

import io.vavr.control.Option;
import meetings.commons.TestSetup;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SignOutForMeetingWaitListFailingPath extends TestSetup {

    @Test
    public void userShouldFailToSignOutOfWaitListOfNotExistingMeeting() {
//        when user tries to sign out from wait list of not existing meeting
        var result = meetingsFacade.signOutFromMeetingWaitList(randomGroupMemberId(), randomGroupMeetingId());
//        then he fails
        assertEquals(Option.none(), result);
    }
}