package com.example.levoyage.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.levoyage.R;
import com.example.levoyage.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private FragmentHomeBinding binding;
    private View root;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private FloatingActionButton addTripFAB;
    private ListView tripListView;

    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        initWidgets();
        selectedDate = LocalDate.now();
        setMonthView();

        return root;
    }

    private void initWidgets() {
        calendarRecyclerView = root.findViewById(R.id.calendarRecyclerView);
        monthYearText = root.findViewById(R.id.monthYearTV);
        addTripFAB = root.findViewById(R.id.addTripButton);
//        tripListView = root.findViewById(R.id.tripListView);
        addTripFAB.hide();
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;

        for(int i = 1; i <= 42; i++) {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       root.findViewById(R.id.previousMonth).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               selectedDate = selectedDate.minusMonths(1);
               setMonthView();
           }
       });

       root.findViewById(R.id.nextMonth).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               selectedDate = selectedDate.plusMonths(1);
               setMonthView();
           }
       });

        addTripFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_nav_home_to_calendarEventEditFragment);
            }
        });
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if(!dayText.equals("")) {
            Bundle bundle = new Bundle();
            bundle.putString("DATE", dayText + " " + monthYearFromDate(selectedDate));
            bundle.putString("UserID", user.getUid());
            Navigation.findNavController(root).navigate(R.id.action_nav_home_to_itineraryFragment, bundle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        setTripAdapter();
    }

    private void setTripAdapter() {
        Log.d("Log", "method used");
        Log.d("log", selectedDate.toString());
        ArrayList<CalendarTripEvent> tripEvents = CalendarTripEvent.tripsBeforeDate(selectedDate);
        Log.d("Log", tripEvents.size() + "");
        CalendarEventAdapter eventAdapter = new CalendarEventAdapter(getContext().getApplicationContext(), tripEvents);
        tripListView.setAdapter(eventAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}