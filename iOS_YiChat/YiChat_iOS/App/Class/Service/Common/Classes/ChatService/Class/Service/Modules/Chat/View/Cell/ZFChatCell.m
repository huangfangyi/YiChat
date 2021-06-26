//
//  ZFChatCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/12.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatCell.h"
#import "ZFChatUIConfigure.h"
#import "ZFChatConfigure.h"
#import "ProjectTableCell+ServiceExtension.h"
#import "YiChatUserManager.h"
#import "ZFChatHelper.h"
#import <UIImageView+WebCache.h>
#import "ZFGroupHelper.h"
#import "NSMutableAttributedString+AddFullUrl.h"
#import "ZFChatLabel.h"
#import <UIImage+GIF.h>

@interface ZFChatCell ()

@property (nonatomic,assign) ZFChatCellType type;

@property (nonatomic,assign) ZFChatType chatType;

@property (nonatomic,strong) ZFChatUIConfigure *uiConfigure;

@property (nonatomic,strong) UIView *chatCellBack;

@property (nonatomic,strong) UIView *chatMessageBack;

@property (nonatomic,strong) UIImageView *user_role;
@property (nonatomic,strong) UIImageView *user_icon;
@property (nonatomic,strong) UIButton *user_icon_Btn;

@property (nonatomic,strong) UILabel *user_nick;

@property (nonatomic,strong) ZFChatLabel *chatTextMessageLab;

@property (nonatomic,strong) UIButton *actionClickForMessageTextHttp;

@property (nonatomic,strong) UIImageView *chatvoiceMessageIcon;
@property (nonatomic,strong) UILabel *chatVoiceMessageLab;
@property (nonatomic,strong) UIView *chatVoiceMessagePlayState;

@property (nonatomic,strong) UIImageView *videoIcon;
@property (nonatomic,strong) UILabel *videoDurationLab;

@property (nonatomic,strong) UIImageView *messagePageIconView;

@property (nonatomic,strong) UIButton *actionClickForMessageBack;

@property (nonatomic,strong) UIImageView *sendFailImage;

@property (nonatomic,strong) UIButton *sendFailBtn;

@property (nonatomic,strong) UIActivityIndicatorView *sendIndicator;

@property (nonatomic,strong) UILabel *commonCMDTextLab;


@property (nonatomic,strong) UIImageView *redPackgeIcon;
@property (nonatomic,strong) UILabel *redPackgeDes;
@property (nonatomic,strong) UILabel *redPackgeTitle;
@property (nonatomic,strong) UIButton *redPackgeClickBtn;

@property (nonatomic,strong) UIImageView *personCardIcon;
@property (nonatomic,strong) UILabel *personCardNick;
@property (nonatomic,strong) UILabel *personCardTitle;
@property (nonatomic,strong) UIButton *personCardClickBtn;

@end



@implementation ZFChatCell

+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth type:(ZFChatCellType)type chatType:(NSInteger)chatType{
    return [[self alloc] initWithStyle:style reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth type:type chatType:chatType];
    
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth type:(ZFChatCellType)type chatType:(NSInteger)chatType{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth];
    if(self){
        _type = type;
        _chatType = chatType;
        _uiConfigure = [ZFChatUIConfigure initialChatUIConfigure];
        
        self.contentView.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    if(_type == ZFChatCellTypeText){
        [self makeCommonSection];
        [self makeUIForText];
    }
    else if(_type == ZFChatCellTypePhoto){
        [self makeCommonSection];
        [self makePageIconImageView];
    }
    else if(_type == ZFChatCellTypeVoice){
        [self makeCommonSection];
        [self makeUIForVoice];
    }
    else if(_type == ZFChatCellTypeVideo){
        [self makeCommonSection];
        [self makeUIForVideo];
    }
    else if(_type == ZFChatCellTypeLocation){
        [self makeCommonSection];
        [self makeUIForLocation];
    }
    else if(_type == ZFChatCellTypeRedPackgeSendOrReceive){
        [self makeCommonSection];
        [self makeUIForRedPackge];
    }
    else if(_type == ZFChatCellTypePersonCard){
        [self makeCommonSection];
        [self makeUIForPersonCard];
    }
    else if(_type == ZFChatCellTypeCommonCMDMessage){
        _chatCellBack = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.sCellWidth, self.sCellHeight)];
        [self.contentView addSubview:_chatCellBack];
        _chatCellBack.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
        
        [self makeCommonCMDMessageBack];
        [self makeUIForCommonCMD];
    }
}

- (void)makeCommonSection{
    _chatCellBack = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.sCellWidth, self.sCellHeight)];
    [self.contentView addSubview:_chatCellBack];
    _chatCellBack.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    
    [self makeIconSection];
    if(self.chatType == ZFChatTypeGroup){
        [self makeNick];
    }
    
    [self makeMessageBack];
    
    [self makeSendImageStatus];
}

- (void)makeIconSection{
    _user_icon = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, _uiConfigure.messageIconW,  _uiConfigure.messageIconW)];
    _user_icon.layer.cornerRadius = _user_icon.frame.size.height / 2;
    _user_icon.clipsToBounds = YES;
    _user_icon.userInteractionEnabled = YES;
    [_chatCellBack addSubview:_user_icon];
    
//    UIButton *clearBtn = [UIButton buttonWithType:UIButtonTypeCustom];
//    [clearBtn addTarget:self action:@selector(actionForClickIcon:) forControlEvents:UIControlEventTouchUpInside];
//    [_chatCellBack addSubview:clearBtn];
//    _user_icon_Btn = clearBtn;
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(actionForClickIcon:)];
    [_user_icon addGestureRecognizer:tap];
    
    UILongPressGestureRecognizer *longPress =  [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(actionForLongPressIcon:)];
    longPress.minimumPressDuration = 0.5;
    [_user_icon addGestureRecognizer:longPress];
}

