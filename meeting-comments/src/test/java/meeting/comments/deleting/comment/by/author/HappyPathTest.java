package meeting.comments.deleting.comment.by.author;

import commons.dto.GroupMeetingId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Option;
import meeting.comments.TestSetup;
import meeting.comments.dto.CommentAuthorId;
import meeting.comments.dto.CommentId;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HappyPathTest extends TestSetup {
    private final CommentAuthorId commentAuthorId = new CommentAuthorId("comment-author-id");
    private final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer-id");
    private final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group-id");
    private final GroupMeetingId groupMeetingId = new GroupMeetingId("group-meeting-id");

    @Test
    public void test() {
//        given that meeting was created
        meetingCommentsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
//        and comment author joined group
        meetingCommentsFacade.newMemberJoinedGroup(asGroupMember(commentAuthorId), meetingGroupId);
//        and meeting was scheduled
        meetingCommentsFacade.newMeetingScheduled(groupMeetingId, meetingGroupId);
//        and comment author is subscribed
        subscriptionRenewed(commentAuthorId);
//        and comment author left the comment
        var commentId = meetingCommentsFacade
                .leaveComment(commentAuthorId, groupMeetingId, notBlankComment()).get().getCommentId();
//        when author comment tries to delete the comment
        var result = meetingCommentsFacade.deleteComment(commentAuthorId, commentId);
//        then he succeeds
        assertEquals(Option.none(), result);
    }
}