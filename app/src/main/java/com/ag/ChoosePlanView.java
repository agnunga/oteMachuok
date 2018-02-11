package com.ag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class ChoosePlanView extends LinearLayout{

    public ChoosePlanView(Context context) {
        super(context);
        init();
    }

    public ChoosePlanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChoosePlanView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        View v = inflate(getContext(), R.layout.choose_plan_view, null);
        addView(v);
    }
}