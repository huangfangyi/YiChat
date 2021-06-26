//
//  RXAESEncryptor.h
//  RuoXinApp
//
//  Created by 金匡元 on 2018/9/12.
//  Copyright © 2018年 合肥乡音科技网络有限公司. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RXAESEncryptor : NSObject

// 普通AES加、解密
+(NSData *)AES256ParmEncryptWithKey:(NSString *)key Encrypttext:(NSData *)text;   //加密
+(NSData *)AES256ParmDecryptWithKey:(NSString *)key Decrypttext:(NSData *)text;   //解密
+(NSString *) aes256_encrypt:(NSString *)key Encrypttext:(NSString *)text;
+(NSString *) aes256_decrypt:(NSString *)key Decrypttext:(NSString *)text;

// 追加base64方式加密
+ (NSString *)encryptAES:(NSString *)content key:(NSString *)key;

+ (NSString *)nonbase64encryptAES:(NSString *)content key:(NSString *)key;

// 追加base64方式解密
+ (NSDictionary *)decryptAES:(NSString *)content key:(NSString *)key;

+ (NSDictionary *)nonbase64decryptAES:(NSString *)content key:(NSString *)key;

@end
