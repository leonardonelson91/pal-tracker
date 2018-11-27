package io.pivotal.pal.tracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private Map<Long, TimeEntry> timeEntriesMap = new HashMap<>();
    private Long currentTimeEntryId = 1L;

    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(currentTimeEntryId);
        currentTimeEntryId++;
        timeEntriesMap.put(timeEntry.getId(), timeEntry);
        return timeEntry;
    }

    public TimeEntry find(long id) {
        return timeEntriesMap.get(id);
    }

    public List<TimeEntry> list() {
        return timeEntriesMap.values().stream().collect(Collectors.toList());
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        if(find(id) == null) {
            return null;
        }

        timeEntry.setId(id);
        timeEntriesMap.put(id, timeEntry);
        return timeEntry;
    }

    public TimeEntry delete(long id) {
        TimeEntry timeEntry = find(id);

        if(timeEntry == null) {
            return null;
        }

        timeEntriesMap.remove(id);

        return timeEntry;
    }
}
