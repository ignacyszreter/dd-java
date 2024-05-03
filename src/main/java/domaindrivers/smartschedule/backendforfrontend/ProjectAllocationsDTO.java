package domaindrivers.smartschedule.backendforfrontend;

import java.util.Set;
import java.util.UUID;

public record ProjectAllocationsDTO(UUID projectAllocationsId,
                                    Set<CapabilityAllocationDTO> capabilityIds) {
}
