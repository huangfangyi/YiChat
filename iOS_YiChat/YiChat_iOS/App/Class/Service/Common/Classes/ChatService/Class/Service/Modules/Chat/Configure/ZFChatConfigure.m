//
//  ZFChatConfigure.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/9.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatConfigure.h"
#import "ZFChatManage.h"
#import "ProjectConfigure.h"
#import "ProjectTranslateHelper.h"
#import "ZFChatStorageManager.h"
#import "ProjectStorageApis.h"
#import "ZFChatMessageHelper.h"
#import "ZFChatHelper.h"
#import "ZFChatResourceHelper.h"
@interface ZFChatConfigure ()

@end


#define  ZFChatConfigure_AppearTimeDuration 60 * 5

@implementation ZFChatConfigure

- (id)initWithHTMsg:(HTMessage *)msg{
    self = [super init];
    if(self){
        _uiConfigure = [ZFChatUIConfigure initialChatUIConfigure];
        _groupRole = 0;
        self.msg = msg;
    }
    return self;
}

- (id)initWithHTCMDMsg:(HTCmdMessage *)cmdMsg{
    self = [super init];
    if(self){
        _uiConfigure = [ZFChatUIConfigure initialChatUIConfigure];
        self.msg = cmdMsg;
    }
    return self;
}

- (void)setMsg:(id)msg{
    if(msg){
        _msg = msg;
        _messageType = ZFMessageTypeUnknown;
        [self initialMSGConfigure];
    }
}

