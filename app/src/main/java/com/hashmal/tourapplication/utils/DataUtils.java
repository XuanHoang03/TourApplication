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

import com.hashmal.tourapplication.constants.FirebaseConst;
import com.hashmal.tourapplication.enums.MessageType;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
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
        return formatter.format(amount) + " vnđ";
    }
}
