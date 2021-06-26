//
//  ProjectRequestParameterModel.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/3.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectRequestParameterModel.h"

@interface ProjectRequestParameterModel ()

@end

static  NSString * const mobileKey = @"mobile";
static  NSString * const nickKey = @"nick";
static  NSString * const passwordKey = @"password";
static  NSString * const avaterKey = @"avatar";
static  NSString * const platformKey = @"platform";
static  NSString * const deviceIdKey = @"deviceId";
static  NSString * const contentKey = @"content";
static  NSString * const userIdKey = @"userId";
static  NSString *const remarkKey = @"remark";
static  NSString * const userIdsKey = @"userIds";
static  NSString * const genderKey = @"gender";
static  NSString * const appIdKey = @"appId";
static  NSString * const tokenKey = @"zf-token";
static  NSString * const ucodeKey = @"ucode";
static  NSString * const srcPasswordKey = @"srcPassword";
static  NSString * const friendIdKey = @"friendId";
static  NSString * const pageNoKey = @"pageNo";
static  NSString * const pageSizeKey = @"pageSize";
static  NSString * const fidKey = @"fid";
static  NSString * const statusKey = @"status";
static  NSString * const groupIdKey = @"groupId";
static  NSString * const messageIdKey = @"messageId";

static  NSString * const numKey = @"num";
static  NSString * const moneyKey = @"money";
static  NSString * const receiveUserIdKey = @"receiveUserId";
static  NSString * const packetIdKey = @"packetId";
static  NSString * const typeKey = @"type";
static  NSString * const bankNumberKey = @"bankNumber";
static  NSString * const memoKey = @"memo";

static  NSString * const nameKey = @"name";
static  NSString * const idNumberKey = @"idNumber";
static  NSString * const bankNameKey = @"bankName";
static  NSString * const uniqueCodeKey = @"uniqueCode";
static  NSString * const openIdKey = @"openId";
static  NSString * const groupIdsKey = @"groupIds";
static  NSString * const searchContentKey = @"searchContent";
static  NSString * const reasonKey = @"reason";
static  NSString * const cardIdKey = @"cardId";
static  NSString * const signTypeKey = @"signType";

static  NSString * const imgsKey = @"imgs";
static  NSString * const imgKey = @"img";
static  NSString * const videosKey = @"videos";
static  NSString * const locationKey = @"location";
static  NSString * const trendIdKey = @"trendId";
static  NSString * const commentIdKey = @"commentId";
static  NSString * const titleKey = @"title";
static  NSString * const currentVersionKey = @"currentVersion";
static  NSString * const noticeIdKey = @"noticeId";
static  NSString * const tradeNoKey = @"tradeNo";
@implementation ProjectRequestParameterModel

+ (NSDictionary *)getTokenParamWithToken:(NSString *)token{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:token key:tokenKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)sendCertifyParametersWithMobile:(NSString *)mobile{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:mobile key:mobileKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getthidLoginWithParameters:(NSInteger)type
                                        nick:(NSString *)nick
                                  uniqueCode:(NSString *)uniqueCode
                                      avatar:(NSString *)avatar
                                    deviceId:(NSString *)deviceId openId:(NSString *)openId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:[NSNumber numberWithInteger:type] key:typeKey dic:dic];
    [self addEntityWithObj:nick key:nickKey dic:dic];
    [self addEntityWithObj:uniqueCode key:uniqueCodeKey dic:dic];
    [self addEntityWithObj:avatar key:avaterKey dic:dic];
    [self addEntityWithObj:deviceId key:deviceIdKey dic:dic];
    [self addEntityWithObj:openId key:openIdKey dic:dic];
    return dic;
}