- (void)initialMSGConfigure{
    id msg = self.msg;
    if(msg && [msg isKindOfClass:[HTMessage class]]){
        HTMessage *message = (HTMessage *)msg;
        
        _cellH = -1;
        _footerH = -1;
        _headerH = -1;
        _messageAction = 0;
        _httpText = nil;
        _isGEmojiText = NO;
        _gEmojiFileName = nil;
        
        ZFChatManage *manage = [ZFChatManage defaultManager];
        NSUInteger extAction = - 1;
        if(message.ext && [message.ext isKindOfClass:[NSDictionary class]]){
            if([message.ext.allKeys containsObject:@"action"]){
                extAction = [message.ext[@"action"] integerValue];
                _messageAction =extAction;
                _messageType = [[ZFChatManage defaultManager] getMessageTypeWithAction:extAction];
            }
            else{
                if(message.body && [message.body isKindOfClass:[HTMessageBody class]]){
                    _messageType = [manage getMessageTypeWithMessageBodyTypeStr:[NSString stringWithFormat:@"%ld",message.msgType]];
                }
            }
        }
        else{
            if(message.body && [message.body isKindOfClass:[HTMessageBody class]]){
                _messageType = [manage getMessageTypeWithMessageBodyTypeStr:[NSString stringWithFormat:@"%ld",message.msgType]];
            }
        }

        
        if(message.chatType && [message.chatType isKindOfClass:[NSString class]]){
            _chatType = [manage getMessageChatTypeWithChatTypeStr:message.chatType];
        }
        
        _isSender = NO;
        if(message.from && [message.from isKindOfClass:[NSString class]]){
            if([message.from isEqualToString:YiChatUserInfo_UserIdStr]){
                _isSender = YES;
            }
        }
        
        BOOL isSender = _isSender;
        
        if(message.body && [message.body isKindOfClass:[HTMessageBody class]]){
            if(_messageType == ZFMessageTypeText){
               
                if(message.body.content && [message.body.content isKindOfClass:[NSString class]]){
                    
                    
                    NSArray *web = [_uiConfigure getWebsitesWithString:message.body.content];
                    if(web){
                        if(web.count != 0){
                            NSMutableArray *urlArr = [NSMutableArray arrayWithCapacity:0];
                            NSMutableArray *rangeArr = [NSMutableArray arrayWithCapacity:0];
                            for (int i = 0; i < web.count; i ++) {
                                NSString *url = web[i];
                                
                                for (int d = 0; d < message.body.content.length; d ++) {
                                    NSInteger lenth = url.length;
                                    
                                    if(message.body.content.length >= lenth + d){
                                        NSString *sub = [message.body.content substringWithRange:NSMakeRange(d, lenth)];
                                        
                                        if([sub isEqualToString:url]){
                                            
                                            
                                            
                                            BOOL isHas = NO;
                                            for (int j = 0; j < rangeArr.count; j++) {
                                                NSValue *value = rangeArr[j];
                                                if(value.rangeValue.location == d && value.rangeValue.length == sub.length){
                                                    isHas = YES;
                                                    break;
                                                }
                                                if(value.rangeValue.location == d){
                                                    isHas = YES;
                                                    break;
                                                }
                                            }
                                            
                                            if(isHas == NO){
                                                [urlArr addObject:url];
                                                [rangeArr addObject:[NSValue valueWithRange:NSMakeRange(d, sub.length)]];
                                                break;
                                            }
                                            else{
                                                continue;
                                            }
                                        }
                                    }
                                }
                                
                            }
                            
                            NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
                            for (int i = 0; i < urlArr.count; i ++) {
                                if(i <= rangeArr.count - 1){
                                    [arr addObject:@{@"url":urlArr[i],@"range":rangeArr[i]}];
                                }
                            }
                            
                            self.httpText = arr;
                        }
                    }
                    
                    if([message.body.content containsString:@"gemoji_"]){
                        BOOL isHas = NO;
                        NSArray *emoji = [ZFChatResourceHelper ZFResourceHelperGetChatGIFEmojiArr];
                        for (int i = 1; i <= emoji.count; i ++) {
                            NSString *tmp = [NSString stringWithFormat:@"[%@%ld]",@"gemoji_",i];
                            if([message.body.content isEqualToString:tmp]){
                                self.gEmojiFileName = [NSString stringWithFormat:@"%@%ld",@"gemoji_",i];
                                isHas = YES;
                                break;
                            }
                        }
                        
                        if(isHas){
                            _isGEmojiText = YES;
                            _messageType = ZFMessageTypePhoto;
                            _showImageSize = CGSizeMake(100, 100);
                        }
                        else{
                            _isGEmojiText = NO;
                            self.chatShowText = [self.uiConfigure tranlateStringToAttributedString:message.body.content font:_uiConfigure.msgFont];
                        }
                        
                    
                    }
                    else{
                        _isGEmojiText = NO;
                        self.chatShowText = [self.uiConfigure tranlateStringToAttributedString:message.body.content font:_uiConfigure.msgFont];
                    }
                    
                }
            }
            else if(_messageType == ZFMessageTypeRedPackageReceiveOrSend){
                NSDictionary *ext = [self getMessageBodyExt];
                if(ext && [ext isKindOfClass:[NSDictionary class]]){
                    NSString *redPackgeId = ext[@"envId"];
                    NSString *redPackgeName = ext[@"envName"];
                    NSString *redPackgeDes = ext[@"envMsg"];
                    NSString *redPackgeSendNick = ext[@"nick"];
                    NSString *redPackgeTitle = nil;
                    if(message.body && [message.body isKindOfClass:[HTMessageBody class]]){
                        if(message.body.content && [message.body.content isKindOfClass:[NSString class]]){
                            redPackgeTitle = message.body.content;
                        }
                    }
                    ZFChatRedPackageEntity *entity = [[ZFChatRedPackageEntity alloc] init];
                    entity.redPackageId = redPackgeId;
                    entity.redPackageName = redPackgeName;
                    entity.redPackageDes = redPackgeDes;
                    entity.title = redPackgeTitle;
                    entity.sendPersonNickName = redPackgeSendNick;
                    self.packageModel = entity;
                }
                
            }
            else if(_messageType == ZFMessageTypeRedPackageGet){
                
                if(message.body.content && [message.body.content isKindOfClass:[NSString class]]){
                    NSString *content = [ZFChatMessageHelper getRedPackageContentMessageWithMsg:message];
                    
                    self.chatShowText = [self.uiConfigure tranlateStringToAttributedString:content font:_uiConfigure.commonCMDMSGFont];
                }
            }
            else if(_messageType == ZFMessageTypePersonCard){
                if(message.body.content && [message.body.content isKindOfClass:[NSString class]]){
                    _showPersonCardTitle = message.body.content;
                }
                if(message.ext && [message.ext isKindOfClass:[NSDictionary class]]){
                    //cardUserId":
                    //cardUserNick":
                    //cardUserAvatar":
                    id cardUserId = message.ext[@"cardUserId"];
                    if(cardUserId && [cardUserId isKindOfClass:[NSString class]]){
                         _showPersonCardUserId = cardUserId;
                    }
                    if(cardUserId && [cardUserId isKindOfClass:[NSNumber class]]){
                        _showPersonCardUserId = [NSString stringWithFormat:@"%ld",[cardUserId integerValue]];
                    }
                   
                    _showPersonCardNick = message.ext[@"cardUserNick"];
                    _showPersonCardAvtar = message.ext[@"cardUserAvatar"];
                }
                
                
            }
            else if(_messageType == ZFMessageTypePhoto){
                
                NSString *sizeStr = nil;
                
                if(message.body.size && [message.body.size isKindOfClass:[NSString class]]){
                    if(message.body.size.length > 0){
                        sizeStr = message.body.size;
                    }
                    else{
                        sizeStr = [NSString stringWithFormat:@"%f,%f",_uiConfigure.messageImageLoadErrorSize.width,_uiConfigure.messageImageLoadErrorSize.height];
                    }
                }
                else{
                    sizeStr = [NSString stringWithFormat:@"%f,%f",_uiConfigure.messageImageLoadErrorSize.width,_uiConfigure.messageImageLoadErrorSize.height];
                }
                
                NSArray *sizeArray = [sizeStr componentsSeparatedByString:@","];
                
                CGSize imageSize = CGSizeZero;
                
                if(sizeArray.count >= 2){
                    imageSize =  CGSizeMake([sizeArray[0] floatValue] , [sizeArray[1] floatValue]);
                    
                    if(imageSize.width != 0 && imageSize.height != 0){
                        imageSize = [self dealShowPhotMessageSizeWithLimiw:_uiConfigure.photoMessageMinW limitMaxW:_uiConfigure.photoMessageMaxW originSize:imageSize];
                    }
                    else{
                        imageSize = _uiConfigure.messageImageLoadErrorSize;
                    }
                }
                else{
                    imageSize = _uiConfigure.messageImageLoadErrorSize;
                }
                
                _showImageSize = imageSize;
            }
            else if(_messageType == ZFMessageTypeVoice){
                _isPlayVoice = NO;
                _voiceIsPlayed = NO;
            }
            else if(_messageType == ZFMessageTypeVideo){
                
                NSString *sizeStr = nil;
                if(message.body.size && [message.body.size isKindOfClass:[NSString class]]){
                    if(message.body.size.length > 0){
                        sizeStr = message.body.size;
                    }
                    else{
                        sizeStr = [NSString stringWithFormat:@"%f,%f",_uiConfigure.messageImageLoadErrorSize.width,_uiConfigure.messageImageLoadErrorSize.height];
                    }
                }
                else{
                    sizeStr = [NSString stringWithFormat:@"%f,%f",_uiConfigure.messageVideoImageSize.width,_uiConfigure.messageVideoImageSize.height];
                }
                
                
                NSArray *sizeArray = [sizeStr componentsSeparatedByString:@","];
                
                CGSize imageSize = CGSizeZero;
                
                if(sizeArray.count >= 2){
                    imageSize =  CGSizeMake([sizeArray[0] floatValue] , [sizeArray[1] floatValue]);
                    
                    if(imageSize.width != 0 && imageSize.height != 0){
                        imageSize = [self dealShowVideoMessageSizeWithLimiw:_uiConfigure.videoMessageMinW limitMaxW:_uiConfigure.videoMessageMaxW originSize:imageSize];
                    }
                    else{
                        imageSize = _uiConfigure.messageVideoImageSize;
                    }
                }
                else{
                    imageSize = _uiConfigure.messageVideoImageSize;
                }
                
                _showVideoSize = imageSize;
            }
            else if(_messageType == ZFMessageTypeGroupMsgNotify){
                if(message.body && [message.body isKindOfClass:[HTMessageBody class]]){
                    if(message.body.content && [message.body.content isKindOfClass:[NSString class]]){
                        self.chatShowText = [self.uiConfigure tranlateStringToAttributedString:message.body.content font:_uiConfigure.commonCMDMSGFont];
                    }
                }
            }
            else if(_messageType == ZFMessageTypeWithdrawn){
                NSString *content = [ZFChatMessageHelper getWithDrawMessageTranslateMessageWithMsg:message groupRole:_groupRole isSender:isSender];
                if(content && [content isKindOfClass:[NSString class]]){
                    self.chatShowText = [self.uiConfigure tranlateStringToAttributedString:content font:_uiConfigure.commonCMDMSGFont];
                }
            }
        }
    }
    
    if(msg && [msg isKindOfClass:[HTCmdMessage class]]){
        HTCmdMessage *message = (HTCmdMessage *)msg;
        
        _msg = message;
        _cellH = -1;
        _footerH = -1;
        _headerH = -1;
        
        ZFChatManage *manage = [ZFChatManage defaultManager];
        
        if(message.chatType && [message.chatType isKindOfClass:[NSString class]]){
            _chatType = [manage getMessageChatTypeWithChatTypeStr:message.chatType];
        }
        NSDictionary *dic = [self getCMDMessageBody];
        NSInteger action = [ZFChatHelper getCMDMessageAction:self.msg];
        _messageType = [ZFChatHelper zfChatHeler_getMessageTypeWithAction:action];
        
        NSString *content = nil;
        if(_messageType == ZFMessageTypeGroupSilence){
            content = @"群已禁言";
        }
        else if(_messageType == ZFMessageTypeGroupCancelSilence){
            content = @"群禁言已解除";
        }
        else if(_messageType == ZFMessageTypeGroupMemberSilence){
            content = @"您已被群管理禁言";
        }
        else if(_messageType == ZFMessageTypeCancelGroupMemberSilence){
            content = @"您的禁言已解除";
        }
        else if(_messageType == ZFMessageTypeGroupSetManager || _messageType == ZFMessageTypeGroupCancelSetManager){
            if(dic && [dic isKindOfClass:[NSDictionary class]]){
                NSDictionary *data = dic[@"data"];
                if(data && [data isKindOfClass:[NSDictionary class]]){
                    NSString *userId = data[@"userId"];
                    if(userId && [userId isKindOfClass:[NSString class]]){
                        NSString *current = [ZFChatHelper zfChatHelper_getCurrentUser];
                        if(current && [current isKindOfClass:[NSString class]]){
                            if([userId isEqualToString:current]){
                                if(_messageType == ZFMessageTypeGroupSetManager){
                                    content = @"你被设置管理员";
                                }
                                else{
                                    content = @"你被取消设置管理员";
                                }
                            }
                            else{
                                NSString *nick = data[@"nick"];
                                if(nick && [nick isKindOfClass:[NSString class]]){
                                    if(_messageType == ZFMessageTypeGroupSetManager){
                                        content = [NSString stringWithFormat:@"%@%@",nick,@"被设置管理员"];
                                    }
                                    else{
                                        content = [NSString stringWithFormat:@"%@%@",nick,@"被取消管理员"];
                                    }
                                }
                            }
                        }
                        
                    }
                }
            }
        }
        if(content && [content isKindOfClass:[NSString class]]){
            self.chatShowText = [self.uiConfigure tranlateStringToAttributedString:content font:_uiConfigure.commonCMDMSGFont];
        }
    }
}

