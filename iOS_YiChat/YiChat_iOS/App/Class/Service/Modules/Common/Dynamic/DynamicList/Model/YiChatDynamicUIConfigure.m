//
//  YiChatDynamicUIConfigure.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatDynamicUIConfigure.h"
#import "ZFChatResourceHelper.h"

@interface YiChatDynamicUIConfigure ()


@end

static YiChatDynamicUIConfigure *uiconfigure = nil;

@implementation YiChatDynamicUIConfigure

+ (id)initialUIConfigure{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        uiconfigure = [[self alloc] init];
        
        [uiconfigure uiConfigureInitial];
    });
    
    return uiconfigure;
}

- (void)uiConfigureInitial{
    
    _dynamicUserIconSize = CGSizeMake(35.0, 35.0);
    _contentBlank = PROJECT_SIZE_NAV_BLANK;
    
    _dynamicContentFont = PROJECT_TEXT_FONT_COMMON(15);
    _dynamicContentColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    
    _dynamicUserFont = PROJECT_TEXT_FONT_COMMON(15);
    _dynamicUserColor = PROJECT_COLOR_APPMAINCOLOR;
    
    _dynamicPraiseFont = PROJECT_TEXT_FONT_COMMON(13);
    _dynamicPraiseColor = PROJECT_COLOR_APPMAINCOLOR;
    
    _dynamicCommitFont = PROJECT_TEXT_FONT_COMMON(13);
    _dynamicCommitColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    
    _dynamicCommitUserNickFont = _dynamicCommitFont;
    _dynamicCommitUserNickColor = PROJECT_COLOR_APPMAINCOLOR;
    
    _dynamicTimeFont =  PROJECT_TEXT_FONT_COMMON(13);
    _dynamicTimeColor = PROJECT_COLOR_TEXTGRAY;
    
    _dynamicDeleteFont = PROJECT_TEXT_FONT_COMMON(13);
    _dynamicDeleteColor = PROJECT_COLOR_TEXTGRAY;
    
    _dynamicUserSelfIconSize = CGSizeMake(60.0, 60.0);
    _dynamicUserSelfNickFont = PROJECT_TEXT_FONT_COMMON(15);
    _dynamicUserSelfNickColor = [UIColor whiteColor];
    _dynamicUserSelfNickSize = CGSizeMake(200.0, 20.0);
    
    //评论点赞提醒
    _dynamicRemindSize = CGSizeMake(160.0, 40.0);
    
    _dynamicBackGroundImgSize = CGSizeMake(PROJECT_SIZE_WIDTH,PROJECT_SIZE_WIDTH);
    
    //+ _dynamicRemindSize.height
    _dynamicHeaderSize = CGSizeMake(_dynamicBackGroundImgSize.width, _dynamicBackGroundImgSize.height + _dynamicUserSelfIconSize.height / 2 + _contentBlank * 2);
    
    //点击出现评论，点赞
    _dynamicToolClickIcon = [UIImage imageNamed:@"AlbumOperateMore@3x.png"];
    CGFloat w = 25.0;
    CGFloat h = 25.0;
    if(_dynamicToolClickIcon){
        h = [ProjectHelper helper_GetWidthOrHeightIntoScale:_dynamicToolClickIcon.size.width / _dynamicToolClickIcon.size.height width:w height:0];
    }
    _dynamicToolClickSize = CGSizeMake(w, h);
    
     w = 25.0;
     h = 25.0;
    
    _dynamicLikeClickIcon = [UIImage imageNamed:@"AlbumLike@3x.png"];
    if(_dynamicLikeClickIcon){
        h = [ProjectHelper helper_GetWidthOrHeightIntoScale:_dynamicLikeClickIcon.size.width / _dynamicLikeClickIcon.size.height width:w height:0];
    }
    _dynamicLikeIconSize = CGSizeMake(w,h);
    
    _dynamicCommitClickIcon = [UIImage imageNamed:@"AlbumComment@3x.png"];
    if(_dynamicCommitClickIcon){
        h = [ProjectHelper helper_GetWidthOrHeightIntoScale:_dynamicCommitClickIcon.size.width / _dynamicCommitClickIcon.size.height width:w height:0];
    }
    _dynamicCommitIconSize = CGSizeMake(w, h);
    _dynamicLikeIcon = [UIImage imageNamed:@"Like@3x.png"];
    
    _dynamicPraiseClickSize = CGSizeMake(40.0, 20.0);
    _dynamicCommitClickSize = CGSizeMake(40.0, 20.0);
    
    _dynamicDeleteBtnSize = CGSizeMake(30.0, 20.0);
    _numOfLineIcons = 3;
    _maxIconsAppear = 9;
    
    _userPlaceHolderIcon = [UIImage imageNamed:PROJECT_ICON_USERDEFAULT];
    _videoPlayIcon = [UIImage imageNamed:@"news_chat_video@3x.png"];
    
    _videoPlayIconSize = CGSizeMake(30.0, 30.0);
    
    _userIconRect = CGRectMake(_contentBlank, _contentBlank, _dynamicUserIconSize.width, _dynamicUserIconSize.height);
    _contentMaxSize = PROJECT_SIZE_WIDTH - _userIconRect.origin.x - _userIconRect.size.width - _contentBlank * 2;
    
    _userNickSize = CGSizeMake(_contentMaxSize,20.0);
    
    _dynamicToolBarSize = CGSizeMake(_contentMaxSize, 30.0);
    
    _imagesInterBlank = 5.0;
    CGFloat blank = (_numOfLineIcons - 1) * _imagesInterBlank;
    CGFloat maxW = _contentMaxSize * 0.9;
    
    _singleImageSize = CGSizeMake((maxW - blank) / _numOfLineIcons, (maxW - blank) / _numOfLineIcons);
    
    _videoSize = CGSizeMake(_contentMaxSize * 0.6, _contentMaxSize * 0.6);
    
    _praiseMaxSize = _contentMaxSize - 10.0;
    _commitMaxSize = _contentMaxSize - 10.0;
    
}

