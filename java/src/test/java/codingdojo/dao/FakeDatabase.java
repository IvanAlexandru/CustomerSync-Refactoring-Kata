package codingdojo.dao;

import codingdojo.dao.CustomerDao;
import codingdojo.entities.Customer;
import codingdojo.entities.ShoppingList;
import codingdojo.printers.CustomerPrinter;
import codingdojo.printers.ShoppingListPrinter;

import java.util.*;

/**
 * Fake implementation of data layer that stores data in-memory
 */
public class FakeDatabase implements CustomerDao {

    private final HashMap<String, Customer> customersByExternalId = new HashMap<String, Customer>();
    private final HashMap<String, Customer> customersByMasterExternalId = new HashMap<String, Customer>();
    private final HashMap<String, Customer> customersByCompanyNumber = new HashMap<String, Customer>();
    private final Set<ShoppingList> shoppingLists = new HashSet<>();


    public void addCustomer(Customer customer) {
        if (customer.getExternalId() != null) {
            this.customersByExternalId.put(customer.getExternalId(), customer);
        }
        if (customer.getMasterExternalId() != null) {
            this.customersByMasterExternalId.put(customer.getMasterExternalId(), customer);
        }
        if (customer.getCompanyNumber() != null) {
            this.customersByCompanyNumber.put(customer.getCompanyNumber(), customer);
        }
        if (customer.getShoppingLists() != null) {
            shoppingLists.addAll(customer.getShoppingLists());
        }
    }

    @Override
    public Customer update(Customer customer) {
        this.addCustomer(customer);
        return customer;
    }

    @Override
    public List<Customer> updateAll(List<Customer> customers) {
        for(Customer customer : customers) {
            this.addCustomer(customer);
        }
        return customers;
    }

    @Override
    public Customer create(Customer customer) {
        String internalId = "fake internalId";
        customer.setId(internalId);
        addCustomer(customer);
        return customer;
    }

    @Override
    public Customer findByExternalId(String externalId) {
        return this.customersByExternalId.get(externalId);
    }

    @Override
    public Customer findByMasterExternalId(String masterExternalId) {
        return this.customersByMasterExternalId.get(masterExternalId);
    }

    @Override
    public Customer findByCompanyNumber(String companyNumber) {
        return this.customersByCompanyNumber.get(companyNumber);
    }

    public List<Customer> getAllCustomers() {
        Set<Customer> allCustomers = new HashSet<Customer>(customersByExternalId.values());
        allCustomers.addAll(customersByMasterExternalId.values());
        allCustomers.addAll(customersByCompanyNumber.values());
        ArrayList<Customer> sortedList = new ArrayList<Customer>(allCustomers);
        sortedList.sort((o1, o2) -> Comparator.comparing(Customer::getId)
                .thenComparing(Customer::getName)
                .compare(o1, o2));
        return sortedList;
    }

    public String printContents() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fake Database.\nAll Customers {\n");
        for (Customer customer : getAllCustomers()) {
            sb.append(CustomerPrinter.print(customer, "    "));
            sb.append("\n");
        }

        sb.append("\n}");
        sb.append("\nAll Shopping Lists\n");
        List<ShoppingList> sortedShoppingLists = new ArrayList<>(shoppingLists);
        sortedShoppingLists.sort(Comparator.comparing(o -> o.getProducts().toString()));
        sb.append(ShoppingListPrinter.printShoppingLists(sortedShoppingLists, ""));
        return sb.toString();
    }
}
