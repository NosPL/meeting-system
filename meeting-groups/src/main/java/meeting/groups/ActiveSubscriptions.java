package meeting.groups;

import commons.dto.GroupOrganizerId;
import commons.dto.UserId;
import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
class ActiveSubscriptions {
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

    public boolean contains(GroupOrganizerId groupOrganizerId) {
        return userIds.contains(new UserId(groupOrganizerId.getId()));
    }
}