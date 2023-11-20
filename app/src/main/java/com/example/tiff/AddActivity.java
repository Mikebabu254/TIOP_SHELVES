package com.example.tiff;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Button submit;
    private EditText product, price, description;
    private ProgressBar progress;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private Uri imageUri;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        imageView = findViewById(R.id.imageView);
        submit = findViewById(R.id.done);
        product = findViewById(R.id.editProductName);
        price = findViewById(R.id.editPrice);
        description = findViewById(R.id.editProductDescription);
        progress = findViewById(R.id.progressBar);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("itemsSold");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("item_images"); // Change this to your desired storage path

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);

                try {
                    String productTxt = product.getText().toString();
                    String priceTxt = price.getText().toString();
                    String descriptionTxt = description.getText().toString();

                    if (productTxt.isEmpty() || priceTxt.isEmpty() || descriptionTxt.isEmpty()) {
                        Toast.makeText(AddActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    } else {
                        DatabaseReference itemsReference = database.getReference("itemSold").child(productTxt);
                        itemsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                uploadImage(productTxt, priceTxt, descriptionTxt);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progress.setVisibility(View.GONE);
                                // Handle database error, if necessary
                            }
                        });
                    }
                } catch (Exception e) {
                    // Handle exceptions
                    progress.setVisibility(View.GONE);
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadImage(String productTxt, String priceTxt, String descriptionTxt) {
        if (imageUri != null) {
            String imageName = System.currentTimeMillis() + "." + getFileExtension(imageUri);
            StorageReference fileReference = storageReference.child(imageName);

            UploadTask uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    imageUrl = task.getResult().toString();

                    HelperClass helperClass = new HelperClass(productTxt, priceTxt, descriptionTxt, imageUrl);
                    reference.child(productTxt).setValue(helperClass);

                    Toast.makeText(AddActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    finish();
                } else {
                    // Handle failures
                    progress.setVisibility(View.GONE);
                    Toast.makeText(AddActivity.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            HelperClass helperClass = new HelperClass(productTxt, priceTxt, descriptionTxt);
            reference.child(productTxt).setValue(helperClass);

            Toast.makeText(AddActivity.this, "Successful", Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
            finish();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
