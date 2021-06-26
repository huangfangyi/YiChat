//
//  YiChatShowZhenView.h
//  YiChat_iOS
//
//  Created by mac on 2019/8/16.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

NS_ASSUME_NONNULL_BEGIN
typedef void(^PromptBlock)(NSDictionary *groupMsg);
typedef void(^PromptDimissBlock)(void);

@interface YiChatShowZhenView : UIView

@property (nonatomic,strong) NSDictionary *dic;
@property (nonatomic,copy) PromptDimissBlock dimissBlock;
@property (nonatomic,copy) PromptBlock promptBlock;

//-(UIView *)showZhenView;
@end

NS_ASSUME_NONNULL_END
