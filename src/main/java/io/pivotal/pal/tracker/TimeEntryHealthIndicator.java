package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.stereotype.Component;

@Component
public class TimeEntryHealthIndicator implements HealthIndicator {

    private final TimeEntryRepository timeEntriesRepository;
    private final Integer MAX_TIME_ENTRIES = 5;

    public TimeEntryHealthIndicator(TimeEntryRepository timeEntriesRepository) {
        this.timeEntriesRepository = timeEntriesRepository;
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();

        if(timeEntriesRepository.list().size() < MAX_TIME_ENTRIES) {
            builder.up();
        } else {
            builder.down();
        }
        return builder.build();
    }
}
