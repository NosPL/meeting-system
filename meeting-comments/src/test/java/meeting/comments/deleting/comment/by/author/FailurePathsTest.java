package meeting.comments.deleting.comment.by.author;

import commons.dto.GroupMeetingId;
import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Option;
import meeting.comments.TestSetup;
import meeting.comments.dto.CommentAuthorId;
import meeting.comments.dto.CommentId;
import org.junit.Before;
import org.junit.Test;

import static meeting.comments.dto.DeleteCommentFailure.*;
import static org.junit.Assert.assertEquals;

public class FailurePathsTest extends TestSetup {
    private final CommentAuthorId commentAuthorId = new CommentAuthorId("comment-author-id");
    private final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer-id");
    private final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group-id");
    private final GroupMeetingId groupMeetingId = new GroupMeetingId("group-meeting-id");

    @Before
    public void failurePathInit() {
        meetingCommentsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
        meetingCommentsFacade.newMeetingScheduled(groupMeetingId, meetingGroupId);
        meetingCommentsFacade.newMemberJoinedGroup(asGroupMember(commentAuthorId), meetingGroupId);
        subscriptionRenewed(commentAuthorId);
    }

    @Test
    public void userShouldFailToDeleteCommentAfterLeavingTheGroup() {
//        given that comment author left the comment
        var commentId = meetingCommentsFacade
                .leaveComment(commentAuthorId, groupMeetingId, notBlankComment())
                .get().getCommentId();
//        and comment author left the group
        meetingCommentsFacade.memberLeftTheMeetingGroup(asGroupMember(commentAuthorId), meetingGroupId);
//        when comment author tries to delete the comment
        var result = meetingCommentsFacade.deleteComment(commentAuthorId, commentId);
//        then he fails
        assertEquals(Option.of(USER_IS_NOT_GROUP_MEMBER), result);
    }

    @Test
    public void userShouldFailToDeleteNotExistingComment() {
//        when comment author tries to delete not existing comment
        var result = meetingCommentsFacade.deleteComment(commentAuthorId, randomCommentId());
//        then he fails
        assertEquals(Option.of(COMMENT_DOESNT_EXIST), result);
    }

    @Test
    public void userShouldFailToDeleteCommentAfterGroupWasRemoved() {
//        given that comment author left the comment
        var commentId = meetingCommentsFacade.leaveComment(commentAuthorId, groupMeetingId, notBlankComment())
                .get().getCommentId();
//        and meeting group was removed
        meetingCommentsFacade.meetingGroupWasRemoved(meetingGroupId);
//        when comment author tries to delete the comment
        var result = meetingCommentsFacade.deleteComment(commentAuthorId, commentId);
//        then he fails
        assertEquals(Option.of(COMMENT_DOESNT_EXIST), result);
    }

    @Test
    public void userShouldFailToDeleteCommentLeftBySomeoneElse() {
//        given that someone posted comment for meeting
        CommentId commentId = postComment();
//        when user tries to delete the comment left
        var result = meetingCommentsFacade.deleteComment(commentAuthorId, commentId);
//        then he fails
        assertEquals(Option.of(USER_IS_NOT_COMMENT_AUTHOR), result);
    }

    protected CommentId postComment() {
        var groupMemberId = randomGroupMemberId();
        return postComment(groupMemberId);
    }

    private CommentId postComment(GroupMemberId groupMemberId) {
        subscriptionRenewed(groupMemberId);
        meetingCommentsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
        return meetingCommentsFacade
                .leaveComment(asCommentAuthor(groupMemberId), groupMeetingId, notBlankComment())
                .get().getCommentId();
    }
}