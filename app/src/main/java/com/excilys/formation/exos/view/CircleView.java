package com.excilys.formation.exos.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * The view for drawing circles
 */
public class CircleView extends SurfaceView {

    private static final int RADIUS = 50;
    private static final int MAX_CIRCLES = 5;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;

    private final Paint paint;

    private boolean onMove;
    private int dragCircleIndex;
    private long lastClickTime;

    // List of circles
    ArrayList<Point> circles = new ArrayList<>(MAX_CIRCLES);

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Search for the clicked circle
     * @param x the abscissa
     * @param y the ordinate
     * @return the Point found, null otherwise
     */
    private Point getCircle(float x, float y) {
        for (Point p : circles) {
            if ((p.x - x) * (p.x - x) + (p.y - y) * (p.y - y) <= RADIUS * RADIUS) {
                return p;
            }
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw all the circles
        for (Point p : circles) {
            canvas.drawCircle(p.x, p.y, RADIUS, paint);
        }
    }

    /**
     * Remove the circle if it has been double tapped
     * @param circle the clicked circle
     * @return true if the circle has been removed
     */
    private boolean removeCircleIfDoubleTap(Point circle) {
        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            circles.set(circles.indexOf(circle), new Point(-50, -50));
            return true;
        } else {
            lastClickTime = clickTime;
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the clicked point
        float x = event.getX();
        float y = event.getY();
        Point circle = getCircle(x, y);
        // Manage actions
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (circle != null) {
                    if (!removeCircleIfDoubleTap(circle)) {
                        // Make drag and drop possible
                        onMove = true;
                        dragCircleIndex = circles.indexOf(circle);
                    }
                } else {
                    onMove = false;
                }
            case MotionEvent.ACTION_UP:
                // Add a new circle
                if (circle == null) {
                    if (circles.size() < MAX_CIRCLES) {
                        circles.add(new Point((int) x, (int) y));
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                // Move the selected circle
                if (onMove) {
                    circles.set(dragCircleIndex, new Point(new Point((int) x, (int) y)));
                    invalidate();
                }
                break;
        }
        return true;
    }
}