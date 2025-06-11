package com.example.cessionappbackend.services;

import com.example.cessionappbackend.dto.SalaryAssignmentDocumentDTO;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.Font;
import com.itextpdf.text.Element;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SalaryAssignmentPdfGeneratorService {

    private static Font arabicFont;

    static {
        try {
            // Load an Arabic font from resources. Make sure NotoSansArabic-Regular.ttf is in src/main/resources/fonts/
            BaseFont baseFont = BaseFont.createFont(new ClassPathResource("fonts/NotoSansArabic-Regular.ttf").getURL().toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            arabicFont = new Font(baseFont, 12); // Default font size

        } catch (Exception e) {
            System.err.println("Error loading Arabic font from resources: " + e.getMessage());
            e.printStackTrace();
            // Fallback to a default font if Arabic font loading fails
            arabicFont = new Font(Font.FontFamily.TIMES_ROMAN, 12); 
        }
    }

    public byte[] generatePdf(SalaryAssignmentDocumentDTO data) {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();
            document.setPageSize(new Rectangle(document.getPageSize().getWidth(), document.getPageSize().getHeight()));
            document.setMargins(72, 72, 72, 72); // Standard margins

            // Set document direction to RTL (important for Arabic)
            writer.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

            // Document Title
            Paragraph title = new Paragraph("إحالة على الأجر تجارية", arabicFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("(في إطار قانون البيع بالتقسيط)", arabicFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // Process the template text line by line and replace placeholders
            String template = """
مراجع الإحالة بسجلات المحكمة:\n
محكمة الناحية: ${courtName}\n
الدفتر: ${bookNumber}\n
الصفحة: ${pageNumber}\n
التاريخ: ${date}\n
البيانات المتعلقة بالمزود:\n
المعرف الجبائي: ${supplierTaxId}\n
هوية المزود: ${supplierName}\n
العنوان: ${supplierAddress}\n
رقم الحساب البنكي للمزود في (20 رقما): ${supplierBankAccount}\n
البيانات المتعلقة بالعون العمومي:\n
المعرف الوحيد: ${workerNumber}\n
الإسم واللقب: ${fullName}\n
رقم بطاقة التعريف الوطنية: ${cin}\n
العنوان الشخصي: ${address}\n
الهيكل الإداري المنتمي اليه: ${workplace}\n
الرتبة: ${jobTitle}\n
الوضعية المهنية: ${employmentStatus}\n
رقم الحساب البنكي أو البريدي (20 رقما): ${bankAccountNumber}\n
البيانات المتعلقة بالبضاعة المقتناة:\n
ذكر طبيعة البضاعة المقتناة بكل دقة: ${itemDescription}\n
المبلغ الجملي للبضاعة المقتناة بلسان القلم: ${amountInWords}\n
المبلغ الجملي للبضاعة المقتناة بالأرقام: ${totalAmountNumeric}\n
المبلغ الشهري المقتطع من الراتب بالأرقام: ${monthlyPayment}\n
مدة الاقتطاع من الأجر (ذكر المدة بحساب عدد الأشهر): ${loanDuration}\n
تاريخ بداية سريان أول اقتطاع من الأجر: ${firstDeductionMonthArabic}\n
محتوى الاتفاق:\n
بمقتضى هذه الإحالة، يأذن السيد الأمين العام للمصاريف لدى ${workplace}\n
الاقتطاع شهريًا من راتبه المبلغ المذكور أعلاه، وتحويله حسب الطرق الإجرائية المعتمدة\n
للمزود ${supplierName} حتى الخلاص النهائي، ما لم تطرأ موانع قانونية أو مهنية أو غيرها تحول دون ذلك.\n
التوقيعات:\n
إمضاء المزود وختمه\n
إمضاء المدين\n
ختم المؤجر\n
ختم المحكمة والإمضاء\n
            """;

            String populatedTemplate = populateTemplate(template, data);

            // Add each line of the populated template as a paragraph
            for (String line : populatedTemplate.split("\\n")) {
                Paragraph p = new Paragraph(line, arabicFont);
                p.setAlignment(Element.ALIGN_RIGHT); // Align text to the right
                document.add(p);
            }
            
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String populateTemplate(String template, SalaryAssignmentDocumentDTO data) {
        String result = template;

        // Court Reference
        result = replacePlaceholder(result, "courtName", data.getCourtName());
        result = replacePlaceholder(result, "bookNumber", data.getBookNumber());
        result = replacePlaceholder(result, "pageNumber", data.getPageNumber());
        result = replacePlaceholder(result, "date", data.getDate());

        // Supplier Information
        result = replacePlaceholder(result, "supplierTaxId", data.getSupplierTaxId());
        result = replacePlaceholder(result, "supplierName", data.getSupplierName());
        result = replacePlaceholder(result, "supplierAddress", data.getSupplierAddress());
        result = replacePlaceholder(result, "supplierBankAccount", data.getSupplierBankAccount());

        // Worker Information
        result = replacePlaceholder(result, "workerNumber", data.getWorkerNumber());
        result = replacePlaceholder(result, "fullName", data.getFullName());
        result = replacePlaceholder(result, "cin", data.getCin());
        result = replacePlaceholder(result, "address", data.getAddress());
        result = replacePlaceholder(result, "workplace", data.getWorkplace());
        result = replacePlaceholder(result, "jobTitle", data.getJobTitle());
        result = replacePlaceholder(result, "employmentStatus", data.getEmploymentStatus());
        result = replacePlaceholder(result, "bankAccountNumber", data.getBankAccountNumber());

        // Purchase Information
        result = replacePlaceholder(result, "itemDescription", data.getItemDescription());
        result = replacePlaceholder(result, "amountInWords", data.getAmountInWords());
        result = replacePlaceholder(result, "totalAmountNumeric", data.getTotalAmountNumeric() != null ? String.format("%.3f", data.getTotalAmountNumeric()) : "");
        result = replacePlaceholder(result, "monthlyPayment", data.getMonthlyPayment() != null ? String.format("%.2f", data.getMonthlyPayment()) : "");
        result = replacePlaceholder(result, "loanDuration", data.getLoanDuration());
        result = replacePlaceholder(result, "firstDeductionMonthArabic", data.getFirstDeductionMonthArabic());

        return result;
    }

    private String replacePlaceholder(String text, String placeholderName, String value) {
        String placeholder = "${" + placeholderName + "}";
        return text.replace(placeholder, value != null ? value : "");
    }
} 