- (void)makeNick{
    _user_nick = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, _uiConfigure.messageNickSize.width, _uiConfigure.messageNickSize.height)];
    _user_nick.textColor = _uiConfigure.nickTextColor;
    _user_nick.font = _uiConfigure.nickTextFont;
    [_chatCellBack addSubview:_user_nick];
    
    if(_chatType == ZFChatTypeGroup){
        UIImage *icon = [_uiConfigure getGroupChatUserRoleWithPower:2];
        CGFloat w = 20.0;
        CGFloat h = _user_nick.frame.size.height * 0.8;
        if(icon){
             w = [ProjectHelper helper_GetWidthOrHeightIntoScale:icon.size.width / icon.size.height width:0 height:h];
        }
    
        
        _user_role = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, w, h)];
        [_chatCellBack addSubview:_user_role];
    }
}


- (void)actionForClickIcon:(UITapGestureRecognizer *)clickIconAction{
    if(self.zfChatCellClickIconAction){
        NSString *userId = [self getUserIdWithChatModel:_chatModel];
        [self becomeFirstResponder];
        self.zfChatCellClickIconAction(userId, self.sIndexPath);
    }
}

- (void)actionForLongPressIcon:(UILongPressGestureRecognizer *)ges{
    if(ges.state == UIGestureRecognizerStateBegan){
        if(self.zfChatCellUserIconLongPressAction){
            self.zfChatCellUserIconLongPressAction(_chatModel, self.sIndexPath);
        }
    }
}

- (void)makeSendImageStatus{
    _sendIndicator = [UIActivityIndicatorView new];
    _sendIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
    [_chatCellBack addSubview:_sendIndicator];
    
    _sendFailImage = [UIImageView new];
    _sendFailImage.image = [UIImage imageNamed:@"messageSendFail@2x.png"];
    [_chatCellBack addSubview:_sendFailImage];
    _sendFailImage.userInteractionEnabled  = NO;
    
    _sendFailBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [_chatCellBack addSubview:_sendFailBtn];
    [_sendFailBtn addTarget:self action:@selector(actionForMsgSendFail:) forControlEvents:UIControlEventTouchUpInside];
}

- (void)actionForMsgSendFail:(UIButton *)btn{
    if(self.zfChatCellClickSendFailAction){
        self.zfChatCellClickSendFailAction(_chatModel, self.sIndexPath);
    }
}

- (void)makeMessageBack{
    _chatMessageBack = [UIView new];
    [_chatCellBack addSubview:_chatMessageBack];
    
    UILongPressGestureRecognizer * longpress = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPressAction:)];
    [_chatMessageBack addGestureRecognizer:longpress];
}

- (void)makeCommonCMDMessageBack{
    _chatMessageBack = [UIView new];
    [_chatCellBack addSubview:_chatMessageBack];
}

- (void)makeUIForText{
    _chatTextMessageLab = [ZFChatLabel new];
    _chatTextMessageLab.textAlignment = NSTextAlignmentCenter;
    [_chatMessageBack addSubview:_chatTextMessageLab];
    
    
    UIButton *voiceClickAction = [UIButton buttonWithType:UIButtonTypeCustom];
         [voiceClickAction addTarget:self action:@selector(chatHttpMethod:) forControlEvents:UIControlEventTouchUpInside];
         [_chatMessageBack addSubview:voiceClickAction];
         _actionClickForMessageTextHttp = voiceClickAction;
    
}

- (void)chatHttpMethod:(UIButton *)btn{
    if(_chatModel.httpText){
        if(_chatModel.httpText.count > 0){
            
            if(_chatModel.httpText.count == 1){
                 [[UIApplication sharedApplication] openURL:[NSURL URLWithString:_chatModel.httpText.firstObject[@"url"]]];
            }
            else{
                NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
                
                for (int i = 0; i < _chatModel.httpText.count; i ++) {
                    [arr addObject:_chatModel.httpText[i][@"url"]];
                }
                
                [ProjectUIHelper projectActionSheetWithListArr:arr click:^(NSInteger row) {
                    if(arr.count - 1 >= row){
                        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:arr[row]]];
                    }
                }];
            }
            
        }
    }
}

- (void)makeUIForVoice{
    _chatvoiceMessageIcon = [UIImageView new];
    [_chatMessageBack addSubview:_chatvoiceMessageIcon];
    
    _chatVoiceMessageLab = [UILabel new];
    _chatVoiceMessageLab.font = _uiConfigure.msgFont;
    [_chatMessageBack addSubview:_chatVoiceMessageLab];
    
    _chatVoiceMessagePlayState = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 10.0, 10.0)];
    _chatVoiceMessagePlayState.layer.cornerRadius = 10.0 / 2;
    _chatVoiceMessagePlayState.backgroundColor = [UIColor redColor];
    [_chatCellBack addSubview:_chatVoiceMessagePlayState];
    
    UIButton *voiceClickAction = [UIButton buttonWithType:UIButtonTypeCustom];
    [voiceClickAction addTarget:self action:@selector(voiceClickMethod:) forControlEvents:UIControlEventTouchUpInside];
    [_chatMessageBack addSubview:voiceClickAction];
    _actionClickForMessageBack = voiceClickAction;
}

- (void)voiceClickMethod:(UIButton *)click{
    if(_chatModel && [_chatModel isKindOfClass:[ZFChatConfigure class]]){
        _chatModel.voiceIsPlayed = YES;
        if(_chatModel.isSender){
            _chatVoiceMessagePlayState.hidden = YES;
        }
        
        _chatModel.isPlayVoice = !_chatModel.isPlayVoice;
        [self changeVoicePlayUIWithState:_chatModel.isPlayVoice];
        
        if(self.zfChatCellClickVoiceAction){
            self.zfChatCellClickVoiceAction(_chatModel, self.sIndexPath);
        }
    }
}

