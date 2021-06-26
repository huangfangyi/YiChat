//
//  ProjectCommonCellModel.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseModel.h"
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectCommonCellModel : ProjectBaseModel

@property (nonatomic,strong) NSString *iconUrl;

@property (nonatomic,strong) NSString *titleStr;

@property (nonatomic,strong) NSString *contentStr;

@property (nonatomic,strong) NSString *desStr;

@property (nonatomic,strong) NSString *contentUrl;

@property (nonatomic,strong) NSString *identifier;

@property (nonatomic,strong) NSString *state;

@property (nonatomic,strong) NSString *ids;

@property (nonatomic,assign) BOOL isSelecte;

@end

NS_ASSUME_NONNULL_END
