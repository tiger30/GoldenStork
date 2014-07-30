package com.android.gs.components.slidemenu;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.android.gs.activity.BaseActivity;
import com.android.gs.listener.OnSlideMenuChangeState;
import com.android.gs.utils.GlobalUtil;


public class MainLayout extends LinearLayout {
	static final int MIN_OFFSET = 100;
	private static final int SLIDING_DURATION = 500;
	private static final int QUERY_INTERVAL = 16;
	int mainLayoutWidth;
	private View menu;
	private View content;

	private enum MenuState {
		HIDING, HIDDEN, SHOWING, SHOWN,
	};

	private int contentXOffset;
	private MenuState currentMenuState = MenuState.HIDDEN;
	private Scroller menuScroller = new Scroller(this.getContext(),
			new EaseInInterpolator());
	private Runnable menuRunnable = new MenuRunnable();
	private Handler menuHandler = new Handler();
	int prevX = 0;
	int prevY = 0;
	boolean isDragging = false;
	boolean isVerticalScrolling = false;
	int lastDiffX = 0;
	OnSlideMenuChangeState childListener;
	public MainLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MainLayout(Context context) {
		super(context);
	}
	
	public void setChildListener(OnSlideMenuChangeState childListener){
		this.childListener = childListener;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mainLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
//		menuRightMargin = mainLayoutWidth * 10 / 100;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		menu = this.getChildAt(0);
		content = this.getChildAt(1);
		content.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return MainLayout.this.onContentTouch(v,event);
			}
		});
		
		setListViewDrag((ViewGroup) content);
		menu.setVisibility(View.GONE);
	}
	
	void setListViewDrag(ViewGroup vg){

		for(int i = 0,count = vg.getChildCount();i<count;i++){
			View v = vg.getChildAt(i);
			if(v instanceof ViewGroup){
				v.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						return MainLayout.this.onContentTouch(v,event);
					}
				});
				setListViewDrag((ViewGroup) v);
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if (changed) {
			LayoutParams contentLayoutParams = (LayoutParams) content
					.getLayoutParams();
//			contentLayoutParams.height = this.getHeight();
			contentLayoutParams.width = this.getWidth();
			LayoutParams menuLayoutParams = (LayoutParams) menu
					.getLayoutParams();
//			menuLayoutParams.height = this.getHeight();
			menuLayoutParams.width = this.getWidth()  /* - menuRightMargin*/;
		}
		menu.layout(left, top, right /* - menuRightMargin */, bottom);
		content.layout(left + contentXOffset, top, right + contentXOffset,
				bottom);

	}
	

	/**
	 * toogle menu
	 * 
	 * DoanDM
	 * Mar 19, 2014
	 */
	public void toggleMenu() {

		if (currentMenuState == MenuState.HIDING
				|| currentMenuState == MenuState.SHOWING)
			return;

		switch (currentMenuState) {
		case HIDDEN:
			currentMenuState = MenuState.SHOWING;
			menu.setVisibility(View.VISIBLE);
			menuScroller.startScroll(0, 0, GlobalUtil.getSlideMenuWidth(), 0,
					SLIDING_DURATION);
			break;
		case SHOWN:
			currentMenuState = MenuState.HIDING;
			menuScroller.startScroll(contentXOffset, 0, -contentXOffset, 0,
					SLIDING_DURATION);
			break;
		default:
			break;
		}
		menuHandler.postDelayed(menuRunnable, QUERY_INTERVAL);
		this.invalidate();
	}
	boolean isScrolling = false;

	protected class MenuRunnable implements Runnable {
		@Override
		public void run() {
			isScrolling = menuScroller.computeScrollOffset();
			adjustContentPosition(isScrolling);
		}
	}
	
	public boolean isScrolling(){
		return isScrolling;
	}
	
	public boolean isDragging(){
		return contentXOffset >= MIN_OFFSET ;
	}

	private void adjustContentPosition(boolean isScrolling) {
		int scrollerXOffset = menuScroller.getCurrX();
		content.offsetLeftAndRight(scrollerXOffset - contentXOffset);

		contentXOffset = scrollerXOffset;
		this.invalidate();
		if (isScrolling)
			menuHandler.postDelayed(menuRunnable, QUERY_INTERVAL);
		else
			this.onMenuSlidingComplete();
	}

	private void onMenuSlidingComplete() {
		switch (currentMenuState) {
		case SHOWING:
			currentMenuState = MenuState.SHOWN;
			if(childListener!=null)
			{
				childListener.onStateChange(true);
			}
			break;
		case HIDING:
			currentMenuState = MenuState.HIDDEN;
			menu.setVisibility(View.GONE);
			if(childListener!=null)
			{
				childListener.onStateChange(false);
			}
			break;
		default:
			return;
		}
	}

	protected class EaseInInterpolator implements Interpolator {
		@Override
		public float getInterpolation(float t) {
			return (float) Math.pow(t - 1, 5) + 1;
		}

	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(isMenuShown() && (ev.getX() >= contentXOffset)){
			toggleMenu();
			return true;
			
		}
		return super.onInterceptTouchEvent(ev);
	}
	

	public boolean isMenuShown() {
		return currentMenuState == MenuState.SHOWN;
	}
	

	public boolean onContentTouch(View v, MotionEvent event) {
//		if (currentMenuState == MenuState.HIDING
//				|| currentMenuState == MenuState.SHOWING)
//			return true;
		if(isScrolling){
			return true;
		}
		int curX = (int) event.getRawX();
		int curY = (int) event.getRawY();
		int diffX = 0;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			prevX = curX;
			prevY = curY;
			return false;

		case MotionEvent.ACTION_MOVE:
			 if(((BaseActivity)getContext()).isInSearchMode()){
					return false;
				}
			if((!isDragging && Math.abs(curY - prevY) > Math.abs(curX - prevX)) || isVerticalScrolling){
				isVerticalScrolling = true;
				return false;
			}
			if (!isDragging) {
				isDragging = true;
				menu.setVisibility(View.VISIBLE);
			}
			diffX = curX - prevX;
			if (contentXOffset + diffX <= 0) {
				diffX = -contentXOffset;
			} else if (contentXOffset + diffX > mainLayoutWidth
					/*- menuRightMargin*/) {
				diffX = mainLayoutWidth /*- menuRightMargin*/ - contentXOffset;
			}
			content.offsetLeftAndRight(diffX);
			contentXOffset += diffX;
			
			this.invalidate();

			prevX = curX;
			lastDiffX = diffX;
			return true;

		case MotionEvent.ACTION_UP:
			isVerticalScrolling = false;
			isDragging = false;
			boolean result = true;
			if(!isDragging()){
				result = false;
			}
			
			if (lastDiffX >= 0 && contentXOffset >= MIN_OFFSET) {
				currentMenuState = MenuState.SHOWING;
				if(contentXOffset > GlobalUtil.getSlideMenuWidth()){
					menuScroller.startScroll(contentXOffset, 0,
							-(contentXOffset - GlobalUtil.getSlideMenuWidth()), 0,
							SLIDING_DURATION);
				}else{
					menuScroller.startScroll(contentXOffset, 0,
							GlobalUtil.getSlideMenuWidth() - contentXOffset, 0,
							SLIDING_DURATION);
				}
			} else if (lastDiffX < 0 && contentXOffset >= MIN_OFFSET ) {
				currentMenuState = MenuState.HIDING;
				menuScroller.startScroll(contentXOffset, 0, - contentXOffset, 0,
						SLIDING_DURATION);
			}
			else{
				
			}
			menuHandler.postDelayed(menuRunnable, QUERY_INTERVAL);
			this.invalidate();
			
			prevX = 0;
			prevY = 0;
			lastDiffX = 0;
			if(result &&(v instanceof RelativeLayout || v instanceof LinearLayout)){
				v.setSelected(false);
				v.setPressed(false);
				deselectChild((ViewGroup) v);
			}	
			return result;

		default:
			break;
		}

		return false;
	}
	
	void deselectChild(ViewGroup view){
		for(int i = 0,size = view.getChildCount();i<size;i++){
			View v = view.getChildAt(i);
			if(v instanceof RelativeLayout || v instanceof LinearLayout){
				v.setSelected(false);
				v.setPressed(false);
				deselectChild((ViewGroup) v);
			}
		}
	}
}