- (void)changeVoicePlayUIWithState:(BOOL)state{
    
    [ProjectHelper helper_getMainThread:^{
        
        NSArray *playIcons = [self getVoicePlayIconsWithSender:_chatModel.isSender];
        if(playIcons && [playIcons isKindOfClass:[NSArray class]]){
            if(playIcons.count >= 1){
                
                if(state){
                    [_chatvoiceMessageIcon stopAnimating];
                    _chatvoiceMessageIcon.animationImages = nil;
                    _chatvoiceMessageIcon.image = nil;
                    
                    _chatvoiceMessageIcon.animationImages = playIcons;
                    _chatvoiceMessageIcon.animationDuration = 1;
                    _chatvoiceMessageIcon.animationRepeatCount = 999;
                    
                    [_chatvoiceMessageIcon startAnimating];
                    
                }
                else{
                    [_chatvoiceMessageIcon stopAnimating];
                    _chatvoiceMessageIcon.animationImages = nil;
                    _chatvoiceMessageIcon.image = playIcons.lastObject;
                }
                
            }
        }
    }];
}

- (NSArray *)getVoicePlayIconsWithSender:(BOOL)isSender{
    if(isSender){
        return _uiConfigure.voiceRightPlayIcons;
    }
    else{
        return _uiConfigure.voiceLeftPlayIcons;
    }
}

- (void)makeUIForVideo{
    [self makePageIconImageView];
    _videoIcon = [UIImageView new];
    [_chatMessageBack addSubview:_videoIcon];
    
    _videoDurationLab = [UILabel new];
    _videoDurationLab.font = _uiConfigure.videoDurationFont;
    _videoDurationLab.textColor = _uiConfigure.videoDurationColor;
    [_chatMessageBack addSubview:_videoDurationLab];
}

- (void)makeUIForLocation{
    [self makePageIconImageView];
}

- (void)makeUIForRedPackge{
    
    _user_icon.frame = CGRectMake(0, 0, _user_icon.frame.size.width, _user_icon.frame.size.height);
    
    CGFloat h =  _uiConfigure.redPackgeTitleSize + _uiConfigure.redPackgeDesSize + _uiConfigure.redPackgeDownSize;
    CGFloat w = _uiConfigure.redPackgeWidth;
    _chatMessageBack.frame = CGRectMake(0, 0, w, h);
    
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, _chatMessageBack.frame.size.width, _uiConfigure.redPackgeTitleSize + _uiConfigure.redPackgeDesSize)];
    [_chatMessageBack addSubview:back];
    back.backgroundColor = [UIColor orangeColor];
    
    CGFloat iconW = 35.0;
    CGFloat iconH = 35.0;
    CGFloat y = _uiConfigure.redPackgeTitleSize + _uiConfigure.redPackgeDesSize / 2 - iconH / 2;
    CGFloat x = PROJECT_SIZE_NAV_BLANK;
    
    _redPackgeIcon = [[UIImageView alloc] initWithFrame:CGRectMake(x, y, iconH, iconW)];
    _redPackgeIcon.image = _uiConfigure.redPakgeIcon;
    [back addSubview:_redPackgeIcon];
    
    x = _redPackgeIcon.frame.origin.x + _redPackgeIcon.frame.size.width + 10.0;
    
    _redPackgeDes = [[UILabel alloc] initWithFrame:CGRectMake(x, _redPackgeIcon.center.y - _uiConfigure.redPackgeDesSize / 2, back.frame.size.width - x -  5.0, _uiConfigure.redPackgeDesSize)];
    [back addSubview:_redPackgeDes];
    _redPackgeDes.font = _uiConfigure.redPakgeDesFont;
    _redPackgeDes.textColor = _uiConfigure.redPakgeDesColor;
    _redPackgeDes.textAlignment = NSTextAlignmentLeft;
    
    _redPackgeTitle = [[UILabel alloc] initWithFrame:CGRectMake(_redPackgeIcon.frame.origin.x,_chatMessageBack.frame.size.height - _uiConfigure.redPackgeDownSize, back.frame.size.width - _redPackgeIcon.frame.origin.x * 2, _uiConfigure.redPackgeDownSize)];
    [_chatMessageBack addSubview:_redPackgeTitle];
    _redPackgeTitle.font = _uiConfigure.redPakgeTitleFont;
    _redPackgeTitle.textAlignment = NSTextAlignmentLeft;
    _redPackgeTitle.textColor = _uiConfigure.redPakgeTitleColor;
    
    _redPackgeClickBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _redPackgeClickBtn.frame = _chatMessageBack.bounds;
    [_redPackgeClickBtn addTarget:self action:@selector(redPackegClickAction:) forControlEvents:UIControlEventTouchUpInside];
    [_chatMessageBack addSubview:_redPackgeClickBtn];
}
    
- (void)makeUIForPersonCard{
    
    _user_icon.frame = CGRectMake(0, 0, _user_icon.frame.size.width, _user_icon.frame.size.height);
    
    CGFloat h =  _uiConfigure.mesaagePersonCardH;
    CGFloat w = _uiConfigure.mesaagePersonCardW;
    _chatMessageBack.frame = CGRectMake(0, 0, w, h);
    
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, _chatMessageBack.frame.size.width, _uiConfigure.mesaagePersonCardBlank * 2 + _uiConfigure.mesaagePersonCardIconSize.height)];
    
    [_chatMessageBack addSubview:back];
    back.backgroundColor = [UIColor whiteColor];
    
    CGFloat iconW = _uiConfigure.mesaagePersonCardIconSize.width;
    CGFloat iconH = _uiConfigure.mesaagePersonCardIconSize.height;
    CGFloat y = _uiConfigure.mesaagePersonCardBlank;
    CGFloat x = _uiConfigure.mesaagePersonCardBlank;
    
    _personCardIcon = [[UIImageView alloc] initWithFrame:CGRectMake(x, y, iconW, iconH)];
    [back addSubview:_personCardIcon];
    
    x = _personCardIcon.frame.origin.x + _personCardIcon.frame.size.width + _uiConfigure.mesaagePersonCardBlank;
    
    _personCardNick = [[UILabel alloc] initWithFrame:CGRectMake(x, _personCardIcon.center.y - iconH / 2, back.frame.size.width - x -  5.0, iconH)];
    [back addSubview:_personCardNick];
    _personCardNick.font = _uiConfigure.redPakgeDesFont;
    _personCardNick.textColor = PROJECT_COLOR_TEXTCOLOR_BLACK;
    _personCardNick.textAlignment = NSTextAlignmentLeft;
    
    _personCardTitle = [[UILabel alloc] initWithFrame:CGRectMake(_uiConfigure.mesaagePersonCardBlank, _personCardIcon.frame.origin.y + _personCardIcon.frame.size.height, back.frame.size.width - _uiConfigure.mesaagePersonCardBlank * 2, _chatMessageBack.frame.size.height - _personCardIcon.frame.origin.y - _personCardIcon.frame.size.height)];
    
    [_chatMessageBack addSubview:_personCardTitle];
    _personCardTitle.font = _uiConfigure.redPakgeTitleFont;
    _personCardTitle.textAlignment = NSTextAlignmentLeft;
    _personCardTitle.textColor = _uiConfigure.redPakgeTitleColor;
    
    _personCardClickBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _personCardClickBtn.frame = _chatMessageBack.bounds;
    [_personCardClickBtn addTarget:self action:@selector(personCardClickBtnAction:) forControlEvents:UIControlEventTouchUpInside];
    [_chatMessageBack addSubview:_personCardClickBtn];
    
}

