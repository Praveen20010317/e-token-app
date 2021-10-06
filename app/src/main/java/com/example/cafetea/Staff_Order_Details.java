package com.example.cafetea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Staff_Order_Details extends AppCompatActivity {
    TextView descrption, time, name, id;
    Spinner coffee, tea, snack;
    Button od_now;
    String dept = "";
    String order_no;
    String email;
    int amt_wallet, updateWallet;
    ProgressDialog progressDialog;
    String price, category;
    String qnt[] = {"0","1"};
    DatabaseReference mdatabase, adminDB, orderDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_staff_order_details);
        od_now = findViewById(R.id.order);
        snack = findViewById(R.id.snack_qnt);
        coffee = findViewById(R.id.coffee_qnt);
        tea = findViewById(R.id.tea_qnt);
        time = findViewById(R.id.time_od);
        descrption = findViewById(R.id.od_dep);
        name = findViewById(R.id.od_name);
        id = findViewById(R.id.od_id);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,qnt);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coffee.setAdapter(spinnerArrayAdapter);
        tea.setAdapter(spinnerArrayAdapter);
        snack.setAdapter(spinnerArrayAdapter);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        email = currentUser.getEmail();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        SimpleDateFormat formatter1 = new SimpleDateFormat("HHmmss");
        Date date1 = new Date();
        order_no = formatter1.format(date1);
        time.setText(formatter.format(date));
        //mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Database.ORDER_DATABASE).child(e_mail);
        adminDB = FirebaseDatabase.getInstance().getReference().child(Database.ADMIN_DATABASE);
        //orderDB = FirebaseDatabase.getInstance().getReference().child(Database.TOTAL_COUNT);
        mdatabase = FirebaseDatabase.getInstance().getReference().child(Database.WALLET_DATABASE);
        /*orderDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                OrderSlip orderSlip = snapshot.getValue(OrderSlip.class);
                order_no = orderSlip.getTotal_count();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Wallet wallet = postSnapshot.getValue(Wallet.class);
                    if(wallet.getEmail().equals(email)) {
                        name.setText(wallet.getFull_name());
                        id.setText(wallet.getUser_id());
                        amt_wallet = Integer.parseInt(wallet.getWallet());
                        dept = wallet.getDept();
                        category = wallet.getCategory();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        od_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cof = coffee.getSelectedItem().toString();
                String te = tea.getSelectedItem().toString();
                String sna = snack.getSelectedItem().toString();
                int cof_qnt = Integer.parseInt(cof);
                int tea_qnt = Integer.parseInt(te);
                int snack_qnt = Integer.parseInt(sna);
                int count = cof_qnt + tea_qnt + snack_qnt;
                updateWallet = amt_wallet - ((count) * 5);
                price = Integer.toString((count) * 5);
                if (count == 0) {
                    Toast.makeText(getApplicationContext(), "select quantity correctly !", Toast.LENGTH_LONG).show();
                }else if (updateWallet >= 0) {
                    upload();
                }else if (updateWallet < 0) {
                    Toast.makeText(getApplicationContext(), "Please top up wallet available balance is : "+amt_wallet, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void upload() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Order Placed");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        adminDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = "Pending";
                String des = "---";
                if(descrption.getText().toString().equals("")) {
                    descrption.setText(des);
                }
                String location = adminDB.push().getKey();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Orders orders = new Orders(order_no,id.getText().toString(), name.getText().toString(), coffee.getSelectedItem().toString(),
                        tea.getSelectedItem().toString(),
                        snack.getSelectedItem().toString(),status,
                        descrption.getText().toString(), time.getText().toString(), price, dept, location, uid, category);

                //adminDB.push().getKey()
                adminDB.child(location).setValue(orders);
                FirebaseDatabase.getInstance().getReference(Database.WALLET_DATABASE).child(id.getText().toString()).child("wallet").setValue(Integer.toString(updateWallet))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                finish();

                            }
                        });
                //mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(orders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}