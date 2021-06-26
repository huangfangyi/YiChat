//
//  XYQRCodeProduct.h
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/20.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface XYQRCodeProduct : NSObject

+ (UIImage *)qrCodeImageWithContent:(NSString *)content
                      codeImageSize:(CGFloat)size
                               logo:(UIImage *)logo
                          logoFrame:(CGRect)logoFrame
                                red:(CGFloat)red
                              green:(CGFloat)green
                               blue:(CGFloat)blue;

@end

NS_ASSUME_NONNULL_END
