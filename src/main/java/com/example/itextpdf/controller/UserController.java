package com.example.itextpdf.controller;

import com.example.itextpdf.service.UserService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/pdf/down")
    public void download(HttpServletResponse response) {
        try {
            response.reset();
            response.setContentType("application/pdf");
            String fileName = "user_pdf_"+System.currentTimeMillis()+".pdf";
            response.setHeader("Content-disposition",
                    "attachment;filename=" + fileName);
            OutputStream os = response.getOutputStream();
            userService.generateItextPdfDocument(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/pdf/fill")
    public void fillTemplate(String templateFilePath){
        try {
            Map<String, String> keyValues = new HashMap<>();
            keyValues.put("num","0001");
            keyValues.put("name","彦祖");
            keyValues.put("sex","男");
            keyValues.put("age","20");
            userService.fillTemplate(templateFilePath,keyValues,"/Users/key/Downloads/new.pdf");
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
