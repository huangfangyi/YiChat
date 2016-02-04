package com.fanxin.app.fx.others;

 

import com.fanxin.app.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
 
public class AutoListView extends ListView implements OnScrollListener {

	 
	public static final int REFRESH = 0;
	public static final int LOAD = 1;

 
	private static final int SPACE = 20;

	 private static final int NONE = 0;
	private static final int PULL = 1;
	private static final int RELEASE = 2;
	private static final int REFRESHING = 3;
	private int state;

	private LayoutInflater inflater;
	private View header;
	private View footer;
	private TextView tip;
	private TextView lastUpdate;
	private ImageView arrow;
	private ProgressBar refreshing;

	private TextView noData;
	private TextView loadFull;
	private TextView more;
	private ProgressBar loading;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY;

	private int firstVisibleItem;
	private int scrollState;
	private int headerContentInitialHeight;
	private int headerContentHeight;

	 
	private boolean isRecorded;
	private boolean isLoading; 
	private boolean loadEnable = true; 
	private boolean isLoadFull;
	private int pageSize = 10;

	private OnRefreshListener onRefreshListener;
	private OnLoadListener onLoadListener;

	public AutoListView(Context context) {
		super(context);
		initView(context);
	}

	public AutoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public AutoListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	 
	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		this.onRefreshListener = onRefreshListener;
	}
 
	public void setOnLoadListener(OnLoadListener onLoadListener) {
		this.loadEnable = true;
		this.onLoadListener = onLoadListener;
	}

	public boolean isLoadEnable() {
		return loadEnable;
	}

	 
	public void setLoadEnable(boolean loadEnable) {
		this.loadEnable = loadEnable;
		this.removeFooterView(footer);
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	 
	private void initView(Context context) {
 	animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(100);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(100);
		reverseAnimation.setFillAfter(true);

		inflater = LayoutInflater.from(context);
		footer = inflater.inflate(R.layout.listview_footer, null);
		loadFull = (TextView) footer.findViewById(R.id.loadFull);
		noData = (TextView) footer.findViewById(R.id.noData);
		more = (TextView) footer.findViewById(R.id.more);
		loading = (ProgressBar) footer.findViewById(R.id.loading);

		header = inflater.inflate(R.layout.pull_to_refresh_header, null);
		arrow = (ImageView) header.findViewById(R.id.arrow);
		tip = (TextView) header.findViewById(R.id.tip);
		lastUpdate = (TextView) header.findViewById(R.id.lastUpdate);
		refreshing = (ProgressBar) header.findViewById(R.id.refreshing);

	 
		headerContentInitialHeight = header.getPaddingTop();
		measureView(header);
		header.setTag("HEADER");
		footer.setTag("FOOTER");
		headerContentHeight = header.getMeasuredHeight();
		topPadding(-headerContentHeight);
		this.addHeaderView(header);
		this.addFooterView(footer);
		this.setOnScrollListener(this);
	}

	public void onRefresh() {
		if (onRefreshListener != null) {
			onRefreshListener.onRefresh();
		}
	}

	public void onLoad() {
		if (onLoadListener != null) {
			onLoadListener.onLoad();
		}
	}

	public void onRefreshComplete(String updateTime) {
		lastUpdate.setText(this.getContext().getString(R.string.lastUpdateTime,
				TimeUtils.getCurrentTime()));
		state = NONE;
		refreshHeaderViewByState();
	}
 
	public void onRefreshComplete() {
		String currentTime = TimeUtils.getCurrentTime();
		onRefreshComplete(currentTime);
	}

 
	public void onLoadComplete() {
		isLoading = false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		ifNeedLoad(view, scrollState);
	}
 
	private void ifNeedLoad(AbsListView view, int scrollState) {
		if (!loadEnable) {
			return;
		}
		try {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& !isLoading
					&& view.getLastVisiblePosition() == view
							.getPositionForView(footer) && !isLoadFull) {
				onLoad();
				isLoading = true;
			}
		} catch (Exception e) {
		}
	}

 
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (firstVisibleItem == 0) {
				isRecorded = true;
				startY = (int) ev.getY();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (state == PULL) {
				state = NONE;
				refreshHeaderViewByState();
			} else if (state == RELEASE) {
				state = REFRESHING;
				refreshHeaderViewByState();
				onRefresh();
			}
			isRecorded = false;
			break;
		case MotionEvent.ACTION_MOVE:
			whenMove(ev);
			break;
		}
		return super.onTouchEvent(ev);
	}

	// ������ƣ�ˢ��header״̬
	private void whenMove(MotionEvent ev) {
		if (!isRecorded) {
			return;
		}
		int tmpY = (int) ev.getY();
		int space = tmpY - startY;
		int topPadding = space - headerContentHeight;
		switch (state) {
		case NONE:
			if (space > 0) {
				state = PULL;
				refreshHeaderViewByState();
			}
			break;
		case PULL:
			topPadding(topPadding);
			if (scrollState == SCROLL_STATE_TOUCH_SCROLL
					&& space > headerContentHeight + SPACE) {
				state = RELEASE;
				refreshHeaderViewByState();
			}
			break;
		case RELEASE:
			topPadding(topPadding);
			if (space > 0 && space < headerContentHeight + SPACE) {
				state = PULL;
				refreshHeaderViewByState();
			} else if (space <= 0) {
				state = NONE;
				refreshHeaderViewByState();
			}
			break;
		}

	}

	 
	private void topPadding(int topPadding) {
		header.setPadding(header.getPaddingLeft(), topPadding,
				header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();
	}

	 
	public void setResultSize(int resultSize) {
		if (resultSize == 0) {
			isLoadFull = true;
			loadFull.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			noData.setVisibility(View.VISIBLE);
		} else if (resultSize > 0 && resultSize < pageSize) {
			isLoadFull = true;
			loadFull.setVisibility(View.VISIBLE);
			loading.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			noData.setVisibility(View.GONE);
		} else if (resultSize == pageSize) {
			isLoadFull = false;
			loadFull.setVisibility(View.GONE);
			loading.setVisibility(View.VISIBLE);
			more.setVisibility(View.VISIBLE);
			noData.setVisibility(View.GONE);
		}

	}

	// ��ݵ�ǰ״̬������header
	private void refreshHeaderViewByState() {
		switch (state) {
		case NONE:
			topPadding(-headerContentHeight);
			tip.setText(R.string.pull_to_refresh);
			refreshing.setVisibility(View.GONE);
			arrow.clearAnimation();
			arrow.setImageResource(R.drawable.pull_to_refresh_arrow);
			break;
		case PULL:
			arrow.setVisibility(View.VISIBLE);
			tip.setVisibility(View.VISIBLE);
			lastUpdate.setVisibility(View.VISIBLE);
			refreshing.setVisibility(View.GONE);
			tip.setText(R.string.pull_to_refresh);
			arrow.clearAnimation();
			arrow.setAnimation(reverseAnimation);
			break;
		case RELEASE:
			arrow.setVisibility(View.VISIBLE);
			tip.setVisibility(View.VISIBLE);
			lastUpdate.setVisibility(View.VISIBLE);
			refreshing.setVisibility(View.GONE);
			tip.setText(R.string.pull_to_refresh);
			tip.setText(R.string.release_to_refresh);
			arrow.clearAnimation();
			arrow.setAnimation(animation);
			break;
		case REFRESHING:
			topPadding(headerContentInitialHeight);
			refreshing.setVisibility(View.VISIBLE);
			arrow.clearAnimation();
			arrow.setVisibility(View.GONE);
			tip.setVisibility(View.GONE);
			lastUpdate.setVisibility(View.GONE);
			break;
		}
	}

 
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

 
	public interface OnRefreshListener {
		public void onRefresh();
	}

 
	public interface OnLoadListener {
		public void onLoad();
	}

}
