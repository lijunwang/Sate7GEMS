package com.sate7.wlj.developerreader.sate7gems.test;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyDrawItemDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int count = parent.getChildCount();
        RectF rect = new RectF();
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        View view;
        for (int i = 0; i < count; i++) {
            view = parent.getChildAt(i);
            view.getX();
            rect.set(0, view.getY() + view.getHeight() - 2, view.getWidth(), view.getY() + view.getHeight());
            c.drawRect(rect, paint);
        }

        paint.setTextSize(50);
        c.drawRect(new Rect(0, 0, parent.getWidth(), 150), paint);
        paint.setColor(Color.WHITE);
        c.drawText("测试数据", 20, 100, paint);
    }
}
