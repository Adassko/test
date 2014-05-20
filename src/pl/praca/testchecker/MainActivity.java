package pl.praca.testchecker;
// testtt
// to jest linijka z branchu proba

import pl.praca.testchecker.util.*;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity {
    private CameraPreview mPreview;
    private RelativeLayout mLayout;
    private DrawingView drawingView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status-bar
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide title-bar, must be before setContentView
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // Requires RelativeLayout.
        mLayout = new RelativeLayout(this);
        setContentView(mLayout);
        
        drawingView = new DrawingView(this);
        LayoutParams layoutParamsDrawing 
        	= new LayoutParams(LayoutParams.MATCH_PARENT, 
        			LayoutParams.MATCH_PARENT);
        this.addContentView(drawingView, layoutParamsDrawing);
        drawingView.bringToFront();
    }

    int mode = 0;
    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
    	super.onTouchEvent(event);
    	if(event.getAction() == MotionEvent.ACTION_UP)
    	{
    		mPreview.autoFocus();
			drawingView.invalidate();
    	}
		return false;
    };
    
    @Override
    protected void onResume() {
        super.onResume();
        // Set the second argument by your choice.
        // Usually, 0 for back-facing camera, 1 for front-facing camera.
        // If the OS is pre-gingerbreak, this does not have any effect.
        mPreview = new CameraPreview(this, 0, CameraPreview.LayoutMode.FitToParent);
        LayoutParams previewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        // Un-comment below lines to specify the size.
        //previewLayoutParams.height = 500;
        //previewLayoutParams.width = 500;

        // Un-comment below line to specify the position.
        //mPreview.setCenterPosition(270, 130);
        
        Camera.Parameters params = mPreview.getCamera().getParameters();
		params.setSceneMode(Camera.Parameters.SCENE_MODE_BARCODE);
		params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
		stabilize(params);
		mPreview.getCamera().setParameters(params);
    
        mLayout.addView(mPreview, 0, previewLayoutParams);
    }
    
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	private void stabilize(Camera.Parameters params)
    {
		params.setVideoStabilization(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
       	mPreview.stop();
       	mLayout.removeView(mPreview); // This is necessary.
      	mPreview = null;
    }
    
    private class DrawingView extends View{
		int i = 0;
		Paint drawingPaint;

		public DrawingView(Context context) {
			super(context);
			drawingPaint = new Paint();
			drawingPaint.setColor(Color.GREEN);
			drawingPaint.setStyle(Paint.Style.STROKE); 
			drawingPaint.setStrokeWidth(2);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			i++;

			// Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
			// UI coordinates range from (0, 0) to (width, height).

			int vWidth = getWidth();
			int vHeight = getHeight();

			drawingPaint.setColor(Color.rgb((int)(256 - (i * 1.36) % 256), i % 256, (int)((i * 1.2) % 256)));


			canvas.drawRect(
					i % vWidth, 10, i % vWidth + 1, vHeight - 10,  
							drawingPaint);
//				canvas.drawColor(Color.TRANSPARENT);
		}
		
	}
}
