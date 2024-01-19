package ch.epfl.gsn.utils.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import javax.mail.MessagingException;
import org.apache.commons.mail.EmailException;

import org.junit.Test;

public class EmailServiceTest {

    @Test
    public void testSendEmailFailure() throws EmailException, MessagingException {
        ArrayList<String> to = new ArrayList<>();
        to.add("recipient@example.com");
        String subject = "Test Subject";
        String message = "Test Message";

        boolean result = EmailService.sendEmail(new ArrayList<>(Arrays.asList("recipient@example.com")), subject, message);
        //no SMTP server
        assertFalse(result);

    }


}
