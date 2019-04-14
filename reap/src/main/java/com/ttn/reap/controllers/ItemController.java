package com.ttn.reap.controllers;

import com.ttn.reap.entities.Item;
import com.ttn.reap.entities.User;
import com.ttn.reap.exceptions.UserNotFoundException;
import com.ttn.reap.services.ItemService;
import com.ttn.reap.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@Controller
public class ItemController {
    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    User user;

    @RequestMapping("/items/{id}")
    public ModelAndView itemList(@PathVariable("id") Integer id, HttpServletRequest request, RedirectAttributes redirectAttributes){

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("activeUser");
        try {
            if (id != user.getUserId()){
                ModelAndView modelAndView = new ModelAndView("redirect:/");
                redirectAttributes.addFlashAttribute("error","Please login to continue");
                return modelAndView;
            }
        }catch (NullPointerException ne){
            ModelAndView modelAndView = new ModelAndView("redirect:/");
            redirectAttributes.addFlashAttribute("error","Please login to continue");
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView("items");
        modelAndView.addObject("user",user);
        modelAndView.addObject("itemList",itemService.findAllItems());
        return modelAndView;
    }

    @GetMapping("/checkAvailability/{id}")
    public ModelAndView check(@PathVariable Integer id, Model model){
       Optional<Item> item = itemService.findItem(1);
       if (!item.isPresent()/*item.get().getPointsWorth()>user.p*/){
           model.addAttribute("error","Purchased failed");
       }
       else {
           model.addAttribute("success","Purchased done");
       }
       ModelAndView modelAndView = new ModelAndView("redirect:/items/" + user.getUserId());
        return modelAndView;
    }
}
