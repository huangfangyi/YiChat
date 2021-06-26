//
//  YiChatDynamicHeader.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatDynamicHeader.h"
#import "ServiceGlobalDef.h"
#import "YiChatDynamicUIConfigure.h"
#import "YiChatDynamicDataSource.h"

#import <UIImageView+WebCache.h>

@interface YiChatDynamicHeader ()<UIGestureRecognizerDelegate>

@property (nonatomic,assign) YiChatDynamicUIConfigure *uiConfigure;

@property (nonatomic,strong) UIImageView *userIcon;

@property (nonatomic,strong) UIButton *userIconBtn;

@property (nonatomic,strong) UILabel *userNickName;

@property (nonatomic,strong) UILabel *content;

@property (nonatomic,strong) UILabel *timeLab;

@property (nonatomic,strong) NSArray *images;

@property (nonatomic,strong) NSArray *imageBtns;

@property (nonatomic,strong) UIImageView *videoIcon;
@property (nonatomic,strong) UIButton *videoBtn;
@property (nonatomic,strong) UIImageView *videoTypeIcon;

@property (nonatomic,strong) UIView *header_line;

@property (nonatomic,strong) UIButton *deleteBtn;

@property (nonatomic,strong) UIButton *toolClickBtn;

@property (nonatomic,strong) UIView *praiseBack;
@property (nonatomic,strong) UILabel *praiseLab;

//1文本 2 图片 3视频
@property (nonatomic) NSInteger type;

@end

@implementation YiChatDynamicHeader

+ (id)initialWithReuseIdentifier:(NSString *)reuseIdentifier type:(NSNumber *)type{
    return [[self alloc] initWithReuseIdentifier:reuseIdentifier type:type.integerValue];
}


- (id)initWithReuseIdentifier:(NSString *)reuseIdentifier type:(NSInteger)type{
    self = [super initWithReuseIdentifier:reuseIdentifier];
    if(self){
        _type = type;
        _uiConfigure = [YiChatDynamicUIConfigure initialUIConfigure];
        self.contentView.backgroundColor = [UIColor whiteColor];
    }
    return self;
}

- (void)createUI{
    [self makeCommonUI];
    if(_type == 1){
        
    }
    else if(_type == 2){
        [self makeImagesUI];
    }
    else if(_type == 3){
        [self makeVideoUI];
    }
}

- (void)makeCommonUI{
    CGFloat contentBlank = _uiConfigure.contentBlank;
    
    CGFloat x = contentBlank;
    CGFloat y = contentBlank;
    CGFloat w = _uiConfigure.dynamicUserIconSize.width;
    CGFloat h = _uiConfigure.dynamicUserIconSize.height;
    
    UIImageView *icon = [[UIImageView alloc] initWithFrame:CGRectMake(x, y, w, h)];
    [self.contentView addSubview:icon];
    _userIcon = icon;
    icon.layer.cornerRadius = 8.0;
    icon.clipsToBounds = YES;
    icon.userInteractionEnabled = NO;
    
    _userIconBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.contentView addSubview:_userIconBtn];
    [_userIconBtn addTarget:self action:@selector(clickUserIconBtn:) forControlEvents:UIControlEventTouchUpInside];
    
    x = icon.frame.origin.x + icon.frame.size.width + contentBlank;
    y = icon.frame.origin.y;
    w = _uiConfigure.userNickSize.width;
    h = _uiConfigure.userNickSize.height;
    
    UILabel *nick = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(x,y,w,h) andfont:_uiConfigure.dynamicUserFont textColor:_uiConfigure.dynamicUserColor textAlignment:NSTextAlignmentLeft];
    [self.contentView addSubview:nick];
    _userNickName = nick;
    
    x = icon.frame.origin.x + icon.frame.size.width + contentBlank;
    y = icon.frame.origin.y + icon.frame.size.height / 2;
    w = 0;
    h = 0;
    
    UILabel *content = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(x,y,w,h) andfont:_uiConfigure.dynamicContentFont textColor:_uiConfigure.dynamicContentColor textAlignment:NSTextAlignmentLeft];
    content.numberOfLines = 0;
    [self.contentView addSubview:content];
    _content = content;
    
    _timeLab = [UILabel new];
    _timeLab.font = _uiConfigure.dynamicTimeFont;
    _timeLab.textColor = _uiConfigure.dynamicTimeColor;
    _timeLab.textAlignment = NSTextAlignmentLeft;
    [self.contentView addSubview:_timeLab];
    
    _header_line = [ProjectHelper helper_factoryMakeHorizontalLineWithPoint:CGPointMake(_userIcon.frame.origin.x + _userIcon.frame.size.width, 0) width:_uiConfigure.contentMaxSize];
    [self.contentView addSubview:_header_line];
    
    _deleteBtn = [ProjectHelper helper_factoryMakeButtonWithFrame:CGRectMake(0, 0, _uiConfigure.dynamicDeleteBtnSize.width, _uiConfigure.dynamicDeleteBtnSize.height) andBtnType:UIButtonTypeRoundedRect];
    [_deleteBtn setTitle:@"删除" forState:UIControlStateNormal];
    [_deleteBtn setTitleColor:_uiConfigure.dynamicDeleteColor forState:UIControlStateNormal];
    _deleteBtn.titleLabel.font = _uiConfigure.dynamicDeleteFont;
    [self.contentView addSubview:_deleteBtn];
    [_deleteBtn addTarget:self action:@selector(deleteBtnMethod:) forControlEvents:UIControlEventTouchUpInside];
    
    _toolClickBtn = [ProjectHelper helper_factoryMakeButtonWithFrame:CGRectMake(0, 0, _uiConfigure.dynamicToolClickSize.width, _uiConfigure.dynamicToolClickSize.height) andBtnType:UIButtonTypeCustom];
    [self.contentView addSubview:_toolClickBtn];
    [_toolClickBtn addTarget:self action:@selector(commitLikeBarClickMethod:) forControlEvents:UIControlEventTouchUpInside];
    [_toolClickBtn setImage:_uiConfigure.dynamicToolClickIcon forState:UIControlStateNormal];
    
    _praiseBack = [UIView new];
    _praiseBack.backgroundColor = [UIColor groupTableViewBackgroundColor];
    _praiseBack.layer.cornerRadius = 5.0;
    _praiseBack.clipsToBounds = YES;
    [self.contentView addSubview:_praiseBack];
    
    _praiseLab = [UILabel new];
    _praiseLab.numberOfLines = 0;
    _praiseLab.textAlignment = NSTextAlignmentLeft;
    _praiseLab.font = _uiConfigure.dynamicPraiseFont;
    _praiseLab.textColor = _uiConfigure.dynamicPraiseColor;
    [_praiseBack addSubview:_praiseLab];
    
    //单击的手势
    UITapGestureRecognizer *tapRecognize = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(handleTap)];
    tapRecognize.numberOfTapsRequired = 1;
    tapRecognize.delegate = self;
    [tapRecognize setEnabled :YES];
    [tapRecognize delaysTouchesBegan];
    [tapRecognize cancelsTouchesInView];
     
    [self addGestureRecognizer:tapRecognize];
}

