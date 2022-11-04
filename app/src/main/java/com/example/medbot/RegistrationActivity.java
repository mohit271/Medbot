package com.example.medbot;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.medbot.databinding.ActivityRegistration2Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistration2Binding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding= ActivityRegistration2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database=FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString().trim()).matches()){
                    binding.etEmail.setError("Invalid email Address");
                    binding.etEmail.requestFocus();
                    return;
                }
                if(binding.etPassword.getText().toString().trim().length()<6){
                    binding.etPassword.setError("Invalid Password");
                    binding.etPassword.requestFocus();
                    return;
                }
//                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString())
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    User user = new User(binding.etName.getText().toString(),binding.etEmail.getText().toString(), binding.etPassword.getText().toString());
                                    String id = task.getResult().getUser().getUid();
//                                    database.getReference().child("Users").child(id).setValue(user);
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    Toast.makeText(RegistrationActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                    Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
//                                    Bundle dataBundle = new Bundle();
//                                    dataBundle.putString("keyid","thevalue");
//                                    mainIntent.putExtras(dataBundle);
                                    startActivity(mainIntent);
                                    // FirebaseUser user = mAuth.getCurrentUser();
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegistrationActivity.this, task.getException().toString(),
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);

                                }
                            }
                        });
            }
        });

        if(mAuth.getCurrentUser()!=null){
            Intent intent= new Intent(RegistrationActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.tvHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this,LogInActivity.class);
                startActivity(intent);
            }
        });


    }
}