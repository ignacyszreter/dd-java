package domaindrivers.smartschedule.backendforfrontend.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record CalendarProjectDTO(
        @JsonProperty("id") UUID id,
        @JsonProperty("projectStartDate")
        Instant projectStartDate,
        @JsonProperty("projectEndDate")
        Instant projectEndDate,
        @JsonProperty("allocations") Set<AllocatedResourceDTO> allocations
) {
}
