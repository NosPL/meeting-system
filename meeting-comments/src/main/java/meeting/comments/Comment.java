package meeting.comments;

import commons.dto.GroupMeetingId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import meeting.comments.dto.CommentAuthorId;
import meeting.comments.dto.CommentContent;
import meeting.comments.dto.CommentId;
import meeting.comments.query.dto.CommentDetails;
import meeting.comments.query.dto.LikesCount;
import meeting.comments.query.dto.RepliesCount;

import java.util.UUID;


@AllArgsConstructor
@Getter
class Comment {
    private CommentId commentId;
    private CommentAuthorId commentAuthorId;
    private GroupMeetingId groupMeetingId;
    private CommentContent commentContent;
    private RepliesCount repliesCount;
    private LikesCount likesCount;

    void replyAdded() {
        repliesCount = new RepliesCount(repliesCount.getCount() + 1);
    }

    void replyRemoved() {
        repliesCount = new RepliesCount(repliesCount.getCount() - 1);
    }

    void likeAdded() {
        likesCount = new LikesCount(likesCount.getCount() + 1);
    }

    void likeRemoved() {
        likesCount = new LikesCount(likesCount.getCount() - 1);
    }

    CommentDetails toDto() {
        return new CommentDetails(commentId, commentAuthorId, groupMeetingId, commentContent, repliesCount, likesCount);
    }

    public static Comment create(CommentAuthorId commentAuthorId, GroupMeetingId groupMeetingId, CommentContent commentContent) {
        CommentId commentId = new CommentId(UUID.randomUUID().toString());
        return new Comment(commentId, commentAuthorId, groupMeetingId, commentContent, new RepliesCount(0), new LikesCount(0));
    }
}