
/*
package hiit.nopsa.pirate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class IslandHomeView extends View {

	private Bitmap sea = null;
	private Bitmap ship = null;
	private Bitmap icons,plus_icon;
	private final String TAG = "NOPSA-P";
	private Activity islandHomeActivity;
	private Intent collectItems;
	
	public IslandHomeView(Context context, Activity activity) {
		super(context);
		islandHomeActivity = activity;
		infoDialog();
		// TODO Auto-generated constructor stub
	}
	
	protected void onDraw(Canvas canvas){
		if (sea==null)
			loadBitmaps();
		//==========Draw Sea & Draw Ship
		Paint sea_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		sea_paint.setStyle(Style.FILL);
		canvas.drawBitmap(sea, 0, 0, sea_paint);
		canvas.drawBitmap(ship, 0, 0, sea_paint);		
		
		//==========Draw Animal, Slaves, and Food Icons & BACK icon
		Paint icon_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		icon_paint.setStyle(Style.FILL);
		plus_icon = BitmapFactory.decodeResource(getResources(), R.drawable.collect_icon);
		
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.ship_wheel);
		canvas.drawBitmap(icons, 904, 480, icon_paint);
		
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.animal_icon);
		canvas.drawBitmap(icons, 20, 20, icon_paint);
		canvas.drawBitmap(plus_icon, 80, 80, icon_paint);		
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.slave_icon);
		canvas.drawBitmap(icons, 20, 140, icon_paint);
		canvas.drawBitmap(plus_icon, 80, 200, icon_paint);
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.food_icon);
		canvas.drawBitmap(icons, 20, 260, icon_paint);
		canvas.drawBitmap(plus_icon, 80, 320, icon_paint);	
	}
	
	private void loadBitmaps(){
		System.gc();
		sea = BitmapFactory.decodeResource(getResources(), R.drawable.sea_island);
		ship = BitmapFactory.decodeResource(getResources(), R.drawable.ship_look);
	}
	
	private void infoDialog(){
		InstructionDialog id = new InstructionDialog();
		String title = "Avast! You found a deserted island...";
		String text = "Now its time to catch some animals, collect some food and capture slaves. Remember that" +
				" when you have more animals and more slaves they need more food.. ";
		id.popInstructionsDialog(title, text, islandHomeActivity);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			// Touched the screen
			if ((480<me.getY())&&(580>me.getY())&&(1004>me.getX())&&(904<me.getX())){
				// Go Back to HOME Screen
				Log.d(TAG, "Back to Home Screen");
				sea = null;
				ship = null;
				System.gc();
				islandHomeActivity.setResult(1);
				islandHomeActivity.finish();
			}
			
			collectItems = new Intent(islandHomeActivity,CollectItems.class);
			if ((20<me.getY())&&(120>me.getY())&&(120>me.getX())&&(20<me.getX())){
				// TODO Catch Animals Implementation
				collectItems.putExtra("type", 0);
	    		islandHomeActivity.startActivity(collectItems);
			}
			if ((140<me.getY())&&(240>me.getY())&&(120>me.getX())&&(20<me.getX())){
				// TODO Catch Slaves
				collectItems.putExtra("type", 1);
	    		islandHomeActivity.startActivity(collectItems);
			}
			if ((260<me.getY())&&(360>me.getY())&&(120>me.getX())&&(20<me.getX())){
				// TODO Collect Food
				collectItems.putExtra("type", 2);
	    		islandHomeActivity.startActivity(collectItems);
			}
			
		}
		return true;
	}
}

*/



package hiit.nopsa.pirate;

