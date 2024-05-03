package domaindrivers.smartschedule.backendforfrontend;

import java.time.Instant;
import java.util.UUID;

public record CapabilityAllocationDTO(UUID capabilityId, Instant from, Instant to) {

}
