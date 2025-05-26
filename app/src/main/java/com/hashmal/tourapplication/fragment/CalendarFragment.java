package com.hashmal.tourapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.activity.TourDetailActivity;
import com.hashmal.tourapplication.adapter.BookingHistoryAdapter;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.DisplayBookingDTO;
import com.hashmal.tourapplication.service.dto.UserDTO;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarFragment extends Fragment implements BookingHistoryAdapter.OnBookingClickListener {
    private CalendarView calendarView;
    private TextView tvSelectedDate;
    private TextView tvNoTours;
    private RecyclerView rvBookingsOnDate;
    private BookingHistoryAdapter bookingAdapter;
    private List<DisplayBookingDTO> allBookings;
    private List<DisplayBookingDTO> filteredBookings;
    private SimpleDateFormat dateFormat;
    private ApiService apiService;
    private LocalDataService localDataService;
    private Gson gson = new Gson();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allBookings = new ArrayList<>();
        filteredBookings = new ArrayList<>();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        
        calendarView = view.findViewById(R.id.calendarView);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        tvNoTours = view.findViewById(R.id.tvNoTours);
        rvBookingsOnDate = view.findViewById(R.id.rvToursOnDate);

        // Setup RecyclerView
        bookingAdapter = new BookingHistoryAdapter(filteredBookings, requireContext(), this);
        rvBookingsOnDate.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBookingsOnDate.setAdapter(bookingAdapter);

        // Setup CalendarView
        setupCalendarView();

        // Load user's bookings
        loadUserBookings();

        return view;
    }

    private void loadUserBookings() {
        UserDTO currentUser = localDataService.getCurrentUser();
        if (currentUser == null) {
            tvNoTours.setVisibility(View.VISIBLE);
            tvNoTours.setText("Please login to view your bookings");
            return;
        }

        apiService.getBookingHistory(currentUser.getId()).enqueue(new Callback<List<DisplayBookingDTO>>() {
            @Override
            public void onResponse(Call<List<DisplayBookingDTO>> call, Response<List<DisplayBookingDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allBookings.clear();
                    allBookings.addAll(response.body());
                    // Filter for current selected date
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(calendarView.getDate());
                    filterBookingsForDate(calendar.getTime());
                } else {
                    tvNoTours.setVisibility(View.VISIBLE);
                    tvNoTours.setText("Failed to load bookings");
                }
            }

            @Override
            public void onFailure(Call<List<DisplayBookingDTO>> call, Throwable t) {
                tvNoTours.setVisibility(View.VISIBLE);
                tvNoTours.setText("Error: " + t.getMessage());
            }
        });
    }

    private void setupCalendarView() {
        // Set initial date
        Calendar calendar = Calendar.getInstance();
        updateSelectedDate(calendar.getTime());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            updateSelectedDate(selectedCalendar.getTime());
            filterBookingsForDate(selectedCalendar.getTime());
        });
    }

    private void updateSelectedDate(Date date) {
        String formattedDate = dateFormat.format(date);
        tvSelectedDate.setText("Bookings on " + formattedDate);
    }

    private void filterBookingsForDate(Date selectedDate) {
        filteredBookings.clear();
        
        // Convert selected date to start of day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        // Add one day to get end of day
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endOfDay = calendar.getTime();

        // Filter bookings that start on the selected date
        for (DisplayBookingDTO booking : allBookings) {
            if (booking.getTourDate() != null) {
                try {
                    SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                    Date bookingDate = apiDateFormat.parse(booking.getTourDate());
                    if (bookingDate != null && bookingDate.after(startOfDay) && bookingDate.before(endOfDay)) {
                        filteredBookings.add(booking);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Update UI
        bookingAdapter.updateData(filteredBookings);
        tvNoTours.setVisibility(filteredBookings.isEmpty() ? View.VISIBLE : View.GONE);
        tvNoTours.setText("No bookings for selected date");
    }

    @Override
    public void onBookingClick(DisplayBookingDTO booking) {
        // Navigate to TourDetailActivity when a booking is clicked
        Intent intent = new Intent(requireContext(), TourDetailActivity.class);
        String bookingJson = gson.toJson(booking);
        intent.putExtra("booking", bookingJson);
        startActivity(intent);
    }
} 