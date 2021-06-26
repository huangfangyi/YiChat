//
//  ProjectTranslateHelper.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/14.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectTranslateHelper : NSObject

/**
 *  从汉子，拼音，英文str中获取首字母 返回的首字母大写
 */
+ (NSString *)helper_getFirstCharacterFromStr:(NSString *)str;

/**
 *  获取索引数据源 @{@"":@[]}
 */
+ (NSArray *)helper_getIndexArrWithArr:(NSArray *)objArr key:(NSString *)key;

/**
 *  获取索引数据源 @{@"":@[]}
 */
+ (NSArray *)helper_getIndexArrWithFriendModelArr:(NSArray *)objArr;
//
///**
// *  聊天文字显示转换 将文字，emoji表情标示转换成聊天显示的文本和emoji表情
// */
//+ (NSAttributedString *)helper_getChatTextWithString:(NSString *)string font:(UIFont *)font;


+ (NSDictionary *)helper_dictionaryWithJsonString:(NSString *)jsonString;

+ (NSDictionary *)helper_translateObjPropertyToDic:(id )obj;

+ (NSString *)helper_convertJsonObjToJsonData:(NSDictionary *)dict;

/**
 *  手机号加密
 */
+ (NSString *)helper_securityPhoneNumWithPhone:(NSString *)number;

@end

NS_ASSUME_NONNULL_END
