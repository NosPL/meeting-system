package meeting.comments.leaving.comment;

import commons.dto.GroupMeetingId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import meeting.comments.TestSetup;
import meeting.comments.dto.CommentAuthorId;
import org.junit.Test;

public class LeavingCommentHappyPathTest extends TestSetup {
    private final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group");
    private final GroupMeetingId groupMeetingId = new GroupMeetingId("group-meeting");
    private final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer");
    private final CommentAuthorId commentAuthorId = new CommentAuthorId("comment-author");

    @Test
    public void leaveCommentHappyPath() {
//        given that group was created
        meetingCommentsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
//        and meeting was scheduled
        meetingCommentsFacade.newMeetingScheduled(groupMeetingId, meetingGroupId);
//        and comment author joined group
        meetingCommentsFacade.newMemberJoinedGroup(asGroupMember(commentAuthorId), meetingGroupId);
//        and comment author is subscribed
        subscriptionRenewed(commentAuthorId);
//        when user tries to leave a comment
        var result = meetingCommentsFacade.leaveComment(commentAuthorId, groupMeetingId, notBlankComment());
//        then he succeeds
        assert result.isRight();
//        when group organizer tries to leave a comment
        subscriptionRenewed(groupOrganizerId);
        result = meetingCommentsFacade
                .leaveComment(asCommentAuthor(groupOrganizerId), groupMeetingId, notBlankComment());
//        then he succeeds
        assert result.isRight();
    }
}