package meeting.comments;

import commons.active.subscribers.ActiveSubscribersFinder;

public class MeetingCommentsConfiguration {

    public MeetingCommentsFacade meetingCommentsFacade(ActiveSubscribersFinder activeSubscribersFinder) {
        var meetingCommentsFacade = new MeetingCommentsFacadeImpl(activeSubscribersFinder);
        return new LogsDecorator(meetingCommentsFacade);
    }
}