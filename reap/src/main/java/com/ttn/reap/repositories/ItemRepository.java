package com.ttn.reap.repositories;

import com.ttn.reap.entities.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends CrudRepository<Item,Integer> {

    List<Item> findAll();

    Optional<Item> findById(Integer id);


}
