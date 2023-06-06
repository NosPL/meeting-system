package meeting.groups.commons;

import commons.dto.AdministratorId;
import commons.dto.GroupOrganizerId;
import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import meeting.groups.MeetingGroupsFacade;
import meeting.groups.dto.ProposalDraft;
import meeting.groups.dto.ProposalId;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AutomatedProcesses {
    private final static AtomicInteger idGenerator = new AtomicInteger();

    public static MeetingGroupId createGroup(MeetingGroupsFacade meetingGroups, UserId userId, ProposalDraft proposalDraft) {
        var administratorId = new AdministratorId("administrator " + idGenerator.incrementAndGet());
        meetingGroups.subscriptionRenewed(userId);
        meetingGroups.addAdministrator(administratorId);
        var result = meetingGroups.submitMeetingGroupProposal(new GroupOrganizerId(userId.getId()), proposalDraft);
        var proposalId = result.get();
        return meetingGroups.acceptProposal(administratorId, proposalId).get();
    }

    public static ProposalId submitProposal(MeetingGroupsFacade meetingGroupsFacade, ProposalDraft proposalDraft) {
        var groupOrganizerId = new GroupOrganizerId(UUID.randomUUID().toString());
        meetingGroupsFacade.subscriptionRenewed(new UserId(groupOrganizerId.getId()));
        return meetingGroupsFacade.submitMeetingGroupProposal(groupOrganizerId, proposalDraft).get();
    }
}