 
package com.fanxin.app;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class Constant {
    
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
 	public static final String ACCOUNT_REMOVED = "account_removed";	
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

    //服务器端
    public static final String URL_Register = "http://120.24.211.126/fanxin/register.php";
    public static final String URL_Register_Tel = "http://120.24.211.126/fanxin/register_tel.php";
    public static final String URL_Login = "http://120.24.211.126/fanxin/login_post.php";
  //  public static final String URL_Friends = "http://120.24.211.126/fanxin/get_allfriends.php";
    public static final String URL_FriendList = "http://120.24.211.126/fanxin/getMyFriends.php";
    public static final String URL_Avatar = "http://120.24.211.126/fanxin/upload/";
    public static final String URL_UPDATE_Avatar = "http://120.24.211.126/fanxin/update_avatar.php";
    public static final String URL_UPDATE_Nick = "http://120.24.211.126/fanxin/update_nick.php";
    public static final String URL_UPDATE_Fxid= "http://120.24.211.126/fanxin/update_fxid.php";
    public static final String URL_UPDATE_Region= "http://120.24.211.126/fanxin/update_region.php";
    public static final String URL_UPDATE_Sign= "http://120.24.211.126/fanxin/update_sign.php";
    public static final String URL_Search_User= "http://120.24.211.126/fanxin/search_friends.php";
    public static final String URL_Get_UserInfo= "http://120.24.211.126/fanxin/get_userinfo.php";
    public static final String URL_UPDATE_Sex = "http://120.24.211.126/fanxin/update_sex.php";
    public static final String URL_UPDATE_Groupnanme ="http://120.24.211.126/fanxin/update_groupname.php";
    public static final String URL_UPDATETIME ="http://120.24.211.126/fanxin/update_time.php";
    public static final String URL_LASTERLOGIN="http://120.24.211.126/fanxin/laster_login.php";
    public static final String URL_PAY="http://120.24.211.126/fanxin/pay.php";
    public static final String URL_RECORDS="http://120.24.211.126/fanxin/records.php";
    public static final String URL_CARDS="http://120.24.211.126/fanxin/cardList.php";
    public static final String URL_ADD_CARD="http://120.24.211.126/fanxin/addCard.php";
    public static final String URL_RECHARGE="http://120.24.211.126/fanxin/recharge.php";
    public static final String URL_WIRHDROW="http://120.24.211.126/fanxin/withdraw.php";
    
    //添加好友通知
    public static final String CMD_ADD_FRIEND="ADD_FRIEND";
    public static final String CMD_AGREE_FRIEND="AGREE_FRIEND";
    
    public static final String URL_ADD_FRIEND="http://120.24.211.126/fanxin/accept_friend.php";
    //交易記錄
    public static final String C_RECORDS = "ja_records";
    public static final String CARDLIST = "ja_cardlist";
    
    //朋友圈接口
    // 服务器端
    public static final String URL_PUBLISH = "http://120.24.211.126/fanxin/publish.php";
    public static final String URL_SOCIAL = "http://120.24.211.126/fanxin/social.php";
    public static final String URL_SOCIAL_PHOTO = "http://120.24.211.126/fanxin/upload/";
    public static final String URL_SOCIAL_COMMENT = "http://120.24.211.126/fanxin/comment.php";
    public static final String URL_SOCIAL_GOOD = "http://120.24.211.126/fanxin/social_good.php";
    public static final String URL_SOCIAL_GOOD_CANCEL = "http://120.24.211.126/fanxin/social_good_cancel.php";
    public static final String URL_SOCIAL_DELETE_COMMENT = "http://120.24.211.126/fanxin/social_comment_delete.php";
    public static final String URL_SOCIAL_DELETE = "http://120.24.211.126/fanxin/social_delete.php";
    public static final String URL_SOCIAL_FRIEND = "http://120.24.211.126/fanxin/social_friend.php";

    public static ImageLoader imageLoader = ImageLoader.getInstance();
    public static DisplayImageOptions IM_IMAGE_OPTIONS = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_empty)
            .showImageForEmptyUri(R.drawable.ic_error)
            .showImageOnFail(R.drawable.ic_error)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .displayer(new RoundedBitmapDisplayer(5)) // default //
                                                        // 可以设置动画，比如圆角或者渐
            .cacheInMemory(true).cacheOnDisc(false).build();
    
    //打赏登记接口
    public static final String URL_ALIPAYME= "http://120.24.211.126/fanxin/alipayme.php";
    public static final String URL_ALIPAYMELIST= "http://120.24.211.126/fanxin/alipaymeList.php";
    
}
