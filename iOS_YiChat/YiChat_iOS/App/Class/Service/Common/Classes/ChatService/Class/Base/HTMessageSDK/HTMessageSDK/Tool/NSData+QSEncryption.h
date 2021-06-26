//
//  NSData+QSEncryption.h
//  AESClass
//
//  Created by a1520 on 16/1/15.
//  Copyright (c) 2016年 a1520. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSData (QSEncryption)

- (NSData *)AES256EncryptWithKey:(NSString *)key;   //加密
- (NSData *)AES256DecryptWithKey:(NSString *)key;   //解密
- (NSData *)AES256EncryptWithKey1:(NSString *)key;   //加密

@end
