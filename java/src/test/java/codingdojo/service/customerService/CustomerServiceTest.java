package codingdojo.service.customerService;

import codingdojo.dao.FakeDatabase;
import codingdojo.entities.CustomerType;
import codingdojo.dto.AddressDto;
import codingdojo.dto.CustomerDto;
import codingdojo.dto.ShoppingListDto;
import codingdojo.entities.Customer;
import codingdojo.entities.ShoppingList;
import codingdojo.exceptions.DifferentExternalIdException;
import codingdojo.exceptions.FieldConstraintException;
import codingdojo.exceptions.WrongCustomerTypeException;
import codingdojo.printers.ExternalCustomerPrinter;
import codingdojo.service.CustomerService;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CustomerServiceTest {

    @Test
    public void syncCompanyByExternalId(){
        String externalId = "12345";

        CustomerDto customerDto = createExternalCompany();
        customerDto.setExternalId(externalId);

        Customer customer = createCustomerWithSameCompanyAs(customerDto);
        customer.setExternalId(externalId);

        FakeDatabase db = new FakeDatabase();
        db.addCustomer(customer);
        CustomerService customerService = new CustomerService(db);

        StringBuilder toAssert = printBeforeState(customerDto, db);

        // ACT
        boolean created = customerService.syncWithDataLayer(customerDto);

        assertFalse(created);
        printAfterState(db, toAssert);
        Approvals.verify(toAssert);
    }

    @Test
    public void syncPrivatePersonByExternalId(){
        String externalId = "12345";

        CustomerDto customerDto = createExternalPrivatePerson();
        customerDto.setExternalId(externalId);

        Customer customer = new Customer();
        customer.setCustomerType(CustomerType.PERSON);
        customer.setId("67576");
        customer.setExternalId(externalId);

        FakeDatabase db = new FakeDatabase();
        db.addCustomer(customer);
        CustomerService customerService = new CustomerService(db);

        StringBuilder toAssert = printBeforeState(customerDto, db);

        // ACT
        boolean created = customerService.syncWithDataLayer(customerDto);

        assertFalse(created);
        printAfterState(db, toAssert);
        Approvals.verify(toAssert);
    }

    @Test
    public void syncShoppingLists(){
        String externalId = "12345";

        CustomerDto customerDto = createExternalCompany();
        customerDto.setExternalId(externalId);

        Customer customer = createCustomerWithSameCompanyAs(customerDto);
        customer.setExternalId(externalId);
        customer.addShoppingList(new ShoppingList(Arrays.asList("eyeliner", "blusher")));

        FakeDatabase db = new FakeDatabase();
        db.addCustomer(customer);
        CustomerService customerService = new CustomerService(db);

        StringBuilder toAssert = printBeforeState(customerDto, db);

        // ACT
        boolean created = customerService.syncWithDataLayer(customerDto);

        assertFalse(created);
        printAfterState(db, toAssert);
        Approvals.verify(toAssert);
    }

    @Test
    public void syncNewCompanyCustomer(){

        CustomerDto customerDto = createExternalCompany();
        customerDto.setExternalId("12345");

        FakeDatabase db = new FakeDatabase();
        CustomerService customerService = new CustomerService(db);

        StringBuilder toAssert = printBeforeState(customerDto, db);

        // ACT
        boolean created = customerService.syncWithDataLayer(customerDto);

        assertTrue(created);
        printAfterState(db, toAssert);
        Approvals.verify(toAssert);
    }

    @Test
    public void syncNewPrivateCustomer(){

        CustomerDto customerDto = createExternalPrivatePerson();
        customerDto.setExternalId("12345");

        FakeDatabase db = new FakeDatabase();
        CustomerService customerService = new CustomerService(db);

        StringBuilder toAssert = printBeforeState(customerDto, db);

        // ACT
        boolean created = customerService.syncWithDataLayer(customerDto);

        assertTrue(created);
        printAfterState(db, toAssert);
        Approvals.verify(toAssert);
    }

    @Test
    public void wrongCustomerTypeExceptionWhenExistingCustomerIsPerson() {
        String externalId = "12345";

        CustomerDto customerDto = createExternalCompany();
        customerDto.setExternalId(externalId);

        Customer customer = new Customer();
        customer.setCustomerType(CustomerType.PERSON);
        customer.setId("45435");
        customer.setExternalId(externalId);

        FakeDatabase db = new FakeDatabase();
        db.addCustomer(customer);
        CustomerService customerService = new CustomerService(db);

        StringBuilder toAssert = printBeforeState(customerDto, db);

        Assertions.assertThrows(WrongCustomerTypeException.class, () -> {
            customerService.syncWithDataLayer(customerDto);
        }, printAfterState(db, toAssert).toString());

        Approvals.verify(toAssert);
    }

    @Test
    public void syncByExternalIdButCompanyNumbersConflict(){
        String externalId = "12345";

        CustomerDto customerDto = createExternalCompany();
        customerDto.setExternalId(externalId);

        Customer customer = createCustomerWithSameCompanyAs(customerDto);
        customer.setExternalId(externalId);
        customer.setCompanyNumber("000-3234");

        FakeDatabase db = new FakeDatabase();
        db.addCustomer(customer);
        CustomerService customerService = new CustomerService(db);

        StringBuilder toAssert = printBeforeState(customerDto, db);

        // ACT
        boolean created = customerService.syncWithDataLayer(customerDto);

        assertTrue(created);
        printAfterState(db, toAssert);
        Approvals.verify(toAssert);
    }


    @Test
    public void syncByCompanyNumber(){
        String companyNumber = "12345";

        CustomerDto customerDto = createExternalCompany();
        customerDto.setCompanyNumber(companyNumber);

        Customer customer = createCustomerWithSameCompanyAs(customerDto);
        customer.setCompanyNumber(companyNumber);
        customer.addShoppingList(new ShoppingList(Arrays.asList("eyeliner", "mascara", "blue bombe eyeshadow")));

        FakeDatabase db = new FakeDatabase();
        db.addCustomer(customer);
        CustomerService customerService = new CustomerService(db);

        StringBuilder toAssert = printBeforeState(customerDto, db);

        // ACT
        boolean created = customerService.syncWithDataLayer(customerDto);

        assertFalse(created);
        printAfterState(db, toAssert);
        Approvals.verify(toAssert);
    }

    @Test
    public void syncByCompanyNumberWithConflictingExternalId(){
        String companyNumber = "12345";

        CustomerDto customerDto = createExternalCompany();
        customerDto.setCompanyNumber(companyNumber);
        customerDto.setExternalId("45646");

        Customer customer = createCustomerWithSameCompanyAs(customerDto);
        customer.setCompanyNumber(companyNumber);
        customer.setExternalId("conflicting id");

        FakeDatabase db = new FakeDatabase();
        db.addCustomer(customer);
        CustomerService customerService = new CustomerService(db);

        StringBuilder toAssert = printBeforeState(customerDto, db);

        // ACT
        Assertions.assertThrows(DifferentExternalIdException.class, () -> {
            customerService.syncWithDataLayer(customerDto);
        }, printAfterState(db, toAssert).toString());

        Approvals.verify(toAssert);
    }

    @Test
    public void wrongCustomerTypeExceptionWhenExistingCustomerIsCompany() {
        String externalId = "12345";

        CustomerDto customerDto = createExternalPrivatePerson();
        customerDto.setExternalId(externalId);

        Customer customer = new Customer();
        customer.setCustomerType(CustomerType.COMPANY);
        customer.setCompanyNumber("32423-342");
        customer.setId("45435");
        customer.setExternalId(externalId);

        FakeDatabase db = new FakeDatabase();
        db.addCustomer(customer);
        CustomerService customerService = new CustomerService(db);

        StringBuilder toAssert = printBeforeState(customerDto, db);

        Assertions.assertThrows(WrongCustomerTypeException.class, () -> {
            customerService.syncWithDataLayer(customerDto);
        }, printAfterState(db, toAssert).toString());

        Approvals.verify(toAssert);
    }

    @Test
    public void syncCompanyByExternalIdWithNonMatchingMasterId(){
        String externalId = "12345";

        CustomerDto customerDto = createExternalCompany();
        customerDto.setExternalId(externalId);

        Customer customer = createCustomerWithSameCompanyAs(customerDto);
        customer.setExternalId(externalId);
        customer.setName("company 1");

        Customer customer2 = new Customer();
        customer2.setCompanyNumber(customerDto.getCompanyNumber());
        customer2.setCustomerType(CustomerType.COMPANY);
        customer2.setId("45435234");
        customer2.setMasterExternalId(externalId);
        customer2.setName("company 2");

        FakeDatabase db = new FakeDatabase();
        db.addCustomer(customer);
        db.addCustomer(customer2);
        CustomerService customerService = new CustomerService(db);

        StringBuilder toAssert = printBeforeState(customerDto, db);

        // ACT
        boolean created = customerService.syncWithDataLayer(customerDto);

        assertFalse(created);
        printAfterState(db, toAssert);
        Approvals.verify(toAssert);
    }

    @Test
    public void fieldConstraintExceptionName() {
        CustomerDto customerDto = createExternalCompany();
        customerDto.setName("");

        Customer customer = new Customer();
        customer.setCustomerType(CustomerType.PERSON);
        customer.setExternalId("12345");
        customer.setId("45435");

        FakeDatabase db = new FakeDatabase();
        db.addCustomer(customer);
        CustomerService customerService = new CustomerService(db);

        StringBuilder toAssert = printBeforeState(customerDto, db);

        Assertions.assertThrows(FieldConstraintException.class, () -> {
            customerService.syncWithDataLayer(customerDto);
        }, printAfterState(db, toAssert).toString());

        Approvals.verify(toAssert);
    }

    private CustomerDto createExternalPrivatePerson() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setExternalId("12345");
        customerDto.setName("Joe Bloggs");
        customerDto.setAddress(new AddressDto("123 main st", "Stockholm", "SE-123 45"));
        customerDto.setPreferredStore("Nordstan");
        customerDto.setBonusPoints(50);
        customerDto.setShoppingList(Arrays.asList(new ShoppingListDto("lipstick", "foundation")));
        return customerDto;
    }


    private CustomerDto createExternalCompany() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setExternalId("12345");
        customerDto.setName("Acme Inc.");
        customerDto.setAddress(new AddressDto("123 main st", "Helsingborg", "SE-123 45"));
        customerDto.setCompanyNumber("470813-8895");
        customerDto.setShoppingList(Arrays.asList(new ShoppingListDto("lipstick", "blusher")));
        return customerDto;
    }

    private Customer createCustomerWithSameCompanyAs(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setCompanyNumber(customerDto.getCompanyNumber());
        customer.setCustomerType(CustomerType.COMPANY);
        customer.setId("45435");
        return customer;
    }

    private StringBuilder printBeforeState(CustomerDto customerDto, FakeDatabase db) {
        StringBuilder toAssert = new StringBuilder();
        toAssert.append("BEFORE:\n");
        toAssert.append(db.printContents());

        toAssert.append("\nSYNCING THIS:\n");
        toAssert.append(ExternalCustomerPrinter.print(customerDto, ""));
        return toAssert;
    }

    private StringBuilder printAfterState(FakeDatabase db, StringBuilder toAssert) {
        toAssert.append("\nAFTER:\n");
        toAssert.append(db.printContents());
        return toAssert;
    }
}
