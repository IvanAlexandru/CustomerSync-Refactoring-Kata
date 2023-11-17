package codingdojo.exceptions;

/**
 * Occurs when externalId of customerDto is different than the one of the customer entity
 */
public class DifferentExternalIdException extends RuntimeException {
    public DifferentExternalIdException(String s) {
        super(s);
    }
}