+ (NSDictionary *)getRegisterParametersWithMobile:(NSString *)mobile
                                             nick:(NSString *)nick
                                         password:(NSString *)password
                                          avrater:(NSString *)avrater
                                         platform:(NSString *)platform{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:mobile key:mobileKey dic:dic];
    [self addEntityWithObj:nick key:nickKey dic:dic];
    [self addEntityWithObj:password key:passwordKey dic:dic];
    [self addEntityWithObj:avrater key:avaterKey dic:dic];
    [self addEntityWithObj:platform key:platformKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getLoginParamWithMobile:(NSString *)mobile
                                 deviceId:(NSString *)deviceId
                                 password:(NSString *)password
                                 platform:(NSString *)platform{
    
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:mobile key:mobileKey dic:dic];
    [self addEntityWithObj:password key:passwordKey dic:dic];
    [self addEntityWithObj:deviceId key:deviceIdKey dic:dic];
    [self addEntityWithObj:platform key:platformKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getcertifyLoginParamWithMobile:(NSString *)mobile
                                 deviceId:(NSString *)deviceId
                                 password:(NSString *)password
                                        platform:(NSString *)platform
                                            type:(NSString *)type{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:mobile key:mobileKey dic:dic];
    [self addEntityWithObj:password key:passwordKey dic:dic];
    [self addEntityWithObj:deviceId key:deviceIdKey dic:dic];
    [self addEntityWithObj:platform key:platformKey dic:dic];
    [self addEntityWithObj:type key:typeKey dic:dic];
    return dic;
}

+ (NSDictionary *)searchUserParamWithContent:(NSString *)content{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:content key:contentKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getUserInfoParamWithUserId:(NSString *)userId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:userId key:userIdKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getGroupInfoParamWithGroupId:(NSString *)groupId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:groupId key:groupIdKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getFriendShipParamWithFriendId:(NSString *)friendId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:friendId key:friendIdKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)setFriendsRemarkNameWithFriendId:(NSString *)friendId remark:(NSString *)remarkName{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:friendId key:friendIdKey dic:dic];
    [self addEntityWithObj:remarkName key:remarkKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getUpdateUserInfoParamWithUserId:(NSString *)userId
                                              nick:(NSString *)nick
                                            gender:(NSString *)gender
                                            avatar:(NSString *)avatar
                                             appId:(NSString *)appId
                                            mobile:(NSString *)mobile
                                          password:(NSString *)password{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:nick key:nickKey dic:dic];
    [self addEntityWithObj:gender key:genderKey dic:dic];
    [self addEntityWithObj:avatar key:avaterKey dic:dic];
    [self addEntityWithObj:appId key:appIdKey dic:dic];
    [self addEntityWithObj:mobile key:mobileKey dic:dic];
    [self addEntityWithObj:password key:passwordKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getResetPasswordParamWithPassword:(NSString *)password
                                             mobile:(NSString *)mobile{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:mobile key:mobileKey dic:dic];
    [self addEntityWithObj:password key:passwordKey dic:dic];
    return dic;
}


+ (NSDictionary *)getFriendListParamWithUserId:(NSString *)userId
                                        pageNo:(NSString *)pageNo{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:userId key:userIdKey dic:dic];
    [self addEntityWithObj:pageNo key:pageNoKey dic:dic];
    [self addEntityWithObj:@"10" key:pageSizeKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getFriendApplyDeleteWithFId:(NSString *)fid{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:fid key:fidKey dic:dic];
    return dic;
}

+ (NSDictionary *)getAddFriendParamWithReason:(NSString *)reason
                           friendId:(NSString *)friendId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:reason key:reasonKey dic:dic];
    [self addEntityWithObj:friendId key:friendIdKey dic:dic];
    
    return dic;
}

/**
 *  审核好友申请
 *  status  1通过 2拒绝
 */
+ (NSDictionary *)getCheckFriendApplyParamWithUserId:(NSString *)userId
                                       fid:(NSString *)fid
                                    status:(NSString *)status{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    //[self addEntityWithObj:userId key:userIdKey dic:dic];
    [self addEntityWithObj:fid key:fidKey dic:dic];
    [self addEntityWithObj:status key:statusKey dic:dic];
    
    return dic;
}

/**
 *  待审核申请列表
 */
