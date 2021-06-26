//
//  ProjectPhotoCell.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/20.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ProjectAlbumnModel.h"
NS_ASSUME_NONNULL_BEGIN

@interface ProjectPhotoCell : UICollectionViewCell

@property (nonatomic, copy) NSString *representedAssetIdentifier;

@property (nonatomic, assign) int32_t imageRequestID;

@property (nonatomic, strong) ProjectAssetModel *model;

@property (nonatomic,copy) NSArray *(^projectPhotoCellSelecte)(BOOL state,NSString * identify);

@property (nonatomic,copy) void(^ProjectVideoDownLoadProgress)(double progress,BOOL isDownLoad,NSString *alert);

@property (nonatomic,copy) BOOL(^ProjectIsCanSelecte)();

- (void)setModel:(ProjectAssetModel *)model size:(CGFloat)width;

- (void)updateSelecteIndexWithNumArr:(NSArray *)numArr;

@end

NS_ASSUME_NONNULL_END
