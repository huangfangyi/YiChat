//
//  UIImage+UIImageCategory.h
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/13.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIImage (UIImageCategory)

@property (nonatomic,strong) NSString *identifierName;

-(NSData *)compressWithMaxLength:(NSUInteger)maxLength;
@end

NS_ASSUME_NONNULL_END
