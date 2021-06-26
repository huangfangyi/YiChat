//
//  ProjectPhotoListVC.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/20.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectPhotoListVC.h"
#import "ProjectAlbumnModel.h"
#import "ServiceGlobalDef.h"
#import "ProjectPhotoCell.h"
#import "ProjectAssetManager.h"
#import <TZImagePickerController/TZImagePickerController.h>
#import "ProjectBrowseManager.h"
@interface ProjectPhotoListVC ()

@property (nonatomic,strong) ProjectAlbumnModel *albmunModel;

@property (nonatomic,strong) ProjectAssetModelArr *assetModelArr;

@property (nonatomic,strong) NSMutableDictionary *cellDic;

@property (nonatomic,strong) NSMutableArray *selecteItem;

@property (nonatomic,assign) NSInteger limitPhoto;

@property (nonatomic,strong) UIView *progress;
@end


static NSString *projectPhotoListIdentify = @"ProjectPhotoListIdentify";

@implementation ProjectPhotoListVC

+ (id)initail{
    return [self initialVCWithNavBarStyle:ProjectNavBarStyleCommon_7 centeritem:nil leftItem:nil rightItem:nil];
}

- (NSArray *)getSelecteItems{
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
    
    for (int i = 0; i < _assetModelArr.count; i ++) {
        ProjectAssetModel *model = _assetModelArr[i];
        if(model.isSelected == YES){
            [arr addObject:model];
        }
    }
    return arr;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _limitPhoto = 9;
    
     _selecteItem = [NSMutableArray arrayWithCapacity:0];
    
    [self collectionConfig];
    
    [self configData];
    
    self.view.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    // Do any additional setup after loading the view.
}

- (ProjectAlbumnModel *)albmunModel{
    if(!_albmunModel){
        _albmunModel = [[ProjectAlbumnModel alloc] init];
        _albmunModel.allowPickingVideo = NO;
        _albmunModel.allowPickingImage = YES;
        _albmunModel.sortAscendingByModificationDate = YES;
    }
    return _albmunModel;
}

- (void)configData{
    WS(weakSelf)
    
    [self.albmunModel projectAlbumnModelGetAllAlumnSources:^(ProjectAssetModelArr * _Nonnull modelArr) {
        [ProjectHelper helper_getMainThread:^{
            weakSelf.assetModelArr = modelArr;
            
            [weakSelf.collectionView reloadData];
            
            CGFloat w = [[ProjectAssetManager assetManager] itemH];
            
            CGFloat magin = [[ProjectAssetManager assetManager] magin];
            
            CGFloat lineSpace = [[ProjectAssetManager assetManager] lineSpaceW];
            
            [weakSelf.collectionView setContentOffset:CGPointMake(0, magin * 2 + modelArr.count * (lineSpace + w) )];
        }];
    }];
}

