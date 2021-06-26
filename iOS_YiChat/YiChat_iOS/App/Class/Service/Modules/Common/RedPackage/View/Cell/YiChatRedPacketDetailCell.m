//
//  YiChatRedPacketDetailCell.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/11.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatRedPacketDetailCell.h"

#define Radius 20

@interface YiChatRedPacketDetailCell ()
@property (nonatomic,strong) UIImageView *avterImageView;
@property (nonatomic,strong) UILabel *nickNameLa;
@property (nonatomic,strong) UILabel *dateLa;
@property (nonatomic,strong) UILabel *moneyLa;

@property (nonatomic,strong) UIImageView *bastLuckImg;
@property (nonatomic,strong) UILabel *bastLuckLa;
@end

@implementation YiChatRedPacketDetailCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

-(instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        UIView *line = [[UIView alloc]initWithFrame:CGRectMake(15, 0, PROJECT_SIZE_WIDTH - 15, 0.5)];
        line.backgroundColor = [UIColor lightGrayColor];
        [self.contentView addSubview:line];
        
        
        self.avterImageView = [[UIImageView alloc]init];
        self.avterImageView.layer.masksToBounds = YES;
        self.avterImageView.layer.cornerRadius = Radius;
        self.avterImageView.backgroundColor = [UIColor blueColor];
        [self.contentView addSubview:self.avterImageView];
        [self.avterImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.mas_equalTo(0);
            make.left.mas_equalTo(10);
            make.size.mas_equalTo(CGSizeMake(Radius * 2, Radius * 2));
        }];
        
        self.nickNameLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.nickNameLa.text = @"廉颇老矣";
        
        [self.contentView addSubview:self.nickNameLa];
        [self.nickNameLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.avterImageView.mas_right).offset(10);
            make.top.mas_equalTo(10);
            make.size.mas_equalTo(CGSizeMake(200, 20));
        }];
        
        self.dateLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.dateLa.text = @"2019-12-23 10：23：21";
        self.dateLa.font = [UIFont systemFontOfSize:13];
        self.dateLa.textColor = [UIColor grayColor];
        self.dateLa.alpha = 0.6;
        [self.contentView addSubview:self.dateLa];
        [self.dateLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.avterImageView.mas_right).offset(10);
            make.bottom.mas_equalTo(-10);
            make.size.mas_equalTo(CGSizeMake(200, 20));
        }];
        
        self.moneyLa = [[UILabel alloc]initWithFrame:CGRectZero];
        self.moneyLa.text = @"2019元";
        self.moneyLa.font = [UIFont systemFontOfSize:13];
        self.moneyLa.textAlignment = NSTextAlignmentRight;
        [self.contentView addSubview:self.moneyLa];
        [self.moneyLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.mas_equalTo(-10);
            make.top.mas_equalTo(10);
            make.left.equalTo(self.avterImageView.mas_right).offset(5);
            make.height.mas_equalTo(20);
        }];
        
        [self.bastLuckLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.mas_equalTo(-5);
            make.bottom.mas_equalTo(-5);
            make.height.mas_equalTo(20);
        }];
        
        [self.bastLuckImg mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self.bastLuckLa.mas_left).offset(-5);
            make.bottom.mas_equalTo(-5);
            make.height.mas_equalTo(20);
            make.width.mas_equalTo(20);
        }];
    }
    return self;
}

-(UIImageView *)bastLuckImg{
    if(!_bastLuckImg){
        _bastLuckImg = [UIImageView new];
        [self.contentView addSubview:_bastLuckImg];
        _bastLuckImg.clipsToBounds = YES;
        _bastLuckImg.image = [UIImage imageNamed:@"bestluck"];
    }
    return _bastLuckImg;
}

-(UILabel *)bastLuckLa{
    if(!_bastLuckLa){
        _bastLuckLa = [UILabel new];
        [self.contentView addSubview:_bastLuckLa];
        _bastLuckLa.clipsToBounds = YES;
        _bastLuckLa.text = @"手气最佳";
        _bastLuckLa.textColor = [UIColor redColor];
        _bastLuckLa.font = [UIFont systemFontOfSize:12];
    }
    return _bastLuckLa;
}

-(void)setIsLuck:(BOOL)isLuck{
    _isLuck = isLuck;
    if (isLuck) {
        self.bastLuckImg.hidden = NO;
        self.bastLuckLa.hidden = NO;
    }else{
        self.bastLuckImg.hidden = YES;
        self.bastLuckLa.hidden = YES;
    }
}

-(void)setModel:(YiChatRedPacketInfoModel *)model{
    _model = model;
    [self.avterImageView sd_setImageWithURL:[NSURL URLWithString:model.avatar] placeholderImage:[UIImage imageNamed:PROJECT_ICON_USERDEFAULT]];
    self.nickNameLa.text = model.nick;
    self.dateLa.text = model.receiveTime;
    self.moneyLa.text = [NSString stringWithFormat:@"%.2f",model.money.floatValue];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
