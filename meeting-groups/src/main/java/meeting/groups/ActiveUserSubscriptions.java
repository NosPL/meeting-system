package meeting.groups;

import commons.dto.UserId;
import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
class ActiveUserSubscriptions {
    private final Set<UserId> userIds;

    boolean contains(UserId userId) {
        return userIds.contains(userId);
    }

    public void add(UserId userId) {
        userIds.add(userId);
    }

    public void remove(UserId userId) {
        userIds.remove(userId);
    }
}