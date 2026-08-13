package com.example.prime.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prime.ChatRequest;
import com.example.prime.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRequestAdapter
        extends RecyclerView.Adapter<ChatRequestAdapter.ViewHolder> {

    private final ArrayList<ChatRequest> requestList;

    private final OnRequestActionListener listener;


    public interface OnRequestActionListener {

        void onAccept(ChatRequest request);

        void onDecline(ChatRequest request);
    }


    public ChatRequestAdapter(
            ArrayList<ChatRequest> requestList,
            OnRequestActionListener listener) {

        this.requestList = requestList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.request_item,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        ChatRequest request =
                requestList.get(position);


        String name =
                request.getSenderName();


        if (name == null ||
                name.trim().isEmpty()) {

            name = "Unknown User";
        }


        holder.requestUserName.setText(name);


        holder.requestProfile.setImageResource(
                R.drawable.ic_dummy_user
        );


        holder.acceptRequest.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (listener != null) {

                            listener.onAccept(
                                    request
                            );
                        }
                    }
                }
        );


        holder.declineRequest.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (listener != null) {

                            listener.onDecline(
                                    request
                            );
                        }
                    }
                }
        );
    }


    @Override
    public int getItemCount() {

        if (requestList == null) {
            return 0;
        }

        return requestList.size();
    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        CircleImageView requestProfile;

        TextView requestUserName;

        TextView acceptRequest;

        TextView declineRequest;


        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);


            requestProfile =
                    itemView.findViewById(
                            R.id.requestProfile
                    );


            requestUserName =
                    itemView.findViewById(
                            R.id.requestUserName
                    );


            acceptRequest =
                    itemView.findViewById(
                            R.id.acceptRequest
                    );


            declineRequest =
                    itemView.findViewById(
                            R.id.declineRequest
                    );
        }
    }
}