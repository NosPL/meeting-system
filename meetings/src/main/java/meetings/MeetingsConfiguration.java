package meetings;

import commons.calendar.Calendar;

public class MeetingsConfiguration {

    public MeetingsFacade meetingsFacade(Calendar calendar) {
        return new MeetingsFacadeImpl();
    }
}