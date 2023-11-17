package codingdojo.dto;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * ShoppingList data transfer object
 */
@Data
public class ShoppingListDto {
    private final List<String> products;

    public ShoppingListDto(String... products) {
        this.products = Arrays.asList(products);
    }
}
