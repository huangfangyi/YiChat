//
//  YiChatDynamicDataSource.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YiChatDynamicModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatDynamicDataSource : NSObject

@property (nonatomic,strong) YiChatDynamicModel *model;

@property (nonatomic,strong) NSIndexPath *index;

@property (nonatomic,strong) NSAttributedString *showContentStr;

@property (nonatomic,assign) CGRect showContentRect;

@property (nonatomic,assign) CGFloat iconsVideosBeginY;

@property (nonatomic,assign) CGFloat downBarBeginY;

@property (nonatomic,assign) CGFloat praiseBeginY;

@property (nonatomic,strong) NSArray *urlIcons;

@property (nonatomic,strong) NSArray *urlThumbIcons;

@property (nonatomic,strong) NSArray *urlVideos;

@property (nonatomic,strong) NSString *urlVideoThumbs;

@property (nonatomic,assign) CGFloat timeSizeW;

@property (nonatomic,assign) NSInteger type;

@property (nonatomic,strong) NSAttributedString *showPraiseListStr;
@property (nonatomic,strong) NSString *praiseListStr;
@property (nonatomic,assign) CGRect showPraiseStrRect;
@property (nonatomic,assign) NSInteger praiseCount;

@property (nonatomic,strong) NSArray <NSAttributedString *>*showCommentListStrArr;
@property (nonatomic,strong) NSArray <NSString *>*commentStrArr;
@property (nonatomic,strong) NSArray <NSValue *> *showCommentStrRectArr;
@property (nonatomic,assign) NSInteger commentCount;


- (id)initWithDynamicModel:(YiChatDynamicModel *)model;

- (void)update;

- (NSString *)getTrendId;

- (NSString *)getUserIdStr;

- (NSString *)getDynamciTime;

- (NSString *)getUserIconUrl;

- (NSArray *)getDynamicUrlIcons;

- (NSArray *)getDynamicVideourls;

- (NSString *)getUserNickName;

- (CGFloat)getHeaderH;

- (CGFloat)getCellH:(NSIndexPath *)index;

- (CGFloat)getFooterH;

- (NSArray *)getPraiseList;

- (NSInteger)getPraiseCount;

- (NSArray *)getCommentList;

- (NSInteger)getCommentCount;

@end

NS_ASSUME_NONNULL_END
