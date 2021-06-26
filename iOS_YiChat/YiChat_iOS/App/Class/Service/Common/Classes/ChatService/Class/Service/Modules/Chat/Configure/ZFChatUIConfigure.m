//
//  ZFChatUIConfigure.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/9.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatUIConfigure.h"
#import "ZFChatGlobal.h"
#import "ZFChatResourceHelper.h"

static ZFChatUIConfigure *uiConfigure = nil;

@interface ZFChatUIConfigure ()

@end

@implementation ZFChatUIConfigure

+ (id)initialChatUIConfigure{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        uiConfigure = [[self alloc] init];
        
        [uiConfigure uiConfigureInitial];
    });
    
    return uiConfigure;
}

- (void)uiConfigureInitial{
    _timeAppearColor = PROJECT_COLOR_TEXTGRAY;
    
    _sendMSGBackColor = PROJECT_COLOR_APPMAINCOLOR;
    _receiveMSGBackColor = [UIColor whiteColor];
    
    _sendMSGTextColor = [UIColor whiteColor];
    _receiveMSGTextColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    _nickTextColor = PROJECT_COLOR_TEXTGRAY;
    
    _sendVoiceLabTextColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    _receiveVoceLabTextColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    
    _nickTextFont = PROJECT_TEXT_FONT_COMMON(12.0);
    _msgFont = PROJECT_TEXT_FONT_COMMON(14.0);
    _msgPersonNameFont = PROJECT_TEXT_FONT_COMMON(12.0);
    _headerTextFont = PROJECT_TEXT_FONT_COMMON(10.0);
    _videoDurationFont = PROJECT_TEXT_FONT_COMMON(10.0);
    
    _messageIconW = 40.0f;
    _messageBlank = PROJECT_SIZE_NAV_BLANK;
    _messageInterBlank = 12.0f;
    
    _messageBackMaxW = PROJECT_SIZE_WIDTH * 0.7 - _messageIconW - _messageBlank * 2;
    _messageNickSize = CGSizeMake(_messageBackMaxW / 2, _messageIconW / 2);
    _messageTextMaxW = _messageBackMaxW - _messageInterBlank * 2;
    _messageMinCellH = 30.0;
    _photoMessageMinW = _messageBackMaxW * 0.1;
    _photoMessageMaxW = _messageBackMaxW * 0.6;
    
    _sendIndicatorSize = CGSizeMake(30.0, 30.0);
    _sendFailImageSize = CGSizeMake(30.0, 30.0);
    
    
    _messageImageLoadErrorSize = CGSizeMake(_messageBackMaxW * 0.6, _messageBackMaxW * 0.6);
    _messageImageLoadingSize = CGSizeMake(_messageBackMaxW * 0.6, _messageBackMaxW * 0.6);
    
    _videoMessageMinW = _messageBackMaxW * 0.1;
    _videoMessageMaxW = _messageBackMaxW * 0.6;
    _messageVideoLoadErrorSize = CGSizeMake(30.0, 30.0);
    _messageVideoLoadingSize = CGSizeMake(30.0, 30.0);
    _messageVideoImageSize = CGSizeMake(_videoMessageMaxW, _videoMessageMaxW * 1.5);
    
    _mesaageVoiceMinW = _messageBackMaxW * 0.4;
    _mesaageVoiceH = 40.0;
    _messageVoiceIconSize = CGSizeMake(20.0, 20.0);
    _messageVoiceInterBlank = 10.0;
    _messageLocationSize = CGSizeMake(_messageBackMaxW * 0.6, _messageBackMaxW * 0.6);
    _messageFileSize = CGSizeMake(_messageBackMaxW * 0.6, _messageBackMaxW * 0.6);
    
    _commonCMDMSGFont = PROJECT_TEXT_FONT_COMMON(12.0);
    _commonCMDMSGTextColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    
    _mesaagePersonCardH = 90;
    _mesaagePersonCardW = 235;
    _mesaagePersonCardBlank = 10.0;
    _mesaagePersonCardIconSize = CGSizeMake(50.0, 50.0);
    
    NSArray *leftArr = @[@"voice_right_0.png",@"voice_right_1.png",@"voice_right_2.png"];
    NSMutableArray *arrTmpLeft = [NSMutableArray arrayWithCapacity:0];
    for (NSString *iconName in leftArr) {
        UIImage *icon = [UIImage imageNamed:iconName];
        if(icon){
            [arrTmpLeft addObject:icon];
        }
    }
    _voiceLeftPlayIcons = arrTmpLeft;
    
    NSArray *rightArr = @[@"voice_left_0.png",@"voice_left_1.png",@"voice_left_2.png"];
    NSMutableArray *arrTmpRight = [NSMutableArray arrayWithCapacity:0];
    for (NSString *iconName in rightArr) {
        UIImage *icon = [UIImage imageNamed:iconName];
        if(icon){
            [arrTmpRight addObject:icon];
        }
    }
    _videoIcon = [UIImage imageNamed:@"news_chat_video@3x.png"];
    _imageLoadErrorIcon = [UIImage imageNamed:PROJECT_ICON_LOADEEROR];
    _videoDurationColor = [UIColor whiteColor];
    _videoIconSize = CGSizeMake(30.0, 30.0);
    _videoDurationSize = CGSizeMake(50.0, 20.0);
    _voiceRightPlayIcons = arrTmpRight;
    
    _messageCommonCMDH = 30.0;
    _messageCommonHeaderH = 30.0;
    _messageCommonFooterH = 0.0001f;
    
    _redPackgeTitleSize = 0.0;
    _redPackgeDesSize = 65.0;
    _redPackgeDownSize = 20.0;
    _redPackgeWidth = 240.0;
    _redPakgeIcon = [UIImage imageNamed:@"chat_redPackge.png"];
    _redPakgeDesFont = PROJECT_TEXT_FONT_COMMON(15.0);
    _redPakgeTitleFont = PROJECT_TEXT_FONT_COMMON(12.0);
    
    _redPakgeDesColor = [UIColor whiteColor];
    _redPakgeTitleColor = PROJECT_COLOR_TEXTGRAY;
    
    _commonGroupRoleIcon = [UIImage imageNamed:@""];
    _groupOwnerIcon = [UIImage imageNamed:@"group_owner_icon.png"];
    _groupManagerIcon = [UIImage imageNamed:@"group_manager_icon.png"];
}

