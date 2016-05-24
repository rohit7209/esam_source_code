/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sam;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
/**
 *
 * @author Sanatan Home
 */
public class Mailer {
    String from_email, name;
    public Mailer(){
        Connection con;
        ResultSet rs;
        try {
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
            Statement stmt=con.createStatement();
            rs=stmt.executeQuery("select name, email from user_details");
            if(rs.next()){
                from_email=rs.getString("email");
                name=rs.getString("name");
            }else{
                from_email="No Email";
                name="No Name";
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, "There is a problem with your request!");
            e.getStackTrace();
            System.out.println("mailer(1205)::"+e.getMessage());
        }
    }
    /**
     * this method sends message to requested email(s) with different body messages
     * @param emails
     * @param body
     * @param sub 
     */
    public String sendMails(Object emails[], Object body[], String sub){
        
		final String username = "rohit.maiden.sharma@gmail.com";
		final String password = "8013403637";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from_email));
			message.setSubject(sub);
                        MimeBodyPart messageBodyPart1=new MimeBodyPart();
                        Multipart multipart = new MimeMultipart();
                        for(int i=0;i<emails.length;i++){
                            if(isEmailValid(String.valueOf(emails[i]))){
                                try{
                                    message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(String.valueOf(emails[i])));
                                    multipart.removeBodyPart(messageBodyPart1);
                                    messageBodyPart1.setText(String.valueOf(body[i]+name));
                                    multipart.addBodyPart(messageBodyPart1);     
                                    message.setContent(multipart);
                                    Transport.send(message);
                                    System.out.println("Done");
                                    
                                }catch(Exception e){
                                    System.out.println("error with email: "+emails[i]);
                                }
                            }
                        }

                        return "done";
                } catch (AddressException e) {
                    // ...
                    e.getStackTrace();
                    System.out.println("abcd1:"+e.getMessage());
                    return "There is a problem with email address!";
                } catch (MessagingException e) {
                    // ...
                    e.getStackTrace();
                    System.out.println("abcd2:"+e.getMessage());
                    return "There is a problem in sending message, please contact developer.";
                } catch (Exception e){
                    // ...
                    e.getStackTrace();
                    System.out.println("abcd3:"+e.getMessage());
                    return "Something is not right!";
                }
    }
    
    
    /**
     * the method send message to the email with the provided body message, sub along with an attached file provided as filename
     * @param to_email
     * @param body
     * @param sub
     * @param filename 
     */
    public String sendMail( String to_email, String body, String sub, String filename){
                final String username = "rohit.maiden.sharma@gmail.com";
		final String password = "8013403637";
                
                body=body+name;
                
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from_email));
			message.setSubject(sub);
                        message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to_email));
                        MimeBodyPart messageBodyPart1=new MimeBodyPart();
                        messageBodyPart1.setText(body);
                        
                        MimeBodyPart messageBodyPart2=new MimeBodyPart();
                        DataSource source = new FileDataSource(filename);    
                        messageBodyPart2.setDataHandler(new DataHandler(source));    
                        messageBodyPart2.setFileName("Attendance_record.pdf");
                            
                        Multipart multipart = new MimeMultipart();
                        
                        multipart.addBodyPart(messageBodyPart1);     
                        multipart.addBodyPart(messageBodyPart2);     
                        message.setContent(multipart);
                        Transport.send(message);
                        
                        return "done";
                } catch (AddressException e) {
                    // ...
                    e.getStackTrace();
                    System.out.println("abcd1:"+e.getMessage());
                    return "There is a problem with the provided email address, please try with another email-Id.";
                } catch (MessagingException e) {
                    // ...
                    e.getStackTrace();
                    System.out.println("abcd2:"+e.getMessage());
                    return "There is a problem in sending message, please try after sometime or contact developer.";
                } catch (Exception e){
                    // ...
                    e.getStackTrace();
                    System.out.println("abcd3:"+e.getMessage());
                    return "There is something not right, please contact developer.";
                }
    }
    
    /**
     * 
     * @param to_email
     * @param body
     * @param sub
     * @param filename
     * @return 
     */
    public String sendMail( String to_email, String body, String sub){
                final String username = "rohit.maiden.sharma@gmail.com";
		final String password = "8013403637";
                
                body=body+name;
                
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from_email));
			message.setSubject(sub);
                        message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to_email));
                        MimeBodyPart messageBodyPart1=new MimeBodyPart();
                        messageBodyPart1.setText(body);
                            
                        Multipart multipart = new MimeMultipart();
                        
                        multipart.addBodyPart(messageBodyPart1);     
                        message.setContent(multipart);
                        Transport.send(message);
                        return "done";
                } catch (AddressException e) {
                    // ...
                    e.getStackTrace();
                    System.out.println("abcd1:"+e.getMessage());
                    return "There is a problem with the provided email address, please try with another email-Id.";
                } catch (MessagingException e) {
                    // ...
                    e.getStackTrace();
                    System.out.println("abcd2:"+e.getMessage());
                    return "There is a problem in sending message, please try after sometime or contact developer.";
                } catch (Exception e){
                    // ...
                    e.getStackTrace();
                    System.out.println("abcd3:"+e.getMessage());
                    return "There is something not right, please contact developer.";
                }
    }
    
    /**
     * send mails to the different email(s) provided with common message body 
     * @param emails
     * @param body
     * @param sub 
     */
    public String sendMailCC( Object emails[], String body, String sub){
                final String username = "rohit.maiden.sharma@gmail.com";
		final String password = "8013403637";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from_email));
			message.setSubject(sub);
                        String email="";
			for(int i=0;i<emails.length;i++){
                            if(isEmailValid(String.valueOf(emails[i]))){
                                String p=email.equals("")?"":", ";
                                email+=p+String.valueOf(emails[i]);
                                System.out.println("Added: "+emails[i]);
                            }
                        }
                        
                                System.out.println("full email string: "+email);
                                
                        message.setRecipients(Message.RecipientType.CC,InternetAddress.parse(email));
                        MimeBodyPart messageBodyPart1=new MimeBodyPart();
                        messageBodyPart1.setText(body+name);
                        
                        Multipart multipart = new MimeMultipart();
                        
                        multipart.addBodyPart(messageBodyPart1);     
                        message.setContent(multipart);
                        Transport.send(message);
                        System.out.println("Done");
                        
                        return "done";
                } catch (AddressException e) {
                    // ...
                    e.getStackTrace();
                    System.out.println("abcd1:"+e.getMessage());
                    return "There is a problem with email address!";
                } catch (MessagingException e) {
                    // ...
                    e.getStackTrace();
                    System.out.println("abcd2:"+e.getMessage());
                    return "There is a problem in sending message, please contact developer.";
                } catch (Exception e){
                    // ...
                    e.getStackTrace();
                    System.out.println("abcd3:"+e.getMessage());
                    return "Something is not right!";
                }
    }
    
    
    boolean isEmailValid(String email){
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            return true;
        }else{
            return false;
        }
    }
}
