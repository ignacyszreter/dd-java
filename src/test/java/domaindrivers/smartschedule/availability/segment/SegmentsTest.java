package domaindrivers.smartschedule.availability.segment;

import domaindrivers.smartschedule.shared.timeslot.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SegmentsTest {

    @Test
    void unitHasToBeMultipleOf15Minutes() {
        //expect
        assertThrows(IllegalArgumentException.class, () -> SegmentInMinutes.of(20));
        assertThrows(IllegalArgumentException.class, () -> SegmentInMinutes.of(18));
        assertThrows(IllegalArgumentException.class, () -> SegmentInMinutes.of(7));
        assertNotNull(SegmentInMinutes.of(15));
        assertNotNull(SegmentInMinutes.of(30));
        assertNotNull(SegmentInMinutes.of(45));

    }

    @Test
    void splittingIntoSegmentsWhenThereIsNoLeftover() {
        //given
        Instant start = Instant.parse("2023-09-09T00:00:00Z");
        Instant end = Instant.parse("2023-09-09T01:00:00Z");
        TimeSlot timeSlot = new TimeSlot(start, end);

        //when
        List<TimeSlot> segments = Segments.split(timeSlot, SegmentInMinutes.of(15));

        //then
        assertEquals(4, segments.size());
        assertEquals(Instant.parse("2023-09-09T00:00:00Z"), segments.get(0).from());
        assertEquals(Instant.parse("2023-09-09T00:15:00Z"), segments.get(0).to());
        assertEquals(Instant.parse("2023-09-09T00:15:00Z"), segments.get(1).from());
        assertEquals(Instant.parse("2023-09-09T00:30:00Z"), segments.get(1).to());
        assertEquals(Instant.parse("2023-09-09T00:30:00Z"), segments.get(2).from());
        assertEquals(Instant.parse("2023-09-09T00:45:00Z"), segments.get(2).to());
        assertEquals(Instant.parse("2023-09-09T00:45:00Z"), segments.get(3).from());
        assertEquals(Instant.parse("2023-09-09T01:00:00Z"), segments.get(3).to());

    }

    @Test
    void splittingIntoSegmentsJustNormalizesIfChosenSegmentLargerThanPassedSlot() {
        //given
        Instant start = Instant.parse("2023-09-09T00:10:00Z");
        Instant end = Instant.parse("2023-09-09T01:00:00Z");
        TimeSlot timeSlot = new TimeSlot(start, end);

        //when
        List<TimeSlot> segments = Segments.split(timeSlot, SegmentInMinutes.of(90));

        //then
        assertEquals(1, segments.size());
        assertEquals(Instant.parse("2023-09-09T00:00:00Z"), segments.get(0).from());
        assertEquals(Instant.parse("2023-09-09T01:30:00Z"), segments.get(0).to());

    }

    @Test
    void normalizingATimeSlot() {
        //given
        Instant start = Instant.parse("2023-09-09T00:10:00Z");
        Instant end = Instant.parse("2023-09-09T01:00:00Z");
        TimeSlot timeSlot = new TimeSlot(start, end);

        //when
        TimeSlot segment = Segments.normalizeToSegmentBoundaries(timeSlot, SegmentInMinutes.of(90));

        //then
        assertEquals(Instant.parse("2023-09-09T00:00:00Z"), segment.from());
        assertEquals(Instant.parse("2023-09-09T01:30:00Z"), segment.to());

    }

    @Test
    void slotsAreNormalizedBeforeSplitting() {
        //given
        Instant start = Instant.parse("2023-09-09T00:10:00Z");
        Instant end = Instant.parse("2023-09-09T00:59:00Z");
        TimeSlot timeSlot = new TimeSlot(start, end);
        SegmentInMinutes oneHour = SegmentInMinutes.of(60);

        //when
        List<TimeSlot> segments = Segments.split(timeSlot, oneHour);

        //then
        assertEquals(1, segments.size());
        assertEquals(Instant.parse("2023-09-09T00:00:00Z"), segments.get(0).from());
        assertEquals(Instant.parse("2023-09-09T01:00:00Z"), segments.get(0).to());

    }

    @Test
    void splittingIntoSegmentsWithoutNormalization() {
        //given
        Instant start = Instant.parse("2023-09-09T00:00:00Z");
        Instant end = Instant.parse("2023-09-09T00:59:00Z");
        TimeSlot timeSlot = new TimeSlot(start, end);

        //when
        List<TimeSlot> segments = new SlotToSegments().apply(timeSlot, SegmentInMinutes.of(30));

        //then
        assertEquals(2, segments.size());

        assertEquals(Instant.parse("2023-09-09T00:00:00Z"), segments.get(0).from());
        assertEquals(Instant.parse("2023-09-09T00:30:00Z"), segments.get(0).to());
        assertEquals(Instant.parse("2023-09-09T00:30:00Z"), segments.get(1).from());
        assertEquals(Instant.parse("2023-09-09T00:59:00Z"), segments.get(1).to());
    }

}