package com.example.tableorderserver.ViewHolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.tableorderserver.Common.Common;
import com.example.tableorderserver.R;

public class OrderDetail extends AppCompatActivity {

    TextView order_id,order_phone,order_address,order_total,order_comment;
    String order_id_value="";
    RecyclerView lstFood;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        order_id = findViewById(R.id.order_id);
        order_phone = findViewById(R.id.order_phone);
        order_address = findViewById(R.id.order_address);
        order_total = findViewById(R.id.order_total);
        order_comment = findViewById(R.id.order_comment);

        lstFood = findViewById(R.id.lstFoods);
        lstFood.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstFood.setLayoutManager(layoutManager);

        if (getIntent() != null){
            order_id_value = getIntent().getStringExtra("OrderId");
        }

        //setting values
        order_id.setText(Common.currentRequest.getPhone());
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTotal());
        order_address.setText(Common.currentRequest.getAddress());
        order_comment.setText(Common.currentRequest.getComment());

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        lstFood.setAdapter(adapter);
    }
}