package meetings.signing.up;

import commons.dto.GroupMemberId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import io.vavr.control.Option;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SignUpForMeetingHappyPath extends SigningUpSetup {
    private final GroupMemberId groupMemberId = new GroupMemberId("group-member-id");
    private final MeetingGroupId meetingGroupId = new MeetingGroupId("meeting-group-id");
    private final GroupOrganizerId groupOrganizerId = new GroupOrganizerId("group-organizer-id");

    @Test
    public void signUpForMeetingHappyPath() {
//        given that meeting group was created
        meetingsFacade.newMeetingGroupCreated(groupOrganizerId, meetingGroupId);
//        and meeting was scheduled
        var groupMeetingId = scheduleMeeting(groupOrganizerId, meetingGroupId);
//        and new member joined meeting group
        meetingsFacade.newMemberJoinedGroup(groupMemberId, meetingGroupId);
//        and group member is subscribed
        subscriptionRenewed(groupMemberId);
//        when he tries to sign up for a meeting
        var result = meetingsFacade.signUpForMeeting(groupMemberId, groupMeetingId);
//        then success
        assertEquals(Option.none(), result);
    }
}