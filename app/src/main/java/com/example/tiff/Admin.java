package com.example.tiff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Admin extends AppCompatActivity {
    Button logout,add,deleteButton;
    private LinearLayout imageContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        imageContainer = findViewById(R.id.imageContainerAdmin);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("itemsSold");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageContainer.removeAllViews();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HelperClass helperClass = dataSnapshot.getValue(HelperClass.class);

                    createImageView(helperClass.getImageUrl());

                    TextView nameTextView = new TextView(Admin.this);
                    nameTextView.setText(helperClass.getProductName());
                    imageContainer.addView(nameTextView);

                    TextView descriptionTextView = new TextView(Admin.this);
                    descriptionTextView.setText(helperClass.getDescription());
                    imageContainer.addView(descriptionTextView);

                    TextView priceTextView = new TextView(Admin.this);
                    priceTextView.setText(helperClass.getPrice());
                    imageContainer.addView(priceTextView);

                    CheckBox soldCheckBox = new CheckBox(Admin.this);
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
                            }else {
                                // Handle the checkbox state change when unchecked
                                // You can add your logic here, for example, display a confirmation dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
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

                    createDeleteButton(dataSnapshot.getKey(), helperClass.getImageUrl());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logout= findViewById(R.id.logoutAdminBtn);
        add = findViewById(R.id.addBtn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Admin.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin.this, AddActivity.class);
                startActivity(intent);

            }
        });

    }

    private void createImageView(String imageUrl) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(5, 20, 5, 10);
        imageView.setLayoutParams(layoutParams);
        imageContainer.addView(imageView);

        // Load the image into the ImageView using Glide or any other image loading library
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }

    private void createDeleteButton(final String imageKey, final String imageUrl) {
        Button deleteButton = new Button(Admin.this);
        deleteButton.setText("Delete Image");

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(imageKey, imageUrl);
            }
        });

        // Add the Button to the LinearLayout
        imageContainer.addView(deleteButton);
    }

    private void showDeleteConfirmationDialog(final String imageKey, final String imageUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Image");
        builder.setMessage("Are you sure you want to delete this image?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>(){
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully, now delete from Firebase Database
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("itemsSold").child(imageKey);
                            databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    showToast("Image deleted");
                                    // Remove the views associated with the deleted image

                                    int deleteButtonIndex = imageContainer.indexOfChild(deleteButton);
                                    if (deleteButtonIndex != -1) {
                                        int imageIndex = deleteButtonIndex - 3;
                                        int nameIndex = deleteButtonIndex - 2;
                                        int descriptionIndex = deleteButtonIndex - 1;

                                        if (imageIndex >= 0 && imageIndex < imageContainer.getChildCount()) {
                                            imageContainer.removeViewAt(imageIndex); // Remove image
                                        }

                                        if (nameIndex >= 0 && nameIndex < imageContainer.getChildCount()) {
                                            imageContainer.removeViewAt(nameIndex); // Remove name
                                        }

                                        if (descriptionIndex >= 0 && descriptionIndex < imageContainer.getChildCount()) {
                                            imageContainer.removeViewAt(descriptionIndex); // Remove description
                                        }

                                        imageContainer.removeView(deleteButton); // Remove price and delete button
                                    }
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showToast("Failed to delete image from database");
                                }
                            });
                        }
                    });
                }catch (Exception e){
                    //nothing to do
                }


            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "No," do nothing or handle accordingly
            }
        });

        builder.show();
    }

    private void showToast(String message) {
        // Display a toast message (you can replace this with your preferred method)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}