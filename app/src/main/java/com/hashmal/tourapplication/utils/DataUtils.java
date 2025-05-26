package com.hashmal.tourapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.constants.FirebaseConst;
import com.hashmal.tourapplication.enums.MessageType;
import com.hashmal.tourapplication.enums.StatusEnum;
import com.hashmal.tourapplication.service.dto.LocationDTO;
import com.hashmal.tourapplication.service.dto.TourPackageDTO;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.YourTourDTO;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DataUtils {

    public static Map<String, Object> buildFireStoreMessage(String conversationId, String senderId, String content, Date createdAt, Date updatedAt, String type) {
        Map<String, Object> message = new HashMap<>();
        message.put(FirebaseConst.MessageFields.createdBy, senderId);
        message.put(FirebaseConst.MessageFields.content, content);
        message.put(FirebaseConst.MessageFields.createdAt, createdAt);
        message.put(FirebaseConst.MessageFields.updatedAt, updatedAt);
        message.put(FirebaseConst.MessageFields.type, type);
    return message;
    }

    public static Map<String, Object> buildFireStoreUserConversation(Date lastSend, String id , Date updatedAt, String content, String type) {
        Map<String, Object> update = new HashMap<>();
        update.put(FirebaseConst.UserConversationFields.lastSend, lastSend);
        update.put(FirebaseConst.UserConversationFields.id, id);
        update.put(FirebaseConst.UserConversationFields.updatedAt, updatedAt);
        update.put(FirebaseConst.UserConversationFields.lastMessageContent, content);
        update.put(FirebaseConst.UserConversationFields.type, type);

        return update;
    }
    public static void applyGradientToText(TextView textView) {
        textView.post(() -> {
            int width = textView.getWidth();
            if (width == 0) width = 1000; // fallback

            Shader textShader = new LinearGradient(
                    0, 0, width, textView.getTextSize(),
                    new int[]{
                            Color.parseColor("#223E91"),
                            Color.parseColor("#1567B7"),
                            Color.parseColor("#02A7F1")
                    },
                    null,
                    Shader.TileMode.CLAMP);

            textView.getPaint().setShader(textShader);
            textView.invalidate();
        });
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, @DrawableRes int drawableId) {
        Drawable drawable = ContextCompat.getDrawable( context, drawableId);
        if (drawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        return null;
    }
    public static String formatCurrency(long amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount) + " VND";
    }

    public static String formatDateTimeString(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        try {
            // Cắt bớt phần dư nếu có
            if (dateString.length() > 23) {
                dateString = dateString.substring(0, 23); // "2025-05-22T16:17:19.035"
            }
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;  // fallback
        }
    }

    public static String convertStatusFromInt(Integer integer) {
        switch (integer) {
            case 1:
                return StatusEnum.Completed.name();
            case 0:
                return StatusEnum.Pending.name();
            case -1:
                return StatusEnum.Cancelled.name();
        }
        return "";
    }

    public static TourResponseDTO convertYourTourToTourResponse(YourTourDTO yourTour) {
        if (yourTour == null || yourTour.getTour() == null) {
            return null;
        }

        YourTourDTO.Tour tour = yourTour.getTour();
        YourTourDTO.TourSchedule schedule = yourTour.getTourSchedule();
        YourTourDTO.TourPackage tourPackage = yourTour.getTourPackage();

        // Convert locations
        List<LocationDTO> locations = new ArrayList<>();
        if (yourTour.getTourLocationList() != null) {
            for (YourTourDTO.TourLocation tourLocation : yourTour.getTourLocationList()) {
                YourTourDTO.Location location = tourLocation.getLocation();
                LocationDTO locationDTO = new LocationDTO(
                    location.getId(),
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getCountry(),
                    location.getProvince(),
                    location.getCity(),
                    location.getFullAddress(),
                    location.getOpeningHour(),
                    location.getClosingHour(),
                    location.getDescription(),
                    location.getName(),
                    location.getThumbnailUrl()
                );
                locations.add(locationDTO);
            }
        }

        // Convert packages
        List<TourPackageDTO> packages = new ArrayList<>();
        if (tourPackage != null) {
            TourPackageDTO packageDTO = new TourPackageDTO(
                tourPackage.getId(),
                tourPackage.getPackageName(),
                tourPackage.getDescription(),
                tourPackage.getPrice(),
                tourPackage.isMain()
            );
            packages.add(packageDTO);
        }

        return new TourResponseDTO(
            tour.getTourId(),
            tour.getTourName(),
            tour.getTourType(),
            tour.getTourDescription(),
            (long) tour.getNumberOfPeople(),
            tour.isHaveTourGuide(),
            tour.getDuration(),
            tour.getThumbnailUrl(),
            schedule != null ? schedule.getStartTime() : null,
            schedule != null ? schedule.getEndTime() : null,
            locations,
            packages
        );
    }
}
