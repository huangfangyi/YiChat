//
//  YiChatTransactionRecordsCell.h
//  YiChat_iOS
//
//  Created by mac on 2019/7/23.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "YiChatWalletRecordModel.h"
NS_ASSUME_NONNULL_BEGIN

@interface YiChatTransactionRecordsCell : UITableViewCell
@property (nonatomic,strong) YiChatWalletRecordListModel *model;
@end

NS_ASSUME_NONNULL_END
