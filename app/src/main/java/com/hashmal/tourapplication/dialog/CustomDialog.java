package com.hashmal.tourapplication.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hashmal.tourapplication.R;

public class CustomDialog extends Dialog {
    private String title;
    private String message;
    private Drawable icon;
    private String positiveButtonText;
    private String negativeButtonText;
    private OnClickListener positiveClickListener;
    private OnClickListener negativeClickListener;
    private boolean isSingleButtonMode = false;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        // Initialize views
        ImageView dialogIcon = findViewById(R.id.dialogIcon);
        TextView dialogTitle = findViewById(R.id.dialogTitle);
        TextView dialogMessage = findViewById(R.id.dialogMessage);
        Button positiveButton = findViewById(R.id.positiveButton);
        Button negativeButton = findViewById(R.id.negativeButton);

        // Set content
        if (icon != null) {
            dialogIcon.setImageDrawable(icon);
            dialogIcon.setVisibility(View.VISIBLE);
        } else {
            dialogIcon.setVisibility(View.GONE);
        }

        if (title != null) {
            dialogTitle.setText(title);
        }

        if (message != null) {
            dialogMessage.setText(message);
        }

        // Set button text and click listeners
        if (positiveButtonText != null) {
            positiveButton.setText(positiveButtonText);
        }
        
        if (isSingleButtonMode) {
            negativeButton.setVisibility(View.GONE);
            positiveButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
        } else {
            if (negativeButtonText != null) {
                negativeButton.setText(negativeButtonText);
                negativeButton.setVisibility(View.VISIBLE);
            }
        }

        positiveButton.setOnClickListener(v -> {
            if (positiveClickListener != null) {
                positiveClickListener.onClick(this);
            }
            dismiss();
        });

        negativeButton.setOnClickListener(v -> {
            if (negativeClickListener != null) {
                negativeClickListener.onClick(this);
            }
            dismiss();
        });
    }

    public static class Builder {
        private final CustomDialog dialog;

        public Builder(Context context) {
            dialog = new CustomDialog(context);
        }

        public Builder setTitle(String title) {
            dialog.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            dialog.message = message;
            return this;
        }

        public Builder setIcon(Drawable icon) {
            dialog.icon = icon;
            return this;
        }

        public Builder setPositiveButton(String text, OnClickListener listener) {
            dialog.positiveButtonText = text;
            dialog.positiveClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String text, OnClickListener listener) {
            dialog.negativeButtonText = text;
            dialog.negativeClickListener = listener;
            return this;
        }

        public Builder setSingleButtonMode(boolean isSingleButtonMode) {
            dialog.isSingleButtonMode = isSingleButtonMode;
            return this;
        }

        public CustomDialog create() {
            return dialog;
        }

        public void show() {
            dialog.show();
        }
    }

    public interface OnClickListener {
        void onClick(CustomDialog dialog);
    }
} 