- (CGRect)getTextMessageRectWithText:(NSAttributedString *)str{
    if(str && [str isKindOfClass:[NSAttributedString class]]){
        NSAttributedString *string = str;
        NSInteger maxW = _contentMaxSize;
        UILabel *lab = [[UILabel alloc] init];
        lab.font = _dynamicContentFont;
        lab.attributedText = string;
        lab.numberOfLines = 0;
        CGSize size = [lab sizeThatFits:CGSizeMake(maxW,MAXFLOAT)];
        
        CGRect rect = CGRectZero;
        
        if(size.width < maxW){
            rect =  CGRectMake(0, 0, size.width, size.height);
        }
        else{
            rect =  CGRectMake(0, 0, maxW, size.height);
        }
        
        return rect;
        
    }
    return CGRectZero;
}

- (CGRect)getTextMessageRectWithText:(NSAttributedString *)str withMaxSize:(CGSize)maxSize font:(UIFont *)font{
    if(str && [str isKindOfClass:[NSAttributedString class]]){
        NSAttributedString *string = str;
        UILabel *lab = [[UILabel alloc] init];
        lab.font = font;
        lab.attributedText = string;
        lab.numberOfLines = 0;
        CGSize size = [lab sizeThatFits:maxSize];
        
        CGRect rect = CGRectZero;
        
        if(size.width < maxSize.width){
            rect =  CGRectMake(0, 0, size.width, size.height);
        }
        else{
            rect =  CGRectMake(0, 0, maxSize.width, size.height);
        }
        
        return rect;
        
    }
    return CGRectZero;
}



