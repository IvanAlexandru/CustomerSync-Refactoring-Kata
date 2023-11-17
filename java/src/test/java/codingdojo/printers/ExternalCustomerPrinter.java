package codingdojo.printers;

import codingdojo.dto.CustomerDto;
import codingdojo.printers.AddressPrinter;
import codingdojo.printers.ShoppingListPrinter;

public class ExternalCustomerPrinter {

    public static String print(CustomerDto customerDto, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("ExternalCustomer {");
        sb.append("\n" + indent + "    externalId='" + customerDto.getExternalId() + '\'');
        sb.append("\n" + indent + "    companyNumber='" + customerDto.getCompanyNumber() + '\'' );
        sb.append("\n" + indent + "    name='" + customerDto.getName() + '\'' );
        sb.append("\n" + indent + "    preferredStore='" + customerDto.getPreferredStore() + '\'');
        sb.append("\n" + indent + "    bonusPoints='" + customerDto.getBonusPoints() + '\'');
        sb.append("\n" + indent + "    address=" + AddressPrinter.printAddressDto(customerDto.getAddress()));
        sb.append("\n" + indent + "    shoppingLists=" + ShoppingListPrinter.printShoppingListsDto(customerDto.getShoppingList(), indent + "    ") );
        sb.append("\n" + indent + "}");

        return sb.toString();
    }
}
