package meeting.groups;

import commons.dto.*;
import io.vavr.control.Either;
import io.vavr.control.Option;
import meeting.groups.dto.*;
import meeting.groups.query.dto.MeetingGroupDetails;
import meeting.groups.query.dto.ProposalDto;

import java.util.List;

public interface MeetingGroupsFacade {

    Option<JoinGroupFailure> joinGroup(UserId userId, MeetingGroupId meetingGroupId);

    Option<LeaveGroupFailure> leaveGroup(GroupMemberId groupMemberId, MeetingGroupId meetingGroupId);

    Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(GroupOrganizerId groupOrganizerId, ProposalDraft proposalDraft);

    Option<RemoveProposalFailure> removeWaitingProposal(GroupOrganizerId groupOrganizerId, ProposalId proposalId);

    Either<ProposalAcceptanceRejected, MeetingGroupId> acceptProposal(AdministratorId administratorId, ProposalId proposalId);

    Option<FailedToRejectProposal> rejectProposal(AdministratorId administratorId, ProposalId proposalId);

    Option<RemoveGroupFailure> removeGroup(GroupOrganizerId groupOrganizerId, MeetingGroupId meetingGroupId);

    void administratorAdded(AdministratorId administratorId);

    void administratorRemoved(AdministratorId administratorId);

    void newMeetingScheduled(MeetingGroupId meetingGroupId, GroupMeetingId groupMeetingId);

    void meetingWasHeld(MeetingGroupId meetingGroupId, GroupMeetingId groupMeetingId);

    void meetingCancelled(MeetingGroupId meetingGroupId, GroupMeetingId groupMeetingId);

    List<ProposalDto> findAllProposalsOfGroupOrganizer(GroupOrganizerId groupOrganizerId);

    Option<MeetingGroupDetails> findMeetingGroupDetails(MeetingGroupId meetingGroupId);
}