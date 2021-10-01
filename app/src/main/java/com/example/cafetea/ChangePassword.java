package com.example.cafetea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity {
    TextView emp_id, emp_email, new_pass, confirm_pass;
    Button change;
    private FirebaseAuth mAuth;
    private DatabaseReference databse;
    ProgressDialog progressDialog;
    boolean checkPass = false;
    boolean check = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        emp_email = findViewById(R.id.ch_email);
        emp_id = findViewById(R.id.ch_empId);
        new_pass = findViewById(R.id.ch_new_pass);
        confirm_pass = findViewById(R.id.ch_confirm_pass);
        change = findViewById(R.id.change_btn);
        mAuth = FirebaseAuth.getInstance();
        databse = FirebaseDatabase.getInstance().getReference().child(Database.WALLET_DATABASE);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emp_email.getText().toString().isEmpty()) {
                    emp_email.setError("Provide email Id");
                    emp_email.requestFocus();
                }else if (emp_id.getText().toString().isEmpty()) {
                    emp_id.setError("Provide ID");
                    emp_id.requestFocus();
                }else if (new_pass.getText().toString().isEmpty()) {
                    new_pass.setError("Provide new password");
                    new_pass.requestFocus();
                }else if (confirm_pass.getText().toString().isEmpty()) {
                    confirm_pass.setError("Provide confirm password");
                    confirm_pass.requestFocus();
                }else if (!(new_pass.getText().toString().equals(confirm_pass.getText().toString()))) {
                    Toast.makeText(getApplicationContext(), "Two password fields are not match", Toast.LENGTH_SHORT).show();
                }
                else {
                    databse.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Wallet wallet = postSnapshot.getValue(Wallet.class);
                                if (wallet.getEmail().equals(emp_email.getText().toString()) && wallet.getUser_id().equals(emp_id.getText().toString()) && wallet.getPassword().equals("")) {
                                    check = false;
                                    checkPass = false;
                                    changePassword(new_pass.getText().toString(), emp_email.getText().toString(), emp_id.getText().toString());
                                }
                                if (wallet.getEmail().equals(emp_email.getText().toString()) && wallet.getUser_id().equals(emp_id.getText().toString()) && !wallet.getPassword().equals("")) {
                                    checkPass = true;
                                }
                            }
                            if (checkPass) {
                                Toast.makeText(getApplicationContext(), "select forget password", Toast.LENGTH_SHORT).show();
                            }if (check){
                                Toast.makeText(getApplicationContext(), "enter field correctly", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    private void changePassword(String password,String emailId,String id) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(emailId, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser();
                            FirebaseDatabase.getInstance().getReference(Database.WALLET_DATABASE).child(id).child("password")
                                    .setValue(password)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            FirebaseDatabase.getInstance().getReference(Database.WALLET_DATABASE).child(id).child("uid")
                                                    .setValue(uid)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            progressDialog.dismiss();
                                                            Intent intent = new Intent(getApplicationContext(), Staff_Main_Page.class);
                                                            startActivity(intent);
                                                            Toast.makeText(getApplicationContext(),"Authentication successfully",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}