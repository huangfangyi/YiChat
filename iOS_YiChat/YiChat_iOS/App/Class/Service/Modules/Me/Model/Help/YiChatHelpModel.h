//
//  YiChatHelpModel.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatHelpModel : ProjectBaseModel

@property (nonatomic,strong) NSString *sectionTitle;

@property (nonatomic,strong) NSString *appearTitle;

@property (nonatomic,strong) NSArray *contentList;


+ (NSArray *)createModel;
@end

@interface YiChatHelpExtensionModel : NSObject

@property (nonatomic,strong) NSString *sectionTitle;

@property (nonatomic,strong) NSString *appearTitle;

@property (nonatomic,strong) NSArray *contentList;


@end

@interface YiChatHelpContentModel : NSObject

@property (nonatomic,strong) NSString *contentTitle;

@property (nonatomic,strong) NSString *contentText;

@end



NS_ASSUME_NONNULL_END
