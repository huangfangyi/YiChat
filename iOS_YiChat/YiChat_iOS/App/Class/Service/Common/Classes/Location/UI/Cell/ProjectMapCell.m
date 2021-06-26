//
//  ProjectMapCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/16.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectMapCell.h"
#import "ProjectLocationManager.h"
#import "ServiceGlobalDef.h"

@interface ProjectMapCell ()
{
    UILabel *_lab;
    UILabel *_address;
    UIImageView *_selecteImg;
    
}
@end

@implementation ProjectMapCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}


+ (id)initalWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine{
    
    ProjectMapCell *cell = [[self alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth isHasDownLine:isHasDownLine];
    cell.selectionStyle=UITableViewCellSelectionStyleNone;
    
    [cell makeUI];
    
    return cell;
}

- (void)makeUI{
    
    UIImage *icon = nil;
    CGFloat iconW = 12.0;
    CGFloat iconH = 12.0;
    
    
    CGFloat x = PROJECT_SIZE_NAV_BLANK;
    CGFloat y = 5.0;
    CGFloat w = self.sCellWidth - x * 3 - iconW;
    CGFloat h = (self.sCellHeight - 10.0) / 2;
    
    _lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(x, y, w, h) andfont:PROJECT_TEXT_FONT_COMMON(16) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
    [self.contentView addSubview:_lab];
    
    _address = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(x, y + h, w, h) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentLeft];
    [self.contentView addSubview:_address];
    
    _selecteImg = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(_lab.frame.size.width + _lab.frame.origin.x + x, self.sCellHeight / 2 - iconH / 2, iconW, iconH) andImg:icon];
    [self.contentView addSubview:_selecteImg];
}

- (void)setNavValue:(AMapPOI *)dic selecte:(NSInteger)selecte{
    
    _lab.text = dic.name;
    _address.text = dic.address;
    
    if(selecte == self.sIndexPath.row){
        _selecteImg.image = nil;
    }
    else{
        _selecteImg.image = nil;
    }
    
}


- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
