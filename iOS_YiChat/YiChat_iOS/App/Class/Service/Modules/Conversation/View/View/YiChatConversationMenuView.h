//
//  YiChatConversationMenuView.h
//  YiChat_iOS
//
//  Created by mac on 2019/8/21.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class ZFMenuEntity;
@interface YiChatConversationMenuView : UIView
@property (nonatomic,copy) void(^zfConversationItemClick)(ZFMenuEntity *entity);
@property (nonatomic,copy) void(^loadDataDoneBlock)(BOOL isData);

+ (id)createMenu;

- (void)reloadDataWhenNoData;

+ (void)downLoadDataWithDataArrs:(void(^)(NSArray *menus))dataBlock;


@end


@interface ZFMenuEntity : NSObject

@property (nonatomic,strong) NSString *icon;

@property (nonatomic,strong) NSString *title;

@property (nonatomic,strong) NSString *url;

@property (nonatomic,strong) NSString *itemId;

- (id)initWithDic:(NSDictionary *)dic;

@end
NS_ASSUME_NONNULL_END