- (void)updateMSGConfire{
    [self initialMSGConfigure];
}

- (void)setLastMessageTime:(NSInteger)lastMessageTime{
    _lastMessageTime = lastMessageTime;
    
    NSInteger currentTime = [self getMessageTime];
    if(self.lastMessageTime != -1){
        if((currentTime - self.lastMessageTime) / 1000 >= ZFChatConfigure_AppearTimeDuration){
            
            _isShowHeaderTime = YES;
            
            [ProjectHelper helper_getMainThread:^{
                _timeText = [self getTimeStrContentWithTimeInterval:[self getMessageTime] / 1000];
                
                CGRect rect = [ProjectHelper helper_getFontSizeWithString:_timeText useSetFont:_uiConfigure.headerTextFont withWidth:PROJECT_SIZE_WIDTH andHeight:_uiConfigure.messageCommonHeaderH];
                self.headerTextSize = CGSizeMake(rect.size.width, _uiConfigure.messageCommonHeaderH);
                _headerH = self.headerTextSize.height;
            }];
        }
        else{
            _isShowHeaderTime = NO;
            self.headerTextSize = CGSizeMake(0.00001, 0.0001);
            _headerH = self.headerTextSize.height;
        }
    }
    else{
        _isShowHeaderTime = YES;
        
        [ProjectHelper helper_getMainThread:^{
             _timeText = [self getTimeStrContentWithTimeInterval:[self getMessageTime] / 1000];
            CGRect rect = [ProjectHelper helper_getFontSizeWithString:_timeText useSetFont:_uiConfigure.headerTextFont withWidth:PROJECT_SIZE_WIDTH andHeight:_uiConfigure.messageCommonHeaderH];
            self.headerTextSize = CGSizeMake(rect.size.width, _uiConfigure.messageCommonHeaderH);
            _headerH = self.headerTextSize.height;
        }];
    }
}

