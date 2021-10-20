package com.example.cafetea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Staff_login extends AppCompatActivity {
    Button login;
    EditText emailId, pass;
    String passCheck;
    private FirebaseAuth mAuth;
    private DatabaseReference databse;
    ProgressDialog progressDialog;
    TextView changePass, fcLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);
        login = findViewById(R.id.log_btn);
        emailId = findViewById(R.id.log_mail);
        pass = findViewById(R.id.log_password);
        fcLogin = findViewById(R.id.fc_login_page);
        changePass = findViewById(R.id.chage_password);
        mAuth = FirebaseAuth.getInstance();
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgetPassword.class);
                startActivity(intent);
            }
        });
        fcLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), fc_login.class);
                startActivity(intent);
            }
        });
        databse = FirebaseDatabase.getInstance().getReference().child(Database.WALLET_DATABASE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailId.getText().toString().isEmpty()) {
                    emailId.setError("Provide email ID");
                    emailId.requestFocus();
                }else if (pass.getText().toString().isEmpty()) {
                    pass.setError("Provide password");
                    pass.requestFocus();
                }else {
                    databse.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Wallet wallet = postSnapshot.getValue(Wallet.class);
                                if (wallet.getEmail().equals(emailId.getText().toString())) {
                                       // && wallet.getPassword().equals(pass.getText().toString())) {
                                    passCheck = wallet.getPassword();
                                    if (passCheck.equals("")) {
                                        Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
                                        startActivity(intent);
                                        Toast.makeText(getApplicationContext(), "Set Password and Login", Toast.LENGTH_SHORT).show();
                                    }else {
                                        signIn(emailId.getText().toString(), pass.getText().toString());
                                    }
                                }
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
    private void signIn(String email, String password) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), Staff_Main_Page.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            if (currentUser.getEmail().equals("fc@gmail.com")) {
                Intent intent = new Intent(getApplicationContext(), fc_main_page.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(getApplicationContext(), Staff_Main_Page.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Exit ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        moveTaskToBack(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}