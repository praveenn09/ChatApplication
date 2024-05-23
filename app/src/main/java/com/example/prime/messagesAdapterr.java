package com.example.prime;

import static com.example.prime.activities.ChatWindow.receiverIMg;
import static com.example.prime.activities.ChatWindow.senderImg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class messagesAdapterr  extends RecyclerView.Adapter {
    Context context;
    ArrayList<msgModelclass> messagesAdpterArraylist;
    int ITEM_SEND=1;
    int ITEM_RECEIVE=2;

    public messagesAdapterr(Context context, ArrayList<msgModelclass> messagesAdpterArraylist) {
        this.context = context;
        this.messagesAdpterArraylist = messagesAdpterArraylist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType== ITEM_SEND){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return  new senderViewHolder(view);
        }
        else{
            View view=LayoutInflater.from(context).inflate(R.layout.receiver_layout,parent,false);
            return  new receiverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        msgModelclass messages=messagesAdpterArraylist.get(position);
        if (holder.getClass()==senderViewHolder.class){
            senderViewHolder viewHolder=(senderViewHolder) holder;
            viewHolder.msgText.setText(messages.getMessage());
            Picasso.get().load(senderImg).into(viewHolder.circleImageView);

        }
        else {
            receiverViewHolder viewHolder=(receiverViewHolder) holder;
            viewHolder.msgText.setText(messages.getMessage());
            Picasso.get().load(receiverIMg).into(viewHolder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return  messagesAdpterArraylist.size();
    }

    @Override
    public int getItemViewType(int position) {
        msgModelclass messages=messagesAdpterArraylist.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())){
            return  ITEM_SEND;
        }
        else {
            return  ITEM_RECEIVE;
        }
    }

    class senderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgText;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.senderImageicon);
            msgText=itemView.findViewById(R.id.sendermsgText);

        }
    }
    class receiverViewHolder extends  RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgText;
        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.receiverImageIcon);
            msgText=itemView.findViewById(R.id.receivermsgText);
        }
    }
}