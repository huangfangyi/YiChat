//
//  YiChatConversationCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/18.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatConversationCell.h"
#import "ServiceGlobalDef.h"
#import "ZFChatHelper.h"
#import "ProjectTableCell+ServiceExtension.h"
#import "YiChatUserManager.h"
#import "ZFChatUIHelper.h"
#import <UIImageView+WebCache.h>
#import "ZFChatMessageHelper.h"
#import "YiChatStorageManager.h"

@interface YiChatConversationCell ()
{
    NSInteger _type;
}

@property (nonatomic,strong) UIImageView *icon;

@property (nonatomic,strong) UILabel *title;

@property (nonatomic,strong) UILabel *content;

@property (nonatomic,strong) UILabel *timeStr;

@property (nonatomic,strong) UIView *msgNumIcon;

@property (nonatomic,strong) NSString *netIconUrl;

@end

@implementation YiChatConversationCell

+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type{
    return [[self alloc] initWithStyle:style reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth isHasDownLine:isHasDownLine type:type];
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth isHasDownLine:isHasDownLine];
    if(self){
        _type = type;
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    [self makeUIForCommon];
}

- (void)makeUIForCommon{
    _icon = [[UIImageView alloc] initWithFrame:CGRectMake(10.0, 7.0, self.sCellHeight - 14.0, self.sCellHeight - 14.0)];
    [self.contentView addSubview:_icon];
    if(_type == 0){
        _icon.layer.cornerRadius = 5.0;
        _icon.clipsToBounds = YES;
    }
    else{
        _icon.layer.cornerRadius = _icon.frame.size.height / 2;
        _icon.clipsToBounds = YES;
    }
    
    _msgNumIcon = [ProjectUIHelper projectCreateNumIconWithPosition:CGPointMake(_icon.frame.origin.x + _icon.frame.size.width, 5.0) num:0];
    [self.contentView addSubview:_msgNumIcon];
    
    CGFloat timeStrW = 80.0;
    CGFloat contentH = (self.sCellHeight - _icon.frame.origin.y * 2) / 2;
    CGFloat x = _icon.frame.origin.x + _icon.frame.size.width + 10.0;
    CGFloat w = self.sCellWidth - x - 10.0 - timeStrW - 10.0;
    
    _title = [[UILabel alloc] initWithFrame:CGRectMake(x, _icon.frame.origin.y, w, contentH)];
    [self.contentView addSubview:_title];
    _title.textAlignment = NSTextAlignmentLeft;
    _title.textColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    _title.font = PROJECT_TEXT_FONT_COMMON(14.0);
    
    _content = [[UILabel alloc] initWithFrame:CGRectMake(x,_title.frame.origin.y + _title.frame.size.height, w, contentH)];
    [self.contentView addSubview:_content];
    _content.textAlignment = NSTextAlignmentLeft;
    _content.textColor = PROJECT_COLOR_TEXTGRAY;
    _content.font = PROJECT_TEXT_FONT_COMMON(12.0);
    
    _timeStr = [[UILabel alloc] initWithFrame:CGRectMake(self.sCellWidth - 10.0 - timeStrW, _title.frame.origin.y , timeStrW, _title.frame.size.height)];
    [self.contentView addSubview:_timeStr];
    _timeStr.textAlignment = NSTextAlignmentRight;
    _timeStr.textColor = PROJECT_COLOR_TEXTGRAY;
    _timeStr.font = PROJECT_TEXT_FONT_COMMON(12.0);
}

