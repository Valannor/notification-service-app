package com.kurdov.task.server.handler;

import com.kurdov.task.exception.IncorrectDataException;
import com.kurdov.task.exception.TryLaterException;
import com.kurdov.task.core.Notification;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class HttpHandler implements NotificationHandler {

    private static boolean isPrintResponse;

    private HttpResponse lastResponse;

    public boolean isPrintResponse() {
        return isPrintResponse;
    }

    public void setPrintResponse(boolean isPrintResponse) {
        HttpHandler.isPrintResponse = isPrintResponse;
    }

    @Override
    public void sendNotification(Notification notification) throws IncorrectDataException, TryLaterException, ClientProtocolException {
    try {
        HttpPost request = new HttpPost();
        populateHttpMessage(notification, request);

        HttpClient client = HttpClientBuilder.create().build();
        lastResponse = client.execute(request);

        if (isPrintResponse()) {
            printResponze();
        }

    } catch (ClientProtocolException e) {
        throw new ClientProtocolException("Incorrect protocol." +
            "Problematic notifications externalId = " + notification.getExternalId());
    } catch (IOException e) {
        throw new TryLaterException("Can not connect to site. " +
                "Problematic notifications externalId = " + notification.getExternalId()
                + ". Url = " + notification.getExtraParam());
    }
    }

    private void populateHttpMessage(Notification notification,
                                     HttpPost request) throws IncorrectDataException {
        String externalId = notification.getExternalId();
        String urlRecipient = notification.getExtraParam();
        String httpMessage = notification.getMessage();

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("Message", httpMessage));

        try {
            request.setURI(new URI(urlRecipient));
            request.addHeader("User-Agent", "Notification service");
            request.setEntity(new UrlEncodedFormEntity(urlParameters));

        } catch (URISyntaxException e) {
            throw new IncorrectDataException("Wrong Url address " + urlRecipient
                    + ". Problematic notifications externalId = " + externalId);
        } catch (UnsupportedEncodingException e) {
            throw new IncorrectDataException("Incorrect message data. " +
                    "Problematic notifications externalId = " + externalId);
        }

    }

    /**
     * Method for visualization of response
     */
    public void printResponze() {
        try {
            System.out.println("Response Code : "
                    + lastResponse.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(lastResponse.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append("\r\n" + line);
            }

            System.out.println(result);
        } catch (IOException e) {
            System.err.println(e.getMessage() + ". Incorrect data received.");
        }
    }
}
