//
//  ProjectBrowseManager.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/10.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectBrowseManager.h"
#import "YBImageBrowser.h"
#import "ProjectAlbumBrowser.h"

static ProjectBrowseManager *browseManager = nil;

@implementation ProjectBrowseManager

+ (id)create{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        browseManager = [[ProjectBrowseManager alloc] init];
    });
    return browseManager;
}


- (void)showImageBrowseWithDataSouce:(NSArray *)dataSource withSourceObjs:(NSArray *)objs currentIndex:(NSInteger)index{
    
    NSMutableArray * browserDataArr = [NSMutableArray arrayWithCapacity:0];
    
    
    [dataSource enumerateObjectsUsingBlock:^(NSString *_Nonnull imageStr, NSUInteger idx, BOOL * _Nonnull stop) {
        
        // 此处只是为了判断测试用例的数据源是否为视频，并不是仅支持 MP4。/ This is just to determine whether the data source of the test case is video, not just MP4.
        if ([imageStr hasPrefix:@"http"]) {
            
            // Type 3 : 网络图片 / Network image
            YBImageBrowseCellData *data = [YBImageBrowseCellData new];
            data.url = [NSURL URLWithString:imageStr];
            data.sourceObject = [self getObjs:objs idx:idx];;
            [browserDataArr addObject:data];
            
        } else {
            
            // Type 4 : 本地图片 / Local image (配置本地图片推荐使用 YBImage)
            YBImageBrowseCellData *data = [YBImageBrowseCellData new];
            data.imageBlock = ^__kindof UIImage * _Nullable{
                return [YBImage imageNamed:imageStr];
            };
            data.sourceObject = [self getObjs:objs idx:idx];;
            [browserDataArr addObject:data];
            
        }
    }];
    
    
    
    YBImageBrowser *browser = [YBImageBrowser new];
    browser.dataSourceArray = browserDataArr;
    browser.currentIndex = index;
    [browser show];
}


- (void)showImageBrowseWithIcons:(NSArray *)icons bgView:(UIView *)bgView currentIndex:(NSInteger)index{
    
    NSMutableArray * browserDataArr = [NSMutableArray arrayWithCapacity:0];
    
    
    [icons enumerateObjectsUsingBlock:^(UIImage *_Nonnull icon, NSUInteger idx, BOOL * _Nonnull stop) {
        
        if([icon isKindOfClass:[UIImage class]]){
            YBImageBrowseCellData *data = [YBImageBrowseCellData new];
            data.imageBlock = ^__kindof UIImage * _Nullable{
                return icon;
            };
            data.sourceObject = [self getObjs:@[bgView] idx:idx];;
            [browserDataArr addObject:data];
        }
        
    }];
    
    
    YBImageBrowser *browser = [YBImageBrowser new];
    browser.dataSourceArray = browserDataArr;
    browser.currentIndex = index;
    [browser show];
}

- (void)showImageBrowseWithIcons:(NSArray *)icons bgViews:(NSArray *)bgViews currentIndex:(NSInteger)index{
    NSMutableArray * browserDataArr = [NSMutableArray arrayWithCapacity:0];
    
    
    [icons enumerateObjectsUsingBlock:^(UIImage *_Nonnull icon, NSUInteger idx, BOOL * _Nonnull stop) {
        
        if([icon isKindOfClass:[UIImage class]]){
            YBImageBrowseCellData *data = [YBImageBrowseCellData new];
            data.imageBlock = ^__kindof UIImage * _Nullable{
                return icon;
            };
            data.sourceObject = [self getObjs:bgViews idx:idx];;
            [browserDataArr addObject:data];
        }
        
    }];
    
    
    YBImageBrowser *browser = [YBImageBrowser new];
    browser.dataSourceArray = browserDataArr;
    browser.currentIndex = index;
    [browser show];
}

