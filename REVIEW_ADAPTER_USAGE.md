# Hướng dẫn sử dụng ReviewAdapter

## Tổng quan
`ReviewAdapter` là một RecyclerView Adapter được thiết kế để hiển thị danh sách đánh giá tour du lịch. Adapter này cung cấp giao diện đẹp mắt và dễ sử dụng cho việc hiển thị các đánh giá của người dùng.

## Tính năng chính

### 1. Hiển thị thông tin đánh giá
- Tên người đánh giá
- Rating (số sao từ 1-5)
- Nội dung đánh giá
- Ngày và giờ đánh giá
- Avatar người dùng (có thể tùy chỉnh)

### 2. Giao diện
- Card layout với shadow và corner radius
- RatingBar chỉ đọc (không thể chỉnh sửa)
- Responsive design
- Empty state layout

### 3. Quản lý dữ liệu
- Cập nhật toàn bộ danh sách
- Thêm đánh giá mới vào đầu danh sách
- Xử lý null/empty data

## Cách sử dụng cơ bản

### 1. Khởi tạo Adapter
```java
List<ReviewDTO> reviews = new ArrayList<>();
ReviewAdapter adapter = new ReviewAdapter(context, reviews);
recyclerView.setLayoutManager(new LinearLayoutManager(context));
recyclerView.setAdapter(adapter);
```

### 2. Cập nhật dữ liệu
```java
// Cập nhật toàn bộ danh sách
adapter.updateReviews(newReviewsList);

// Thêm đánh giá mới vào đầu
ReviewDTO newReview = new ReviewDTO();
adapter.addReview(newReview);
```

### 3. Trong TourReviewsActivity
```java
public class TourReviewsActivity extends AppCompatActivity {
    private ReviewAdapter reviewAdapter;
    
    private void setupRecyclerView() {
        reviewAdapter = new ReviewAdapter(this, new ArrayList<>());
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
    }
    
    private void loadReviews(String tourId) {
        apiService.getReviewsByTourId(tourId).enqueue(new Callback<List<ReviewDTO>>() {
            @Override
            public void onResponse(Call<List<ReviewDTO>> call, Response<List<ReviewDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<ReviewDTO> reviews = response.body();
                    displayReviews(reviews);
                } else {
                    showEmptyState();
                }
            }
            
            @Override
            public void onFailure(Call<List<ReviewDTO>> call, Throwable t) {
                showEmptyState();
            }
        });
    }
    
    private void displayReviews(List<ReviewDTO> reviews) {
        rvReviews.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        reviewAdapter.updateReviews(reviews);
    }
    
    private void showEmptyState() {
        rvReviews.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }
}
```

## Layout Files

### 1. item_review.xml
Layout cho từng item đánh giá trong RecyclerView:
- CardView với shadow và corner radius
- User avatar (ImageView)
- User name (TextView)
- Rating (RatingBar)
- Comment (TextView)
- Date và time (TextView)

### 2. layout_empty_reviews.xml
Layout cho trường hợp không có đánh giá nào:
- Icon empty state
- Text thông báo
- Text khuyến khích đánh giá

## Tùy chỉnh

### 1. Thêm Click Listener
```java
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private OnReviewClickListener listener;
    
    public interface OnReviewClickListener {
        void onReviewClick(ReviewDTO review, int position);
    }
    
    public void setOnReviewClickListener(OnReviewClickListener listener) {
        this.listener = listener;
    }
    
    class ReviewViewHolder extends RecyclerView.ViewHolder {
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onReviewClick(reviews.get(position), position);
                    }
                }
            });
        }
    }
}
```

### 2. Sử dụng Click Listener
```java
reviewAdapter.setOnReviewClickListener((review, position) -> {
    // Xử lý khi click vào review
    Toast.makeText(this, "Clicked: " + review.getReviewerName(), Toast.LENGTH_SHORT).show();
});
```

### 3. Thêm Avatar Loading
```java
// Trong ReviewAdapter.bind()
if (review.getAvatarUrl() != null && !review.getAvatarUrl().isEmpty()) {
    Glide.with(context)
        .load(review.getAvatarUrl())
        .placeholder(R.drawable.default_avatar)
        .error(R.drawable.default_avatar)
        .circleCrop()
        .into(ivUserAvatar);
} else {
    ivUserAvatar.setImageResource(R.drawable.ic_person);
}
```

### 4. Thêm Pull to Refresh
```java
SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
swipeRefreshLayout.setOnRefreshListener(() -> {
    loadReviews(tourId);
});

private void loadReviews(String tourId) {
    apiService.getReviewsByTourId(tourId).enqueue(new Callback<List<ReviewDTO>>() {
        @Override
        public void onResponse(Call<List<ReviewDTO>> call, Response<List<ReviewDTO>> response) {
            swipeRefreshLayout.setRefreshing(false);
            // ... existing code
        }
        
        @Override
        public void onFailure(Call<List<ReviewDTO>> call, Throwable t) {
            swipeRefreshLayout.setRefreshing(false);
            // ... existing code
        }
    });
}
```

## Data Format

### ReviewDTO Structure
```java
public class ReviewDTO {
    private String reviewerName;    // Tên người đánh giá
    private Integer rating;         // Số sao (1-5)
    private String comment;         // Nội dung đánh giá
    private LocalDateTime reviewTime; // Thời gian đánh giá
    // Có thể thêm: avatarUrl, userId, etc.
}
```

### Date/Time Format
- Date: "dd/MM/yyyy" (ví dụ: "25/12/2024")
- Time: "HH:mm" (ví dụ: "14:30")

## Performance Optimization

### 1. ViewHolder Pattern
Adapter đã sử dụng ViewHolder pattern để tối ưu hiệu suất.

### 2. Efficient Updates
```java
// Sử dụng notifyItemInserted thay vì notifyDataSetChanged
public void addReview(ReviewDTO review) {
    if (this.reviews != null) {
        this.reviews.add(0, review);
        notifyItemInserted(0);
    }
}
```

### 3. Image Loading
Sử dụng Glide để load avatar với placeholder và error handling.

## Error Handling

### 1. Null Safety
```java
// Kiểm tra null trước khi sử dụng
if (review.getReviewerName() != null && !review.getReviewerName().isEmpty()) {
    tvUserName.setText(review.getReviewerName());
} else {
    tvUserName.setText("Khách hàng");
}
```

### 2. Empty State
Hiển thị layout empty khi không có đánh giá nào.

### 3. Network Error
Xử lý lỗi network và hiển thị empty state.

## Testing

### 1. Unit Tests
```java
@Test
public void testAdapterItemCount() {
    List<ReviewDTO> reviews = createMockReviews();
    ReviewAdapter adapter = new ReviewAdapter(context, reviews);
    assertEquals(reviews.size(), adapter.getItemCount());
}

@Test
public void testUpdateReviews() {
    ReviewAdapter adapter = new ReviewAdapter(context, new ArrayList<>());
    List<ReviewDTO> newReviews = createMockReviews();
    adapter.updateReviews(newReviews);
    assertEquals(newReviews.size(), adapter.getItemCount());
}
```

### 2. UI Tests
```java
@Test
public void testReviewItemDisplay() {
    // Test hiển thị thông tin review
    onView(withId(R.id.tvUserName))
        .check(matches(withText("Test User")));
    
    onView(withId(R.id.ratingBar))
        .check(matches(withRating(5.0f)));
}
``` 