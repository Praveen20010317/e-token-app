package com.example.cafetea;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {
    private Context context;
    private List<Orders> orders;

    public StaffAdapter(Context context, List<Orders> orders) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blog_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        final Orders order = orders.get(position);
        holder.oder_no.setText(order.getOrder_no());
        holder.coffee.setText(order.getCoffee());
        holder.tea.setText(order.getTea());
        holder.snack.setText(order.getSnacks());
        holder.price.setText(order.getAmount());
        holder.description.setText(order.getDescription());
        holder.time.setText(order.getTime());
        if (order.getStatus().equals( "Ordered")) {
            holder.ordered.setChecked(true);
            holder.pending.setChecked(true);
        }if(order.getStatus().equals("Pending")) {
            holder.ordered.setChecked(true);
            holder.pending.setChecked(true);
        }if ( order.getStatus().equals("Completed")) {
            holder.ordered.setChecked(true);
            holder.pending.setChecked(true);
            holder.completed.setChecked(true);
        }
        String email;
        String e_mail = "";
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        email = currentUser.getEmail();
        for(int i=0; i<email.length(); i++) {
            if(email.charAt(i) != '.' ) {
                e_mail += email.charAt(i);
            }
        }
        final String[] amt = {""};
        final String[] od_n = {""};
        /*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Database.TOTAL_COUNT);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                OrderSlip wallet = snapshot.getValue(OrderSlip.class);
                od_n[0] = wallet.getTotal_count();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        DatabaseReference walletDB = FirebaseDatabase.getInstance().getReference().child(Database.WALLET_DATABASE);
        walletDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Wallet wallet = postSnapshot.getValue(Wallet.class);
                    if(wallet.getEmail().equals(email)) {
                        amt[0] = wallet.getWallet();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(Database.ORDER_DATABASE);
        if ( !order.getStatus().equals("Completed")) {
            String finalE_mail = e_mail;
            holder.menu_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context,v);
                    popupMenu.inflate(R.menu.menu);
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            final AlertDialog.Builder dailog = new AlertDialog.Builder(v.getRootView().getContext());
                            dailog.setMessage("Are you Surely wants to delete?");
                            dailog.setTitle("Delete");
                            dailog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String key =order.getKey();
                                    FirebaseDatabase.getInstance().getReference(Database.ADMIN_DATABASE).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            int updateWallet = Integer.parseInt(order.getAmount())+Integer.parseInt(amt[0]);
                                            FirebaseDatabase.getInstance().getReference(Database.WALLET_DATABASE).child(order.getId()).child("wallet").setValue(Integer.toString(updateWallet))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(context,"Deleted Successfully"+order.getOrder_no() ,Toast.LENGTH_SHORT).show();
                                                            /*FirebaseDatabase.getInstance().getReference(Database.TOTAL_COUNT).setValue(orderSlip)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                        }
                                                                    });*/
                                                        }
                                                    });
                                        }
                                    });
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialog = dailog.create();
                            alertDialog.show();
                            return false;
                        }
                    });
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
    public  class ViewHolder extends RecyclerView.ViewHolder{

        private TextView coffee, tea, snack, description, time, price, oder_no;
        public ImageView menu_btn;
        RadioButton ordered, pending, completed;

        public ViewHolder(View itemView) {
            super(itemView);
            menu_btn = itemView.findViewById(R.id.menu_btn);
            oder_no = itemView.findViewById(R.id.od_no);
            coffee = itemView.findViewById(R.id.cof_price);
            tea = itemView.findViewById(R.id.tea_price);
            price = itemView.findViewById(R.id.final_price);
            snack = itemView.findViewById(R.id.snack_price);
            description = itemView.findViewById(R.id.des_price);
            time = itemView.findViewById(R.id.time_bo);
            ordered = itemView.findViewById(R.id.ordered_bt);
            pending = itemView.findViewById(R.id.pending_bt);
            completed = itemView.findViewById(R.id.completed_btn);
        }
    }
}
