//
//  HTClientDelegate.h
//  HTMessage
//
//  Created by 非夜 on 17/2/16.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 ~cn:网络连接状态 ~en:network status
 
 - HTConnectionConnected: ~cn:已连接 ~en:connected
 - HTConnectionConnecting: ~cn:正在连接 ~en:connecting
 - HTConnectionDisconnected: ~cn:已断开连接 ~en:disconnected
 */
typedef NS_ENUM(NSInteger,HTConnectionState){
    HTConnectionStateConnected = 0,
    HTConnectionStateConnecting = 1,
    HTConnectionStateDisconnected,
};

/**
 ~cn:HTClient的代理 ~en:HTClient delegate
 */
@protocol HTClientDelegate <NSObject>

@required
/**
 ~cn:自动登陆失败的回调 ~en:a callback when automatic login failed
 
 @param aFaile ~cn:是否失败，默认为NO ~en:failed or not, default is NO
 */
- (void)didAutoLoginWithFailed:(BOOL)aFaile;

@optional
/**
 ~cn:HTClient登陆成功后连接服务器的状态变化时会接收到该回调 ~en:a callback after has logged
 
 @param aConnectionState ~cn:当前的状态 ~en:current status
 */
- (void)connectionStateDidChange:(HTConnectionState)aConnectionState;

/**
 ~cn:当前登录账号在其它设备登录时会接收到此回调 ~en:a callback when the same account logged in anohter device
 */
- (void)userAccountDidLoginFromOtherDevice;

/**
 ~cn:自动登陆成功的回调
*/
- (void)accountDidAutoLoginSuccess;


@end
