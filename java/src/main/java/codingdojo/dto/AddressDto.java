package codingdojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Address data transfer object
 */
@Data
@AllArgsConstructor
public class AddressDto {
    private String street;
    private String city;
    private String postalCode;
}
