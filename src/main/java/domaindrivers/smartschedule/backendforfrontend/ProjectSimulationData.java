package domaindrivers.smartschedule.backendforfrontend;

import domaindrivers.smartschedule.allocation.ProjectAllocationsId;

import java.util.Set;

public record ProjectSimulationData(ProjectAllocationsId projectAllocationsId, Set<CapabilityAllocationData> capabilities) {
}

