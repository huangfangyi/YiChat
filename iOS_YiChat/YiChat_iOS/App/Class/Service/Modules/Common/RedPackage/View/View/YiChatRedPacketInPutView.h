//
//  YiChatRedPacketInPutView.h
//  YiChat_iOS
//
//  Created by mac on 2019/6/27.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol YiChatRedPacketInPutViewDelegate <NSObject>

@optional

-(void)textFieldInPutChangeText:(NSString *)string tag:(NSInteger)tag;

@end

@interface YiChatRedPacketInPutView : UIView
@property (nonatomic,weak) id<YiChatRedPacketInPutViewDelegate> delegate;

@property (nonatomic,copy) NSString *redPacketTitle;
@property (nonatomic,copy) NSString *placeholder;
@property (nonatomic,copy) NSString *unit;
@property (nonatomic,assign) NSInteger textFieldTag;
@property (nonatomic,assign) BOOL isGroup;
@end

NS_ASSUME_NONNULL_END
