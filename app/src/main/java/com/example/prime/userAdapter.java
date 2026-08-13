package com.example.prime;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prime.activities.ChatWindow;
import com.example.prime.activities.useradd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class userAdapter
        extends RecyclerView.Adapter<userAdapter.viewholder> {


    private useradd activity;

    private ArrayList<ChatListModel> chatList;


    public userAdapter(
            useradd activity,
            ArrayList<ChatListModel> chatList) {

        this.activity = activity;
        this.chatList = chatList;
    }


    @NonNull
    @Override
    public viewholder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.user_item,
                                parent,
                                false
                        );

        return new viewholder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull viewholder holder,
            int position) {


        ChatListModel chat =
                chatList.get(position);


        Users user =
                chat.getUser();


        if (user == null) {
            return;
        }


        // =========================================
        // USERNAME
        // =========================================

        holder.username.setText(
                user.getUsername()
        );


        // =========================================
        // LAST MESSAGE
        // =========================================

        holder.lastMessage.setText(
                chat.getLastMessage()
        );


        // =========================================
        // TIME
        // =========================================

        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "h:mm a",
                        Locale.getDefault()
                );


        holder.chatTime.setText(
                sdf.format(
                        new Date(
                                chat.getTimestamp()
                        )
                )
        );


        // =========================================
        // CARTOON DUMMY IMAGE
        // =========================================

        holder.imageprofile.setImageResource(
                R.drawable.ic_dummy_user
        );


        // =========================================
        // UNREAD COUNT
        // =========================================

        int unread =
                chat.getUnreadCount();


        if (unread > 0) {

            holder.unreadCount.setVisibility(
                    View.VISIBLE
            );


            if (unread > 99) {

                holder.unreadCount.setText(
                        "99+"
                );

            } else {

                holder.unreadCount.setText(
                        String.valueOf(unread)
                );
            }

        } else {

            holder.unreadCount.setVisibility(
                    View.GONE
            );
        }


        // =========================================
        // CLICK CHAT
        // =========================================

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {


                        Intent intent =
                                new Intent(
                                        activity,
                                        ChatWindow.class
                                );


                        intent.putExtra(
                                "namE",
                                user.getUsername()
                        );


                        intent.putExtra(
                                "profilePic",
                                user.getProfileImage()
                        );


                        intent.putExtra(
                                "receiverRID",
                                chat.getUserId()
                        );


                        activity.startActivity(
                                intent
                        );
                    }
                }
        );
    }


    @Override
    public int getItemCount() {

        return chatList.size();
    }


    // =========================================
    // VIEW HOLDER
    // =========================================

    public static class viewholder
            extends RecyclerView.ViewHolder {


        CircleImageView imageprofile;

        TextView username;

        TextView lastMessage;

        TextView chatTime;

        TextView unreadCount;


        public viewholder(
                @NonNull View itemView) {

            super(itemView);


            imageprofile =
                    itemView.findViewById(
                            R.id.imageprofilee
                    );


            username =
                    itemView.findViewById(
                            R.id.username
                    );


            lastMessage =
                    itemView.findViewById(
                            R.id.lastMessage
                    );


            chatTime =
                    itemView.findViewById(
                            R.id.chatTime
                    );


            unreadCount =
                    itemView.findViewById(
                            R.id.unreadCount
                    );
        }
    }
}