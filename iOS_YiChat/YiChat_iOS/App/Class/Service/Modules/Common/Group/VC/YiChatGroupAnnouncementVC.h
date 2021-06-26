//
//  YiChatGroupAnnouncementVC.h
//  YiChat_iOS
//
//  Created by mac on 2019/8/18.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "NavProjectVC.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatGroupAnnouncementVC : NavProjectVC
+ (id)initialVCWithManeger:(BOOL)maneger;
@property (nonatomic,strong) NSString *groupID;
//@property (nonatomic,assign) BOOL isManage;
@end

NS_ASSUME_NONNULL_END
