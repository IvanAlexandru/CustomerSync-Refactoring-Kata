package codingdojo.exceptions;

import codingdojo.entities.CustomerType;

/**
 * Occurs when {@link CustomerType} of customerDto is different than the one of the customer entity
 */
public class WrongCustomerTypeException extends RuntimeException {
    public WrongCustomerTypeException(String s) {
        super(s);
    }
}
