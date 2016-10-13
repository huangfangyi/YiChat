package com.fanxin.huangfangyi.main.uvod.ui.base;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class UMenuItem implements Parcelable{
	public String title;
	public String type;
	public String description;
	public UMenuItem parent;
	public int defaultSelected = 0;
	public boolean isVisible;

	public List<UMenuItem> childs;


	private UMenuItem(Builder builder) {
		this.title = builder.title;
		this.description = builder.description;
		this.parent = builder.parent;
		this.childs = builder.childs;
		this.defaultSelected = builder.defaultSelected;
		this.type = builder.type;
		this.isVisible = builder.isVisible;
	}

	protected UMenuItem(Parcel in) {
		title = in.readString();
		type = in.readString();
		description = in.readString();
		parent = in.readParcelable(UMenuItem.class.getClassLoader());
		defaultSelected = in.readInt();
		isVisible = in.readByte() != 0;
		childs = in.createTypedArrayList(UMenuItem.CREATOR);
	}

	public static final Creator<UMenuItem> CREATOR = new Creator<UMenuItem>() {
		@Override
		public UMenuItem createFromParcel(Parcel in) {
			return new UMenuItem(in);
		}

		@Override
		public UMenuItem[] newArray(int size) {
			return new UMenuItem[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(type);
		dest.writeString(description);
		dest.writeParcelable(parent, flags);
		dest.writeInt(defaultSelected);
		dest.writeByte((byte) (isVisible ? 1 : 0));
		dest.writeTypedList(childs);
	}

	public static class Builder {
		private String title;
		public String type;
		private String description;
		private UMenuItem parent;
		private int defaultSelected = 0;
		private boolean isVisible;
		public List<UMenuItem> childs = new ArrayList<>();

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder decription(String description) {
			this.description = description;
			return this;
		}

		public Builder type(String type) {
			this.type = type;
			return this;
		}

		public Builder index(int index) {
			this.defaultSelected = index;
			return this;
		}

		public Builder parent(UMenuItem parent) {
			this.parent = parent;
			return this;
		}

		public Builder isVisible(boolean isVisible) {
			this.isVisible = isVisible;
			return this;
		}

		public UMenuItem builder() {
			return new UMenuItem(this);
		}
	}
}