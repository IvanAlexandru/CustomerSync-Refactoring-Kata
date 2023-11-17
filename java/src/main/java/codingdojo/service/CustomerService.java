package codingdojo.service;

import codingdojo.entities.CustomerType;
import codingdojo.dao.CustomerDao;
import codingdojo.dto.AddressDto;
import codingdojo.dto.CustomerDto;
import codingdojo.entities.Address;
import codingdojo.entities.Customer;
import codingdojo.entities.ShoppingList;
import codingdojo.exceptions.DifferentExternalIdException;
import codingdojo.exceptions.FieldConstraintException;
import codingdojo.exceptions.WrongCustomerTypeException;
import codingdojo.util.CustomerMatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contains customer business logic
 */
public class CustomerService {

    private static final String EXTERNAL_ID = "ExternalId";
    private static final String COMPANY_NUMBER = "CompanyNumber";
    private static Logger logger = LogManager.getLogger(CustomerService.class);
    private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final CustomerDao customerDao;

    public CustomerService(CustomerDao db) {
        this.customerDao = db;
    }

    /**
     * Create/Update customer entity with the data from the provided customer
     * @param customerDto - customer that holds the information to create/update customer entity with
     */
    public boolean syncWithDataLayer(CustomerDto customerDto) {
        logger.info("Customer sync started for customer {}", customerDto);
        validateCustomer(customerDto);
        CustomerMatch customerMatch = getCustomerMatch(customerDto);
        Customer customer = customerMatch.getCustomer();

        customer = createCustomerIfDoesntExist(customerDto, customer);

        populateFields(customerDto, customer);

        Boolean created = false;
        created = createOrUpdateCustomer(customer, created);

        updateDuplicates(customerDto, customerMatch);

        logger.info("Finished customer syncing!");
        return created;
    }

    /**
     * Validates constraint violations for {@link CustomerDto}
     * @param customerDto - to be validated
     */
    private void validateCustomer(CustomerDto customerDto) {
        logger.info("Validating customer...");
        Set<ConstraintViolation<CustomerDto>> violations = validatorFactory.getValidator().validate(customerDto);
        if(!violations.isEmpty()){
            String violationsMessage = violations.stream().map(v -> v.getPropertyPath() + " " + v.getMessage()).collect(Collectors.joining("\n"));
            throw new FieldConstraintException(violationsMessage);
        }
    }

    /**
     * Create customer if not present in db
     * @param customerDto - customerDto to create customer from
     * @param customer - customer from db
     * @return
     */
    private Customer createCustomerIfDoesntExist(CustomerDto customerDto, Customer customer) {
        if (customer == null) {
            logger.info("Create customer...");
            customer = new Customer();
            customer.setExternalId(customerDto.getExternalId());
            customer.setMasterExternalId(customerDto.getExternalId());
        }
        return customer;
    }

    /**
     * Populate customer fields during create/update operation
     * @param customerDto - customerDto to populate fields drom
     * @param customer - customer being populated
     */
    private void populateFields(CustomerDto customerDto, Customer customer) {
        logger.info("Populate customer fields...");
        customer.setName(customerDto.getName());
        if (customerDto.isCompany()) {
            customer.setCompanyNumber(customerDto.getCompanyNumber());
            customer.setCustomerType(CustomerType.COMPANY);
        } else {
            customer.setCustomerType(CustomerType.PERSON);
            customer.setBonusPoints(customerDto.getBonusPoints());
        }
        customer.setPreferredStore(customerDto.getPreferredStore());
        mapAddress(customerDto, customer);
        updateCustomerShoppingLists(customerDto, customer);
    }

    /**
     * Maps customer address
     * @param customerDto - customer address source
     * @param customer - customer address destination
     */
    private void mapAddress(CustomerDto customerDto, Customer customer) {
        AddressDto addressDto = customerDto.getAddress();
        Address address = new Address(addressDto.getStreet(), addressDto.getCity(), addressDto.getPostalCode());
        customer.setAddress(address);
    }

