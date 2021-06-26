//
//  YiChatVersionUpdateView.h
//  YiChat_iOS
//
//  Created by mac on 2019/8/23.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
typedef void(^VersionBlock)(BOOL isCancel);
@interface YiChatVersionUpdateView : UIView
//@property (nonatomic,assign) BOOL isMandatory;//是否强制更新
-(instancetype)initWithFrame:(CGRect)frame isMandatory:(BOOL)isMandatory version:(NSString *)version;
@property (nonatomic,copy) VersionBlock versionBlock;

@end

NS_ASSUME_NONNULL_END
