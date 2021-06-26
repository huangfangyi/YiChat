//
//  YiChatQRCodeVisitCardView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/28.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatQRCodeVisitCardView.h"
#import "YiChatQrcodeView.h"

@interface YiChatQRCodeVisitCardView ()

@property (nonatomic,strong) YiChatQrcodeView *visitCard;
@property (nonatomic,assign) BOOL isSave;

@end


@implementation YiChatQRCodeVisitCardView

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        
        self.addSubView(self.visitCard);
        
        UILongPressGestureRecognizer *longPress = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPressMethod:)];
        longPress.minimumPressDuration = 0.5;
        [self addGestureRecognizer:longPress];
        
        
    }
    return self;
}

- (void)longPressMethod:(UIButton *)btn{
    if(!_isSave){
         _isSave = YES;
        WS(weakSelf);
        [ProjectUIHelper projectActionSheetWithListArr:@[@"保存二维码"] click:^(NSInteger row) {
            _isSave = NO;
            if(row == 0){
                [[TZImageManager manager] savePhotoWithImage:weakSelf.visitCard.qrcode completion:^(PHAsset *asset, NSError *error) {
                }];
            }
        }];
    }
}

- (YiChatQrcodeView *)visitCard{
    if(!_visitCard){
        _visitCard = [[YiChatQrcodeView alloc] initWithFrame:self.controlUIBackView.bounds];
        _visitCard.backgroundColor = [UIColor whiteColor];
        _visitCard.layer.cornerRadius = 10.0;
        _visitCard.clipsToBounds = YES;
    }
    return _visitCard;
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
