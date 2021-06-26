//
//  YiChatDynamicDataSource.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatDynamicDataSource.h"
#import "YiChatDynamicUIConfigure.h"

@interface YiChatDynamicDataSource ()

@property (nonatomic,assign) YiChatDynamicUIConfigure *uiConfigure;

@property (nonatomic,assign) CGFloat headerH;
@property (nonatomic,strong) NSArray *cellHArr;
@property (nonatomic,assign) CGFloat footerH;

//有多少行
@property (nonatomic,assign) NSInteger numOfLines;


@end

@implementation YiChatDynamicDataSource

- (id)initWithDynamicModel:(YiChatDynamicModel *)model{
    self = [super init];
    if(self){
        _uiConfigure = [YiChatDynamicUIConfigure initialUIConfigure];
        self.model = model;
    }
    return self;
}

- (void)setModel:(YiChatDynamicModel *)model{
    if(model && [model isKindOfClass:[YiChatDynamicModel class]]){
        _model = model;
        
        ////1文本 2 图片 3视频
        
        
        _cellHArr = nil;
        _headerH = 0;
        _footerH = 0;
        
        NSString *content = [self getContentStr];
        
        if(content && [content isKindOfClass:[NSString class]]){
            _showContentStr = [_uiConfigure tranlateStringToAttributedString:content font:_uiConfigure.dynamicContentFont];
        }
        else{
            _showContentStr = nil;
        }
        
        NSString *time = [self getDynamciTime];
        if(time && [time isKindOfClass:[NSString class]]){
            _timeSizeW = [ProjectHelper helper_getFontSizeWithString:time useSetFont:_uiConfigure.dynamicTimeFont withWidth:MAXFLOAT andHeight:_uiConfigure.dynamicToolBarSize.height].size.width;
        }
        else{
            _timeSizeW = 0;
        }
        
        NSArray *iconsArr = [self getDynamicUrlIcons];
        NSArray *videoArr = [self getDynamicVideourls];
        
        BOOL isHasIcons = NO;
        BOOL isHasVideos = NO;
        
        if((iconsArr && [iconsArr isKindOfClass:[NSArray class]])){
            if(iconsArr.count > 0){
                isHasIcons = YES;
            }
        }
        
        if((videoArr && [videoArr isKindOfClass:[NSArray class]])){
            if(videoArr.count > 0){
                isHasVideos = YES;
            }
        }
        
        if(isHasVideos == NO && isHasIcons == NO){
            _type = 1;
        }
        else if(isHasIcons == YES && isHasVideos == NO){
            _type = 2;
            NSArray *urlIcons = [self getDynamicUrlIcons];
            if(urlIcons && [urlIcons isKindOfClass:[NSArray class]]){
                _urlIcons = urlIcons;
                NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
                for (int i = 0; i < urlIcons.count; i ++) {
                    NSString *url = urlIcons[i];
                    if(url && [url isKindOfClass:[NSString class]]){
                        
                        CGFloat w = 240;
                        CGFloat h = 240;
                        //?x-oss-process=image/crop,w_%.0f,h_%.0f,g_center
                        
                       // NSString *thumb = [NSString stringWithFormat:@"%@?x-oss-process=image/resize,m_fill,w_%.0f,h_%.0f",url,w,h];
                        
                         NSString *thumb = [NSString stringWithFormat:@"%@?x-oss-process=image/resize,m_fill,w_%.0f,h_%.0f",url,w,h];
                        [tmp addObject:thumb];
                    }
                }
                if(tmp.count != 0){
                    _urlThumbIcons = tmp;
                }
            }
        }
        else if(isHasIcons == NO && isHasVideos == YES){
            _type = 3;
            NSArray *videoUrl = [self getDynamicVideourls];
            if(videoUrl && [videoUrl isKindOfClass:[NSArray class]]){
                NSString *videoUrlStr  = videoUrl.firstObject;
                if(videoUrlStr && [videoUrlStr isKindOfClass:[NSString class]]){
                    
                    _urlVideoThumbs = [NSString stringWithFormat:@"%@?x-oss-process=video/snapshot,t_0,f_jpg,w_%.0f,h_%.0f,ar_auto",videoUrlStr,_uiConfigure.videoSize.width,_uiConfigure.videoSize.height];
                    
                }
            }
           
        }
        
        NSArray *praiseListarr = [self getPraiseList];
        if(praiseListarr && [praiseListarr isKindOfClass:[NSArray class]]){
            NSMutableArray *temp = [NSMutableArray arrayWithCapacity:0];
            for (int i = 0;i < praiseListarr.count;i ++) {
                YiChatDynamicPraiseEntityModel *model = praiseListarr[i];
                if(model && [model isKindOfClass:[YiChatDynamicPraiseEntityModel class]]){
                    NSString *usernick = model.nick;
                    if([usernick isKindOfClass:[NSString class]] && usernick){
                        
                    }
                    else{
                        usernick = @"";
                    }
                    [temp addObject:usernick];
                }
                else{
                    [temp addObject:@""];
                }
            }
            if(temp.count > 0 ){
                _praiseListStr = [temp componentsJoinedByString:@","];
                
                NSTextAttachment *attach = [[NSTextAttachment alloc] init];
                attach.bounds = CGRectMake(0,- 5.0, 20.0, 20);
                attach.image = _uiConfigure.dynamicLikeIcon;
                
               NSMutableAttributedString *str = [[NSMutableAttributedString alloc] init];
                
                [str appendAttributedString:[NSAttributedString attributedStringWithAttachment:attach]];
                
                NSAttributedString *textAttribute = [_uiConfigure tranlateStringToAttributedString:_praiseListStr font:_uiConfigure.dynamicPraiseFont];
                if(textAttribute && [textAttribute isKindOfClass:[NSAttributedString class]]){
                    [str appendAttributedString:textAttribute];
                }
                _showPraiseListStr = str;
            }
            else{
                _praiseListStr = @"";
                
                _praiseListStr = @"";
                
                _showPraiseListStr = [[NSAttributedString alloc] initWithString:@""];
            }
        }
        _praiseCount = [self getPraiseCount];
        
        NSArray *commentListArr = [self getCommentList];
        
        NSMutableArray *tmpShowAttributeStrArr = [NSMutableArray arrayWithCapacity:0];
        NSMutableArray *tmpShowStrArr = [NSMutableArray arrayWithCapacity:0];
        NSMutableArray *tmpShowCommentCotentRectArr = [NSMutableArray arrayWithCapacity:0];
        NSMutableArray *tmpCellHArr = [NSMutableArray arrayWithCapacity:0];
        
        if(commentListArr && [commentListArr isKindOfClass:[NSArray class]]){
            for (int i = 0; i < commentListArr.count; i ++) {
                YiChatDynamicCommitEntityModel *model = commentListArr[i];
                
                NSString *comment = @"";
                //评论人
                NSString *nickCommter = @"";
                //评论人
                NSInteger userIdCommenter = model.userId;
                //被评论人
                NSString *nickCommented = @"";
                //被评论人
                NSInteger userIdCommented = model.srcUserId;
                
                NSString *dynamicSenderNick = [self getUserNickName];
                NSInteger dynamicSenderUserId = [[self getUserIdStr] integerValue];
                
                if((model && [model isKindOfClass:[YiChatDynamicCommitEntityModel class]])){
                    NSString *commentStr = model.content;
                    NSString *commterNickStr = model.nick;
                    NSString *commteredNickStr = model.srcNick;
                    
                    if(commentStr && [commentStr isKindOfClass:[NSString class]]){
                        comment = commentStr;
                    }
                    if(commterNickStr && [commterNickStr isKindOfClass:[NSString class]]){
                        nickCommter = commterNickStr;
                    }
                    if(commteredNickStr && [commteredNickStr isKindOfClass:[NSString class]]){
                        nickCommented = commteredNickStr;
                    }
                }
                
                //评论动态
                //评论他们评论
                NSString *showStr = [NSString stringWithFormat:@"%@ %@ %@",nickCommter,@":",comment];
                
                NSAttributedString *showAttributeStr = [ProjectHelper helper_factoryFontMakeAttributedStringWithTwoDiffirrentTextWhileSpecialWithRange:NSMakeRange(0, nickCommter.length) font:_uiConfigure.dynamicCommitUserNickFont andFont:_uiConfigure.dynamicCommitFont color:_uiConfigure.dynamicCommitUserNickColor color:_uiConfigure.dynamicCommitColor withText:showStr];
                
                if(!(showStr && [showStr isKindOfClass:[NSString class]])){
                    showStr = @"";
                }
                
                if(!(comment && [comment isKindOfClass:[NSString class]])){
                    comment = @"";
                }
                [tmpShowStrArr addObject:comment];
                
                if(!(showAttributeStr && [showAttributeStr isKindOfClass:[NSAttributedString class]])){
                    showAttributeStr = [[NSAttributedString alloc] initWithString:@""];
                }
                [tmpShowAttributeStrArr addObject:showAttributeStr];
                
                CGRect rect = [_uiConfigure getTextMessageRectWithText:showAttributeStr withMaxSize:CGSizeMake(_uiConfigure.commitMaxSize, MAXFLOAT) font:_uiConfigure.dynamicCommitFont];
                
                [tmpShowCommentCotentRectArr addObject:[NSValue valueWithCGRect:rect]];
                
                [tmpCellHArr addObject:[NSNumber numberWithFloat:rect.size.height + 5 * 2]];
            }
            
            _showCommentListStrArr = tmpShowAttributeStrArr;
            _commentStrArr = tmpShowStrArr;
            _showCommentStrRectArr = tmpShowCommentCotentRectArr;
            _cellHArr = tmpCellHArr;
        }
        _commentCount = [self getCommentCount];
    }
}

