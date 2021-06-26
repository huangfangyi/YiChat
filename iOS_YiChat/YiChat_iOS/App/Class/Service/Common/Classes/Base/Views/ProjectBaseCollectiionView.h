//
//  ProjectBaseCollectiionView.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/10.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectBaseCollectiionView : UIView

@property (nonatomic,strong) UICollectionViewFlowLayout *layout;

@property (nonatomic,strong) UICollectionView *collectionView;

@property (nonatomic,copy) void(^ProjectCollectionViewSelecteItem)(UICollectionView *collection,NSIndexPath *index);

@property (nonatomic,copy) UIEdgeInsets(^ProjectCollectionViewItemInset)(UICollectionView *collection,UICollectionViewLayout *layout,NSInteger section);

@property (nonatomic,copy) CGSize(^ProjectCollectionViewItemSize)(UICollectionView *collection,UICollectionViewLayout *layout,NSIndexPath *index);

@property (nonatomic,copy) UICollectionViewCell *(^ProjectCollectionViewItem)(UICollectionView *collection,NSIndexPath *index);

@property (nonatomic,copy) UICollectionReusableView *(^ProjectCollectionViewItemHeaderFooter)(UICollectionView *collection,NSString *kind,NSIndexPath *index);

@property (nonatomic,copy) CGFloat(^ProjectCollectionViewLineSpace)(UICollectionView *collection,UICollectionViewLayout *layout,NSInteger section);

@property (nonatomic,copy) CGFloat(^ProjectCollectionViewInteritemSpace)(UICollectionView *collection,UICollectionViewLayout *layout,NSInteger section);

@property (nonatomic,copy) CGSize(^ProjectCollectionViewHeaderSize)(UICollectionView *collection,UICollectionViewLayout *layout,NSInteger section);

@property (nonatomic,copy) CGSize(^ProjectCollectionViewFooterSize)(UICollectionView *collection,UICollectionViewLayout *layout,NSInteger section);

@property (nonatomic,copy) NSUInteger(^ProjectCollectionViewNumSection)(UICollectionView *collection);

@property (nonatomic,copy) NSUInteger(^ProjectCollectionViewNumItem)(UICollectionView *collection,NSUInteger section);

- (void)makeCollectionView;

@end

NS_ASSUME_NONNULL_END