+ (NSDictionary *)getCheckFriendApplyDataListParamWithUserId:(NSString *)userId
                                            pageNo:(NSString *)pageNo{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:userId key:userIdKey dic:dic];
    [self addEntityWithObj:pageNo key:pageNoKey dic:dic];
    [self addEntityWithObj:@"10" key:pageSizeKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getDeleteFriendParamWithFriendId:(NSString *)friendId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:friendId key:friendIdKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)updateMessageWithMessageId:(NSString *)messageId content:(NSString *)content{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:messageId key:messageIdKey dic:dic];
    [self addEntityWithObj:content key:contentKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getGroupMemberListParamWithGroupId:(NSString *)groupId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:groupId key:groupIdKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getGroupMemberListParamWithGroupId:(NSString *)groupId
                                              pageNo:(int)page
                                            pageSize:(int)pageSize{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:[NSNumber numberWithInt:page] key:pageNoKey dic:dic];
    [self addEntityWithObj:[NSNumber numberWithInt:pageSize] key:pageSizeKey dic:dic];
    [self addEntityWithObj:groupId key:groupIdKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)setGroupManagerParamWithGroupId:(NSString *)groupId userIds:(NSString *)userIds status:(NSInteger)statetus{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:groupId key:groupIdKey dic:dic];
    [self addEntityWithObj:userIds key:userIdsKey dic:dic];
    [self addEntityWithObj:[NSNumber numberWithInt:statetus] key:statusKey dic:dic];

    return dic;
}

+ (NSDictionary *)setGroupSilenceParamWithGroupId:(NSString *)groupId status:(NSInteger)statetus{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:groupId key:groupIdKey dic:dic];
    [self addEntityWithObj:[NSNumber numberWithInt:statetus] key:statusKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getGroupManagerlistParamWithGroupId:(NSString *)groupId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:groupId key:groupIdKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)setGroupShutUpParamWithGroupId:(NSString *)groupId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:groupId key:groupIdKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getgroupShutUpStateParamWithGroupId:(NSString *)groupId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:groupId key:groupIdKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)setgroupMemberShutUpStateParamWithGroupId:(NSInteger)groupId userId:(NSInteger)userId status:(NSInteger)status{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:[NSNumber numberWithInteger:groupId] key:groupIdKey dic:dic];
    [self addEntityWithObj:[NSNumber numberWithInteger:userId] key:userIdKey dic:dic];
    [self addEntityWithObj:[NSNumber numberWithInteger:status] key:statusKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getUserGroupRoleWithGroupId:(NSString *)groupId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:groupId key:groupIdKey dic:dic];
    
    return dic;
}

+ (NSDictionary *)getSendSingleRedPacketParametersWithReceiveUserId:(NSString *)receiveUserId
                                                              money:(NSString *)money
                                                            content:(NSString *)content password:(NSString *)password type:(NSString *)type{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:receiveUserId key:receiveUserIdKey dic:dic];
    [self addEntityWithObj:money key:moneyKey dic:dic];
    [self addEntityWithObj:content key:contentKey dic:dic];//passwordKey
    [self addEntityWithObj:password key:passwordKey dic:dic];
    [self addEntityWithObj:type key:typeKey dic:dic];
    return dic;
}

+ (NSDictionary *)getSendGroupRedPacketParametersWithGroupId:(NSString *)groupId
                                                       money:(NSString *)money
                                                     content:(NSString *)content nun:(NSString *)num password:(NSString *)password type:(NSString *)type{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:groupId key:groupIdKey dic:dic];
    [self addEntityWithObj:money key:moneyKey dic:dic];
    [self addEntityWithObj:content key:contentKey dic:dic];
    [self addEntityWithObj:num key:numKey dic:dic];
    [self addEntityWithObj:password key:passwordKey dic:dic];
    [self addEntityWithObj:type key:typeKey dic:dic];
    return dic;
}

+ (NSDictionary *)getSetPayPassWord:(NSString *)password{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:password key:passwordKey dic:dic];
    return dic;
}

+ (NSDictionary *)getReceiveRedPacketParametersWithPacketId:(NSString *)packetId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:packetId key:packetIdKey dic:dic];
    return dic;
}

+ (NSDictionary *)getSendRedPacketInfoParametersWithType:(NSString *)type{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:type key:typeKey dic:dic];
    return dic;
}

+ (NSDictionary *)getReceiveRedPacketInfoParametersWithType:(NSString *)type{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:type key:typeKey dic:dic];
    return dic;
}

