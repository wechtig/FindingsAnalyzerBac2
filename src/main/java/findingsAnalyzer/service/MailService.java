package findingsAnalyzer.service;

import findingsAnalyzer.data.ProjectConfig;
import findingsAnalyzer.data.User;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Properties;

public class MailService {

    private final static String EMAIL_CLIENT = "findingsbac@gmail.com";
    private final static String PASSWORD = "findingsbac_!";

    public void sendMail(ProjectConfig projectConfig, byte[] pdfExport, LocalDate startDate, LocalDate endDate) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_CLIENT, PASSWORD);
                    }
                });

        MimeMessage message = new MimeMessage(session);

        for (User user : projectConfig.getUsers()) {
            try {
                message.setFrom(new InternetAddress(EMAIL_CLIENT));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
                message.setSubject("Report for " + projectConfig.getName());
                message.setText("This is a generated report for the project " + projectConfig.getName() + "." +
                        " The data is collect between " + startDate.toString() + " and " + endDate.toString()+".");

                /*MimeBodyPart att = new MimeBodyPart();
                ByteArrayDataSource bds = new ByteArrayDataSource(pdfExport, "AttName");
                att.setDataHandler(new DataHandler(bds));
                att.setFileName(bds.getName());
                Multipart multipart = new MimeMultipart( "alternative" );
                multipart.addBodyPart(att);
                att.setContent();*/



                Multipart multipart = new MimeMultipart( "alternative" );
                MimeBodyPart attachment = new MimeBodyPart();
                DataSource dataSrc = new ByteArrayDataSource(pdfExport, "application/pdf");
                attachment.setDataHandler(new DataHandler(dataSrc));
                attachment.setFileName("myPdfDocument.pdf");
                multipart.addBodyPart(attachment);
                message.setContent(multipart);



                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
}