- (void)update{
    self.model = _model;
}

- (CGFloat)getHeaderH{
    
    if(_headerH != 0){
        return _headerH;
    }
    
    CGFloat blank = _uiConfigure.contentBlank;
    CGFloat iconSize = _uiConfigure.userIconRect.size.height;
    CGFloat nickSize = _uiConfigure.userNickSize.height;
    _showContentRect = [_uiConfigure getTextMessageRectWithText:_showContentStr];
    
    CGFloat contentH = _showContentRect.size.height + nickSize + blank ;
    
    if(contentH <= iconSize){
        contentH = iconSize;
    }
    
    CGFloat h = 0;
    h += blank;
    
    if(_showContentRect.size.height != 0){
        h += (contentH + blank);
    }
    else{
        h += contentH;
    }
    
    _iconsVideosBeginY = h;
    
    NSArray *urlIcons = nil;
    if(!(urlIcons && [urlIcons isKindOfClass:[NSArray class]])){
        urlIcons = [self getDynamicUrlIcons];
    }
    
    if(urlIcons.count != 0){
        CGFloat interBlank = _uiConfigure.imagesInterBlank;
        NSInteger rows = [self getIconsLinesWithUrlIcons:urlIcons];
        h += (rows - 1) * interBlank + _uiConfigure.singleImageSize.height * rows;
        h += 10.0;
    }
    
    NSArray *urlVideos = nil;
    if(!(urlVideos && [urlVideos isKindOfClass:[NSArray class]])){
        urlVideos = [self getDynamicVideourls];
        _urlVideos = urlVideos;
    }
    
    if(urlVideos.count != 0){
        h += (_uiConfigure.videoSize.height) + 10.0;
    }
    
    _downBarBeginY = h;
    
    h += _uiConfigure.dynamicToolBarSize.height;
    
    if(_praiseCount > 0){
        _praiseBeginY = h;
        _showPraiseStrRect = [_uiConfigure getTextMessageRectWithText:_showPraiseListStr withMaxSize:CGSizeMake(_uiConfigure.praiseMaxSize, MAXFLOAT) font:_uiConfigure.dynamicPraiseFont];
        if(_showPraiseStrRect.size.height != 0){
            h += _showPraiseStrRect.size.height;
        }
    }
    
    _headerH = (h + 5.0);
    
    return _headerH;
    
}

