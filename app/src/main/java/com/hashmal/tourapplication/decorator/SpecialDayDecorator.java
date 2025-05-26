package com.hashmal.tourapplication.decorator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import com.hashmal.tourapplication.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashSet;
import java.util.List;

public class SpecialDayDecorator implements DayViewDecorator {
    private final HashSet<CalendarDay> specialDates;
    private final Drawable backgroundDrawable;

    public SpecialDayDecorator(Context context, List<CalendarDay> dates) {
        this.specialDates = new HashSet<>(dates);
        this.backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.bg_day_special);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return specialDates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(backgroundDrawable); // đổi nền
        view.addSpan(new ForegroundColorSpan(Color.WHITE)); // đổi màu chữ
    }
}

