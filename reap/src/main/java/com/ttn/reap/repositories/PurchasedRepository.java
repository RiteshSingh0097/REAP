package com.ttn.reap.repositories;

import com.ttn.reap.entities.Purchase;
import org.springframework.data.repository.CrudRepository;

public interface PurchasedRepository extends CrudRepository<Purchase, Integer> {

}
