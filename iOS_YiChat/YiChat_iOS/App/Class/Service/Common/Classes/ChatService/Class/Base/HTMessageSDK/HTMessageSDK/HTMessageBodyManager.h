//
//  HTMessageBodyManager.h
//  HTMessage
//
//  Created by 非夜 on 2016/12/13.
//  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HTMessage.h"

@interface HTMessageBodyManager : NSObject

+ (NSDictionary *)convertMessageBodyToDicWithMessageBody:(HTMessage *)aMessage;

+ (void)reWriteMessageBodyWithMessage:(HTMessage *)aMessage;

@end

@interface IHTFileMessageBody : NSObject

@property(nonatomic,strong)NSString * remotePath;
@property(nonatomic,strong)NSString * fileName;
@property(nonatomic,assign)NSInteger fileSize;
@property(nonatomic,strong)NSString *localPath;

@end

@interface IHTTextMessageBody : NSObject
@property(nonatomic,strong)NSString *content;
@end

@interface IHTImageMessageBody : IHTFileMessageBody
@property(nonatomic,strong)NSString * thumbnailRemotePath;
@property(nonatomic,strong)NSString * thumbnailLocalPath;
@property(nonatomic,strong)NSString * size;
@end


@interface IHTAudioMessageBody : IHTFileMessageBody
@property(nonatomic,strong)NSString * audioDuration;
@end


@interface IHTVideoMessageBody : IHTFileMessageBody
@property(nonatomic,strong)NSString * thumbnailRemotePath;
@property(nonatomic,strong)NSString * thumbnailLocalPath;
@property(nonatomic,assign)CGFloat videoDuration;
@property(nonatomic,strong)NSString * size;
@end


@interface IHTLocationMessageBody : IHTFileMessageBody
@property(nonatomic,assign)CGFloat latitude;
@property(nonatomic,assign)CGFloat longitude;
@property(nonatomic,strong)NSString * address;
@end








