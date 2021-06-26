//
//  YiChatQRCodeScanVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/1.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "NavProjectVC.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatQRCodeScanVC : NavProjectVC

@property (nonatomic,strong) NSString *decodeScanString;

+ (id)initialVC;
@end

NS_ASSUME_NONNULL_END
