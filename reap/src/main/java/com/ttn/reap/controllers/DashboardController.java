package com.ttn.reap.controllers;

import com.ttn.reap.entities.Recognition;
import com.ttn.reap.entities.Role;
import com.ttn.reap.entities.User;
import com.ttn.reap.exceptions.UserNotFoundException;
import com.ttn.reap.services.RecognitionService;
import com.ttn.reap.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class DashboardController {

    @Autowired
    UserService userService;

    @Autowired
    RecognitionService recognitionService;

    @GetMapping("/users/{id}")
    public ModelAndView getUser(@PathVariable Integer id, HttpServletRequest httpServletRequest,
                                RedirectAttributes redirectAttributes) {
        HttpSession httpSession = httpServletRequest.getSession();
        User activeUser = (User) httpSession.getAttribute("activeUser");

        try {
            if (id != activeUser.getUserId()){
                ModelAndView modelAndView = new ModelAndView("redirect:/");
                redirectAttributes.addFlashAttribute("error","Please login to continue");
                return modelAndView;
            }
        }catch (NullPointerException ne){
            ModelAndView modelAndView = new ModelAndView("redirect:/");
            redirectAttributes.addFlashAttribute("error","Please login to continue");
            return modelAndView;
        }

        Optional<User> optionalUser = userService.getUser(id);
        if (!optionalUser.isPresent())
            throw new UserNotFoundException("No user with id " + id);
        ModelAndView modelAndView = new ModelAndView("dashboard");
        modelAndView.addObject("user", optionalUser.get());
        modelAndView.addObject("recognition", new Recognition());
        modelAndView.addObject("recognitionSearch", new RecognitionService());
        List<Recognition> recognitionList = recognitionService.getListOfRecognition();
        Collections.reverse(recognitionList);
        modelAndView.addObject("recognitionList",recognitionList);
        Map<String, List<Integer>> recognizedUserRedeemableBadges = new LinkedHashMap<>();
        Integer recognizedUserGold, recognizedUserSilver, recognizedUserBronze;
        for (Recognition recognition : recognitionList){
            User recognizedUser = userService.getUserByFullName(recognition.getReceiverName());

            recognizedUserGold = recognizedUser.getGoldRedeemable();
            recognizedUserSilver  = recognizedUser.getSilverRedeemable();
            recognizedUserBronze = recognizedUser.getBronzeRedeemable();
            recognizedUserRedeemableBadges.put(recognizedUser.getFullName(), Arrays.asList(recognizedUserGold, recognizedUserSilver, recognizedUserBronze));
        }
        modelAndView.addObject("recognizedUserRedeemableBadges", recognizedUserRedeemableBadges);
        boolean isAdmin = optionalUser.get().getRoleSet().contains(Role.ADMIN);

        if (isAdmin){
            modelAndView.addObject("isAdmin",isAdmin);
            List<User> userList = userService.getUserList();
            modelAndView.addObject("users",userList);
        }
        return modelAndView;
    }
}
