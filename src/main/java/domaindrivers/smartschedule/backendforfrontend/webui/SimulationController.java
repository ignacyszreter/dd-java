package domaindrivers.smartschedule.backendforfrontend.webui;


import domaindrivers.smartschedule.allocation.AllocationFacade;
import domaindrivers.smartschedule.allocation.ProjectsAllocationsSummary;
import domaindrivers.smartschedule.backendforfrontend.BackendForFrontendFacade;
import domaindrivers.smartschedule.backendforfrontend.ProjectIdsDTO;
//import domaindrivers.smartschedule.backendforfrontend.SimulationClient;
import domaindrivers.smartschedule.backendforfrontend.calendar.CalendarProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class SimulationController {

    @Autowired
    private BackendForFrontendFacade backendForFrontendFacade;
    @Autowired
//    private SimulationClient simulationClient;
//    @Autowired
    private AllocationFacade allocationFacade;

    @GetMapping("/simulation/calendar")
    public ResponseEntity<List<CalendarProjectDTO>> getCalendar() {
        List<CalendarProjectDTO> projects = backendForFrontendFacade.getCalendarForPeriodOfTime();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/simulation/projects")
    public ResponseEntity<ProjectIdsDTO> getAllProjectsIds() {
        ProjectsAllocationsSummary projectsSummary = allocationFacade.findAllProjectsAllocations();
        Collection<UUID> projectsIds = projectsSummary.projectAllocations().keySet().stream().map(x -> x.id()).collect(Collectors.toList());
        return ResponseEntity.ok(new ProjectIdsDTO(projectsIds));
    }

//    @PostMapping("/simulation/simulation-move")
//    public ResponseEntity<SimulationResultDTO> simulationMove(@RequestBody RequestSimulationMove requestSimulationMove) {
//        Double result = simulationClient
//                .simulate(requestSimulationMove.toCommand());
//        return ResponseEntity.ok(new SimulationResultDTO(result));
//    }
//
//    @PostMapping("/simulation/simulation-reschedule")
//    public ResponseEntity<SimulationResultDTO> simulationReschedule(@RequestBody RequestSimulationReschedule requestSimulationReschedule) {
//        Double result = simulationClient
//                .simulate(requestSimulationReschedule.toCommand());
//        return ResponseEntity.ok(new SimulationResultDTO(result));
//    }
}
