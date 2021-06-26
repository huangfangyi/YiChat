//
//  YRActionSheet.h
//  BussinessManager
//
//  Created by yunlian on 2017/4/11.
//  Copyright © 2017年 yunlian. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "YRGeneralApis.h"
@class YRActionSheet;

typedef void (^YRActionSheetClick)(NSInteger row);
@protocol YRActionSheetDelegate <NSObject>

@required
- (void)YRActionSheetDelegate:(YRActionSheet *)actionSheet GetCurrentClickBtnNumber:(NSInteger)number;

@end

@interface YRActionSheet : UIView
@property (nonatomic,assign) id<YRActionSheetDelegate>delegate;
@property (nonatomic,strong)    NSMutableArray *btnArr;
@property (nonatomic,strong)    NSMutableArray *labArr;
@property (nonatomic,strong)    UIColor *titleColor;
@property (nonatomic)   CGFloat titleFont;
@property (nonatomic)   CGFloat cellHeight;
@property (nonatomic,strong)    UIView *backView;
@property (nonatomic,copy) YRActionSheetClick click;

- (void)removeItem;
- (id)initWithListArr:(NSArray *)arr;
@end