+ (NSDictionary *)getSendRedPacketListParametersWithType:(NSString *)type pageSize:(NSString *)pageSize pageNo:(NSString *)pageNo{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:type key:typeKey dic:dic];
    [self addEntityWithObj:pageNo key:pageNoKey dic:dic];
    [self addEntityWithObj:pageSize key:pageSizeKey dic:dic];
    return dic;
}

+ (NSDictionary *)getReceiveRedPacketListParametersWithType:(NSString *)type pageSize:(NSString *)pageSize pageNo:(NSString *)pageNo{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:type key:typeKey dic:dic];
    [self addEntityWithObj:pageNo key:pageNoKey dic:dic];
    [self addEntityWithObj:pageSize key:pageSizeKey dic:dic];
    return dic;
}

+ (NSDictionary *)getBalanceListParametersWithType:(NSString *)type pageSize:(NSString *)pageSize pageNo:(NSString *)pageNo{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:type key:typeKey dic:dic];
    [self addEntityWithObj:pageNo key:pageNoKey dic:dic];
    [self addEntityWithObj:pageSize key:pageSizeKey dic:dic];
    return dic;
}

+ (NSDictionary *)getWithdrawApplyParametersWithMoney:(NSString *)money bankNumber:(NSString *)bankNumber memo:(NSString *)memo{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:money key:moneyKey dic:dic];
    [self addEntityWithObj:bankNumber key:bankNumberKey dic:dic];
    [self addEntityWithObj:memo key:memoKey dic:dic];
    return dic;
}

+ (NSDictionary *)getSendSMSParametersWithMobile:(NSString *)mobile{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:mobile key:mobileKey dic:dic];
    return dic;
}

+ (NSDictionary *)getAddBankCardParametersWithName:(NSString *)name mobile:(NSString *)mobile idNumber:(NSString *)idNumber bankName:(NSString *)bankName bankNumber :(NSString *)bankNumber{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:name key:nameKey dic:dic];
    [self addEntityWithObj:mobile key:mobileKey dic:dic];
    [self addEntityWithObj:idNumber key:idNumberKey dic:dic];
    [self addEntityWithObj:bankName key:bankNameKey dic:dic];
    [self addEntityWithObj:bankNumber key:bankNumberKey dic:dic];
    return dic;
}

+ (NSDictionary *)feedbackParametersWithContent:(NSString *)content{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:content key:contentKey dic:dic];
    return dic;
}

+ (NSDictionary *)getSignParametersWithSignType:(NSString *)signType{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:signType key:signTypeKey dic:dic];
    return dic;
}

+ (NSDictionary *)getDeleteBankCardParametersWithCardId:(NSString *)cardId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:cardId key:cardIdKey dic:dic];
    return dic;
}

+ (NSDictionary *)getDeleteGroupNoticeParametersWithNoticeId:(NSString *)noticeId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:noticeId key:noticeIdKey dic:dic];
    return dic;
}

+ (NSDictionary *)getSearchMessageListParametersWithSearchContent:(NSString *)searchContent groupId:(NSString *)groupId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:searchContent key:searchContentKey dic:dic];
    [self addEntityWithObj:groupId key:groupIdsKey dic:dic];
    return dic;
}

+ (NSDictionary *)sendDynamicWithimgs:(NSString *)imgs
                     videos:(NSString *)videos
                    content:(NSString *)content
                   location:(NSString *)location{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:imgs key:imgsKey dic:dic];
    [self addEntityWithObj:videos key:videosKey dic:dic];
    [self addEntityWithObj:content key:contentKey dic:dic];
    [self addEntityWithObj:location key:locationKey dic:dic];
    return dic;
}

+ (NSDictionary *)deleteDynamicWithTrendId:(NSInteger)trendId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:[NSNumber numberWithInteger:trendId] key:trendIdKey dic:dic];
    return dic;
}

+ (NSDictionary *)getDynamicListWithUserId:(NSInteger)userId
                          pageNo:(NSInteger)pageNo
                        pageSize:(NSInteger)pageSize{
    
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:[NSNumber numberWithInteger:userId] key:userIdKey dic:dic];
    [self addEntityWithObj:[NSNumber numberWithInteger:pageNo] key:pageNoKey dic:dic];
    [self addEntityWithObj:[NSNumber numberWithInteger:pageSize] key:pageSizeKey dic:dic];
    return dic;
}

