/*! 
@header  HTGroupManager

@abstract 

@author  Created by 非夜 on 16/12/19.

@version 1.0 16/12/19 Creation(HTMessage Born)

  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.

*/

/**
 
 创建群：             	群普通消息  message.ext = {"action":"2000","groupName":"群名称","groupDescription":"群描述","groupAvatar":"群头像"}  					message.body.content ="\"某某群\"创建成功"
 更新群名称：			群普通消息  message.ext = {"action":"2001","groupName":"群名称","uid":"用户id","nickName":"用户昵称"}  									UI展示为：群主自己 -> "你修改群名称为"新的群名称""  群成员 -> ""某人"修改群名称为"新的群名称""
 更新群描述：			群普通消息  message.ext = {"action":"2002","groupDescription":"群描述","uid":"用户id","nickName":"用户昵称"}
 更新群头像：			群普通消息  message.ext = {"action":"2005","groupAvatar":"群头像","uid":"用户id","nickName":"用户昵称"}
 往群里加人：          群普通消息  message.ext = {"action":"2003","groupName":"群名称","groupDescription":"群描述","groupAvatar":"群头像","members":[{"uid":"第一个用户id","nickName":"第一个用户昵称"},{"uid":"第二个用户id","nickName":"第二个用户昵称"}]}
 UI展示为：被加的人:   "你加入了群聊"群名称  其他成员: ""某某"加入了群聊"
 从群里移除群成员：		群普通消息  message.ext = {"action":"2004","uid":"被移除的群成员id","nickName":"被移除的群成员昵称"}
 个人透传    message.body = {"action":"2004":"data":"当前群id"}
 
 

 */

#import "HTGroupManager.h"
#import "HTDBManager.h"
#import "HTClient.h"
#import "GCDMulticastDelegate.h"
#import "QSMessageSDKHelper.h"
#import "NSObject+QSModel.h"
#import "QSNetworkTool.h"
#import "QSAESManager.h"
#import "HTMessageDefines.h"
#import "QSTools.h"
#import "ZFChatRequestHelper.h"

@interface HTGroupManager()

@property (nonatomic,strong)GCDMulticastDelegate <HTGroupDelegate> *groupManagerDelegate;

@end


@implementation HTGroupManager

- (id)init {
    if (self = [super init]) {
        self.groupManagerDelegate = (GCDMulticastDelegate <HTGroupDelegate> *)[[GCDMulticastDelegate alloc] init];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(groupCreatedByMessage:) name:HT_NEW_GROUP_CREATED object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateGroupByMessage:) name:HT_UPDATE_GROUP_INFO object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(deleteGroupByMessage:) name:HT_DELETE_GROUP_INFO object:nil];
        return self;
    }
    return nil;
}

- (void)addDelegate:(id)aDelegate delegateQueue:(dispatch_queue_t)delegateQueue {
    if (delegateQueue == nil || delegateQueue == NULL) {
        [self.groupManagerDelegate addDelegate:aDelegate delegateQueue:dispatch_get_main_queue()];
    }else{
        [self.groupManagerDelegate addDelegate:aDelegate delegateQueue:delegateQueue];
    }
}

- (void)removeDelegate:(id)aDelegate {
    [self.groupManagerDelegate removeDelegate:aDelegate];
}

- (void)initGroups {
    
    // 先初始化本地的群，等服务器的回来后再重新校验群
    [[HTDBManager sharedInstance] fetchAllGroupsFromDBCompelation:^(NSArray *result) {
        self.groups = result;
//        for (HTGroup *group in result) {
//            [self requestForGroupMessageWithGroupId:group.groupId];
//        }
    }];
    [self requestGroupList];
}

- (void)requestForGroupMessageWithGroupId:(NSString *)gid{
    NSDictionary * requestDic = @{@"gid":gid};
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:@"http://47.97.35.220/api/groupChat"]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[QSNetworkTool serializeParameters:requestDic] dataUsingEncoding:NSUTF8StringEncoding]];
    QSNetworkTool * netWork = [QSNetworkTool new];
    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *responseDicI, NSData *data) {
        if ([responseDicI[@"code"] integerValue] == 1) {
            NSArray *messageDataArray = responseDicI[@"data"];
            for (NSDictionary *tempMsgDic in messageDataArray) {
                NSLog(@"tempMsgDic >>>>>>  %@",tempMsgDic);
                HTMessage *message = [HTMessage modelWithJSON:tempMsgDic[@"message"]];
                NSLog(@"msgContent >>>>>>  %@",message.body.content);
                [[HTDBManager sharedInstance] insertOneNormalMessage:message];
            }
        }
        
        
    } failure:^(NSError *error) {
        
    }];
}


- (void)requestGroupList {
    [self getSelfGroups:^(NSArray *aGroups) {
        [self formatGroups:aGroups];
    } failure:^(NSError *error) {}];
}

- (void)formatGroups:(NSArray *)aGroups {
    NSMutableArray * groups = @[].mutableCopy;
    for (NSString * group in aGroups) {
        HTGroup * model = [HTGroup modelWithJSON:group];
        [[HTDBManager sharedInstance] insertOrUpdateOneGroup:model];
        [groups addObject:model];
    }
    self.groups = groups.copy;
}


- (void)createNewGroup:(HTGroup *)group isSender:(BOOL)isSender {
    // updata DB
    [[HTDBManager sharedInstance] insertOrUpdateOneGroup:group];
    // updata Cache
    NSMutableArray * newGroups = @[].mutableCopy;
    [newGroups addObjectsFromArray:self.groups];
    [newGroups addObject:group];
    self.groups = newGroups.copy;
    // send group message to all group memebers
    if (isSender) {
        [self sendCreateGroupMessage:group];
    }
    // callback delegate
    if (self.groupManagerDelegate && [self.groupManagerDelegate hasDelegateThatRespondsToSelector:@selector(didGroupListUpdatad)]) {
        [self.groupManagerDelegate didGroupListUpdatad];
    }
}

