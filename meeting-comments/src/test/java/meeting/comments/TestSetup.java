package meeting.comments;

import commons.active.subscribers.InMemoryActiveSubscribers;
import commons.dto.GroupMeetingId;
import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.UserId;
import meeting.comments.dto.CommentAuthorId;
import meeting.comments.dto.CommentContent;
import meeting.comments.dto.CommentId;
import org.junit.Before;

import java.util.UUID;

public class TestSetup {
    protected InMemoryActiveSubscribers activeSubscribers;
    protected MeetingCommentsFacade meetingCommentsFacade;

    @Before
    public void testSetupInit() {
        activeSubscribers = new InMemoryActiveSubscribers();
        meetingCommentsFacade = new MeetingCommentsConfiguration().inMemoryMeetingCommentsFacade(activeSubscribers);
    }

    protected void subscriptionRenewed(CommentAuthorId commentAuthorId) {
        activeSubscribers.add(new UserId(commentAuthorId.getId()));
    }

    protected void subscriptionRenewed(GroupOrganizerId groupOrganizerId) {
        activeSubscribers.add(new UserId(groupOrganizerId.getId()));
    }

    protected void subscriptionExpired(CommentAuthorId commentAuthorId) {
        activeSubscribers.remove(new UserId(commentAuthorId.getId()));
    }

    protected CommentContent notBlankComment() {
        return new CommentContent("not blank");
    }

    protected CommentContent blankComment() {
        return new CommentContent("  ");
    }

    protected GroupMeetingId randomGroupMeetingId() {
        return new GroupMeetingId(UUID.randomUUID().toString());
    }

    protected GroupMemberId asGroupMember(CommentAuthorId commentAuthorId) {
        return new GroupMemberId(commentAuthorId.getId());
    }

    protected CommentAuthorId asCommentAuthor(GroupOrganizerId groupOrganizerId) {
        return new CommentAuthorId(groupOrganizerId.getId());
    }

    protected GroupMemberId randomGroupMemberId() {
        return new GroupMemberId(UUID.randomUUID().toString());
    }

    protected CommentId randomCommentId() {
        return new CommentId(UUID.randomUUID().toString());
    }

    protected void subscriptionRenewed(GroupMemberId groupMemberId) {
        activeSubscribers.add(new UserId(groupMemberId.getId()));
    }

    protected CommentAuthorId asCommentAuthor(GroupMemberId groupMemberId) {
        return new CommentAuthorId(groupMemberId.getId());
    }
}