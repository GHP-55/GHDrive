package com.ghp55.eli.ghp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by elijn on 6/29/2018.
 */

public class OnScreenJoystick extends View {

    Canvas canvas;
    Context ctx;
    OnSlideListener listener;
    int color;

    int cX;
    int cY;
    int constantCX;//center of outer circle that won't move
    int constantCY;
    int r;
    int r2;

    boolean isSliding = false;

    interface OnSlideListener{
        public void onSlide(int x, int y);
    }

    public OnScreenJoystick(Context context) {
        super(context);
        ctx = context;
        color = Color.GREEN;
    }

    public OnScreenJoystick(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        color = Color.GREEN;
    }

    public void setColor(int col){
        color = col;
        this.invalidate();
    }

    public void setOnSlideListener(OnSlideListener l){
        this.listener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int x = (int)e.getX();
        int y = (int)e.getY();
        boolean insideBigCircle = ((x>constantCX-r2 && x<constantCX+r2)&&(y<constantCY+r2 && y>constantCY-r2));
        if(!isSliding) {
            isSliding = (e.getX() > cX - r && e.getX() < cX + r) && (e.getY() > cY - r && e.getY() < cY + r);
        }
        if(isSliding){
            double deltaX = (x-constantCX);
            deltaX = (deltaX == 0 ? deltaX+1:deltaX);
            double angle = Math.atan((y-constantCY)/deltaX);
            if(insideBigCircle){
                cX = x;
                cY = y;
            }else {
                if(x<constantCX){
                    cX = (int) (-1*(Math.cos(angle) * r2)) + constantCX;
                    cY = (int) (-1*(Math.sin(angle) * r2)) + constantCY;
                }else {
                    cX = (int) (Math.cos(angle) * r2) + constantCX;
                    cY = (int) (Math.sin(angle) * r2) + constantCY;
                }
            }
            isSliding = !(e.getAction() == MotionEvent.ACTION_UP);
            //handle event handler
            if(listener!=null)
                listener.onSlide(cX-constantCX,cY-constantCY);

            this.invalidate();
        }
        CustomViewPager.setPagingEnabled(!isSliding);
        return insideBigCircle;
    }

    @Override
    protected void onDraw(Canvas c) {
        //initialize variables for first draw
        if(canvas == null) {
            canvas = c;
            r = c.getHeight()/8;
            r2 = c.getHeight()/3;
            constantCX = canvas.getWidth()/2;
            constantCY = (canvas.getHeight()/2)-(canvas.getHeight()/6);
            resetToCenter();
        }
        if(!isSliding)
            resetToCenter();

        this.bringToFront();
        Paint p = new Paint();
        p.setColor(color);
        c.drawCircle(cX, cY, r, p);
        p.setStyle(Paint.Style.STROKE);
        c.drawCircle(constantCX, constantCY, r2, p);
        super.onDraw(c);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    //precondition: instance var canvas is not null
    //sets the circle that you drag to center of view
    private void resetToCenter(){
        cX = canvas.getWidth()/2;
        cY = (canvas.getHeight()/2)-(canvas.getHeight()/6);
        this.invalidate();
    }
}