- (void)updateGroupNameWithGroup:(HTGroup *)group withNickName:(NSString *)nickName  isSender:(BOOL)isSender {
    // update DB
    [[HTDBManager sharedInstance] insertOrUpdateOneGroup:group];
    // update cache
    [self updateDBAndCacheGroups:group];
    // send group message to all group members
    if (isSender) {
        [self sendUpdateGroupNameMessage:group  withNickName:nickName ];
    }
    // callback delegate
    if (self.groupManagerDelegate && [self.groupManagerDelegate hasDelegateThatRespondsToSelector:@selector(groupInfoChanged:)]) {
        [self.groupManagerDelegate groupInfoChanged:group];
    }
}

- (void)updateGroupDescWithGroup:(HTGroup *)group withNickName:(NSString *)nickName  isSender:(BOOL)isSender {
    // update DB
    [[HTDBManager sharedInstance] insertOrUpdateOneGroup:group];
    // update Chche
    [self updateDBAndCacheGroups:group];
    // send goup message to all group members
    if (isSender) {
        [self sendUpdateGroupDescMessage:group  withNickName:nickName ];
    }
    // callback delegate
    if (self.groupManagerDelegate && [self.groupManagerDelegate hasDelegateThatRespondsToSelector:@selector(groupInfoChanged:)]) {
        [self.groupManagerDelegate groupInfoChanged:group];
    }
}

- (void)updateGroupAvatarWithGroup:(HTGroup *)group withNickName:(NSString *)nickName  isSender:(BOOL)isSender {
    // update DB
    [[HTDBManager sharedInstance] insertOrUpdateOneGroup:group];
    // update Cache
    [self updateDBAndCacheGroups:group];
    // send goup message to all group members
    if (isSender) {
        [self sendUpdateGroupAvatarMessage:group withNickName:nickName ];
    }
    // callback delegate
    if (self.groupManagerDelegate && [self.groupManagerDelegate hasDelegateThatRespondsToSelector:@selector(groupInfoChanged:)]) {
        [self.groupManagerDelegate groupInfoChanged:group];
    }
}

- (void)deleteGroupByGroupId:(NSString *)groupId isSender:(BOOL)isSender{
    // update DB
    [[HTDBManager sharedInstance] deleteOneGroup:groupId];
    // update Cache 
    NSMutableArray * newGroups = @[].mutableCopy;
    [newGroups addObjectsFromArray:self.groups];
    HTGroup * newModel = nil;
    for (HTGroup * tGroup in newGroups) {
        if ([tGroup.groupId isEqualToString:groupId]) {
            newModel = tGroup;
            break;
        }
    }
    if (newModel) {
        [newGroups removeObject:newModel];
    }
    self.groups = newGroups.copy;
    if (self.groupManagerDelegate && [self.groupManagerDelegate hasDelegateThatRespondsToSelector:@selector(didGroupListUpdatad)]) {
        [self.groupManagerDelegate didGroupListUpdatad];
    }
}

- (HTGroup *)groupByGroupId:(NSString *)groupId {
    NSMutableArray * newGroups = @[].mutableCopy;
    [newGroups addObjectsFromArray:self.groups];
    HTGroup * newModel = nil;
    for (HTGroup * tGroup in newGroups) {
        if ([tGroup.groupId isEqualToString:groupId]) {
            newModel = tGroup;
            break;
        }
    }
    if (newModel) {
        return newModel;
    }
    return nil;
}

- (void)updateDBAndCacheGroups:(HTGroup *)group{
    NSMutableArray * newGroups = @[].mutableCopy;
    [newGroups addObjectsFromArray:self.groups];
    HTGroup * newModel = nil;
    for (HTGroup * tGroup in newGroups) {
        if ([tGroup.groupId isEqualToString:group.groupId]) {
            newModel = tGroup;
            break;
        }
    }
    if (newModel) {
        [newGroups replaceObjectAtIndex:[self.groups indexOfObject:newModel] withObject:group];
    }
    self.groups = newGroups.copy;
}

- (void)sendCreateGroupMessage:(HTGroup *)group {
    HTMessage * message = [QSMessageSDKHelper sendTextMessage:[NSString stringWithFormat:@"\"%@\"创建成功",group.groupName] to:group.groupId messageType:@"2" messageExt:@{@"action":@"2000",@"groupName":group.groupName,@"groupDescription":group.groupDescription ? group.groupDescription : @"",@"groupAvatar":group.groupAvatar ? group.groupAvatar : @""}];
    
    [[HTClient sharedInstance] sendMessage:message completion:^(HTMessage *message, NSError *error) {
        [self uploadMessage:message];
    }];
    
}

-(void)uploadMessage:(HTMessage *)message {
//    NSString *string = [message modelToJSONString];
//    //    string = [string stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
//    NSDictionary *dic = [self dictionaryWithJsonString:string];
//    NSDictionary *requestDic = @{@"type":@2000,@"data":dic};
//    NSString *requestString = [self convertToJsonData:requestDic];
//    NSMutableDictionary *params = @{@"fromId": message.from,
//                                    @"toId":message.to,
//                                    @"chattype":message.chatType,
//                                    @"message":[QSTools base64StringFromText:requestString],
//                                    @"timeStamp":[NSNumber numberWithInteger:message.timestamp]
//                                    }.mutableCopy;
//    if ([message.chatType isEqualToString: @"2"]){
//        [params setObject:message.msgId forKey:@"mid"];
//    }
//    NSLog(@"SDK上传的parameters：%@", params);
//
//    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://%@/api/uploadMessage",BUSINESS_HOST]]];
//    [request setHTTPMethod:@"POST"];
//    [request setHTTPBody:[[QSNetworkTool serializeParameters:params] dataUsingEncoding:NSUTF8StringEncoding]];
//    QSNetworkTool * netWork = [QSNetworkTool new];
//    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *responseDicI, NSData *data) {
//        NSLog(@"SDK上传消息uploadMessage: %@", responseDicI);
//
//    } failure:^(NSError *error) {
//        NSLog(@"SDK上传消息失败:%@",error.localizedDescription);
//    }];
    
    [ZFChatRequestHelper zfRequest_uploadMessage:message chatType:message.chatType];

}

//json格式字符串转字典：

