//
//  ProjectVideoListVC.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/20.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectCollectionVC.h"

NS_ASSUME_NONNULL_BEGIN

@interface ProjectVideoListVC : ProjectCollectionVC

+ (id)initail;

- (NSArray *)getSelecteItems;

- (void)layoutSubview;

@end

NS_ASSUME_NONNULL_END
