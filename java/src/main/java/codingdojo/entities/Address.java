package codingdojo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Address database entity
 */
@Data
@AllArgsConstructor
public class Address {
    private String street;
    private String city;
    private String postalCode;
}
