package com.fanxin.huangfangyi.main.widget.pull;

/**
 * 
 * @author xutao
 * 
 */
public enum SwipyRefreshLayoutDirection {

	TOP(0), // 只有下拉刷新
	BOTTOM(1), // 只有加载更多
	BOTH(2);// 全都有

	private int mValue;

	SwipyRefreshLayoutDirection(int value) {
		this.mValue = value;
	}

	public static SwipyRefreshLayoutDirection getFromInt(int value) {
		for (SwipyRefreshLayoutDirection direction : SwipyRefreshLayoutDirection
				.values()) {
			if (direction.mValue == value) {
				return direction;
			}
		}
		return BOTH;
	}

}
