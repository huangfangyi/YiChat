//
//  YRAddressPicker.m
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/18.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import "YRAddressPicker.h"
#import "YRGeneralApis.h"
@interface YRAddressPicker ()<UIPickerViewDelegate,UIPickerViewDataSource>

@end

@implementation YRAddressPicker

- (void)getData{
    
}

- (void)makeUI{
    NSArray *title = @[@"取消",@"确定"];
    CGFloat x = 10;
    CGFloat w = 60.0;
    CGFloat h = 30.0;
    
    UIButton *clearBtn = [YRGeneralApis yrGeneralApis_FactoryMakeClearButtonWithFrame:self.bounds target:self method:@selector(blankMethod:)];
    [self addSubview:clearBtn];
    
    UIView *back = [YRGeneralApis yrGeneralApis_FactoryMakeViewWithFrame:CGRectMake(0, self.frame.size.height - 180.0 - h, self.frame.size.width, h + 180.0) backGroundColor:[UIColor whiteColor]];
    back.layer.cornerRadius = 5.0;
    back.layer.borderWidth = 0.5;
    back.layer.borderColor = [UIColor whiteColor].CGColor;
    [self addSubview:back];
    
    UIPickerView *pickerView = [[UIPickerView alloc]initWithFrame:CGRectMake(0,h, self.frame.size.width, 162)];
    
    pickerView.backgroundColor = [UIColor clearColor];
    
    pickerView.delegate = self;
    
    pickerView.dataSource = self;
    
    [back addSubview:pickerView];
    
    [pickerView reloadAllComponents];//刷新UIPickerView
    
    _picker = pickerView;
    
    for (int i = 0; i < title.count; i++ ) {
        i == 0 ?( x = 10.0)  : (x = (self.frame.size.width - 10.0 - w));
        
        UIButton *btn = [YRGeneralApis yrGeneralApis_FactoryMakeButtonWithFrame:CGRectMake(x , 0 , w, h) andBtnType:UIButtonTypeRoundedRect];
        [btn setTitle:title[i] forState:UIControlStateNormal];
        btn.backgroundColor = [UIColor whiteColor];
        [btn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        if(i == 1){
            [btn addTarget:self action:@selector(sureMethod:) forControlEvents:UIControlEventTouchUpInside];
        }
        else{
            [btn addTarget:self action:@selector(cancelMethod:) forControlEvents:UIControlEventTouchUpInside];
        }
        [back addSubview:btn];
    }
}

- (void)blankMethod:(UIButton *)btn{
    [self cancelMethod:btn];
    if(self.dissapearClick){
        self.dissapearClick();
    }
}

- (void)sureMethod:(UIButton *)btn{
    if(self.dissapearClick){
        self.dissapearClick();
    }
    
    if(self.didSelecteAddress){
        
        NSMutableArray *selecteAddress = [NSMutableArray arrayWithCapacity:0];
        for (int  i = 0; i<self.currentSelecteComponetList.count; i++) {
            NSString *name = self.dataSource[self.currentSelecteComponetList[i].section][self.currentSelecteComponetList[i].row][@"name"];
            NSString *code = self.dataSource[self.currentSelecteComponetList[i].section][self.currentSelecteComponetList[i].row][@"code"];
            [selecteAddress addObject:[YRAddressEntity creaateWithAddressName:name addressCode:code]];
        }
        self.didSelecteAddress(selecteAddress);
    }
    [self removeFromSuperview];
    
}

- (void)cancelMethod:(UIButton *)btn{
    if(self.dissapearClick){
        self.dissapearClick();
    }
    [self removeFromSuperview];
}


- (void)hidden{
    [UIView animateWithDuration:0.5 animations:^{
        self.frame = CGRectMake(0, self.frame.size.height, self.frame.size.width, self.frame.size.height);
    } completion:^(BOOL finished) {
        for (id temp in self.subviews) {
            [temp removeFromSuperview];
        }
        [self removeFromSuperview];
    }];
}


-(NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView

{
    return _dataSource.count;
    
}

//返回指定列的行数

-(NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component

{
    if(_dataSource.count - 1 >= component){
        NSArray *rows = _dataSource[component];
        return rows.count;
    }
    else{
        return 0;
    }
}

//返回指定列，行的高度，就是自定义行的高度

- (CGFloat)pickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component{
    
    return 20.0f;
    
}

//返回指定列的宽度

- (CGFloat) pickerView:(UIPickerView *)pickerView widthForComponent:(NSInteger)component{
    
    return self.frame.size.width / _dataSource.count;
}



// 自定义指定列的每行的视图，即指定列的每行的视图行为一致

- (UIView *)pickerView:(UIPickerView *)pickerView viewForRow:(NSInteger)row forComponent:(NSInteger)component reusingView:(UIView *)view{
    
    if (!view){
        
        view = [[UIView alloc]init];
        
    }
    
    NSInteger totalComponents = _dataSource.count;
    
    UILabel *text = [[UILabel alloc]initWithFrame:CGRectMake(0, 0, self.frame.size.width / totalComponents, 20)];
    
    text.textAlignment = NSTextAlignmentCenter;
    
    NSString *appearText = _dataSource[component][row][@"name"];
    
    text.text = appearText;
    
    [view addSubview:text];
    
    //隐藏上下直线
    //
    //    [self.pickerView.subviews objectAtIndex:1].backgroundColor = [UIColor clearColor];
    //
    //    [self.pickerView.subviews objectAtIndex:2].backgroundColor = [UIColor clearColor];
    
    return view;
    
}

//显示的标题

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component{
    
    NSString *appearText = _dataSource[component][row][@"name"];
    
    return appearText;
    
}

//显示的标题字体、颜色等属性

- (NSAttributedString *)pickerView:(UIPickerView *)pickerView attributedTitleForRow:(NSInteger)row forComponent:(NSInteger)component{
    
    NSString *appearText = _dataSource[component][row][@"name"];
    
    NSString *str = appearText;
    
    NSMutableAttributedString *AttributedString = [[NSMutableAttributedString alloc]initWithString:str];
    
    [AttributedString addAttributes:@{NSFontAttributeName:[UIFont boldSystemFontOfSize:18], NSForegroundColorAttributeName:[UIColor whiteColor]} range:NSMakeRange(0, [AttributedString  length])];
    
    return AttributedString;
    
}//NS_AVAILABLE_IOS(6_0);


//被选择的行

-(void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component{
}


/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect {
 // Drawing code
 }
 */

@end

@implementation YRAddressEntity


- (instancetype)initWithAddressName:(NSString *)addressName
                        addressCode:(NSString *)addressCode{
    self = [super init];
    if(self){
        _addressCode = addressCode;
        _addressName = addressName;
    }
    return self;
}
+ (instancetype)creaateWithAddressName:(NSString *)addressName
                           addressCode:(NSString *)addressCode{
    return [[self alloc] initWithAddressName:addressName addressCode:addressCode];
}

@end

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/
