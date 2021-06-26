//
//  QSAESManager.h
//  ChatDemo-UI3.0
//
//  Created by hawk on 16/9/21.
//  Copyright © 2016年 hawk. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSAESManager : NSObject

//加密data
+ (NSData *)EncryptionDataEncode:(NSData *)kData;
//解密data
+ (NSData *)EncryptionDataDecode:(NSData *)kData;

//返回data
+ (NSData *)encryptionEncodeToData:(NSString *)dataString;

//加密string
+ (NSString *)EncryptionEncode:(NSString *)dataString;
//解密string
+ (NSString *)EncryptionDecode:(NSString *)dataString;

+ (NSString *)authorizationCode;

+ (NSData *)EncryptionDecodeResponse:(NSString *)dataString;

+ (NSString *)URLDecodedString:(NSString *)oldString;

+ (NSString *)EncryptionDecodeByData:(NSData *)data;
@end