- (void)showVideoBrowseWithDataSouce:(NSArray *)dataSource withSourceObjs:(NSArray *)objs currentIndex:(NSInteger)index corverImage:(UIImage *)cover{
    NSMutableArray * browserDataArr = [NSMutableArray arrayWithCapacity:0];
    
    [dataSource enumerateObjectsUsingBlock:^(id _Nonnull video, NSUInteger idx, BOOL * _Nonnull stop) {
        if([video isKindOfClass:[NSString class]] && video){
            NSString *videoStr = video;
            if ([video hasPrefix:@"http"]) {
                
                // Type 1 : 网络视频 / Network video
                YBVideoBrowseCellData *data = [YBVideoBrowseCellData new];
                data.url = [NSURL URLWithString:videoStr];
                data.sourceObject = [self getObjs:objs idx:idx];
                data.firstFrame = cover;
                [browserDataArr addObject:data];
                
            } else {
                
                // Type 2 : 本地视频文件 / Local video
                NSString *path = videoStr;
                
                if(path && [path isKindOfClass:[NSString class]]){
                    NSURL *url = [NSURL fileURLWithPath:path];
                    YBVideoBrowseCellData *data = [YBVideoBrowseCellData new];
                    data.url = url;
                    data.firstFrame = cover;
                    data.sourceObject = [self getObjs:objs idx:idx];
                    [browserDataArr addObject:data];
                }
            }
        }
        if([video isKindOfClass:[AVAsset class]] && video){
            YBVideoBrowseCellData *data = [YBVideoBrowseCellData new];
            data.avAsset = video;
            data.firstFrame = cover;
            data.sourceObject = [self getObjs:objs idx:idx];
            [browserDataArr addObject:data];
        }
    }];
    
    YBImageBrowser *browser = [YBImageBrowser new];
    browser.dataSourceArray = browserDataArr;
    browser.currentIndex = index;
    [browser show];
}


- (void)showVideoBrowseWithDataSouce:(NSArray *)dataSource withSourceObjs:(NSArray *)objs currentIndex:(NSInteger)index{
    
    NSMutableArray * browserDataArr = [NSMutableArray arrayWithCapacity:0];
    
    [dataSource enumerateObjectsUsingBlock:^(id _Nonnull video, NSUInteger idx, BOOL * _Nonnull stop) {
        if([video isKindOfClass:[NSString class]] && video){
             NSString *videoStr = video;
            if ([video hasPrefix:@"http"]) {
                
                // Type 1 : 网络视频 / Network video
                YBVideoBrowseCellData *data = [YBVideoBrowseCellData new];
                data.url = [NSURL URLWithString:videoStr];
                data.sourceObject = [self getObjs:objs idx:idx];
                [browserDataArr addObject:data];
                
            } else {
                
                // Type 2 : 本地视频文件 / Local video
                NSString *path = videoStr;
                
                if(path && [path isKindOfClass:[NSString class]]){
                    NSURL *url = [NSURL fileURLWithPath:path];
                    YBVideoBrowseCellData *data = [YBVideoBrowseCellData new];
                    data.url = url;
                    
                    data.sourceObject = [self getObjs:objs idx:idx];
                    [browserDataArr addObject:data];
                }
            }
        }
        if([video isKindOfClass:[AVAsset class]] && video){
            YBVideoBrowseCellData *data = [YBVideoBrowseCellData new];
            data.avAsset = video;
            data.sourceObject = [self getObjs:objs idx:idx];
            [browserDataArr addObject:data];
        }
    }];
    
    YBImageBrowser *browser = [YBImageBrowser new];
    browser.dataSourceArray = browserDataArr;
    browser.currentIndex = index;
    [browser show];
}

- (id)getObjs:(NSArray *)objs idx:(NSInteger)idx{
    if(objs.count -1 >= idx){
        return objs[idx];
    }
    else{
        return objs.lastObject;
    }
}

- (UIView *)showBrowserWithPhasset:(NSArray *)assets index:(NSInteger)index{
    return [ProjectAlbumBrowser showWithAssets:assets index:index];
}
@end
