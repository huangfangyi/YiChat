/*!
@header  HTMessageBody.h

@abstract 

@author  Created by 非夜 on 16/11/25.

@version 1.0 16/11/25 Creation(HTMessage Born)

  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
*/

#import <UIKit/UIKit.h>


/**
 消息下载状态

 - DownloadStateNorlmal: 未下载
 - DownloadStateDownloading: 下载中
 - DownloadStateSuccessed: 下载成功
 */
typedef NS_ENUM(NSUInteger,DownloadState){
    DownloadStateNorlmal = 0,
    DownloadStateDownloading,
    DownloadStateSuccessed,
};

/**
 消息内容
 */
@interface HTMessageBody : NSObject

/**
 消息文本内容
 */
@property(nonatomic,strong)NSString *content;

/**
 消息类型：Text,Image,Audio,Video,Position,File
 */
@property(nonatomic,strong)NSString * messageType;

/**
 Image消息，image 原图 size的大小
 */
@property(nonatomic,strong)NSString * size;

/**
 Audio消息的语音时长
 */
@property(nonatomic,strong)NSString * audioDuration;

/**
 File消息的大小
 */
@property(nonatomic,strong)NSString * fileType;

/**
 文件消息的名称
 */
@property(nonatomic,strong)NSString * fileName;

/**
 Image,Audio,Video,File这几种文件类消息在远程服务器的地址
 */
@property(nonatomic,strong)NSString * remotePath;

/**
 位置消息的地址
 */
@property(nonatomic,strong)NSString * address;

/**
 位置消息的纬度
 */
@property(nonatomic,assign)CGFloat latitude;

/**
 位置消息的经度
 */
@property(nonatomic,assign)CGFloat longitude;

/**
 消息发送时的时间戳
 */
@property(nonatomic,assign)NSInteger timestamp;

/**
 文件大小 （单位）Bytes
 */
@property(nonatomic,assign)NSInteger fileSize;

/**
 缩略文件本地地址，针对图片
 */
@property(nonatomic,strong)NSString * thumbnailLocalPath;

/**
 缩略文件
 */
@property(nonatomic,strong)NSString * thumbnailRemotePath;

/**
 文件本地地址
 */
@property(nonatomic,strong)NSString * localPath;

/**
 视频时长
 */
@property(nonatomic,assign)CGFloat videoDuration;


@end

