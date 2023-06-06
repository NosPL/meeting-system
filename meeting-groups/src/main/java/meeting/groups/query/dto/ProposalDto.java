package meeting.groups.query.dto;

import commons.dto.GroupOrganizerId;
import lombok.Value;
import meeting.groups.dto.ProposalId;

@Value
public class ProposalDto {
    ProposalId proposalId;
    GroupOrganizerId groupOrganizerId;
    String groupName;
    State state;

    public enum State {
        ACCEPTED, REJECTED, WAITING
    }
}