package com.hashmal.tourapplication.fragment;

import static com.hashmal.tourapplication.utils.DataUtils.formatDateValue;

import android.content.Intent;
import android.graphics.Color;
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

import com.google.gson.reflect.TypeToken;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.activity.YourTourActivity;
import com.hashmal.tourapplication.adapter.BookingHistoryAdapter;
import com.hashmal.tourapplication.decorator.SpecialDayDecorator;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.DisplayBookingDTO;
import com.hashmal.tourapplication.service.dto.UserDTO;
import com.google.gson.Gson;
import com.hashmal.tourapplication.utils.DataUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarFragment extends Fragment implements BookingHistoryAdapter.OnBookingClickListener {
    private MaterialCalendarView calendarView;
    private TextView tvSelectedDate;
    private TextView tvNoTours;
    private RecyclerView rvBookingsOnDate;
    private BookingHistoryAdapter bookingAdapter;
    private List<DisplayBookingDTO> allBookings;
    private List<DisplayBookingDTO> filteredBookings;
    private List<CalendarDay> specialDates;
    private ApiService apiService;
    private LocalDataService localDataService;
    private Gson gson = new Gson();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allBookings = new ArrayList<>();
        specialDates = new ArrayList<>();
        filteredBookings = new ArrayList<>();
        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        tvNoTours = view.findViewById(R.id.tvNoTours);
        rvBookingsOnDate = view.findViewById(R.id.rvToursOnDate);

        // Setup RecyclerView
        bookingAdapter = new BookingHistoryAdapter(filteredBookings, requireContext(), this);
        rvBookingsOnDate.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBookingsOnDate.setAdapter(bookingAdapter);

        loadUserBookings();

        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            filterBookingsForDate(DataUtils.buildDateFromCalendarDay(date));
        });
        return view;
    }

    private void loadUserBookings() {
        UserDTO currentUser = localDataService.getCurrentUser();
        if (currentUser == null) {
            tvNoTours.setVisibility(View.VISIBLE);
            tvNoTours.setText("Please login to view your bookings");
            return;
        }

        // Show loading state
        tvNoTours.setVisibility(View.VISIBLE);
        tvNoTours.setText("Loading bookings...");

        apiService.getBookingsByUserId(currentUser.getAccount().getAccountId()).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse resp = response.body();
                    String listBookings = gson.toJson(resp.getData());
                    Type bookingType = new TypeToken<List<DisplayBookingDTO>>() {}.getType();
                    List<DisplayBookingDTO> bookingHistory = gson.fromJson(listBookings, bookingType);
                    
                    // Process data in background
                    new Thread(() -> {
                        allBookings.clear();
                        allBookings.addAll(bookingHistory);
                        
                        // Create set of dates with tours more efficiently
                        Set<Date> datesWithTours = new HashSet<>();
                        List<CalendarDay> calendarDays = new ArrayList<>();
                        
                        for (DisplayBookingDTO booking : bookingHistory) {
                            if (booking.getStartTime() != null) {
                                try {
                                    Date bookingDate = DataUtils.convertStringToDateV1(booking.getStartTime());
                                    if (bookingDate != null) {
                                        datesWithTours.add(bookingDate);
                                        calendarDays.add(CalendarDay.from(bookingDate));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        
                        // Update UI on main thread
                        requireActivity().runOnUiThread(() -> {
                            specialDates = calendarDays;
                            calendarView.addDecorator(new SpecialDayDecorator(requireContext(), specialDates));
                            
                            // Filter for current selected date
                            Calendar calendar = Calendar.getInstance();
                            filterBookingsForDate(calendar.getTime());
                        });
                    }).start();
                } else {
                    tvNoTours.setVisibility(View.VISIBLE);
                    tvNoTours.setText("Failed to load bookings");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                tvNoTours.setVisibility(View.VISIBLE);
                tvNoTours.setText("Error: " + t.getMessage());
            }
        });
    }

    private void filterBookingsForDate(Date selectedDate) {
        filteredBookings.clear();
        tvSelectedDate.setText("Bookings on " + formatDateValue(selectedDate));

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
            if (booking.getStartTime() != null) {
                try {
                    Date bookingDate = DataUtils.convertStringToDateV1(booking.getStartTime());
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
        Intent intent = new Intent(requireContext(), YourTourActivity.class);
        intent.putExtra("bookingId", booking.getBookingId());
        startActivity(intent);
    }
} 