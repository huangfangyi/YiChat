//
//  ProjectSearchMsgCell.h
//  YiChat_iOS
//
//  Created by mac on 2019/8/1.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ProjectSearchMsgModel.h"
#import "HTMessage.h"
NS_ASSUME_NONNULL_BEGIN

@interface ProjectSearchMsgCell : UITableViewCell
@property (nonatomic,strong) NSArray *dataArr;
@property (nonatomic,strong) HTMessage *message;
@end

NS_ASSUME_NONNULL_END