- (void)setCellModel:(HTConversation *)cellModel{
    if(cellModel && [cellModel isKindOfClass:[HTConversation class]]){
        _cellModel = cellModel;
        
        [self dealUserInfoWithModel:cellModel];
        
        if(cellModel.lastMessage && [cellModel.lastMessage isKindOfClass:[HTMessage class]]){
            
            if(cellModel.lastMessage.timestamp > 0){
                NSDate * date = [NSDate dateWithTimeIntervalSince1970:cellModel.lastMessage.timestamp / 1000];
                self.timeStr.text = [ZFChatUIHelper zfChatUIHelperConversationLastMessageTimeWithDate:date];
            }
            else{
                self.timeStr.text = @"";
            }
          
            
            
            [ProjectUIHelper projectNumIcon:self.msgNumIcon changeNum:(cellModel.unreadMessageCount - 1)];
            
            HTConversation *model = cellModel;
            
            NSString * messageStr;
            
            if(model.lastMessage.body && [model.lastMessage.body isKindOfClass:[HTMessageBody class]] && model.lastMessage.ext && [model.lastMessage.ext isKindOfClass:[NSDictionary class]]){
        
                if([model.lastMessage.ext.allKeys containsObject:@"action"]){
                    id action = model.lastMessage.ext[@"action"];
                    if(action && ([action isKindOfClass:[NSString class]] || [action isKindOfClass:[NSNumber class]])){
                        
                        ZFMessageType type = [ZFChatHelper zfChatHeler_getMessageTypeWithAction:[action integerValue]];
                        
                        if(type == ZFMessageTypeWithdrawn){
                            messageStr = [ZFChatMessageHelper getWithDrawMessageTranslateMessageWithMsg:model.lastMessage groupRole:0 isSender:model.lastMessage.isSender];
                        }
                        else if(type == ZFMessageTypeGroupMsgNotify){
                            if(model.lastMessage.body.content && [model.lastMessage.body.content isKindOfClass:[NSString class]]){
                                
                                messageStr = model.lastMessage.body.content;
                                if(!YiChatProject_IsNeedAppearMemberRemovedAlert){
                                    if ([model.lastMessage.ext[@"action"] integerValue] == 2004) {
                                        messageStr = @"";
                                    }
                                }
                                
                              
                            }
                        }
                        else if(type == ZFMessageTypeRedPackageReceiveOrSend){
                            NSDictionary *dic = model.lastMessage.ext;
                            
                            NSString *content = @"";
                            if(dic && [dic isKindOfClass:[NSDictionary class]]){
                                NSString *str = dic[@"envMsg"];
                                if(str && [str isKindOfClass:[NSString class]]){
                                    content = str;
                                }
                            }
                            messageStr = [NSString stringWithFormat:@"[%@%@]%@",PROJECT_TEXT_APPNAME,@"红包",content];
                        }
                        else if(type == ZFMessageTypeRedPackageGet){
                            if(model.lastMessage.body.content && [model.lastMessage.body.content isKindOfClass:[NSString class]]){
                                messageStr = [ZFChatMessageHelper getRedPackageContentMessageWithMsg:model.lastMessage];
                            }
                        }
                        else{
                            if(model.lastMessage.body.content && [model.lastMessage.body.content isKindOfClass:[NSString class]]){
                                messageStr = [ZFChatMessageHelper getRedPackageContentMessageWithMsg:model.lastMessage];
                            }
                        }
                        
                    }
                }
                else if (model.lastMessage.msgType == 2001) {
                    messageStr = model.lastMessage.body.content;
                    
                    if ([model.lastMessage.ext[@"action"] integerValue] == 2001 && [model.lastMessage.chatType isEqualToString:@"2"] && [model.lastMessage.from isEqualToString:[HTClient sharedInstance].currentUsername]) {
                        messageStr = messageStr;
                    }
                    
                    // 如果是活动消息
                    if ([model.lastMessage.ext[@"action"] integerValue] == 3000) {
                        messageStr = @"活动消息";
                    }
                    
                   
                }
                else{
                    messageStr = [self cellShowContent:model];
                }
            }
            if(messageStr && [messageStr isKindOfClass:[NSString class]]){
                
                NSString *chatType = cellModel.lastMessage.chatType;
                NSString *from = cellModel.lastMessage.from;
                NSString *to = cellModel.lastMessage.to;
                NSString *chatId = cellModel.chatterId;
                
                // self.content.text = messageStr;
                if(chatType && [chatType isKindOfClass:[NSString class]]){
                    if(from && [from isKindOfClass:[NSString class]] && to && [to isKindOfClass:[NSString class]] && chatId && [chatId isKindOfClass:[NSString class]]){
                        
                        if([chatType isEqualToString:@"1"]){
                            if(cellModel.lastMessage.isSender){
                                if([to isEqualToString:chatId]){
                                     self.content.text = messageStr;
                                }
                                
                            }
                            else{
                                if([to isEqualToString:YiChatUserInfo_UserIdStr]){
                                    self.content.text = messageStr;
                                }
                            }
                        }
                        else{
                            self.content.text = messageStr;
                            
                            
                            WS(weakSelf);
                            [[YiChatStorageManager sharedManager] getStorageMessageAlertWithKey:chatId handle:^(id  _Nonnull obj) {
                                if(obj && [obj isKindOfClass:[HTMessage class]]){
                                    HTMessage *msg = obj;
                                    
                                    if(weakSelf.cellModel && [weakSelf.cellModel isKindOfClass:[HTConversation class]]){
                                        
                                        if(weakSelf.cellModel.chatterId && [weakSelf.cellModel.chatterId isKindOfClass:[NSString class]]){
                                            
                                            if([weakSelf.cellModel.chatterId isEqualToString:msg.to]){
                                                
                                                
                                                if(msg.from && [msg.from isKindOfClass:[NSString class]] && msg.body && [msg.body isKindOfClass:[HTMessageBody class]]){
                                                    
                                                    if(msg.body.content){
                                                        [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:msg.from invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                                                            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                                                                
                                                                NSString *alertText = @"[有人@我]";
                                                                NSString *msgText = [NSString stringWithFormat:@"%@%@%@",[model appearName],@":",msg.body.content];
                                                                
                                                                NSString *showStr = [alertText stringByAppendingString:msgText];
                                                                
                                                                NSAttributedString *show = [ProjectHelper helper_factoryFontMakeAttributedStringWithTwoDiffirrentTextWhileSpecialWithRange:NSMakeRange(0, alertText.length) font:PROJECT_TEXT_FONT_COMMON(12.0) andFont:PROJECT_TEXT_FONT_COMMON(12.0) color:[UIColor redColor] color:PROJECT_COLOR_TEXTGRAY withText:showStr];
                                                                
                                                                [ProjectHelper helper_getMainThread:^{
                                                                    weakSelf.content.text = nil;
                                                                    weakSelf.content.attributedText = show;
                                                                }];
                                                               
                                                            }
                                                        }];
                                                    }
                                                    
                                                }
                                                
                                            }
                                        }
                                    }
                                }
                            }];
                        }
                    }
                }
                
            
               
            }
        }
    }
}

