package com.photoeditorsdk.android.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

public class ProductDetailsActivity extends AppCompatActivity  implements View.OnTouchListener {


    public String str_category,str_name,str_details,str_customer_price,str_commission,str_discount,str_quantity,str_img1,str_img2,str_img3;
    TextView category,name,details,customer_price,commission,discount,quantity;

    //for slider
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private ViewFlipper mViewFlipper;
    private Context mContext;
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());
    ImageView img1,img2,img3;
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;
    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccentLight)));

        category= (TextView) findViewById(R.id.category);
        name= (TextView) findViewById(R.id.name);
        details= (TextView) findViewById(R.id.details);
        customer_price= (TextView) findViewById(R.id.customer_price);
        commission= (TextView) findViewById(R.id.commission);
        discount= (TextView) findViewById(R.id.discount);
        quantity= (TextView) findViewById(R.id.quantity);
        img1= (ImageView) findViewById(R.id.img1);
        img2= (ImageView) findViewById(R.id.img2);
        img3= (ImageView) findViewById(R.id.img3);

        category.setText(getIntent().getStringExtra("category"));
        name.setText(getIntent().getStringExtra("name"));
        details.setText(getIntent().getStringExtra("details"));
        customer_price.setText("BDT "+getIntent().getStringExtra("customer_price"));
        commission.setText("BDT "+getIntent().getStringExtra("commission"));
        discount.setText(getIntent().getStringExtra("discount")+"%");
        quantity.setText(getIntent().getStringExtra("quantity"));
        Picasso.with(this).load(getIntent().getStringExtra("image1")).placeholder(R.drawable.productpic1).into(img1);
        final Animation zoomAnimation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        img1.startAnimation(zoomAnimation1);
        Picasso.with(this).load(getIntent().getStringExtra("image2")).placeholder(R.drawable.productpic1).into(img2);
        final Animation zoomAnimation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        img2.startAnimation(zoomAnimation2);
        Picasso.with(this).load(getIntent().getStringExtra("image3")).placeholder(R.drawable.productpic1).into(img3);
        final Animation zoomAnimation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        img3.startAnimation(zoomAnimation3);
        img1.setOnTouchListener(this);
        img2.setOnTouchListener(this);
        img3.setOnTouchListener(this);


        //for slider start
        mContext = this;
        mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);
        left_right();
        mViewFlipper.setAutoStart(true);
        mViewFlipper.setFlipInterval(4000);
        mViewFlipper.startFlipping();
        mViewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
        findViewById(R.id.swipe_left).setOnClickListener(new View.OnClickListener() {
                                                             @Override
                                                             public void onClick(View v) {
                                                                 mViewFlipper.setAutoStart(true);
                                                                 mViewFlipper.startFlipping();
                                                                 img1.startAnimation(zoomAnimation1);
                                                                 img2.startAnimation(zoomAnimation2);
                                                                 img3.startAnimation(zoomAnimation3);
                                                                 right_left();
                                                                 return;
                                                             }
                                                         }
        );
        findViewById(R.id.swipe_right).setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View v) {
                                                                  mViewFlipper.setAutoStart(true);
                                                                  mViewFlipper.startFlipping();
                                                                  img1.startAnimation(zoomAnimation1);
                                                                  img2.startAnimation(zoomAnimation2);
                                                                  img3.startAnimation(zoomAnimation3);
                                                                  left_right();
                                                                  return;
                                                              }
                                                          }
        );
        //end
    }




    //for slider start
    public boolean left_right() {
        try   {
            // right to left swipe
            mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
            mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
            mViewFlipper.showNext();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public boolean right_left() {
        try   {
            // right to left swipe
            mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_in));
            mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,R.anim.right_out));
            mViewFlipper.showPrevious();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
                    mViewFlipper.showNext();
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_out));
                    mViewFlipper.showPrevious();
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

    }


    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        mViewFlipper.setAutoStart(false);
        mViewFlipper.stopFlipping();
        img1.clearAnimation();
        img2.clearAnimation();
        img3.clearAnimation();

        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;
        //  dumpEvent(event);
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG)
                {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                }
                else if (mode == ZOOM)
                {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f)
                    {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
//end





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,ViewProductActivity.class));
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
