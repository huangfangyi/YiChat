//
//  QSMessageSDKHelper.h
//  HTMessage
//
//  Created by 非夜 on 17/1/5.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HTMessage.h"
#import "HTCmdMessage.h"

@interface QSMessageSDKHelper : NSObject

+ (HTMessage *)sendTextMessage:(NSString *)text
                            to:(NSString *)to
                   messageType:(NSString *)messageType
                    messageExt:(NSDictionary *)messageExt;

+ (void )sendCmdMessage:(HTCmdMessage *)aCMDMessage;

+ (HTMessage *)sendLocationMessageWithLatitude:(double)latitude
                                     longitude:(double)longitude
                                       address:(NSString *)address
                                            to:(NSString *)to
                                   messageType:(NSString *)messageType
                                    andSSImage:(UIImage *)mapImage
                                    messageExt:(NSDictionary *)messageExt;

+ (HTMessage *)sendImageMessageWithImage:(UIImage *)image
                                      to:(NSString *)to
                             messageType:(NSString *)messageType
                              messageExt:(NSDictionary *)messageExt;

+ (HTMessage *)sendAudioMessageWithLocalPath:(NSString *)localPath
                                    duration:(NSInteger)duration
                                          to:(NSString *)to
                                 messageType:(NSString *)messageType
                                  messageExt:(NSDictionary *)messageExt;

+ (HTMessage *)sendVideoMessageWithURL:(NSURL *)url
                                    to:(NSString *)to
                           messageType:(NSString *)messageType
                            andSSImage:(UIImage *)mapImage
                          andVideoTime:(NSInteger)videoDurtion
                            messageExt:(NSDictionary *)messageExt;

@end
