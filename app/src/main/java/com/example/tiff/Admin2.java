package com.example.tiff;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Admin2 extends AppCompatActivity {
    Button logout;
    private LinearLayout imageContainer;
    private int savedScrollPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin2);

        imageContainer = findViewById(R.id.imageContainerAdmin2);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("itemsSold");

        // Retrieve and restore the scroll position if it was saved before
        if (savedInstanceState != null) {
            savedScrollPosition = savedInstanceState.getInt("scroll_position", 0);
            imageContainer.post(new Runnable() {
                @Override
                public void run() {
                    imageContainer.scrollTo(0, savedScrollPosition);
                }
            });
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageContainer.removeAllViews();
                int index = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HelperClass helperClass = dataSnapshot.getValue(HelperClass.class);

                    createImageView(helperClass.getImageUrl());

                    TextView nameTextView = new TextView(Admin2.this);
                    nameTextView.setText(helperClass.getProductName());
                    imageContainer.addView(nameTextView);

                    TextView descriptionTextView = new TextView(Admin2.this);
                    descriptionTextView.setText(helperClass.getDescription());
                    imageContainer.addView(descriptionTextView);

                    TextView priceTextView = new TextView(Admin2.this);
                    priceTextView.setText(helperClass.getPrice());
                    imageContainer.addView(priceTextView);

                    CheckBox soldCheckBox = new CheckBox(Admin2.this);
                    soldCheckBox.setText("Sold");

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
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            // Retrieve the status from the database
                            DatabaseReference itemReference = FirebaseDatabase.getInstance().getReference("itemsSold").child(dataSnapshot.getKey());
                            if (isChecked) {
                                // Handle the checkbox state change when checked
                                String status = "sold";
                                // Update the status in Firebase database
                                itemReference.child("status").setValue(status);
                            } else {
                                // Handle the checkbox state change when unchecked
                                // You can add your logic here, for example, display a confirmation dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(Admin2.this);
                                builder.setTitle("Confirmation");
                                builder.setMessage("Are you sure you want to mark this item as 'ready stock'?");

                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String status = "ready stock";
                                        // Update the status in Firebase database
                                        itemReference.child("status").setValue(status);
                                        showToast("Item marked as 'ready stock'");
                                    }
                                });

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Revert the checkbox state back to checked
                                        soldCheckBox.setChecked(true);
                                    }
                                });

                                builder.show();
                            }
                        }
                    });
                    imageContainer.addView(soldCheckBox);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logout = findViewById(R.id.logoutPreAdminBtn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Admin2.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current scroll position
        outState.putInt("scroll_position", imageContainer.getScrollY());
    }

    private void createImageView(String imageUrl) {
        try {
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

        } catch (Exception e) {
            //nothing to do
        }
    }

    private void showToast(String message) {
        // Display a toast message (you can replace this with your preferred method)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
