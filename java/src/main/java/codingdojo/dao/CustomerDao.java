package codingdojo.dao;

import codingdojo.entities.Customer;

import java.util.List;

/**
 * Customer data access object
 */
public interface CustomerDao {

    /**
     * Updates customer entity
     * @param customer - customer entity to update
     * @return
     */
    Customer update(Customer customer);

    /**
     * Updates a list if customer entities
     * @param customers - customer entities to update
     * @return
     */
    List<Customer> updateAll(List<Customer> customers);

    /**
     * Creates customer entity
     * @param customer - customer entity to create
     * @return
     */
    Customer create(Customer customer);

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
