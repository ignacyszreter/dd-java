package domaindrivers.smartschedule.backendforfrontend;

import domaindrivers.smartschedule.allocation.ProjectAllocationsId;
import domaindrivers.smartschedule.allocation.capabilityscheduling.AllocatableCapabilityId;

import java.util.List;

public record RequestSimulationMoveCommand(List<ProjectSimulationData> projectSimulationData,
                                           AllocatableCapabilityId capabilityId, ProjectAllocationsId to) {}

