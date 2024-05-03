package domaindrivers.smartschedule.backendforfrontend.webui;

import domaindrivers.smartschedule.allocation.ProjectAllocationsId;
import domaindrivers.smartschedule.allocation.capabilityscheduling.AllocatableCapabilityId;
import domaindrivers.smartschedule.backendforfrontend.CapabilityAllocationData;
import domaindrivers.smartschedule.backendforfrontend.ProjectAllocationsDTO;
import domaindrivers.smartschedule.backendforfrontend.ProjectSimulationData;
import domaindrivers.smartschedule.backendforfrontend.RequestSimulationMoveCommand;
import domaindrivers.smartschedule.shared.timeslot.TimeSlot;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record RequestSimulationMove(List<ProjectAllocationsDTO> projectsAllocations, UUID capabilityId, UUID toProject) {
    public RequestSimulationMoveCommand toCommand() {
        List<ProjectSimulationData> projectSimulationDataList = projectsAllocations.stream()
                .map(project -> new ProjectSimulationData(new ProjectAllocationsId(project.projectAllocationsId()),
                        project.capabilityIds().stream().map(x -> new CapabilityAllocationData(new AllocatableCapabilityId(x.capabilityId()),
                                new TimeSlot(x.from(), x.to()))).collect(Collectors.toSet())
                        )
                ).collect(Collectors.toList());
        return new RequestSimulationMoveCommand(projectSimulationDataList,
                new AllocatableCapabilityId(capabilityId), new ProjectAllocationsId(toProject));
    }
}


