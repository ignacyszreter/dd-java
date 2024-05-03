package domaindrivers.smartschedule.backendforfrontend.webui;

import domaindrivers.smartschedule.allocation.ProjectAllocationsId;
import domaindrivers.smartschedule.allocation.capabilityscheduling.AllocatableCapabilityId;
import domaindrivers.smartschedule.backendforfrontend.CapabilityAllocationData;
import domaindrivers.smartschedule.backendforfrontend.ProjectAllocationsDTO;
import domaindrivers.smartschedule.backendforfrontend.ProjectSimulationData;
import domaindrivers.smartschedule.backendforfrontend.RequestSimulationRescheduleCommand;
import domaindrivers.smartschedule.shared.timeslot.TimeSlot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record RequestSimulationReschedule(List<ProjectAllocationsDTO> projectsAllocations, UUID capabilityId, Instant from, Instant to) {
    public RequestSimulationRescheduleCommand toCommand() {
        List<ProjectSimulationData> projectSimulationDataList = projectsAllocations.stream()
                .map(project -> new ProjectSimulationData(new ProjectAllocationsId(project.projectAllocationsId()),
                                project.capabilityIds().stream().map(x -> new CapabilityAllocationData(new AllocatableCapabilityId(x.capabilityId()),
                                        new TimeSlot(x.from(), x.to()))).collect(Collectors.toSet())
                        )
                ).collect(Collectors.toList());
        return new RequestSimulationRescheduleCommand(projectSimulationDataList,
                new AllocatableCapabilityId(capabilityId), new TimeSlot(from, to));
    }
}
