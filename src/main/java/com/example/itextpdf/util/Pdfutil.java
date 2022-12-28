package com.example.itextpdf.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class Pdfutil {

    public static Paragraph createTitle(String content) throws IOException, DocumentException {
        Font font = new Font(getBaseFont(), 24, Font.BOLD);
        Paragraph paragraph = new Paragraph(content, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        return paragraph;
    }


    public static Paragraph createChapterH1(String content) throws IOException, DocumentException {
        Font font = new Font(getBaseFont(), 22, Font.BOLD);
        Paragraph paragraph = new Paragraph(content, font);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        return paragraph;
    }

    public static Paragraph createChapterH2(String content) throws IOException, DocumentException {
        Font font = new Font(getBaseFont(), 18, Font.BOLD);
        Paragraph paragraph = new Paragraph(content, font);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        return paragraph;
    }

    public static Paragraph createParagraph(String content) throws IOException, DocumentException {
        Font font = new Font(getBaseFont(), 12, Font.NORMAL);
        Paragraph paragraph = new Paragraph(content, font);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        paragraph.setIndentationLeft(12); //设置左缩进
        paragraph.setIndentationRight(12); //设置右缩进
        paragraph.setFirstLineIndent(24); //设置首行缩进
        paragraph.setLeading(20f); //行间距
        paragraph.setSpacingBefore(5f); //设置段落上空白
        paragraph.setSpacingAfter(10f); //设置段落下空白
        return paragraph;
    }

    public static PdfPCell createCell(String content) throws IOException, DocumentException {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        Font font = new Font(getBaseFont(), 12, Font.NORMAL);
        cell.setPhrase(new Phrase(content, font));
        return cell;
    }

    private static BaseFont getBaseFont() throws IOException, DocumentException {
        return BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
    }

    public static void fillTemplate(String templateFilePath, Map<String, String> keyValues, String saveFilePath) throws IOException, DocumentException {
        PdfReader reader = null;
        PdfStamper stamper = null;
        Document doc = null;
        try (FileOutputStream out = new FileOutputStream(saveFilePath);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
//            PdfReader reader = new PdfReader(src, "Vibhu@123456789".getBytes());
            reader = new PdfReader(templateFilePath); // 读取pdf模板

//            stamper.setEncryption("".getBytes(), "Vibhu@123456789".getBytes(),
//                    PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
//            PdfReader.unethicalreading = true;

            int pageCount = reader.getNumberOfPages();
            stamper = new PdfStamper(reader, bos);
            doFillTemplate(keyValues, stamper);
            stamper.setFormFlattening(true); // 如果为false那么生成的PDF文件还能编辑，一定要设为true
            stamper.close();

            doc = new Document();
            PdfCopy copy = new PdfCopy(doc, out);
            doc.open();
            for (int j = 1; j <= pageCount; j++) {
                PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), j);
                copy.addPage(importPage);
            }
            doc.close();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    private static void doFillTemplate(Map<String, String> keyValues, PdfStamper stamper) throws IOException, DocumentException {
        AcroFields form = stamper.getAcroFields();
        // 字体,使用本机的宋体
//          BaseFont bf = BaseFont.createFont(font_cn + ",1", // 注意这里有一个,1
//          BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        java.util.Iterator<String> it = form.getFields().keySet().iterator();
        while (it.hasNext()) {
            //获取文本域名称
            String name = it.next().toString();
            String value = keyValues.containsKey(name) ? keyValues.get(name) : "";
            //设置文本域字体
            if (StringUtils.hasChinese(value))
                form.setFieldProperty(name, "textfont", bf, null);
            form.setFieldProperty(name, "textsize", 12F, null);
            // 图片
            if (name.startsWith("img_")) {
                // 通过域名获取所在页和坐标，左下角为起点
                int pageNo = form.getFieldPositions(name).get(0).page;
                Rectangle signRect = form.getFieldPositions(name).get(0).position;
                float x = signRect.getLeft();
                float y = signRect.getBottom();
                // 读图片
                Image image = Image.getInstance(value);
                // 获取操作的页面
                PdfContentByte under = stamper.getOverContent(pageNo);
                // 根据域的大小缩放图片
                image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                // 添加图片
                image.setAbsolutePosition(x, y);
                under.addImage(image);
            } else {
                //设置文本域内容
                form.setField(name, value);
            }
        }
    }
}
