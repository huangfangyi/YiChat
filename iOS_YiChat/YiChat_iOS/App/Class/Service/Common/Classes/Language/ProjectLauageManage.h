//
//  ProjectLauageManage.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger,ProjectLauageType){
    ProjectLauageTypeEnglish,ProjectLauageTypeSimpleChinese,ProjectLauageTypeUnsupport
};


@interface ProjectLauageManage : NSObject

@property (nonatomic,strong) NSBundle *currentBundle;

@property (nonatomic,strong) NSString *currentLanguage;

+ (id)sharedLanguage;

- (void)initialLanguage;

- (NSString *)getAppearWordWithKey:(NSString *)key;

- (ProjectLauageType)getLanguageWithLanguageWord:(NSString *)languageWord;

@end

NS_ASSUME_NONNULL_END