- (NSInteger)numOfCell{
    if(self){
        return _commentCount;
    }
    return 0;
}

- (CGFloat)getCellH:(NSIndexPath *)index{
    if(_cellHArr && [_cellHArr isKindOfClass:[NSArray class]]){
        if(_cellHArr.count == 0){
            return 0;
        }
        if(_cellHArr.count - 1 >= index.row){
            NSNumber *cellH = _cellHArr[index.row];
            if(cellH && [cellH isKindOfClass:[NSNumber class]]){
                return [cellH floatValue];
            }
        }
    }
    return 0;
}

- (CGFloat)getFooterH{
    return 0.00001f;
}

- (NSString *)getTrendId{
    if(_model && [_model isKindOfClass:[YiChatDynamicModel class]]){
       return [NSString stringWithFormat:@"%ld",_model.trendId];
    }
    return @"";
}

- (NSString *)getUserIdStr{
    if(_model && [_model isKindOfClass:[YiChatDynamicModel class]]){
        if(_model.userId && [_model.userId isKindOfClass:[NSString class]]){
            return _model.userId;
        }
        if(_model.userId && [_model.userId isKindOfClass:[NSNumber class]]){
            return [NSString stringWithFormat:@"%ld",[_model.userId integerValue]];
        }
    }
    return @"";
}

