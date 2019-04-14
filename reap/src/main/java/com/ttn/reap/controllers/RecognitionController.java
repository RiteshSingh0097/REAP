package com.ttn.reap.controllers;

import com.ttn.reap.entities.Recognition;
import com.ttn.reap.entities.User;
import com.ttn.reap.exceptions.UserNotFoundException;
import com.ttn.reap.services.EmailService;
import com.ttn.reap.services.RecognitionService;
import com.ttn.reap.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class RecognitionController {

    @Autowired
    RecognitionService recognitionService;

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @PostMapping("/recognize")
    public ResponseEntity<String> recognizeNewer(@Valid @ModelAttribute("recognition") Recognition recognition,
                                                 RedirectAttributes redirectAttributes){
        User receivingUser = userService.getUserByFullName(recognition.getReceiverName());
        HttpHeaders httpHeaders = new HttpHeaders();
        if (receivingUser == null){
            httpHeaders.set("myResponseHeader","doesNotExist");
            return new ResponseEntity<>("User does not exist", httpHeaders, HttpStatus.OK);
        }
        if (receivingUser.getUserId().equals(recognition.getSenderId())){
            httpHeaders.set("myResponseHeader","selfRecognition");
            return new ResponseEntity<>("User can not recognize themselves",httpHeaders,HttpStatus.OK);
        }
        recognition.setReceiverId(receivingUser.getUserId());
        recognitionService.createRecognition(recognition);
        httpHeaders.set("myResponseHeader","successful Recognition");
        return new ResponseEntity<>("User recognized!",httpHeaders,HttpStatus.OK);
    }

    @PutMapping("/recognitions/{id}")
    @ResponseBody
    public void revokeRecognition(@PathVariable("id") String id) {
        Recognition recognition = recognitionService.getRecognitionById(Integer.parseInt(id)).get();
        recognition.setRevoked(true);
        recognitionService.updateRecognition(recognition);
        User receivingUser = userService.findUserById(recognition.getReceiverId());
        User senderUser = userService.findUserById(recognition.getSenderId());
        userService.revokeUserBadge(receivingUser, senderUser, recognition.getBadge());
        // Send recognition receiver an email on revocation of badge
        SimpleMailMessage badgeRevokedEmail = new SimpleMailMessage();
        badgeRevokedEmail.setFrom("riteshsingh893@gmail.com");
        badgeRevokedEmail.setTo(receivingUser.getEmail());
        badgeRevokedEmail.setSubject("REAP - Recognition Revoked");
        badgeRevokedEmail.setText("Hi, " + recognition.getReceiverName() +
                "!\nYour recognition with id " + recognition.getId() +
                " for a " + recognition.getBadge() +
                " badge shared by " + recognition.getSenderName() +
                " for " + recognition.getReason() +
                ", with comments '" + recognition.getComment() +
                "', made on " + recognition.getDate() +
                ", has been revoked." +
                "\nYou now have " + receivingUser.getPoints() + " redeemable points.");
        emailService.revokeMail(badgeRevokedEmail);
    }

    @PostMapping("/searchRecognitionByName")
    @ResponseBody
    public List<Recognition> getRecognitionByName(@RequestParam("fullName") String fullName) {
        List<Recognition> recognitionList = recognitionService.getRecognitionsByName(fullName);
        System.out.println(recognitionList);
        return recognitionList;
    }

    @GetMapping("/searchRecognitionsByDate/{date}")
    @ResponseBody
    public List<Recognition> getRecognitionsByDate(@PathVariable("date") String dateString) {
        System.out.println("In controller: " + dateString);
        List<Recognition> recognitionList = recognitionService.getRecognitionsBetweenDates(dateString);
        return recognitionList;
    }


    @GetMapping("/users/{id}/recognitions")
    public ModelAndView getUserRecognitions(@PathVariable("id") Integer id,
                                            HttpServletRequest httpServletRequest,
                                            RedirectAttributes redirectAttributes){
        HttpSession httpSession = httpServletRequest.getSession();
        User activeUser = (User) httpSession.getAttribute("activeUser");
        try{
            if (id != activeUser.getUserId()){
                ModelAndView modelAndView = new ModelAndView("redirect:/");
                redirectAttributes.addFlashAttribute("error","Please login first");
                return modelAndView;
            }
        }catch (NullPointerException e){
            ModelAndView modelAndView = new ModelAndView("redirect:/");
            redirectAttributes.addFlashAttribute("error","Please login first");
            return modelAndView;
        }

        Optional<User> optionalUser = userService.getUser(id);
        if (!optionalUser.isPresent()){
            throw new UserNotFoundException("No user found");
        }
        ModelAndView modelAndView = new ModelAndView("recognitions");
        modelAndView.addObject("user",optionalUser.get());
        List<Recognition> receivedRecognitionList =  recognitionService.getRecognitionsByReceiverId(optionalUser.get().getUserId());
        modelAndView.addObject("receivedRecognitionList",receivedRecognitionList);
        List<Recognition> sentRecognitionList = recognitionService.getRecognitionsBySenderId(optionalUser.get().getUserId());
        modelAndView.addObject("sentRecognitionList",sentRecognitionList);
        return modelAndView;
    }

    @GetMapping("/autocomplete")
    @ResponseBody
    public List<User> getUsersByNamePattern(@RequestParam("pattern") String pattern) {
        List<User> userList = userService.findUserByFullNamePattern(pattern+"%");
        return userList;
    }
}
