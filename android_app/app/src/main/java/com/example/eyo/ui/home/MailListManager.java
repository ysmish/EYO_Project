package com.example.eyo.ui.home;

import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eyo.data.Mail;
import com.example.eyo.utils.TokenManager;

import java.util.List;

public class MailListManager {
    
    private final MailAdapter mailAdapter;
    private final RecyclerView mailsRecyclerView;
    private final View emptyState;
    
    public MailListManager(RecyclerView mailsRecyclerView, View emptyState, TokenManager tokenManager) {
        this.mailsRecyclerView = mailsRecyclerView;
        this.emptyState = emptyState;
        
        // Initialize mail adapter
        this.mailAdapter = new MailAdapter(new MailAdapter.OnMailClickListener() {
            @Override
            public void onMailClick(Mail mail) {
                // TODO: Handle mail click (open mail detail)
                Toast.makeText(mailsRecyclerView.getContext(), 
                    "Mail clicked: " + mail.getSubject(), Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onStarClick(Mail mail) {
                // TODO: Handle star click (toggle star)
                Toast.makeText(mailsRecyclerView.getContext(), 
                    "Star clicked for: " + mail.getSubject(), Toast.LENGTH_SHORT).show();
            }
        });
        
        // Set TokenManager for loading sender avatars
        mailAdapter.setTokenManager(tokenManager);
        
        // Setup RecyclerView
        mailsRecyclerView.setLayoutManager(new LinearLayoutManager(mailsRecyclerView.getContext()));
        mailsRecyclerView.setAdapter(mailAdapter);
    }
    
    /**
     * Updates the mail list display based on the provided mails
     * @param mails List of mails to display
     */
    public void updateMailsList(List<Mail> mails) {
        if (mails == null || mails.isEmpty()) {
            showEmptyState();
        } else {
            showMailList(mails);
        }
    }
    
    /**
     * Shows the empty state when no mails are available
     */
    private void showEmptyState() {
        mailsRecyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
        mailAdapter.clearMails();
    }
    
    /**
     * Shows the mail list with the provided mails
     * @param mails List of mails to display
     */
    private void showMailList(List<Mail> mails) {
        emptyState.setVisibility(View.GONE);
        mailsRecyclerView.setVisibility(View.VISIBLE);
        mailAdapter.setMails(mails);
    }
    
    /**
     * Sets the current category for the mail adapter
     * @param filter The current filter/category
     */
    public void setCurrentCategory(String filter) {
        if (filter != null && mailAdapter != null) {
            mailAdapter.setCurrentCategory(filter);
        }
    }
    
    /**
     * Gets the mail adapter instance
     * @return The MailAdapter instance
     */
    public MailAdapter getMailAdapter() {
        return mailAdapter;
    }
} 