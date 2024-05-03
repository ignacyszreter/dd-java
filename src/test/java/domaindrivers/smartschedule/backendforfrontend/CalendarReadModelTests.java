package domaindrivers.smartschedule.backendforfrontend;

import domaindrivers.smartschedule.MockedClockConfiguration;
import domaindrivers.smartschedule.MockedEventPublisherConfiguration;
import domaindrivers.smartschedule.TaskExecutorConfiguration;
import domaindrivers.smartschedule.TestDbConfiguration;
import domaindrivers.smartschedule.allocation.AllocationFacade;
import domaindrivers.smartschedule.allocation.Demand;
import domaindrivers.smartschedule.allocation.Demands;
import domaindrivers.smartschedule.allocation.ProjectAllocationsId;
import domaindrivers.smartschedule.allocation.capabilityscheduling.AllocatableCapabilityId;
import domaindrivers.smartschedule.allocation.cashflow.CashFlowFacade;
import domaindrivers.smartschedule.allocation.cashflow.Cost;
import domaindrivers.smartschedule.allocation.cashflow.Income;
import domaindrivers.smartschedule.backendforfrontend.calendar.AllocatedResourceDTO;
import domaindrivers.smartschedule.backendforfrontend.calendar.CalendarProjectDTO;
import domaindrivers.smartschedule.resource.device.DeviceFacade;
import domaindrivers.smartschedule.resource.device.DeviceId;
import domaindrivers.smartschedule.resource.employee.EmployeeFacade;
import domaindrivers.smartschedule.resource.employee.EmployeeId;
import domaindrivers.smartschedule.resource.employee.Seniority;
import domaindrivers.smartschedule.shared.capability.Capability;
import domaindrivers.smartschedule.shared.timeslot.TimeSlot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import({TestDbConfiguration.class, MockedEventPublisherConfiguration.class, TaskExecutorConfiguration.class})
@Sql(scripts = {"classpath:schema-availability.sql", "classpath:schema-resources.sql", "classpath:schema-allocations.sql", "classpath:schema-risk.sql", "classpath:schema-cashflow.sql"})
public class CalendarReadModelTests {

    static final TimeSlot JAN_1 = TimeSlot.createDailyTimeSlotAtUTC(2021, 1, 1);
    static final TimeSlot JANUARY = TimeSlot.createMonthlyTimeSlotAtUTC(2021, 1);

    @Autowired
    EmployeeFacade employeeFacade;

    @Autowired
    DeviceFacade deviceFacade;

    @Autowired
    AllocationFacade allocationFacade;

    @Autowired
    CashFlowFacade cashFlowFacade;

    @Autowired
    BackendForFrontendFacade backendForFrontendFacade;

    @Test
    void returnsProjectsWithinGivenTimeSlot() {
        //given
        TimeSlot oneDay = TimeSlot.createDailyTimeSlotAtUTC(2021, 1, 1);

        //given
        ProjectAllocationsId projectAllocationsId = allocationFacade
                .createAllocation(JANUARY, Demands.of(new Demand(Capability.skill("JAVA-MID"), JAN_1)));
        cashFlowFacade.addIncomeAndCost(projectAllocationsId, Income.of(10), Cost.of(1));

        ProjectAllocationsId projectAllocationsId2 = allocationFacade
                .createAllocation(JANUARY,
                        Demands.of(
                                new Demand(Capability.skill("JAVA-MID"), JAN_1),
                                new Demand(Capability.asset("BULLDOZER"), JANUARY)));

        cashFlowFacade.addIncomeAndCost(projectAllocationsId2, Income.of(100), Cost.of(1));

        //and
        EmployeeId staszek = employeeFacade.addEmployee("Staszek", "Staszkowski", Seniority.MID,
                Set.of(Capability.skill("JAVA-MID")), Set.of());
        List<AllocatableCapabilityId> staszekAllocatableCapabilitiesIds = employeeFacade.scheduleCapabilities(staszek, JAN_1);
        allocationFacade.allocateToProject(projectAllocationsId, staszekAllocatableCapabilitiesIds.get(0), JAN_1);

        EmployeeId jozek = employeeFacade.addEmployee("Jozek", "Bizon", Seniority.SENIOR,
                Set.of(Capability.skill("JAVA-SENIOR")), Set.of());
        List<AllocatableCapabilityId> jozekAllocatableCapabilitiesIds = employeeFacade.scheduleCapabilities(jozek, JAN_1);
        allocationFacade.allocateToProject(projectAllocationsId2, jozekAllocatableCapabilitiesIds.get(0), JAN_1);

        DeviceId bulldozer = deviceFacade.createDevice("SUPER-BULLDOZER-1000", Set.of(Capability.asset("BULLDOZER")));
        List<AllocatableCapabilityId> bulldozerAllocatableCapabilitiesIds = deviceFacade.scheduleCapabilities(bulldozer, JANUARY);
        allocationFacade.allocateToProject(projectAllocationsId2, jozekAllocatableCapabilitiesIds.get(0), JANUARY);

        //when
        List<CalendarProjectDTO> actualCalendarProjectDtoList = backendForFrontendFacade.getCalendarForPeriodOfTime();

        assertEquals(2, actualCalendarProjectDtoList.size());
        assertTrue(actualCalendarProjectDtoList.contains(new CalendarProjectDTO(
                projectAllocationsId2.id(),
                JANUARY.from(),
                JANUARY.to(),
                Arrays.asList(
                        new AllocatedResourceDTO(
                                jozekAllocatableCapabilitiesIds.get(0).getId(),
                                "Jozek Bizon (SENIOR)",
                                "employee",
                                oneDay.from(),
                                oneDay.to(),
                                List.of(Capability.skill("JAVA-SENIOR"))
                        ),
                        new AllocatedResourceDTO(
                                bulldozerAllocatableCapabilitiesIds.get(0).getId(),
                                "SUPER-BULLDOZER-1000",
                                "device",
                                JANUARY.from(),
                                JANUARY.to(),
                                List.of(Capability.asset("BULLDOZER"))
                        )
                )
        )));
        assertTrue(actualCalendarProjectDtoList.contains(new CalendarProjectDTO(
                projectAllocationsId.id(),
                JANUARY.from(),
                JANUARY.to(),
                Arrays.asList(
                        new AllocatedResourceDTO(
                                staszekAllocatableCapabilitiesIds.get(0).getId(),
                                "Staszek Staszkowski (MID)",
                                "employee",
                                oneDay.from(),
                                oneDay.to(),
                                List.of(Capability.skill("JAVA-MID"))
                        )
                )
        )));
    }
}