- (void)redPackegClickAction:(UIButton *)btn{
    if(self.zfChatCellRedPackgeClickAction){
        self.zfChatCellRedPackgeClickAction(self.chatModel,self.sIndexPath);
    }
}

- (void)personCardClickBtnAction:(UIButton *)btn{
    if(self.zfChatCellPersonCardClickAction){
        self.zfChatCellPersonCardClickAction(self.chatModel,self.sIndexPath);
    }
}

- (void)makeUIForCommonCMD{
    _commonCMDTextLab = [UILabel new];
    _commonCMDTextLab.font = _uiConfigure.msgFont;
    _commonCMDTextLab.textAlignment = NSTextAlignmentCenter;
    [_chatMessageBack addSubview:_commonCMDTextLab];
}

- (void)makePageIconImageView{
    _messagePageIconView = [UIImageView new];
    [_chatMessageBack addSubview:_messagePageIconView];
    _messagePageIconView.userInteractionEnabled = NO;
    _messagePageIconView.layer.cornerRadius = 10.0;
    _messagePageIconView.clipsToBounds = YES;
    
    UIButton *clearBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [clearBtn addTarget:self action:@selector(actionForClickPageIconImageView:) forControlEvents:UIControlEventTouchUpInside];
    [_chatMessageBack addSubview:clearBtn];
    _actionClickForMessageBack = clearBtn;
    
}

- (void)actionForClickPageIconImageView:(UIButton *)btn{
    if(_chatModel && [_chatModel isKindOfClass:[ZFChatConfigure class]]){
        if(_chatModel.messageType ==  ZFMessageTypePhoto){
            
            if(self.zfChatCellReviewPhotoMessageAction){
                self.zfChatCellReviewPhotoMessageAction(_chatModel, self.sIndexPath, _messagePageIconView);
            }
//            NSString *url = [_chatModel getPhotoOriginUrl];
//
//            if(url && [url isKindOfClass:[NSString class]] && _chatMessageBack && [_chatMessageBack isKindOfClass:[UIView class]]){
//
//
//                [ProjectUIHelper helper_showImageBrowseWithDataSouce:@[url] withSourceObjs:@[_messagePageIconView] currentIndex:0];
//            }
        }
        else if(_chatModel.messageType == ZFMessageTypeVideo){
            if(self.zfChatCellReviewVideoMessageAction){
                self.zfChatCellReviewVideoMessageAction(_chatModel, self.sIndexPath, _messagePageIconView);
            }
            
//            NSString *url = [_chatModel getVideoPlayUrl];
//
//            if(url && [url isKindOfClass:[NSString class]] && _chatMessageBack && [_chatMessageBack isKindOfClass:[UIView class]]){
//
//
//                [ProjectUIHelper helper_showVideoBrowseWithDataSouce:@[url] withSourceObjs:@[_messagePageIconView] currentIndex:0];
//            }
        }
    }
}

- (void)setChatModel:(ZFChatConfigure *)chatModel{
    if(chatModel && [chatModel isKindOfClass:[ZFChatConfigure class]]){
        _chatModel = chatModel;
       
        if(_chatCellBack){
            _chatCellBack.frame = CGRectMake(0, 0, self.sCellWidth, self.sCellHeight);
        }
        
        if(chatModel.messageType >= 0 && chatModel.messageType <= 7){
            
            [self calculateIconFrameWithIsSender:chatModel.isSender];
            
            [self calculateMessageBackWithIsSender:chatModel.isSender model:chatModel];
            
            [self dealSendMesStatus:chatModel];
            
            _actionClickForMessageBack.frame = _chatMessageBack.bounds;
            
            [self dealUserInfoAppear:chatModel];
            
            if(chatModel.messageType == ZFMessageTypeText){
                [self dealTextMessage:chatModel];
            }
            else if(_chatModel.messageType ==  ZFMessageTypePhoto){
                
                [self dealPhotoMessage:chatModel];
                
            }
            else if(_chatModel.messageType == ZFMessageTypeVoice){
                
                [self dealVoiceMessage:chatModel];
                
            }
            else if(_chatModel.messageType == ZFMessageTypeVideo){
                
                [self dealVideoMessage:chatModel];
                
            }
            else if(_chatModel.messageType == ZFMessageTypeLocation){
                
                [self dealLocationMessage:chatModel];
            }
            else if(_chatModel.messageType == ZFMessageTypeRedPackageReceiveOrSend){
                [self dealRedPackge:chatModel];
            }
            else if(_chatModel.messageType == ZFMessageTypePersonCard){
                [self dealPersonCard:chatModel];
            }
            
            if(self.user_role){
                if(chatModel.isSender == YES){
                    self.user_role.hidden = YES;
                }
                else{
                    self.user_role.hidden = NO;
                }
            }
        }
        else if(chatModel.messageType == ZFMessageTypeGroupMsgNotify || chatModel.messageType ==  ZFMessageTypeWithdrawn || chatModel.messageType == ZFMessageTypeGroupCancelSetManager || chatModel.messageType == ZFMessageTypeGroupSetManager || chatModel.messageType == ZFMessageTypeGroupSilence || chatModel.messageType == ZFMessageTypeGroupCancelSilence || chatModel.messageType == ZFMessageTypeRedPackageGet || chatModel.messageType == ZFMessageTypeGroupMemberSilence || chatModel.messageType == ZFMessageTypeCancelGroupMemberSilence ){
            
            if(self.user_role){
                self.user_role.hidden = YES;
            }
            
            [self calculateMessageBackWithIsSender:chatModel.isSender model:chatModel];
            [self dealNotifyMessage:chatModel];
        }
    }
    else{
        
    }
}

