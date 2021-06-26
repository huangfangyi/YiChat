//
//  YiChatGroupSelectePersonView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupSelectePersonView.h"
#import "ServiceGlobalDef.h"
#import "YiChatGroupSelectePersonViewCell.h"

@interface YiChatGroupSelectePersonView ()

@property (nonatomic,assign) CGSize itemSize;
@property (nonatomic,strong) NSArray *selectePersons;

@end


static NSString *cellIdentify = @"YiChatGroupSelectePersonViewCell";
@implementation YiChatGroupSelectePersonView

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        self.hidden = YES;
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    [self collectionConfigWithLineSpace:10.0 interalSpace:5.0 magin:5.0 itemSize:CGSizeMake(self.frame.size.height - 20.0, self.frame.size.height - 20.0)];
}


- (void)collectionConfigWithLineSpace:(CGFloat)lineSpace interalSpace:(CGFloat)interalSpace magin:(CGFloat)magin itemSize:(CGSize)itemSize{
    
    _itemSize = itemSize;
    WS(weakSelf);
    
    CGFloat w = itemSize.width;
    CGFloat h = itemSize.height;
    
    [self setProjectCollectionViewNumItem:^NSUInteger(UICollectionView * _Nonnull collection, NSUInteger section) {
        return weakSelf.selectePersons.count;
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
        YiChatGroupSelectePersonViewCell *cell = [collection dequeueReusableCellWithReuseIdentifier:cellIdentify forIndexPath:index];
        
        if((weakSelf.selectePersons.count - 1) >= index.row){
            YiChatUserModel *user = weakSelf.selectePersons[index.row];
             [cell setModelWithModel:user index:index];
        }
        
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
    
    [self.collectionView registerClass:[YiChatGroupSelectePersonViewCell class] forCellWithReuseIdentifier:cellIdentify];
    
    if (@available(iOS 10.0,*)) {
        self.collectionView.prefetchingEnabled = NO;
    }
    
    self.collectionView.pagingEnabled = YES;
    
    self.collectionView.backgroundColor = [UIColor blackColor];
    
    [self layoutSubview];
}

- (void)layoutSubview{
    self.layout.scrollDirection =  UICollectionViewScrollDirectionHorizontal;
    self.collectionView.showsHorizontalScrollIndicator = NO;
    self.collectionView.showsVerticalScrollIndicator = NO;
    self.collectionView.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    self.collectionView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
}

- (void)changeSelectePersons:(NSArray *)persons invocation:(void(^)(CGRect frame))selectePersonChangedInvocation{
    if([persons isKindOfClass:[NSArray class]] ){
       
        self.selectePersons = persons;
        
        CGFloat itemW = _itemSize.width;
        
        if(persons.count > 0){
            
             self.hidden = NO;
            
            CGFloat w = 5 + persons.count * (itemW + 5.0);
            if(w >= 200){
                w = 200.0;
            }
            
            [UIView animateWithDuration:0.4 animations:^{
                self.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y,w, self.frame.size.height);
                self.collectionView.frame = self.bounds;
            }];
        
        }
        else{
            self.frame = CGRectMake(self.frame.origin.x,self.frame.origin.y, 0, self.frame.size.height);
            self.collectionView.frame = self.bounds;
            self.hidden = YES;
        }
        
        [self.collectionView reloadData];
        
        if(self.collectionView.contentSize.width > self.collectionView.frame.size.width){
            [self.collectionView setContentOffset:CGPointMake(self.collectionView.contentSize.width - self.collectionView.frame.size.width, self.collectionView.frame.size.height) animated:NO];
        }
        
        if(selectePersonChangedInvocation){
            selectePersonChangedInvocation(self.frame);
        }
    }
    
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
