//package domaindrivers.smartschedule.backendforfrontend;
//
//import domaindrivers.smartschedule.MockedEventPublisherConfiguration;
//import domaindrivers.smartschedule.TestDbConfiguration;
//import domaindrivers.smartschedule.allocation.AllocationFacade;
//import domaindrivers.smartschedule.allocation.ProjectAllocationsId;
//import domaindrivers.smartschedule.allocation.cashflow.CashFlowFacade;
//import domaindrivers.smartschedule.builders.*;
//import domaindrivers.smartschedule.resource.device.DeviceFacade;
//import domaindrivers.smartschedule.resource.employee.EmployeeFacade;
//import domaindrivers.smartschedule.resource.employee.Seniority;
//import domaindrivers.smartschedule.shared.timeslot.TimeSlot;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.jdbc.Sql;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//@Import({TestDbConfiguration.class, MockedEventPublisherConfiguration.class})
//@Sql(scripts = {"classpath:schema-availability.sql", "classpath:schema-resources.sql", "classpath:schema-devices.sql", "classpath:schema-allocations.sql", "classpath:schema-cashflow.sql"})
//public class SimulateTests {
//    static final TimeSlot JAN_1 = TimeSlot.createDailyTimeSlotAtUTC(2021, 1, 1);
//    static final TimeSlot JAN_2 = TimeSlot.createDailyTimeSlotAtUTC(2021, 1, 2);
//    static final TimeSlot JAN_3 = TimeSlot.createDailyTimeSlotAtUTC(2021, 1, 3);
//    static final TimeSlot JANUARY = TimeSlot.createMonthlyTimeSlotAtUTC(2021, 1);
//
//    @Autowired
//    private SimulationClient simulationClient;
//
//    @Autowired
//    private CashFlowFacade cashFlowFacade;
//
//    @Autowired
//    private AllocationFacade allocationFacade;
//
//    @Autowired
//    private EmployeeFacade employeeFacade;
//
//    @Autowired
//    private DeviceFacade deviceFacade;
//
//    @Test
//    void simulatesMovingCapabilitiesToDifferentProject() {
//        //given
//        ProjectAllocationsId projectId = project()
//                .withName("project")
//                .withDemands(demand().skill("JAVA-MID").atTime(JAN_1))
//                .withDates(JANUARY)
//                .withCashflow(cashflow().ofIncome(10).ofCost(1))
//                .create();
//
//        ProjectAllocationsId projectId2 = project()
//                .withName("project")
//                .withDemands(demand().skill("JAVA-MID").atTime(JAN_1))
//                .withDates(JANUARY)
//                .withCashflow(cashflow().ofIncome(100).ofCost(1))
//                .create();
//
//        //and
//        EmployeeResult staszek = employee("Staszek", "Staszkowski")
//                .ofSeniority(Seniority.MID)
//                .withSkill("JAVA-MID")
//                .thatHasScheduledCapabilitiesFor(JAN_1)
//                .allocatedTo(projectId).employee();
//
//        ProjectSimulationData simulateProject = new ProjectSimulationData(projectId, new HashSet<>(staszek.allocatableCapabilityIds().stream().map(x -> new CapabilityAllocationData(x, JAN_1))
//                .collect(Collectors.toSet())));
//        ProjectSimulationData simulateProject2 = new ProjectSimulationData(projectId2, Set.of());
//
//        //when
//        Double result = simulationClient.simulate(new RequestSimulationMoveCommand(List.of(simulateProject, simulateProject2), staszek.allocatableCapabilityIds().get(0), simulateProject2.projectAllocationsId()));
//
//        //then
//        assertEquals(90d, result);
//    }
//
//    @Test
//    void simulatesMovingCapabilitiesToDifferentProject_InputCapabilitiesAreNotAssignedInReal() {
//        //given
//        ProjectAllocationsId projectId = project()
//                .withName("project")
//                .withDemands(demand().skill("JAVA-MID").atTime(JAN_1))
//                .withDates(JANUARY)
//                .withCashflow(cashflow().ofIncome(10).ofCost(1))
//                .create();
//
//        ProjectAllocationsId projectId2 = project()
//                .withName("project")
//                .withDemands(demand().skill("JAVA-MID").atTime(JAN_1))
//                .withDates(JANUARY)
//                .withCashflow(cashflow().ofIncome(100).ofCost(1))
//                .create();
//
//        //and
//        EmployeeResult staszek = employee("Staszek", "Staszkowski")
//                .ofSeniority(Seniority.MID)
//                .withSkill("JAVA-MID")
//                .thatHasScheduledCapabilitiesFor(JAN_1)
//                .allocatedTo(projectId2).employee();
//
//        ProjectSimulationData simulateProject = new ProjectSimulationData(projectId, new HashSet<>(staszek.allocatableCapabilityIds().stream().map(x -> new CapabilityAllocationData(x, JAN_1))
//                .collect(Collectors.toSet())));
//        ProjectSimulationData simulateProject2 = new ProjectSimulationData(projectId2, Set.of());
//
//        //when
//        Double result = simulationClient.simulate(new RequestSimulationMoveCommand(List.of(simulateProject, simulateProject2), staszek.allocatableCapabilityIds().get(0), simulateProject2.projectAllocationsId()));
//
//        //then
//        assertEquals(90d, result);
//    }
//
//    @Test
//    void simulatesReschedulingCapability() {
//        //given
//        ProjectAllocationsId projectId = project()
//                .withName("project")
//                .withDemands(demand().skill("JAVA-MID").atTime(JAN_1))
//                .withDates(JANUARY)
//                .withCashflow(cashflow().ofIncome(10).ofCost(1))
//                .create();
//
//        //and
//        EmployeeResult staszek = employee("Staszek", "Staszkowski")
//                .ofSeniority(Seniority.MID)
//                .withSkill("JAVA-MID")
//                .thatHasScheduledCapabilitiesFor(JAN_1)
//                .allocatedTo(projectId).employee();
//
//
//        ProjectSimulationData projectSimulationData = new ProjectSimulationData(projectId,
//                new HashSet<>(staszek.allocatableCapabilityIds().stream().map(x -> new CapabilityAllocationData(x, JAN_1))
//                        .collect(Collectors.toSet())));
//
//        //when
//        Double result = simulationClient.simulate(new RequestSimulationRescheduleCommand(List.of(projectSimulationData),
//                staszek.allocatableCapabilityIds().get(0),
//                JAN_2));
//
//        //then
//        assertEquals(-9d, result);
//    }
//
//    @Test
//    void simulatesReschedulingCapability_InputCapabilitiesAreNotScheduledForGivenDayInReal() {
//        //given
//        ProjectAllocationsId projectId = project()
//                .withName("project")
//                .withDemands(demand().skill("JAVA-MID").atTime(JAN_3))
//                .withDates(JANUARY)
//                .withCashflow(cashflow().ofIncome(10).ofCost(1))
//                .create();
//
//        //and
//        EmployeeResult staszek = employee("Staszek", "Staszkowski")
//                .ofSeniority(Seniority.MID)
//                .withSkill("JAVA-MID")
//                .thatHasScheduledCapabilitiesFor(JANUARY)
//                .allocatedTo(projectId).employee();
//
//
//        ProjectSimulationData projectSimulationData = new ProjectSimulationData(projectId,
//                new HashSet<>(staszek.allocatableCapabilityIds().stream().map(x -> new CapabilityAllocationData(x, JAN_2))
//                        .collect(Collectors.toSet())));
//
//        //when
//        Double result = simulationClient.simulate(new RequestSimulationRescheduleCommand(List.of(projectSimulationData),
//                staszek.allocatableCapabilityIds().get(0),
//                JAN_3));
//
//        //then
//        assertEquals(9d, result);
//    }
//
//    ProjectAllocationsBuilder project() {
//        return new ProjectAllocationsBuilder(allocationFacade, cashFlowFacade);
//    }
//
//    ScheduledDemandsBuilder demand() {
//        return new ScheduledDemandsBuilder();
//    }
//
//    CashFlowBuilder cashflow() {
//        return new CashFlowBuilder();
//    }
//
//    EmployeeBuilder employee(String firstName, String lastName) {
//        return new EmployeeBuilder(employeeFacade, allocationFacade, firstName, lastName);
//    }
//}
//
//