- (NSDictionary *)dictionaryWithJsonString:(NSString *)jsonString {
    
    if (jsonString == nil) {
        
        return nil;
        
    }
    
    NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
    
    NSError *err;
    
    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData
                         
                                                        options:NSJSONReadingMutableContainers
                         
                                                          error:&err];
    
    if(err) {
        
        NSLog(@"json解析失败：%@",err);
        
        return nil;
        
    }
    
    return dic;
    
}

//去空格 替换加号
-(NSString *)convertToJsonData:(NSDictionary *)dict
{
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:&error];
    NSString *jsonString;
    if (!jsonData) {
        NSLog(@"%@",error);
    }else{
        jsonString = [[NSString alloc]initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    NSMutableString *mutStr = [NSMutableString stringWithString:jsonString];
    NSRange range = {0,jsonString.length};
    //去掉字符串中的空格
    [mutStr replaceOccurrencesOfString:@" " withString:@"" options:NSLiteralSearch range:range];
    NSRange range1 = {0,mutStr.length};
    //去掉字符串中的换行符
    [mutStr replaceOccurrencesOfString:@"\n" withString:@"" options:NSLiteralSearch range:range1];
    return mutStr;
    
}

- (void)sendUpdateGroupNameMessage:(HTGroup *)group  withNickName:(NSString *)nickName{
    HTMessage * message = [QSMessageSDKHelper sendTextMessage:[NSString stringWithFormat:@"管理员修改了群资料"] to:group.groupId messageType:@"2" messageExt:@{@"action":@"2001",@"groupName":group.groupName,@"groupDescription":group.groupDescription ? group.groupDescription : @"",@"groupAvatar":group.groupAvatar ? group.groupAvatar : @"",@"uid":[HTClient sharedInstance].currentUsername,@"nickName":nickName}];
    
    [[HTClient sharedInstance] sendMessage:message completion:^(HTMessage *message, NSError *error) {
        [self uploadMessage:message];
    }];
}

- (void)sendUpdateGroupDescMessage:(HTGroup *)group  withNickName:(NSString *)nickName {
    HTMessage * message = [QSMessageSDKHelper sendTextMessage:[NSString stringWithFormat:@"管理员修改了群资料"] to:group.groupId messageType:@"2" messageExt:@{@"action":@"2001",@"groupName":group.groupName,@"groupDescription":group.groupDescription ? group.groupDescription : @"",@"groupAvatar":group.groupAvatar ? group.groupAvatar : @"",@"uid":[HTClient sharedInstance].currentUsername,@"nickName":nickName}];
    [[HTClient sharedInstance] sendMessage:message completion:^(HTMessage *message, NSError *error) {
        [self uploadMessage:message];
    }];
}

- (void)sendUpdateGroupAvatarMessage:(HTGroup *)group  withNickName:(NSString *)nickName {
    HTMessage * message = [QSMessageSDKHelper sendTextMessage:[NSString stringWithFormat:@"管理员修改了群资料"] to:group.groupId messageType:@"2" messageExt:@{@"action":@"2001",@"groupName":group.groupName,@"groupDescription":group.groupDescription ? group.groupDescription : @"",@"groupAvatar":group.groupAvatar ? group.groupAvatar : @"",@"uid":[HTClient sharedInstance].currentUsername,@"nickName":nickName}];
    [[HTClient sharedInstance] sendMessage:message completion:^(HTMessage *message, NSError *error) {
        [self uploadMessage:message];
    }];
}

- (void)addMemberToGroupByGroupId:(NSString *)groupId andMembers:(NSArray *)memebers byUser:(NSString *)nick {
    
    NSString * showContent = memebers[0][@"nickName"];
    for (int i = 1; i < memebers.count; i++) {
        showContent = [NSString stringWithFormat:@"%@、%@",showContent,memebers[i][@"nickName"]];
    }
    showContent = [NSString stringWithFormat:@"%@邀请了%@加入了群聊",nick,showContent];
    
    HTGroup * group = [self groupByGroupId:groupId];
    HTMessage * message = [QSMessageSDKHelper sendTextMessage:showContent to:group.groupId messageType:@"2" messageExt:@{@"action":@"2003",@"owner":[[HTClient sharedInstance] currentUsername],@"groupName":group.groupName,@"groupDescription":group.groupDescription ? group.groupDescription : @"",@"groupAvatar":group.groupAvatar ? group.groupAvatar : @"",@"members":memebers}];
    [[HTClient sharedInstance] sendMessage:message completion:^(HTMessage *message, NSError *error) {
        [self uploadMessage:message];
    }];
    
    [self addDeleteGroupWithGroupId:groupId];
    
}

- (void)addSelfToGroupByGroupId:(NSString *)groupId andMembers:(NSArray *)memebers andAdmin:(NSString *)admin{
    
    NSString *user = memebers[0][@"nickName"];
//    for (int i = 1; i < memebers.count; i++) {
//        showContent = [NSString stringWithFormat:@"%@、%@",showContent,memebers[i][@"nickName"]];
//    }
    NSString *showContent = [NSString stringWithFormat:@"%@加入了群聊",user];
    
    [[HTClient sharedInstance].groupManager getSingleGroupInfoWithGroupId:groupId success:^(HTGroup *aGroup) {
        HTMessage * message = [QSMessageSDKHelper sendTextMessage:showContent to:groupId messageType:@"2" messageExt:@{@"action":@"2003",@"owner":admin,@"groupName":aGroup.groupName,@"groupDescription":aGroup.description,@"groupAvatar":aGroup.groupAvatar,@"members":memebers}];
        [[HTClient sharedInstance] sendMessage:message completion:^(HTMessage *message, NSError *error) {
            [self uploadMessage:message];
        }];
        
    } failure:^(NSError *error) {
    }];
    
}

- (void)quitMemberToGroupByGroupId:(NSString *)groupId andMemberNickName:(NSString *)nickname {
    
    [[HTClient sharedInstance].conversationManager deleteOneConversationWithChatterId:groupId isCleanAllHistoryMessage:YES];
}

- (void)removeMemberToGroupByGroupId:(NSString *)groupId andMemberNickName:(NSString *)nickname andRemoveMemberUid:(NSString *)userId{
    HTMessage * message = [QSMessageSDKHelper sendTextMessage:[NSString stringWithFormat:@"\"%@\"被移除群聊",nickname] to:groupId messageType:@"2" messageExt: @{@"action":@"2004",@"uid":userId,@"nickName":nickname}];
    [[HTClient sharedInstance] sendMessage:message completion:^(HTMessage *message, NSError *error) {
        [self uploadMessage:message];
    }];
    
    HTCmdMessage * cmdMessage = [HTCmdMessage new];
    cmdMessage.to = userId;
    cmdMessage.chatType = @"1";
    NSDictionary * bodyDic = @{@"action":@"2004",@"data":groupId};
    cmdMessage.body = [bodyDic modelToJSONString];
    [QSMessageSDKHelper sendCmdMessage:cmdMessage];
    [[HTClient sharedInstance] sendCMDMessage:cmdMessage completion:nil];
    
   // [self addDeleteGroupWithGroupId:groupId];
}
    
- (void)removeMembersToGroupByGroupId:(NSString *)groupId andMemberNickName:(NSArray *)nickname andRemoveMemberUid:(NSArray *)userId{
    NSMutableString *nicks = [NSMutableString stringWithCapacity:0];
    NSMutableString *userIds = [NSMutableString stringWithCapacity:0];
    
    for (int i = 0; i < userId.count; i ++) {
        NSString *userIdStr = userId[i];
        
        NSString *nickStr = @"";
        if(nickname.count - 1 >= i){
            if(nickname[i] && [nickname[i] isKindOfClass:[NSString class]]){
                nickStr = nickname[i];
            }
        }
        
        if(userIdStr && [userIdStr isKindOfClass:[NSString class]]){
            if(i != 0){
                [userIds appendString:[NSString stringWithFormat:@"%@%@",@",",userIdStr]];
            }
            else{
                [userIds appendString:userIdStr];
            }
        }
        
        if(nickStr){
            if(i != 0){
                [nicks appendString:[NSString stringWithFormat:@"%@%@",@"、",nickStr]];
            }
            else{
                [nicks appendString:nickStr];
            }
        }
        
        
        
    }
    
    HTMessage * message = [QSMessageSDKHelper sendTextMessage:[NSString stringWithFormat:@"\"%@\"被移除群聊",nicks] to:groupId messageType:@"2" messageExt: @{@"action":@"2004",@"uid":userIds,@"nickName":nicks}];
    [[HTClient sharedInstance] sendMessage:message completion:^(HTMessage *message, NSError *error) {
        [self uploadMessage:message];
    }];
    
    for (int i = 0; i < userId.count; i ++) {
        NSString *userIdStr = userId[i];
        if(userIdStr && [userIdStr isKindOfClass:[NSString class]]){
            HTCmdMessage * cmdMessage = [HTCmdMessage new];
               cmdMessage.to = userIdStr;
               cmdMessage.chatType = @"1";
               NSDictionary * bodyDic = @{@"action":@"2004",@"data":groupId};
               cmdMessage.body = [bodyDic modelToJSONString];
               [QSMessageSDKHelper sendCmdMessage:cmdMessage];
               [[HTClient sharedInstance] sendCMDMessage:cmdMessage completion:nil];
        }
    }
    
 //   [self addDeleteGroupWithGroupId:groupId];
    
    
}

- (void)addDeleteGroupWithGroupId:(NSString *)groupId{
    HTCmdMessage * cmdMessage = [HTCmdMessage new];
    cmdMessage.to = groupId;
    cmdMessage.chatType = @"2";
    NSDictionary * bodyDic = @{@"action":@"2008",@"data":groupId};
    cmdMessage.body = [bodyDic modelToJSONString];
    [QSMessageSDKHelper sendCmdMessage:cmdMessage];
    [[HTClient sharedInstance] sendCMDMessage:cmdMessage completion:nil];
}

// 过滤控制符
- (NSString *)removeUnescapedCharacter:(NSString *)inputStr{
    if(inputStr && [inputStr isKindOfClass:[NSString class]]){
        NSCharacterSet *controlChars = [NSCharacterSet controlCharacterSet];
        //获取那些特殊字符
        NSRange range = [inputStr rangeOfCharacterFromSet:controlChars];
        //寻找字符串中有没有这些特殊字符
        if (range.location != NSNotFound) {
            NSMutableString *mutable = [NSMutableString stringWithString:inputStr];
            while (range.location != NSNotFound) {
                [mutable deleteCharactersInRange:range];
                //去掉这些特殊字符
                range = [mutable rangeOfCharacterFromSet:controlChars];
            }
            return mutable;
        }
        return inputStr;
    }
    return nil;
}

- (void)createGroup:(HTGroup *)model withMembers:(NSArray *)members success:(void (^)(HTGroup *aGroup))success failure:(void (^)(NSError *error))failure {
    
    NSDictionary * parameters = @{@"command":
                                      @{@"node":@"group-add",
                                        @"fields":@[
                                                @{@"var":@"uid",@"value":[[HTClient sharedInstance] currentUsername]},
                                                @{@"var":@"name",@"value":model.groupName},
                                                @{@"var":@"desc",@"value":model.groupDescription},
                                                @{@"var":@"imgurl",@"value":model.groupAvatar},
                                                @{@"var":@"members",@"value":members},
                                                ]
                                        }
                                  };
    
    NSMutableURLRequest *request = [self getRequestWithParameters:parameters AndAPIMethod:@"muc@muc.app.im"];
    QSNetworkTool * netWork = [QSNetworkTool new];
    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *dic, NSData *data) {
        NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        NSString * responseString = [QSAESManager EncryptionDecode:result];
        responseString = [self removeUnescapedCharacter:responseString];
        NSError *err;
        NSDictionary *responseDicI =[NSJSONSerialization JSONObjectWithData:[responseString dataUsingEncoding:NSUTF8StringEncoding]
                                                                    options:NSJSONReadingMutableContainers
                                                                      error:&err];
        NSArray * fields = responseDicI[@"command"][@"fields"];
        NSDictionary * group = [fields objectAtIndex:0];
        NSString * gid = group[@"value"];
        model.groupId = gid;
        [[HTClient sharedInstance].groupManager createNewGroup:model isSender:YES];
        success(model);
        
    } failure:^(NSError *error) {
        failure(error);
    }];
}


