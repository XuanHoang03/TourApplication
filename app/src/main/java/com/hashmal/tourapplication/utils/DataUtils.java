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
import com.hashmal.tourapplication.enums.RoleEnum;
import com.hashmal.tourapplication.enums.StatusEnum;
import com.hashmal.tourapplication.service.dto.LocationDTO;
import com.hashmal.tourapplication.service.dto.TourPackageDTO;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.YourTourDTO;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
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

    public static Map<String, Object> buildFireStoreUserConversation(Date lastSend, String id, Date updatedAt, String content, String type) {
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
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
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
        return "VND " +  formatter.format(amount) ;
    }

    public static Date convertStringToDateV1(String localDateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            return inputFormat.parse(localDateTime);
        } catch (ParseException ex) {
            try {
                return inputFormat1.parse(localDateTime);
            } catch (ParseException exception) {
                return new Date();
            }
        }

    }

    public static String formatDateValue(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return outputFormat.format(date);
    }

    public static String formatDateTimeString(String dateString, boolean haveTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        SimpleDateFormat outputFormat2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            // Cắt bớt phần dư nếu có
            if (dateString.length() > 23) {
                dateString = dateString.substring(0, 23); // "2025-05-22T16:17:19.035"
            }
            Date date = inputFormat.parse(dateString);
            if (haveTime) {
                return outputFormat.format(date);
            } else return outputFormat2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;  // fallback
        }
    }

    public static String parseDateOfBirth(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat2.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dateString;
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
            case 11:
                return StatusEnum.Progress.name();
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
                packages,
                tour.getStatus()
        );
    }

    public static CalendarDay buildCalendarDay(int year, int month, int day) {
        return CalendarDay.from(year, month + 1, day);
    }

    public static Date buildDateFromCalendarDay(CalendarDay calendarDay) {
        return calendarDay.getDate();
    }

    public static String buildFullFilePath(String fileName) {
        return "http://14.225.198.227:8188/upload/images/" + fileName;
    }

    public static String getStartOrEndTime(String value) {
        return value.replace("_",":");
    }

    public static String getDayOfWeek(LocalDateTime dateTime) {
        Map<DayOfWeek, String> dayMap = new HashMap<>();
        dayMap.put(DayOfWeek.MONDAY,"Th 2");
        dayMap.put(DayOfWeek.TUESDAY,"Th 3");
        dayMap.put(DayOfWeek.WEDNESDAY,"Th 4");
        dayMap.put(DayOfWeek.THURSDAY,"Th 5");
        dayMap.put(DayOfWeek.FRIDAY,"Th 6");
        dayMap.put(DayOfWeek.SATURDAY,"Th 7");
        dayMap.put(DayOfWeek.SUNDAY,"CN ");

        return dayMap.get(dateTime.getDayOfWeek());
    }

    public static String getVietNamFormatDateTime(LocalDateTime dateTime) {
        String dayOfWeek = getDayOfWeek(dateTime);
        String dayMonth = dateTime.getDayOfMonth() + " thg " + dateTime.getMonthValue();
        return dayOfWeek + "\n" + dayMonth;
    }

    public static String getStringValueFromStatusValue(Integer status) {
        switch (status) {
            case 1:
                return "Đã kết thúc";
            case 0:
                return "Chưa khởi hành";
            case -1:
                return "Đã bị hủy";
            default:
                return "Đã khởi hành";
        }
    }
}
