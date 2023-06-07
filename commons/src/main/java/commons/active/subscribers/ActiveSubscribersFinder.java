package commons.active.subscribers;

import commons.dto.UserId;

public interface ActiveSubscribersFinder {
    boolean contains(UserId userId);
}