- (NSAttributedString *)tranlateStringToAttributedString:(NSString *)string font:(UIFont *)font{
    
    if(string == nil || ![string isKindOfClass:[NSString class]] || !font || ![font isKindOfClass:[UIFont class]]){
        return nil;
    }
    if(string.length < 4 ){
        return [[NSAttributedString alloc] initWithString:string];
    }
    NSMutableAttributedString *response = [[NSMutableAttributedString alloc] init];
    
    NSArray *emojiArr = [ZFChatResourceHelper ZFResourceHelperGetChatEmojiTTextArr];
    
    for (int i = 0; i<string.length; i++) {
        NSString *read = [string substringWithRange:NSMakeRange(i, 1)];
        
        if([read isEqualToString:@"["]){
            for (int j = i + 1; j<string.length; j++) {
                NSString *readNext = [string substringWithRange:NSMakeRange(j, 1)];
                
                if([readNext isEqualToString:@"]"]){
                    NSString *emoji = [string substringWithRange:NSMakeRange(i, (j - i) + 1)];
                    
                    BOOL isHas = NO;
                    
                    for (int k = 0; k<emojiArr.count; k++) {
                        if([emojiArr[k] isEqualToString:emoji]){
                            isHas = YES;
                            NSTextAttachment *attach = [[NSTextAttachment alloc] init];
                            attach.bounds = CGRectMake(0,-2, 25, 25);
                            attach.image = [self getDefaultEmojiIntoEmojiString:emojiArr[k]];
                            [response appendAttributedString:[NSAttributedString attributedStringWithAttachment:attach]];
                            break;
                        }
                    }
                    if(isHas == YES){
                        i = j;
                        break;
                    }
                    else{
                        NSString *append = [string substringWithRange:NSMakeRange(i, j - i)];
                        [response appendAttributedString:[self getAttributeStringWithText:append UIfont:font]];
                        i = j;
                        break;
                    }
                }
                else if([readNext isEqualToString:@"["]){
                    
                    [response appendAttributedString:[self getAttributeStringWithText:read UIfont:font]];
                    break;
                }
                else if(j == string.length - 1){
                    
                    NSString *append = [string substringWithRange:NSMakeRange(i, (j - i) + 1)];
                    
                    
                    [response appendAttributedString:[self getAttributeStringWithText:append UIfont:font]];
                    
                    i = j;
                    break;
                }
            }
            
        }
        else{
            [response appendAttributedString:[self getAttributeStringWithText:read UIfont:font]];
        }
    }
    
    NSMutableParagraphStyle *parag=[[NSMutableParagraphStyle alloc] init];
    parag.lineSpacing = 2.0;
    parag.lineBreakMode = NSLineBreakByWordWrapping;
    [response addAttribute:NSParagraphStyleAttributeName value:parag range:NSMakeRange(0, response.length)];
    return response;
}

- (NSMutableAttributedString *)getAttributeStringWithText:(NSString *)text font:(CGFloat)font{
    NSMutableAttributedString *readAttribute = [[NSMutableAttributedString alloc] initWithString:text];
    [readAttribute addAttribute: NSFontAttributeName value: [UIFont systemFontOfSize:font]                                                      range: NSMakeRange(0, readAttribute.length)];
    return readAttribute;
}

- (NSMutableAttributedString *)getAttributeStringWithText:(NSString *)text UIfont:(UIFont *)font{
    if(text && [text isKindOfClass:[NSString class]] && font && [font isKindOfClass:[UIFont class]]){
        NSMutableAttributedString *readAttribute = [[NSMutableAttributedString alloc] initWithString:text];
        [readAttribute addAttribute: NSFontAttributeName value:font                                                      range: NSMakeRange(0, readAttribute.length)];
        return readAttribute;
    }
    return nil;
}

//将文本与表情分离
- (NSDictionary *)feltEmojiTextString:(NSString *)text{
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:0];
    NSMutableArray *existEmojiArr = [NSMutableArray arrayWithCapacity:0];
    
    NSArray *emojiArr = @[];
    
    for (int i =0 ; i<emojiArr.count; i++) {
        NSString *tempEmoji = emojiArr[i];
        for (int j = 0; j<text.length; j++) {
            NSString *compare = nil;
            if(text.length >= (j + tempEmoji.length)){
                compare = [text substringWithRange:NSMakeRange(j, tempEmoji.length)];
            }
            else{
                break;
            }
            
            if([compare isEqualToString:tempEmoji]){
                [existEmojiArr addObject:compare];
                j += (tempEmoji.length - 1);
                
            }
        }
    }
    for (int i =0 ; i<emojiArr.count; i++) {
        NSArray *arr = [text componentsSeparatedByString:emojiArr[i]];
        text= [arr componentsJoinedByString:@""];
    }
    if(text == nil){
        text = @"";
    }
    return @{@"text":text,@"emoji":existEmojiArr};
}


- (UIImage *)getDefaultEmojiIntoEmojiString:(NSString *)emojiStr{

    NSArray *defaultEmojiTextArr = [ZFChatResourceHelper ZFResourceHelperGetChatEmojiTTextArr];
    NSArray *defaultEmojiArr = [ZFChatResourceHelper ZFResourceHelperGetChatEmojiArr];
    for (int i = 0; i<defaultEmojiTextArr.count; i++) {
        if([defaultEmojiTextArr[i] isEqualToString:emojiStr]){

            if(i <= (defaultEmojiArr.count - 1)){
                return defaultEmojiArr[i];
            }
        }
    }
    return nil;
}
@end
