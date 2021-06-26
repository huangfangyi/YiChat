//
//  NSData+base64.h
//  Vote
//
//  Created by Pro Mac on 13-1-28.
//  Copyright (c) 2013年 __MyCompanyName__. All rights reserved.
//



#import "NSData+base64.h"
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
@interface NSData (MBBase64)

+ (id)dataWithBase64EncodedString:(NSString *)string;     //  Padding '=' characters are optional. Whitespace is ignored.
- (NSString *)base64Encoding;
@end