- (void)updateGroup:(HTGroup *)model  withNickname:(NSString *)aNickName success:(void (^)(HTGroup *aGroup))success failure:(void (^)(NSError *error))failure {
    NSDictionary * parameters= @{@"command":
                                     @{@"node":@"group-update",
                                       @"fields":@[
                                               @{@"var":@"uid",@"value":[[HTClient sharedInstance] currentUsername]},
                                               @{@"var":@"gid",@"value":model.groupId},
                                               @{@"var":@"name",@"value":model.groupName},
                                               @{@"var":@"desc",@"value":model.groupDescription},
                                               @{@"var":@"imgurl",@"value":model.groupAvatar}
                                               ]
                                       }
                                 };
    NSMutableURLRequest *request = [self getRequestWithParameters:parameters AndAPIMethod:@"muc@muc.app.im"];
    QSNetworkTool * netWork = [QSNetworkTool new];
    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *dic, NSData *data) {
        NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        NSString * responseString = [QSAESManager EncryptionDecode:result];
        responseString = [self removeUnescapedCharacter:responseString];
        NSError *err;
        NSDictionary *responseDicI =[NSJSONSerialization JSONObjectWithData:[responseString dataUsingEncoding:NSUTF8StringEncoding]
                                                                    options:NSJSONReadingMutableContainers
                                                                      error:&err];
        NSArray *tempArray = responseDicI[@"command"][@"fields"];
        NSDictionary *tempDic = tempArray[0];
        if ([tempDic[@"var"] isEqualToString:@"code"]) {
            if ([tempDic[@"value"]integerValue] == 1) {
                [[HTClient sharedInstance].groupManager updateGroupNameWithGroup:model withNickName:aNickName isSender:YES];
                success(model);
            }else{
                NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
                failure(fail);
            }
        }else{
            NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
            failure(fail);
        }
    } failure:^(NSError *error) {
        failure(error);
    }];
    
}