-(void)handleTap{
    if(self.YiChatDynamicHeaderHideOrReport){
        
        CGRect rect = [self convertRect:_toolClickBtn.frame toView:self.controlVC.view];
        
        self.YiChatDynamicHeaderHideOrReport(self.model,CGPointMake(rect.origin.x, rect.origin.y));
    }
    
}

- (void)clickUserIconBtn:(UIButton *)btn{
    if(self.YiChatDynamicHeaderClickUserIcon){
        self.YiChatDynamicHeaderClickUserIcon(self.model);
    }
}

- (void)commitLikeBarClickMethod:(UIButton *)btn{
    if(self.YiChatDynamicHeaderClickCommitLikeBar){
        
        CGRect rect = [self convertRect:_toolClickBtn.frame toView:self.controlVC.view];
        
        self.YiChatDynamicHeaderClickCommitLikeBar(self.model,CGPointMake(rect.origin.x, rect.origin.y));
    }
}


- (void)deleteBtnMethod:(UIButton *)btn{
    if(self.YiChatDynamicHeaderClickDelete){
        self.YiChatDynamicHeaderClickDelete(_model);
    }
}

- (void)makeImagesUI{
    NSMutableArray *icons = [NSMutableArray arrayWithCapacity:0];
    NSMutableArray *btnIcons = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < _uiConfigure.maxIconsAppear; i ++) {
        UIImageView *icon = [UIImageView new];
        [self.contentView addSubview:icon];
        icon.backgroundColor = [UIColor whiteColor];
        [icons addObject:icon];
        
        UIButton *btn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:CGRectZero target:self method:@selector(dynamicBtnIconClick:)];
        [self.contentView addSubview:btn];
        
        [btnIcons addObject:btn];
        
    }
    _imageBtns = btnIcons;
    _images = icons;
}

- (void)dynamicBtnIconClick:(UIButton *)btn{
    WS(weakSelf);
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < _images.count; i ++) {
        UIImageView *icon = _images[i];
        if(icon && [icon isKindOfClass:[UIImageView class]]){
            if(icon.hidden == NO){
                [arr addObject:icon];
            }
        }
    }
    
    [_imageBtns enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if(obj == btn){
            if(weakSelf.model.urlIcons && [weakSelf.model.urlIcons isKindOfClass:[NSArray class]]){
                if(arr.count == weakSelf.model.urlIcons.count){
                    
                    [weakSelf showImgsWithCurrentIndex:idx images:arr imageUrls:weakSelf.model.urlIcons];
                }
            }
        }
    }];
}

