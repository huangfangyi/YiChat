//
//  YiChatSendDynamicToolBarCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/10.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatSendDynamicToolBarCell.h"
#import "YiChatSendDynamicBarModel.h"
#import "ServiceGlobalDef.h"
#import <UIImageView+WebCache.h>

@interface YiChatSendDynamicToolBarCell ()


@property (nonatomic,strong) UIImageView *playBtn;

@property (nonatomic,strong) UIButton *canCelBtn;


@end

@implementation YiChatSendDynamicToolBarCell


- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        
        self.size = 0;
        
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    _icon = [UIImageView new];
    _icon.alpha = 1;
    _icon.layer.cornerRadius = 5.0;
    _icon.userInteractionEnabled = NO;
    [self.contentView addSubview:_icon];
    
    _playBtn  = [UIImageView new];
    [self.contentView addSubview:_playBtn];
    _playBtn.hidden = YES;
    
    _canCelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.contentView addSubview:_canCelBtn];
    [_canCelBtn addTarget:self action:@selector(cancelCellMethod:) forControlEvents:UIControlEventTouchDown];
    
}

- (void)cancelCellMethod:(UIButton *)cancel{
    if(self.YiChatSendDynamicBarDidClickCancel){
        self.YiChatSendDynamicBarDidClickCancel(_model, nil);
    }
}

- (void)setModel:(YiChatSendDynamicBarModel *)model{
    _model = model;
    _model.itemBgView = self.contentView;
    
    _icon.frame = CGRectMake(self.contentView.frame.size.width / 2 - self.size / 2, self.contentView.frame.size.height / 2 - self.size / 2, self.size, self.size);
    
    if(_model.icon){
        if(_model.icon.size.width != _model.icon.size.height){
            _icon.image = [ProjectHelper helper_getSquareIconFromImage:_model.icon];
        }
        else{
            _icon.image = _model.icon;
        }
    }
    
    if(_model.type == YiChatSendDynamicBarModelTypeVideo){
        _playBtn.hidden = NO;
        _playBtn.frame = CGRectMake(self.contentView.frame.size.width / 2 - 15.0, self.contentView.frame.size.height / 2 - 15.0, 30.0, 30.0);
        _playBtn.image = [UIImage imageNamed:@"news_dynamic_video@3x.png"];
    }
    
    if(!(model.type == YiChatSendDynamicBarModelTypeAdd)){
        _canCelBtn.hidden = NO;
        _canCelBtn.frame = CGRectMake(-15.0,-15.0, 40.0, 40.0);
        [_canCelBtn setImage:[UIImage imageNamed:@"zdy_icon_delete@3x.png"] forState:UIControlStateNormal];
    }
    else{
        _playBtn.hidden = YES;
        _canCelBtn.hidden = YES;
    }
}

@end