- (void)deleteGroupWithGroupId:(NSString *)groupId success:(void (^)(void))success failure:(void (^)(NSError *error))failure {
    NSDictionary * parameters = @{@"command":
                                      @{@"node":@"group-del",
                                        @"fields":@[
                                                @{@"var":@"uid",@"value":[[HTClient sharedInstance] currentUsername]},
                                                @{@"var":@"gid",@"value":groupId},
                                                ]
                                        }
                                  };
    NSMutableURLRequest *request = [self getRequestWithParameters:parameters AndAPIMethod:@"muc@muc.app.im"];
    QSNetworkTool * netWork = [QSNetworkTool new];
    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *dic, NSData *data) {
        NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        NSString * responseString = [QSAESManager EncryptionDecode:result];
        responseString = [self removeUnescapedCharacter:responseString];
        NSError *err;
        NSDictionary *responseDicI =[NSJSONSerialization JSONObjectWithData:[responseString dataUsingEncoding:NSUTF8StringEncoding]
                                                                    options:NSJSONReadingMutableContainers
                                                                      error:&err];
        
        NSArray *tempArr = responseDicI[@"command"][@"fields"];
        NSDictionary *tempDic = tempArr[0];
        if ([tempDic[@"var"] isEqualToString:@"code"]) {
            if ([tempDic[@"value"]integerValue] == 1) {
                

                [[HTClient sharedInstance].conversationManager deleteOneConversationWithChatterId:groupId isCleanAllHistoryMessage:YES];
                [[HTClient sharedInstance].groupManager deleteGroupByGroupId:groupId isSender:YES];
                
                success();
            
            }else{
                NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
                failure(fail);
            }
        }else{
            NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
            failure(fail);
        }
    } failure:^(NSError *error) {
        failure(error);
    }];
}

- (void)exitGroupWithGroupId:(NSString *)groupId withNickname:(NSString *)nickName success:(void (^)(void))success failure:(void (^)(NSError *error))failure
{
    NSString *uid = [[HTClient sharedInstance] currentUsername];
    NSDictionary * parameters = @{@"command":
                                      @{@"node":@"group-quit",
                                        @"fields":@[
                                                @{@"var":@"uid",@"value":uid},
                                                @{@"var":@"nickname",@"value":nickName},
                                                @{@"var":@"gid",@"value":groupId},
                                                ]
                                        }
                                  };
    NSMutableURLRequest *request = [self getRequestWithParameters:parameters AndAPIMethod:@"muc@muc.app.im"];
    QSNetworkTool * netWork = [QSNetworkTool new];
    
    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *dic, NSData *data) {
        NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        NSString * responseString = [QSAESManager EncryptionDecode:result];
        responseString = [self removeUnescapedCharacter:responseString];
        NSError *err;
        NSDictionary *responseDicI =[NSJSONSerialization JSONObjectWithData:[responseString dataUsingEncoding:NSUTF8StringEncoding]
                                                                    options:NSJSONReadingMutableContainers
                                                                      error:&err];
        NSArray *tempArray = responseDicI[@"command"][@"fields"];
        NSDictionary *tempDic = tempArray[0];
        if ([tempDic[@"var"] isEqualToString:@"code"]) {
            if ([tempDic[@"value"]integerValue] == 1) {
                
                [[HTClient sharedInstance].groupManager quitMemberToGroupByGroupId:groupId andMemberNickName:nickName];
                success();
                
            }else{
                NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
                failure(fail);
            }
        }else{
            NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
            failure(fail);
        }
        
    } failure:^(NSError *error) {
        failure(error);
    }];
}

