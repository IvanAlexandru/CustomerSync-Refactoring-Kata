package codingdojo.util;

import codingdojo.entities.Customer;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link Customer}mer wrapper class containing additional info as well
 */
@Data
public class CustomerMatch {
    private List<Customer> duplicates = new ArrayList<>();
    private String matchTerm;
    private Customer customer;

    /**
     * Returns true if user has duplicates, false otherwise
     */
    public boolean hasDuplicates() {
        return !duplicates.isEmpty();
    }

    /**
     * Add duplicate to duplicates list
     * @param duplicate - customer duplicate to be added
     */
    public void addDuplicate(Customer duplicate) {
        duplicates.add(duplicate);
    }
}
