//
//  YiChatAgreementVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/9/5.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "NavProjectVC.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatAgreementVC : NavProjectVC
    
@property (nonatomic,strong) NSString *url;

+ (id)initialVCName:(NSString *)name;
@end

NS_ASSUME_NONNULL_END
