//
//  ProjectTextInputView.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/21.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ProjectHelper.h"
NS_ASSUME_NONNULL_BEGIN

typedef  NS_ENUM(NSUInteger,ProjectInputViewStyle){
    ProjectInputViewStylePhoneInput=0,
    ProjectInputViewPasswordInput,
    ProjectInputViewStyleSetPasswordInput,
    ProjectInputViewStyleInputCertify,
    ProjectInputViewStyleSelecteAreaCountry
};

@interface ProjectTextInputView : UIView

@property (nonatomic,strong) UITextField *textInput;

@property (nonatomic,strong) UILabel *selecteAppearLab;

@property (nonatomic,strong) NSArray <NSDictionary *>*clickInvocationArr;

@property (nonatomic) ProjectInputViewStyle inputStyle;

@property (nonatomic,strong) NSNumber *isShowHorizontalLine;

@property (nonatomic,strong) NSNumber *isShowArrow;

@property (nonatomic,assign) NSInteger row;

@property (nonatomic,copy) HelperIntergeFlagInvocation clickRowsInvocation;

- (void)createUI;

- (void)clickSendCertify;

- (void)addCertifyInvocation:(NSDictionary *)dic;

@end

NS_ASSUME_NONNULL_END
