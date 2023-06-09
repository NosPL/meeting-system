package meetings;

import commons.dto.GroupMemberId;
import io.vavr.control.Option;
import commons.dto.AttendeeId;

class WaitingList {

    Option<AttendeeId> pop() {
        return Option.none();
    }

    void remove(GroupMemberId groupMemberId) {

    }
}