package com.rhix.ordering;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment {
    private LinearLayout ordersContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        ordersContainer = view.findViewById(R.id.order_container);
        fetchOrders();
        return view;
    }

    private void fetchOrders() {
        ApiOrder apiOrder = ApiClient.getClient().create(ApiOrder.class);
        Call<OrderListResponse> call = apiOrder.listOrders("7999b0bd43fe96b083f8430a0de1cc65ecf3902993d15ffb6d3a287f9e939000");

        call.enqueue(new Callback<OrderListResponse>() {
            @Override
            public void onResponse(Call<OrderListResponse> call, Response<OrderListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderItem> orders = response.body().getOrders();
                    displayOrders(orders);
                } else {
                    Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderListResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayOrders(List<OrderItem> orders) {
        for (OrderItem order : orders) {
            View orderView = LayoutInflater.from(getContext()).inflate(R.layout.item_order, ordersContainer, false);

            TextView customerNameTextView = orderView.findViewById(R.id.customerNameTextView);
            TextView orderDetailsTextView = orderView.findViewById(R.id.orderDetailsTextView);
            TextView totalPriceTextView = orderView.findViewById(R.id.totalPriceTextView);
            TextView createdAtTextView = orderView.findViewById(R.id.createdAtTextView);

            // Set order details
            customerNameTextView.setText(order.getCustomerName());
            orderDetailsTextView.setText(order.getOrderDetails());
            totalPriceTextView.setText(String.format("Total: Php%.2f", order.getTotalPrice()));
            createdAtTextView.setText("Date: " + order.getOrderDate());

            ordersContainer.addView(orderView);
        }
    }
}