- (NSString *)getTimeStrContentWithTimeInterval:(NSInteger)timeInterval{
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc]init];
    [formatter setDateStyle:NSDateFormatterMediumStyle];
    [formatter setTimeStyle:NSDateFormatterShortStyle];
    [formatter setDateFormat:@"MM.dd HH:mm"];
    
    NSDate *current = [NSDate dateWithTimeIntervalSince1970:timeInterval];
    
   
    NSString *confromTimespStr = [formatter stringFromDate:current];
    return confromTimespStr;
    
}

- (CGSize)dealShowPhotMessageSizeWithLimiw:(CGFloat)minW limitMaxW:(CGFloat)maxW originSize:(CGSize)size{
    CGFloat w = size.width;
    CGFloat h = size.height;
    CGSize imageSize = CGSizeZero;
    
    CGFloat scale = w / h;
    if(w >= h){
        if(w <= maxW){
            imageSize = CGSizeMake(w, h);
        }
        else{
            w = maxW;
            h = [ProjectHelper helper_GetWidthOrHeightIntoScale:scale width:w height:0];
        }
        if(h < _uiConfigure.messageMinCellH){
            h = _uiConfigure.messageMinCellH;
            w = [ProjectHelper helper_GetWidthOrHeightIntoScale:scale width:0 height:h];
            if(w >= maxW){
                w = maxW;
            }
        }
        w = ceil(w);
        h = ceil(h);
        imageSize = CGSizeMake(w, h);
    }
    else{
        CGFloat scale = w / h;
        
        if(scale < 0.1){
            w = minW;
            h = maxW;
        }
        else{
            w = maxW;
            h = [ProjectHelper helper_GetWidthOrHeightIntoScale:scale width:w height:0];
        }
        
        w = ceil(w);
        h = ceil(h);
        imageSize = CGSizeMake(w, h);
    }
    
    return imageSize;
}

- (CGSize)dealShowVideoMessageSizeWithLimiw:(CGFloat)minW limitMaxW:(CGFloat)maxW originSize:(CGSize)size{
    CGFloat w = size.width;
    CGFloat h = size.height;
    CGSize imageSize = CGSizeZero;
    
    CGFloat scale = w / h;
    if(w >= h){
        if(w <= maxW){
            imageSize = CGSizeMake(w, h);
        }
        else{
            w = maxW;
            h = [ProjectHelper helper_GetWidthOrHeightIntoScale:scale width:w height:0];
        }
        if(h < _uiConfigure.messageMinCellH){
            h = _uiConfigure.messageMinCellH;
            w = [ProjectHelper helper_GetWidthOrHeightIntoScale:scale width:0 height:h];
        }
        w = ceil(w);
        h = ceil(h);
        imageSize = CGSizeMake(w, h);
    }
    else{
        CGFloat scale = w / h;
        
        if(scale < 0.1){
            w = self.uiConfigure.videoMessageMinW;
            h = [ProjectHelper helper_GetWidthOrHeightIntoScale:scale width:w height:0];
        }
        else{
            w = self.uiConfigure.videoMessageMaxW;
            h = [ProjectHelper helper_GetWidthOrHeightIntoScale:scale width:w height:0];
        }
        
        w = ceil(w);
        h = ceil(h);
        imageSize = CGSizeMake(w, h);
    }
    
    return imageSize;
}

- (void)setChatShowText:(NSAttributedString *)chatShowText{
    _chatShowText = chatShowText;
    
    [ProjectHelper helper_getMainThread:^{
        _showTextRect = [_uiConfigure getTextMessageRectWithText:_chatShowText];
    }];
}

