package codingdojo.entities;

import lombok.Data;

import java.util.List;

/**
 * ShoppingList database entity
 */
@Data
public class ShoppingList {
    private final List<String> products;

    public ShoppingList(List<String> products) {
        this.products = products;
    }
}
