package com.example.tiff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    TextView register;
    ProgressBar progress;
    FirebaseAuth auth;
    Button login;
    EditText email,password;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            String userEmail = currentUser.getEmail(); // Assuming getEmail() returns the user's email

            if (userEmail != null && userEmail.equals("admin@mail.com")) {
                Intent adminIntent = new Intent(getApplicationContext(), Admin.class);
                startActivity(adminIntent);
            } else if (userEmail != null && userEmail.equals("admin2@mail.com")) {
                Intent adminIntent = new Intent(getApplicationContext(), Admin2.class);
                startActivity(adminIntent);
            } else {
                Intent userIntent = new Intent(getApplicationContext(), DisplayImageActivity.class);
                startActivity(userIntent);
            }

            finish();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = findViewById(R.id.mainProgressBar);
        login  = findViewById(R.id.loginBtn);
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        register = findViewById(R.id.registerTxtBtn);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);

                String emailTxt = String.valueOf(email.getText());
                String passwordTxt = String.valueOf(password.getText());

                try{
                    if(TextUtils.isEmpty(emailTxt)){
                        Toast.makeText(MainActivity.this, "please enter you email", Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.GONE);
                        return;
                    }
                    if(TextUtils.isEmpty(passwordTxt)){
                        Toast.makeText(MainActivity.this, "please enter your password", Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.GONE);
                        return;
                    }

                    auth.signInWithEmailAndPassword(emailTxt, passwordTxt)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progress.setVisibility(View.VISIBLE);
                                    if (task.isSuccessful()) {
                                        if(emailTxt.equals("admin@mail.com")){
                                            Toast.makeText(MainActivity.this, "login successful", Toast.LENGTH_SHORT).show();
                                            Intent loginAdmin = new Intent(getApplicationContext(), Admin.class);
                                            startActivity(loginAdmin);
                                            finish();
                                        }else if(emailTxt.equals("admin2@mail.com")){
                                            Toast.makeText(MainActivity.this, "login successful", Toast.LENGTH_SHORT).show();
                                            Intent loginAdmin = new Intent(getApplicationContext(), Admin2.class);
                                            startActivity(loginAdmin);
                                            finish();
                                        }else{
                                            Toast.makeText(MainActivity.this, "login successful", Toast.LENGTH_SHORT).show();
                                            Intent login = new Intent(getApplicationContext(), DisplayImageActivity.class);
                                            startActivity(login);
                                            finish();
                                        }

                                    }else {
                                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        progress.setVisibility(View.GONE);
                                    }
                                }
                            });

                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "please enter your credentials", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

    }
}