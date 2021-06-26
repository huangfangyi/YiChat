//
//  YiChatPhoneConnectionSelecteVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatPhoneConnectionSelecteCell.h"
#import "ServiceGlobalDef.h"
#import "YiChatPhoneConnectionModel.h"

@interface YiChatPhoneConnectionSelecteCell ()
{
    NSInteger _type;
}


@property (nonatomic,assign) BOOL isSelecte;
@property (nonatomic,strong) UILabel *nick;
@property (nonatomic,strong) UILabel *phone;
@property (nonatomic,strong) UIImageView *selecte;

@end

@implementation YiChatPhoneConnectionSelecteCell

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
    if(_type == 0){
        _selecte = [[UIImageView alloc] initWithFrame:CGRectMake(10.0, 20.0, self.sCellHeight - 40.0, self.sCellHeight - 40.0)];
        [self.contentView addSubview:_selecte];
        _selecte.layer.cornerRadius = _selecte.frame.size.height / 2;
        _selecte.clipsToBounds = YES;
        
        CGFloat x = _selecte.frame.origin.x + _selecte.frame.size.width + 20.0;
        CGFloat w = self.sCellWidth - x - 10.0;
        
        CGFloat itemH = (self.sCellHeight - 10.0) / 2;
        
        _nick = [[UILabel alloc] initWithFrame:CGRectMake(x, 5.0, w, itemH)];
        [self.contentView addSubview:_nick];
        _nick.textAlignment = NSTextAlignmentLeft;
        _nick.textColor = PROJECT_COLOR_APPTEXT_MAINCOLOR;
        _nick.font = PROJECT_TEXT_FONT_COMMON(15.0);
        
        _phone = [[UILabel alloc] initWithFrame:CGRectMake(x,_nick.frame.origin.y + _nick.frame.size.height, w, itemH)];
        [self.contentView addSubview:_phone];
        _phone.textAlignment = NSTextAlignmentLeft;
        _phone.textColor = PROJECT_COLOR_APPTEXT_SUBCOLOR;
        _phone.font = PROJECT_TEXT_FONT_COMMON(12.0);
    }
}

- (void)updateType:(NSInteger)type{
    _type = type;
}

- (void)setContactEntity:(YiChatContactEntity *)contactEntity{
    _contactEntity = contactEntity;
    
    _selecte.image = [self getSelcteIconWithState:_contactEntity.isSelecte];
    if([_contactEntity.connectionName isKindOfClass:[NSString class]]){
        _nick.text = _contactEntity.connectionName;
    }
    
    if([_contactEntity.phoneNum isKindOfClass:[NSString class]]){
        _phone.text = _contactEntity.phoneNum;
    }
    
}

- (UIImage *)getSelcteIconWithState:(BOOL)state{
    if(state){
        return [UIImage imageNamed:@"selecteCircle@3x.png"];
    }
    else{
        return [UIImage imageNamed:@"unselecteCircle@3x.png"];
    }
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
