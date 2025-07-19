package com.example.eyo.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eyo.R;
import com.example.eyo.data.Mail;

import java.util.ArrayList;
import java.util.List;

public class MailAdapter extends RecyclerView.Adapter<MailAdapter.MailViewHolder> {
    
    private List<Mail> mails = new ArrayList<>();
    private OnMailClickListener onMailClickListener;
    private String currentCategory = "inbox"; // Default category
    
    public interface OnMailClickListener {
        void onMailClick(Mail mail);
        void onStarClick(Mail mail);
    }
    
    public MailAdapter(OnMailClickListener listener) {
        this.onMailClickListener = listener;
    }
    
    @NonNull
    @Override
    public MailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mail, parent, false);
        return new MailViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MailViewHolder holder, int position) {
        Mail mail = mails.get(position);
        holder.bind(mail);
    }
    
    @Override
    public int getItemCount() {
        return mails.size();
    }
    
    public void setMails(List<Mail> mails) {
        this.mails = mails != null ? mails : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void addMails(List<Mail> newMails) {
        if (newMails != null) {
            int startPosition = this.mails.size();
            this.mails.addAll(newMails);
            notifyItemRangeInserted(startPosition, newMails.size());
        }
    }
    
    public void clearMails() {
        this.mails.clear();
        notifyDataSetChanged();
    }
    
    public void setCurrentCategory(String category) {
        this.currentCategory = category != null ? category : "inbox";
        notifyDataSetChanged(); // Refresh to update display
    }
    
    class MailViewHolder extends RecyclerView.ViewHolder {
        private TextView senderName;
        private TextView mailTime;
        private TextView mailSubject;
        private TextView mailPreview;
        private ImageButton starIcon;
        private ImageView senderAvatar;
        
        public MailViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.sender_name);
            mailTime = itemView.findViewById(R.id.mail_time);
            mailSubject = itemView.findViewById(R.id.mail_subject);
            mailPreview = itemView.findViewById(R.id.mail_preview);
            starIcon = itemView.findViewById(R.id.star_icon);
            senderAvatar = itemView.findViewById(R.id.sender_avatar);
            
            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (onMailClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onMailClickListener.onMailClick(mails.get(position));
                    }
                }
            });
            
            starIcon.setOnClickListener(v -> {
                if (onMailClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onMailClickListener.onStarClick(mails.get(position));
                    }
                }
            });
        }
        
        public void bind(Mail mail) {
            // Set sender/draft/recipient text based on category
            setSenderText(mail);
            
            // Set time
            mailTime.setText(mail.getFormattedTime());
            
            // Set subject (cleaned)
            mailSubject.setText(mail.getCleanSubject());
            
            // Set preview text (cleaned)
            mailPreview.setText(mail.getPreviewText());
            
            // Handle starred state
            if (mail.isStarred()) {
                starIcon.setImageResource(R.drawable.ic_star_filled);
                starIcon.setContentDescription("Remove from starred");
            } else {
                starIcon.setImageResource(R.drawable.ic_star);
                starIcon.setContentDescription("Add to starred");
            }
            
                                // Set read/unread state
            if (mail.isRead()) {
                // READ MAIL STYLING - All unbold and same gray color
                senderName.setTypeface(null, Typeface.NORMAL);
                mailSubject.setTypeface(null, Typeface.NORMAL);
                mailPreview.setTypeface(null, Typeface.NORMAL);
                
                // Don't override draft color for read mails
                if (!isDraftMail(mail)) {
                    senderName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.mail_read_all));
                }
                mailSubject.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.mail_read_all));
                mailPreview.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.mail_read_all));
            } else {
                // UNREAD MAIL STYLING - Sender and subject bold white, body gray
                senderName.setTypeface(null, Typeface.BOLD);
                mailSubject.setTypeface(null, Typeface.BOLD);
                mailPreview.setTypeface(null, Typeface.NORMAL);
                
                // Don't override draft color for unread mails
                if (!isDraftMail(mail)) {
                    senderName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.mail_unread_text));
                }
                mailSubject.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.mail_unread_subject));
                mailPreview.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.mail_preview_text));
            }
            
            // Set sender avatar (simple placeholder for now)
            senderAvatar.setImageResource(R.drawable.ic_person);
        }
        
        private void setSenderText(Mail mail) {
            if (isDraftMail(mail)) {
                // ALWAYS show "Draft" in red for draft mails, regardless of current category
                senderName.setText("Draft");
                senderName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.mail_draft_text));
            } else if ("sent".equals(currentCategory)) {
                // Show "To: recipients" for sent mails (only if not draft)
                String recipients = getRecipientsText(mail);
                senderName.setText("To: " + recipients);
                // Use normal color for sent mails, will be set later based on read status
            } else {
                // Show sender name for other categories (inbox, starred, spam, all)
                senderName.setText(mail.getCleanFrom());
                // Use normal color, will be set later based on read status
            }
        }
        
        private boolean isDraftMail(Mail mail) {
            // Use the built-in isDraft() method from Mail class (checks for "Drafts" or "4")
            if (mail.isDraft()) {
                return true;
            }
            
            // Also check if we're in drafts category as fallback
            if ("drafts".equals(currentCategory)) {
                return true;
            }
            
            return false;
        }
        
        private String getRecipientsText(Mail mail) {
            StringBuilder recipients = new StringBuilder();
            
            // Add "to" recipients
            if (mail.getTo() != null && !mail.getTo().isEmpty()) {
                for (int i = 0; i < mail.getTo().size(); i++) {
                    if (i > 0) recipients.append(", ");
                    recipients.append(mail.getTo().get(i));
                }
            }
            
            // Add CC recipients if any
            if (mail.getCc() != null && !mail.getCc().isEmpty()) {
                if (recipients.length() > 0) recipients.append(", ");
                for (int i = 0; i < mail.getCc().size(); i++) {
                    if (i > 0) recipients.append(", ");
                    recipients.append(mail.getCc().get(i));
                }
            }
            
            return recipients.toString();
        }
    }
} 