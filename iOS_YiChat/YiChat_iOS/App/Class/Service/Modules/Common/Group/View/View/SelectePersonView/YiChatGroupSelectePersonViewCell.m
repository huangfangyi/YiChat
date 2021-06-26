//
//  YiChatGroupSelectePersonViewCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupSelectePersonViewCell.h"
#import "ServiceGlobalDef.h"
#import "YiChatUserModel.h"
#import <UIImageView+WebCache.h>
@interface YiChatGroupSelectePersonViewCell ()

@property (nonatomic,strong) UIImageView *icon;

@property (nonatomic,assign) NSIndexPath  *index;

@end

@implementation YiChatGroupSelectePersonViewCell

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {

        
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    _icon = [UIImageView new];
    _icon.frame = CGRectMake(0, 0, self.contentView.frame.size.width, self.contentView.frame.size.height);
    _icon.alpha = 1;
    _icon.layer.cornerRadius = 5.0;
    
    [self.contentView addSubview:_icon];
    self.contentView.layer.cornerRadius = 10.0;
    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.frame = _icon.frame;
    [self.contentView addSubview:btn];
    [btn addTarget:self action:@selector(btnMethod:) forControlEvents:UIControlEventTouchUpInside];
}

- (void)btnMethod:(UIButton *)btn{
    if(self.yiChatGroupSelecteCellClick){
        self.yiChatGroupSelecteCellClick(self.index);
    }
}

- (void)setModel:(YiChatUserModel *)model{
    if(model && [model isKindOfClass:[YiChatUserModel class]]){
        _model = model;
        _icon.frame = CGRectMake(0, 0, self.contentView.frame.size.width, self.contentView.frame.size.height);
        
        NSString *url = [ProjectHelper helper_getSDWebImageLoadUrlWithUrl:model.avatar];
        
        [_icon sd_setImageWithURL:[NSURL URLWithString:url] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT] completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
            if(!error && image){
                if(self && self.model && [self.model isKindOfClass:[YiChatUserModel class]]){
                    NSString *currentCellUrl = [ProjectHelper helper_getSDWebImageLoadUrlWithUrl:self.model.avatar];
                    
                    if(currentCellUrl && [currentCellUrl isKindOfClass:[NSString class]]){
                        if(imageURL && [imageURL isKindOfClass:[NSURL class]]){
                            if(currentCellUrl.length > 0){
                                if([imageURL.absoluteString isEqualToString:currentCellUrl]){
                                    _icon.image = image;
                                    return ;
                                }
                            }
                        }
                    }
                }
            }
            _icon.image = [UIImage imageNamed:PROJECT_ICON_USERDEFAULT];
        }];
    }
}


- (void)setModelWithModel:(YiChatUserModel *)model index:(NSIndexPath *)index{
    
    if(model && [model isKindOfClass:[YiChatUserModel class]]){
        self.model = model;
    }
    
    if(index && [index isKindOfClass:[NSIndexPath class]]){
        self.index = index;
    }
    
}
@end
