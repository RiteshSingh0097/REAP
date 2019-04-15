package com.ttn.reap.services;

import com.ttn.reap.entities.Purchase;
import com.ttn.reap.repositories.PurchasedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchasedService {

    @Autowired
    PurchasedRepository purchasedRepository;

    public void purchasedSave(Purchase purchase){
        purchasedRepository.save(purchase);
    }
}
