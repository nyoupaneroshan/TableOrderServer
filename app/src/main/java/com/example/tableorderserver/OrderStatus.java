package com.example.tableorderserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.tableorderserver.Common.Common;
import com.example.tableorderserver.Interface.ItemClickListener;
import com.example.tableorderserver.Module.MyResponse;
import com.example.tableorderserver.Module.Notification;
import com.example.tableorderserver.Module.Request;
import com.example.tableorderserver.Module.Sender;
import com.example.tableorderserver.Module.Token;
import com.example.tableorderserver.Remote.APIService;
import com.example.tableorderserver.ViewHolder.OrderDetail;
import com.example.tableorderserver.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    FirebaseDatabase db;
    DatabaseReference requests;

    MaterialSpinner spinner;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //conne
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Request");

        //initait
        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //init service
        mService = Common.getFCMClient();

        listOrder();
    }

    private void listOrder() {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, final Request request,final int position) {

                orderViewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                orderViewHolder.txtOrderStatus.setText(Common.convertCodeeToStatus(request.getStatus()));
                orderViewHolder.txtOrderAddress.setText(request.getAddress());
                orderViewHolder.txtOrderPhone.setText(request.getPhone());

                orderViewHolder.btnEdit.setOnClickListener(new  View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adapter.getRef(position)
                                                 .getKey(),adapter.getItem(position));

                    }
                });
                orderViewHolder.btnDelete.setOnClickListener(new  View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                      deleteOrder(adapter.getRef(position).getKey());
                    }
                });

                orderViewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent orderDetail = new Intent(OrderStatus.this, OrderDetail.class);
                        Common.currentRequest = request;
                        orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });

                orderViewHolder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent trackOrder = new Intent(OrderStatus.this,TrackingOrder.class);
                        Common.currentRequest = request;
                        startActivity(trackOrder);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();;
        recyclerView.setAdapter(adapter);
    }



    private void deleteOrder(String key) {
        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }

    private void showUpdateDialog(String key, final Request item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Status Update:");
        alertDialog.setMessage("Please select the status");

        LayoutInflater inflater = this.getLayoutInflater();
        final  View view = inflater.inflate(R.layout.order_update_layout,null);

        spinner = (MaterialSpinner)view.findViewById(R.id.spinStatus);
        spinner.setItems("Placed", "On the Way", "Delivered");

        alertDialog.setView(view);

        final  String localKey =key;

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                requests.child(localKey).setValue(item);
                adapter.notifyDataSetChanged();
                sendOrderStatusToUser(localKey,item);
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();


    }

    private void sendOrderStatusToUser(final String key,  final Request item) {
        DatabaseReference tokens = db.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Token token = postSnapshot.getValue(Token.class);
                            Notification notification = new Notification("Food Order", "Your Order "+key+" was updated");
                            Sender content = new Sender(token.getToken(),notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1){
                                                Toast.makeText(OrderStatus.this,"Order was updated!", Toast.LENGTH_SHORT).show();

                                            }
                                            else
                                                Toast.makeText(OrderStatus.this,"Order update failed but notification not sent!", Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
