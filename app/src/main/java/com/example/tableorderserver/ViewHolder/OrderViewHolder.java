package com.example.tableorderserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tableorderserver.Interface.ItemClickListener;
import com.example.tableorderserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder
        {
            public Button btnEdit, btnDelete,btnDetail, btnDirection;
    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;

    public OrderViewHolder( View itemView) {
        super(itemView);

        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);

        btnEdit = itemView.findViewById(R.id.btnEdit);
        btnDelete = itemView.findViewById(R.id.btnDelete);
        btnDetail = itemView.findViewById(R.id.btnDetail);
        btnDirection = itemView.findViewById(R.id.btnDirection);

    }

}
