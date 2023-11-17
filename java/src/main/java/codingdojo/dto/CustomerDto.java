package codingdojo.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * Customer data transfer object
 */
@Data
public class CustomerDto {
    private AddressDto address;
    @NotBlank
    private String name;
    private String preferredStore;
    private List<ShoppingListDto> shoppingList;
    @NotBlank
    private String externalId;
    private String companyNumber;
    private int bonusPoints;

    public boolean isCompany() {
        return companyNumber != null;
    }
}
