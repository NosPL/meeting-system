package meeting.groups.query.dto;

import lombok.Value;

@Value
public class ProposalDto {
    String id;
    String organizerId;
    String groupName;
    State state;

    public enum State {
        ACCEPTED, REJECTED, WAITING
    }
}