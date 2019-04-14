package com.ttn.reap.component;

import com.ttn.reap.entities.*;
import com.ttn.reap.repositories.UserRepository;
import com.ttn.reap.services.ItemService;
import com.ttn.reap.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class Bootstrap {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    ItemService itemService;
    
    @EventListener(ApplicationStartedEvent.class)
    void setUp() {
        if (!userRepository.findAll().iterator().hasNext()) {

            User admin = new User();
            admin.setEmail("ritesh.singh@tothenew.com");
            admin.setFirstName("Ritesh");
            admin.setLastName("Singh");
            admin.setFullName(admin.getFirstName()+" "+admin.getLastName());
            admin.setPhotoPath("/user-images/shivam32@gmail.com.jpg");
            admin.setPassword("225415");
            admin.getRoleSet().add(Role.ADMIN);

            userService.save(admin);

            Item item1 = new Item("/images/items/tshirt.jpg","T-Shirt",100,50);
            itemService.saveItem(item1);


            Item item2 = new Item("/images/items/cap.jpg","Cap",70,100);
            itemService.saveItem(item2);

            Item item3 = new Item("/images/items/backpack.jpg","Backpack",120,55);
            itemService.saveItem(item3);

            Item item4 = new Item("/images/items/bottle.png","Bottle",75,85);
            itemService.saveItem(item4);

            Item item5 = new Item("/images/items/easy-button.jpg","Easy Button",155,22);
            itemService.saveItem(item5);

            Item item6 = new Item("/images/items/keychain.jpg","Keychain",30,100);
            itemService.saveItem(item6);

            Item item7 = new Item("/images/items/notebook.jpg","Spiral Notebook + Pen Set",40,80);
            itemService.saveItem(item7);

            Item item8 = new Item("/images/items/passport-wallet.jpg","Passport/Travel Wallet",125,30);
            itemService.saveItem(item8);

            Item item9 = new Item("/images/items/stationery-organizer.jpg","Stationery Organizer",50,50);
            itemService.saveItem(item9);

            Item item10 = new Item("/images/items/power-bank.jpeg","Belkin 10000 mAh Power Bank",200,60);
            itemService.saveItem(item10);
        }
    }
}
