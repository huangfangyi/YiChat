//
//  AppDelegate.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/22.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "AppDelegate.h"
#import "TabBarProjectVC.h"
#import "ProjectDef.h"
#import "YiChatLoginVC.h"
#import "YiChatServiceClient.h"
#import "ProjectLauageManage.h"
#import <Bugly/Bugly.h>
#import "ProjectStorageApis.h"
#import <AlipaySDK/AlipaySDK.h>
#import "WXApi.h"
#import <JPUSHService.h>
#import <AdSupport/AdSupport.h>
#import <CoreLocation/CoreLocation.h>
#import <TencentOpenAPI/TencentOAuth.h>
#import <TencentOpenAPI/QQApiInterface.h>
#import <TencentOpenAPI/QQApiInterfaceObject.h>
#import "AvoidCrash.h"
#import <UserNotifications/UserNotifications.h>

@interface AppDelegate ()<JPUSHRegisterDelegate>
// qq授权对象
@property (nonatomic,strong)TencentOAuth *tencentOAth;
@end

@implementation AppDelegate

- (void)dealwithCrashMessage:(NSNotification *)note {
    //不论在哪个线程中导致的crash，这里都是在主线程
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    [AvoidCrash becomeEffective];
    NSArray *noneSelClassStrings = @[@"NSNull",@"NSNumber",@"NSString",@"NSDictionary",@"NSArray"];
    [AvoidCrash setupNoneSelClassStringsArr:noneSelClassStrings];
    
    //监听通知:AvoidCrashNotification, 获取AvoidCrash捕获的崩溃日志的详细信息
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(dealwithCrashMessage:) name:AvoidCrashNotification object:nil];
    
    BuglyConfig *config = [BuglyConfig new];
    config.consolelogEnable = NO;
    config.excludeModuleFilter = @[@"NSBlockOperation",@"Foundation",@"UIKitCore",@"CoreFoundation"];
    [Bugly startWithAppId:@"ef7b51b809" config:config];
    
    UIWindow *window = [ProjectHelper helper_factoryMakeWindow];
    self.window = window;
    self.window.rootViewController = [[UIViewController alloc]init];
    
    [self appSetUpInitial];

    [[YiChatServiceClient defaultChatClient] yiChatServiceClient_initial];
    
    WS(weakSelf);
    [[YiChatServiceClient defaultChatClient] yiChatServiceClient_AutoLogin:^(BOOL isSuccess) {
        if(isSuccess == NO){
            [ProjectHelper helper_getMainThread:^{
                YiChatLoginVC *login = [[ProjectConfigure defaultConfigure] getAppAppearLoginVC];
                UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:login];
                weakSelf.window.rootViewController = nav;
            }];
        }
    }];
    
    [self setJGPush:launchOptions];
    
    self.tencentOAth = [[TencentOAuth alloc] initWithAppId:YiChatProject_QQ_AppId andDelegate:[YiChatServiceClient defaultChatClient]];
    return YES;
}

- (void)appSetUpInitial{
    [[ProjectConfigure defaultConfigure] navInitial];
    [[ProjectConfigure defaultConfigure] tabInitial];
    [[ProjectLauageManage sharedLanguage] initialLanguage];
}

-(void)setJGPush:(NSDictionary *)launchOptions{
    JPUSHRegisterEntity * entity = [[JPUSHRegisterEntity alloc] init];
    if (@available(iOS 12.0, *)) {
        entity.types = JPAuthorizationOptionAlert|JPAuthorizationOptionBadge|JPAuthorizationOptionSound|JPAuthorizationOptionProvidesAppNotificationSettings;
    } else {
        entity.types = JPAuthorizationOptionAlert|JPAuthorizationOptionBadge|JPAuthorizationOptionSound;
    }

    [JPUSHService registerForRemoteNotificationConfig:entity delegate:self];
    [JPUSHService setupWithOption:launchOptions appKey:YiChatProject_JGPUSH_AppKey
                          channel:@"channel"
                 apsForProduction:YES
            advertisingIdentifier:nil];
    
    //2.1.9版本新增获取registration id block接口。
    [JPUSHService registrationIDCompletionHandler:^(int resCode, NSString *registrationID) {
        if(resCode == 0){
            NSLog(@"registrationID获取成功：%@",registrationID);
            
        }
        else{
            NSLog(@"registrationID获取失败，code：%d",resCode);
        }
    }];
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    ZFChatNotifyEntity *entity = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleAppBecomeBackground];
    
    [entity postNotifyWithContent:nil];
}


- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    ZFChatNotifyEntity *entity = [ZFChatHelper zfChatHelper_getChatNotifyWithStyle:ZFChatNotifyStyleAppBecomeActive];
    
    [entity postNotifyWithContent:nil];
}


- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    
}


- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}