- (CGFloat)getCellH{
    if(_cellH > -1){
        return _cellH;
    }
    CGFloat blank = _uiConfigure.messageBlank;
    
    if(self.msg && [self.msg isKindOfClass:[HTMessage class]]){
        
        CGFloat contentH = 0;
        
        if(self.messageType == ZFMessageTypeText){
            if(self.showTextRect.size.height == 0){
                _showTextRect = [_uiConfigure getTextMessageRectWithText:_chatShowText];
            }
            _contentSize = CGSizeMake(self.showTextRect.size.width + _uiConfigure.messageInterBlank * 2, self.showTextRect.size.height + _uiConfigure.messageInterBlank * 2);
        }
        else if(self.messageType == ZFMessageTypePhoto){
            
            _contentSize = CGSizeMake(_showImageSize.width, _showImageSize.height);
        }
        else if(self.messageType == ZFMessageTypeVoice){
            CGFloat duration = [self getVoiceMsgDuration];
            
            _contentSize = CGSizeMake([_uiConfigure getVoiceMessageWidthWithDuration:duration], _uiConfigure.mesaageVoiceH);
        }
        else if(self.messageType == ZFMessageTypeVideo){
              _contentSize = CGSizeMake(_showVideoSize.width, _showVideoSize.height);
        }
        else if(self.messageType == ZFMessageTypeFile){
            _contentSize = CGSizeMake(30.0, _uiConfigure.messageFileSize.height);
        }
        else if(self.messageType == ZFMessageTypeLocation){
            _contentSize = CGSizeMake(_uiConfigure.messageLocationSize.width, _uiConfigure.messageLocationSize.height);
        }
        else if(self.messageType == ZFMessageTypeGroupMsgNotify){
            if(self.showTextRect.size.height == 0){
                _showTextRect = [_uiConfigure getCommonCMDMessageRectWithText:_chatShowText];
            }
            _contentSize = CGSizeMake(self.showTextRect.size.width + _uiConfigure.messageInterBlank * 2, self.showTextRect.size.height + _uiConfigure.messageInterBlank * 2);
            
        }
        else if(self.messageType == ZFMessageTypeWithdrawn){
            if(self.showTextRect.size.height == 0){
                _showTextRect = [_uiConfigure getCommonCMDMessageRectWithText:_chatShowText];
            }
            _contentSize = CGSizeMake(self.showTextRect.size.width + _uiConfigure.messageInterBlank * 2, self.showTextRect.size.height + _uiConfigure.messageInterBlank * 2);
        }
        
        contentH = _contentSize.height;
        
        if(self.messageType == ZFMessageTypeText || self.messageType == ZFMessageTypeVoice || self.messageType == ZFMessageTypeVideo || self.messageType == ZFMessageTypeLocation || self.messageType == ZFMessageTypePhoto){
            if(_chatType == ZFChatTypeChat){
                
                //头像高度 contenth blank，
                //back origin.y在 icon.y下方一个blank 距离
                //icon.y == 0
                if(self.isSender == YES){
                    _cellH = contentH + _uiConfigure.messageInterBlank;
                }
                else{
                    if(self.chatType == ZFChatTypeChat){
                        _cellH = contentH + _uiConfigure.messageInterBlank;
                    }
                    else{
                        if(contentH < (_uiConfigure.messageIconW - blank)){
                            _cellH =  _uiConfigure.messageIconW + blank;
                        }
                        else{
                            // blank + content + down blank
                            _cellH =  contentH + blank * 2;
                        }
                    }
                }
                
            }
            else if(_chatType == ZFChatTypeGroup){
                //nick.y == 0 nick.h = icon.h / 2;
                //back origin.y在 nick.y + nick.h
                //icon.y == 0
                if(self.isSender == YES){
                    _cellH = contentH + _uiConfigure.messageInterBlank;
                }
                else{
                    if(contentH < (_uiConfigure.messageIconW / 2)){
                        _cellH =  _uiConfigure.messageIconW + blank;
                    }
                    else{
                        // name + content + down blank
                        _cellH =  _uiConfigure.messageIconW / 2 + contentH + blank;
                    }
                }
               
            }
        }
        else if(_messageType == ZFMessageTypeGroupMsgNotify){
            _cellH = _contentSize.height;
        }
        else if(_messageType == ZFMessageTypeWithdrawn){
            _cellH = _contentSize.height;
        }
        else if(_messageType == ZFMessageTypePersonCard){
            _contentSize = CGSizeMake(_uiConfigure.mesaagePersonCardW,_uiConfigure.mesaagePersonCardH);
            _cellH = _contentSize.height;
            
            CGFloat h = _uiConfigure.mesaagePersonCardH;
            CGFloat w = _uiConfigure.mesaagePersonCardW;
            _contentSize = CGSizeMake(w, h);
            
            contentH = h ;
            if(_chatType == ZFChatTypeChat){
                
                //头像高度 contenth blank，
                //back origin.y在 icon.y下方一个blank 距离
                //icon.y == 0
               
                if(contentH < (_uiConfigure.messageIconW - blank)){
                    _cellH =  _uiConfigure.messageIconW + blank;
                }
                else{
                    // blank + content + down blank
                    _cellH =  contentH + blank;
                }
            }
            else if(_chatType == ZFChatTypeGroup){
                //nick.y == 0 nick.h = icon.h / 2;
                //back origin.y在 nick.y + nick.h
                //icon.y == 0
                if(self.isSender == YES){
                    _cellH = contentH + _uiConfigure.messageInterBlank;
                }
                else{
                    if(contentH < (_uiConfigure.messageIconW / 2)){
                        _cellH =  _uiConfigure.messageIconW + blank;
                    }
                    else{
                        // name + content + down blank
                        _cellH =  _uiConfigure.messageIconW / 2 + contentH + blank;
                    }
                }
               
            }
            
        }
        else if(self.messageType == ZFMessageTypeRedPackageReceiveOrSend){
            
            CGFloat h =  _uiConfigure.redPackgeTitleSize + _uiConfigure.redPackgeDesSize + _uiConfigure.redPackgeDownSize;
            CGFloat w = _uiConfigure.redPackgeWidth;
            _contentSize = CGSizeMake(w, h);
            
            contentH = h ;
            if(_chatType == ZFChatTypeChat){
                
                //头像高度 contenth blank，
                //back origin.y在 icon.y下方一个blank 距离
                //icon.y == 0
                _cellH = contentH + _uiConfigure.messageInterBlank;
               
            }
            else if(_chatType == ZFChatTypeGroup){
                //nick.y == 0 nick.h = icon.h / 2;
                //back origin.y在 nick.y + nick.h
                //icon.y == 0
                if(_isSender == YES){
                    _cellH = contentH + _uiConfigure.messageInterBlank;
                }
                else{
                    if(contentH < (_uiConfigure.messageIconW / 2)){
                        _cellH =  _uiConfigure.messageIconW + blank;
                    }
                    else{
                        // name + content + down blank
                        _cellH =  _uiConfigure.messageIconW / 2 + contentH + blank;
                    }
                }
                
                
            }
        }
        else if(self.messageType == ZFMessageTypeRedPackageGet){
            if(self.showTextRect.size.height == 0){
                _showTextRect = [_uiConfigure getCommonCMDMessageRectWithText:_chatShowText];
            }
            _contentSize = CGSizeMake(self.showTextRect.size.width + _uiConfigure.messageInterBlank * 2, self.showTextRect.size.height + _uiConfigure.messageInterBlank * 2);
            
            _cellH = _contentSize.height;
        }
        else{
            _cellH = _uiConfigure.messageCommonCMDH;
        }
    }
    
    if(self.msg && [self.msg isKindOfClass:[HTCmdMessage class]]){
        if(self.showTextRect.size.height == 0){
            _showTextRect = [_uiConfigure getCommonCMDMessageRectWithText:_chatShowText];
        }
        _contentSize = CGSizeMake(self.showTextRect.size.width + _uiConfigure.messageInterBlank * 2, self.showTextRect.size.height + _uiConfigure.messageInterBlank * 2);
        
        _cellH = _contentSize.height;
    }
    
    if(_cellH != 0){
        return _cellH;
    }
    return 0;
}

