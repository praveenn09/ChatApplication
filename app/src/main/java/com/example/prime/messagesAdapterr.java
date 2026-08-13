package com.example.prime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class messagesAdapterr
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<msgModelclass> messagesAdpterArraylist;

    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;

    public messagesAdapterr(
            Context context,
            ArrayList<msgModelclass> messagesAdpterArraylist) {

        this.context = context;
        this.messagesAdpterArraylist = messagesAdpterArraylist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        if (viewType == ITEM_SEND) {

            View view = LayoutInflater
                    .from(context)
                    .inflate(
                            R.layout.sender_layout,
                            parent,
                            false
                    );

            return new senderViewHolder(view);

        } else {

            View view = LayoutInflater
                    .from(context)
                    .inflate(
                            R.layout.receiver_layout,
                            parent,
                            false
                    );

            return new receiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position) {

        msgModelclass message =
                messagesAdpterArraylist.get(position);

        // =========================
        // SENDER
        // =========================

        if (holder instanceof senderViewHolder) {

            senderViewHolder viewHolder =
                    (senderViewHolder) holder;

            viewHolder.msgText.setText(
                    message.getMessage()
            );

            // IMPORTANT:
            // Use dummy cartoon image.
            // Do NOT use Picasso here.
            viewHolder.circleImageView.setImageResource(
                    R.drawable.ic_dummy_user
            );
        }

        // =========================
        // RECEIVER
        // =========================

        else if (holder instanceof receiverViewHolder) {

            receiverViewHolder viewHolder =
                    (receiverViewHolder) holder;

            viewHolder.msgText.setText(
                    message.getMessage()
            );

            // Different dummy cartoon image
            viewHolder.circleImageView.setImageResource(
                    R.drawable.ic_dummy_receiver
            );
        }
    }

    @Override
    public int getItemCount() {
        return messagesAdpterArraylist.size();
    }

    @Override
    public int getItemViewType(int position) {

        msgModelclass message =
                messagesAdpterArraylist.get(position);

        String currentUserId =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .getUid();

        if (currentUserId.equals(message.getSenderId())) {

            return ITEM_SEND;

        } else {

            return ITEM_RECEIVE;
        }
    }


    // =====================================
    // SENDER VIEW HOLDER
    // =====================================

    class senderViewHolder
            extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView msgText;

        public senderViewHolder(
                @NonNull View itemView) {

            super(itemView);

            circleImageView =
                    itemView.findViewById(
                            R.id.senderImageicon
                    );

            msgText =
                    itemView.findViewById(
                            R.id.sendermsgText
                    );
        }
    }


    // =====================================
    // RECEIVER VIEW HOLDER
    // =====================================

    class receiverViewHolder
            extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView msgText;

        public receiverViewHolder(
                @NonNull View itemView) {

            super(itemView);

            circleImageView =
                    itemView.findViewById(
                            R.id.receiverImageIcon
                    );

            msgText =
                    itemView.findViewById(
                            R.id.receivermsgText
                    );
        }
    }
}