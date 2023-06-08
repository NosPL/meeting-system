package meeting.groups;

import commons.dto.GroupMemberId;
import commons.dto.UserId;
import io.vavr.Tuple;
import io.vavr.control.Option;
import meeting.groups.commons.TestSetup;
import org.junit.Test;

import java.util.List;

import static meeting.groups.dto.LeaveGroupFailure.GROUP_DOES_NOT_EXIST;
import static meeting.groups.dto.LeaveGroupFailure.USER_IS_NOT_GROUP_MEMBER;
import static org.junit.Assert.assertEquals;

public class LeavingGroup extends TestSetup {

    @Test
    public void groupDosNotExist() {
//        given that group was created
        var meetingGroupId = createMeetingGroup();
//        and user joined group
        assert meetingGroupsFacade.joinGroup(subscribedUser, meetingGroupId).isEmpty();
//        when he tries to leave not existing group
        var groupMemberId = toGroupMember(subscribedUser);
        var result = meetingGroupsFacade.leaveGroup(groupMemberId, randomMeetingGroupId());
//        then failure
        assertEquals(Option.of(GROUP_DOES_NOT_EXIST), result);
    }

    @Test
    public void userWasNotGroupMember() {
//        given that group was created
        var meetingGroupId = createMeetingGroup();
//        and user joined group
        assert meetingGroupsFacade.joinGroup(subscribedUser, meetingGroupId).isEmpty();
//        and user left group
        var groupMemberId = toGroupMember(subscribedUser);
        assert meetingGroupsFacade.leaveGroup(groupMemberId, meetingGroupId).isEmpty();
//        when he tries to leave the same group again
        var result = meetingGroupsFacade.leaveGroup(groupMemberId, meetingGroupId);
//        then failure
        assertEquals(Option.of(USER_IS_NOT_GROUP_MEMBER), result);
    }

    @Test
    public void success() {
//        given that group was created
        var meetingGroupId = createMeetingGroup();
//        and user joined group
        assert meetingGroupsFacade.joinGroup(subscribedUser, meetingGroupId).isEmpty();
//        when user tries to leave the group
        var groupMemberId = toGroupMember(subscribedUser);
        var result = meetingGroupsFacade.leaveGroup(groupMemberId, meetingGroupId);
//        then success
        assertEquals(Option.none(), result);
//        and
        eventPublisherMock.memberLeftGroupEventInvoked(List.of(Tuple.of(groupMemberId, meetingGroupId)));
    }

    private GroupMemberId toGroupMember(UserId userId) {
        return new GroupMemberId(userId.getId());
    }
}