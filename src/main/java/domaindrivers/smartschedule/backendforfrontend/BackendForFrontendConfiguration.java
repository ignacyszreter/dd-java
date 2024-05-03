package domaindrivers.smartschedule.backendforfrontend;

import domaindrivers.smartschedule.allocation.AllocationFacade;
import domaindrivers.smartschedule.allocation.cashflow.CashFlowFacade;
import domaindrivers.smartschedule.backendforfrontend.calendar.SimulationCalendarReadModel;
import domaindrivers.smartschedule.simulation.SimulationFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


@Configuration
class BackendForFrontendConfiguration {

    @Bean
    BackendForFrontendFacade sharedCalendarReadModelFacade(JdbcTemplate jdbcTemplate) {
        return new BackendForFrontendFacade(new SimulationCalendarReadModel(jdbcTemplate));
    }

//    @Bean
//    SimulationClient backendForFrontendSimulationClient(AllocationFacade allocationFacade, SimulationFacade simulationFacade, CashFlowFacade cashFlowFacade) {
//        return new SimulationClient(allocationFacade, simulationFacade, cashFlowFacade);
//    }
}