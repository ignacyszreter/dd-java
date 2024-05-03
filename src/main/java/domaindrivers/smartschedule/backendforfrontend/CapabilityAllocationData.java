package domaindrivers.smartschedule.backendforfrontend;

import domaindrivers.smartschedule.allocation.capabilityscheduling.AllocatableCapabilityId;
import domaindrivers.smartschedule.shared.timeslot.TimeSlot;

public record CapabilityAllocationData(AllocatableCapabilityId allocatableCapabilityId, TimeSlot timeSlot) {

}
