//
//  ZFEmojiListView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ProjectBaseTableView.h"
NS_ASSUME_NONNULL_BEGIN

@interface ZFEmojiListView : ProjectBaseTableView

@property (nonatomic,strong) NSString * identifier;
@property (nonatomic) CGFloat cellH;
@property (nonatomic,strong) NSArray *dataSourceArr;

- (void)makeUI;

- (void)addSelecteEmojiInvocation:(NSDictionary *)invocattion;

- (void)updateTable;
@end

@interface ZFEmojiViewCell :UITableViewCell

@property (nonatomic,strong) NSNumber *itemsPer_row;


- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier type:(NSInteger)type ;

- (void)createUI;

- (void)addInvocation:(NSDictionary *)invocattion;

- (void)getDataWithArr:(NSArray *)arr;

@end

@interface ZFDefaultEmojiUI :UIView

@property (nonatomic,strong) UIImageView *defaultIcon;

- (id)initWithFrame:(CGRect)frame image:(UIImage *)img num:(NSInteger)num click:(NSDictionary *)clickInvocatiton;

- (void)makeUI;
@end


@interface ZFAddeMojiUI : UIView

@property (nonatomic,strong) UIImageView *addIcon;

- (id)initWithFrame:(CGRect)frame image:(UIImage *)img num:(NSInteger)num click:(NSDictionary *)clickInvocatiton;

- (void)makeUI;

@end

NS_ASSUME_NONNULL_END