import java.security.acl.LastOwnerException;
import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class IslandHomeView extends SurfaceView implements SurfaceHolder.Callback{

	private final String TAG = "NOPSA-P";
	
	private Bitmap sea1 = null;
	private Bitmap ship, icons, button;
	
	private Activity islandHomeActivity;
	private Intent collectItems;
	
	private InstructionDialog id;
	private ViewControllerThread _thread;
	private boolean activityIsOnTop = true; 
	private boolean showMenuButtons = false;
	private float glowValue = 0;
	private int angle;
	
	private int selectedKey;
	private final int FOOD = 1;
	private final int SLAVE = 2;
	private final int ANIMAL = 3;
	private final int EXIT = 4;
	private final int MARKET = 5;
	int selected_x,selected_y;
	
	private Bitmap plus_icon;
	

	public IslandHomeView(Context context, Activity activity) {
		super(context);
		islandHomeActivity = activity;
		getHolder().addCallback(this);
		_thread = new ViewControllerThread(getHolder(), this);
		setFocusable(true);
		infoDialog();
	}
	
	protected void onDraw(Canvas canvas){
		if (sea1 == null){
			loadBitmaps();
		    startGameTimeElapseThread();
		}
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.BLACK);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		Paint button_glow = new Paint(Paint.ANTI_ALIAS_FLAG);
		button_glow.setAlpha((int) glowValue);
		Bitmap glow_center = BitmapFactory.decodeResource(getResources(), R.drawable.center_button_glow);
		
		try{
			//Log.d(TAG,"DRAWSKY onDraw() Called");
			//==========Draw Sea & Sky & Draw Ship
			Paint sea_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			sea_paint.setStyle(Style.FILL);
			canvas.drawBitmap(sea1, 0, 0, sea_paint);
			//Paint ship_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			//ship_paint.setStyle(Style.FILL);
			canvas.drawBitmap(ship, 0, 0, sea_paint);
			//canvas.drawCircle(501, 399, 100, button_glow);
			canvas.drawBitmap(button, 416,314, sea_paint);
			canvas.drawBitmap(glow_center, 402,302, button_glow);
		}
		catch (NullPointerException ne) {
			Log.d(TAG,"sea1 or ship Bitmaps are NULL");
		}
		
		if (showMenuButtons){
			//============ Show Selected Button
			if (selectedKey>0){
				Paint button_select = new Paint(Paint.ANTI_ALIAS_FLAG);
				button_select.setColor(Color.WHITE);
				button_select.setAlpha(150);
				switch (selectedKey) {
				case ANIMAL:
					selected_x = 300;
					selected_y = 250;
					break;
				case FOOD:
					selected_x = 700;
					selected_y = 250;
					break;
				case SLAVE:
					selected_x = 500;
					selected_y = 150;
					break;
				case EXIT:
					selected_x = 954;
					selected_y = 530;
					break;
				case MARKET:
					selected_x = 70;
					selected_y = 530;
					break;
				default:
					break;
				}
				canvas.drawCircle(selected_x, selected_y, 100, button_select);
			}
			//==========Draw Animal, Slaves, and Food Icons & BACK icon
			Paint icon_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			icon_paint.setStyle(Style.FILL);			
			plus_icon = BitmapFactory.decodeResource(getResources(), R.drawable.collect_icon);
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.animal_icon);
			canvas.drawBitmap(icons, 250, 200, icon_paint);
			canvas.drawBitmap(plus_icon, 310, 260, icon_paint);	
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.slave_icon);
			canvas.drawBitmap(icons, 450, 100, icon_paint);
			canvas.drawBitmap(plus_icon, 510, 160, icon_paint);	
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.food_icon);
			canvas.drawBitmap(icons, 650, 200, icon_paint);
			canvas.drawBitmap(plus_icon, 710, 260, icon_paint);	

			icons = BitmapFactory.decodeResource(getResources(), R.drawable.back_icon);
			canvas.drawBitmap(icons, 904, 480, icon_paint);
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.coins_icon);
			canvas.drawBitmap(icons,20,480 , icon_paint);			
		}
		
		//=========Write(Draw) Text (Ship Class & etc..)
		Paint text_paint = new Paint();
		text_paint.setColor(Color.BLACK);
		text_paint.setStyle(Style.FILL);
		text_paint.setAntiAlias(true);
		text_paint.setTextSize(20);
		text_paint.setTypeface(Typeface.SANS_SERIF);
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			if (cartDist(501, 399, (int)me.getX(), (int)me.getY())<90){
				showMenuButtons = true;
			}
			selectedKey = 0;
		}
		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			//TODO
			if (cartDist(501, 399, (int)me.getX(), (int)me.getY())>70){
				angle = getAngle(501, 399, (int)me.getX(), (int)me.getY());
				if ((angle<-1)&&(angle>-60))
					selectedKey = FOOD;
				else if ((angle<-60)&&(angle>-120))
					selectedKey = SLAVE;
				else if ((angle<-120)&&(angle>-179))
					selectedKey = ANIMAL;
				else if ((angle<60)&&(angle>0))
					selectedKey = EXIT;
				else if ((angle<179)&&(angle>120))
					selectedKey = MARKET;
				else
					selectedKey = 0;
			}
			else
				selectedKey = 0;
		}
		if (me.getAction() == MotionEvent.ACTION_UP){
			if (showMenuButtons){
				showMenuButtons = false;
				collectItems = new Intent(islandHomeActivity,CollectItems.class);
			
				switch (selectedKey) {
			
				case EXIT:
					// Go Back to Sailing
					Log.d(TAG, "Back to Home Screen");
					sea1 = null;
					ship = null;
					System.gc();
					islandHomeActivity.setResult(1);
					islandHomeActivity.finish();
					break;
					
				case FOOD:
					collectItems.putExtra("type", 2);
		    		islandHomeActivity.startActivity(collectItems);
					break;
					
				case ANIMAL:
					collectItems.putExtra("type", 0);
		    		islandHomeActivity.startActivity(collectItems);
					break;
					
				case SLAVE:
					collectItems.putExtra("type", 1);
		    		islandHomeActivity.startActivity(collectItems);
					break;
					
				case MARKET:
					// TODO - MArket Activity
					Intent market = new Intent(islandHomeActivity,MarketHome.class);
					islandHomeActivity.startActivityForResult(market, 233);
					break;
	
				default:
					break;
				}
			}
		}
		return true;
	}
	
	private int getAngle(int x1, int y1, int x2, int y2){
		double theta = Math.atan2((y2-y1),(x2-x1));
		theta = theta*57.2957795;
		return (int) theta;
	}
	private int cartDist(int x1, int y1, int x2, int y2){
		 return (int) Math.sqrt((Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
	}
	
	private synchronized void startGameTimeElapseThread(){
		if (activityIsOnTop){
			new Thread(new Runnable() {
				public void run() {
						for (int i=0;i<10;i++){
							Date d = new Date();
							glowValue = (((float) Math.sin((d.getTime()/10)*0.0174532925))*120)+120;
							android.os.SystemClock.sleep(100);
						}
						startGameTimeElapseThread();
			    }
			}).start();
		}
	}

	private void loadBitmaps(){
		System.gc();
		sea1 = BitmapFactory.decodeResource(getResources(), R.drawable.sea_island);
		ship = BitmapFactory.decodeResource(getResources(), R.drawable.ship_look);
		button = BitmapFactory.decodeResource(getResources(), R.drawable.center_button);
	}
	
	private void infoDialog(){
		InstructionDialog id = new InstructionDialog();
		String title = "Avast! You found a deserted island...";
		String text = "Now its time to catch some animals, collect some food and capture slaves. Remember that" +
				" when you have more animals and more slaves they need more food.. ";
		id.popInstructionsDialog(title, text, islandHomeActivity);
	}
	
	public void gameResumeFromCollectItems(){
		_thread = new ViewControllerThread(getHolder(), this);
		_thread.setRunning(true);
		activityIsOnTop = true; // Child Intent terminated and currently this Activity is alive
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		_thread = new ViewControllerThread(getHolder(), this);
		_thread.setRunning(true);
		_thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		 boolean retry = true;
		    _thread.setRunning(false);
		    while (retry) {
		        try {
		            _thread.join();
		            retry = false;
		        } catch (InterruptedException e) {
		            // we will try it again and again...
		        }
		    }	
	}
	
    class ViewControllerThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private IslandHomeView _islandHomeView;
        private boolean _run = false;
     
        public ViewControllerThread(SurfaceHolder sh, IslandHomeView ihv) {
            _surfaceHolder = sh;
            _islandHomeView = ihv;
        }
     
        public void setRunning(boolean run) {
            _run = run;
        }
        
        public SurfaceHolder getSurfaceHolder(){
        	return _surfaceHolder;
        }
     
        @Override
        public void run() {
    	   Canvas c;
    	    while (_run) {
    	        c = null;
    	        try {
    	            c = _surfaceHolder.lockCanvas(null);
    	            synchronized (_surfaceHolder) {
    	                _islandHomeView.onDraw(c);
    	            }
    	        } finally {
    	            // do this in a finally so that if an exception is thrown
    	            // during the above, we don't leave the Surface in an
    	            // inconsistent state
    	            if (c != null) {
    	                _surfaceHolder.unlockCanvasAndPost(c);
    	            }
    	        }
    	    }     
        }
    }
	
}
