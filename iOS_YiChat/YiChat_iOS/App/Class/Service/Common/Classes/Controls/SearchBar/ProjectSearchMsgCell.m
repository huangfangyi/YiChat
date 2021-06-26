//
//  ProjectSearchMsgCell.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/1.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "ProjectSearchMsgCell.h"
#import "HTMessage.h"
#import "HTConversation.h"
#import "ZFChatUIHelper.h"

@interface ProjectSearchMsgCell ()
@property (nonatomic,strong) UIImageView *iconImgView;
@property (nonatomic,strong) UILabel *nameLa;
@property (nonatomic,strong) UILabel *contentLa;

@property (nonatomic,strong) UILabel *deteLa;
@end

@implementation ProjectSearchMsgCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

-(instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    if (self == [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        self.iconImgView = [[UIImageView alloc] initWithFrame:CGRectZero];
        [self.contentView addSubview:self.iconImgView];
        [self.iconImgView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.mas_equalTo(0);
            make.left.mas_equalTo(10);
            make.size.mas_equalTo(CGSizeMake(40, 40));
        }];
    }
    
    self.nameLa = [[UILabel alloc]initWithFrame:CGRectZero];
    [self.contentView addSubview:self.nameLa];
    [self.nameLa mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.iconImgView.mas_right).offset(10);
        make.bottom.equalTo(self.iconImgView.mas_centerY).offset(0);
        make.right.mas_equalTo(-10);
        make.top.mas_equalTo(10);
    }];
    
    self.deteLa = [[UILabel alloc]initWithFrame:CGRectZero];
    self.deteLa.textAlignment = NSTextAlignmentRight;
    self.deteLa.textColor = [UIColor lightGrayColor];
    self.deteLa.font = [UIFont systemFontOfSize:12];
    [self.contentView addSubview:self.deteLa];
    [self.deteLa mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-10);
        make.centerY.equalTo(self.nameLa.mas_centerY).offset(0);
        make.height.mas_equalTo(20);
    }];
    
    self.contentLa = [[UILabel alloc]initWithFrame:CGRectZero];
    self.contentLa.textColor = [UIColor lightGrayColor];
    self.contentLa.font = [UIFont systemFontOfSize:14];
    [self.contentView addSubview:self.contentLa];
    [self.contentLa mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.iconImgView.mas_right).offset(10);
        make.top.equalTo(self.iconImgView.mas_centerY).offset(0);
        make.right.mas_equalTo(-10);
        make.bottom.mas_equalTo(-10);
    }];
    
    UIView *line = [[UIView alloc]initWithFrame:CGRectZero];
    line.backgroundColor = [UIColor groupTableViewBackgroundColor];
    [self.contentView addSubview:line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.nameLa.mas_left).offset(0);
        make.bottom.mas_equalTo(-0.5);
        make.right.mas_equalTo(0);
        make.height.mas_equalTo(0.5);
    }];
    return self;
}

-(void)setDataArr:(NSArray *)dataArr{
    
    _dataArr = dataArr;
    self.deteLa.hidden = YES;
    HTMessage *message = dataArr.firstObject;
    if (dataArr.count == 1) {
        self.contentLa.text = message.body.content;
    }else{
        self.contentLa.text = [NSString stringWithFormat:@"%lu条相关聊天记录",(unsigned long)dataArr.count];
    }
    
    YiChatUserModel *userModel = [YiChatUserModel mj_objectWithKeyValues:[[YiChatUserManager defaultManagaer] getCashUserDicInfo]];
    
    if ([message.chatType isEqualToString:@"2"]) {
        [[YiChatUserManager defaultManagaer] fetchGroupInfoWithGroupId:message.to invocation:^(YiChatGroupInfoModel * _Nonnull model, NSString * _Nonnull error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.iconImgView sd_setImageWithURL:[NSURL URLWithString:model.groupAvatar] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
                self.nameLa.text = model.groupName;
            });
        }];
    }else{
        if (message.to.integerValue != userModel.userId) {
            [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:message.to invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.iconImgView sd_setImageWithURL:[NSURL URLWithString:model.avatar] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
                    self.nameLa.text = model.nick;
                });
            }];
        }else{
            [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:message.from invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.iconImgView sd_setImageWithURL:[NSURL URLWithString:model.avatar] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
                        self.nameLa.text = model.nick;
                    });
                });
            }];
        }
    }
}

-(void)setMessage:(HTMessage *)message{
    _message = message;
    NSDictionary *dic = message.ext;
    [self.iconImgView sd_setImageWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"%@",dic[@"avatar"]]] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
    self.nameLa.text = [NSString stringWithFormat:@"%@",dic[@"nick"]];
    self.contentLa.text = message.body.content;
    
    NSDate * date = [NSDate dateWithTimeIntervalSince1970:message.timestamp / 1000];
    self.deteLa.text = [ZFChatUIHelper zfChatUIHelperConversationLastMessageTimeWithDate:date];
}


- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
