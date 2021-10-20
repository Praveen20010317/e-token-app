package com.example.cafetea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fc_Order_List extends AppCompatActivity {
    RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private StaffAdapter adapter;
    private DatabaseReference mDatabase, confirmDB;
    private ProgressDialog progressDialog;
    private List<Orders> uploads;
    String id;
    TextView f_id;
    Button confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fc_order_list);
        relativeLayout = findViewById(R.id.layout_empty);
        recyclerView = findViewById(R.id.recyclerView);
        confirm = findViewById(R.id.confirm_od);
        f_id = findViewById(R.id.faculty_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(this);
        uploads = new ArrayList<>();
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        recyclerView.setHasFixedSize(true);
        id = getIntent().getStringExtra("id");
        f_id.setText(id);
        confirmDB = FirebaseDatabase.getInstance().getReference().child(Database.ADMIN_DATABASE);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Database.REMOTE_DATABASE);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog
                uploads.clear();
                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Orders order = postSnapshot.getValue(Orders.class);
                    order.setKey(postSnapshot.getKey());
                    if (order.getStatus().equals("Pending") && (order.getId().equals(id))) {
                        uploads.add(order);
                    }
                }
                //creating adapter
                adapter = new StaffAdapter(getApplicationContext(),uploads);
                //adding adapter to recyclerview
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
                if (uploads.size() > 0) {
                    relativeLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                else {
                    relativeLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Orders order = postSnapshot.getValue(Orders.class);
                            order.setKey(postSnapshot.getKey());
                            if (order.getStatus().equals("Pending") && (order.getId().equals(id))) {
                                Orders orders = new Orders(order.getOrder_no(),id, order.getName(), order.getCoffee(),
                                        order.getTea(),
                                        order.getSnacks(),order.getStatus(),
                                        order.description, order.getTime(), order.getAmount(),
                                        order.getDept(), order.getLocationK(), order.getUid(), order.getCategory());
                                FirebaseDatabase.getInstance().getReference(Database.ADMIN_DATABASE).child(order.getLocationK()).setValue(orders)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mDatabase.child(order.getLocationK()).removeValue();
                                            Toast.makeText(getApplicationContext(),"Order Completed"+order.getOrder_no() ,Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(),"Error : " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}