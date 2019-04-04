package com.ttn.reap.controllers;

import com.ttn.reap.entities.User;
import com.ttn.reap.exceptions.UserNotFoundException;
import com.ttn.reap.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    
    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "/home/ttn/Desktop/reap/src/main/resources/static/user-images";


    @PostMapping("/users")
    public ModelAndView createNewUser(@Valid @ModelAttribute User user,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("index");
        } else {
            try {
                Path path = Paths.get(UPLOADED_FOLDER, user.getFirstName()+".jpg");
                path = Files.write(path, user.getPhoto().getBytes());
                System.out.println(path);
                user.setPhotoPath(path.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            userService.save(user);

            System.out.println("User saved: " + user.toString());
            return new ModelAndView("redirect:/users/" + user.getId());
        }
    }


    @GetMapping("/users/{id}")
    public ModelAndView getUser(@PathVariable Integer id) {
        Optional<User> optionalUser = userService.getUser(id);
        if (!optionalUser.isPresent())
            throw new UserNotFoundException("No user with id " + id);
        ModelAndView modelAndView = new ModelAndView("dashboard");
        modelAndView.addObject("user", optionalUser.get());
        return modelAndView;
    }

    
    @PutMapping("/users/{id}")
    public User editUser(@PathVariable Integer id, @RequestBody @Valid User user) {
        Optional<User> userOptional = userService.getUser(id);
        if (userOptional.isPresent())
            return userService.save(user);
        else throw new UserNotFoundException("No user with id " + user);
    }

    @GetMapping("/chillax")
    public String adminpanel(){
        return "adminpanel";
    }


    @GetMapping("/users")
    @ResponseBody
    List<User> getUserList() {
        return userService.getUserList();
    }
}
