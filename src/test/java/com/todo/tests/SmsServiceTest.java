/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.tests;

import com.todo.api.exceptions.ValidationException;
import com.todo.api.service.SearchService;
import com.todo.api.service.SmsService;
import com.twilio.sdk.TwilioRestException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author ruben
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:test-applicationContext.xml"})
public class SmsServiceTest {

    @Autowired
    SmsService smsService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() throws Exception {

        testSendMessage();
        testSendMessageInvalidBody();
        testSendMessageEmptyToNumber();
        testSendMessageInvalidToNumber();
        
    }

    private void testSendMessage() throws Exception {

        String body = "this is a test msg from an api";
        smsService.send(body);
    }

    private void testSendMessageInvalidBody() throws TwilioRestException {

        try {
            String body = "";
            smsService.send(body);
        } catch (ValidationException ex) {
            //expected exception
        }

        try {
            String body = null;
            smsService.send(body);
        } catch (ValidationException ex) {
            //expected exception
        }

    }

    private void testSendMessageEmptyToNumber() throws TwilioRestException {

        try {
            String body = "some text";
            String toNumber = "";
            smsService.send(toNumber, body);
        } catch (ValidationException ex) {
            //expected exception
        }

        try {
            String body = "some text";
            String toNumber = null;
            smsService.send(toNumber, body);
        } catch (ValidationException ex) {
            //expected exception
        }

    }

    private void testSendMessageInvalidToNumber() throws ValidationException {
        try {
            String body = "some text";
            String toNumber = "abcd";
            smsService.send(toNumber, body);
        } catch (TwilioRestException ex) {
            Logger.getLogger(SmsServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
