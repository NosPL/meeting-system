package commons.active.subscribers;

import commons.dto.UserId;

import java.util.HashSet;
import java.util.Set;

public class InMemoryActiveSubscribers implements ActiveSubscribersUpdater, ActiveSubscribersFinder {
    private final Set<UserId> userIds = new HashSet<>();

    @Override
    public boolean contains(UserId userId) {
        return userIds.contains(userId);
    }

    @Override
    public void add(UserId userId) {
        userIds.add(userId);
    }

    @Override
    public void remove(UserId userId) {
        userIds.remove(userId);
    }
}