- (void)changeGroupRoleIcon:(NSInteger)power{
    self.user_role.image = [_uiConfigure getGroupChatUserRoleWithPower:power];
}

- (void)dealSendMesStatus:(ZFChatConfigure *)chatModel{
    if(chatModel.isSender == YES){
        
        if(chatModel && [chatModel isKindOfClass:[ZFChatConfigure class]]){
            SendState stastus = [chatModel getMSGSendStatus];
            if(stastus == SendStateSending){
                _sendIndicator.hidden = NO;
                [_sendIndicator startAnimating];
                
                _sendFailImage.hidden = YES;
                _sendFailBtn.hidden = YES;
                
                _sendIndicator.frame = CGRectMake(_chatMessageBack.frame.origin.x - _uiConfigure.sendIndicatorSize.width - _uiConfigure.messageBlank, _chatMessageBack.frame.origin.y + _chatMessageBack.frame.size.height / 2 -_uiConfigure.sendIndicatorSize.height / 2, _uiConfigure.sendIndicatorSize.width, _uiConfigure.sendIndicatorSize.height);
                
            }
            else if(stastus == SendStateSuccessed){
                _sendFailBtn.hidden = YES;
                _sendFailImage.hidden = YES;
                _sendIndicator.hidden = YES;
            }
            else if(stastus == SendStateFail){
                _sendIndicator.hidden = YES;
                [_sendIndicator stopAnimating];
                
                _sendFailImage.hidden = NO;
                _sendFailBtn.hidden = NO;
                
                _sendFailImage.frame = CGRectMake(_chatMessageBack.frame.origin.x - _uiConfigure.sendFailImageSize.width - _uiConfigure.messageBlank, _chatMessageBack.frame.origin.y + _chatMessageBack.frame.size.height / 2 -_uiConfigure.sendFailImageSize.height / 2, _uiConfigure.sendFailImageSize.width, _uiConfigure.sendFailImageSize.height);
                _sendFailBtn.frame = _sendFailImage.frame;
            }
            else{
                _sendFailBtn.hidden = YES;
                _sendFailImage.hidden = YES;
                _sendIndicator.hidden = YES;
            }
        }
        
    }
    else{
        _sendFailBtn.hidden = YES;
        [_sendIndicator stopAnimating];
        _sendFailImage.hidden = YES;
        _sendIndicator.hidden = YES;
    }
}

- (NSString *)getUserIdWithChatModel:(ZFChatConfigure *)chatModel{
    NSString *userId = nil;
    
    if(chatModel.chatType == ZFChatTypeGroup){
        if(chatModel.isSender == YES){
            userId = [ZFChatHelper zfChatHelper_getCurrentUser];
        }
        else{
            userId = [chatModel getMsgFrom];
        }
    }
    else if(chatModel.chatType == ZFChatTypeChat){
        if(chatModel.isSender == YES){
            userId = [ZFChatHelper zfChatHelper_getCurrentUser];
        }
        else{
            userId = [chatModel getMsgFrom];
        }
    }
    return userId;
}

- (void)dealUserInfoAppear:(ZFChatConfigure *)chatModel{
    
    NSString *userId = [self getUserIdWithChatModel:chatModel];
   
    if(userId && [userId isKindOfClass:[NSString class]]){
        
        [ZFChatHelper fetchUserInfoWithUserId:userId invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
            
            if(_user_role){
                NSString *text = [model appearName];
                
                [ProjectHelper helper_getMainThread:^{
                    if(_user_role && _user_nick){
                        CGRect rect = [ProjectHelper helper_getFontSizeWithString:text useSetFont:_uiConfigure.nickTextFont withWidth:MAXFLOAT andHeight:MAXFLOAT];
                        
                        CGFloat w = rect.size.width;
                        if(w > _user_nick.frame.size.width){
                            w = _user_nick.frame.size.width;
                        }
                                          
                        _user_role.frame = CGRectMake(_user_nick.frame.origin.x + w +  5.0, _user_nick.frame.origin.y + _user_nick.frame.size.height / 2 - _user_role.frame.size.height / 2 - 2.0, _user_role.frame.size.width, _user_role.frame.size.height);
                    }
                  
                }];
                
            }
            
            [self loadUserInfoNetResourceWithModel:model chatModel:chatModel];
        }];
    }
}

- (ZFChatConfigure *)getCurrentChatModel{
    if(self){
        if(self.chatModel && [self.chatModel isKindOfClass:[ZFChatConfigure class]]){
            return self.chatModel;
        }
    }
    return nil;
}

