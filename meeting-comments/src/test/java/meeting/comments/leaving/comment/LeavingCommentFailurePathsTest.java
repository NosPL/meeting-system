package meeting.comments.leaving.comment;

import commons.dto.GroupMeetingId;
import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Either;
import io.vavr.control.Option;
import meeting.comments.TestSetup;
import meeting.comments.dto.CommentAuthorId;
import org.junit.Before;
import org.junit.Test;

import static meeting.comments.dto.LeaveCommentFailure.*;
import static org.junit.Assert.assertEquals;

public class LeavingCommentFailurePathsTest extends TestSetup {
    private final CommentAuthorId commentAuthorId = new CommentAuthorId("comment-author-id");
    private final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group-id");
    private final GroupMeetingId groupMeetingId = new GroupMeetingId("group-meeting-id");
    private final GroupMemberId groupMemberId = new GroupMemberId("group-member-id");
    private final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer-id");

    @Before
    public void leavingCommentFailureInit() {
        meetingCommentsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
        meetingCommentsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
        meetingCommentsFacade.newMeetingScheduled(groupMeetingId, meetingGroupId);
        subscriptionRenewed(groupMemberId);
    }

    @Test
    public void userShouldFailToLeaveBlankComment() {
//        when comment author tries to leave blank comment
        var result = meetingCommentsFacade.leaveComment(commentAuthorId, groupMeetingId, blankComment());
//        then he fails
        assertEquals(Either.left(COMMENT_CONTENT_CANNOT_BE_BLANK), result);
    }

    @Test
    public void userShouldFailToLeaveTheCommentForNotExistingMeeting() {
//        when comment author tries to leave the comment for not existing meeting
        var result = meetingCommentsFacade.leaveComment(commentAuthorId, randomGroupMeetingId(), notBlankComment());
//        then he fails
        assertEquals(Either.left(MEETING_DOESNT_EXIST), result);
    }

    @Test
    public void userThatIsNotGroupMemberShouldFailToLeaveTheComment() {
//        given that comment author left the group
        meetingCommentsFacade.memberLeftTheMeetingGroup(asGroupMember(commentAuthorId), meetingGroupId);
//        when comment author tries to leave the comment
        var result = meetingCommentsFacade.leaveComment(commentAuthorId, groupMeetingId, notBlankComment());
//        then he fails
        assertEquals(Either.left(USER_IS_NOT_GROUP_MEMBER), result);
    }

    @Test
    public void unsubscribedUserShouldFailToLeaveTheComment() {
//        given that comment author is not subscribed
        subscriptionExpired(commentAuthorId);
//        when user tries to leave the comment
        var result = meetingCommentsFacade.leaveComment(commentAuthorId, groupMeetingId, notBlankComment());
//        then he fails
        assertEquals(Either.left(USER_IS_NOT_SUBSCRIBED), result);
    }
}