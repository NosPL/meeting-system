package meeting.groups;

import io.vavr.control.Option;
import meeting.groups.commons.TestSetup;
import org.junit.Test;

import static meeting.groups.dto.LeaveGroupFailure.GROUP_DOES_NOT_EXIST;
import static meeting.groups.dto.LeaveGroupFailure.USER_IS_NOT_GROUP_MEMBER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class LeavingGroup extends TestSetup {

    @Test
    public void groupMemberShouldFailToLeaveNotExistingGroup() {
//        given that group was created
        var meetingGroupId = createMeetingGroup();
//        and user joined group
        assert meetingGroupsFacade.joinGroup(user, meetingGroupId).isEmpty();
//        when he tries to leave not existing group
        var result = meetingGroupsFacade.leaveGroup(groupMember(user), randomMeetingGroupId());
//        then he fails
        assertEquals(Option.of(GROUP_DOES_NOT_EXIST), result);
    }

    @Test
    public void userShouldFailToLeaveTheGroupThatHeIsNotPartOf() {
//        given that group was created
        var meetingGroupId = createMeetingGroup();
//        and user joined group
        assert meetingGroupsFacade.joinGroup(user, meetingGroupId).isEmpty();
//        and user left group
        assert meetingGroupsFacade.leaveGroup(groupMember(user), meetingGroupId).isEmpty();
//        when he tries to leave the same group again
        var result = meetingGroupsFacade.leaveGroup(groupMember(user), meetingGroupId);
//        then failure
        assertEquals(Option.of(USER_IS_NOT_GROUP_MEMBER), result);
    }

    @Test
    public void success() {
//        given that group was created
        var meetingGroupId = createMeetingGroup();
//        and user joined group
        assert meetingGroupsFacade.joinGroup(user, meetingGroupId).isEmpty();
//        when user tries to leave the group
        var result = meetingGroupsFacade.leaveGroup(groupMember(user), meetingGroupId);
//        then success
        assertEquals(Option.none(), result);
//        and 'group member left group event was emitted
        verify(eventPublisher).groupMemberLeftGroup(groupMember(user), meetingGroupId);
    }
}