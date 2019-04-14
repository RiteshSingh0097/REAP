package com.ttn.reap.controllers;

import com.ttn.reap.entities.Recognition;
import com.ttn.reap.entities.User;
import com.ttn.reap.exceptions.UserNotFoundException;
import com.ttn.reap.services.RecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
public class DownloadController {

    @Autowired
    RecognitionService recognitionService;

    @GetMapping("/downloadCSV/{date}")
    public void downloadCSV(@PathVariable("date") String dateString,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException{
        System.out.println("Downloading CSV for: "+dateString);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("activeUser");

        if (user == null){
            throw new UserNotFoundException("Unauthorized user");
        }

        String csvFileName = "reap-recognitions-"+dateString+".csv";

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",csvFileName);
        response.setHeader(headerKey, headerValue);
        response.setContentType("text/csv");

        List<Recognition> recognitionList = recognitionService.getRecognitionsBetweenDates(dateString);

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.EXCEL_PREFERENCE);

        String[] header = {"id", "badge", "comment", "date", "reason",
        "receiverId", "receiverName", "senderId", "senderName"};

        csvBeanWriter.writeHeader(header);
        for (Recognition recognition : recognitionList){
            csvBeanWriter.write(recognition, header);
        }
        csvBeanWriter.close();
    }
}
