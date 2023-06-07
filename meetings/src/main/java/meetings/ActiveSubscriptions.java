package meetings;

import commons.dto.UserId;

import java.util.LinkedHashSet;
import java.util.Set;

class ActiveSubscriptions {
    private final Set<UserId> userIds = new LinkedHashSet<>();

    boolean contains(UserId userId) {
        return userIds.contains(userId);
    }

    void add(UserId userId) {
        userIds.add(userId);
    }

    void remove(UserId userId) {
        userIds.remove(userId);
    }
}