//
//  YRAddressPicker.h
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/18.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class YRAddressEntity;
typedef void(^YRAddressPickerClickWillDisspear)();

typedef void (^YRAddressPickerClickDidSelecteAddress)(NSArray <YRAddressEntity *>*address);

@interface YRAddressPicker : UIView

@property (nonatomic,copy)      YRAddressPickerClickWillDisspear dissapearClick;
@property (nonatomic,copy)      YRAddressPickerClickDidSelecteAddress didSelecteAddress;
@property (nonatomic,strong)    NSMutableArray <NSIndexPath *>*currentSelecteComponetList;
@property (nonatomic,strong)    NSArray <NSArray *>*dataSource;
@property (nonatomic,strong)    UIPickerView *picker;

- (void)makeUI;

- (void)sureMethod:(UIButton *)btn;

@end

@interface YRAddressEntity : NSObject

@property (nonatomic,strong) NSString *addressName;
@property (nonatomic,strong) NSString *addressCode;

+ (instancetype)creaateWithAddressName:(NSString *)addressName
                           addressCode:(NSString *)addressCode;

@end
NS_ASSUME_NONNULL_END