- (void)dealTextMessage:(ZFChatConfigure *)chatModel{
    CGFloat blank = _uiConfigure.messageInterBlank;
    
    _chatTextMessageLab.frame = CGRectMake(blank, blank, _chatModel.contentSize.width - blank * 2, _chatModel.contentSize.height - blank * 2);
    _chatTextMessageLab.textAlignment = NSTextAlignmentCenter;
    _chatTextMessageLab.backgroundColor = [UIColor clearColor];
    _chatTextMessageLab.numberOfLines = 0;
    _chatTextMessageLab.font = _uiConfigure.msgFont;
    
    _actionClickForMessageTextHttp.frame =  CGRectMake(blank, blank, _chatModel.contentSize.width - blank * 2, _chatModel.contentSize.height - blank * 2);
  
    if(chatModel.isSender == YES){
        _chatTextMessageLab.textColor = _uiConfigure.sendMSGTextColor;
    }
    else{
        _chatTextMessageLab.textColor = _uiConfigure.receiveMSGTextColor;
    }
    if(chatModel.chatShowText && [chatModel.chatShowText isKindOfClass:[NSAttributedString class]]){
        _chatTextMessageLab.attributedText = chatModel.chatShowText;
        
        NSArray *arr = chatModel.httpText;
        _chatTextMessageLab.httpText = nil;
        if(arr && [arr isKindOfClass:[NSArray class]]){
            
            if(arr.count > 0){
                NSMutableAttributedString *str = [[NSMutableAttributedString alloc] initWithAttributedString:chatModel.chatShowText];
                
                for (int i = 0; i < arr.count; i ++) {
                    NSDictionary *dic = arr[i];
                    if(dic && [dic isKindOfClass:[NSDictionary class]]){
                        NSValue *rangeValue = dic[@"range"];
                        NSString *url = dic[@"url"];
                        if(rangeValue && url){
                             [str addFullUrlWithWithColor:[UIColor blueColor] range:rangeValue.rangeValue url:url];
                        }
                    }
                }
                _chatTextMessageLab.httpText = [chatModel.httpText mutableCopy];
                _chatTextMessageLab.attributedText = str;
            }
            
           
        }
       
    }
}

- (void)dealVoiceMessage:(ZFChatConfigure *)chatModel{
    if(chatModel.isSender){
        _chatVoiceMessagePlayState.hidden = YES;
        
        _chatvoiceMessageIcon.frame = CGRectMake(_chatMessageBack.frame.size.width - _uiConfigure.messageVoiceInterBlank - _uiConfigure.messageVoiceIconSize.width, _chatMessageBack.frame.size.height / 2 - _uiConfigure.messageVoiceIconSize.height / 2, _uiConfigure.messageVoiceIconSize.width, _uiConfigure.messageVoiceIconSize.height);
        
        
        _chatVoiceMessageLab.frame = CGRectMake(0, 0, _chatMessageBack.frame.size.width - _chatvoiceMessageIcon.frame.origin.x, _chatMessageBack.frame.size.height);
        _chatVoiceMessageLab.textAlignment = NSTextAlignmentRight;
        
    }
    else{
        
        _chatVoiceMessagePlayState.hidden = NO;
        if(chatModel.voiceIsPlayed){
            _chatVoiceMessagePlayState.hidden = YES;
        }
        
        _chatVoiceMessagePlayState.frame = CGRectMake(_chatMessageBack.frame.origin.x + _chatMessageBack.frame.size.width + _uiConfigure.messageBlank, _chatMessageBack.frame.origin.y + _chatMessageBack.frame.size.height / 2 - _chatVoiceMessagePlayState.frame.size.height / 2, _chatVoiceMessagePlayState.frame.size.width, _chatVoiceMessagePlayState.frame.size.height);
        
        _chatvoiceMessageIcon.frame = CGRectMake(_uiConfigure.messageVoiceInterBlank, _chatMessageBack.frame.size.height / 2 - _uiConfigure.messageVoiceIconSize.height / 2, _uiConfigure.messageVoiceIconSize.width, _uiConfigure.messageVoiceIconSize.height);
        
        
        _chatVoiceMessageLab.frame = CGRectMake(_chatvoiceMessageIcon.frame.origin.x + _chatvoiceMessageIcon.frame.size.width, 0, _chatMessageBack.frame.size.width - (_chatvoiceMessageIcon.frame.origin.x + _chatvoiceMessageIcon.frame.size.width), _chatMessageBack.frame.size.height);
        _chatVoiceMessageLab.textAlignment = NSTextAlignmentLeft;
        
    }
    
    [self changeVoicePlayUIWithState:chatModel.isPlayVoice];
    
    _chatVoiceMessageLab.text = [NSString stringWithFormat:@"%.0f",[chatModel getVoiceMsgDuration]];
}

- (void)dealVideoMessage:(ZFChatConfigure *)chatModel{
    _messagePageIconView.frame = _chatMessageBack.bounds;
    
    NSString *url = [chatModel getVideoThumbUrl];
    
    _messagePageIconView.image = nil;
    
    if(_uiConfigure.videoIcon && url && [url isKindOfClass:[NSString class]]){
        CGFloat w = _uiConfigure.videoIconSize.width;
        CGFloat h = _uiConfigure.videoIconSize.height;
        _videoIcon.frame = CGRectMake(_chatMessageBack.frame.size.width / 2 - w / 2, _chatMessageBack.frame.size.height / 2 - h / 2,w , h);
        
        _videoIcon.image = _uiConfigure.videoIcon;
        
        _videoIcon.hidden = NO;
    }
    else{
        _videoIcon.hidden = YES;
    }
    
    CGFloat duration = [chatModel getVideoDuration];
    
    NSString *durationAppear = [_uiConfigure getVideoMessageAppearDurationWithDuration:duration];
    if(durationAppear != nil && duration != 0){
        _videoDurationLab.frame = CGRectMake(_chatMessageBack.frame.size.width - _uiConfigure.videoDurationSize.width - _uiConfigure.messageBlank, _chatMessageBack.frame.size.height - _uiConfigure.videoDurationSize.height, _uiConfigure.videoDurationSize.width, _uiConfigure.videoDurationSize.height);
        _videoDurationLab.textAlignment = NSTextAlignmentRight;
        _videoDurationLab.text = durationAppear;
        
        _videoDurationLab.hidden = NO;
    }
    else{
        _videoDurationLab.hidden = YES;
    }
    
    [self loadImageNetResourceWithChatModel:chatModel url:url];
}

