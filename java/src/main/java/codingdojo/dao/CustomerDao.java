package codingdojo.dao;

import codingdojo.entities.Customer;

/**
 * Customer data access object
 */
public interface CustomerDao {

    /**
     * Updates customer entity
     * @param customer - customer entity to update
     * @return
     */
    Customer updateCustomer(Customer customer);

    /**
     * Creates customer entity
     * @param customer - customer entity to create
     * @return
     */
    Customer createCustomer(Customer customer);

    /**
     * Find customer entity by externalId field
     * @param externalId - externalId to search by
     * @return
     */
    Customer findByExternalId(String externalId);

    /**
     * Find customer entity by masterExternalId field
     * @param externalId - externalId to search by
     * @return
     */
    Customer findByMasterExternalId(String externalId);

    /**
     * Find customer entity by company number
     * @param companyNumber - companyNumber to search by
     * @return
     */
    Customer findByCompanyNumber(String companyNumber);
}
