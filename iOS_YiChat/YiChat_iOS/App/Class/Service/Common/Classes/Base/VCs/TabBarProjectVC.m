//
//  TabBarProjectVC.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/13.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "TabBarProjectVC.h"
#import "ProjectHelper.h"
#import "ServiceGlobalDef.h"
#import <objc/runtime.h>
#import <objc/message.h>
#import <SDWebImage/UIImageView+WebCache.h>
#import "ProjectUIHelper.h"
#import "ZFChatHelper.h"

@interface TabBarProjectVC ()
{
    UITabBar *_cTabBarView;
}

@property (nonatomic) BOOL isConfigure;

@property (nonatomic,strong) NSMutableArray *dIndexTabBarIconArr;
@property (nonatomic,strong) NSMutableArray *dIndexTabBarLabArr;
@property (nonatomic,strong) NSMutableArray *dIndexTabBarBtnArr;
@property (nonatomic,strong) NSMutableArray *dIndexIconNumArr;

@property(nonatomic,strong) NSArray *tagsArr;
@property(nonatomic,strong) NSArray *tagDarkIconArr;
@property(nonatomic,strong) NSArray *tagLightIconArr;
@property(nonatomic,strong) NSArray *tagClassesArr;

@property (nonatomic,strong) UIFont *systemFont;

@property (nonatomic,strong) ZFChatNotifyEntity *changeBradge;

@end

@implementation TabBarProjectVC

- (void)dealloc{
    [_changeBradge removeMotify];
    _changeBradge = nil;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _changeBradge = [[ZFChatNotifyEntity alloc] initWithChatNotifyStyle:ZFChatNotifyStyleChangeBradgeNum target:self sel:@selector(changeBradge:)];
    [_changeBradge addNotify];
    // Do any additional setup after loading the view.
}

- (void)changeBradge:(id)notify{
    id obj = [notify object];
    if([obj isKindOfClass:[NSDictionary class]] && obj){
        NSString *tabName = obj[@"tabNum"];
        NSInteger bradgeNum = [obj[@"num"] integerValue];
        
        NSInteger num = -1;
        
        if(tabName && [tabName isKindOfClass:[NSString class]]){
            if([tabName isEqualToString:Project_TabIdengtify_Conversation]){
                num = [self getNumForClassesName:@"YiChatConversationVC"];
            }
            else if ([tabName isEqualToString:Project_TabIdengtify_Connection]){
                num = [self getNumForClassesName:@"YiChatConnectionVC"];
            }
        }
        
       
        if(num <= _dIndexIconNumArr.count - 1){
            [ProjectHelper helper_getMainThread:^{
                
                if((_dIndexIconNumArr.count - 1) >= num){
                    
                    UIView *icon = _dIndexIconNumArr[num];
                    
                    [ProjectUIHelper projectNumIcon:icon changeNum:bradgeNum];
                    
                    if(bradgeNum <= 0){
                        icon.hidden = YES;
                    }
                    else if(bradgeNum > 0){
                        icon.hidden = NO;
                    }
                    else{
                        icon.hidden = YES;
                    }
                    
                }
            }];
        }
    }
}

- (NSInteger)getNumForClassesName:(NSString *)name{
    if(name && [name isKindOfClass:[NSString class]]){
        for (int i = 0; i < self.tagClassesArr.count; i ++) {
            if([self.tagClassesArr[i] isEqualToString:name]){
                return i;
            }
        }
    }
    return -1;
}

- (void)configure{
    
    _isConfigure = YES;
    
    self.navigationController.navigationBar.hidden=YES;
    for (id temp in self.tabBar.subviews) {
        [temp removeFromSuperview];
    }
    self.view.backgroundColor = [UIColor whiteColor];
}

- (TabBarProjectVC *(^)(void))addUI{
    WS(weakSelf);
    
    return ^TabBarProjectVC *(){
        
        [weakSelf makeVC];
        
        return weakSelf;
    };
}

- (TabBarProjectVC *(^)(void))addConfigure{
    WS(weakSelf);
    
    return ^TabBarProjectVC *(){
        
        weakSelf.isConfigure = YES;
        
        weakSelf.dIndexTabBarBtnArr = [NSMutableArray arrayWithCapacity:0];
        weakSelf.dIndexTabBarLabArr = [NSMutableArray arrayWithCapacity:0];
        weakSelf.dIndexTabBarIconArr = [NSMutableArray arrayWithCapacity:0];
        weakSelf.dIndexIconNumArr = [NSMutableArray arrayWithCapacity:0];
        
        if(!weakSelf.systemFont){
            weakSelf.systemFont = PROJECT_TEXT_FONT_COMMON(12.0);
        }
        
        return weakSelf;
    };
}

