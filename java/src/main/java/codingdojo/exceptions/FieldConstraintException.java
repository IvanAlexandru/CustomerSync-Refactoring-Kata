package codingdojo.exceptions;

/**
 * Occurs when {@link codingdojo.dto.CustomerDto} fails field validation
 */
public class FieldConstraintException extends RuntimeException {
    public FieldConstraintException(String s) {
        super(s);
    }
}
