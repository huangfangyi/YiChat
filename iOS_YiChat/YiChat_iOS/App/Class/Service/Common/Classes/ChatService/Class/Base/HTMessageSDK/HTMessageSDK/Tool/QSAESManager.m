//
//  QSAESManager.m
//  ChatDemo-UI3.0
//
//  Created by hawk on 16/9/21.
//  Copyright © 2016年 hawk. All rights reserved.
//

#import "QSAESManager.h"
#import "NSData+QSEncryption.h"
#import "QSGTMBase64.h"

#define PASSWORD @"A286D372M63HFUKV"

@implementation QSAESManager

//加密
+ (NSData *)EncryptionDataEncode:(NSData *)kData{
    NSData *cipher = [kData AES256EncryptWithKey1:PASSWORD];//加密
    return cipher;
}

//解密
+ (NSData *)EncryptionDataDecode:(NSData *)kData{
    NSData *cipher = [kData AES256DecryptWithKey:PASSWORD];//解密
    return cipher;
}

//加密
+ (NSString *)EncryptionEncode:(NSString *)dataString{
    if (dataString.length > 0) {
        NSData *plain = [dataString dataUsingEncoding:NSUTF8StringEncoding];
        NSData *cipher = [plain AES256EncryptWithKey1:PASSWORD];//加密
        NSString *base64Encoded = [QSGTMBase64 stringByEncodingData:cipher];
        base64Encoded = [self URLEncodedString:base64Encoded];
        return base64Encoded;
    } else {
        return nil;
    }
}

+ (NSData *)encryptionEncodeToData:(NSString *)dataString {
    if (dataString.length > 0) {
        NSData *plain = [dataString dataUsingEncoding:NSUTF8StringEncoding];
        NSData *cipher = [plain AES256EncryptWithKey1:PASSWORD];//加密
        return cipher;
    } else {
        return nil;
    }
}

//解密
+ (NSString *)EncryptionDecode:(NSString *)dataString{
    if (dataString.length > 0) {
        NSData *plain = [QSGTMBase64 decodeString:dataString];
        NSData *cipher = [plain AES256DecryptWithKey:PASSWORD];//解密
        NSString *result = [[NSString alloc] initWithData:cipher  encoding:NSUTF8StringEncoding];
        
        return result;
    }
    else{
        return nil;
    }
}

//解密
+ (NSString *)EncryptionDecodeByData:(NSData *)data{
    if (data.length > 0) {
        NSData *plain = [QSGTMBase64 decodeData:data];
        NSData *cipher = [plain AES256DecryptWithKey:PASSWORD];//解密
        NSString *result = [[NSString alloc] initWithData:cipher  encoding:NSUTF8StringEncoding];
        return result;
    }
    else{
        return nil;
    }
}

//解密
+ (NSData *)EncryptionDecodeResponse:(NSString *)dataString{
    if (dataString.length > 0) {
        NSData *plain = [QSGTMBase64 decodeString:dataString];
        NSData *cipher = [plain AES256DecryptWithKey:PASSWORD];//解密
        return cipher;
    }
    else{
        return nil;
    }
}


+ (NSString *)authorizationCode {
    
    NSData * plain = [QSAESManager encryptionEncodeToData:@"Basic YWRtaW5AYXBwLmltOjEyMzQ1NkBhcHA="];
    NSString * authCode = [QSGTMBase64 stringByEncodingData:plain];
    authCode = [self URLEncodedString:authCode];
    return authCode;
}

+ (NSString *)URLEncodedString:(NSString *)oldString
{

    NSString *unencodedString = oldString;
    NSString *encodedString = (NSString *)
    CFBridgingRelease(CFURLCreateStringByAddingPercentEscapes(kCFAllocatorDefault,
                                                              (CFStringRef)unencodedString,
                                                              NULL,
                                                              (CFStringRef)@"!*'();:@&=+$,/?%#[]",
                                                              kCFStringEncodingUTF8));
    
    return encodedString;
}

/**
 *  URLDecode
 */
+ (NSString *)URLDecodedString:(NSString *)oldString
{
    //NSString *decodedString = [encodedString stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding ];
    
    NSString *encodedString = oldString;
    NSString *decodedString  = (__bridge_transfer NSString *)CFURLCreateStringByReplacingPercentEscapesUsingEncoding(NULL,
                                                                                                                     (__bridge CFStringRef)encodedString,
                                                                                                                     CFSTR(""),
                                                                                                                     CFStringConvertNSStringEncodingToEncoding(NSUTF8StringEncoding));
    return decodedString;
}

@end