- (CGFloat)getHeaderH{
    if(_headerH > -1){
        return _headerH;
    }
    
    if(_isShowHeaderTime){
         _headerH = _uiConfigure.messageCommonHeaderH;
    }
    else{
        _headerH = 0.0001;
    }

    return _headerH;
}

- (CGFloat)getFooterH{
    if(_footerH > -1){
        return _footerH;
    }
    return _uiConfigure.messageCommonFooterH;
}

- (void)changeMSGSendStatus:(NSInteger)sendState{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            msg.sendState = sendState;
        }
    }

}

- (NSInteger)getMSGSendStatus{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            return msg.sendState;
        }
    }
    return -1;
}

- (NSInteger)getMSGDownStatus{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            return msg.downLoadState;
        }
    }
    return -1;
}

- (NSString *)getMsgFrom{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            return msg.from;
        }
        else if([self.msg isKindOfClass:[HTCmdMessage class]]){
            HTCmdMessage *msg = self.msg;
            return msg.from;
        }
    }
    return nil;
}

- (NSString *)getMsgTo{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            return msg.to;
        }
        else if([self.msg isKindOfClass:[HTCmdMessage class]]){
            HTCmdMessage *msg = self.msg;
            return msg.to;
        }
    }
    return nil;
}

- (NSString *)getMsgId{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            return msg.msgId;
        }
        else if([self.msg isKindOfClass:[HTCmdMessage class]]){
            HTCmdMessage *msg = self.msg;
            return msg.msgId;
        }
    }
    return nil;
}

- (NSInteger)getMessageTime{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            return msg.timestamp;
        }
        else if([self.msg isKindOfClass:[HTCmdMessage class]]){
            HTCmdMessage *msg = self.msg;
            return msg.timestamp;
        }
    }
    return -1;
}

- (NSString *)getTextMessageContent{
    
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *message = self.msg;
            if(message.body && [message.body isKindOfClass:[HTMessageBody class]]){
                if(message.body.content && [message.body.content isKindOfClass:[NSString class]]){
                    return message.body.content;
                }
            }
        }
    }
    return @"";
}

- (void)setMsgTime:(NSInteger)unixtime{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            msg.timestamp = unixtime;
        }
        else if([self.msg isKindOfClass:[HTCmdMessage class]]){
            HTCmdMessage *msg = self.msg;
            msg.timestamp = unixtime;
        }
    }
}

- (CGFloat)getVoiceMsgDuration{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            return  [msg.body.audioDuration floatValue];
        }
        else if([self.msg isKindOfClass:[HTCmdMessage class]]){
            return 0;
        }
    }
    return 0;
}

