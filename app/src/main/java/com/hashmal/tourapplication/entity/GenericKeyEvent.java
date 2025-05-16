package com.hashmal.tourapplication.entity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class GenericKeyEvent implements View.OnKeyListener {
    private final EditText currentView;
    private final EditText previousView;

    public GenericKeyEvent(EditText currentView, EditText previousView) {
        this.currentView = currentView;
        this.previousView = previousView;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // Nếu là phím BACKSPACE và EditText đang rỗng
        if (event.getAction() == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_DEL
                ) {
            currentView.setText(null);
            previousView.requestFocus();
            return true;
        }
        return false;
    }
}

