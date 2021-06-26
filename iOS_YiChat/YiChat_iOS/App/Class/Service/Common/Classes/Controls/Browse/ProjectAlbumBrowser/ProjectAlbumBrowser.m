//
//  ProjectAlbumBrowser.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/10.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectAlbumBrowser.h"
#import "ProjectDef.h"
#import "ProjectAssetManager.h"
#import "ProjectAlbumnBrowserCell.h"
#import "ProjectAlbumBrowserModel.h"

@interface ProjectAlbumBrowser ()<UIGestureRecognizerDelegate,UIScrollViewDelegate>

@property (nonatomic,strong) NSArray *assetArr;

@property (nonatomic,assign) NSInteger index;


@end

static NSString *AliyunFilterViewIdentiter = @"ProjectAlbumBrowser";
@implementation ProjectAlbumBrowser


+ (id)showWithAssets:(NSArray *)assetArray index:(NSInteger)index{
    return [[self alloc] initWithFrame:CGRectMake(0, 0,PROJECT_SIZE_WIDTH , PROJECT_SIZE_HEIGHT) assets:assetArray index:index];
}

- (id)initWithFrame:(CGRect)frame assets:(NSArray *)assetArray index:(NSInteger)index{
    self = [super initWithFrame:frame];
    if(self){
        
        NSMutableArray *arr =  [NSMutableArray arrayWithCapacity:0];
        [assetArray enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            ProjectAlbumBrowserModel *model = [[ProjectAlbumBrowserModel alloc] init];
            model.asset = obj;
            if(model != nil){
                [arr addObject:model];
            }
        }];
        _assetArr = arr;
        _index = index;
        
        CGFloat lineSpace = 0;
        CGFloat interalSpace = 0;
        CGFloat magin = 0;
        CGFloat w = self.frame.size.width;
        CGFloat h = self.frame.size.height;
        
        [self collectionConfigWithLineSpace:lineSpace interalSpace:interalSpace magin:magin itemSize:CGSizeMake(w, h)];
        self.layout.scrollDirection =  UICollectionViewScrollDirectionHorizontal;
        
        [self.collectionView setContentOffset:CGPointMake(self.frame.size.width * _index, 0) animated:YES];
    }
    return self;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self removeFromSuperview];
}

- (void)collectionConfigWithLineSpace:(CGFloat)lineSpace interalSpace:(CGFloat)interalSpace magin:(CGFloat)magin itemSize:(CGSize)itemSize{
    
    WS(weakSelf);
    
    CGFloat w = itemSize.width;
    CGFloat h = itemSize.height;
    
    [self setProjectCollectionViewNumItem:^NSUInteger(UICollectionView * _Nonnull collection, NSUInteger section) {
        return weakSelf.assetArr.count;
    }];
    
    [self setProjectCollectionViewNumSection:^NSUInteger(UICollectionView * _Nonnull collection) {
        return 1;
    }];
    
    [self setProjectCollectionViewItemSize:^CGSize(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSIndexPath * _Nonnull index) {
        return CGSizeMake(w, h);
    }];
    
    [self setProjectCollectionViewItemInset:^UIEdgeInsets(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        
        return UIEdgeInsetsMake(magin, magin, magin, magin);
    }];
    
    [self setProjectCollectionViewItem:^UICollectionViewCell * _Nonnull(UICollectionView * _Nonnull collection, NSIndexPath * _Nonnull index) {
        ProjectAlbumnBrowserCell *cell = [collection dequeueReusableCellWithReuseIdentifier:AliyunFilterViewIdentiter forIndexPath:index];
        
        [cell setModel:weakSelf.assetArr[index.row] size:CGSizeMake(weakSelf.collectionView.frame.size.width, weakSelf.collectionView.frame.size.height)];
        return cell;
    }];
    
    [self setProjectCollectionViewSelecteItem:^(UICollectionView * _Nonnull collection, NSIndexPath * _Nonnull index) {
        
    }];
    
    [self setProjectCollectionViewLineSpace:^CGFloat(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        
        return lineSpace;
    }];
    
    [self setProjectCollectionViewInteritemSpace:^CGFloat(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        
        return interalSpace;
    }];
    
    [self setProjectCollectionViewHeaderSize:^CGSize(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        return CGSizeMake(0, 0);
    }];
    
    [self setProjectCollectionViewFooterSize:^CGSize(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        return CGSizeMake(0, 0);
    }];
    //[[NSNotificationCenter defaultCenter] postNotificationName:@"photoPreviewCollectionViewDidScroll" object:nil];
    
    [self makeCollectionView];
    self.collectionView.backgroundColor = [UIColor clearColor];
    
    [self.collectionView registerClass:[ProjectAlbumnBrowserCell class] forCellWithReuseIdentifier:AliyunFilterViewIdentiter];
    
    if (@available(iOS 10.0,*)) {
        self.collectionView.prefetchingEnabled = NO;
    }
    
    self.collectionView.pagingEnabled = YES;
    
    self.collectionView.backgroundColor = [UIColor blackColor];
    
    [self layoutSubview];
}

- (void)layoutSubview{
    self.collectionView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
}

- (void)makeUI{
    
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    [[NSNotificationCenter defaultCenter] postNotificationName:@"photoPreviewCollectionViewDidScroll" object:nil];
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
