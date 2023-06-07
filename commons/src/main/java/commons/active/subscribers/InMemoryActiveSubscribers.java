package commons.active.subscribers;

import commons.dto.UserId;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class InMemoryActiveSubscribers implements ActiveSubscribersUpdater, ActiveSubscribersFinder {
    private final Set<UserId> userIds = new HashSet<>();

    @Override
    public boolean contains(UserId userId) {
        return userIds.contains(userId);
    }

    @Override
    public void add(UserId userId) {
        userIds.add(userId);
        log.info("subscription renewed, user id {}", userId.getId());
    }

    @Override
    public void remove(UserId userId) {
        userIds.remove(userId);
        log.info("subscription expired, user id {}", userId.getId());
    }
}