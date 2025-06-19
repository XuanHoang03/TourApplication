package com.hashmal.tourapplication.fragment.admin;

import static android.view.View.GONE;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.admin.AdminToursAdapter;
import com.hashmal.tourapplication.enums.RoleEnum;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.CreateTourRequest;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.hashmal.tourapplication.service.dto.YourTourDTO;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import java.io.File;
import java.io.IOException;
import android.widget.TextView;

public class AdminToursFragment extends Fragment {
    private RecyclerView rvTours;
    private AdminToursAdapter toursAdapter;
    private ApiService apiService;
    private List<TourResponseDTO> allTours = new ArrayList<>();
    private Uri selectedImageUri = null;
    private LocalDataService localDataService;
    private String uploadedImageUrl = null;
    private ImageView imagePreviewRef;

    private List<YourTourDTO.Location> allLocations = new ArrayList<>();
    private List<CreateTourRequest.VisitOrderDTO> selectedLocations = new ArrayList<>();
    private static final int ADD_TOUR_REQUEST = 1001;
    private static final int DETAIL_TOUR_REQUEST = 1002;
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null && imagePreviewRef != null) {
                        imagePreviewRef.setImageURI(selectedImageUri);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_tours, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvTours = view.findViewById(R.id.rvTours);
        SearchView searchView = view.findViewById(R.id.searchViewTour);
        FloatingActionButton fabAddTour = view.findViewById(R.id.fabAddTour);
        rvTours.setLayoutManager(new LinearLayoutManager(requireContext()));
        toursAdapter = new AdminToursAdapter(requireContext());
        rvTours.setAdapter(toursAdapter);
        toursAdapter.setOnTourActionListener(tour -> {
            Intent intent = new Intent(requireContext(), com.hashmal.tourapplication.activity.AdminTourDetailActivity.class);
            intent.putExtra("tour", new com.google.gson.Gson().toJson(tour));
            startActivityForResult(intent, DETAIL_TOUR_REQUEST);
        });
        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(requireContext());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTours(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterTours(newText);
                return true;
            }
        });
        if (localDataService.getSysUser().getAccount().getRoleName() != RoleEnum.SYSTEM_ADMIN.name()) {
            fabAddTour.setVisibility(GONE);
        }
        fabAddTour.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), com.hashmal.tourapplication.activity.AddTourActivity.class);
            startActivityForResult(intent, ADD_TOUR_REQUEST);
        });
        loadTours();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TOUR_REQUEST && resultCode == Activity.RESULT_OK) {
            loadTours(); // Reload danh sách tours
        }
        if (requestCode == DETAIL_TOUR_REQUEST && resultCode == Activity.RESULT_OK) {
            loadTours(); // Reload khi quay về từ chi tiết tour
        }
    }
    private void loadTours() {
        apiService.getAllTours().enqueue(new Callback<List<TourResponseDTO>>() {
            @Override
            public void onResponse(Call<List<TourResponseDTO>> call, Response<List<TourResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allTours = response.body();
                    toursAdapter.updateTours(allTours);
                } else {
                    Toast.makeText(requireContext(), "Không tải được danh sách tour", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<TourResponseDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterTours(String query) {
        if (allTours == null) return;
        String lower = query.toLowerCase();
        List<TourResponseDTO> filtered = new ArrayList<>();
        for (TourResponseDTO tour : allTours) {
            if (tour.getTourName().toLowerCase().contains(lower)
                || (tour.getTourType() != null && tour.getTourType().toLowerCase().contains(lower))) {
                filtered.add(tour);
            }
        }
        toursAdapter.updateTours(filtered);
    }

    private void showAddTourDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_tour, null, false);
        TextInputEditText edtTourName = dialogView.findViewById(R.id.edtTourName);
        TextInputEditText edtTourType = dialogView.findViewById(R.id.edtTourType);
        TextInputEditText edtTourDescription = dialogView.findViewById(R.id.edtTourDescription);
        TextInputEditText edtNumberOfPeople = dialogView.findViewById(R.id.edtNumberOfPeople);
        TextInputEditText edtDuration = dialogView.findViewById(R.id.edtDuration);
        Button btnPickImage = dialogView.findViewById(R.id.btnPickImage);
        imagePreviewRef = dialogView.findViewById(R.id.imgThumbnailPreview);
        Button btnPickLocations = dialogView.findViewById(R.id.btnPickLocations);
        TextView tvSelectedLocations = dialogView.findViewById(R.id.tvSelectedLocations);
        Button btnSaveTour = dialogView.findViewById(R.id.btnSaveTour);

        // Chọn ảnh đại diện
        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        // Chọn địa điểm
        btnPickLocations.setOnClickListener(v -> showPickLocationsDialog(tvSelectedLocations));

        final com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        dialog.setContentView(dialogView);
        dialog.show();

        btnSaveTour.setOnClickListener(v -> {
            String name = edtTourName.getText() != null ? edtTourName.getText().toString().trim() : "";
            String type = edtTourType.getText() != null ? edtTourType.getText().toString().trim() : "";
            String desc = edtTourDescription.getText() != null ? edtTourDescription.getText().toString().trim() : "";
            String numStr = edtNumberOfPeople.getText() != null ? edtNumberOfPeople.getText().toString().trim() : "";
            String duration = edtDuration.getText() != null ? edtDuration.getText().toString().trim() : "";
            if (name.isEmpty() || type.isEmpty() || numStr.isEmpty() || duration.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }
            Integer numPeople = null;
            try { numPeople = Integer.parseInt(numStr); } catch (Exception e) {}
            if (numPeople == null) {
                Toast.makeText(requireContext(), "Số người không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedImageUri == null) {
                Toast.makeText(requireContext(), "Vui lòng chọn 1 ảnh cho Tour", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedLocations.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn ít nhất 1 địa điểm", Toast.LENGTH_SHORT).show();
                return;
            }
            // Upload ảnh trước, sau đó tạo tour (demo: chỉ log)
            // TODO: Gọi API upload ảnh, lấy url, sau đó tạo tour
            Toast.makeText(requireContext(), "Đã nhập thông tin tour (demo)", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void showPickLocationsDialog(TextView tvSelectedLocations) {
        // Nếu đã có allLocations thì show luôn, chưa có thì gọi API
        if (allLocations.isEmpty()) {
            apiService.getAllLocations().enqueue(new retrofit2.Callback<List<YourTourDTO.Location>>() {
                @Override
                public void onResponse(Call<List<YourTourDTO.Location>> call, Response<List<YourTourDTO.Location>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        allLocations = response.body();
                        showAddLocationDialog(tvSelectedLocations);
                    } else {
                        Toast.makeText(requireContext(), "Không tải được danh sách địa điểm", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<List<YourTourDTO.Location>> call, Throwable t) {
                    Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            showAddLocationDialog(tvSelectedLocations);
        }
    }

    private void showAddLocationDialog(TextView tvSelectedLocations) {
        // Lọc ra các location chưa chọn
        List<YourTourDTO.Location> available = new ArrayList<>();
        for (YourTourDTO.Location loc : allLocations) {
            boolean already = false;
            for (CreateTourRequest.VisitOrderDTO dto : selectedLocations) {
                if (dto.getLocationId() != null && dto.getLocationId().equals(loc.getId())) {
                    already = true;
                    break;
                }
            }
            if (!already) available.add(loc);
        }
        if (available.isEmpty()) {
            Toast.makeText(requireContext(), "Đã chọn hết địa điểm!", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] names = new String[available.size()];
        for (int i = 0; i < available.size(); i++) names[i] = available.get(i).getName();
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Chọn địa điểm để thêm")
            .setItems(names, (dialog, which) -> {
                YourTourDTO.Location loc = available.get(which);
                selectedLocations.add(new CreateTourRequest.VisitOrderDTO(loc.getId(), selectedLocations.size() + 1));
                updateSelectedLocationsText(tvSelectedLocations);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void updateSelectedLocationsText(TextView tvSelectedLocations) {
        LinearLayout layoutSelected = ((View) tvSelectedLocations.getParent()).findViewById(R.id.layoutSelectedLocations);
        layoutSelected.removeAllViews();
        if (selectedLocations.isEmpty()) {
            tvSelectedLocations.setText("Chưa chọn địa điểm nào");
            return;
        }
        tvSelectedLocations.setText("");
        for (int i = 0; i < selectedLocations.size(); i++) {
            final int index = i;
            Long id = selectedLocations.get(i).getLocationId();
            String name = "";
            for (YourTourDTO.Location loc : allLocations) {
                if (loc.getId() == id) { name = loc.getName(); break; }
            }
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);
            TextView tv = new TextView(requireContext());
            tv.setText((i + 1) + ". " + name);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            Button btnRemove = new Button(requireContext());
            btnRemove.setText("X");
            btnRemove.setTextSize(12);
            btnRemove.setPadding(16, 0, 16, 0);
            btnRemove.setOnClickListener(v -> {
                selectedLocations.remove(index);
                updateSelectedLocationsText(tvSelectedLocations);
            });
            row.addView(tv);
            row.addView(btnRemove);
            layoutSelected.addView(row);
        }
    }

    private void showDatePicker(TextInputEditText target) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            String dateStr = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
            target.setText(dateStr);
        },
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH),
        calendar.get(java.util.Calendar.DAY_OF_MONTH));
        datePicker.show();
    }
} 