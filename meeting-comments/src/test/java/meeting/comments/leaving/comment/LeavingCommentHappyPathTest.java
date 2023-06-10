package meeting.comments.leaving.comment;

import commons.dto.*;
import io.vavr.control.Option;
import meeting.comments.TestSetup;
import meeting.comments.dto.CommentAuthorId;
import org.junit.Before;
import org.junit.Test;

import static meeting.comments.dto.LeaveCommentFailure.*;
import static org.junit.Assert.assertEquals;

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
//        when user tries to leave the comment
        var result = meetingCommentsFacade.leaveComment(commentAuthorId, groupMeetingId, notBlankComment());
//        then he succeeds
        assert result.isRight();
    }
}