- (void)showImgsWithCurrentIndex:(NSInteger)index images:(NSArray *)images imageUrls:(NSArray *)imagesUrls{
       [ProjectUIHelper helper_showImageBrowseWithDataSouce:imagesUrls withSourceObjs:images currentIndex:index];
}

- (void)showVideoWithCurrentIndex:(NSInteger)index videos:(NSArray *)videos videosUrls:(NSArray *)videosUrls{
    [ProjectUIHelper helper_showVideoBrowseWithDataSouce:videosUrls withSourceObjs:videos currentIndex:index];
}

- (void)makeVideoUI{
    _videoIcon = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectZero andImg:nil];
    [self.contentView addSubview:_videoIcon];
    
    _videoTypeIcon = [UIImageView new];
    [self.contentView addSubview:_videoTypeIcon];
    _videoTypeIcon.image = _uiConfigure.videoPlayIcon;

    _videoBtn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:CGRectZero target:self method:@selector(dynamicBtnVideoClick:)];
    [self.contentView addSubview:_videoBtn];
}

- (void)dynamicBtnVideoClick:(UIButton *)btn{
    if(btn == _videoBtn){
        if(_model.urlVideos && [_model.urlVideos isKindOfClass:[NSArray class]]){
            if(_model.urlVideos.count == 1){
                [self showVideoWithCurrentIndex:0 videos:@[_videoIcon] videosUrls:_model.urlVideos];
            }
        }
    }
}

- (void)setModel:(YiChatDynamicDataSource *)model{
    if(model && [model isKindOfClass:[YiChatDynamicDataSource class]]){
        _model = model;
        [self dealUserInfoWithModel:model];
        
        [self dealContentWithModel:model];
        
        if(_type == 2){
            [self dealContentIconsWithModel:model];
        }
        else if(_type == 3){
             [self dealContentVideosWithModel:model];
        }
        
        [self dealToolBarWithModel:model];
        
        [self dealPraiseWithModel:model];
    }
}

- (void)updateType:(NSInteger)type{
    _type = type;
}

- (void)dealUserInfoWithModel:(YiChatDynamicDataSource *)model{
    if(model && [model isKindOfClass:[YiChatDynamicDataSource class]]){
        NSString *url = [model getUserIconUrl];
        UIImage *placeHolder = _uiConfigure.userPlaceHolderIcon;
        
        if(url && [url isKindOfClass:[NSString class]]){
            [_userIcon sd_setImageWithURL:[NSURL URLWithString:url] placeholderImage:placeHolder completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
                
                if(image && !error){
                    NSString *loadUserUrl = imageURL.absoluteString;
                    NSString *modelUserUrl = nil;
                    if(model && [model isKindOfClass:[YiChatDynamicDataSource class]]){
                        modelUserUrl = [model getUserIconUrl];
                    }
                    if(loadUserUrl && [loadUserUrl isKindOfClass:[NSString class]]){
                        if(modelUserUrl && [modelUserUrl isKindOfClass:[NSString class]]){
                            _userIcon.image = image;
                            return ;
                        }
                    }
                    _userIcon.image = placeHolder;
                }
                else{
                    _userIcon.image = placeHolder;
                }
            }];
        }
        _userIconBtn.frame = _userIcon.frame;
        _userNickName.text = [model getUserNickName];
    }
}

- (void)dealContentWithModel:(YiChatDynamicDataSource *)model{
    if(model && [model isKindOfClass:[YiChatDynamicDataSource class]]){
        if(_userIcon && _userNickName){
            _content.frame = CGRectMake(_userIcon.frame.origin.x + _userIcon.frame.size.width + _uiConfigure.contentBlank, _userNickName.frame.size.height + _userNickName.frame.origin.y + _uiConfigure.contentBlank, _uiConfigure.contentMaxSize, model.showContentRect.size.height);
            _content.attributedText = model.showContentStr;
        }
    }
}

