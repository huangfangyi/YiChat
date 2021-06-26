//
//  YiChatChangeSexCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatChangeSexCell.h"
#import "ServiceGlobalDef.h"
#import "ProjectCommonCellModel.h"
@interface YiChatChangeSexCell ()

@property (nonatomic,strong) UILabel *title;

@property (nonatomic,strong) UIImageView *selecte;

@end

@implementation YiChatChangeSexCell


+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine{
    return [[self alloc] initWithStyle:style reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth isHasDownLine:isHasDownLine];
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth isHasDownLine:isHasDownLine];
    if(self){
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    UILabel *title = [ProjectHelper helper_factoryMakeLabelWithFrame:[self getTitleFrame] andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
    [self.contentView addSubview:title];
    _title = title;
    
    
    CGFloat w = 20.0;
    CGFloat h = w;
    
    _selecte = [[UIImageView alloc] initWithFrame:CGRectMake(self.sCellWidth - PROJECT_SIZE_NAV_BLANK - w, (self.sCellHeight / 2 - h / 2) , w, h)];
    [self.contentView addSubview:_selecte];
    _selecte.layer.cornerRadius = _selecte.frame.size.height / 2;
    _selecte.clipsToBounds = YES;
    

}

- (void)setCellModel:(ProjectCommonCellModel *)cellModel{
    if([cellModel isKindOfClass:[ProjectCommonCellModel class]]){
        _cellModel = cellModel;
        
        _selecte.image = [self getSelcteIconWithState:_cellModel.isSelecte];
        if([_cellModel.titleStr isKindOfClass:[NSString class]]){
            _title.text = _cellModel.titleStr;
        }
    }
   
    
}


- (CGRect)getTitleFrame{
    CGFloat x = PROJECT_SIZE_NAV_BLANK;
    CGFloat y = 0;
    CGFloat w = 100.0;
    CGFloat h = self.sCellHeight;
    return CGRectMake(x, y, w, h);
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
