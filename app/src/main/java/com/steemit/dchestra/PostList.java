package com.steemit.dchestra;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class PostList extends ListView {

    public PostList(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(new OnTouchListener() {
            int threshold; // The swipe threshold, at which the swipe changes the position

            float swipeY, swipeX; // How much the swipe travelled
            long lastTouch; // Used for quick swipe
            int position = 0; // The current Post that is selected
            int direction;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Setting the threshold
                if(threshold == 0) {
                    threshold = v.getHeight() / 3;
                }
                int action = event.getAction();
                float swipeY = event.getY();
                float swipeX = event.getX();
                if(action == MotionEvent.ACTION_DOWN){
                    this.swipeY = swipeY;
                    this.swipeX = swipeX;
                    lastTouch = System.currentTimeMillis();
                    direction = 0;
                }else if(action == MotionEvent.ACTION_UP){
                    if(direction == 2) {
                        if(this.swipeY < swipeY && (this.swipeY < swipeY - threshold || (this.swipeY < swipeY - 20 && System.currentTimeMillis() < lastTouch + 500))) {
                            // Scroll Up
                            smoothScrollToPosition(--position);
                        } else if(this.swipeY > swipeY && (this.swipeY > swipeY + threshold || (this.swipeY > swipeY + 20 && System.currentTimeMillis() < lastTouch + 500))){
                            // Scroll Down
                            smoothScrollToPosition(++position);
                        } else {
                            // Snap back into view
                            smoothScrollToPosition(position);
                        }
                    }
                } else if(action == MotionEvent.ACTION_MOVE) {
                    if(direction == 0) {
                        float difX = this.swipeX - swipeX; if(difX < 0) difX *= -1;
                        float difY = this.swipeY - swipeY; if(difY < 0) difY *= -1;
                        if(difX > difY && difX > 10) {
                            direction = 1; // Side Swipe
                        } else if(difY > difX && difY > 10) {
                            direction = 2; // Up/Down Swipe
                            return false;
                        } else {

                        }
                        return true;
                    } else if(direction == 1) {
                        return true;
                    }
                    return false;
                } else {
                    return false;
                }
                return true;
            }
        });
    }



}
