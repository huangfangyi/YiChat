//
//  ProjectConfigure.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/23.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectConfigure.h"
#import "TabBarProjectVC.h"
#import "YiChatLoginVC.h"
#import "ServiceGlobalDef.h"
#import <SDWebImage/SDWebImageDownloader.h>

static ProjectConfigure *configure = nil;
@implementation ProjectConfigure

+ (id)defaultConfigure{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        configure = [[ProjectConfigure alloc] init];
        
        configure.screenHeight = [UIScreen mainScreen].bounds.size.height;
        
        configure.safeArea = safeAreaInsets();
        
        [[SDWebImageDownloader sharedDownloader] setValue:@"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8" forHTTPHeaderField:@"Accept"];
    });
    return configure;
}

- (CGFloat)screenHeight{
    return _screenHeight;
}

UIEdgeInsets safeAreaInsets(void) {
    UIEdgeInsets safeAreaInsets = UIEdgeInsetsZero;
    if (@available(iOS 11.0, *)) {
        safeAreaInsets = [[[[UIApplication sharedApplication] delegate] window] safeAreaInsets];
    }
    return safeAreaInsets;
}

- (void)navInitial{
    UIViewController *vc = [[UIViewController alloc] init];
    UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:vc];
    
    //获取状态栏的rect
    CGRect statusRect = [[UIApplication sharedApplication] statusBarFrame];
    //获取导航栏的rect 44
    CGRect navRect = nav.navigationBar.frame;
    
    _statusSize = CGSizeMake(statusRect.size.width, statusRect.size.height);
    _navSize = CGSizeMake(navRect.size.width, navRect.size.height);
    
}

- (void)tabInitial{
    UITabBarController *tab = [[UITabBarController alloc] init];
    CGRect tabBarRect = tab.tabBar.bounds;
    _tabSize = CGSizeMake(tabBarRect.size.width, tabBarRect.size.height);
}

- (UITabBarController *)getTabBarVC{
    TabBarProjectVC *tab = [[TabBarProjectVC alloc] init];

    //,PROJECT_TEXT_LOCALIZE_NAME(@"discoverMain")
    //,@"YiChatDiscoverVC"
    //,@"find@3x.png"
    //,@"find_n@3x.png"
    if(YiChatProject_IsNeedMainAdvertisement){
        NSArray *textArr = @[PROJECT_TEXT_LOCALIZE_NAME(@"advertisementMain"),PROJECT_TEXT_LOCALIZE_NAME(@"conversationMain"),PROJECT_TEXT_LOCALIZE_NAME(@"connectionMain"),PROJECT_TEXT_LOCALIZE_NAME(@"meMain")];
           NSArray *classNameArr = @[@"YiChatAdvertisementMain",@"YiChatConversationVC",@"YiChatConnectionVC",@"YiChatPersonalVC"];
           NSArray *lightIconArr = @[@"advertisementmanin_light.png",@"news@3x.png",@"contacts@3x.png",@"we@3x.png"];
           NSArray *darkIconArr = @[@"advertisementmanin_dark.png",@"news_n@3x.png",@"contacts_n@3x.png",@"we_n@3x.png"];
           
       tab.addConfigure().addTextArr(textArr).addClassArr(classNameArr).addLightIconsArr(lightIconArr).addDarkIconsArr(darkIconArr).addUI();
        
    }
    else{
        NSArray *textArr = @[PROJECT_TEXT_LOCALIZE_NAME(@"conversationMain"),PROJECT_TEXT_LOCALIZE_NAME(@"connectionMain"),PROJECT_TEXT_LOCALIZE_NAME(@"meMain")];
           NSArray *classNameArr = @[@"YiChatConversationVC",@"YiChatConnectionVC",@"YiChatPersonalVC"];
           NSArray *lightIconArr = @[@"news@3x.png",@"contacts@3x.png",@"we@3x.png"];
           NSArray *darkIconArr = @[@"news_n@3x.png",@"contacts_n@3x.png",@"we_n@3x.png"];
           
    tab.addConfigure().addTextArr(textArr).addClassArr(classNameArr).addLightIconsArr(lightIconArr).addDarkIconsArr(darkIconArr).addUI();
    }
    
   
    
    return tab;
}

- (void)jumpToMain{
    if([[NSThread currentThread] isMainThread]){
        [self jump];
    }
    else{
        [ProjectHelper helper_getMainThread:^{
            [self jump];
        }];
    }
}

- (void)jump{
    AppDelegate *app = [ProjectHelper helper_getAppDelegate];
    if(![app.window.rootViewController isKindOfClass:[UITabBarController class]]){
        UITabBarController *tab = [[ProjectConfigure defaultConfigure] getTabBarVC];
        app.window.rootViewController = tab;
    }
}

- (UIViewController *)getAppAppearLoginVC{
    YiChatLoginVC *login = [YiChatLoginVC initialForPassword:NO];
    
    return login;
}

- (void)backToLogin{
    
    [ProjectHelper helper_getMainThread:^{
        
        UIWindow *window = [ProjectHelper helper_getAppDelegate].window;
        if(![window.rootViewController isKindOfClass:[UINavigationController class]] && [window.rootViewController isKindOfClass:[UITabBarController class]]){
            UIViewController *login = [self getAppAppearLoginVC];
            UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:login];
            window.rootViewController = nav;
        }
        
    }];
}
@end