    /**
     * Update customer duplicates
     * @param customerDto - customerDto from which to update duplicates
     * @param customerMatch - holding the customer duplicates
     */
    private void updateDuplicates(CustomerDto customerDto, CustomerMatch customerMatch) {
        logger.info("Update customer duplicates...");
        if (customerMatch.hasDuplicates()) {
            customerMatch.getDuplicates().forEach(duplicate -> updateDuplicate(customerDto, duplicate));
        }
    }

    /**
     * Update customer duplicate
     * @param customerDto - customerDto from which to update duplicate
     * @param duplicate - customer duplicate
     */
    private void updateDuplicate(CustomerDto customerDto, Customer duplicate) {
        duplicate.setName(customerDto.getName());
        customerDao.updateCustomer(duplicate);
    }

    /**
     * Create/Update customer
     * @param customer - customer to be created/updated
     * @param created - flag that will show if customer was created or not
     */
    private boolean createOrUpdateCustomer(Customer customer, Boolean created) {
        if (customer.getId() == null) {
            logger.info("Create customer entity in db...");
            customerDao.createCustomer(customer);
            created = true;
        } else {
            logger.info("Update customer entity in db...");
            customerDao.updateCustomer(customer);
        }
        return created;
    }

    /**
     * Build {@link CustomerMatch} object with customer info from database
     * @param customerDto - customer to be built from
     */
    private CustomerMatch getCustomerMatch(CustomerDto customerDto) {
        logger.info("Get customer match...");
        CustomerMatch customerMatch;
        if (customerDto.isCompany()) {
            customerMatch = loadCompany(customerDto);
        } else {
            customerMatch = loadPerson(customerDto);
        }
        return customerMatch;
    }

    /**
     * Searches for customer of {@link CustomerType} COMPANY type in the db and builds {@link CustomerMatch} with it, if found
     * @param customerDto - customer to be searched and built from
     */
    private CustomerMatch loadCompany(CustomerDto customerDto) {
        logger.info("Load company...");
        final String externalId = customerDto.getExternalId();
        final String companyNumber = customerDto.getCompanyNumber();

        CustomerMatch customerMatch = loadCompanyCustomer(externalId, companyNumber);

        if (customerMatch.getCustomer() != null && !CustomerType.COMPANY.equals(customerMatch.getCustomer().getCustomerType())) {
            logger.error("Wrong customer type exception thrown for customer with externalId {}. Customer is not a COMPANY", externalId);
            throw new WrongCustomerTypeException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
        }

        if (EXTERNAL_ID.equals(customerMatch.getMatchTerm())) {
            moveToDuplicateIfDiffCompanyNumber(companyNumber, customerMatch);
        } else if (COMPANY_NUMBER.equals(customerMatch.getMatchTerm())) {
            populateExternalIdIfNull(externalId, companyNumber, customerMatch);
        }

        return customerMatch;
    }

    /**
     * Populates externalId and masterExternalId if null
     * Throws exception is customer externalId is different than customerDto externalId
     * @param externalId - customerDto externalId
     * @param companyNumber - customerDto companyNumber
     * @param customerMatch - holding customer related info
     */
    private void populateExternalIdIfNull(String externalId, String companyNumber, CustomerMatch customerMatch) {
        String customerExternalId = customerMatch.getCustomer().getExternalId();
        if (customerExternalId != null && !externalId.equals(customerExternalId)) {
            String exceptionMessage = "Existing customer for externalCustomer " + companyNumber + " doesn't match external id " + externalId + ", instead found " + customerExternalId;
            logger.error(exceptionMessage);
            throw new DifferentExternalIdException(exceptionMessage);
        }
        Customer customer = customerMatch.getCustomer();
        customer.setExternalId(externalId);
        customer.setMasterExternalId(externalId);
    }

