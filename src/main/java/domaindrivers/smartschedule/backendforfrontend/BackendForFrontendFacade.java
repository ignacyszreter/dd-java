package domaindrivers.smartschedule.backendforfrontend;

import domaindrivers.smartschedule.backendforfrontend.calendar.CalendarProjectDTO;
import domaindrivers.smartschedule.backendforfrontend.calendar.SimulationCalendarReadModel;

import java.util.List;

public class BackendForFrontendFacade {
    private final SimulationCalendarReadModel simulationCalendarReadModel;

    public BackendForFrontendFacade(SimulationCalendarReadModel simulationCalendarReadModel) {
        this.simulationCalendarReadModel = simulationCalendarReadModel;
    }

    public List<CalendarProjectDTO> getCalendarForPeriodOfTime() {
        return simulationCalendarReadModel.loadAll();
    }
}
