//
//  YiChatCollectionCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatCollectionCell.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import "YiChatCollectionEntity.h"

@interface YiChatCollectionCell ()
{
    //type == 0 文本消息 type == 1 图片消息 type == 2 语音消息
    NSInteger _type;
}

@property (nonatomic,strong) UILabel *name;
@property (nonatomic,strong) UILabel *time;

@property (nonatomic,strong) UILabel *textContent;
@property (nonatomic,strong) UIImageView *imageContent;

@property (nonatomic,strong) UIImageView *voiceContent;
@property (nonatomic,strong) UIButton *voicePlayBtn;

@property (nonatomic,strong) NSString *sourceUrl;

@property (nonatomic,strong) UIView *line;

@property (nonatomic,strong) NSArray *voiceAnimateImages;

@property (nonatomic,assign) BOOL isPlay;


@end

@implementation YiChatCollectionCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier type:(NSInteger)type{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        _type = type;
        
        [self makeUI];
    }
    return self;
}

- (void)dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)makeUI{
    [self makeCommonUI];
    
    if(_type == 0){
        [self makeUIForText];
    }
    else if(_type == 1){
        [self makeUIForImage];
    }
    else if(_type == 2){
        _voiceAnimateImages =  @[[UIImage imageNamed:@"audio_receive_03"],[UIImage imageNamed:@"audio_receive_02"], [UIImage imageNamed:@"audio_receive_01"]];
        [self makeUIForVoice];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(voiceReadyToPlay:) name:@"voiceReadyToPlay" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(voicePlayPause:) name:@"voicePlayPause" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(voicePlayFinish:) name:@"voicePlayFinish" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(voicePlayError:) name:@"voicePlayError" object:nil];
    }
    
    [self.contentView addSubview:self.line];
}

- (NSString *)getNotifyResourceUrlWithNotify:(NSNotification *)notify{
    id obj = [notify object];
    if([obj isKindOfClass:[NSDictionary class]]){
        return obj[@"index"];
    }
    else{
        return nil;
    }
}

//play
- (void)voiceReadyToPlay:(NSNotification *)notify{
    NSString *index = [self getNotifyResourceUrlWithNotify:notify];
    if(index != nil){
        if([index integerValue] == self.index.row){
            dispatch_async(dispatch_get_main_queue(), ^{
                _isPlay = YES;
                _voiceContent.image = nil;
                _voiceContent.animationImages = _voiceAnimateImages;
                _voiceContent.animationDuration = 0.8;
                [_voiceContent startAnimating];
            });
        }
    }
}

//cancle
- (void)voicePlayPause:(NSNotification *)notify{
    
    NSString *index = [self getNotifyResourceUrlWithNotify:notify];
    if(index != nil){
        if([index integerValue] == self.index.row){
            dispatch_async(dispatch_get_main_queue(), ^{
                _isPlay = NO;
                [_voiceContent stopAnimating];
                _voiceContent.image = _voiceAnimateImages.firstObject;
            });
        }
    }
}

//play over
- (void)voicePlayFinish:(NSNotification *)notify{
    NSString *index = [self getNotifyResourceUrlWithNotify:notify];
    if(index != nil){
        if([index integerValue] == self.index.row){
            dispatch_async(dispatch_get_main_queue(), ^{
                _isPlay = NO;
                [_voiceContent stopAnimating];
                _voiceContent.image = _voiceAnimateImages.firstObject;
            });
        }
    }
}


- (void)makeCommonUI{
    _name = [[UILabel alloc] init];
    [self.contentView addSubview:_name];
    _name.font = [UIFont systemFontOfSize:12];
    _name.textAlignment = NSTextAlignmentLeft;
    _name.textColor = [UIColor grayColor];
    
    _time = [[UILabel alloc] init];
    [self.contentView addSubview:_time];
    _time.font = [UIFont systemFontOfSize:12];
    _time.textAlignment = NSTextAlignmentLeft;
    _time.textColor = [UIColor grayColor];
    
    
}

- (UIView *)line{
    if(!_line){
        _line = [[UIView alloc] init];
        _line.backgroundColor = [UIColor blackColor];
        _line.alpha = 0.3;
        
    }
    return _line;
}

- (UIButton *)voicePlayBtn{
    if(!_voicePlayBtn){
        _voicePlayBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [self.contentView addSubview:_voicePlayBtn];
        [_voicePlayBtn addTarget:self action:@selector(voicePlayBtnMethod:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _voicePlayBtn;
}

- (void)voicePlayBtnMethod:(UIButton *)btn{
    _isPlay = !_isPlay;
    if(_isPlay == NO){
        if(self.voiceResourceStopPlay){
            self.voiceResourceStopPlay(self.sourceUrl,self.index);
        }
    }
    else{
        if(self.voiceResourceStartPlay){
            self.voiceResourceStartPlay(self.sourceUrl,self.index);
        }
    }
}

- (void)makeUIForText{
    _textContent = [[UILabel alloc] init];
    _textContent.numberOfLines = 0;
    [self.contentView addSubview:_textContent];
    _textContent.font = [UIFont systemFontOfSize:14.0];
    _textContent.textAlignment = NSTextAlignmentLeft;
    _textContent.textColor = [UIColor blackColor];
}

- (void)makeUIForVoice{
    _voiceContent = [[UIImageView alloc] init];
    [self.contentView addSubview:_voiceContent];
    _voiceContent.backgroundColor = [UIColor groupTableViewBackgroundColor];
}

- (void)makeUIForImage{
    _imageContent = [[UIImageView alloc] init];
    [self.contentView addSubview:_imageContent];
}

- (void)dic:(YiChatCollectionEntity *)dic cellSize:(CGSize)size sourceSize:(CGSize)sourceSize{
    if(_type == 0){
        NSString *text = dic.text;
        
        _textContent.frame = CGRectMake(10.0, 10.0,sourceSize.width , sourceSize.height);
        _textContent.text = text;
        _textContent.font = dic.font;
        
    }
    else if(_type == 1){
        _imageContent.frame = CGRectMake(10.0, 10.0, sourceSize.width, sourceSize.height);
        [_imageContent sd_setImageWithURL:[NSURL URLWithString:dic.url]];
    }
    else if(_type == 2){
        
        _voiceContent.frame = CGRectMake(10.0, 10.0, sourceSize.width, sourceSize.height);
        _sourceUrl = dic.url;
        _voiceContent.image = _voiceAnimateImages.firstObject;
        self.voicePlayBtn.frame = _voiceContent.frame;
        
        _isPlay = NO;
        
        if(self.voiceResourceStopPlay){
            [_voiceContent stopAnimating];
            _voiceContent.image = _voiceAnimateImages.firstObject;
            
            self.voiceResourceStopPlay(dic.url,self.index);
        }
    }
    
    _name.frame = CGRectMake(10.0, size.height - 26.0, dic.nameSize.width, 20.0);
    _name.text = dic.userIdBe_nickName;
    
    _time.text = dic.time;
    _time.frame = CGRectMake(_name.frame.origin.x + _name.frame.size.width + 10.0, size.height - 26.0, size.width - (_name.frame.origin.x + _name.frame.size.width + 10.0) - 10.0, 20.0);
    
    _name.font = dic.userIdBe_nickNameFont;
    _time.font = dic.timeFont;
    
    self.line.frame = CGRectMake(10, size.height - 0.5, size.width - 20.0, 0.5);
    [self.contentView bringSubviewToFront:_line];
}


- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
