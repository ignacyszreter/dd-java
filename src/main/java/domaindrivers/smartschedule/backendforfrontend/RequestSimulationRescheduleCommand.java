package domaindrivers.smartschedule.backendforfrontend;

import domaindrivers.smartschedule.allocation.capabilityscheduling.AllocatableCapabilityId;
import domaindrivers.smartschedule.shared.timeslot.TimeSlot;

import java.util.List;

public record RequestSimulationRescheduleCommand(List<ProjectSimulationData> projectSimulationData,
                                                 AllocatableCapabilityId capabilityId, TimeSlot timeSlot) {
}
