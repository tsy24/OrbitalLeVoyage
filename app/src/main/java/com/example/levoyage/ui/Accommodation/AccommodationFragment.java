package com.example.levoyage.ui.Accommodation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.levoyage.R;
import com.example.levoyage.SearchFragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AccommodationFragment extends SearchFragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<AccommodationItineraryItem> list = new ArrayList<>();
    private AccommodationAdapter adapter;
    private RequestQueue queue;

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
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
        adapter = new AccommodationAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void extractInfo(String locationID) {
        String propertiesURL = "https://travel-advisor.p.rapidapi.com/hotels/list?sort=recommended&adults=1&rooms=1&nights=1";
        propertiesURL = propertiesURL + "&location_id=" + locationID;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest searchHotels = new JsonObjectRequest(Request.Method.GET,
                propertiesURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.isNull("data")) {
                        Toast.makeText(getContext(), "No accommodation found.", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            if (i != 6 && i != 15 && i != 24) { // API call returns different data at these positions
                                JSONObject item = arr.getJSONObject(i);
                                AccommodationItineraryItem hotel = new AccommodationItineraryItem();
                                hotel.setLocation(getFromJson("name", item));
                                hotel.setRating(getFromJson("rating", item));
                                hotel.setPrice(getFromJson("price", item));
                                hotel.setId(getFromJson("location_id", item));
                                hotel.setImageURL(getImageURLFromJson(item));
                                list.add(hotel);
                            }
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error. Please try again.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new AccommodationAdapter(getContext(), list);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(ProgressBar.GONE);
            }
        }, e -> Toast.makeText(getContext(), "Error. Please try again.", Toast.LENGTH_SHORT).show())
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("x-rapidapi-key", "445a09e84fmsh6d11b122cbebd2bp1bbc53jsnfed0b11069eb");
                h.put("x-rapidapi-host", "travel-advisor.p.rapidapi.com");
                return h;
            }
        };
        searchHotels.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(searchHotels);
    }

}