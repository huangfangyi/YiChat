//
//  YiChatSearchGroupMsgVC.h
//  YiChat_iOS
//
//  Created by mac on 2019/11/21.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NavProjectVC.h"
NS_ASSUME_NONNULL_BEGIN

@interface YiChatSearchGroupMsgVC : NavProjectVC
+ (id)initialVC;

@property (nonatomic,strong) NSString *chatId;
@property (nonatomic,strong) NSString *chatType;
@end


NS_ASSUME_NONNULL_END
