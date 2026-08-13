package com.example.prime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prime.activities.firstUsersPage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSelectAdapter
        extends RecyclerView.Adapter<UserSelectAdapter.UserViewHolder> {

    private final Context context;
    private final ArrayList<Users> usersList;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public UserSelectAdapter(
            Context context,
            ArrayList<Users> usersList) {

        this.context = context;
        this.usersList = usersList;
    }


    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.user_select_item,
                        parent,
                        false
                );

        return new UserViewHolder(view);
    }


    // =========================================================
    // BIND VIEW HOLDER
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull UserViewHolder holder,
            int position) {

        if (position < 0 ||
                position >= usersList.size()) {
            return;
        }


        Users user = usersList.get(position);

        if (user == null) {
            return;
        }


        // =====================================================
        // USERNAME
        // =====================================================

        String username = user.getUsername();

        if (username == null ||
                username.trim().isEmpty()) {

            username = "Unknown User";
        }

        holder.username.setText(username);


        // =====================================================
        // PROFILE IMAGE
        // =====================================================

        String image = user.getProfileImage();


        if (image != null &&
                !image.trim().isEmpty() &&
                !image.equals("null")) {

            Picasso.get()
                    .load(image)
                    .placeholder(
                            R.drawable.ic_dummy_user
                    )
                    .error(
                            R.drawable.ic_dummy_user
                    )
                    .into(holder.profileImage);

        } else {

            holder.profileImage.setImageResource(
                    R.drawable.ic_dummy_user
            );
        }


        // =====================================================
        // CLICK USER
        // =====================================================

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (!(context instanceof firstUsersPage)) {
                            return;
                        }


                        firstUsersPage activity =
                                (firstUsersPage) context;


                        activity.checkChatStatus(user);
                    }
                }
        );
    }


    // =========================================================
    // ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        if (usersList == null) {
            return 0;
        }

        return usersList.size();
    }


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    public static class UserViewHolder
            extends RecyclerView.ViewHolder {

        CircleImageView profileImage;

        TextView username;


        public UserViewHolder(
                @NonNull View itemView) {

            super(itemView);


            profileImage =
                    itemView.findViewById(
                            R.id.selectUserProfile
                    );


            username =
                    itemView.findViewById(
                            R.id.selectUserName
                    );
        }
    }
}