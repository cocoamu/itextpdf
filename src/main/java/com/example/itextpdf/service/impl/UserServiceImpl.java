package com.example.itextpdf.service.impl;

import com.example.itextpdf.entity.User;
import com.example.itextpdf.service.UserService;
import com.example.itextpdf.util.MyHeaderFooterPageEventHelper;
import com.example.itextpdf.util.Pdfutil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public Document generateItextPdfDocument(OutputStream os) throws Exception {
        //创建pdf
        // document
        Document document = new Document(PageSize.A4);
//        PdfWriter.getInstance(document, os);
        PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
        //添加页眉页脚和水印
        pdfWriter.setPageEvent(new MyHeaderFooterPageEventHelper("Java 全栈知识体系", "SpringBoot集成ItextPDF导出", "https://pdai.tech", "https://pdai.tech"));

        // open
        document.open();

        // add content - pdf meta information
        document.addAuthor("key");
        document.addCreationDate();
        document.addLanguage("中文");
        document.addTitle("pdai-pdf-itextpdf");
        document.addKeywords("pdf-pdai-keyword");
        document.addCreator("key");

        // add content -  page content 添加章节与段落
        // Title
        document.add(Pdfutil.createTitle("Java 全栈知识体系"));

        // Chapter 1
        document.add(Pdfutil.createChapterH1("1. 知识准备"));
        document.add(Pdfutil.createChapterH2("1.1 什么是POI"));
        document.add(Pdfutil.createParagraph("Apache POI 是创建和维护操作各种符合Office Open XML（OOXML）标准和微软的OLE 2复合文档格式（OLE2）的Java API。用它可以使用Java读取和创建,修改MS Excel文件.而且,还可以使用Java读取和创建MS Word和MSPowerPoint文件。更多请参考[官方文档](https://poi.apache.org/index.html)"));
        document.add(Pdfutil.createChapterH2("1.2 POI中基础概念"));
        document.add(Pdfutil.createParagraph("生成xls和xlsx有什么区别？POI对Excel中的对象的封装对应关系？"));

        // Chapter 2
        document.add(Pdfutil.createChapterH1("2. 实现案例"));
        document.add(Pdfutil.createChapterH2("2.1 用户列表示例"));
        document.add(Pdfutil.createParagraph("以导出用户列表为例"));

        // 表格
        List<User> userList = getUserList();
        PdfPTable table = new PdfPTable(new float[]{20, 40, 50, 40, 40});
        table.setTotalWidth(500);
        table.setLockedWidth(true);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setBorder(1);

        for (int i = 0; i < userList.size(); i++) {
            table.addCell(Pdfutil.createCell(userList.get(i).getId() + ""));
            table.addCell(Pdfutil.createCell(userList.get(i).getUserName()));
            table.addCell(Pdfutil.createCell(userList.get(i).getEmail()));
            table.addCell(Pdfutil.createCell(userList.get(i).getPhoneNumber() + ""));
            table.addCell(Pdfutil.createCell(userList.get(i).getDescription()));
        }
        document.add(table);

        document.add(Pdfutil.createChapterH2("2.2 图片导出示例"));
        document.add(Pdfutil.createParagraph("以导出图片为例"));
        // 图片
        Resource resource = new ClassPathResource("pdf.png");
        Image image = Image.getInstance(resource.getURL());
        // Image image = Image.getInstance("/Users/pdai/pdai/www/tech-pdai-spring-demos/481-springboot-demo-file-pdf-itextpdf/src/main/resources/pdai-guli.png");
        image.setAlignment(Element.ALIGN_CENTER);
        image.scalePercent(60); // 缩放
        document.add(image);

        // close
        document.close();
        return document;
    }

    @Override
    public File fillTemplate(String fromPath, Map<String, String> keyValues, String toPath) throws DocumentException, IOException {
        File fromFile = new File(fromPath);
        if (!fromFile.exists()) {
            throw new RuntimeException("file is not exists");
        }
        File toFile = new File(toPath);
        if (!toFile.exists()) {
            toFile.createNewFile();
        }
        Pdfutil.fillTemplate(fromPath, keyValues, toPath);
        return toFile;
    }

    private List<User> getUserList() {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            userList.add(User.builder()
                    .id(Long.parseLong(i + "")).userName("pdai" + i).email("pdai@pdai.tech" + i).phoneNumber(121231231231L)
                    .description("hello world" + i)
                    .build());
        }
        return userList;
    }
}