- (TabBarProjectVC *(^)(NSArray *classArr))addClassArr{
    WS(weakSelf);
    
    return ^TabBarProjectVC *(NSArray *classArr){
        if(classArr.count != 0){
            weakSelf.tagClassesArr = classArr;
        }
        
        return weakSelf;
    };
}

- (TabBarProjectVC *(^)(NSArray *textArr))addTextArr{
    WS(weakSelf);
    
    return ^TabBarProjectVC *(NSArray *textArr){
        if(textArr.count != 0){
            weakSelf.tagsArr = textArr;
        }
        
        return weakSelf;
    };
}

- (TabBarProjectVC *(^)(NSArray *arr))addDarkIconsArr{
    WS(weakSelf);
    
    return ^TabBarProjectVC *(NSArray *arr){
        if(arr.count != 0){
            weakSelf.tagDarkIconArr = arr;
        }
        
        return weakSelf;
    };
}

- (TabBarProjectVC *(^)(NSArray *arr))addLightIconsArr{
    WS(weakSelf);
    
    return ^TabBarProjectVC *(NSArray *arr){
        if(arr.count != 0){
            weakSelf.tagLightIconArr = arr;
        }
        
        return weakSelf;
    };
}

- (void)makeVC{
    NSArray *vcArr=_tagClassesArr;
    
    NSMutableArray *VCArr=[NSMutableArray arrayWithCapacity:0];
    NSMutableArray *navArr = [NSMutableArray arrayWithCapacity:0];
    for (int i=0; i<vcArr.count; i++) {
        
        UIViewController *vc = [ProjectHelper helper_getVCWithName:vcArr[i] initialMethod:@selector(initialVC)];
        
        UINavigationController *nav=[[UINavigationController alloc] initWithRootViewController:vc];
        
        if(vc != nil && nav != nil){
            [VCArr addObject:vc];
            [navArr addObject:nav];
        }
    }
    
    self.viewControllers=navArr;
    self.selectedIndex=0;
    
    [self makeTabBarView];
}

