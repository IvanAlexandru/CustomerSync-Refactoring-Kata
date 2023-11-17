package codingdojo.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Customer database entity
 */
@Data
public class Customer {
    private String id;
    private String externalId;
    private String masterExternalId;
    private Address address;
    private String preferredStore;
    private List<ShoppingList> shoppingLists = new ArrayList<>();
    private String name;
    private CustomerType customerType;
    private String companyNumber;
    private int bonusPoints;

    /**
     * Add shopping list to already existing list of shopping lists
     * @param customerShoppingList - list to be added
     */
    public void addShoppingList(ShoppingList customerShoppingList) {
        shoppingLists.add(customerShoppingList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(externalId, customer.externalId) &&
                Objects.equals(masterExternalId, customer.masterExternalId) &&
                Objects.equals(companyNumber, customer.companyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId, masterExternalId, companyNumber);
    }
}
