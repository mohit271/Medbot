package com.example.medbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.medbot.databinding.ActivityLogInBinding;
import com.example.medbot.databinding.ActivityRegistration2Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    ActivityLogInBinding binding;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding= ActivityLogInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString().trim()).matches()){
                    binding.etEmail.setError("Invalid email Address");
                    binding.etEmail.requestFocus();
                    return;
                }
                if(binding.etPassword.getText().toString().trim().length()<6) {
                    binding.etPassword.setError("Invalid Password");
                    binding.etPassword.requestFocus();
                    return;
                }
//                progressDialog.show();
                mAuth.signInWithEmailAndPassword(binding.etEmail.getText().toString(),binding.etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(LogInActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent(LogInActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(LogInActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        if(mAuth.getCurrentUser()!=null){
            Intent intent= new Intent(LogInActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding.tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });


    }
}