package com.example.firebasesocialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ViewPostsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView postsListView;
    private ArrayList<String> usernames;
    private ArrayAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private ImageView sentPostsImageView;
    private TextView txtDes;
    private ArrayList<DataSnapshot> dataSnapshots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posts);

        postsListView = findViewById(R.id.postsListView);
        usernames = new ArrayList<>();
        adapter = new ArrayAdapter(ViewPostsActivity.this, android.R.layout.simple_list_item_1, usernames);
        postsListView.setAdapter(adapter);
        firebaseAuth = FirebaseAuth.getInstance();
        txtDes = findViewById(R.id.txtDes);
        sentPostsImageView = findViewById(R.id.sentPostsImageView);
        dataSnapshots = new ArrayList<>();
        postsListView.setOnItemClickListener(this);
        postsListView.setOnItemLongClickListener(this);


        FirebaseDatabase.getInstance().getReference().child("my_users").child(firebaseAuth.getCurrentUser().getUid()).child("received_posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                dataSnapshots.add(dataSnapshot);
                String fromWhomUsername = (String) dataSnapshot.child("fromWhom").getValue();
                usernames.add(fromWhomUsername);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        DataSnapshot myDataSnapshot = dataSnapshots.get(position);
        String downloadLink = (String) myDataSnapshot.child("imageLink").getValue();

        Picasso.get().load(downloadLink).into(sentPostsImageView);
        txtDes.setText((String) myDataSnapshot.child("Des").getValue());
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


        AlertDialog.Builder builder;
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            builder = new AlertDialog.Builder(this,android.R.style.Theme_Material_Dialog_Alert);
        }
        else{
          builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Delete Entry")
                .setMessage("Are you want to delete this entry")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Continue with delete
                        FirebaseStorage.getInstance().getReference().child("my_images").child((String) dataSnapshots.get(position).child("imageIdentifier").getValue()).delete();

                        FirebaseDatabase.getInstance().getReference().child("my_users").child(firebaseAuth.getCurrentUser().getUid()).child("received_posts").child(dataSnapshots.get(position).getKey()).removeValue();
                    }
                })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {



                        }
                    })

                    .setIcon(R.drawable.ic_launcher_foreground)
                .show();

        return false;
    }
}
