package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<TimeEntry> timeEntryRowMapper;
    private final ResultSetExtractor<TimeEntry> extractor;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        timeEntryRowMapper = (rs, rowNum) -> {
            return new TimeEntry(
                    rs.getLong("id"),
                    rs.getLong("project_id"),
                    rs.getLong("user_id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getInt("hours"));
        };

        extractor = (rs) -> rs.next() ? timeEntryRowMapper.mapRow(rs, 1) : null;
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_TIME_ENTRY_SQL, new String[] {"id"});
            ps.setLong(1, timeEntry.getProjectId());
            ps.setLong(2, timeEntry.getUserId());
            ps.setDate(3, Date.valueOf(timeEntry.getDate()));
            ps.setInt(4, timeEntry.getHours());

            return ps;
        }, keyHolder);

        return find(keyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        return jdbcTemplate.query(
                FIND_TIME_ENTRY_SQL,
                new Object[]{timeEntryId},
                extractor
        );
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query(
                FIND_ALL_TIME_ENTRIES,
                timeEntryRowMapper
        );
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntryToUpdate) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(UPDATE_TIME_ENTRY);
            ps.setLong(1, timeEntryToUpdate.getProjectId());
            ps.setLong(2, timeEntryToUpdate.getUserId());
            ps.setDate(3, Date.valueOf(timeEntryToUpdate.getDate()));
            ps.setInt(4, timeEntryToUpdate.getHours());
            ps.setLong(5, id);
            return ps;
        });

        return find(id);
    }

    @Override
    public TimeEntry delete(long timeEntryId) {
        jdbcTemplate.update(DELETE_TIME_ENTRY_SQL, new Object[] {timeEntryId});
        return null;
    }

    private final String INSERT_TIME_ENTRY_SQL = "INSERT INTO time_entries (id,project_id,user_id,date,hours) VALUES (NULL, ?, ?, ?, ?)";
    private final String FIND_TIME_ENTRY_SQL = "SELECT * FROM time_entries WHERE id = ?";
    private final String FIND_ALL_TIME_ENTRIES = "SELECT * FROM time_entries";
    private final String UPDATE_TIME_ENTRY = "UPDATE time_entries SET project_id=?, user_id=?, date=?, hours=? WHERE id = ?";
    private final String DELETE_TIME_ENTRY_SQL = "DELETE from time_entries WHERE id = ?";
}