- (void)addMemberWithUserIds:(NSArray *)aMembers andGroupId:(NSString *)aGroupId byUser:(NSString *)nick success:(void (^)(void))aSuccess failure:(void (^)(NSError *))aFailure{
    
    NSMutableArray * userIds = @[].mutableCopy;
    for (NSDictionary * memberDic in aMembers) {
        [userIds addObject:memberDic[@"uid"]];
    }
    
    NSDictionary * parameters = @{@"command":
                                      @{@"node":@"member-add",
                                        @"fields":@[
                                                @{@"var":@"uid",@"value":[[HTClient sharedInstance] currentUsername]},
                                                @{@"var":@"gid",@"value":aGroupId},
                                                @{@"var":@"oid",@"value":userIds.copy},
                                                ]
                                        }
                                  };
    NSMutableURLRequest *request = [self getRequestWithParameters:parameters AndAPIMethod:@"muc@muc.app.im"];
    QSNetworkTool * netWork = [QSNetworkTool new];
    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *dic, NSData *data) {
        NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        NSString * responseString = [QSAESManager EncryptionDecode:result];
        responseString = [self removeUnescapedCharacter:responseString];
        NSError *err;
        NSDictionary *responseDicI =[NSJSONSerialization JSONObjectWithData:[responseString dataUsingEncoding:NSUTF8StringEncoding]
                                                                    options:NSJSONReadingMutableContainers
                                                                      error:&err];
        NSDictionary * commend = responseDicI[@"command"];
        NSArray * fields = commend[@"fields"];
        NSDictionary * resultDic = [fields objectAtIndex:0];
        if ([resultDic[@"var"] isEqualToString:@"code"]) {
            if ([resultDic[@"value"] integerValue] == 1) {
                [[HTClient sharedInstance].groupManager addMemberToGroupByGroupId:aGroupId andMembers:aMembers byUser:nick];
                aSuccess();
            }else{
                NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
                aFailure(fail);
            }
        }else{
            NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
            aFailure(fail);
        }
    } failure:^(NSError *error) {
        aFailure(error);
    }];
}

- (void)addMemberWithUserIds:(NSArray *)members andGroupId:(NSString *)groupId andAdmin:(NSString *)admin success:(void (^)(void))success failure:(void (^)(NSError *error))failure{
    
    NSMutableArray * userIds = @[].mutableCopy;
    for (NSDictionary * memberDic in members) {
        [userIds addObject:memberDic[@"uid"]];
    }
    
    NSDictionary * parameters = @{@"command":
                                      @{@"node":@"member-add",
                                        @"fields":@[
                                                @{@"var":@"uid",@"value":admin},
                                                @{@"var":@"gid",@"value":groupId},
                                                @{@"var":@"oid",@"value":userIds.copy},
                                                ]
                                        }
                                  };
    NSMutableURLRequest *request = [self getRequestWithParameters:parameters AndAPIMethod:@"muc@muc.app.im"];
    QSNetworkTool * netWork = [QSNetworkTool new];
    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *dic, NSData *data) {
        NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        NSString * responseString = [QSAESManager EncryptionDecode:result];
        responseString = [self removeUnescapedCharacter:responseString];
        NSError *err;
        NSDictionary *responseDicI =[NSJSONSerialization JSONObjectWithData:[responseString dataUsingEncoding:NSUTF8StringEncoding]
                                                                    options:NSJSONReadingMutableContainers
                                                                      error:&err];
        NSDictionary * commend = responseDicI[@"command"];
        NSArray * fields = commend[@"fields"];
        NSDictionary * resultDic = [fields objectAtIndex:0];
        if ([resultDic[@"var"] isEqualToString:@"code"]) {
            if ([resultDic[@"value"] integerValue] == 1) {
                
                [self requestGroupList];
                [[HTClient sharedInstance].groupManager addSelfToGroupByGroupId:groupId andMembers:members andAdmin:admin];
                success();
            }else{
                NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
                failure(fail);
            }
        }else{
            NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
            failure(fail);
        }
    } failure:^(NSError *error) {
        failure(error);
    }];
}

- (void)deleteMemberWithGroupOwnerId:(NSString *)groupOwnerId userId:(NSString *)userId andGroupId:(NSString *)groupId andNickname:(NSString *)aNickName success:(void (^)(void))success failure:(void (^)(NSError *error))failure{
    NSDictionary * parameters = @{@"command":
                                      @{@"node":@"member-del",
                                        @"fields":@[
                                                @{@"var":@"uid",@"value":groupOwnerId},
                                                @{@"var":@"gid",@"value":groupId},
                                                @{@"var":@"oid",@"value":userId},
                                                ]
                                        }
                                  };
    NSMutableURLRequest *request = [self getRequestWithParameters:parameters AndAPIMethod:@"muc@muc.app.im"];
    QSNetworkTool * netWork = [QSNetworkTool new];
    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *dic, NSData *data) {
        NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        NSString * responseString = [QSAESManager EncryptionDecode:result];
        responseString = [self removeUnescapedCharacter:responseString];
        NSError *err;
        NSDictionary *responseDicI =[NSJSONSerialization JSONObjectWithData:[responseString dataUsingEncoding:NSUTF8StringEncoding]
                                                                    options:NSJSONReadingMutableContainers
                                                                      error:&err];
        NSArray *tempArray = responseDicI[@"command"][@"fields"];
        NSDictionary *tempDic = tempArray[0];
        if ([tempDic[@"var"] isEqualToString:@"code"]) {
            if ([tempDic[@"value"]integerValue] == 1) {
                [[HTClient sharedInstance].groupManager removeMemberToGroupByGroupId:groupId andMemberNickName:aNickName andRemoveMemberUid:userId];
                success();
            }else{
                NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
                failure(fail);
            }
        }else{
            NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
            failure(fail);
        }
    } failure:^(NSError *error) {
        failure(error);
    }];
}

