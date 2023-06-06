package meeting.groups;

import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import meeting.groups.Proposal.ProposalAccepted;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@Getter
class MeetingGroup {
    private MeetingGroupId meetingGroupId;
    private String name;
    private GroupOrganizerId groupOrganizerId;

    static MeetingGroup createFromProposal(ProposalAccepted proposalAccepted) {
        String id = UUID.randomUUID().toString();
        return new MeetingGroup(new MeetingGroupId(id), proposalAccepted.getGroupName(), proposalAccepted.getGroupOrganizerId());
    }

    boolean isOrganizer(UserId userId) {
        return groupOrganizerId.equals(new GroupOrganizerId(userId.getId()));
    }
}