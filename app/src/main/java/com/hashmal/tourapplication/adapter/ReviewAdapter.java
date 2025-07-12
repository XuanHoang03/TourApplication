package com.hashmal.tourapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.service.dto.ReviewDTO;
import com.hashmal.tourapplication.utils.DataUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<ReviewDTO> reviews;

    public ReviewAdapter(Context context, List<ReviewDTO> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewDTO review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    public void updateReviews(List<ReviewDTO> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }

    public void addReview(ReviewDTO review) {
        if (this.reviews != null) {
            this.reviews.add(0, review); // Thêm vào đầu danh sách
            notifyItemInserted(0);
        }
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivUserAvatar;
        private TextView tvUserName, tvComment, tvReviewDate, tvReviewTime;
        private RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            tvReviewTime = itemView.findViewById(R.id.tvReviewTime);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        public void bind(ReviewDTO review) {
            // Set user name
            if (review.getReviewerName() != null && !review.getReviewerName().isEmpty()) {
                tvUserName.setText(review.getReviewerName());
            } else {
                tvUserName.setText("Khách hàng");
            }

            // Set rating
            if (review.getRating() != null) {
                ratingBar.setRating(review.getRating());
            } else {
                ratingBar.setRating(0);
            }

            // Set comment
            if (review.getComment() != null && !review.getComment().isEmpty()) {
                tvComment.setText(review.getComment());
                tvComment.setVisibility(View.VISIBLE);
            } else {
                tvComment.setVisibility(View.GONE);
            }

            // Set review time
            if (review.getReviewTime() != null) {
                if (review.getReviewTime() != null) {
                    String reviewTime = DataUtils.formatDateTimeString(review.getReviewTime(), true);
                    String[] reviewTimes = reviewTime.split(" ");
                    tvReviewDate.setText(reviewTimes[0]);
                    tvReviewTime.setText(reviewTimes[1]);
                } else {
                    tvReviewDate.setText("--/--/----");
                    tvReviewTime.setText("--:--");
                }

                ivUserAvatar.setImageResource(R.drawable.ic_person);
            }
        }
    }
}