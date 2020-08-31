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
import java.time.LocalDate;
import java.util.Properties;

public class MailService {

    private final static String EMAIL_ADDRESS = "findingsbac@gmail.com";
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
                        return new PasswordAuthentication(EMAIL_ADDRESS, PASSWORD);
                    }
                });

        MimeMessage message = new MimeMessage(session);

        for (User user : projectConfig.getUsers()) {
            try {
                message.setFrom(new InternetAddress(EMAIL_ADDRESS));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
                message.setSubject("Report for " + projectConfig.getName());
                message.setText("This is a generated report for the project " + projectConfig.getName() + "." +
                        " The data is collect between " + startDate.toString() + " and " + endDate.toString()+".");

                Multipart multipart = new MimeMultipart( "alternative" );
                MimeBodyPart attachment = new MimeBodyPart();
                DataSource dataSrc = new ByteArrayDataSource(pdfExport, "application/pdf");
                attachment.setDataHandler(new DataHandler(dataSrc));
                attachment.setFileName("report.pdf");
                multipart.addBodyPart(attachment);
                message.setContent(multipart);



                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMailToAdress(String mail, byte[] pdfExport, LocalDate startDate, LocalDate endDate) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_ADDRESS, PASSWORD);
                    }
                });

        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(EMAIL_ADDRESS));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mail));
            message.setSubject("PDF Findings Report");
            message.setText("This is a generated report" +
                        " The data is collect between " + startDate.toString() + " and " + endDate.toString()+".");

            Multipart multipart = new MimeMultipart( "alternative" );
            MimeBodyPart attachment = new MimeBodyPart();
            DataSource dataSrc = new ByteArrayDataSource(pdfExport, "application/pdf");
            attachment.setDataHandler(new DataHandler(dataSrc));
            attachment.setFileName("report.pdf");
            multipart.addBodyPart(attachment);
            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