- (void)deleteMemberWithUserId:(NSString *)userId andGroupId:(NSString *)groupId andNickname:(NSString *)aNickName success:(void (^)(void))success failure:(void (^)(NSError *error))failure{
    NSDictionary * parameters = @{@"command":
                                      @{@"node":@"member-del",
                                        @"fields":@[
                                                @{@"var":@"uid",@"value":[[HTClient sharedInstance] currentUsername]},
                                                @{@"var":@"gid",@"value":groupId},
                                                @{@"var":@"oid",@"value":userId},
                                                ]
                                        }
                                  };
    NSMutableURLRequest *request = [self getRequestWithParameters:parameters AndAPIMethod:@"muc@muc.app.im"];
    QSNetworkTool * netWork = [QSNetworkTool new];
    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *dic, NSData *data) {
        NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        NSString * responseString = [QSAESManager EncryptionDecode:result];
        responseString = [self removeUnescapedCharacter:responseString];
        NSError *err;
        NSDictionary *responseDicI =[NSJSONSerialization JSONObjectWithData:[responseString dataUsingEncoding:NSUTF8StringEncoding]
                                                                    options:NSJSONReadingMutableContainers
                                                                      error:&err];
        NSArray *tempArray = responseDicI[@"command"][@"fields"];
        NSDictionary *tempDic = tempArray[0];
        if ([tempDic[@"var"] isEqualToString:@"code"]) {
            if ([tempDic[@"value"]integerValue] == 1) {
                [[HTClient sharedInstance].groupManager removeMemberToGroupByGroupId:groupId andMemberNickName:aNickName andRemoveMemberUid:userId];
                success();
            }else{
                NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
                failure(fail);
            }
        }else{
            NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
            failure(fail);
        }
    } failure:^(NSError *error) {
        failure(error);
    }];
}

- (void)deleteMemberWithUserId:(NSString *)userId andGroupId:(NSString *)groupId andNickname:(NSString *)aNickName andAdminId:(NSString *)adminId success:(void (^)(void))success failure:(void (^)(NSError *))failure{
    NSDictionary * parameters = @{@"command":
                                      @{@"node":@"member-del",
                                        @"fields":@[
                                                @{@"var":@"uid",@"value":adminId},
                                                @{@"var":@"gid",@"value":groupId},
                                                @{@"var":@"oid",@"value":userId},
                                                ]
                                        }
                                  };
    NSMutableURLRequest *request = [self getRequestWithParameters:parameters AndAPIMethod:@"muc@muc.app.im"];
    QSNetworkTool * netWork = [QSNetworkTool new];
    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *dic, NSData *data) {
        NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        NSString * responseString = [QSAESManager EncryptionDecode:result];
        responseString = [self removeUnescapedCharacter:responseString];
        NSError *err;
        NSDictionary *responseDicI =[NSJSONSerialization JSONObjectWithData:[responseString dataUsingEncoding:NSUTF8StringEncoding]
                                                                    options:NSJSONReadingMutableContainers
                                                                      error:&err];
        NSArray *tempArray = responseDicI[@"command"][@"fields"];
        NSDictionary *tempDic = tempArray[0];
        if ([tempDic[@"var"] isEqualToString:@"code"]) {
            if ([tempDic[@"value"]integerValue] == 1) {
                [[HTClient sharedInstance].groupManager removeMemberToGroupByGroupId:groupId andMemberNickName:aNickName andRemoveMemberUid:userId];
                success();
            }else{
                NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
                failure(fail);
            }
        }else{
            NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
            failure(fail);
        }
    } failure:^(NSError *error) {
        failure(error);
    }];
}

- (void)deleteMembersWithGroupOwnerId:(NSString *)groupOwnerId  userIds:(NSArray *)userIds andGroupId:(NSString *)groupId andNickname:(NSArray *)aNickNames success:(void (^)(void))success failure:(void (^)(NSError *))failure{
    NSDictionary * parameters = @{@"command":
                                      @{@"node":@"member-del",
                                        @"fields":@[
                                                @{@"var":@"uid",@"value":groupOwnerId},
                                                @{@"var":@"gid",@"value":groupId},
                                                @{@"var":@"oid",@"value":userIds},
                                                ]
                                        }
                                  };
    NSMutableURLRequest *request = [self getRequestWithParameters:parameters AndAPIMethod:@"muc@muc.app.im"];
    QSNetworkTool * netWork = [QSNetworkTool new];
    [netWork requestWithMutableURLRequest:request success:^(NSDictionary *dic, NSData *data) {
        if(data.length == 0){
            NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
            failure(fail);
            return ;
        }
        NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        NSString * responseString = [QSAESManager EncryptionDecode:result];
        responseString = [self removeUnescapedCharacter:responseString];
        NSError *err;
        NSDictionary *responseDicI =[NSJSONSerialization JSONObjectWithData:[responseString dataUsingEncoding:NSUTF8StringEncoding]
                                                                    options:NSJSONReadingMutableContainers
                                                                      error:&err];
        NSArray *tempArray = responseDicI[@"command"][@"fields"];
        NSDictionary *tempDic = tempArray[0];
        if ([tempDic[@"var"] isEqualToString:@"code"]) {
            if ([tempDic[@"value"]integerValue] == 1) {
                
                [[HTClient sharedInstance].groupManager removeMembersToGroupByGroupId:groupId andMemberNickName:aNickNames andRemoveMemberUid:userIds];
                success();
            }else{
                NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
                failure(fail);
            }
        }else{
            NSError * fail = [NSError errorWithDomain:NSCocoaErrorDomain code:10000 userInfo:@{@"info":@"request IM server fail"}];
            failure(fail);
        }
    } failure:^(NSError *error) {
        failure(error);
    }];
}

