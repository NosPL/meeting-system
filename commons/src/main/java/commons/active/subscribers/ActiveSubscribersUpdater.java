package commons.active.subscribers;

import commons.dto.UserId;

public interface ActiveSubscribersUpdater {

    void add(UserId userId);

    void remove(UserId userId);
}