- (void)makeTabBarView{
    
    CGFloat x = 0;
    CGFloat y = 0;
    CGFloat w = PROJECT_SIZE_WIDTH;
    CGFloat h = self.tabBarController.tabBar.frame.size.height;
    
    UITabBar *backView=[[UITabBar alloc] initWithFrame:CGRectMake(x, y, w, h)];
    [self.tabBar addSubview:backView];
    _cTabBarView=backView;
    backView.backgroundColor = PROJECT_COLOR_TABBARBACKCOLOR;

    self.tabBar.backgroundImage = [self imageWithColor:[UIColor colorWithRed:1 green:1 blue:1 alpha:0]];
    self.tabBar.shadowImage = [UIImage new];

    backView.backgroundImage = [self imageWithColor:[UIColor colorWithRed:1 green:1 blue:1 alpha:0]];
    backView.shadowImage = [UIImage new];
    self.tabBar.tintColor = [UIColor clearColor];
    self.tabBar.translucent = NO;
    
//    UIView *line=[ProjectHelper helper_factoryMakeHorizontalLineWithPoint:CGPointMake(0, 0) width:backView.frame.size.width];
//    [backView addSubview:line];
    
    NSArray *indexTitleArr=_tagsArr;
    
    for (int i = 0; i <indexTitleArr.count; i++) {
        
        
        UILabel *lab=[ProjectHelper helper_factoryMakeLabelWithFrame:CGRectZero andfont:_systemFont textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
        [backView addSubview:lab];
        [_dIndexTabBarLabArr addObject:lab];
        
        y = backView.frame.size.height - h - 1;
        
        UIImageView *icon=[ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectZero andImg:nil];
        
        [backView addSubview:icon];
        [_dIndexTabBarIconArr addObject:icon];
        
        UIView *iconNum = [ProjectUIHelper projectCreateNumIconWithPosition:CGPointMake(icon.frame.origin.x, icon.frame.origin.y) num:0];
        
        if(i == [self getNumForClassesName:@"YiChatConnectionVC"]){
            CGSize size = CGSizeMake(10.0, 10.0);
            iconNum.frame = CGRectMake(icon.frame.origin.x - size.width / 2, icon.frame.origin.y, size.width, size.height);
            iconNum.layer.cornerRadius = 10.0 / 2;
            iconNum.clipsToBounds = YES;
        }
        
        [backView addSubview:iconNum];
        if(iconNum){
            [_dIndexIconNumArr addObject:iconNum];
        }
        
        UIButton *clearBtn= [ProjectHelper helper_factoryMakeClearButtonWithFrame:CGRectZero target:self method:@selector(clearBtnMethod:)];
        [backView addSubview:clearBtn];
        [_dIndexTabBarBtnArr addObject:clearBtn];
    }
    
    [self selecteIndex:0];
}

#pragma mark click touch

- (void)clearBtnMethod:(UIButton *)btn{
    for (int i=0; i<_dIndexTabBarBtnArr.count; i++) {
        if(_dIndexTabBarBtnArr[i] == btn){
            [self selecteIndex:i];
            break;
        }
    }
}

- (void)tabBarProjectVCSelecteIndex:(NSInteger)index{
    [self selecteIndex:index];
}

- (void)selecteIndex:(NSInteger)index{
    self.selectedIndex=index;
    
    NSArray *indexIconLightArr=_tagLightIconArr;
    
    NSArray *indexIconDarkArr=_tagDarkIconArr;
    
    
    for (int i=0;i<_dIndexTabBarLabArr.count; i++) {
        
        UILabel *indexLab = _dIndexTabBarLabArr[i];
        indexLab.textColor = PROJECT_COLOR_TABBARTEXTCOLOR_UNSELECTE;
        
        UIImageView *imageView=_dIndexTabBarIconArr[i];
        
        if(indexIconDarkArr.count - 1 >= i){
            [self addImageWithUrl:indexIconDarkArr[i] image:imageView];
        }
    }
    
    UILabel *selecteIndexLab=_dIndexTabBarLabArr[index];
    UIImageView *imageView=_dIndexTabBarIconArr[index];
    
    if(indexIconLightArr.count - 1 >= index){
        [self addImageWithUrl:indexIconLightArr[index] image:imageView];
    }
    
    selecteIndexLab.textColor = PROJECT_COLOR_TABBARTEXTCOLOR_SELECTE;
    
    [self adjustTabbarSubView];
}

- (void)adjustTabbarSubView{
    CGFloat x = 20.0;
     CGFloat h = 20.0;
    CGFloat y = PROJECT_SIZE_TABH - h;
    CGFloat w = 0;
    
    CGFloat blank = 50.0;
    
    CGFloat text_w_single = (self.tabBar.frame.size.width - x * 2 - blank * (_tagsArr.count - 1)) / _tagsArr.count;
    CGFloat iconNumY = 0;
    for (int i = 0; i < _tagsArr.count; i ++ ) {
        NSString *text = _tagsArr[i];
        UILabel * indexLab = _dIndexTabBarLabArr[i];
        UIView *iconNum = _dIndexIconNumArr[i];
        indexLab.text = text;
        
        CGRect rect =  [ProjectHelper helper_getFontSizeWithString:text useSetFont:_systemFont withWidth:MAXFLOAT andHeight:MAXFLOAT];
        
        w = rect.size.width;
        
        indexLab.frame = CGRectMake(x + i * (blank + text_w_single), y, text_w_single, h);
        
        
        UIImageView *indexImg = _dIndexTabBarIconArr[i];
        UIButton *indexBtn = _dIndexTabBarBtnArr[i];
        
        w = (y - 5.0);
        
        indexImg.frame = CGRectMake(indexLab.frame.origin.x + indexLab.frame.size.width / 2 - w / 2, 5.0, w,w);
        
        iconNumY = indexImg.frame.origin.y  - iconNum.frame.size.height / 2;
        if(iconNumY <= 0){
            iconNumY = 1.0;
        }
        
        iconNum.frame = CGRectMake(indexImg.frame.origin.x + indexImg.frame.size.width - iconNum.frame.size.width / 2, iconNumY, iconNum.frame.size.width, iconNum.frame.size.height);
      
        indexBtn.frame = CGRectMake(indexLab.frame.origin.x + indexLab.frame.size.width / 2 - text_w_single / 2,0,text_w_single,PROJECT_SIZE_TABH);
    }
}

- (void)addImageWithUrl:(NSString *)url image:(UIImageView *)imageView{
    if([url hasPrefix:@"http"]){
        [imageView sd_setImageWithURL:[NSURL URLWithString:url]];
    }
    else{
        if(url != nil){
            imageView.image = [UIImage imageNamed:url];
        }
    }
}

- (UIImage *)imageWithColor:(UIColor *)color {
    
    CGRect rect = CGRectMake(0.0f,0.0f, 1.0f,1.0f);
    
    UIGraphicsBeginImageContext(rect.size);
    
    CGContextRef context =UIGraphicsGetCurrentContext();
    
    CGContextSetFillColorWithColor(context, [color CGColor]);
    
    CGContextFillRect(context, rect);
    
    UIImage *image =UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    
    return image;
    
}

- (void)addIconNum:(NSInteger)num index:(NSInteger)index{
    UIView *iconNum = nil;
    if((_dIndexIconNumArr.count - 1) >= index){
        iconNum = _dIndexIconNumArr[index];
        [ProjectUIHelper projectNumIcon:iconNum changeNum:num];
    }
}

- (void)removeIconNumWithIndex:(NSInteger)index{
    UIView *iconNum = nil;
    if((_dIndexIconNumArr.count - 1) >= index){
        iconNum = _dIndexIconNumArr[index];
        [ProjectUIHelper projectNumIcon:iconNum changeNum:9];
    }
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