- (UIImage *)getGroupChatUserRoleWithPower:(NSInteger)power{
    if(power == 0){
        return _commonGroupRoleIcon;
    }
    else if(power == 1){
        return _groupManagerIcon;
    }
    else if(power == 2){
        return _groupOwnerIcon;
    }
    return _commonGroupRoleIcon;
}

- (CGRect)getTextMessageRectWithText:(NSAttributedString *)str{
    if(str && [str isKindOfClass:[NSAttributedString class]]){
        NSAttributedString *string = str;
        NSInteger maxW = _messageTextMaxW;
        UILabel *lab = [[UILabel alloc] init];
        lab.font = _msgFont;
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

- (CGRect)getCommonCMDMessageRectWithText:(NSAttributedString *)str{
    if(str && [str isKindOfClass:[NSAttributedString class]]){
        NSAttributedString *string = str;
        NSInteger maxW = _messageTextMaxW;
        UILabel *lab = [[UILabel alloc] init];
        lab.font = _commonCMDMSGFont;
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

- (CGFloat)getVoiceMessageWidthWithDuration:(NSInteger)duration{
    CGFloat maxW = self.messageBackMaxW;
    CGFloat durationW = (maxW - _mesaageVoiceMinW) / 60.0 * duration + _mesaageVoiceMinW;
    if(durationW < _mesaageVoiceMinW){
        return _mesaageVoiceMinW;
    }
    else{
        if(durationW > maxW){
            return maxW;
        }
        else{
            return durationW;
        }
    }
}

- (UIImage *)getVoiceRightAppearceIcon{
    if(_voiceRightPlayIcons.count != 0){
        return _voiceRightPlayIcons.lastObject;
    }
    return nil;
}

- (NSArray <UIImage *>*)getVoiceRightPlayAppearceIcons{
    return _voiceRightPlayIcons;
}

- (UIImage *)getVoiceLeftAppearceIcon{
    if(_voiceLeftPlayIcons.count != 0){
        return _voiceLeftPlayIcons.lastObject;
    }
    return nil;
}

- (NSArray <UIImage *>*)getVoiceLeftPlayAppearceIcons{
    return _voiceLeftPlayIcons;
}

- (NSString *)getVideoMessageAppearDurationWithDuration:(CGFloat)duration{
    if(duration == 0){
        return nil;
    }
    else{
        NSInteger minue = duration / 60.0;
        if(minue * 60 == duration){
            return [NSString stringWithFormat:@"%ld:00",minue];
        }
        else if(minue * 60 < duration){
            NSInteger second = duration - minue * 60;
            if(second < 10){
                return [NSString stringWithFormat:@"%ld:0%.0f",minue,duration - minue * 60];
            }
            else{
                 return [NSString stringWithFormat:@"%ld:%.0f",minue,duration - minue * 60];
            }
        }
        else if(minue * 60 > duration){
            return [NSString stringWithFormat:@"%ld:00",minue];
        }
        return nil;
    }
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

- (NSArray *)getWebsitesWithString:(NSString *)string
{
    NSError *error;
    //    NSString *regulaStr = @"\\bhttps?://[a-zA-Z0-9\\-.]+(?::(\\d+))?(?:(?:/[a-zA-Z0-9\\-._?,'+\\&%$=~*!():@\\\\]*)+)?";
    NSString *regulaStr = @"((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";
    NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:regulaStr options:NSRegularExpressionCaseInsensitive error:&error];
    NSArray *arrayOfAllMatches = [regex matchesInString:string options:0 range:NSMakeRange(0, [string length])];
    NSMutableArray *result = [NSMutableArray array];
    for (NSTextCheckingResult *match in arrayOfAllMatches)
    {
        NSString *substringForMatch = [string substringWithRange:match.range];
        NSLog(@"%@",substringForMatch);
        [result addObject:substringForMatch];
    }
    return (NSArray *)result;
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
