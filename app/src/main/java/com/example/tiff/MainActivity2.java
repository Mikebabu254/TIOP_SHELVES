package com.example.tiff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity2 extends AppCompatActivity {
    EditText email, password, confirmPassword;
    Button register;
    FirebaseAuth auth;
    ProgressBar progress;

    /*@Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), User.class);
            startActivity(intent);
            finish();
        }
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        confirmPassword = findViewById(R.id.regConfirmPassword);
        register = findViewById(R.id.loginBtn);
        progress = findViewById(R.id.regProgress);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                try {
                    String emailTxt = String.valueOf(email.getText());
                    String passwordTxt = String.valueOf(password.getText());
                    String confirmPasswordTxt = String.valueOf(confirmPassword.getText());

                    if (TextUtils.isEmpty(emailTxt) || TextUtils.isEmpty(passwordTxt) || TextUtils.isEmpty(confirmPasswordTxt) ){
                        Toast.makeText(MainActivity2.this, "Please fill all the field", Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.GONE);
                    } else if (passwordTxt.length() < 6) {
                        Toast.makeText(MainActivity2.this, "password should have more than 6 characters", Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.GONE);
                    } else if (!confirmPasswordTxt.equals(passwordTxt)) {
                        Toast.makeText(MainActivity2.this, "confirmed password is incorrect", Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.GONE);
                    } else{
                        auth.createUserWithEmailAndPassword(emailTxt,passwordTxt)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progress.setVisibility(View.GONE);
                                        if(task.isSuccessful()){
                                            Toast.makeText(MainActivity2.this, "you have registered successful", Toast.LENGTH_SHORT).show();
                                            //onStart();
                                        }else {
                                            Toast.makeText(MainActivity2.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                            progress.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }

                }catch (Exception e){
                    Toast.makeText(MainActivity2.this, "make sure you have field all the forms", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                }


            }
        });


    }
}