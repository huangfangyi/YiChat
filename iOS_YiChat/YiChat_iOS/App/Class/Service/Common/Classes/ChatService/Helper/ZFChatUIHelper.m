//
//  ZFChatUIHelper.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatUIHelper.h"
#import "ZFChatVC.h"
#import "YiChatGroupInfoModel.h"
#import "ZFChatGlobal.h"
#import "ZFChatUIConfigure.h"
#import "YiChatGroupInfoVC.h"
#import "YiChatFriendInfoVC.h"


@implementation ZFChatUIHelper

+ (UIViewController *)getChatVCWithChatId:(NSString *)chatId chatType:(NSString *)chatType{
    return [ZFChatVC initialVCWithChatId:chatId chatType:chatType];
}

+ (UIViewController *)getGroupChatVCWithGroupModel:(YiChatGroupInfoModel *)groupInfoModel{
    if(groupInfoModel && [groupInfoModel isKindOfClass:[YiChatGroupInfoModel class]]){
        return  [ZFChatVC initialVCWithGroupModel:groupInfoModel];
    }
    return nil;
}

+ (id)zfChatUIHelper_initialMapVCWithLocation:(CLLocationCoordinate2D)location address:(NSString *)address description:(NSString *)description{
    ProjectMapVC *map = [ProjectMapVC initialMapVCWithLocation:location address:address description:description];
    return map;
}

+ (id)zfChatUIHelper_initialSendMapVCInvocation:(ProjectNavSendLocationHandle)invocation{
    ProjectMapVC *map = [ProjectMapVC initialSendMapVC];
    map.sendLocationHandle = invocation;
    return map;
}

+ (UIViewController *)getGroupInfoVCWithGroupId:(NSString *)groupId{
    YiChatGroupInfoVC *info = [YiChatGroupInfoVC initialVC];
    info.groupId = groupId;
    return info;
}

+ (UIViewController *)getGroupInfoVCWithGroupModel:(YiChatGroupInfoModel *)groupModel{
    YiChatGroupInfoVC *info = [YiChatGroupInfoVC initialVC];
    info.groupInfoModel = groupModel;
    return info;
}

+ (UIViewController *)getUserInfoVCWithUserId:(NSString *)userId{
    YiChatFriendInfoVC *info = [YiChatFriendInfoVC initialVC];
    info.userId = userId;
    return info;
}

+ (UIViewController *)getUserInfoVCWithUserModel:(YiChatUserModel *)userModel{
    YiChatFriendInfoVC *info = [YiChatFriendInfoVC initialVC];
    info.model = userModel;
    return info;
}

+ (NSString *)zfChatUIHelperConversationLastMessageTimeWithDate:(NSDate *)date{
    NSDateFormatter *formatter = [[NSDateFormatter alloc]init];
    [formatter setDateStyle:NSDateFormatterMediumStyle];
    [formatter setTimeStyle:NSDateFormatterShortStyle];
    [formatter setDateFormat:@"MM-dd HH:mm:ss"];
    
    NSString*confromTimespStr = [formatter stringFromDate:date];
    
    NSInteger unixTime= [[NSDate date] timeIntervalSince1970] - [date timeIntervalSince1970];
    
    if(unixTime < (24 * 3600))
    {
        if(unixTime < 0){
            NSInteger day = abs(unixTime / 3600 / 24);
            if(day == 0){
                day = 1;
            }
            confromTimespStr = @"刚刚";
        }
        else{
            [formatter setDateFormat:@"HH:mm"];
            confromTimespStr = [NSString stringWithFormat:@"%@%@",@"昨天",[formatter stringFromDate:date]];
            
            if(unixTime < 3600 && unixTime > 0){
                confromTimespStr =@"1个小时前";
                
                if(unixTime < 60 * 60 & unixTime >= 60){
                    confromTimespStr =[NSString stringWithFormat:@"%ld%@",unixTime / 60,@"分钟前"];
                }
                else{
                    confromTimespStr =[NSString stringWithFormat:@"%ld%@",unixTime,@"秒前"];
                }
                
            }
            else if(unixTime >= 3600 && unixTime < 24 * 3600){
                confromTimespStr =[NSString stringWithFormat:@"%ld%@",unixTime / 3600,@"小时前"];
            }
            else{
                confromTimespStr = @"刚刚";
            }
            
        }
        
    }
    else{
        confromTimespStr = [NSString stringWithFormat:@"%ld天前",unixTime / (24 * 3600)];
    }
    
    return confromTimespStr;
}

+ (NSAttributedString *)tranlateStringToAttributedString:(NSString *)string font:(UIFont *)font{
    if(string && [string isKindOfClass:[NSString class]] && font && [font isKindOfClass:[UIFont class]]){
        return  [[ZFChatUIConfigure initialChatUIConfigure] tranlateStringToAttributedString:string font:font];
    }
    return nil;
}
@end

