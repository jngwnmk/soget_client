package com.soget.soget_client.connector;

import android.os.AsyncTask;
import android.util.Log;

import com.soget.soget_client.callback.OnTaskCompleted;
import com.soget.soget_client.common.RESTAPIManager;
import com.soget.soget_client.model.Bookmark;
import com.soget.soget_client.model.Comment;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by wonmook on 15. 5. 16..
 */
public class AddCommentRequestTask extends AsyncTask<Void, Void, Comment> {
    private OnTaskCompleted listener;
    private String token ;
    private Comment comment;
    private String bookmark_id;
    ResponseEntity<Comment> response;

    public AddCommentRequestTask(OnTaskCompleted listener, String bookmark_id, Comment comment, String token){
        this.listener = listener;
        this.bookmark_id = bookmark_id;
        this.comment = comment;
        this.token = token;
    }

    @Override
    protected Comment doInBackground(Void... params) {
        try{

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            HttpHeaders headers = RESTAPIManager.getRestAPIManager().createHeaders(token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            response = restTemplate.exchange(RESTAPIManager.comment_url+bookmark_id, HttpMethod.POST, new HttpEntity(comment,headers), Comment.class);
            return response.getBody();

        } catch (Exception e){
            Log.e("AddBookmarkRequestTask", e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Comment comment){
        listener.onTaskCompleted(comment);
    }
}
