package com.ttn.reap.services;

import com.ttn.reap.entities.Recognition;
import com.ttn.reap.entities.User;
import com.ttn.reap.repositories.RecognitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RecognitionService {

    @Autowired
    RecognitionRepository recognitionRepository;

    @Autowired
    UserService userService;

    public Recognition createRecognition(Recognition recognition){
        recognition.setDate(LocalDate.now());

        System.out.println(recognition.getBadge());
        User sendingUser = userService.findUserById(recognition.getSenderId());
        User receiveUser = userService.findUserById(recognition.getReceiverId());
        if (recognition.getBadge().equals("gold") && sendingUser.getGoldShareable()>0){
            sendingUser.setGoldShareable(sendingUser.getGoldShareable()-1);
            receiveUser.setGoldRedeemable(receiveUser.getGoldRedeemable()+1);
        }
        if (recognition.getBadge().equals("silver") && sendingUser.getSilverShareable()>0){
            sendingUser.setSilverShareable(sendingUser.getSilverShareable()-1);
            receiveUser.setSilverRedeemable(receiveUser.getSilverRedeemable()+1);
        }
        if (recognition.getBadge().equals("bronze")&& sendingUser.getBronzeShareable()>0){
            sendingUser.setBronzeShareable(sendingUser.getBronzeShareable()-1);
            receiveUser.setBronzeRedeemable(receiveUser.getBronzeRedeemable()+1);
        }
        receiveUser.setPoints(userService.calculatePoints(receiveUser));
        return recognitionRepository.save(recognition);
    }

    public List<Recognition> getListOfRecognition(){
        return recognitionRepository.findAll();
    }

    public List<Recognition> getRecognitionsByName(String receiverName){
        return recognitionRepository.findByReceiverNameLike(receiverName);
    }

    public List<Recognition> getRecognitionsBetweenDates(String dateString){
        LocalDate today = LocalDate.now();
        if (dateString.equals("today")){
            return recognitionRepository.findByDateBetween(today,today);
        }
        else if (dateString.equals("yesterday")){
            return recognitionRepository.findByDateBetween(today.minusDays(1),today);
        }
        else if (dateString.equals("last7")){
            return recognitionRepository.findByDateBetween(today.minusDays(7),today);
        }else {
            return recognitionRepository.findByDateBetween(today.minusDays(30),today);
        }
    }

    public List<Recognition> getRecognitionsByReceiverId(Integer receiverId){
        return recognitionRepository.findByReceiverId(receiverId);
    }

    public List<Recognition> getRecognitionsBySenderId(Integer senderId){
        return recognitionRepository.findBySenderId(senderId);
    }

    public Optional<Recognition> getRecognitionById(Integer id){
        return recognitionRepository.findById(id);
    }

    public void updateRecognition(Recognition recognition){
        recognitionRepository.save(recognition);
    }
}
