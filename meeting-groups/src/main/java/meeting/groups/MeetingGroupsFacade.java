package meeting.groups;

import commons.dto.*;
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

    Either<ProposalRejected, ProposalId> submitMeetingGroupProposal(GroupOrganizerId groupOrganizerId, ProposalDraft proposalDraft);

    Either<ProposalAcceptanceRejected, MeetingGroupId> acceptProposal(AdministratorId administratorId, ProposalId proposalId);

    Option<FailedToRejectProposal> rejectProposal(AdministratorId administratorId, ProposalId proposalId);

    void addAdministrator(AdministratorId administratorId);

    void removeAdministrator(AdministratorId administratorId);

    List<ProposalDto> findAllProposalsOfGroupOrganizer(GroupOrganizerId groupOrganizerId);

    Option<MeetingGroupDetails> findMeetingGroupDetails(MeetingGroupId meetingGroupId);
}