package com.example.levoyage.ui.food;

import android.os.Bundle;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.levoyage.R;
import com.example.levoyage.SearchFragment;
import com.google.firebase.database.annotations.NotNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FoodFragment extends SearchFragment {

    private ArrayList<FoodItineraryItem> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private SearchView searchView;
    private ProgressBar progressBar;
    private RequestQueue queue;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.searchRecycler);
        searchView = view.findViewById(R.id.searchView);
        progressBar = view.findViewById(R.id.searchProgressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                list = new ArrayList<>();
                queue = Volley.newRequestQueue(getContext());
                getLocationID(query, queue);
//                extractInfo("298570");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { return false; }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FoodAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void extractInfo(String locationID) {
        String foodURL = "https://travel-advisor.p.rapidapi.com/restaurants/list?currency=USD&location_id=" + locationID;
        JsonObjectRequest searchFood = new JsonObjectRequest(Request.Method.GET,
                foodURL, null, response -> {
                    try {
                        if (response.isNull("data")) {
                            Toast.makeText(getContext(), "No restaurants found.", Toast.LENGTH_SHORT).show();
                        } else {
                            JSONArray arr = response.getJSONArray("data");
                            for (int i = 0; i < arr.length(); i++) {
                                if (i != 4 && i != 11 && i != 18) { // API call returns different data at these positions
                                    JSONObject item = arr.getJSONObject(i);
                                    FoodItineraryItem restaurant = new FoodItineraryItem();
                                    restaurant.setId(getFromJson("location_id", item));
                                    restaurant.setLocation(getFromJson("name", item));
                                    restaurant.setRating(getFromJson("rating", item));
                                    restaurant.setAddress(getFromJson("address", item));
                                    restaurant.setLink(getURLFromJson("website", item));
                                    restaurant.setPrice(getFromJson("price_level", item));
                                    restaurant.setDescription(getFromJson("description", item));
                                    restaurant.setImageURL(getImageURLFromJson(item));
                                    restaurant.setCategory(getFromJsonArray("cuisine", "name", item));

                                    list.add(restaurant);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Error. Please try again.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new FoodAdapter(getContext(), list);
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(ProgressBar.GONE);
                }, e -> Toast.makeText(getContext(), "Error. Please try again.", Toast.LENGTH_SHORT).show())
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("x-rapidapi-key", "864bde7699msh8d3f983cd6c3ed2p11e31ajsn15e541ffb40a");
                h.put("x-rapidapi-host", "travel-advisor.p.rapidapi.com");
                return h;
            }
        };
        searchFood.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(searchFood);
    }
}