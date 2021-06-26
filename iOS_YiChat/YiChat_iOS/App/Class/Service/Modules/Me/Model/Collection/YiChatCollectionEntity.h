//
//  YiChatCollectionEntity.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseModel.h"
#import <UIKit/UIKit.h>
NS_ASSUME_NONNULL_BEGIN

@interface YiChatCollectionEntity : ProjectBaseModel


@property (nonatomic,strong) NSString *url;

@property (nonatomic,strong) NSString *userId;

@property (nonatomic,strong) NSString *nickName;

@property (nonatomic,strong) NSString *userIdBe;

@property (nonatomic,strong) NSString *userIdBe_nickName;

@property (nonatomic,strong) UIFont *userIdBe_nickNameFont;

@property (nonatomic,strong) NSString *text;

@property (nonatomic,strong) UIFont *font;

@property (nonatomic,strong) NSString *time;

@property (nonatomic,strong) UIFont *timeFont;

@property (nonatomic,assign) NSInteger type;

@property (nonatomic,assign) CGFloat sourceMaxW;

@property (nonatomic,assign) CGSize sourceSize;

@property (nonatomic,assign) CGSize nameSize;

@property (nonatomic,strong) UIImage *icon;



- (id)initWithDic:(NSDictionary *)dic;

@end

NS_ASSUME_NONNULL_END
