package com.campus.repository;

import com.campus.model.Company;
import com.campus.model.Event;
import com.campus.model.EventSponsor;
import com.campus.model.EventSponsorId;
import com.campus.model.Sponsor; // Import Sponsor model
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EventRepositoryImpl implements EventRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper for Event
    private static final class EventRowMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            Event event = new Event();
            event.setEventId(rs.getInt("eventID")); // Corrected setter: setEventId
            // Assuming adminID is needed, otherwise fetch Admin object if required
            // event.setAdmin(adminRepository.findById(rs.getInt("adminID")).orElse(null));
            event.setTitle(rs.getString("title"));
            event.setDescription(rs.getString("description"));
            event.setDate(rs.getDate("date").toLocalDate());
            event.setLocation(rs.getString("location"));
            return event;
        }
    }

    // RowMapper for EventSponsor (mapping sponsor details and amount)
    // NOTE: Removed invalid @Autowired SponsorRepository from static inner class
    private static final class EventSponsorDetailsRowMapper implements RowMapper<EventSponsor> {
        // If full Sponsor object is needed later, SponsorRepository must be accessed differently (e.g., passed to mapper)

        @Override
        public EventSponsor mapRow(ResultSet rs, int rowNum) throws SQLException {
            EventSponsor eventSponsor = new EventSponsor();
            EventSponsorId id = new EventSponsorId();
            // We don't get eventID back from sp_get_sponsors_for_event, set it later if needed
            id.setSponsorId(rs.getInt("sponsorID")); // From sponsor table
            eventSponsor.setId(id);
            eventSponsor.setAmount(rs.getBigDecimal("amount")); // From eventsponsor table

            // Optionally map Sponsor details if needed, requires SponsorRepository access
            // Sponsor sponsor = new Sponsor();
            // sponsor.setSponsorID(rs.getInt("sponsorID"));
            // sponsor.setName(rs.getString("name"));
            // sponsor.setEmail(rs.getString("email"));
            // eventSponsor.setSponsor(sponsor); // Assuming EventSponsor has a Sponsor field

            return eventSponsor;
        }
    }


    @Override
    public Event save(Event event) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_insert_event")
                .declareParameters(
                        new SqlParameter("p_admin_id", Types.INTEGER),
                        new SqlParameter("p_title", Types.VARCHAR),
                        new SqlParameter("p_description", Types.VARCHAR),
                        new SqlParameter("p_date", Types.DATE),
                        new SqlParameter("p_location", Types.VARCHAR),
                        new SqlOutParameter("p_new_id", Types.INTEGER));

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_admin_id", event.getAdmin() != null ? event.getAdmin().getAdminId() : null) // Corrected getter: getAdminId
                .addValue("p_title", event.getTitle())
                .addValue("p_description", event.getDescription())
                .addValue("p_date", event.getDate())
                .addValue("p_location", event.getLocation());

        Map<String, Object> out = jdbcCall.execute(in);
        int newId = (Integer) out.get("p_new_id");
        event.setEventId(newId); // Corrected setter: setEventId
        return event;
    }

    @Override
    public Optional<Event> findById(Integer eventId) {
         try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_event_by_id")
                .declareParameters(new SqlParameter("p_event_id", Types.INTEGER))
                .returningResultSet("event", new EventRowMapper());

            SqlParameterSource in = new MapSqlParameterSource().addValue("p_event_id", eventId);
            Map<String, Object> out = jdbcCall.execute(in);
            List<Event> list = (List<Event>) out.get("event");
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Event> findAll() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_all_events")
                .returningResultSet("events", new EventRowMapper());

        Map<String, Object> out = jdbcCall.execute();
        return (List<Event>) out.get("events");
    }

    @Override
    public boolean update(Event event) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_update_event")
                .declareParameters(
                        new SqlParameter("p_event_id", Types.INTEGER),
                        new SqlParameter("p_admin_id", Types.INTEGER),
                        new SqlParameter("p_title", Types.VARCHAR),
                        new SqlParameter("p_description", Types.VARCHAR),
                        new SqlParameter("p_date", Types.DATE),
                        new SqlParameter("p_location", Types.VARCHAR),
                        new SqlOutParameter("p_rows_updated", Types.INTEGER));

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_event_id", event.getEventId()) // Corrected getter: getEventId
                .addValue("p_admin_id", event.getAdmin() != null ? event.getAdmin().getAdminId() : null) // Corrected getter: getAdminId
                .addValue("p_title", event.getTitle())
                .addValue("p_description", event.getDescription())
                .addValue("p_date", event.getDate())
                .addValue("p_location", event.getLocation());

        Map<String, Object> out = jdbcCall.execute(in);
        int rowsUpdated = (Integer) out.get("p_rows_updated");
        return rowsUpdated > 0;
    }

    @Override
    public boolean deleteById(Integer eventId) {
         SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_delete_event_by_id")
                .declareParameters(
                        new SqlParameter("p_event_id", Types.INTEGER),
                        new SqlOutParameter("p_rows_deleted", Types.INTEGER));

        SqlParameterSource in = new MapSqlParameterSource().addValue("p_event_id", eventId);
        Map<String, Object> out = jdbcCall.execute(in);
        int rowsDeleted = (Integer) out.get("p_rows_deleted");
        return rowsDeleted > 0;
    }

    @Override
    public void setEventCompanies(Integer eventId, List<Integer> companyIds) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_set_event_companies")
                .declareParameters(
                        new SqlParameter("p_event_id", Types.INTEGER),
                        new SqlParameter("p_company_ids", Types.VARCHAR)); // Pass as comma-separated string

        String companyIdString = companyIds.stream()
                                           .map(String::valueOf)
                                           .collect(Collectors.joining(","));

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_event_id", eventId)
                .addValue("p_company_ids", companyIdString);

        jdbcCall.execute(in);
    }

    @Override
    public void setEventSponsors(Integer eventId, List<EventSponsor> sponsors) {
         SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_set_event_sponsors")
                .declareParameters(
                        new SqlParameter("p_event_id", Types.INTEGER),
                        new SqlParameter("p_sponsor_data", Types.VARCHAR)); // Pass as comma-separated sponsorID:amount string

        String sponsorDataString = sponsors.stream()
                                           .map(s -> s.getId().getSponsorId() + ":" + s.getAmount().toPlainString())
                                           .collect(Collectors.joining(","));

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_event_id", eventId)
                .addValue("p_sponsor_data", sponsorDataString);

        jdbcCall.execute(in);
    }

     @Override
    public List<Integer> findCompanyIdsByEventId(Integer eventId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_companies_for_event")
                .declareParameters(new SqlParameter("p_event_id", Types.INTEGER))
                .returningResultSet("companies", BeanPropertyRowMapper.newInstance(Company.class)); // Map full company first

        SqlParameterSource in = new MapSqlParameterSource().addValue("p_event_id", eventId);
        Map<String, Object> out = jdbcCall.execute(in);
        List<Company> companies = (List<Company>) out.get("companies");

        // Extract just the IDs
        return companies.stream().map(Company::getCompanyId).collect(Collectors.toList()); // Corrected getter: getCompanyId
    }

    @Override
    public List<EventSponsor> findSponsorsByEventId(Integer eventId) {
         SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_sponsors_for_event")
                .declareParameters(new SqlParameter("p_event_id", Types.INTEGER))
                .returningResultSet("sponsors", new EventSponsorDetailsRowMapper()); // Use custom mapper

        SqlParameterSource in = new MapSqlParameterSource().addValue("p_event_id", eventId);
        Map<String, Object> out = jdbcCall.execute(in);
        List<EventSponsor> eventSponsors = (List<EventSponsor>) out.get("sponsors");

        // Set the eventId for each EventSponsorId as it wasn't returned by the SP
        eventSponsors.forEach(es -> es.getId().setEventId(eventId));

        return eventSponsors;
    }
}
