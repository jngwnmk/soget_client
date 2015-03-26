package com.soget.soget_client.connector;

import android.util.Log;

import com.soget.soget_client.callback.OnTaskCompleted;
import com.soget.soget_client.common.RESTAPIManager;
import com.soget.soget_client.model.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by wonmook on 2015-03-25.
 */
public class FriendReceiveListRequestTask extends FriendRequestTask{
    public FriendReceiveListRequestTask(OnTaskCompleted listener, String user_id, String token) {
        super(listener, user_id, token);
    }


    @Override
    protected ArrayList<User> doInBackground(Void... params) {
        try{

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            ResponseEntity<User[]> response;
            HttpHeaders headers = RESTAPIManager.getRestAPIManager().createHeaders(token);
            response = restTemplate.exchange(RESTAPIManager.friends_receive_url + "/" + user_id, HttpMethod.GET, new HttpEntity(headers), User[].class);
            friends = new ArrayList<User>();
            friends.addAll(Arrays.asList(response.getBody()));
            return friends;

        } catch (Exception e){
            Log.e("FriendReceive", e.getMessage(), e);
        }
        return null;
    }
}