- (BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url{
    if ([url.host isEqualToString:@"oauth"]){
        return [WXApi handleOpenURL:url delegate:[YiChatServiceClient defaultChatClient]];
    }
    
    if ([url.host isEqualToString:@"qzapp"]){
        return [TencentOAuth HandleOpenURL:url];
    }
    return YES;
}

- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<NSString*, id> *)options
{
    NSLog(@"%@", url.host);
    NSLog(@"%@", url.scheme);
    if ([url.host isEqualToString:@"oauth"]){
        return [WXApi handleOpenURL:url delegate:[YiChatServiceClient defaultChatClient]];
    }
    
    if ([url.scheme isEqualToString:@"tencent1109844332"]){
        return [TencentOAuth HandleOpenURL:url];
    }
    
    if ([url.scheme isEqualToString:YiChatProject_WeiChat_AppKey]){
        return [WXApi handleOpenURL:url delegate:[YiChatServiceClient defaultChatClient]];
    }
    
    if ([url.host isEqualToString:@"safepay"]) {
        // 支付跳转支付宝钱包进行支付，处理支付结果
        [[AlipaySDK defaultService] processOrderWithPaymentResult:url standbyCallback:^(NSDictionary *resultDic) {
            if ([[resultDic objectForKey:@"resultStatus"] isEqualToString:@"9000"]) {
                NSLog(@"支付宝支付成功 = %@",resultDic);
//                [[NSNotificationCenter defaultCenter] postNotificationName:ALIPayResp object:nil];
//                [[NSNotificationCenter defaultCenter] removeObserver:self name:ALIPayResp object:nil];
            }
        }];
        
        // 授权跳转支付宝钱包进行支付，处理支付结果
        [[AlipaySDK defaultService] processAuth_V2Result:url standbyCallback:^(NSDictionary *resultDic) {
            NSLog(@"result = %@",resultDic);
            // 解析 auth code
            NSString *result = resultDic[@"result"];
            NSString *authCode = nil;
            if (result.length>0) {
                NSArray *resultArr = [result componentsSeparatedByString:@"&"];
                for (NSString *subResult in resultArr) {
                    if (subResult.length > 10 && [subResult hasPrefix:@"auth_code="]) {
                        authCode = [subResult substringFromIndex:10];
                        break;
                    }
                }
            }
            NSLog(@"授权结果 authCode = %@", authCode?:@"");
        }];
    }
    return YES;
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation
{
    if ([url.host isEqualToString:@"safepay"]) {
        // 支付跳转支付宝钱包进行支付，处理支付结果
        [[AlipaySDK defaultService] processOrderWithPaymentResult:url standbyCallback:^(NSDictionary *resultDic) {
            NSLog(@"result = %@",resultDic);
            if ([[resultDic objectForKey:@"resultStatus"] isEqualToString:@"9000"]) {
                NSLog(@"支付宝支付成功 = %@",resultDic);
//                [[NSNotificationCenter defaultCenter] postNotificationName:ALIPayResp object:nil];
//                [[NSNotificationCenter defaultCenter] removeObserver:self name:ALIPayResp object:nil];
            }
        }];
        
        // 授权跳转支付宝钱包进行支付，处理支付结果
        [[AlipaySDK defaultService] processAuth_V2Result:url standbyCallback:^(NSDictionary *resultDic) {
            NSLog(@"result = %@",resultDic);
            // 解析 auth code
            NSString *result = resultDic[@"result"];
            NSString *authCode = nil;
            if (result.length>0) {
                NSArray *resultArr = [result componentsSeparatedByString:@"&"];
                for (NSString *subResult in resultArr) {
                    if (subResult.length > 10 && [subResult hasPrefix:@"auth_code="]) {
                        authCode = [subResult substringFromIndex:10];
                        break;
                    }
                }
            }
            NSLog(@"授权结果 authCode = %@", authCode?:@"");
        }];
    }
    return YES;
}

- (void)application:(UIApplication *)application
didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {

    NSLog(@"%@", [NSString stringWithFormat:@"Device Token: %@", deviceToken]);
    [JPUSHService registerDeviceToken:deviceToken];
}

- (void)application:(UIApplication *)application
didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    NSLog(@"did Fail To Register For Remote Notifications With Error: %@", error);
}

#if __IPHONE_OS_VERSION_MAX_ALLOWED > __IPHONE_7_1
- (void)application:(UIApplication *)application didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings {
}


- (void)application:(UIApplication *)application handleActionWithIdentifier:(NSString *)identifier forLocalNotification:(UILocalNotification*)notification completionHandler:(void (^)())completionHandler {
}

- (void)application:(UIApplication *)application handleActionWithIdentifier:(NSString *)identifier
forRemoteNotification:(NSDictionary *)userInfo completionHandler:(void (^)())completionHandler {
    
}
#endif

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    [JPUSHService handleRemoteNotification:userInfo];
    NSLog(@"iOS7及以上系统，收到通知:%@", [self logDic:userInfo]);
    completionHandler(UIBackgroundFetchResultNewData);
}

- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
    [JPUSHService showLocalNotificationAtFront:notification identifierKey:nil];
}

- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(NSInteger))completionHandler {
    NSDictionary * userInfo = notification.request.content.userInfo;
    if([notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        [JPUSHService handleRemoteNotification:userInfo];
        completionHandler(UNNotificationPresentationOptionBadge);
    }else {
       
    }
    
}

- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)())completionHandler {
    NSDictionary * userInfo = response.notification.request.content.userInfo;
    if([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        [JPUSHService handleRemoteNotification:userInfo];
        NSLog(@"iOS10 收到远程通知:%@", [self logDic:userInfo]);
    }else {
       
    }
    
    completionHandler();  // 系统要求执行这个方法
}

#ifdef __IPHONE_12_0
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center openSettingsForNotification:(UNNotification *)notification{
 
}
#endif

- (NSString *)logDic:(NSDictionary *)dic {
    if (![dic count]) {
        return nil;
    }
    NSString *tempStr1 = [[dic description] stringByReplacingOccurrencesOfString:@"\\u" withString:@"\\U"];
    NSString *tempStr2 = [tempStr1 stringByReplacingOccurrencesOfString:@"\"" withString:@"\\\""];
    NSString *tempStr3 = [[@"\"" stringByAppendingString:tempStr2] stringByAppendingString:@"\""];
    NSData *tempData = [tempStr3 dataUsingEncoding:NSUTF8StringEncoding];
    NSString *str = [NSPropertyListSerialization propertyListFromData:tempData mutabilityOption:NSPropertyListImmutable format:NULL errorDescription:NULL];
    return str;
}

@end
