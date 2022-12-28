package com.example.itextpdf.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface UserService {

    Document generateItextPdfDocument(OutputStream os) throws Exception;

    File fillTemplate(String templateFilePath, Map<String, String> keyValues, String saveFilePath) throws DocumentException, IOException;

}
