package com.example.tiff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DisplayImageActivity extends AppCompatActivity {

    private LinearLayout imageContainer;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        logout = findViewById(R.id.logoutUserBtn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DisplayImageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imageContainer = findViewById(R.id.imageContainer);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("itemsSold");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageContainer.removeAllViews();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HelperClass helperClass = dataSnapshot.getValue(HelperClass.class);

                    createImageView(helperClass.getImageUrl());

                    TextView nameTextView = new TextView(DisplayImageActivity.this);
                    nameTextView.setText(helperClass.getProductName());
                    imageContainer.addView(nameTextView);

                    TextView descriptionTextView = new TextView(DisplayImageActivity.this);
                    descriptionTextView.setText(helperClass.getDescription());
                    imageContainer.addView(descriptionTextView);

                    TextView priceTextView = new TextView(DisplayImageActivity.this);
                    priceTextView.setText(helperClass.getPrice());
                    imageContainer.addView(priceTextView);

                    CheckBox soldCheckBox = new CheckBox(DisplayImageActivity.this);
                    soldCheckBox.setText("Sold");
                    soldCheckBox.setEnabled(false);

                    // Retrieve the status from the database
                    DatabaseReference itemReference = FirebaseDatabase.getInstance().getReference("itemsSold").child(dataSnapshot.getKey());

                    itemReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String status = dataSnapshot.child("status").getValue(String.class);
                                // Set the CheckBox status based on the retrieved status
                                soldCheckBox.setChecked("sold".equals(status));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle onCancelled
                        }
                    });

                    soldCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            String status = b ? "sold" : "ready stock";

                            // Update the status in Firebase database
                            DatabaseReference itemReference = FirebaseDatabase.getInstance().getReference("itemsSold").child(dataSnapshot.getKey());
                            itemReference.child("status").setValue(status);

                        }
                    });
                    imageContainer.addView(soldCheckBox);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void createImageView(String imageUrl) {
        if (!isDestroyed()) {  // Check if the activity is still valid
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 16, 0, 0);
            imageView.setLayoutParams(layoutParams);
            imageContainer.addView(imageView);

            // Load the image into the ImageView using Glide or any other image loading library
            Glide.with(this)
                    .load(imageUrl)
                    .into(imageView);
        }

    }
}