//package domaindrivers.smartschedule.backendforfrontend;
//
//import domaindrivers.smartschedule.allocation.AllocationFacade;
//import domaindrivers.smartschedule.allocation.Allocations;
//import domaindrivers.smartschedule.allocation.ProjectAllocationsId;
//import domaindrivers.smartschedule.allocation.ProjectsAllocationsSummary;
//import domaindrivers.smartschedule.allocation.capabilityscheduling.AllocatableCapabilityId;
//import domaindrivers.smartschedule.allocation.capabilityscheduling.AllocatedCapability;
//import domaindrivers.smartschedule.allocation.cashflow.CashFlowFacade;
//import domaindrivers.smartschedule.allocation.cashflow.Earnings;
//import domaindrivers.smartschedule.simulation.ProjectsSimulation;
//import domaindrivers.smartschedule.simulation.SimulationFacade;
//import domaindrivers.smartschedule.simulation.TotalAvailability;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.function.Supplier;
//import java.util.stream.Collectors;
//
//public class SimulationClient {
//    private final AllocationFacade allocationFacade;
//    private final SimulationFacade simulationFacade;
//    private final CashFlowFacade cashFlowFacade;
//
//    public SimulationClient(AllocationFacade allocationFacade,
//                            SimulationFacade simulationFacade,
//                            CashFlowFacade cashFlowFacade) {
//        this.allocationFacade = allocationFacade;
//        this.simulationFacade = simulationFacade;
//        this.cashFlowFacade = cashFlowFacade;
//    }
//
//    public Double simulate(RequestSimulationMoveCommand requestSimulationMoveCommand) {
//        Set<ProjectAllocationsId> projectAllocationsIdSet = getUniqueProjectIds(requestSimulationMoveCommand.projectSimulationData());
//
//        Map<ProjectAllocationsId, Supplier<BigDecimal>> earnings = findProjectValuesByEarnings();
//        ProjectsAllocationsSummary projectsAllocations = allocationFacade.findAllProjectsAllocations(projectAllocationsIdSet);
//        Map<AllocatableCapabilityId, AllocatedCapability> allocatedCapabilitiesByIds =
//                projectsAllocations.projectAllocations().values().stream().flatMap(x -> x.all().stream())
//                        .collect(HashMap::new, (map, entry) -> map.put(entry.id(), entry), HashMap::putAll);
//
//        Map<ProjectAllocationsId, Allocations> simulatedAllocations =
//                enrichInputSimulationDataWithRealAllocatedCapabilities(requestSimulationMoveCommand.projectSimulationData(), allocatedCapabilitiesByIds);
//
//        ProjectsSimulation projectsSimulation = new ProjectsSimulation(earnings, simulatedAllocations, projectsAllocations.demands());
//
//        Double result = simulationFacade.calculateProfitAfterMovingCapabilities(
//                projectsSimulation,
//                requestSimulationMoveCommand.to(),
//                Set.of(requestSimulationMoveCommand.capabilityId()),
//                allocatedCapabilitiesByIds.get(requestSimulationMoveCommand.capabilityId()).timeSlot(),
//                new TotalAvailability(Map.of())
//        );
//        return result;
//    }
//
//    public Double simulate(RequestSimulationRescheduleCommand requestSimulationRescheduleCommand) {
//        ProjectsSimulation projectsSimulation = fetchProjectsSimulation(requestSimulationRescheduleCommand.projectSimulationData());
//
//        Double result = simulationFacade.calculateProfitAfterReschedulingCapabilities(
//                projectsSimulation,
//                requestSimulationRescheduleCommand.capabilityId(),
//                requestSimulationRescheduleCommand.timeSlot(),
//                new TotalAvailability(Map.of())
//        );
//        return result;
//    }
//
//    private ProjectsSimulation fetchProjectsSimulation(List<ProjectSimulationData> projectSimulationData) {
//        Set<ProjectAllocationsId> projectAllocationsIdSet = getUniqueProjectIds(projectSimulationData);
//
//        Map<ProjectAllocationsId, Supplier<BigDecimal>> earnings = findProjectValuesByEarnings();
//        ProjectsAllocationsSummary projectsAllocations = allocationFacade.findAllProjectsAllocations(projectAllocationsIdSet);
//        Map<AllocatableCapabilityId, AllocatedCapability> allocatedCapabilitiesByIds =
//                projectsAllocations.projectAllocations().values().stream().flatMap(x -> x.all().stream())
//                        .collect(HashMap::new, (map, entry) -> map.put(entry.id(), entry), HashMap::putAll);
//
//        Map<ProjectAllocationsId, Allocations> simulatedAllocations =
//                enrichInputSimulationDataWithRealAllocatedCapabilities(projectSimulationData, allocatedCapabilitiesByIds);
//
//        ProjectsSimulation projectsSimulation = new ProjectsSimulation(earnings, simulatedAllocations, projectsAllocations.demands());
//
//        return projectsSimulation;
//    }
//
//    private static HashMap<ProjectAllocationsId, Allocations> enrichInputSimulationDataWithRealAllocatedCapabilities(List<ProjectSimulationData> projectSimulationData, Map<AllocatableCapabilityId, AllocatedCapability> allocatedCapabilitiesByIds) {
//        return projectSimulationData.stream()
//                .collect(HashMap::new, (map, entry) ->
//                        map.put(entry.projectAllocationsId(),
//                                new Allocations(entry.capabilities().stream().map(x -> new AllocatedCapability(x.allocatableCapabilityId(), allocatedCapabilitiesByIds.get(x.allocatableCapabilityId()).capability(), x.timeSlot()))
//                                        .collect(Collectors.toSet()))
//                        ), HashMap::putAll);
//    }
//
//    private static Set<ProjectAllocationsId> getUniqueProjectIds(List<ProjectSimulationData> projectSimulationData) {
//        return projectSimulationData
//                .stream().map(x -> x.projectAllocationsId()).collect(Collectors.toSet());
//    }
//
//    private Map<ProjectAllocationsId, Supplier<BigDecimal>> findProjectValuesByEarnings() {
//        Map<ProjectAllocationsId, Earnings> allEarnings = cashFlowFacade.findAllEarnings();
//        return allEarnings
//                .entrySet()
//                .stream()
//                .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), () -> entry.getValue().toBigDecimal()), HashMap::putAll);
//    }
//}