- (void)getSingleGroupInfoWithGroupId:(NSString *)groupId  success:(void (^)(HTGroup *aGroup))success failure:(void (^)(NSError *error))failure{
    
    
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_async(queue, ^{
        NSDictionary * parameters = @{@"groupId":[[HTClient sharedInstance] currentUsername]
                                      };
        NSMutableURLRequest *request = [self getNewRequestWithParameters:parameters AndAPIMethod:@"/api/group/detail"];
        QSNetworkTool * netWork = [QSNetworkTool new];
        [netWork requestWithMutableURLRequest:request success:^(NSDictionary *dic, NSData *data) {
            NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
            NSString * responseString = [QSAESManager EncryptionDecode:result];
            responseString = [self removeUnescapedCharacter:responseString];
            
            
            NSError *err;
            //[responseString dataUsingEncoding:NSUTF8StringEncoding]
            NSDictionary *responseDicI =[NSJSONSerialization JSONObjectWithData:data
                                                                        options:NSJSONReadingMutableContainers
                                                                          error:&err];
            
            HTGroup *groupModel = [HTGroup new];
            
            if(responseDicI && [responseDicI isKindOfClass:[NSDictionary class]]){
                           NSDictionary *groupinfo = responseDicI[@"data"];
                           if(groupinfo && [groupinfo isKindOfClass:[NSDictionary class]]){
                               
                               if(groupinfo[@"name"] && [groupinfo[@"name"] isKindOfClass:[NSString class]]){
                                    groupModel.groupName = groupinfo[@"name"];
                               }
                                if (groupinfo[@"gid"] && [groupinfo[@"gid"] isKindOfClass:[NSNumber class]]){
                                    groupModel.groupId = [NSString stringWithFormat:@"%ld",[groupinfo[@"gid"] integerValue]];
                                }
                                if (groupinfo[@"desc"] && [groupinfo[@"desc"] isKindOfClass:[NSString class]]){
                                    groupModel.groupDescription = groupinfo[@"desc"];
                                }
                                if (groupinfo[@"creator"] && [groupinfo[@"creator"] isKindOfClass:[NSNumber class]]){
                                    groupModel.owner = [NSString stringWithFormat:@"%ld",[groupinfo[@"creator"] integerValue]];
                                }
                                if (groupinfo[@"imgurlde"] && [groupinfo[@"imgurlde"] isKindOfClass:[NSString class]]){
                                    groupModel.groupAvatar = groupinfo[@"imgurlde"];
                                }
                               
                           }
            }
            
           
            success(groupModel);
        } failure:^(NSError *error) {
            failure(error);
        }];
    });
    
    
    
}

- (void)getSelfGroups:(void (^)(NSArray *aGroups))success failure:(void (^)(NSError *error))failure{
    
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_async(queue, ^{
        NSDictionary * parameters = @{};
           
           NSMutableURLRequest *request = [self getNewRequestWithParameters:parameters AndAPIMethod:@"/api/group/my/list"];
           QSNetworkTool * netWork = [QSNetworkTool new];
           [netWork requestWithMutableURLRequest:request success:^(NSDictionary *dic, NSData *data) {
               NSArray * groups = @[];
               if (data) {
                   NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
                   NSString * responseString = [QSAESManager EncryptionDecode:result];
                   responseString = [self removeUnescapedCharacter:responseString];
                   NSError *err;
                   
                   
                   NSDictionary *responseDicI =[NSJSONSerialization JSONObjectWithData:data
                                                                               options:NSJSONReadingMutableContainers
                                                                                 error:&err];
                   
                   if(responseDicI && [responseDicI isKindOfClass:[NSDictionary class]]){
                       NSArray *groupList = responseDicI[@"data"];
                       
                       if(groupList && [groupList isKindOfClass:[NSArray class]]){
                           groups = groupList;
                       }
                   }
                   
               }
               success(groups);
           } failure:^(NSError *error) {
               failure(error);
           }];
    });
    
   
}

//构造请求request

- (NSMutableURLRequest *)getRequestWithParameters:(id)parameters AndAPIMethod:(NSString *)apiMethod;
{
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"%@%@",IM_HOST_URL,apiMethod]]];
    [request setHTTPMethod:@"POST"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
//    [request setValue:@"0" forHTTPHeaderField:@"None-AES"];
//    [request setValue:@"Basic YWRtaW5AYXBwLmltOjEyMzQ1NkBhcHA=" forHTTPHeaderField:@"Authorization"];
//    [request setHTTPBody:[parameters modelToJSONData]];
    [request setValue:[QSAESManager authorizationCode] forHTTPHeaderField:@"Authorization"];
    [request setHTTPBody:[[QSAESManager EncryptionEncode:[parameters modelToJSONString]] dataUsingEncoding:NSUTF8StringEncoding]];

    return request;

}

- (NSMutableURLRequest *)getNewRequestWithParameters:(id)parameters AndAPIMethod:(NSString *)apiMethod;
{
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"%@%@",IM_HOST_NEW_URL,apiMethod]]];
    [request setHTTPMethod:@"POST"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
//    [request setValue:@"0" forHTTPHeaderField:@"None-AES"];
//    [request setValue:@"Basic YWRtaW5AYXBwLmltOjEyMzQ1NkBhcHA=" forHTTPHeaderField:@"Authorization"];
//    [request setHTTPBody:[parameters modelToJSONData]];
    [request setValue:[QSAESManager authorizationCode] forHTTPHeaderField:@"Authorization"];
    
    HTClient *client = [HTClient sharedInstance];
    
    if(client.htClientGetUserInfo){
        NSDictionary *userInfo = client.htClientGetUserInfo();
        if(userInfo && [userInfo isKindOfClass:[NSDictionary class]]){
             NSString *token = userInfo[@"token"];
            if(token){
                [request setValue:token forHTTPHeaderField:@"zf-token"];
            }
        }
    }
    [request setHTTPBody:[[QSAESManager EncryptionEncode:[parameters modelToJSONString]] dataUsingEncoding:NSUTF8StringEncoding]];

    return request;

}

#pragma mark - HT SDK Noti

- (void)groupCreatedByMessage:(NSNotification *)noti {
    NSDictionary * notiDic = noti.userInfo;
    if (notiDic) {
        [self createNewGroup:notiDic[@"group"] isSender:[notiDic[@"isSender"] boolValue]];
    }
}

- (void)updateGroupByMessage:(NSNotification *)noti {
    NSDictionary * notiDic = noti.userInfo;
    if (notiDic) {
        [self updateGroupDescWithGroup:notiDic[@"group"] withNickName:notiDic[@"nickName"] isSender:[notiDic[@"isSender"] boolValue]];
    }
}

- (void)deleteGroupByMessage:(NSNotification *)noti {
    NSDictionary * notiDic = noti.userInfo;
    if (notiDic) {
        [self deleteGroupByGroupId:notiDic[@"groupId"] isSender:NO];
    }
}

@end