- (NSString *)getPhotoThumbUrl{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            if(msg.body && [msg.body isKindOfClass:[HTMessageBody class]]){
                if(msg.body.thumbnailLocalPath && [msg.body.thumbnailLocalPath isKindOfClass:[NSString class]]){
                    
                    BOOL exist = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:msg.body.thumbnailLocalPath];
                    if(exist){
                        return msg.body.thumbnailLocalPath;
                    }
                }
                if(msg.body.thumbnailRemotePath && [msg.body.thumbnailRemotePath isKindOfClass:[NSString class]]){
                    if(_showImageSize.width > 0 && _showImageSize.height > 0){
                        return [NSString stringWithFormat:@"%@?x-oss-process=image/resize,m_fill,w_%.0f,h_%.0f",msg.body.thumbnailRemotePath,_showImageSize.width,_showImageSize.height];
                    }
                    else{
                         return msg.body.thumbnailRemotePath;
                    }
                }
                if(msg.body.remotePath && [msg.body.remotePath isKindOfClass:[NSString class]]){
                    if(_showImageSize.width > 0 && _showImageSize.height > 0){
                        return [NSString stringWithFormat:@"%@?x-oss-process=image/resize,m_fill,w_%.0f,h_%.0f",msg.body.remotePath,_showImageSize.width,_showImageSize.height];
                    }
                    else{
                        return msg.body.remotePath;
                    }
                }
            }
        }
    }
    return nil;
}


/*
 不指定w和h参数，或者w和h都指定为0，输出的图片大小为视频的真实长宽。
 单独指定w或者h参数，输出的图片大小指定的数值，另外一个没有指定的数值通过视频原始比例自动计算出来。
 同时指定w和h参数，输出图片大小为指定的数值，如果长宽比例和原始视频比不相等则强制拉伸。
*/
- (NSString *)getVideoThumbUrl{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
    
            if(msg.body && [msg.body isKindOfClass:[HTMessageBody class]]){
                if(msg.body.thumbnailLocalPath && [msg.body.thumbnailLocalPath isKindOfClass:[NSString class]]){
                    BOOL isExtst = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:msg.body.thumbnailLocalPath];
                    if(isExtst){
                        return msg.body.thumbnailLocalPath;
                    }
                }
                if(msg.body.remotePath && [msg.body.remotePath isKindOfClass:[NSString class]]){
                    NSString *url =  [NSString stringWithFormat:@"%@?x-oss-process=video/snapshot,t_0,f_jpg,w_%.0f,h_%.0f,ar_auto,m_fast",msg.body.remotePath,_showVideoSize.width,_showVideoSize.height];
                    return url;
                }
            }
        }
    }
    return nil;
}

- (CGFloat)getVideoDuration{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            if(msg.body && [msg.body isKindOfClass:[HTMessageBody class]]){
                return msg.body.videoDuration;
            }
        }
    }
    return 0;
}

- (NSString *)getLocationThumbUrl{
    return [self getLocationThumbUrl];
}

- (NSString *)getPhotoOriginUrl{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            if(msg.body && [msg.body isKindOfClass:[HTMessageBody class]]){
                if(msg.body.localPath && [msg.body.localPath isKindOfClass:[NSString class]]){
                    BOOL isExsit = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:msg.body.localPath];
                    if(isExsit){
                        return msg.body.localPath;
                    }
                }
                if(msg.body.remotePath && [msg.body.remotePath isKindOfClass:[NSString class]]){
                    return msg.body.remotePath;
                }
            }
        }
    }
    return nil;
}

- (NSString *)getVideoPlayUrl{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            if(msg.body && [msg.body isKindOfClass:[HTMessageBody class]]){
                if(msg.body.localPath && [msg.body.localPath isKindOfClass:[NSString class]]){
                    
                    BOOL isExsit = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:msg.body.localPath];
                    if(isExsit){
                        return msg.body.localPath;
                    }
                }
                if(msg.body.remotePath && [msg.body.remotePath isKindOfClass:[NSString class]]){
                    return msg.body.remotePath;
                }
            }
        }
    }
    return nil;
}


//amr
- (NSString *)getRemoteVoiceResourceLoadUrl{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            if(msg.body && [msg.body isKindOfClass:[HTMessageBody class]]){
                if(msg.body.localPath && [msg.body.localPath isKindOfClass:[NSString class]]){
                    BOOL isExsit = [ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:msg.body.localPath];
                    if(isExsit){
                        return msg.body.localPath;
                    }
                }
                if(msg.body.remotePath && [msg.body.remotePath isKindOfClass:[NSString class]]){
                    return msg.body.remotePath;
                }
            }
        }
    }
    return nil;
}

- (NSString *)getRemoteFileName{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            if(msg.body && [msg.body isKindOfClass:[HTMessageBody class]]){
                if(msg.body.fileName && [msg.body.fileName isKindOfClass:[NSString class]]){
                    
                    NSString *fileName = msg.body.fileName;
                    
                    if([fileName hasSuffix:@".amr"]){
                        NSArray *arr = [fileName componentsSeparatedByString:@".amr"];
                        if(arr.count == 2){
                            fileName = arr.firstObject;
                        }
                    }
                    else if([fileName hasSuffix:@".mp4"]){
                        NSArray *arr = [fileName componentsSeparatedByString:@".mp4"];
                        if(arr.count == 2){
                            fileName = arr.firstObject;
                        }
                    }
                    
                    return fileName;
                }
            }
        }
    }
    return [[ZFChatStorageManager sharedManager] productFileName];
}


