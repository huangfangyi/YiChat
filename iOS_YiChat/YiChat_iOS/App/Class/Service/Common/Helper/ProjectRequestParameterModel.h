//
//  ProjectRequestParameterModel.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/3.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface ProjectRequestParameterModel : ProjectBaseModel

+ (NSDictionary *)getTokenParamWithToken:(NSString *)token;

+ (NSDictionary *)getthidLoginWithParameters:(NSInteger)type
                                        nick:(NSString *)nick
                                  uniqueCode:(NSString *)uniqueCode
                                      avatar:(NSString *)avatar
                                    deviceId:(NSString *)deviceId openId:(NSString *)openId;

+ (NSDictionary *)sendCertifyParametersWithMobile:(NSString *)mobile;

+ (NSDictionary *)getRegisterParametersWithMobile:(NSString *)mobile
                                             nick:(NSString *)nick
                                         password:(NSString *)password
                                          avrater:(NSString *)avrater
                                         platform:(NSString *)platform;

+ (NSDictionary *)getLoginParamWithMobile:(NSString *)mobile
                                 deviceId:(NSString *)deviceId
                                 password:(NSString *)password
                                 platform:(NSString *)platform;

+ (NSDictionary *)getcertifyLoginParamWithMobile:(NSString *)mobile
                                        deviceId:(NSString *)deviceId
                                        password:(NSString *)password
                                        platform:(NSString *)platform
                                            type:(NSString *)type;

+ (NSDictionary *)searchUserParamWithContent:(NSString *)content;

+ (NSDictionary *)getUserInfoParamWithUserId:(NSString *)userId;

+ (NSDictionary *)getUpdateUserInfoParamWithUserId:(NSString *)userId
                                              nick:(NSString *)nick
                                            gender:(NSString *)gender
                                            avatar:(NSString *)avatar
                                             appId:(NSString *)appId
                                            mobile:(NSString *)mobile
                                          password:(NSString *)password;

+ (NSDictionary *)getResetPasswordParamWithPassword:(NSString *)password
                                             mobile:(NSString *)mobile;


+ (NSDictionary *)getFriendListParamWithUserId:(NSString *)userId
                                        pageNo:(NSString *)pageNo;

+ (NSDictionary *)getAddFriendParamWithReason:(NSString *)reason
                                     friendId:(NSString *)friendId;

+ (NSDictionary *)getFriendShipParamWithFriendId:(NSString *)friendId;

+ (NSDictionary *)setFriendsRemarkNameWithFriendId:(NSString *)friendId remark:(NSString *)remarkName;

/**
 *  审核好友申请
 *  status  1通过 2拒绝
 */
+ (NSDictionary *)getCheckFriendApplyParamWithUserId:(NSString *)userId
                                       fid:(NSString *)fid
                                    status:(NSString *)status;

/**
 *  待审核申请列表
 */
+ (NSDictionary *)getCheckFriendApplyDataListParamWithUserId:(NSString *)userId
                                            pageNo:(NSString *)pageNo;


+ (NSDictionary *)getDeleteFriendParamWithFriendId:(NSString *)friendId;

+ (NSDictionary *)updateMessageWithMessageId:(NSString *)messageId content:(NSString *)content;

+ (NSDictionary *)getGroupMemberListParamWithGroupId:(NSString *)groupId;

+ (NSDictionary *)getGroupMemberListParamWithGroupId:(NSString *)groupId
                                              pageNo:(int)page
                                            pageSize:(int)pageSize;

+ (NSDictionary *)getGroupInfoParamWithGroupId:(NSString *)groupId;

+ (NSDictionary *)setGroupManagerParamWithGroupId:(NSString *)groupId userIds:(NSString *)userIds status:(NSInteger)statetus;

+ (NSDictionary *)setGroupSilenceParamWithGroupId:(NSString *)groupId status:(NSInteger)statetus;

+ (NSDictionary *)getGroupManagerlistParamWithGroupId:(NSString *)groupId;

+ (NSDictionary *)setGroupShutUpParamWithGroupId:(NSString *)groupId;

+ (NSDictionary *)getgroupShutUpStateParamWithGroupId:(NSString *)groupId;
    
+ (NSDictionary *)setgroupMemberShutUpStateParamWithGroupId:(NSInteger)groupId userId:(NSInteger)userId status:(NSInteger)status;

+ (NSDictionary *)getUserGroupRoleWithGroupId:(NSString *)groupId;

/**
 *  发单聊红包
 */
+ (NSDictionary *)getSendSingleRedPacketParametersWithReceiveUserId:(NSString *)receiveUserId
                                                              money:(NSString *)money
                                                            content:(NSString *)content password:(NSString *)password type:(NSString *)type;
/**
 *  发群聊红包
 */
