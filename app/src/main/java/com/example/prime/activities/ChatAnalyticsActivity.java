package com.example.prime.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatAnalyticsActivity extends AppCompatActivity {

    TextView chatUserName;
    ImageView analyticsBack;

    TextView totalMessages;
    TextView sentMessages;
    TextView receivedMessages;
    TextView todayMessages;
    TextView mostActiveDay;
    TextView mostActiveTime;

    FirebaseDatabase database;
    FirebaseAuth auth;

    String receiverId;
    String receiverName;

    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_analytics);

        // -------------------------
        // GET DATA FROM INTENT
        // -------------------------

        receiverId = getIntent().getStringExtra("receiverRID");
        receiverName = getIntent().getStringExtra("namE");

        // -------------------------
        // INITIALIZE FIREBASE
        // -------------------------

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }

        currentUserId = auth.getCurrentUser().getUid();

        // -------------------------
        // FIND VIEWS
        // -------------------------

        chatUserName = findViewById(R.id.chatUserName);

        totalMessages = findViewById(R.id.totalMessages);
        sentMessages = findViewById(R.id.sentMessages);
        receivedMessages = findViewById(R.id.receivedMessages);
        todayMessages = findViewById(R.id.todayMessages);
        mostActiveDay = findViewById(R.id.mostActiveDay);
        mostActiveTime = findViewById(R.id.mostActiveTime);

        chatUserName.setText(receiverName);


        analyticsBack = findViewById(R.id.analyticsBack);

        analyticsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // -------------------------
        // LOAD ANALYTICS
        // -------------------------

        loadAnalytics();
    }


    private void loadAnalytics() {

        String room = currentUserId + receiverId;

        DatabaseReference reference =
                database
                        .getReference()
                        .child("chats")
                        .child(room)
                        .child("message");


        reference.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        int total = 0;
                        int sent = 0;
                        int received = 0;
                        int today = 0;

                        // Day statistics
                        Map<String, Integer> dayCount =
                                new HashMap<>();

                        // Hour statistics
                        Map<Integer, Integer> hourCount =
                                new HashMap<>();


                        // -------------------------
                        // READ ALL MESSAGES
                        // -------------------------

                        for (DataSnapshot messageSnapshot :
                                snapshot.getChildren()) {

                            Long timestamp =
                                    messageSnapshot
                                            .child("timeStamp")
                                            .getValue(Long.class);

                            String senderId =
                                    messageSnapshot
                                            .child("senderId")
                                            .getValue(String.class);


                            if (timestamp == null) {
                                continue;
                            }


                            total++;


                            // -------------------------
                            // SENT / RECEIVED
                            // -------------------------

                            if (currentUserId.equals(senderId)) {

                                sent++;

                            } else {

                                received++;
                            }


                            // -------------------------
                            // DATE
                            // -------------------------

                            Date date =
                                    new Date(timestamp);


                            Calendar calendar =
                                    Calendar.getInstance();

                            calendar.setTime(date);


                            // -------------------------
                            // TODAY
                            // -------------------------

                            Calendar todayCalendar =
                                    Calendar.getInstance();

                            if (calendar.get(
                                    Calendar.YEAR
                            ) == todayCalendar.get(
                                    Calendar.YEAR
                            )
                                    &&
                                    calendar.get(
                                            Calendar.DAY_OF_YEAR
                                    ) == todayCalendar.get(
                                            Calendar.DAY_OF_YEAR
                                    )) {

                                today++;
                            }


                            // -------------------------
                            // DAY OF WEEK
                            // -------------------------

                            String day =
                                    new SimpleDateFormat(
                                            "EEEE",
                                            Locale.getDefault()
                                    ).format(date);


                            dayCount.put(
                                    day,
                                    dayCount.getOrDefault(
                                            day,
                                            0
                                    ) + 1
                            );


                            // -------------------------
                            // HOUR
                            // -------------------------

                            int hour =
                                    calendar.get(
                                            Calendar.HOUR_OF_DAY
                                    );


                            hourCount.put(
                                    hour,
                                    hourCount.getOrDefault(
                                            hour,
                                            0
                                    ) + 1
                            );
                        }


                        // -------------------------
                        // FIND MOST ACTIVE DAY
                        // -------------------------

                        String activeDay = "No data";
                        int maxDayMessages = 0;

                        for (Map.Entry<String, Integer> entry :
                                dayCount.entrySet()) {

                            if (entry.getValue() >
                                    maxDayMessages) {

                                maxDayMessages =
                                        entry.getValue();

                                activeDay =
                                        entry.getKey();
                            }
                        }


                        // -------------------------
                        // FIND MOST ACTIVE HOUR
                        // -------------------------

                        int activeHour = -1;
                        int maxHourMessages = 0;

                        for (Map.Entry<Integer, Integer> entry :
                                hourCount.entrySet()) {

                            if (entry.getValue() >
                                    maxHourMessages) {

                                maxHourMessages =
                                        entry.getValue();

                                activeHour =
                                        entry.getKey();
                            }
                        }


                        String activeTime =
                                formatHour(activeHour);


                        // -------------------------
                        // SHOW RESULTS
                        // -------------------------

                        totalMessages.setText(
                                String.valueOf(total)
                        );

                        sentMessages.setText(
                                String.valueOf(sent)
                        );

                        receivedMessages.setText(
                                String.valueOf(received)
                        );

                        todayMessages.setText(
                                String.valueOf(today)
                        );

                        mostActiveDay.setText(
                                activeDay
                        );

                        mostActiveTime.setText(
                                activeTime
                        );
                    }


                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                ChatAnalyticsActivity.this,
                                "Failed to load analytics",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }


    // =========================================
    // FORMAT HOUR
    // =========================================

    private String formatHour(int hour) {

        if (hour == -1) {
            return "No data";
        }

        Calendar calendar =
                Calendar.getInstance();

        calendar.set(
                Calendar.HOUR_OF_DAY,
                hour
        );

        calendar.set(
                Calendar.MINUTE,
                0
        );

        return new SimpleDateFormat(
                "h a",
                Locale.getDefault()
        ).format(
                calendar.getTime()
        );
    }
}