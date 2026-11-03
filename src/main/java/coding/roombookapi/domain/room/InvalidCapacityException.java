package coding.roombookapi.domain.room;

import coding.roombookapi.domain.shared.DomainException;

public class InvalidCapacityException extends DomainException {
    public InvalidCapacityException(String message) {
        super(message);
    }
}
