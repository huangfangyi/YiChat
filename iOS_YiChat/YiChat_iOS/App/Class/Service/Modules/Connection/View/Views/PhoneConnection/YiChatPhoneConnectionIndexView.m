//
//  YiChatPhoneConnectionIndexView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/28.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatPhoneConnectionIndexView.h"
#import "ProjectClickView.h"
#import "ServiceGlobalDef.h"
@interface  YiChatPhoneConnectionIndexView ()

@property (nonatomic,strong) NSArray *charactersArr;

@property (nonatomic,weak) UIView *bgView;

@end


#define YiChatPhoneConnectionIndexView_Blank 3.0f
#define YiChatPhoneConnectionIndexView_TextFont PROJECT_TEXT_FONT_COMMON(13)
#define YiChatPhoneConnectionIndexView_CommonWidth 20.0f
@implementation YiChatPhoneConnectionIndexView

- (id)initWithData:(NSArray *)charactersStrArr bgView:(UIView *)bgView{
    CGFloat h = [[self class] getIndexViewHeightWithCharacters:charactersStrArr];
    
    self = [super initWithFrame:CGRectMake(bgView.frame.size.width - YiChatPhoneConnectionIndexView_CommonWidth - YiChatPhoneConnectionIndexView_Blank, bgView.frame.size.height / 2 - h / 2, YiChatPhoneConnectionIndexView_CommonWidth, h)];
    if(self){
        
        [bgView addSubview:self];
        _bgView = bgView;
        _charactersArr = charactersStrArr;
        [self makeUI];
        
    }
    return self;
}

- (void)makeUI{
    CGFloat itemH = [[self class] getIndexViewItemHeight];
    CGFloat blank = YiChatPhoneConnectionIndexView_Blank;
    NSString *title = nil;
    
    WS(weakSelf);
    for (int i = 0; i < _charactersArr.count; i ++) {
        
        if([_charactersArr[i] isKindOfClass:[NSString class]]){
            title = _charactersArr[i];
        }
        else{
            title = nil;
        }
        
        ProjectClickView *click = [ProjectClickView createClickViewWithFrame:CGRectMake(0, blank + i * (blank + itemH), self.frame.size.width, itemH) title:title type:-1];
        click.identify = [NSString stringWithFormat:@"%d",i];
        click.lab.font = YiChatPhoneConnectionIndexView_TextFont;
        click.clickInvocation = ^(NSString * _Nonnull identify) {
            if(weakSelf.yichatIndexViewClick){
                weakSelf.yichatIndexViewClick(identify.integerValue);
            }
        };
        [self addSubview:click];
    }
}

- (void)updateUIWithData:(NSArray *)characters{
    if([characters isKindOfClass:[NSArray class]]){
        _charactersArr = characters;
        
        CGFloat h = [self getIndexViewSize].height;
        
        self.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y + self.frame.size.height / 2 - h / 2, self.frame.size.width, h);
        
        for (id tmp in self.subviews) {
            [tmp removeFromSuperview];
        }
        
        [self makeUI];
        
        if(_bgView){
            [_bgView bringSubviewToFront:self];
        }
    }
}

- (CGSize)getIndexViewSize{
    
    CGFloat h = [[self class] getIndexViewHeightWithCharacters:_charactersArr];
    
    return CGSizeMake(self.frame.size.width,h);
}

+ (CGFloat)getIndexViewItemHeight{
    CGRect rect = [ProjectHelper helper_getFontSizeWithString:@"A" useSetFont:YiChatPhoneConnectionIndexView_TextFont withWidth:YiChatPhoneConnectionIndexView_CommonWidth andHeight:MAXFLOAT];
    
    CGFloat h = rect.size.height;
    
    return h;
}

+ (CGFloat)getIndexViewHeightWithCharacters:(NSArray *)character{
    CGFloat h = character.count * [self getIndexViewItemHeight] + (character.count + 1) * YiChatPhoneConnectionIndexView_Blank;
    return h;
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
