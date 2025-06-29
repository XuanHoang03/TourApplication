package com.hashmal.tourapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.activity.ScheduleDetailActivity;
import com.hashmal.tourapplication.adapter.TourScheduleListAdapter;
import com.hashmal.tourapplication.decorator.SpecialDayDecorator;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.TourScheduleResponseDTO;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

public class CustomerTicketsFragment extends Fragment {
    private MaterialCalendarView calendarView;
    private RecyclerView rvSchedules;
    private TourScheduleListAdapter scheduleListAdapter;
    private List<TourScheduleResponseDTO> allSchedules = new ArrayList<>();
    private ApiService apiService;
    private LocalDataService localDataService;

    private String tourScheduleId;
    private String tourId;
    private String status;
    private String startDate;
    private String endDate;
    private String tourGuideId;
    private Boolean isAvailable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_tickets, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        rvSchedules = view.findViewById(R.id.rvSchedules);

        scheduleListAdapter = new TourScheduleListAdapter(new ArrayList<>(), schedule -> {
            Intent newIntent = new Intent(requireContext(), ScheduleDetailActivity.class);
            newIntent.putExtra("tourScheduleId", schedule.getTourScheduleId());
            newIntent.putExtra("tourId", schedule.getTourId());
            startActivity(newIntent);
        });
        rvSchedules.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSchedules.setAdapter(scheduleListAdapter);

        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(requireContext());
        tourGuideId = localDataService.getSysUser().getAccount().getAccountId();
        loadTourSchedules(null, null, null, null, null, tourGuideId, null);

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            onCalendarDaySelected(date);
        });
        return view;
    }

    private void loadTourSchedules(String tourScheduleId, String tourId, Integer status, String startDate, String endDate, String tourGuideId, Boolean isAvailable) {
        apiService.getScheduledTours(tourScheduleId, tourId, status, startDate, endDate, tourGuideId, isAvailable).enqueue(new Callback<List<TourScheduleResponseDTO>>() {
            @Override
            public void onResponse(Call<List<TourScheduleResponseDTO>> call, Response<List<TourScheduleResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TourScheduleResponseDTO> schedules = response.body();
                    allSchedules.clear();
                    allSchedules.addAll(schedules);

                    // Đánh dấu tất cả các ngày từ start đến end
                    List<CalendarDay> calendarDays = new ArrayList<>();
                    for (TourScheduleResponseDTO schedule : schedules) {
                        try {
                            String startTime = schedule.getStartTime();
                            String endTime = schedule.getEndTime();
                            LocalDateTime startDateTime, endDateTime;
                            if (startTime.length() > 19) {
                                startDateTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
                            } else {
                                startDateTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                            }
                            if (endTime != null && endTime.length() > 19) {
                                endDateTime = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
                            } else if (endTime != null) {
                                endDateTime = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                            } else {
                                endDateTime = startDateTime;
                            }
                            LocalDateTime current = startDateTime;
                            while (!current.isAfter(endDateTime)) {
                                calendarDays.add(CalendarDay.from(current.getYear(), current.getMonthValue() - 1, current.getDayOfMonth()));
                                current = current.plusDays(1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    calendarView.addDecorator(new SpecialDayDecorator(requireContext(), calendarDays));
                } else {
                    Toast.makeText(getContext(), "Không thể tải lịch trình tour", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TourScheduleResponseDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onCalendarDaySelected(CalendarDay selectedDay) {
        List<TourScheduleResponseDTO> filtered = new ArrayList<>();
        for (TourScheduleResponseDTO schedule : allSchedules) {
            try {
                String start = schedule.getStartTime();
                String end = schedule.getEndTime();
                LocalDate startDate, endDate;
                if (start.length() > 19) {
                    startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
                } else {
                    startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                }
                if (end != null && end.length() > 19) {
                    endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
                } else if (end != null) {
                    endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                } else {
                    endDate = startDate;
                }
                LocalDate selected = LocalDate.of(selectedDay.getYear(), selectedDay.getMonth() + 1, selectedDay.getDay());
                if ((selected.isEqual(startDate) || selected.isAfter(startDate)) && (selected.isEqual(endDate) || selected.isBefore(endDate))) {
                    filtered.add(schedule);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        scheduleListAdapter.updateData(filtered);
    }
} 