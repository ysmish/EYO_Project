package com.example.eyo.ui.components;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eyo.R;
import com.example.eyo.data.Mail;

import java.util.Arrays;
import java.util.Date;

public class MiniMailActionBarDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_mail_action_bar_demo);

        MiniMailActionBar actionBar = findViewById(R.id.mini_action_bar);
        
        // Create a sample mail for demonstration
        Mail sampleMail = createSampleMail();
        
        // Set up the action bar with the sample mail
        actionBar.setMail(sampleMail);
        actionBar.setOnActionListener(new MiniMailActionBar.OnActionListener() {
            @Override
            public void onMailUpdated(Mail mail) {
                Toast.makeText(MiniMailActionBarDemoActivity.this, 
                    "Mail updated successfully", Toast.LENGTH_SHORT).show();
                // You can update your UI here
            }
            
            @Override
            public void onMailDeleted(Mail mail) {
                Toast.makeText(MiniMailActionBarDemoActivity.this, 
                    "Mail deleted successfully", Toast.LENGTH_SHORT).show();
                // You can update your UI here, maybe close the activity or navigate back
                finish();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(MiniMailActionBarDemoActivity.this, 
                    "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private Mail createSampleMail() {
        Mail mail = new Mail();
        mail.setId(123);
        mail.setFrom("john.doe@example.com");
        mail.setTo(Arrays.asList("user@example.com"));
        mail.setSubject("Sample Email");
        mail.setBody("This is a sample email for testing the mini action bar.");
        mail.setDate(new Date());
        mail.setRead(false);
        mail.setLabels(Arrays.asList("1", "Inbox")); // Inbox label
        
        return mail;
    }
}
