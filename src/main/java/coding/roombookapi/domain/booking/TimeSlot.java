package coding.roombookapi.domain.booking;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public record TimeSlot(LocalDateTime start, LocalDateTime end) {
    private static final Duration MAX_DURATION = Duration.ofHours(4);

    public TimeSlot {
        Objects.requireNonNull(start, "Start duration cannot be null");
        Objects.requireNonNull(end, " End duration cannot be null");

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if(Duration.between(start, end).compareTo(MAX_DURATION) > 0) {
            throw new IllegalArgumentException("Time slot must not exceed " + MAX_DURATION.toHours() + " hours");
        }
    }

    public static TimeSlot startingAt(LocalDateTime start, Duration duration) {
        return new TimeSlot(start, start.plus(duration));
    }

    public boolean overlaps(TimeSlot other) {
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }

    public boolean hasStartedAt(LocalDateTime date) {
        return this.start.isBefore(date);
    }

    public Duration getDuration() {
        return Duration.between(start, end);
    }
}
