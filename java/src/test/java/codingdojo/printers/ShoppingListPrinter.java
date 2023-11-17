package codingdojo.printers;

import codingdojo.dto.ShoppingListDto;
import codingdojo.entities.ShoppingList;

import java.util.List;

public class ShoppingListPrinter {

    public static String printShoppingLists(List<ShoppingList> shoppingLists, String indent) {
        if (shoppingLists.size() == 0) {
            return "[]";
        }
        if (shoppingLists.size() == 1) {
            ShoppingList shoppingList = shoppingLists.get(0);
            return "[" + printShoppingList(shoppingList) + "]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (ShoppingList shoppingList : shoppingLists) {
            sb.append("\n    " + indent);
            sb.append(printShoppingList(shoppingList));

        }
        sb.append("\n" + indent + "]");
        return sb.toString();
    }

    private static String printShoppingList(ShoppingList shoppingList) {
        return shoppingList.getProducts().toString();
    }

    public static String printShoppingListsDto(List<ShoppingListDto> shoppingListsDto, String indent) {
        if (shoppingListsDto.size() == 0) {
            return "[]";
        }
        if (shoppingListsDto.size() == 1) {
            ShoppingListDto shoppingListDto = shoppingListsDto.get(0);
            return "[" + printShoppingListDto(shoppingListDto) + "]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (ShoppingListDto shoppingListDto : shoppingListsDto) {
            sb.append("\n    " + indent);
            sb.append(printShoppingListDto(shoppingListDto));

        }
        sb.append("\n" + indent + "]");
        return sb.toString();
    }

    private static String printShoppingListDto(ShoppingListDto shoppingListDto) {
        return shoppingListDto.getProducts().toString();
    }
}
