/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.service;

import com.todo.api.exceptions.ValidationException;
import static com.todo.api.service.TodoService.logger;
import java.util.List;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author ruben
 */
@Service
public class SmsService {

    final static org.slf4j.Logger logger = LoggerFactory.getLogger(SmsService.class);
    private static String accountSid;
    private static String authToken;
    private static String defaultTo;
    private static String defaultFrom;
    private static TwilioRestClient client;
    private boolean enabled;

    @PostConstruct
    public void initialize() {

        if (client == null) {
            client = new TwilioRestClient(accountSid, authToken);
        }
    }

    @Async
    public void send(String body) throws TwilioRestException, ValidationException {

        send(defaultTo, defaultFrom, body);
    }

    @Async
    public void send(String to, String body) throws TwilioRestException, ValidationException {
        send(to, defaultFrom, body);
    }
    
    public void send(String to, String from, String body) throws ValidationException, TwilioRestException {

        if (isEnabled()) {

            validateInput(to, from, body);

            // Build a filter for the MessageList
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Body", body));
            params.add(new BasicNameValuePair("To", to));
            params.add(new BasicNameValuePair("From", from));

            MessageFactory messageFactory = client.getAccount().getMessageFactory();
            Message message = messageFactory.create(params);

            String status = message.getStatus();
            String sid = message.getSid();
            logger.debug("Sending sms message to [" + to + "] from [" + from + "] status [" + status + "] sid [" + sid + "]");

        }

    }

    public void setAccountSid(String sid) {
        accountSid = sid;
    }

    public void setAuthToken(String token) {
        authToken = token;
    }

    public void setDefaultTo(String to) {
        defaultTo = to;
    }

    public void setDefaultFrom(String from) {
        defaultFrom = from;
    }

    private void validateInput(String to, String from, String body) throws ValidationException {
        if (StringUtils.isEmpty(to)) {
            throw new ValidationException("sms to number must be specified");
        }
        if (StringUtils.isEmpty(from)) {
            throw new ValidationException("sms from number must be specified");
        }
        if (StringUtils.isEmpty(body)) {
            throw new ValidationException("sms body must be specified");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
}
