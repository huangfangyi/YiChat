//
//  ZFChatConfigure.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/9.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZFChatGlobal.h"
#import "ZFChatUIConfigure.h"

NS_ASSUME_NONNULL_BEGIN
@class HTMessage;
@class HTCmdMessage;
@class ZFChatRedPackageEntity;
@interface ZFChatConfigure : NSObject

@property (nonatomic,weak,readonly) ZFChatUIConfigure *uiConfigure;

@property (nonatomic,strong) NSIndexPath *indexPath;

@property (nonatomic,strong) id msg;

@property (nonatomic,assign) ZFMessageType messageType;

@property (nonatomic,assign) ZFChatType chatType;

@property (nonatomic,assign) BOOL isGEmojiText;
@property (nonatomic,strong) NSString *gEmojiFileName;

@property (nonatomic,assign) BOOL isShowHeaderTime;
@property (nonatomic,strong) NSString *timeText;
@property (nonatomic,assign) CGSize headerTextSize;

@property (nonatomic,strong) NSArray *httpText;

@property (nonatomic,assign) CGSize contentSize;
@property (nonatomic,assign) CGFloat cellH;
@property (nonatomic,assign) CGFloat headerH;
@property (nonatomic,assign) CGFloat footerH;

@property (nonatomic,strong) NSAttributedString *chatShowText;
@property (nonatomic,assign) CGRect showTextRect;

@property (nonatomic,assign) CGSize showImageSize;

@property (nonatomic,assign) CGSize showVideoSize;

@property (nonatomic,assign) BOOL isPlayVoice;

@property (nonatomic,assign) BOOL voiceIsPlayed;

@property (nonatomic,assign) NSInteger lastMessageTime;

@property (nonatomic,assign) NSInteger groupRole;

@property (nonatomic,strong) ZFChatRedPackageEntity *packageModel;
    
@property (nonatomic,assign) NSInteger messageAction;
    
@property (nonatomic,strong) NSString *showPersonCardAvtar;
@property (nonatomic,strong) NSString *showPersonCardNick;
@property (nonatomic,strong) NSString *showPersonCardUserId;
@property (nonatomic,strong) NSString *showPersonCardTitle;

//yes 表示消息的发送方 no 表示消息的接收方
@property (nonatomic,assign) BOOL isSender;

- (id)initWithHTMsg:(HTMessage *)msg;

- (id)initWithHTCMDMsg:(HTCmdMessage *)cmdMsg;

- (void)changeMSGSendStatus:(NSInteger)sendState;

- (void)updateMSGConfire;

- (NSInteger)getMSGSendStatus;

- (NSInteger)getMSGDownStatus;

- (NSString *)getMsgFrom;

- (NSString *)getMsgTo;

- (NSString *)getMsgId;

- (CGFloat)getCellH;

- (CGFloat)getHeaderH;

- (CGFloat)getFooterH;

- (NSInteger)getMessageTime;

- (NSString *)getTextMessageContent;

- (void)setMsgTime:(NSInteger)unixtime;

- (void)changeVoicePlayState:(BOOL)state;

- (CGFloat)getVoiceMsgDuration;

- (NSString *)getPhotoThumbUrl;

- (NSString *)getVideoThumbUrl;

- (NSString *)getVideoPlayUrl;

- (CGFloat)getVideoDuration;

- (NSString *)getLocationThumbUrl;

- (NSString *)getPhotoOriginUrl;

- (NSString *)getRemoteVoiceResourceLoadUrl;

- (void)getRemoteVoiceResourceWavLoadUrlInvocation:(void(^)(NSString *url))handle;

- (NSDictionary *)getMessageBodyExt;

- (NSDictionary *)getCMDMessageBody;

- (NSDictionary *)getCMDMessageExtData;
    
- (NSString *)getRemoteFilePath;
@end

NS_ASSUME_NONNULL_END

@interface ZFChatRedPackageEntity : NSObject

@property (nonatomic,strong) NSString *title;

@property (nonatomic,strong) NSString *redPackageId;

@property (nonatomic,strong) NSString *redPackageDes;

@property (nonatomic,strong) NSString *redPackageName;

@property (nonatomic,strong) NSString *sendPersonNickName;

@end