    /**
     * Move customer to duplicate if company number is different
     * @param companyNumber - dto company number
     * @param customerMatch - holds customer related info
     */
    private void moveToDuplicateIfDiffCompanyNumber(String companyNumber, CustomerMatch customerMatch) {
        String customerCompanyNumber = customerMatch.getCustomer().getCompanyNumber();
        if (!companyNumber.equals(customerCompanyNumber)) {
            logger.info("Move customer to duplicates because companyNumber {} different from incoming companyNumber {}", customerCompanyNumber, companyNumber);
            customerMatch.addDuplicate(customerMatch.getCustomer());
            customerMatch.setCustomer(null);
        }
    }

    /**
     * Searches for customer of {@link CustomerType} PERSON type in the db and builds {@link CustomerMatch} with it, if found
     * @param customerDto - customer to be searched and built from
     */
    private CustomerMatch loadPerson(CustomerDto customerDto) {
        logger.info("Load person...");
        final String externalId = customerDto.getExternalId();

        CustomerMatch customerMatch = loadPersonCustomer(externalId);

        if (customerMatch.getCustomer() != null) {
            if (!CustomerType.PERSON.equals(customerMatch.getCustomer().getCustomerType())) {
                String exceptionMessage = "Existing customer for externalCustomer " + externalId + " already exists and is not a person";
                logger.info(exceptionMessage);
                throw new WrongCustomerTypeException(exceptionMessage);
            }

            if (!EXTERNAL_ID.equals(customerMatch.getMatchTerm())) {
                logger.info("Update externalId and masterExternalId...");
                Customer customer = customerMatch.getCustomer();
                customer.setExternalId(externalId);
                customer.setMasterExternalId(externalId);
            }
        }

        return customerMatch;
    }

    /**
     * Find customer of {@link CustomerType} COMPANY  by externalId or companyNumber and wrap it in a {@link CustomerMatch}
     * @param externalId - externalId of customer
     * @param companyNumber - companyNumber of customer
     */
    private CustomerMatch loadCompanyCustomer(String externalId, String companyNumber) {
        logger.info("Load company customer...");
        CustomerMatch matches = new CustomerMatch();
        Customer customerMatchByExternalId = this.customerDao.findByExternalId(externalId);
        logger.info("Customer found {}", customerMatchByExternalId);
        if (customerMatchByExternalId != null) {
            logger.info("Customer match by externalId");
            matches.setCustomer(customerMatchByExternalId);
            matches.setMatchTerm(EXTERNAL_ID);
            Customer matchByMasterId = this.customerDao.findByMasterExternalId(externalId);
            if (matchByMasterId != null) matches.addDuplicate(matchByMasterId);
        } else {
            logger.info("Customer match by companyNumber");
            Customer matchByCompanyNumber = this.customerDao.findByCompanyNumber(companyNumber);
            if (matchByCompanyNumber != null) {
                matches.setCustomer(matchByCompanyNumber);
                matches.setMatchTerm(COMPANY_NUMBER);
            }
        }

        return matches;
    }

    /**
     * Find customer of {@link CustomerType} PERSON  by externalId or companyNumber and wrap it in a {@link CustomerMatch}
     * @param externalId - externalId of customer
     */
    private CustomerMatch loadPersonCustomer(String externalId) {
        logger.info("Load person customer...");
        CustomerMatch matches = new CustomerMatch();
        Customer matchByExternalId = this.customerDao.findByExternalId(externalId);
        logger.info("Customer found {}", matchByExternalId);
        matches.setCustomer(matchByExternalId);
        if (matchByExternalId != null) matches.setMatchTerm(EXTERNAL_ID);
        return matches;
    }

    /**
     * Update customer shopping lists
     * @param customerDto - customer to get new list of shoppingLists from
     * @param customer - customer to be updated
     */
    private void updateCustomerShoppingLists(CustomerDto customerDto, Customer customer) {
        List<ShoppingList> customerShoppingLists = customerDto.getShoppingList().stream()
                .map(shoppingListDto -> new ShoppingList(shoppingListDto.getProducts()))
                .collect(Collectors.toList());
        customerShoppingLists.forEach(customer::addShoppingList);
    }
}
