package com.ttn.reap.controllers;

import com.ttn.reap.entities.Role;
import com.ttn.reap.entities.User;
import com.ttn.reap.exceptions.UserNotFoundException;
import com.ttn.reap.services.EmailService;
import com.ttn.reap.services.RecognitionService;
import com.ttn.reap.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class UserController {

    private static String UPLOADED_FOLDER = "/home/ttn/Desktop/reap/out/production/resources/static/user-images/";

    @Autowired
    UserService userService;

    @Autowired
    RecognitionService recognitionService;

    @Autowired
    EmailService emailService;

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("newUser", new User());
        return modelAndView;
    }

    @PostMapping("/users")
    public ModelAndView createNewUser(@Valid @ModelAttribute("newUser") User user,
                                      BindingResult bindingResult, MultipartFile file,
                                      HttpServletRequest request, RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()) {
            return new ModelAndView("index");
        }

        else {

            if (userService.findByEmail(user.getEmail()).isPresent()) {
                ModelAndView modelAndView = new ModelAndView("redirect:/");
                redirectAttributes.addFlashAttribute("error", "Email already in use");
                return modelAndView;
            }

        }
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("activeUser", user);
        try {
            String userImage = UUID.randomUUID().toString() + ".jpg";
            Path path = Paths.get(UPLOADED_FOLDER, userImage);
            path = Files.write(path, user.getPhoto().getBytes());
            user.setPhotoPath("/user-images/" + userImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userService.save(user);
        return new ModelAndView("redirect:/users/" + user.getUserId());
    }


    @PostMapping("/login")
    public ModelAndView loginUser(@RequestParam("email") String email, @RequestParam("password") String password,
                                  HttpServletRequest httpServletRequest,
                                  RedirectAttributes redirectAttributes) {
        Optional<User> user= userService.findUserByEmailAndPassword(email,password);

        if (!user.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("redirect:/");
            redirectAttributes.addFlashAttribute("error","Invalid credentials");
            return modelAndView;
        }
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute("activeUser",user.get());
        return new ModelAndView("redirect:/users/" + user.get().getUserId());
    }

    @PostMapping("/setNewPassword")
    public String setNewPassword(@RequestParam("email") String email, Model model, HttpServletRequest request){
        Optional<User> user = userService.findByEmail(email);

        if (!user.isPresent()){
            model.addAttribute("error","We didn't find an account for that e-mail address.");
        }else {
            user.get().setResetToken((UUID.randomUUID().toString()));
            userService.save(user.get());

            String appUrl = request.getScheme() + "://" + request.getServerName();
            emailService.sendMail(user.get(),appUrl);
            model.addAttribute("newUser",user);
            model.addAttribute("success","A password reset link has been sent to "+user.get().getEmail());
        }
        return "index";
    }

    @GetMapping("/reset")
    public String reset(@RequestParam("token") String token, Model model){
        System.out.println(token);
       Optional<User> optionalUser = userService.findByResetToken(token);
       if (!optionalUser.isPresent()){
           model.addAttribute("error","Sorry token not found.");
           model.addAttribute("user",new User());
           return "index";
       }
        User user = new User();
        user.setResetToken(token);
       model.addAttribute("user",user);
       return "passwordreset";
    }



    @PostMapping("/logout")
    public ModelAndView logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
        return new ModelAndView("redirect:/");
    }

    @PostMapping("/updatePass")
    public String updatePassword(@RequestParam("token") String token, @RequestParam("password") String password, Model model){
        Optional<User> optionalUser = userService.findByResetToken(token);

        if (!optionalUser.isPresent()) {
            model.addAttribute("error", "Password reset failed");
            return "index";
        }
        optionalUser.get().setPassword(password);
        optionalUser.get().setResetToken(null);
        userService.save(optionalUser.get());
        model.addAttribute("newUser", optionalUser.get());
        model.addAttribute("success","Password reset successful");

        return "index";
    }

    @PutMapping("/users/{id}")
    public ModelAndView editUser(@PathVariable Integer id,
                                 @RequestParam Map<String, String> requestParams,
                                 HttpServletRequest httpServletRequest){
        HttpSession httpSession = httpServletRequest.getSession();
        User activeUser = (User) httpSession.getAttribute("activeUser");
        if (httpSession == null){
            throw new RuntimeException("Unauthorized modification of user");
        }

        Optional<User> user = userService.getUser(id);
        if (!user.isPresent())
            throw new UserNotFoundException("No user found");

        Set<Role> userRoleSet = user.get().getRoleSet();

        if (requestParams.get("active") == null){
            user.get().setActive(false);
        }
        else if (requestParams.get("active").equals("on")){
            user.get().setActive(true);
        }

        userRoleSet = userService.roleModifier(userRoleSet, requestParams.get("adminCheck"), Role.ADMIN);
        userRoleSet = userService.roleModifier(userRoleSet, requestParams.get("practiceHeadCheck"),Role.PRACTICE_HEAD);
        userRoleSet = userService.roleModifier(userRoleSet, requestParams.get("supervisorCheck"), Role.SUPERVISOR);
        userRoleSet = userService.roleModifier(userRoleSet, requestParams.get("userCheck"),Role.USER);

        user.get().setRoleSet(userRoleSet);

        user.get().setGoldRedeemable(Integer.parseInt(requestParams.get("goldRedeemable")));
        user.get().setSilverRedeemable(Integer.parseInt(requestParams.get("silverRedeemable")));
        user.get().setBronzeRedeemable(Integer.parseInt(requestParams.get("bronzeRedeemable")));

        userService.adminEditUser(user.get());
        ModelAndView modelAndView = new ModelAndView("redirect:/users/"+activeUser.getUserId());
        return modelAndView;

    }
}
