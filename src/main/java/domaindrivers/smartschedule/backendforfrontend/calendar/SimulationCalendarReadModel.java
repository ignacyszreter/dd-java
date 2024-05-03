package domaindrivers.smartschedule.backendforfrontend.calendar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SimulationCalendarReadModel {
    String calendar_query = """
            SELECT
                p.project_allocations_id AS "projectId",
                p.from_date AS "projectStartDate",
                p.to_date AS "projectEndDate",
                jsonb_agg(
                    CASE
                        WHEN e.employee_id IS NOT NULL THEN
                            jsonb_build_object(
                                'id', ac.id,
                                'from', ac.from_date,
                                'to', ac.to_date,
                                'description', e.name || ' ' || e.last_name || ' (' || e.seniority || ')',
                                'type', 'employee',
                                'capabilities', jsonb_extract_path(ac.possible_capabilities, 'capabilities')
                            )
                        ELSE
                            jsonb_build_object(
                                'id', ac.id,
                                'from', ac.from_date,
                                'to', ac.to_date,
                                'description', d.model,
                                'type', 'device',
                                'capabilities', jsonb_extract_path(ac.possible_capabilities, 'capabilities')
                            )
                    END
                ) AS "allocations"
            FROM
                project_allocations p
                CROSS JOIN LATERAL jsonb_array_elements(p.allocations->'all') AS allocs
                JOIN allocatable_capabilities ac ON ac.id = (allocs->'id'->>'id')::uuid
                LEFT JOIN employees e ON ac.resource_id = e.employee_id
                LEFT JOIN devices d ON ac.resource_id = d.device_id
            GROUP BY
                p.project_allocations_id, p.from_date, p.to_date;
            """;

    private final JdbcTemplate jdbcTemplate;

    public SimulationCalendarReadModel(JdbcTemplate client) {
        this.jdbcTemplate = client;
    }

    public List<CalendarProjectDTO> loadAll() {
        List<Map<String, Object>> results =
                jdbcTemplate.queryForList(
                        calendar_query);

        List<CalendarProjectDTO> projectAllocationDTOs = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Instant.class, new InstantDeserializer());
        mapper.registerModule(simpleModule);

        for (Map<String, Object> result : results) {
            String allocationsJson = result.get("allocations").toString();
            List<AllocatedResourceDTO> allocations = new ArrayList<>();
            try {
                allocations = mapper.readValue(allocationsJson, new TypeReference<>() {
                });
            } catch (Exception e) {
                // Handle the exception appropriately
                e.printStackTrace();
            }

            CalendarProjectDTO dto = new CalendarProjectDTO(
                    (UUID) result.get("projectId"),
                    toUtcInstant(result.get("projectStartDate").toString()),
                    toUtcInstant(result.get("projectEndDate").toString()),
                    allocations
            );
            projectAllocationDTOs.add(dto);
        }

        return projectAllocationDTOs;
    }

    private static Instant toUtcInstant(String dateTimeStr) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 6, true)
                .toFormatter();
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, formatter);

        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        return instant;
    }
}

