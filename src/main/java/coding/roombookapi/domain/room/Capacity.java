package coding.roombookapi.domain.room;

import java.util.Objects;

public record Capacity(int capacityValue) {

    private static final int MIN_CAPACITY = 1;
    private static final int MAX_CAPACITY = 200;

    public Capacity {
        Objects.requireNonNull(capacityValue, "room capacity cannot be null");
        if (capacityValue < MIN_CAPACITY || capacityValue > MAX_CAPACITY) {
            throw new IllegalArgumentException("capacity must be between %d and %d, got :%d".formatted(MIN_CAPACITY, MAX_CAPACITY, capacityValue));
        }
    }

    public boolean canHost(int number) {
        return number >= 1 && number <= 200;
    }
}
