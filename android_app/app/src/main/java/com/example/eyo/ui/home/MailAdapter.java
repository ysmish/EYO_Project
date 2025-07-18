package com.example.eyo.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eyo.R;
import com.example.eyo.data.Mail;

import java.util.ArrayList;
import java.util.List;

public class MailAdapter extends RecyclerView.Adapter<MailAdapter.MailViewHolder> {
    
    private List<Mail> mails = new ArrayList<>();
    private OnMailClickListener onMailClickListener;
    
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
    
    class MailViewHolder extends RecyclerView.ViewHolder {
        private TextView senderName;
        private TextView mailTime;
        private TextView mailSubject;
        private TextView mailPreview;
        private ImageView starIcon;
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
            // Set sender name
            senderName.setText(mail.getFrom());
            
            // Set time
            mailTime.setText(mail.getFormattedTime());
            
            // Set subject
            mailSubject.setText(mail.getSubject());
            
            // Set preview text
            mailPreview.setText(mail.getPreviewText());
            
            // Handle starred state
            if (mail.isStarred()) {
                starIcon.setVisibility(View.VISIBLE);
                starIcon.setImageResource(R.drawable.ic_star);
            } else {
                starIcon.setVisibility(View.GONE);
            }
            
            // Set read/unread state
            if (mail.isRead()) {
                senderName.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
                mailSubject.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
            } else {
                senderName.setTextColor(itemView.getContext().getColor(android.R.color.black));
                mailSubject.setTextColor(itemView.getContext().getColor(android.R.color.black));
            }
            
            // Set sender avatar (simple placeholder for now)
            senderAvatar.setImageResource(R.drawable.ic_person);
        }
    }
} 