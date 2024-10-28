package com.rhix.ordering;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class CartFragment extends Fragment {

    private LinearLayout cartItemsContainer;
    private TextView totalItemsTextView;
    private TextView totalAmountTextView;
    private TextView noOrdersMessage; // Reference to the no orders message
    private Button checkoutButton;

    private List<CartItem> cartItems = new ArrayList<>(); // Store cart items

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        cartItemsContainer = view.findViewById(R.id.cart_items_container);
        totalItemsTextView = view.findViewById(R.id.total_items);
        totalAmountTextView = view.findViewById(R.id.total_amount);
        noOrdersMessage = view.findViewById(R.id.no_orders_message); // Initialize the no orders message
        checkoutButton = view.findViewById(R.id.checkout_button);

        loadCart(); // Load items from the cart
        updateCartView(); // Update the UI based on loaded cart items

        checkoutButton.setOnClickListener(v -> checkout());

        return view;
    }

    private void loadCart() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cart", Context.MODE_PRIVATE);

        // Check if Shared Preferences contain any entries
        if (sharedPreferences.getAll().isEmpty()) {
            // No items in Shared Preferences, initialize an empty cart
            cartItems.clear(); // Clear any existing items
            return; // Exit the method
        }

        Map<String, ?> allEntries = sharedPreferences.getAll(); // Get all entries in SharedPreferences

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("product_")) { // Check if the key is for a product
                Object value = entry.getValue();
                String productJson = null;

                // Check if the value is a String
                if (value instanceof String) {
                    productJson = (String) value; // Get the product JSON
                } else {
                    continue; // Skip if it's not a String
                }

                int quantity = sharedPreferences.getInt(key + "_quantity", 0); // Get the quantity

                Product product = new Gson().fromJson(productJson, Product.class); // Convert JSON back to Product object
                cartItems.add(new CartItem(product, quantity)); // Add to cart items list
            }
        }

        // After loading, check if cart is empty and update UI accordingly
        updateCartView();
    }

    private void updateCartView() {
        cartItemsContainer.removeAllViews(); // Clear existing views
        double totalAmount = 0.0;

        // Check if the cart is empty
        if (cartItems.isEmpty()) {
            // Make sure all UI elements are available before modifying them
            if (noOrdersMessage != null) {
                noOrdersMessage.setVisibility(View.VISIBLE); // Show the no orders message
            }
            if (totalItemsTextView != null) {
                totalItemsTextView.setVisibility(View.GONE); // Hide total items
            }
            if (totalAmountTextView != null) {
                totalAmountTextView.setVisibility(View.GONE); // Hide total amount
            }
            if (checkoutButton != null) {
                checkoutButton.setVisibility(View.GONE); // Hide checkout button
            }
        } else {
            noOrdersMessage.setVisibility(View.GONE); // Hide the no orders message
            totalItemsTextView.setVisibility(View.VISIBLE); // Show total items
            totalAmountTextView.setVisibility(View.VISIBLE); // Show total amount
            checkoutButton.setVisibility(View.VISIBLE); // Show checkout button

            for (CartItem item : cartItems) {
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_cart, cartItemsContainer, false);

                TextView nameTextView = itemView.findViewById(R.id.cart_item_name);
                TextView quantityTextView = itemView.findViewById(R.id.cart_item_quantity);
                TextView priceTextView = itemView.findViewById(R.id.cart_item_price);

                // Check if the item is not null before accessing it
                if (item.getProduct() != null) {
                    nameTextView.setText(item.getProduct().getName());
                    quantityTextView.setText("Quantity: " + item.getQuantity());
                    double itemTotal = item.getProduct().getPrice() * item.getQuantity(); // Calculate total for item
                    priceTextView.setText(String.format("Total: Php%.2f", itemTotal));

                    totalAmount += itemTotal; // Update overall total
                    cartItemsContainer.addView(itemView); // Add item view to the container
                }
            }

            totalItemsTextView.setText("Total Items: " + cartItems.size());
            totalAmountTextView.setText(String.format("Total Amount: Php%.2f", totalAmount));
        }
    }

    private void checkout() {
        // Call the method to submit the order to the API
        submitOrderToApi();
    }

    private void submitOrderToApi() {
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject orderDetails = new JSONObject();
        JSONArray itemsArray = new JSONArray();
        double totalPrice = 0.0;

        // Prepare items and calculate total price
        for (CartItem item : cartItems) {
            try {
                JSONObject itemObject = new JSONObject();
                itemObject.put("product_id", item.getProduct().getId());
                itemObject.put("quantity", item.getQuantity());
                itemsArray.put(itemObject);
                totalPrice += item.getProduct().getPrice() * item.getQuantity();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error preparing order data", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        try {
            orderDetails.put("items", itemsArray);
            orderDetails.put("total_price", totalPrice);
            orderDetails.put("api_key", "7999b0bd43fe96b083f8430a0de1cc65ecf3902993d15ffb6d3a287f9e939000");
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error preparing order data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert JSONObject to String
        String orderDetailsString = orderDetails.toString();

        // Make the API call
        ApiOrder apiOrder = ApiClient.getClient().create(ApiOrder.class);
        Call<ApiResponse> call = apiOrder.checkout(orderDetailsString);
        Log.d("RESULT", "Data: " + orderDetailsString);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    resetCart();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProductFragment())
                            .addToBackStack(null)
                            .commit();
                    // Highlight the corresponding menu item in BottomNavigationView
                    BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                    bottomNavigationView.setSelectedItemId(R.id.nav_home);
                } else {
                    Toast.makeText(getContext(), "Checkout failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Checkout failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("RESULT", "Failure: " + t.getMessage());
            }
        });
    }


    private void resetCart() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all entries in the cart
        editor.apply(); // Apply changes
    }
}