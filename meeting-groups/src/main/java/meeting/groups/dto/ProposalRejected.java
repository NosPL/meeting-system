package meeting.groups.dto;

public enum ProposalRejected {
    GROUP_LIMIT_PER_USER_EXCEEDED,
    MEETING_GROUP_WITH_PROPOSED_NAME_ALREADY_EXISTS,
    PROPOSAL_WITH_THE_SAME_GROUP_NAME_ALREADY_EXISTS,
    SUBSCRIPTION_NOT_ACTIVE
}