+ (NSDictionary *)getFriendFynamiclistPageNo:(NSInteger)pageNo
                          pageSize:(NSInteger)pageSize{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:[NSNumber numberWithInteger:pageNo] key:pageNoKey dic:dic];
    [self addEntityWithObj:[NSNumber numberWithInteger:pageSize] key:pageSizeKey dic:dic];
    return dic;
}


+ (NSDictionary *)dynamicPraiseWithTrendId:(NSInteger)trendId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:[NSNumber numberWithInteger:trendId] key:trendIdKey dic:dic];
    return dic;
}


+ (NSDictionary *)dynamicCancelPraiseWithTrendId:(NSInteger)trendId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:[NSNumber numberWithInteger:trendId] key:trendIdKey dic:dic];
    return dic;
}


//评论回复评论
+ (NSDictionary *)dynamicCommandWithTrendId:(NSInteger)trendId
                          content:(NSString *)content
                        commentId:(NSInteger)commentId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    
    [self addEntityWithObj:[NSNumber numberWithInteger:trendId] key:trendIdKey dic:dic];
    if(commentId != -1){
        [self addEntityWithObj:[NSNumber numberWithInteger:commentId] key:commentIdKey dic:dic];
    }
    [self addEntityWithObj:content key:contentKey dic:dic];
    return dic;
}

+ (NSDictionary *)deleteDynamicCommandWithCommentId:(NSInteger)commentId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:[NSNumber numberWithInteger:commentId] key:commentIdKey dic:dic];
    return dic;
}

+ (NSDictionary *)dynamicCommandListWithTrendId:(NSInteger)trendId
                               pageNo:(NSInteger)pageNo
                             pageSize:(NSInteger)pageSize{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:[NSNumber numberWithInteger:pageNo] key:pageNoKey dic:dic];
    [self addEntityWithObj:[NSNumber numberWithInteger:pageSize] key:pageSizeKey dic:dic];
    [self addEntityWithObj:[NSNumber numberWithInteger:trendId] key:trendIdKey dic:dic];
    return dic;
}


+ (NSDictionary *)getDynamicBackImageWithUserId:(NSInteger)userId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:[NSNumber numberWithInteger:userId] key:userIdKey dic:dic];
    return dic;
}

+ (NSDictionary *)setDynamicBackImageWithUserId:(NSInteger)userId img:(NSString *)imgURL{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:[NSNumber numberWithInteger:userId] key:userIdKey dic:dic];
    [self addEntityWithObj:imgURL key:imgKey dic:dic];
    return dic;
}

+ (NSDictionary *)setWeChatPayWithMoney:(NSString *)money type:(NSString *)type{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:money key:moneyKey dic:dic];
    [self addEntityWithObj:type key:typeKey dic:dic];
    return dic;
}

+ (NSDictionary *)setWeChatPayWithTradeNo:(NSString *)tradeNo{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:tradeNo key:tradeNoKey dic:dic];
//    [self addEntityWithObj:type key:typeKey dic:dic];
    return dic;
}

+ (NSDictionary *)setGroupNoticePublishWithTitle:(NSString *)title content:(NSString *)content groupId:(NSString *)groupId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:title key:titleKey dic:dic];
    [self addEntityWithObj:content key:contentKey dic:dic];
    [self addEntityWithObj:groupId key:groupIdKey dic:dic];
    return dic;
}

+ (NSDictionary *)checkVersionWithType:(NSString *)type currentVersion:(NSString *)currentVersion{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:type key:typeKey dic:dic];
    [self addEntityWithObj:currentVersion key:currentVersionKey dic:dic];
    return dic;
}

+ (NSDictionary *)setGroupNoticeListWithGroupId:(NSString *)groupId{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    [self addEntityWithObj:groupId key:groupIdKey dic:dic];
    return dic;
}

#pragma mark

+ (NSDictionary *)addEntityWithObj:(id)obj key:(NSString *)key dic:(NSMutableDictionary *)dic{
    if(obj && key){
        [dic setObject:obj forKey:key];
    }
    return dic;
}
@end