- (void)collectionConfig{
    
    WS(weakSelf);
    ProjectAssetManager *manager = [ProjectAssetManager assetManager];
    manager.columnNumber = 4;
    
    CGFloat w = [[ProjectAssetManager assetManager] itemH];
    CGFloat h = w;
    
    [self setProjectCollectionViewNumItem:^NSUInteger(UICollectionView * _Nonnull collection, NSUInteger section) {
        return weakSelf.assetModelArr.count;
    }];
    
    [self setProjectCollectionViewNumSection:^NSUInteger(UICollectionView * _Nonnull collection) {
        return 1;
    }];
    
    [self setProjectCollectionViewItemSize:^CGSize(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSIndexPath * _Nonnull index) {
        return CGSizeMake(w, h);
    }];
    
    [self setProjectCollectionViewItemInset:^UIEdgeInsets(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        
        CGFloat magin = [[ProjectAssetManager assetManager] magin];
        
        return UIEdgeInsetsMake(magin, magin, magin, magin);
    }];
    
    [self setProjectCollectionViewItem:^UICollectionViewCell * _Nonnull(UICollectionView * _Nonnull collection, NSIndexPath * _Nonnull index) {
        
        ProjectPhotoCell *cell = [collection dequeueReusableCellWithReuseIdentifier:projectPhotoListIdentify forIndexPath:index];
        
        cell.ProjectIsCanSelecte = ^BOOL{
            
            if(weakSelf.selecteItem.count >= weakSelf.limitPhoto){
          //      [GSYUIHelper GSYUIHelper_getAlertWithMsm:[NSString stringWithFormat:@"最多只能选择%ld个图片",weakSelf.limitPhoto]];
                return NO;
            }
            else{
                return YES;
            }
        };
        
        cell.ProjectVideoDownLoadProgress = ^(double progress, BOOL isDownLoad, NSString * _Nonnull alert) {
            
            if(isDownLoad){
                [ProjectHelper helper_getMainThread:^{
                    [ProjectHelper helper_performInstanceSelectorWithTarget:weakSelf.progress initialMethod:@selector(setProgressvalueWithValue:) flags:@[[NSString stringWithFormat:@"%.1f",progress]]];
                }];
            }
            else{
                [weakSelf removeProgress];
                
                if(alert.length != 0){
           //         [GSYUIHelper GSYUIHelper_getAlertWithMsm:alert];
                }
                
            }
        };
        cell.projectPhotoCellSelecte = ^NSArray * _Nonnull(BOOL state, NSString *identify) {
            if(state == YES){
                
                __block BOOL isHas = NO;
                
                [weakSelf.selecteItem enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                    NSString *num = obj;
                    if([num isEqualToString:identify]){
                        isHas = YES;
                    }
                }];
                if(isHas == NO){
                    if(weakSelf.selecteItem.count < weakSelf.limitPhoto){
                        [weakSelf.selecteItem addObject:identify];
                    }
                    else{
           //             [GSYUIHelper GSYUIHelper_getAlertWithMsm:[NSString stringWithFormat:@"最多只能选择%ld张照片",weakSelf.limitPhoto]];
                        
                        return nil;
                    }
                }
            }
            else{
                [weakSelf.selecteItem enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                    NSString *num = obj;
                    if([num isEqualToString:identify]){
                        [weakSelf.selecteItem removeObjectAtIndex:idx];
                        
                        [[NSNotificationCenter defaultCenter] postNotificationName:@"GSYNotificationUnselectePhotoItem" object:weakSelf.selecteItem];
                    }
                }];
            }
            
            return weakSelf.selecteItem;
        };
        [cell setModel:weakSelf.assetModelArr[index.row] size:w];
        
        if(weakSelf.selecteItem.count > 0){
            [cell updateSelecteIndexWithNumArr:weakSelf.selecteItem];
        }
        return cell;
    }];
    
    [self setProjectCollectionViewSelecteItem:^(UICollectionView * _Nonnull collection, NSIndexPath * _Nonnull index) {
        
        NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
        [weakSelf.assetModelArr enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            ProjectAssetModel *model = obj;
            [arr addObject:model.asset];
        }];

        ProjectBrowseManager *broswerManager = [[ProjectBrowseManager alloc] init];
        UIView *broswer = [broswerManager showBrowserWithPhasset:arr index:index.row];
        [weakSelf.parentViewController.view addSubview:broswer];
    }];
    
    [self setProjectCollectionViewLineSpace:^CGFloat(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        CGFloat lineSpace = [[ProjectAssetManager assetManager] lineSpaceW];
        
        return lineSpace;
    }];
    
    [self setProjectCollectionViewInteritemSpace:^CGFloat(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        
         CGFloat lineSpace = [[ProjectAssetManager assetManager] interitemSpaceW];
        
        return lineSpace;
    }];
    
    [self setProjectCollectionViewHeaderSize:^CGSize(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        return CGSizeMake(0, 0);
    }];
    
    [self setProjectCollectionViewFooterSize:^CGSize(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        return CGSizeMake(0, 0);
    }];
    
    
    [self makeCollectionView];
    
    [self.collectionView registerClass:[ProjectPhotoCell class] forCellWithReuseIdentifier:projectPhotoListIdentify];

    
    self.collectionView.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    
    if (@available(iOS 10.0,*)) {
        self.collectionView.prefetchingEnabled = NO;
    }
}

- (UIView *)progress{
    if(!_progress){
     //   _progress = [GSYUIHelper GSYUIHelper_getProgressValue:0];
    }
    return _progress;
}

- (void)removeProgress{
    [ProjectHelper helper_getMainThread:^{
        if([_progress respondsToSelector:@selector(hidden)]){
            [_progress performSelector:@selector(hidden)];
            _progress = nil;
        }
    }];
}

- (void)layoutSubview{
    self.collectionView.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