+ (NSDictionary *)getSendGroupRedPacketParametersWithGroupId:(NSString *)groupId
                                                       money:(NSString *)money
                                                     content:(NSString *)content nun:(NSString *)num password:(NSString *)password type:(NSString *)type;
/**
 *  设置支付密码
 */

+ (NSDictionary *)getSetPayPassWord:(NSString *)password;

/**
 *  领取红包
 */
+ (NSDictionary *)getReceiveRedPacketParametersWithPacketId:(NSString *)packetId;

/**
 *  交易记录
 */
+ (NSDictionary *)getBalanceListParametersWithType:(NSString *)type pageSize:(NSString *)pageSize pageNo:(NSString *)pageNo;

/**
 *  发送红包汇总
 */
+ (NSDictionary *)getSendRedPacketInfoParametersWithType:(NSString *)type;

/**
 *  领取红包汇总
 */
+ (NSDictionary *)getReceiveRedPacketInfoParametersWithType:(NSString *)type;

/**
 *  发送红包列表
 */
+ (NSDictionary *)getSendRedPacketListParametersWithType:(NSString *)type pageSize:(NSString *)pageSize pageNo:(NSString *)pageNo;

/**
 *  领取红包列表
 */
+ (NSDictionary *)getReceiveRedPacketListParametersWithType:(NSString *)type pageSize:(NSString *)pageSize pageNo:(NSString *)pageNo;

/**
 *  发送手机验证码
 */
+ (NSDictionary *)getSendSMSParametersWithMobile:(NSString *)mobile;

/**
 *  提现
 */
+ (NSDictionary *)getWithdrawApplyParametersWithMoney:(NSString *)money bankNumber:(NSString *)bankNumber memo:(NSString *)memo;

/**
 *  添加银行卡
 */
+ (NSDictionary *)getAddBankCardParametersWithName:(NSString *)name mobile:(NSString *)mobile idNumber:(NSString *)idNumber bankName:(NSString *)bankName bankNumber :(NSString *)bankNumber;

+ (NSDictionary *)getSearchMessageListParametersWithSearchContent:(NSString *)searchContent groupId:(NSString *)groupId;

+ (NSDictionary *)sendDynamicWithimgs:(NSString *)imgs
                     videos:(NSString *)videos
                    content:(NSString *)content
                   location:(NSString *)location;

+ (NSDictionary *)deleteDynamicWithTrendId:(NSInteger)trendId;

+ (NSDictionary *)getDynamicListWithUserId:(NSInteger)userId
                             pageNo:(NSInteger)pageNo
                           pageSize:(NSInteger)pageSize;

+ (NSDictionary *)getFriendFynamiclistPageNo:(NSInteger)pageNo
                          pageSize:(NSInteger)pageSize;

+ (NSDictionary *)dynamicPraiseWithTrendId:(NSInteger)trendId;

+ (NSDictionary *)dynamicCancelPraiseWithTrendId:(NSInteger)trendId;

//评论回复评论
+ (NSDictionary *)dynamicCommandWithTrendId:(NSInteger)trendId
                          content:(NSString *)content
                        commentId:(NSInteger)commentId;

+ (NSDictionary *)deleteDynamicCommandWithCommentId:(NSInteger)commentId;



+ (NSDictionary *)dynamicCommandListWithTrendId:(NSInteger)trendId
                              pageNo:(NSInteger)pageNo
                            pageSize:(NSInteger)pageSize;

+ (NSDictionary *)getDynamicBackImageWithUserId:(NSInteger)userId;

+ (NSDictionary *)setDynamicBackImageWithUserId:(NSInteger)userId img:(NSString *)imgURL;

+ (NSDictionary *)getDeleteBankCardParametersWithCardId:(NSString *)cardId;

+ (NSDictionary *)getSignParametersWithSignType:(NSString *)signType;

+ (NSDictionary *)setWeChatPayWithMoney:(NSString *)money type:(NSString *)type;

+ (NSDictionary *)setGroupNoticePublishWithTitle:(NSString *)title content:(NSString *)content groupId:(NSString *)groupId;

+ (NSDictionary *)setGroupNoticeListWithGroupId:(NSString *)groupId;

+ (NSDictionary *)checkVersionWithType:(NSString *)type currentVersion:(NSString *)currentVersion;

+ (NSDictionary *)getFriendApplyDeleteWithFId:(NSString *)fid;

+ (NSDictionary *)feedbackParametersWithContent:(NSString *)content;

+ (NSDictionary *)getDeleteGroupNoticeParametersWithNoticeId:(NSString *)noticeId;

+ (NSDictionary *)setWeChatPayWithTradeNo:(NSString *)tradeNo;
@end

NS_ASSUME_NONNULL_END