- (void)dealPhotoMessage:(ZFChatConfigure *)chatModel{
    _messagePageIconView.frame = _chatMessageBack.bounds;
    
    if(chatModel.isGEmojiText == YES){
        NSString *filepath = [[NSBundle mainBundle] pathForResource:chatModel.gEmojiFileName ofType:@"gif"];
        if (!filepath) {
            filepath = [[NSBundle mainBundle] pathForResource:chatModel.gEmojiFileName ofType:@"jpg"];
        }
        NSData *data = [NSData dataWithContentsOfFile:filepath];
        self.messagePageIconView.image = [UIImage sd_animatedGIFWithData:data];
        return;
    }
    
    NSString *url = [chatModel getPhotoThumbUrl];
    
    [self loadImageNetResourceWithChatModel:chatModel url:url];
}

- (void)dealLocationMessage:(ZFChatConfigure *)chatModel{
    _messagePageIconView.frame = _chatMessageBack.bounds;
    
    NSString *url = [chatModel getLocationThumbUrl];
    
    [self loadImageNetResourceWithChatModel:chatModel url:url];
}

- (void)dealRedPackge:(ZFChatConfigure *)chatModel{
    
    if(chatModel && [chatModel isKindOfClass:[ZFChatConfigure class]]){
        ZFChatRedPackageEntity *model = chatModel.packageModel;
        if(model && [model isKindOfClass:[ZFChatRedPackageEntity class]]){
            if(model.redPackageDes && [model.redPackageDes isKindOfClass:[NSString class]]){
                if(model.redPackageDes.length == 0){
                    _redPackgeDes.text = @"恭喜发财，大吉大利!";
                }
                else{
                    _redPackgeDes.text = model.redPackageDes;
                }
            }
            else{
                _redPackgeDes.text = @"恭喜发财，大吉大利!";
            }
            _redPackgeTitle.text = model.redPackageName;
        }
    }
}
    
- (void)dealPersonCard:(ZFChatConfigure *)chatModel{
    
    if(chatModel && [chatModel isKindOfClass:[ZFChatConfigure class]]){
        
        
        WS(weakSelf);
        [ProjectHelper projectHelper_asyncLoadNetImage:chatModel.showPersonCardAvtar imageView:_personCardIcon placeHolder:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT] invocation:^NSString * _Nonnull{
            return weakSelf.chatModel.showPersonCardAvtar;
        }];
        
        _personCardTitle.text = chatModel.showPersonCardTitle;
        _personCardNick.text = chatModel.showPersonCardNick;
        
    }
}

- (void)dealNotifyMessage:(ZFChatConfigure *)chatModel{
    CGFloat blank = _uiConfigure.messageInterBlank;
    
    _commonCMDTextLab.frame = CGRectMake(blank, blank, _chatModel.contentSize.width - blank * 2, _chatModel.contentSize.height - blank * 2);
    _commonCMDTextLab.textAlignment = NSTextAlignmentCenter;
    _commonCMDTextLab.numberOfLines = 0;
    _commonCMDTextLab.textColor = _uiConfigure.commonCMDMSGTextColor;
    _commonCMDTextLab.font = _uiConfigure.commonCMDMSGFont;
    if(chatModel.chatShowText && [chatModel.chatShowText isKindOfClass:[NSAttributedString class]]){
        _commonCMDTextLab.attributedText = chatModel.chatShowText;
    }
}

