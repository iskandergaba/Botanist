// Quick Tutorial of app usage
// @author: Christopher Besser
package com.scientists.happy.botanist.ui;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.scientists.happy.botanist.R;
public class TutorialActivity extends AppCompatActivity {
    private ImageView mCurrentTip;
    private int tip = 0;
    private int[] ids = {R.drawable.tutorial_0, R.drawable.tutorial_1, R.drawable.tutorial_2};
    /**
     * Launch the tutorial
     * @param savedInstanceState - current app context
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        mCurrentTip = (ImageView) findViewById(R.id.tutorial);
        mCurrentTip.setImageResource(ids[tip]);
        mCurrentTip.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            /**
             * User touched the image
             * @param v - current view
             * @param e - the touch event
             * @return Returns whether this listener handled the event
             */
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN){
                    startX = e.getX();
                }
                else if (e.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        float endX = e.getX();
                        if ((startX - endX) > 40) {
                            Bitmap bmp = BitmapFactory.decodeResource(getResources(), ids[++tip]);
//                          Animation in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);
//                          Animation out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);
                            mCurrentTip.setImageBitmap(bmp);
//                          ImageViewAnimatedChange(in, out, bmp);
                        }
                        else if ((endX - startX) > 40) {
                            Bitmap bmp = BitmapFactory.decodeResource(getResources(), ids[--tip]);
//                          Animation in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);
//                          Animation out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);
                            mCurrentTip.setImageBitmap(bmp);
//                          ImageViewAnimatedChange(in, out, bmp);
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException ex) {
                        buildHelpDialog().show();
                    }
                }
                return true;
            }
        });
    }

    /**
     * Animate changing the current tip
     * @param in - transition in animation
     * @param out - transition out animation
     * @param new_image - new image to replace old one with
     */
    public void ImageViewAnimatedChange(final Animation in, final Animation out, final Bitmap new_image) {
        out.setAnimationListener(new Animation.AnimationListener() {
            /**
             * Start the animation
             * @param animation - animation to start
             */
            @Override
            public void onAnimationStart(Animation animation) {
            }

            /**
             * Repeat the animation
             * @param animation - animation to repeat
             */
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            /**
             * Stop the animation
             * @param animation - animation to end
             */
            @Override
            public void onAnimationEnd(Animation animation) {
                mCurrentTip.setImageBitmap(new_image);
                in.setAnimationListener(new Animation.AnimationListener() {
                    /**
                     * Start the animation
                     * @param animation - animation to start
                     */
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    /**
                     * Repeat the animation
                     * @param animation - animation to repeat
                     */
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    /**
                     * End the animation
                     * @param animation - animation to end
                     */
                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });
                mCurrentTip.startAnimation(in);
            }
        });
        mCurrentTip.startAnimation(in);
    }

    /**
     * User navigated up from the activity
     * @return returns true
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    /**
     * The activity was paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.slide_down);
    }

    /**
     * Make sure user wanted to leave tutorial
     * @return Returns alert window
     */
    private AlertDialog buildHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.find_tutorial).setTitle(R.string.ready);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            /**
             * User clicked confirm
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(TutorialActivity.this, MainActivity.class));
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            /**
             * User clicked cancel
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                System.out.println(tip);
                // Send tip back to legal state
                if (tip >= ids.length) {
                    tip--;
                }
                else {
                    tip++;
                }
                System.out.println(tip);
            }
        });
        return builder.create();
    }
}