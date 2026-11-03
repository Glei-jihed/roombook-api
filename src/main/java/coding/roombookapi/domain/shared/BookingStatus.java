package coding.roombookapi.domain.shared;

public enum BookingStatus {
    CONFIRMED,
    CANCELLED;

    public boolean canBeCancelled() {
        return this != CANCELLED;
    }

    public boolean canBeConfirmed() {
        return !this.canBeConfirmed();
    }
}
