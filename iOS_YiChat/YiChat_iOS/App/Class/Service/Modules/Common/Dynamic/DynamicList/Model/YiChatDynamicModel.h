//
//  YiChatDynamicModel.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/6.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ProjectBaseModel.h"
NS_ASSUME_NONNULL_BEGIN

@interface YiChatDynamicModel : ProjectBaseModel

@property (nonatomic,assign) NSInteger trendId;

@property (nonatomic,strong) NSString *userId;

@property (nonatomic,strong) NSString *content;

@property (nonatomic,strong) NSString *imgs;

@property (nonatomic,strong) NSString *videos;

@property (nonatomic,strong) NSString *location;

@property (nonatomic,strong) NSString *nick;

@property (nonatomic,strong) NSString *avatar;

@property (nonatomic,assign) NSInteger praiseCount;

@property (nonatomic,assign) NSInteger commentCount;

@property (nonatomic,strong) NSString *timeDesc;

@property (nonatomic,strong) NSArray *praiseList;
@property (nonatomic,strong) NSArray *commentList;

- (void)inittialPraiselistWithArray:(NSArray *)praiseList;

- (void)initialCommandListWithArray:(NSArray *)commandList;

@end

@interface YiChatDynamicCommitEntityModel : ProjectBaseModel

@property (nonatomic,strong) NSString *content;

@property (nonatomic,assign) NSInteger commentId;
//srcUserId
@property (nonatomic,assign) NSInteger srcUserId;
//srcNick
@property (nonatomic,strong) NSString *srcNick;
//评论人
@property (nonatomic,assign) NSInteger userId;
//nick
@property (nonatomic,strong) NSString *nick;
//timeDesc
@property (nonatomic,strong) NSString *timeDesc;
//动态ID
@property (nonatomic,assign) NSInteger trendId;

@end

@interface YiChatDynamicPraiseEntityModel : ProjectBaseModel

@property (nonatomic,assign) NSInteger userId;
@property (nonatomic,strong) NSString *nick;

@end


NS_ASSUME_NONNULL_END
