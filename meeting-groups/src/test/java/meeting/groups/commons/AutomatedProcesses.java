package meeting.groups.commons;

import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import meeting.groups.MeetingGroupsFacade;
import meeting.groups.dto.ProposalDraft;
import meeting.groups.dto.ProposalId;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AutomatedProcesses {
    private final static AtomicInteger idGenerator = new AtomicInteger();

    public static MeetingGroupId createGroup(MeetingGroupsFacade meetingGroups, ProposalDraft proposalDraft) {
        return createGroup(meetingGroups, new UserId(UUID.randomUUID().toString()), proposalDraft);
    }


    public static MeetingGroupId createGroup(MeetingGroupsFacade meetingGroups, UserId userId, ProposalDraft proposalDraft) {
        UserId administratorId = new UserId("administrator " + idGenerator.incrementAndGet());
        meetingGroups.subscriptionRenewed(userId);
        meetingGroups.addAdministrator(administratorId);
        var result = meetingGroups.submitMeetingGroupProposal(userId, proposalDraft);
        var proposalId = result.get();
        return meetingGroups.acceptProposal(administratorId, proposalId).get();
    }

    public static ProposalId submitProposal(MeetingGroupsFacade meetingGroupsFacade, ProposalDraft proposalDraft) {
        UserId userId = new UserId(UUID.randomUUID().toString());
        meetingGroupsFacade.subscriptionRenewed(userId);
        return meetingGroupsFacade.submitMeetingGroupProposal(userId, proposalDraft).get();
    }
}