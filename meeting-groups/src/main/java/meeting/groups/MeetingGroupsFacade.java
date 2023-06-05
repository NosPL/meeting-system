package meeting.groups;

import commons.dto.MeetingGroupId;
import commons.dto.UserId;
import io.vavr.control.Either;
import io.vavr.control.Option;
import meeting.groups.dto.*;
import meeting.groups.query.dto.MeetingGroupDetails;
import meeting.groups.query.dto.ProposalDto;

import java.util.List;

public interface MeetingGroupsFacade {

    void subscriptionRenewed(UserId userId);

    void subscriptionExpired(UserId userId);

    Option<JoinGroupFailure> joinGroup(UserId userId, MeetingGroupId meetingGroupId);

    Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(UserId userId, ProposalDraft proposalDraft);

    Either<ProposalAcceptanceRejected, MeetingGroupId> acceptProposal(UserId userId, ProposalId proposalId);

    Option<FailedToRejectProposal> rejectProposal(UserId userId, ProposalId proposalId);

    void addAdministrator(UserId administratorId);

    void removeAdministrator(UserId administratorId);

    List<ProposalDto> findAllProposalsByOrganizer(UserId userId);

    Option<MeetingGroupDetails> findMeetingGroupDetails(MeetingGroupId meetingGroupId);
}