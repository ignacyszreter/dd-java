package domaindrivers.smartschedule.backendforfrontend.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import domaindrivers.smartschedule.shared.capability.Capability;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AllocatedResourceDTO(
        @JsonProperty("id") UUID id,
        @JsonProperty("description") String description,
        @JsonProperty("type") String type,
        @JsonProperty("from") Instant from,
        @JsonProperty("to") Instant to,
        @JsonProperty("capabilities") List<Capability> capabilities
) {
}
