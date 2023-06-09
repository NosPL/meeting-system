package meetings.signing.up;

import commons.dto.*;
import io.vavr.control.Option;
import meetings.commons.TestSetup;
import meetings.dto.*;

import java.util.UUID;

public class SigningUpSetup extends TestSetup {

    protected GroupMeetingHostId asHost(GroupMemberId groupMemberId) {
        return new GroupMeetingHostId(groupMemberId.getId());
    }

    protected void subscriptionExpired(GroupMemberId groupMemberId) {
        activeSubscribers.remove(new UserId(groupMemberId.getId()));
    }

    protected void subscriptionRenewed(GroupMemberId groupMemberId) {
        activeSubscribers.add(new UserId(groupMemberId.getId()));
    }

    protected GroupMemberId createGroupMemberId() {
        return new GroupMemberId(UUID.randomUUID().toString());
    }

    protected GroupMeetingId randomMeetingId() {
        return new GroupMeetingId(UUID.randomUUID().toString());
    }
}