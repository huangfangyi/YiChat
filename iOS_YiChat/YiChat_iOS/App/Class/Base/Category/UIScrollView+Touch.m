//
//  UIScrollView+Touch.m
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/21.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import "UIScrollView+Touch.h"

@implementation UIScrollView (Touch)


- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    
    [[self nextResponder] touchesBegan:touches withEvent:event];
    
}

-(void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    
    [[self nextResponder] touchesMoved:touches withEvent:event];
    
}



- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    
    [[self nextResponder] touchesEnded:touches withEvent:event];
    
}


@end