- (void)dealContentIconsWithModel:(YiChatDynamicDataSource *)model{
    if(model && [model isKindOfClass:[YiChatDynamicDataSource class]]){
        CGFloat y = model.iconsVideosBeginY;
        CGFloat blank = _uiConfigure.imagesInterBlank;
        CGFloat w = _uiConfigure.singleImageSize.width;
        CGFloat h = _uiConfigure.singleImageSize.height;
        
        CGRect rect;
        NSArray *iconsUrl = model.urlThumbIcons;
        
        for (int i = 0; i < _uiConfigure.maxIconsAppear; i ++) {
            
            if(_images.count - 1 >= i){
                UIImageView *icon = _images[i];
               
                BOOL isHideen = NO;
                if(iconsUrl.count - 1 >= i){
                    NSString *url = iconsUrl[i];
                    
                    rect = CGRectMake(_userNickName.frame.origin.x + (i % _uiConfigure.numOfLineIcons) * (w + blank), y + (i / _uiConfigure.numOfLineIcons) * (h + blank) ,w , h);
                    
                    icon.frame = rect;
                    
                    if(url && [url isKindOfClass:[NSString class]]){
                        [icon sd_setImageWithURL:[NSURL URLWithString:url] completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
                            
                        }];
                    }
                    isHideen = NO;
                }
                else{
                    isHideen = YES;
                }
                
                if(_imageBtns.count - 1 >= i){
                    UIButton *btn = _imageBtns[i];
                    btn.frame = icon.frame;
                    
                    btn.hidden = isHideen;
                    icon.hidden = isHideen;
                }
            }
        }
    }
}

- (void)dealContentVideosWithModel:(YiChatDynamicDataSource *)model{
    if(model && [model isKindOfClass:[YiChatDynamicDataSource class]]){
        CGFloat y = model.iconsVideosBeginY;
        CGFloat blank = _uiConfigure.contentBlank;
        CGFloat w = _uiConfigure.videoSize.width;
        CGFloat h = _uiConfigure.videoSize.height;
        CGFloat x = _userNickName.frame.origin.x;
        
        _videoIcon.frame = CGRectMake(x, y, w, h);
        NSString *videoThumb = model.urlVideoThumbs;
        if(videoThumb && [videoThumb isKindOfClass:[NSString class]]){
            if(videoThumb.length > 0){
                [_videoIcon sd_setImageWithURL:[NSURL URLWithString:videoThumb] completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
                    
                }];
            }
        }
        _videoTypeIcon.frame = CGRectMake(_videoIcon.frame.origin.x + _videoIcon.frame.size.width / 2 - _uiConfigure.videoPlayIconSize.width / 2 , _videoIcon.frame.origin.y + _videoIcon.frame.size.height / 2 - _uiConfigure.videoPlayIconSize.height / 2, _uiConfigure.videoPlayIconSize.width, _uiConfigure.videoPlayIconSize.height);
        
        _videoBtn.frame = _videoIcon.frame;
    }
}

- (void)dealToolBarWithModel:(YiChatDynamicDataSource *)model{
    if(model && [model isKindOfClass:[YiChatDynamicDataSource class]]){
        
        CGFloat y = model.downBarBeginY;
        CGFloat h = _uiConfigure.dynamicToolBarSize.height;
        
        _timeLab.frame = CGRectMake(_userNickName.frame.origin.x,y,model.timeSizeW, h);
        _timeLab.text =  [model getDynamciTime];
        
        NSString *userId = [model getUserIdStr];
        
        _deleteBtn.hidden = YES;
        
        if(userId && [userId isKindOfClass:[NSString class]]){
            NSString *currentUserId = YiChatUserInfo_UserIdStr;
            if(currentUserId && [currentUserId isKindOfClass:[NSString class]]){
                if([currentUserId isEqualToString:userId]){
                    _deleteBtn.hidden = NO;
                }
            }
           
        }
        _deleteBtn.frame = CGRectMake(_timeLab.frame.origin.x + _timeLab.frame.size.width + _uiConfigure.contentBlank, y, _uiConfigure.dynamicDeleteBtnSize.width, _timeLab.frame.size.height);
        
        _toolClickBtn.frame = CGRectMake(PROJECT_SIZE_WIDTH - _uiConfigure.contentBlank - _uiConfigure.dynamicToolClickSize.width, y + h / 2 - _uiConfigure.dynamicToolClickSize.height / 2, _uiConfigure.dynamicToolClickSize.width, _uiConfigure.dynamicToolClickSize.height);
    }
}

- (void)dealPraiseWithModel:(YiChatDynamicDataSource *)model{
    
    CGFloat h = [model getHeaderH];
    _header_line.frame = CGRectMake(_userNickName.frame.origin.x,h - 1, _uiConfigure.contentMaxSize, 1);
    
    if(model && [model isKindOfClass:[YiChatDynamicDataSource class]]){
        if(model.praiseCount > 0){
            _praiseBack.hidden = NO;
            
            CGRect rect = model.showPraiseStrRect;
            CGFloat y = model.praiseBeginY;
            
            _praiseBack.frame = CGRectMake(_userNickName.frame.origin.x,y , _uiConfigure.contentMaxSize,rect.size.height + 5.0);
            _praiseLab.frame = CGRectMake((_uiConfigure.contentMaxSize - _uiConfigure.praiseMaxSize) / 2, _praiseBack.frame.size.height / 2 - rect.size.height / 2, rect.size.width, rect.size.height);
            _praiseLab.attributedText = model.showPraiseListStr;
            return;
        }
    }
    _praiseBack.hidden = YES;
    
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
