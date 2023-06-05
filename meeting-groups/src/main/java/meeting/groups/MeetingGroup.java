package meeting.groups;

import commons.dto.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import meeting.groups.Proposal.ProposalAccepted;

import java.util.UUID;

@AllArgsConstructor
@Getter
class MeetingGroup {
    private String id;
    private String name;
    private String organizerId;

    static MeetingGroup createFromProposal(ProposalAccepted proposalAccepted) {
        return new MeetingGroup(UUID.randomUUID().toString(), proposalAccepted.getGroupName(), proposalAccepted.getCreatorId());
    }

    boolean isOrganizer(UserId userId) {
        return organizerId.equals(userId.getId());
    }
}