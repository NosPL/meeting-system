package meetings.notifications;

import commons.dto.AttendeeId;

import java.util.Set;

public interface MeetingsNotificationsFacade {

    void notifyAttendeeAboutBeingSignedUpFromWaitingList(AttendeeId attendeeId);

    void notifyAttendeesAboutMeetingCancellation(Set<AttendeeId> attendees);
}