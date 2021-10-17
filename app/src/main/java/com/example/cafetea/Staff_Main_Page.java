package com.example.cafetea;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class Staff_Main_Page extends AppCompatActivity {
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private StaffAdapter adapter;
    FloatingActionButton Old_order, order_now, signout;
    RelativeLayout relativeLayout;
    private DatabaseReference mDatabase, databse2;
    private ProgressDialog progressDialog;
    private List<Orders> uploads;
    private List<Wallet> upload;
    TextView amount, name, od_tv;
    int count1 = 1;
    String id;
    private TextView coffee, tea, snack, description, time, price, oder_no;
    public ImageView menu_btn;
    RadioButton ordered, pending, completed;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_staff_main_page);
        Old_order = findViewById(R.id.old_order);
        od_tv = findViewById(R.id.od_details);
        order_now = findViewById(R.id.order_Now);
        relativeLayout = findViewById(R.id.layout_empty);
        recyclerView = findViewById(R.id.recyclerView);
        amount = findViewById(R.id.wallet_amount);
        signout = findViewById(R.id.signout);
        name = findViewById(R.id.name_main);
        menu_btn = findViewById(R.id.menu_btn);
        oder_no = findViewById(R.id.od_no);
        coffee = findViewById(R.id.cof_price);
        tea = findViewById(R.id.tea_price);
        price = findViewById(R.id.final_price);
        snack = findViewById(R.id.snack_price);
        description = findViewById(R.id.des_price);
        time = findViewById(R.id.time_bo);
        ordered = findViewById(R.id.ordered_bt);
        pending = findViewById(R.id.pending_bt);
        completed = findViewById(R.id.completed_btn);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(this);
        uploads = new ArrayList<>();
        upload = new ArrayList<>();
        //displaying progress dialog while fetching images
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        recyclerView.setHasFixedSize(true);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String email = currentUser.getEmail();
        String e_mail = "";
        for(int i=0; i<email.length(); i++) {
            if(email.charAt(i) != '.' ) {
                e_mail += email.charAt(i);
            }
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Database.ADMIN_DATABASE);
        databse2 = FirebaseDatabase.getInstance().getReference().child(Database.WALLET_DATABASE);
        databse2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Wallet wallet = postSnapshot.getValue(Wallet.class);
                    if(wallet.getEmail().equals(email)) {
                        amount.setText(wallet.getWallet());
                        name.setText(wallet.getFull_name());
                        id = wallet.getUser_id();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog
                uploads.clear();
                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Orders order = postSnapshot.getValue(Orders.class);
                    order.setKey(postSnapshot.getKey());
                    if (!order.getStatus().equals("Completed") && (order.getId().equals(id))) {
                        uploads.add(order);
                    }
                }
                //creating adapter
                adapter = new StaffAdapter(getApplicationContext(),uploads);
                //adding adapter to recyclerview
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
                if (uploads.size() > 0) {
                    count1++;
                    od_tv.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                else {
                    count1 = 1;
                    relativeLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
        order_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count1 == 1) {
                    IntentIntegrator integrator = new IntentIntegrator(Staff_Main_Page.this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                    integrator.setPrompt("Scan");
                    integrator.setCameraId(0);
                    integrator.setBeepEnabled(true);
                    integrator.initiateScan();
                    //Intent intent = new Intent(getApplicationContext(), Staff_Order_Details.class);
                    //startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"Your order already in queue!", Toast.LENGTH_LONG).show();
                }

            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AlertDialog.Builder dailog = new AlertDialog.Builder(v.getRootView().getContext());
                dailog.setMessage("Are you sure? you want to signout.");
                dailog.setTitle("Sign Out");
                dailog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(),Staff_login.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dailog.create();
                alertDialog.show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");
            } else {
                Log.e("Scan", "Scanned");
                if (result.getContents().equals("order now")) {
                    Intent intent = new Intent(getApplicationContext(), Staff_Order_Details.class);
                    startActivity(intent);
                    //Toast.makeText(this,result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
