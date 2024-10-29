package com.rhix.ordering;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {
    private LinearLayout productContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        productContainer = view.findViewById(R.id.product_container);

        fetchProducts();

        return view;
    }

    private void fetchProducts() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // update to api values that matches your api requirement
        Call<ProductResponse> call = apiService.getProducts("7999b0bd43fe96b083f8430a0de1cc65ecf3902993d15ffb6d3a287f9e939");

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getProducts();
                    displayProducts(products);
                } else {
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProducts(List<Product> products) {
        for (Product product : products) {
            View productView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout, productContainer, false);

            TextView nameTextView = productView.findViewById(R.id.product_name);
            TextView priceTextView = productView.findViewById(R.id.product_price);
            TextView descriptionTextView = productView.findViewById(R.id.product_description); // Get the description TextView
            ImageView imageView = productView.findViewById(R.id.product_image);

            // Quantity control
            final TextView quantityTextView = productView.findViewById(R.id.quantity_text);
            Button increaseButton = productView.findViewById(R.id.increase_button);
            Button decreaseButton = productView.findViewById(R.id.decrease_button);
            Button addToCartButton = productView.findViewById(R.id.add_to_cart_button);

            // Initial quantity
            int[] quantity = {1}; // Use an array to modify the quantity

            // Set product details
            nameTextView.setText(product.getName());
            priceTextView.setText(String.format("Php%s", product.getPrice())); // Assuming price is a string
            descriptionTextView.setText(product.getDescription()); // Set the description text
            String fullImageUrl = "{your url base}" + product.getImageUrl(); // Adjust based on your URL structure

            Picasso.get()
                    .load(fullImageUrl)
                    //.placeholder(R.drawable.product_image)
                    //.error(R.drawable.error)
                    .into(imageView);

            // Update quantity text view
            quantityTextView.setText(String.valueOf(quantity[0]));

            // Increase button
            increaseButton.setOnClickListener(v -> {
                quantity[0]++;
                quantityTextView.setText(String.valueOf(quantity[0]));
            });

            // Decrease button
            decreaseButton.setOnClickListener(v -> {
                if (quantity[0] > 1) { // Ensure quantity does not go below 1
                    quantity[0]--;
                    quantityTextView.setText(String.valueOf(quantity[0]));
                }
            });

            // Add to cart button
            addToCartButton.setOnClickListener(v -> {
                saveToCart(product, quantity[0]);
                Toast.makeText(getContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
            });

            productContainer.addView(productView);
        }
    }

    private void saveToCart(Product product, int quantity) {
        // Get SharedPreferences for storing the cart
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Create a unique key for the product in the cart
        String key = "product_" + product.getId();

        // Prepare the product data as a JSON string
        Gson gson = new Gson();
        String productJson = gson.toJson(product); // Convert product object to JSON

        // Save product details in SharedPreferences
        editor.putString(key, productJson);
        editor.putInt(key + "_quantity", quantity); // Save the quantity as well
        editor.apply(); // Commit the changes
    }
}
