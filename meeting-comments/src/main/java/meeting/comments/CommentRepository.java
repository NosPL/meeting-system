package meeting.comments;

import commons.dto.GroupMeetingId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;
import meeting.comments.dto.CommentAuthorId;
import meeting.comments.dto.CommentId;
import meeting.comments.query.dto.CommentDetails;

import java.util.List;
import java.util.function.Function;

interface CommentRepository extends Repository<Comment, CommentId> {

    List<Comment> findByGroupMeetingId(GroupMeetingId groupMeetingId);

    List<Comment> findByCommentAuthorId(CommentAuthorId commentAuthorId);

    void removeByGroupMeetingId(GroupMeetingId groupMeetingId);

    class InMemory extends InMemoryRepository<Comment, CommentId> implements CommentRepository {

        InMemory(List<Comment> entities, Function<Comment, CommentId> idGetter) {
            super(entities, idGetter);
        }

        @Override
        public List<Comment> findByGroupMeetingId(GroupMeetingId groupMeetingId) {
            return entities
                    .stream()
                    .filter(comment -> comment.getGroupMeetingId().equals(groupMeetingId))
                    .toList();
        }

        @Override
        public List<Comment> findByCommentAuthorId(CommentAuthorId commentAuthorId) {
            return entities
                    .stream()
                    .filter(comment -> comment.getCommentAuthorId().equals(commentAuthorId))
                    .toList();
        }

        @Override
        public void removeByGroupMeetingId(GroupMeetingId groupMeetingId) {
            entities = entities
                    .stream()
                    .filter(comment -> !comment.getGroupMeetingId().equals(groupMeetingId))
                    .toList();
        }
    }
}