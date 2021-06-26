//
//  ProjectRequestModel.h
//  YiChat_iOS
//
//  Created by mac on 2019/8/30.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef enum : NSUInteger {
    RequestApiType_user_bank_card_delete,
    RequestApiType_version,
    RequestApiType_pay_alipay_pre,
    RequestApiType_pay_weixin_pre,
    RequestApiType_trend_background_set,
    RequestApiType_trend_background,
    RequestApiType_trend_comment_list,
    RequestApiType_trend_comment_delete,
    RequestApiType_trend_comment,
    RequestApiType_trend_praise_cancle,
    RequestApiType_trend_praise,
    RequestApiType_trend_friend_list,
    RequestApiType_trend_list,
    RequestApiType_trend_delete,
    RequestApiType_trend_publish,
    RequestApiType_user_bank_card_add,
    RequestApiType_user_withdraw_list,
    RequestApiType_user_withdraw_apply,
    RequestApiType_user_sign,
    RequestApiType_sms_send,
    RequestApiType_group_notice_last,
    RequestApiType_group_notice_publish,
    RequestApiType_user_balance,
    RequestApiType_user_pay_password_set,
    RequestApiType_packet_detail,
    RequestApiType_packet_receive,
    RequestApiType_packet_receive_list,
    RequestApiType_packet_receive_info,
    RequestApiType_app_small_list,
    RequestApiType_user_auth_group_create,
    RequestApiType_packet_send_list,
    RequestApiType_packet_send_info,
    RequestApiType_packet_create_group,
    RequestApiType_packet_create_single,
    RequestApiType_message_list,
    RequestApiType_message_update,
    RequestApiType_message_upload,
    RequestApiType_message_search_list,
    RequestApiType_group_silent_status,
    RequestApiType_group_silent_set,
    RequestApiType_group_role,
    RequestApiType_user_timestamp_update,
    RequestApiType_group_admin_list,
    RequestApiType_group_user_list,
    RequestApiType_group_admin_set,
    RequestApiType_group_info,
    RequestApiType_friend_status,
    RequestApiType_friend_remark_set,
    RequestApiType_friend_delete,
    RequestApiType_friend_apply_delete,
    RequestApiType_friend_apply_list,
    RequestApiType_friend_apply_check,
    RequestApiType_friend_apply,
    RequestApiType_friend_list,
    RequestApiType_user_password_reset,
    RequestApiType_user_search,
    RequestApiType_user_info_update,
    RequestApiType_user_info,
    RequestApiType_login_out,
    RequestApiType_login,
    RequestApiType_user_register,
    RequestApiType_config
} RequestApiType;

@interface ProjectRequestModel : NSObject
@property (nonatomic,assign) RequestApiType apiType;
@property (nonatomic,strong) NSDictionary *parameters;
@property (nonatomic,strong) NSDictionary *headerParameters;
@property (nonatomic,weak) id progress;
@property (nonatomic,assign) BOOL screteState;
@property (nonatomic,assign) BOOL isAsyn;

@end

NS_ASSUME_NONNULL_END
