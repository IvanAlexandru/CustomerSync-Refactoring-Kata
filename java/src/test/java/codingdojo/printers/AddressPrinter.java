package codingdojo.printers;

import codingdojo.dto.AddressDto;
import codingdojo.entities.Address;

public class AddressPrinter {
    public static String printAddress(Address address) {
        if (address == null) {
            return "'null'";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\'");
        sb.append(address.getStreet());
        sb.append(", ");
        sb.append(address.getPostalCode());
        sb.append(" ");
        sb.append(address.getCity());
        sb.append("\'");
        return sb.toString();
    }

    public static String printAddressDto(AddressDto addressDto) {
        if (addressDto == null) {
            return "'null'";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\'");
        sb.append(addressDto.getStreet());
        sb.append(", ");
        sb.append(addressDto.getPostalCode());
        sb.append(" ");
        sb.append(addressDto.getCity());
        sb.append("\'");
        return sb.toString();
    }
}