- (void)loadUserInfoNetResourceWithModel:(YiChatUserModel *)model chatModel:(ZFChatConfigure *)chatModel{
    
    ZFChatConfigure *currentChatModel = [self getCurrentChatModel];
    
    WS(weakSelf);
    
    if(currentChatModel && [currentChatModel isKindOfClass:[ZFChatConfigure class]]){
        
        if(model && [model isKindOfClass:[YiChatUserModel class]] ){
            
            NSString *currentUserId = [self getUserIdWithChatModel:currentChatModel];
            
            if(currentUserId && [currentUserId isKindOfClass:[NSString class]]){
                if(self && [currentUserId isEqualToString:[model getUserIdStr]]){
                    
                    [ProjectHelper helper_getMainThread:^{
                        
                        if(chatModel.chatType == ZFChatTypeGroup){
                            if(chatModel.isSender == YES){
                                self.user_nick.hidden = YES;
                            }
                            else{
                                self.user_nick.hidden = NO;
                                if(self.user_nick){
                                    self.user_nick.text = [model appearName];
                                }
                            }
                            
                        }
                        else{
                            self.user_nick.hidden = YES;
                        }
                        
                        if(weakSelf.user_icon){
                            UIImage *placeHolder = [UIImage imageNamed:PROJECT_ICON_USERDEFAULT];
                            NSString *url = [ProjectHelper helper_getSDWebImageLoadUrlWithUrl:model.avatar];
                            
                            [weakSelf.user_icon sd_setImageWithURL:[NSURL URLWithString:url] placeholderImage:placeHolder completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
                                if(self){
                                    if(!error && image){
                                        NSString *currentUserId = [weakSelf getUserIdWithChatModel:currentChatModel];
                                        if(imageURL && [imageURL isKindOfClass:[NSURL class]]){
                                            if(currentUserId && [currentUserId isKindOfClass:[NSString class]] && model && [model isKindOfClass:[YiChatUserModel class]]){
                                                NSString *modelUserId = [model getUserIdStr];
                                                if(modelUserId && [modelUserId isKindOfClass:[NSString class]]){
                                                    if([modelUserId isEqualToString:currentUserId]){
                                                        
                                                        if(imageURL.absoluteString.length >  0){
                                                            weakSelf.user_icon.image = image;
                                                            return ;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if(placeHolder){
                                        weakSelf.user_icon.image = placeHolder;
                                    }
                                }
                            }];
                        }
                    }];
                }
            }

        }
    }
}

- (void)loadImageNetResourceWithChatModel:(ZFChatConfigure *)chatModel url:(NSString *)url{
    
    NSString *urlStr = [ProjectHelper helper_getSDWebImageLoadUrlWithUrl:url];
    UIImage *placeHolder =   _uiConfigure.imageLoadErrorIcon;
    
    WS(weakSelf);
    if([urlStr isKindOfClass:[NSString class]] && urlStr){
        if([urlStr hasSuffix:@"http://"]){
            [_messagePageIconView sd_setImageWithURL:[NSURL URLWithString:urlStr] placeholderImage:placeHolder completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
                if(weakSelf){
                    if(image && !error){
                        if(imageURL && [imageURL isKindOfClass:[NSURL class]]){
                            if(chatModel && [chatModel isKindOfClass:[ZFChatConfigure class]]){
                                NSString *chatMsgId = [chatModel getMsgId];
                                if(chatMsgId && [chatMsgId isKindOfClass:[NSString class]]){
                                    
                                    ZFChatConfigure *currentModel = [weakSelf getCurrentChatModel];
                                    if(currentModel && [currentModel isKindOfClass:[ZFChatConfigure class]]){
                                        NSString *currentMsgId = [currentModel getMsgId];
                                        if([currentMsgId isKindOfClass:[NSString class]] && currentMsgId){
                                            if([currentMsgId isEqualToString:chatMsgId] && image && [image isKindOfClass:[UIImage class]]){
                                                if(imageURL.absoluteString.length >  0){
                                                    weakSelf.messagePageIconView.image = image;
                                                    return ;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(placeHolder){
                        weakSelf.messagePageIconView.image = placeHolder;
                    }
                }
                
            }];
        }
        else{
            [self imageLoadIconWithUrl:url placeHolder:placeHolder imageControl:_messagePageIconView];
        }
    }
}

- (void)calculateIconFrameWithIsSender:(BOOL)isSender{
    //
    if(isSender){
        if(_user_icon){
            _user_icon.frame = CGRectMake(self.sCellWidth - _uiConfigure.messageBlank - _uiConfigure.messageIconW, 0, _user_icon.frame.size.width, _user_icon.frame.size.height);
          //  _user_icon_Btn.frame = _user_icon.frame;
        }
        if(_user_nick){
            _user_nick.frame = CGRectMake(_user_icon.frame.origin.x - _uiConfigure.messageBlank - _uiConfigure.messageNickSize.width,0, _uiConfigure.messageNickSize.width, _uiConfigure.messageNickSize.height);
            _user_nick.textAlignment = NSTextAlignmentRight;
            
        }
        
        if(_user_role){
                      
             _user_role.frame = CGRectMake(_user_nick.frame.origin.x - _user_role.frame.size.width - 5.0, _user_nick.frame.origin.y + _user_nick.frame.size.height / 2 - _user_role.frame.size.height / 2, _user_role.frame.size.width, _user_role.frame.size.height);
        }
        
    }
    else{
        if(_user_icon){
            _user_icon.frame = CGRectMake(_uiConfigure.messageBlank, 0,_user_icon.frame.size.width, _user_icon.frame.size.height);
          //  _user_icon_Btn.frame = _user_icon.frame;
        }
        if(_user_nick){
            _user_nick.frame = CGRectMake(_user_icon.frame.origin.x + _uiConfigure.messageBlank + _user_icon.frame.size.width,0, _uiConfigure.messageNickSize.width, _uiConfigure.messageNickSize.height);
            _user_nick.textAlignment = NSTextAlignmentLeft;
        }
    }
}

- (void)calculateMessageBackWithIsSender:(BOOL)isSender model:(ZFChatConfigure *)chatModel{
    CGFloat y = 0;
    
    if(self.chatType == ZFChatTypeChat){
        y = _user_icon.frame.origin.y;
    }
    else if(self.chatType == ZFChatTypeGroup){
        if(isSender){
            y = _user_icon.frame.origin.y;
        }
        else{
            y = _uiConfigure.messageNickSize.height;
        }
    }
    CGFloat w = chatModel.contentSize.width;
    CGFloat h = chatModel.contentSize.height;
    
    UIColor *backColor = nil;
    if(chatModel.messageType >= 0 && chatModel.messageType <= 7){
        if(isSender){
            _chatMessageBack.frame = CGRectMake(_user_icon.frame.origin.x - _uiConfigure.messageBlank - w, y, w, h);
            backColor = _uiConfigure.sendMSGBackColor;
        }
        else{
            _chatMessageBack.frame = CGRectMake(_user_icon.frame.origin.x + _user_icon.frame.size.width + _uiConfigure.messageBlank, y , w, h);
            backColor = _uiConfigure.receiveMSGBackColor;
        }
        if(_chatModel && [_chatModel isKindOfClass:[ZFChatConfigure class]]){
            if(_chatModel.messageType == ZFMessageTypeText || _chatModel.messageType == ZFMessageTypeVoice){
                _chatMessageBack.backgroundColor = backColor;
            }
            else{
                _chatMessageBack.backgroundColor = [UIColor whiteColor];
            }
        }
    }
    else{
        _chatMessageBack.frame = CGRectMake(self.sCellWidth / 2 - w / 2, self.sCellHeight / 2 - h / 2, w, h);
        backColor = PROJECT_COLOR_APPBACKCOLOR;
    }
    
    _chatMessageBack.layer.cornerRadius = 5.0;
    _chatMessageBack.clipsToBounds = YES;
}

- (void)longPressAction:(UILongPressGestureRecognizer *)longGesture{
    if(longGesture.state == UIGestureRecognizerStateBegan){
        if(self.zfChatCellLongpressClickAction){
            [self becomeFirstResponder];
            self.zfChatCellLongpressClickAction(_chatModel, self.sIndexPath);
        }
    }
}

- (UIView *)getMessageBack{
    return _chatMessageBack;
}
    
- (UIView *)getIconBack{
    return _user_icon;
}

- (UIView *)getCellBack{
    return _chatCellBack;
}

- (BOOL)canBecomeFirstResponder{
    return YES;
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
