package com.hashmal.tourapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import static androidx.appcompat.content.res.AppCompatResources.getColorStateList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.activity.AdminTourDetailActivity;
import com.hashmal.tourapplication.activity.LoginActivity;
import com.hashmal.tourapplication.activity.StaffLoginActivity;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.TourScheduleResponseDTO;
import com.hashmal.tourapplication.utils.DataUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuideCurrentTourFragment extends Fragment {
    private TextView tvScheduleId, tvTime, tvEmpty, tvStatus;
    private CardView cardTour;
    private Button btnTourInfo, btnChangeStatus;
    private ApiService apiService;
    private LocalDataService localDataService;
    private String scheduleId;
    private Integer statusValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_current_tour, container, false);
        tvScheduleId = view.findViewById(R.id.tvScheduleId);
        tvTime = view.findViewById(R.id.tvTime);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        cardTour = view.findViewById(R.id.cardTour);
        tvStatus = view.findViewById(R.id.tvStatus);
        btnTourInfo = view.findViewById(R.id.btnTourInfo);
        btnChangeStatus = view.findViewById(R.id.btnChangeStatus);
        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(requireContext());
        loadCurrentTour();
        return view;
    }

    private void loadCurrentTour() {
        String userId = localDataService.getCurrentUser().getAccount().getAccountId();

        apiService.getCurrentOrNextTour(userId).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if(response.isSuccessful()) {
                    BaseResponse res = response.body();
                    if (res.getCode().equals(Code.SUCCESS.getCode()) && res.getData() != null) {
                        scheduleId = res.getData().toString();

                        if (scheduleId != null) {
                            getScheduleData();
                        }
                    } else {
                        scheduleId = null;
                        cardTour.setVisibility(View.GONE);
                        tvEmpty.setText("Bạn chưa có chuyến Tour nào cả");
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable throwable) {
                scheduleId = null;
                cardTour.setVisibility(View.GONE);
                tvEmpty.setText("Lỗi hệ thống!!");
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });

    }

    private void getScheduleData() {
        // status=1: đang diễn ra, có thể điều chỉnh theo backend
        apiService.getTourSchedule(scheduleId)
                .enqueue(new Callback<TourScheduleResponseDTO>() {
                    @Override
                    public void onResponse(Call<TourScheduleResponseDTO> call, Response<TourScheduleResponseDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TourScheduleResponseDTO tour = response.body(); // Lấy tour đầu tiên (gần nhất)
                            bindTour(tour);
                        } else {
                            showEmpty();
                        }
                    }

                    @Override
                    public void onFailure(Call<TourScheduleResponseDTO> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi tải tour: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        showEmpty();
                    }
                });
    }

    private void bindTour(TourScheduleResponseDTO tour) {
        cardTour.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        tvScheduleId.setText("Mã chuyến đi: " + (tour.getTourScheduleId() != null ? tour.getTourScheduleId() : ""));
        String time = "";
        if (tour.getStartTime() != null && tour.getEndTime() != null) {
            time = "Từ: " + tour.getStartTime() + "\nĐến: " + tour.getEndTime();
        }
        tvTime.setText(time);
        btnTourInfo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AdminTourDetailActivity.class);
            intent.putExtra("tourScheduleId", tour.getTourScheduleId());
            intent.putExtra("tourId", tour.getTourId());
            startActivity(intent);
        });
        setUpStatusBtn(tour.getStatus());
    }

    private void setUpStatusBtn(Integer status) {
        String text = "";
        switch (status) {
            case 1:
                btnChangeStatus.setBackgroundTintList(getColorStateList( requireContext(),R.color.status_confirmed));
                tvStatus.setBackgroundTintList(getColorStateList(requireContext(),R.color.status_confirmed));
                tvStatus.setText("Đã hoàn thành");
                btnChangeStatus.setText("Đã hoàn thành chuyến Tour");
                btnChangeStatus.setEnabled(false);
                break;
            case 0:
                btnChangeStatus.setBackgroundTintList(getColorStateList(requireContext() , R.color.status_pending));
                tvStatus.setBackgroundTintList(getColorStateList(requireContext(),R.color.status_pending));

                tvStatus.setText("Chưa khởi hành");
                btnChangeStatus.setText("Khởi hành");
                text = "khởi hành chuyến Tour";
                statusValue = 11;
                break;
            case -1:
                btnChangeStatus.setBackgroundTintList(getColorStateList(requireContext() ,R.color.status_cancelled));
                tvStatus.setBackgroundTintList(getColorStateList(requireContext(),R.color.status_cancelled));
                btnChangeStatus.setText("Đã hủy");
                btnChangeStatus.setEnabled(false);
                tvStatus.setText("Đã hủy");

                break;
            default:
                btnChangeStatus.setBackgroundTintList(getColorStateList(requireContext() , R.color.status_default));
                tvStatus.setBackgroundTintList(getColorStateList(requireContext(),R.color.status_default));

                btnChangeStatus.setText("Kết thúc chuyến Tour");
                tvStatus.setText("Đã hủy");
                text = "kết thúc chuyến Tour";
                statusValue = 1;
                break;
        }
        String finalText = text;
        btnChangeStatus.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setMessage("Xác thực " + finalText)
                    .setPositiveButton("Có", (dialog, which) -> {
                        modifyScheduleStatus();
                    })
                    .setNegativeButton("Không", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    private void modifyScheduleStatus() {
        apiService.modifyStatusSchedule(scheduleId, statusValue).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() &&  response.body() != null) {
                    BaseResponse res = response.body();
                    if (res.getCode().equals(Code.SUCCESS.getCode())) {
                        Toast.makeText(requireContext(), "Chuyển trạng thái Tour thành công.", Toast.LENGTH_SHORT).show();
                        getScheduleData();
                    }
                } else {
                    Toast.makeText(requireContext(), "Chuyển trạng thái Tour không thành công.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable throwable) {
                Toast.makeText(requireContext(), "Lỗi thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmpty() {
        cardTour.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
    }
} 