package coding.roombookapi.domain.booking;

import coding.roombookapi.domain.shared.DomainException;

public class InvalidTimeSlotException extends DomainException {
    public InvalidTimeSlotException(String message) {
        super(message);
    }
}