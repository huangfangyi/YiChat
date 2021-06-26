//
//  ProjectMapVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/16.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "NavProjectVC.h"
#import "ProjectLocationManager.h"
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^ProjectNavSendLocationHandle)(CLLocationCoordinate2D location,NSString *address,NSString *name,UIImage *locationIMG);

#define YRLocationZero CLLocationCoordinate2DMake(0, 0)
#define YRLocation(x) (x.latitude == 0 && x.longitude == 0)

@interface ProjectMapVC : NavProjectVC

@property (nonatomic,copy) ProjectNavSendLocationHandle sendLocationHandle;

+ (id)initialMapVCWithLocation:(CLLocationCoordinate2D)location address:(NSString *)address description:(NSString *)description;

+ (id)initialSendMapVC;

@end

NS_ASSUME_NONNULL_END
