package com.example.eyo.data;

import com.example.eyo.data.requests.CheckUserRequest;
import com.example.eyo.data.requests.CreateLabelRequest;
import com.example.eyo.data.requests.DeleteMailRequest;
import com.example.eyo.data.requests.GetLabelsRequest;
import com.example.eyo.data.requests.GetMailRequest;
import com.example.eyo.data.requests.GetUserRequest;
import com.example.eyo.data.requests.LoginRequest;
import com.example.eyo.data.requests.RegisterRequest;
import com.example.eyo.data.requests.SaveDraftRequest;
import com.example.eyo.data.requests.SearchRequest;
import com.example.eyo.data.requests.SendDraftRequest;
import com.example.eyo.data.requests.SendMailRequest;
import com.example.eyo.data.requests.UpdateDraftRequest;
import com.example.eyo.data.requests.UpdateMailRequest;
import com.example.eyo.data.requests.UpdatePhotoRequest;

import java.io.File;

import java.util.List;

public class ApiService {
    
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
    
    public static void registerUser(User user, ApiCallback<String> callback) {
        RegisterRequest request = new RegisterRequest(user);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void loginUser(String username, String password, ApiCallback<String> callback) {
        LoginRequest request = new LoginRequest(username, password);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void searchMails(String query, String authToken, ApiCallback<List<Mail>> callback) {
        SearchRequest request = new SearchRequest(query, authToken);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void sendMail(Mail mail, String authToken, ApiCallback<String> callback) {
        SendMailRequest request = new SendMailRequest(mail, authToken);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void checkUser(String username, String authToken, ApiCallback<Boolean> callback) {
        CheckUserRequest request = new CheckUserRequest(username, authToken);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void getUserData(String username, String authToken, ApiCallback<User> callback) {
        GetUserRequest request = new GetUserRequest(username, authToken);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void getLabels(String authToken, ApiCallback<List<Label>> callback) {
        GetLabelsRequest request = new GetLabelsRequest(authToken);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void updateUserPhoto(String authToken, File imageFile, ApiCallback<String> callback) {
        UpdatePhotoRequest request = new UpdatePhotoRequest(authToken, imageFile);
        PhotoUploadTask task = new PhotoUploadTask(request, callback);
        task.execute();
    }
    
    public static void updateUserPhoto(String authToken, String imageUri, ApiCallback<String> callback) {
        UpdatePhotoRequest request = new UpdatePhotoRequest(authToken, imageUri);
        PhotoUploadTask task = new PhotoUploadTask(request, callback);
        task.execute();
    }
    

    public static void createLabel(String name, String color, String authToken, ApiCallback<String> callback) {
        CreateLabelRequest request = new CreateLabelRequest(name, color, authToken);
        new GenericApiTask<>(request, callback).execute();
    }

    public static void getMail(int mailId, String authToken, ApiCallback<Mail> callback) {
        GetMailRequest request = new GetMailRequest(mailId, authToken);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void saveDraft(List<String> toList, List<String> ccList, String subject, String body, String authToken, ApiCallback<String> callback) {
        SaveDraftRequest request = new SaveDraftRequest(toList, ccList, subject, body, authToken);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void updateDraft(int draftId, List<String> toList, List<String> ccList, String subject, String body, String authToken, ApiCallback<String> callback) {
        UpdateDraftRequest request = new UpdateDraftRequest(draftId, toList, ccList, subject, body, authToken);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void updateMail(int mailId, boolean reportSpam, String authToken, ApiCallback<String> callback) {
        UpdateMailRequest request = new UpdateMailRequest(mailId, reportSpam, authToken);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void updateMailLabels(int mailId, List<String> labels, String authToken, ApiCallback<String> callback) {
        UpdateMailRequest request = new UpdateMailRequest(mailId, labels, authToken);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void updateMailReadStatus(int mailId, boolean read, String authToken, ApiCallback<String> callback) {
        UpdateMailRequest request = new UpdateMailRequest(mailId, read, authToken, true);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void deleteMail(int mailId, String authToken, ApiCallback<String> callback) {
        DeleteMailRequest request = new DeleteMailRequest(mailId, authToken);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void sendDraft(int draftId, List<String> toList, List<String> ccList, String subject, String body, String authToken, ApiCallback<String> callback) {
        SendDraftRequest request = new SendDraftRequest(draftId, toList, ccList, subject, body, authToken);
        new GenericApiTask<>(request, callback).execute();
    }
} 