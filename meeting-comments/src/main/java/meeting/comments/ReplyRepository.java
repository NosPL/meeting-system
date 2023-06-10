package meeting.comments;

import commons.dto.GroupMeetingId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;
import meeting.comments.dto.CommentId;
import meeting.comments.dto.ReplyAuthorId;
import meeting.comments.dto.ReplyId;

import java.util.List;
import java.util.function.Function;

interface ReplyRepository extends Repository<Reply, ReplyId> {

    List<Reply> findByCommentId(CommentId commentId);

    List<Reply> findByReplyAuthorId(ReplyAuthorId replyAuthorId);

    void removeByGroupMeetingId(GroupMeetingId groupMeetingId);

    class InMemory extends InMemoryRepository<Reply, ReplyId> implements ReplyRepository {

        public InMemory(List<Reply> entities, Function<Reply, ReplyId> idGetter) {
            super(entities, idGetter);
        }

        @Override
        public List<Reply> findByCommentId(CommentId commentId) {
            return entities
                    .stream()
                    .filter(reply -> reply.getCommentId().equals(commentId))
                    .toList();
        }

        @Override
        public List<Reply> findByReplyAuthorId(ReplyAuthorId replyAuthorId) {
            return entities
                    .stream()
                    .filter(reply -> reply.getReplyAuthorId().equals(replyAuthorId))
                    .toList();
        }

        @Override
        public void removeByGroupMeetingId(GroupMeetingId groupMeetingId) {
            entities = entities
                    .stream()
                    .filter(reply -> reply.getGroupMeetingId().equals(groupMeetingId))
                    .toList();
        }
    }
}