- (void)getRemoteVoiceResourceWavLoadUrlInvocation:(void(^)(NSString *url))handle{
    NSString *url = [self getRemoteVoiceResourceLoadUrl];
    if(url && [url isKindOfClass:[NSString class]]){
        if([url hasPrefix:@"http://"]){
            //网络url
            if([url hasSuffix:@".amr"]){
                
                NSString *amrSavePath = [[ZFChatStorageManager sharedManager] zfChatStorageManager_getVoiceRecorderTranslatedItemPath];
                
                NSString *fileName = [self getRemoteFileName];
                
                NSString *wavSavePath = [[ZFChatStorageManager sharedManager] zfChatStorageManager_getVoiceRecorderItemPath];
                
                NSString *wavFullPath = [self getFullPathWithItem:wavSavePath fileName:[@"/" stringByAppendingString:fileName] extesion:@".wav"];
                
                NSString *amrFullPath = [self getFullPathWithItem:amrSavePath fileName:[@"/" stringByAppendingString:fileName] extesion:@".amr"];
                
                if(amrFullPath && [amrFullPath isKindOfClass:[NSString class]]){
                    
                    if(wavFullPath && [wavFullPath isKindOfClass:[NSString class]]){
                        if([ProjectStorageApis projectStorageApis_JudgeFileIsExistsInPath:wavFullPath]){
                            handle(wavFullPath);
                            return;
                        }
                        
                        NSData *data = [[NSData alloc] initWithContentsOfURL:[NSURL URLWithString:url]];
                        
                        [data writeToFile:amrFullPath atomically:YES];
                        
                        
                        BOOL success = [[ZFChatStorageManager sharedManager] convertAmrFile:amrFullPath toWavPath:wavFullPath];
                        
                        NSString *path = (NSString *)wavFullPath;
                        
                        if(success){
                            handle(path);
                            return;
                        }
                        
                    }
                }
                
            }
            else if([url hasSuffix:@".wav"]){
                NSData *data = [[NSData alloc] initWithContentsOfURL:[NSURL URLWithString:url]];
                NSString *fileName = [self getRemoteFileName];
                
                NSString *wavSavePath = [[ZFChatStorageManager sharedManager] zfChatStorageManager_getVoiceRecorderItemPath];
                
                NSString *wavFullPath = [self getFullPathWithItem:wavSavePath fileName:[@"/" stringByAppendingString:fileName] extesion:@".wav"];
                
                
                if(wavFullPath && [wavFullPath isKindOfClass:[NSString class]]){
                    
                    [data writeToFile:wavFullPath atomically:YES];
                    
                    handle(wavFullPath);
                    return;
                }
            }
        }
        else if([url hasSuffix:@".amr"]){
            //本地的
            NSString *amrFullPath = url;
            
            NSString *wavSavePath = [[ZFChatStorageManager sharedManager] zfChatStorageManager_getVoiceRecorderItemPath];
            
            NSString *fileName = [self getRemoteFileName];
            
            NSString *wavFullPath = [self getFullPathWithItem:wavSavePath fileName:[@"/" stringByAppendingString:fileName] extesion:@".wav"];
            
            if(wavFullPath && [wavFullPath isKindOfClass:[NSString class]]){
                BOOL success = [[ZFChatStorageManager sharedManager] convertAmrFile:amrFullPath toWavPath:wavFullPath];
                
                NSString *path = (NSString *)wavFullPath;
                if(success){
                    handle(path);
                    return;
                }
            }
        }
        else if([url hasSuffix:@".wav"]){
            //本地的
            handle(url);
            return;
        }
    }
    handle(nil);
    
}

- (void)changeVoicePlayState:(BOOL)state{
    self.isPlayVoice = state;
}

- (NSString *)getFullPathWithItem:(NSString *)item fileName:(NSString *)fileName extesion:(NSString *)fileType{
    if(item && [item isKindOfClass:[NSString class]] && fileName && [fileName isKindOfClass:[NSString class]] && fileType && [fileType isKindOfClass:[NSString class]]){
        
        return [[item stringByAppendingString:fileName] stringByAppendingString:fileType];
    }
    return nil;
}

- (NSDictionary *)getMessageBodyExt{
    if(self.msg && [self.msg isKindOfClass:[HTMessage class]]){
        HTMessage *msg = self.msg;
        if(msg.ext && [msg.ext isKindOfClass:[NSDictionary class]]){
            return msg.ext;
        }
    }
    return nil;
}

- (NSDictionary *)getCMDMessageBody
{
    if(self.msg && [self.msg isKindOfClass:[HTCmdMessage class]]){
        return [ZFChatHelper getCMDMessageBody:self.msg];
    }
    return nil;
}

- (NSDictionary *)getCMDMessageExtData{
    
    if(self.msg && [self.msg isKindOfClass:[HTCmdMessage class]]){
        return [ZFChatHelper getCmdMessageExtData:self.msg];
    }
    return nil;
    
}
    
- (NSString *)getRemoteFilePath{
    if(self.msg){
        if([self.msg isKindOfClass:[HTMessage class]]){
            HTMessage *msg = self.msg;
            if(msg.body && [msg.body isKindOfClass:[HTMessageBody class]]){
                
                if(msg.body.remotePath && [msg.body.remotePath isKindOfClass:[NSString class]]){
                    return msg.body.remotePath;
                }
            }
        }
    }
    return nil;
}
@end


@implementation ZFChatRedPackageEntity



@end