- (NSString *)getDynamciTime{
    if(_model && [_model isKindOfClass:[YiChatDynamicModel class]]){
        if(_model.timeDesc && [_model.timeDesc isKindOfClass:[NSString class]]){
            return _model.timeDesc;
        }
    }
    return @"";
}

- (NSInteger)getIconsLinesWithUrlIcons:(NSArray *)urlIcons{
    if(urlIcons && [urlIcons isKindOfClass:[NSArray class]]){
        NSInteger rows = urlIcons.count % _uiConfigure.numOfLineIcons;
        if(rows == 0){
            return urlIcons.count / _uiConfigure.numOfLineIcons;
        }
        else{
            return (urlIcons.count / _uiConfigure.numOfLineIcons + 1);
        }
    }
    return 0;
}


- (NSString *)getContentStr{
    if(_model && [_model isKindOfClass:[YiChatDynamicModel class]]){
        if(_model.content && [_model.content isKindOfClass:[NSString class]]){
            return _model.content;
        }
    }
    return @"";
}

- (NSArray *)getDynamicUrlIcons{
    if(_model && [_model isKindOfClass:[YiChatDynamicModel class]]){
        if(_model.imgs && [_model.imgs isKindOfClass:[NSString class]]){
            NSArray *icons = [_model.imgs componentsSeparatedByString:@","];
            return icons;
        }
    }
    return @[];
}

- (NSArray *)getDynamicVideourls{
    if(_model && [_model isKindOfClass:[YiChatDynamicModel class]]){
        if(_model.videos && [_model.videos isKindOfClass:[NSString class]]){
            NSArray *icons = [_model.videos componentsSeparatedByString:@","];
            return icons;
        }
    }
    return @[];
}

- (NSString *)getUserIconUrl{
    if(_model && [_model isKindOfClass:[YiChatDynamicModel class]]){
        if(_model.avatar && [_model.avatar isKindOfClass:[NSString class]]){
            if(_model.avatar.length > 0){
                return _model.avatar;
            }
            else{
                return nil;
            }
        }
    }
    return nil;
}

- (NSString *)getUserNickName{
    if(_model && [_model isKindOfClass:[YiChatDynamicModel class]]){
        if(_model.nick && [_model.nick isKindOfClass:[NSString class]]){
            return _model.nick;
        }
    }
    return nil;
}

- (NSArray *)getPraiseList{
    if(_model && [_model isKindOfClass:[YiChatDynamicModel class]]){
        if(_model.praiseList && [_model.praiseList isKindOfClass:[NSArray class]]){
            return _model.praiseList;
        }
    }
    return nil;
}

- (NSInteger)getPraiseCount{
    if(_model && [_model isKindOfClass:[YiChatDynamicModel class]]){
        return _model.praiseCount;
    }
    return 0;
}

- (NSArray *)getCommentList{
    if(_model && [_model isKindOfClass:[YiChatDynamicModel class]]){
        if(_model.commentList && [_model.commentList isKindOfClass:[NSArray class]]){
            return _model.commentList;
        }
    }
    return nil;
}

- (NSInteger)getCommentCount{
    if(_model && [_model isKindOfClass:[YiChatDynamicModel class]]){
        return _model.commentCount;
    }
    return 0;
}
@end
