package meeting.groups.query.dto;

import lombok.Value;

import java.util.List;

@Value
public class MeetingGroupDetails {
    String id;
    String groupName;
    String organizerId;
    List<String> membersIds;
}