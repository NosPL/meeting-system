package meeting.comments;

import commons.active.subscribers.ActiveSubscribersFinder;

import java.util.LinkedList;

public class MeetingCommentsConfiguration {
    private final CommentRepository commentRepository = new CommentRepository.InMemory(new LinkedList<>(), Comment::getCommentId);
    private final ReplyRepository replyRepository = new ReplyRepository.InMemory(new LinkedList<>(), Reply::getReplyId);
    private final GroupRepository groupRepository = new GroupRepository.InMemory(new LinkedList<>(), Group::getMeetingGroupId);
    private final GroupMembershipRepository groupMembershipRepository = new GroupMembershipRepository.InMemory(new LinkedList<>(), GroupMembership::getGroupMembershipId);
    private final MeetingRepository meetingRepository = new MeetingRepository.InMemory(new LinkedList<>(), Meeting::getGroupMeetingId);

    public MeetingCommentsFacade inMemoryMeetingCommentsFacade(ActiveSubscribersFinder activeSubscribersFinder) {
        var meetingCommentsFacade = new MeetingCommentsFacadeImpl(
                commentRepository, replyRepository, meetingRepository, groupRepository, groupMembershipRepository, activeSubscribersFinder);
        return new LogsDecorator(meetingCommentsFacade);
    }
}