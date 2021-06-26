//
//  ProjectBaseCollectiionView.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/10.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectBaseCollectiionView.h"
#import "ProjectDef.h"

@interface ProjectBaseCollectiionView ()<UICollectionViewDataSource,UICollectionViewDelegateFlowLayout,UICollectionViewDelegate>

@end

@implementation ProjectBaseCollectiionView

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        
    }
    return self;
}

- (void)makeCollectionView{
    UICollectionViewFlowLayout *fl = [[UICollectionViewFlowLayout alloc]init];
    _layout = fl;
    
    _collectionView = [[UICollectionView alloc]initWithFrame:CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, PROJECT_SIZE_WIDTH, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + PROJECT_SIZE_TABH)) collectionViewLayout:fl];
    
    _collectionView.dataSource = self;
    
    _collectionView.delegate = self;
    
    [self addSubview: _collectionView];
}

#pragma mark -- UICollectionViewDataSource
//定义展示的UICollectionViewCell的个数
-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    if(self.ProjectCollectionViewNumItem){
        return self.ProjectCollectionViewNumItem(collectionView,section);
    }
    return 0;
}
//定义展示的Section的个数
-(NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    if(self.ProjectCollectionViewNumSection){
        return self.ProjectCollectionViewNumSection(collectionView);
    }
    return 0;
}
//每个UICollectionView展示的内容
-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if(self.ProjectCollectionViewItem){
        return self.ProjectCollectionViewItem(collectionView,indexPath);
    }
    return [UICollectionViewCell new];
}

/**
 *  collection view item header or footer
 */
- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath{
    // if ([kind isEqualToString:UICollectionElementKindSectionHeader])
    if(self.ProjectCollectionViewItemHeaderFooter){
        return self.ProjectCollectionViewItemHeaderFooter(collectionView,kind,indexPath);
    }
    return [UICollectionReusableView new];
}

#pragma mark --UICollectionViewDelegateFlowLayout
//定义每个UICollectionView 的大小
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if(self.ProjectCollectionViewItemSize){
        return self.ProjectCollectionViewItemSize(collectionView,collectionViewLayout,indexPath);
    }
    return CGSizeZero;
}

//定义每个UICollectionView 的 margin
-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    if(self.ProjectCollectionViewItemInset){
        return self.ProjectCollectionViewItemInset(collectionView,collectionViewLayout,section);
    }
    return UIEdgeInsetsZero;
}

// 设置最小行间距，也就是前一行与后一行的中间最小间隔
- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumLineSpacingForSectionAtIndex:(NSInteger)section {
    if(self.ProjectCollectionViewLineSpace){
        return self.ProjectCollectionViewLineSpace(collectionView,collectionViewLayout,section);
    }
    return 0;
}

// 设置最小列间距，也就是左行与右一行的中间最小间隔
- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumInteritemSpacingForSectionAtIndex:(NSInteger)section {
    if(self.ProjectCollectionViewInteritemSpace){
        return self.ProjectCollectionViewInteritemSpace(collectionView,collectionViewLayout,section);
    }
    return 0;
}

// 设置section头视图的参考大小，与tableheaderview类似
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout referenceSizeForHeaderInSection:(NSInteger)section {
    if(self.ProjectCollectionViewHeaderSize){
        return self.ProjectCollectionViewHeaderSize(collectionView,collectionViewLayout,section);
    }
    return CGSizeZero;
}

// 设置section尾视图的参考大小，与tablefooterview类似
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout referenceSizeForFooterInSection:(NSInteger)section {
    if(self.ProjectCollectionViewFooterSize){
        return self.ProjectCollectionViewFooterSize(collectionView,collectionViewLayout,section);
    }
    return CGSizeZero;
}

#pragma mark --UICollectionViewDelegate
//UICollectionView被选中时调用的方法
-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    if(self.ProjectCollectionViewSelecteItem){
        self.ProjectCollectionViewSelecteItem(collectionView, indexPath);
    }
}
//返回这个UICollectionView是否可以被选择
-(BOOL)collectionView:(UICollectionView *)collectionView shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