- (void)dealUserInfoWithModel:(HTConversation *)model{
    WS(weakSelf);
    if ([model.lastMessage.chatType isEqualToString:@"1"]) {
        
        if(model.chatterId && [model.chatterId isKindOfClass:[NSString class]]){
            
            [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:model.chatterId invocation:^(YiChatUserModel * _Nonnull userModel, NSString * _Nonnull error) {
                if(weakSelf){
                    
                        if(userModel && [userModel isKindOfClass:[YiChatUserModel class]]){
                            NSString *currentUserId = [self getCurrentChatId];
                            
                            if(currentUserId && [currentUserId isKindOfClass:[NSString class]]){
                                NSString *userId = [userModel getUserIdStr];
                                if(userId && [userId isKindOfClass:[NSString class]]){
                                    if([currentUserId isEqualToString:userId]){
                                        
                                        [ProjectHelper helper_getMainThread:^{
                                             [UIView performWithoutAnimation:^{
                                                 self.title.text = [userModel appearName];
                                             }];
                                        }];
                                    }
                                }
                            }
                            
                            [self loadNetIconWithModel:model url:userModel.avatar placeHoler:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
                            
                        }
                }
            }];
        }
    }else{
        
        HTGroup * group = [[HTClient sharedInstance].groupManager groupByGroupId:model.lastMessage.to];
        if(group && [group isKindOfClass:[HTGroup class]]){
            
            NSString * groupname = group.groupName;
            if(groupname && [groupname isKindOfClass:[NSString class]]){
                [UIView performWithoutAnimation:^{
                   self.title.text = groupname;
                }];
            }
            
            [self loadNetIconWithModel:model url:group.groupAvatar placeHoler:[UIImage imageNamed:PROJECT_ICON_GROUPDEFAULT]];
        }
    }
}

- (void)loadNetIconWithModel:(HTConversation *)model url:(NSString *)url placeHoler:(UIImage *)placeHolder{
    
    WS(weakSelf);
    self.netIconUrl = url;
    [ProjectHelper projectHelper_asyncLoadNetImage:url imageView:_icon placeHolder:placeHolder invocation:^NSString * _Nonnull{
        return weakSelf.netIconUrl;
    }];
    
}

- (HTConversation *)getCurrentModel{
    if(self){
        if(self.cellModel && [self.cellModel isKindOfClass:[HTConversation class]]){
            return self.cellModel;
        }
    }
    return nil;
}

- (NSString *)getCurrentChatId{
    HTConversation *conversation = [self getCurrentModel];
    if(conversation && [conversation isKindOfClass:[HTConversation class]]){
        NSString *chatId = conversation.chatterId;
        if(chatId && [chatId isKindOfClass:[NSString class]]){
            return chatId;
        }
    }
    return @"";
}

- (NSString *)cellShowContent:(HTConversation *)model {
    if ([model.lastMessage.chatType isEqualToString:@"2"] && ![model.lastMessage.from isEqualToString:[[HTClient sharedInstance] currentUsername]]) {
        switch (model.lastMessage.msgType) {
            case 2001:
                return [NSString stringWithFormat:@"%@%@",[self reformerGroupMessage:model.lastMessage],model.lastMessage.body.content];
                break;
            case 2002:
                return [NSString stringWithFormat:@"%@%@",[self reformerGroupMessage:model.lastMessage],@"[图片]"];
                break;
            case 2003:
                return [NSString stringWithFormat:@"%@%@",[self reformerGroupMessage:model.lastMessage],@"[语音]"];
                break;
            case 2004:
                return [NSString stringWithFormat:@"%@%@",[self reformerGroupMessage:model.lastMessage],@"[视频]"];
                break;
            case 2005:
                return [NSString stringWithFormat:@"%@%@",[self reformerGroupMessage:model.lastMessage],@"[文件]"];
                break;
            case 2006:
                return [NSString stringWithFormat:@"%@%@",[self reformerGroupMessage:model.lastMessage],@"[位置]"];
                break;
            default:
                return nil;
                break;
        }
        
    }else{
        switch (model.lastMessage.msgType) {
            case 2001:
                return model.lastMessage.body.content;
                break;
            case 2002:
                return @"[图片]";
                break;
            case 2003:
                return @"[语音]";
                break;
            case 2004:
                return @"[视频]";
                break;
            case 2005:
                return @"[文件]";
                break;
            case 2006:
                return @"[位置]";
                break;
            default:
                return nil;
                break;
        }
        
    }
}

- (NSString *)reformerGroupMessage:(HTMessage *)message {
    if (message.ext[@"action"]) {
        NSUInteger extAction = [message.ext[@"action"] integerValue];
        // 处理了群通知消息
        if ( extAction == 2000 || extAction == 2001 || extAction == 2002 || extAction == 2003 || extAction == 2004 || extAction == 2005) {
            return @"";
        }
    }
    return [NSString stringWithFormat:@"%@:",message.ext[@"nick"]];
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
