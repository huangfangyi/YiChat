//
//  ProjectSearchBarView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/24.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectSearchBarView.h"
#import "ServiceGlobalDef.h"
#import "ProjectSearchView.h"
#import "ProjectSearchUntilies.h"
@interface ProjectSearchBarView ()

@property (nonatomic) BOOL dIsSearching;

@property (nonatomic,strong) ProjectSearchView *searchView;
@property (nonatomic) ProjectSearchBarStyle searchStyle;
@property (nonatomic) NSInteger type;

@property (nonatomic,strong) UIImageView *icon;

@property (nonatomic,strong) UILabel *labText;

@property (nonatomic,strong) UITextField *searchText;

@property (nonatomic,strong) UIView *back;

@property (nonatomic,strong) UIButton *clearBtn;
    
@property (nonatomic,copy) HelperReturnInvocation getDataInvocation;
@end

@implementation ProjectSearchBarView

- (void)createUI{
    
    self.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    
    _dIsSearching=NO;
    
    [self makeUI];
    [self addTap];
}

- (void)initialSearchType:(NSInteger)type{
    _type = type;
}

- (void)initialSearchStyle:(NSInteger)style{
    _searchStyle = style;
}

- (void)makeUI{
    UIView *back = [[UIView alloc] initWithFrame:CGRectZero];
    [self addSubview:back];
    back.backgroundColor = [UIColor whiteColor];
    back.layer.cornerRadius = 6.0;
    _back = back;
    
    UIImageView *searchImg=[ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectZero andImg:nil];
    [back addSubview:searchImg];
    _icon = searchImg;
    
    if(_type == 0){
        UITextField *text = [ProjectHelper helper_factoryMakeTextFieldWithFrame:CGRectZero withPlaceholder:_placeHolder fontSize:PROJECT_TEXT_FONT_COMMON(14.0) isClearButtonMode:UITextFieldViewModeWhileEditing andKeybordType:UIKeyboardTypeDefault textColor:PROJECT_COLOR_TEXTCOLOR_BLACK];
        [back addSubview:text];
        _searchText = text;
    }
    else{
        UILabel *description=[ProjectHelper helper_factoryMakeLabelWithFrame:CGRectZero andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentLeft];
        [back addSubview:description];
        _labText = description;
        description.text=_placeHolder;
        
        UIButton *clearBtn=[ProjectHelper helper_factoryMakeClearButtonWithFrame:CGRectMake(0, 0, back.frame.size.width, back.frame.size.height) target:self method:@selector(clearBtnMethod:)];
        [back addSubview:clearBtn];
        _clearBtn = clearBtn;
    }
    
    [self refreshUI];
}

- (void)refreshUI{
    UIImage *searchIcon=[UIImage imageNamed:@"search@3x.png"];
    
    if(searchIcon != nil && [searchIcon isKindOfClass:[UIImage class]]){
        CGFloat x = (10.0 / 345.0) * self.frame.size.width;
        
        CGFloat searchIconH = 16.0 / 36.0 * self.frame.size.height;
        
        if(searchIconH <= 15.0){
            searchIconH = 15.0;
        }
        else if(searchIconH >= 18.0){
            searchIconH = 18.0;
        }
        
        CGFloat searchIconW = [ProjectHelper helper_GetWidthOrHeightIntoScale:searchIcon.size.width / searchIcon.size.height width:0 height:searchIconH];
        
        CGFloat blank = (self.frame.size.height - searchIconH) / 2;
        
        CGFloat y = 0;
        if(blank >= 10.0){
            y = 10.0;
        }
        else{
            y = blank;
        }
        _back.frame = CGRectMake(x, y, self.frame.size.width - x * 2, self.frame.size.height - y * 2);
        
        _icon.frame = CGRectMake(3.0, _back.frame.size.height / 2 - searchIconH / 2, searchIconW, searchIconH);
        _icon.image = searchIcon;
        
        x = _icon.frame.origin.x + _icon.frame.size.width + 10.0;
        CGFloat w = _back.frame.size.width - x - 10.0;
        CGFloat h = 20.0;
        y = _icon.frame.origin.y + _icon.frame.size.height / 2 - h / 2;
        
        if(_type == 0){
            _searchText.frame = CGRectMake(x, y, self.frame.size.width - x - 15.0, h);
        }
        else{
            _labText.frame = CGRectMake(x, y, w, h);
            _clearBtn.frame = _back.bounds;
        }
    }
}

- (void)clearBtnMethod:(UIButton *)btn{
    _dIsSearching = !_dIsSearching;
    
    WS(weakSelf);
    if(_dIsSearching == YES){
        
        ProjectSearchView * list = [[ProjectSearchView alloc] initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH,PROJECT_SIZE_HEIGHT) style:_searchStyle];
        list.cellClick = ^(NSInteger searchStyle, id  _Nonnull obj) {
            
            [ProjectHelper helper_getMainThread:^{
                [weakSelf.searchView animate_end:^{
                    [weakSelf.searchView removeFromSuperview];
                    weakSelf.searchView = nil;
                    weakSelf.dIsSearching = NO;
                }];
            }];
            if (searchStyle == ProjectSearchBarStyleSearchMessage) {
                NSDictionary *dic = @{
                                      @"searchStyle": @"ProjectSearchBarStyleSearchMessage",
                                      @"msgArr": obj
                                      };
            
                weakSelf.projectSearchBarSearchResult(dic);
            }
            if( searchStyle == ProjectSearchBarStyleSearchConnection){
                weakSelf.projectSearchBarSearchResult(obj);
            }
            if( searchStyle == ProjectSearchBarStyleSearchPersonCard){
                weakSelf.projectSearchBarSearchResult(obj);
            }
            
        };
        
        AppDelegate *app = [ProjectHelper helper_getAppDelegate];
        [app.window addSubview:list];
        [list animate_begin];
        
        [list getSearchOriginData:self.getDataInvocation];
        
        list.projectSearchBarSearchResult = ^(id  _Nonnull obj) {
            if(obj){
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf.searchView animate_end:^{
                        [weakSelf.searchView removeFromSuperview];
                        weakSelf.searchView = nil;
                        weakSelf.dIsSearching = NO;
                    }];
                }];
               
                if(weakSelf.projectSearchBarSearchResult){
                    weakSelf.projectSearchBarSearchResult(obj);
                }
            }
        };
        list.projectSearchBarInputResult = ^(id  _Nonnull obj) {
            if(obj){
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf.searchView animate_end:^{
                        [weakSelf.searchView removeFromSuperview];
                        weakSelf.searchView = nil;
                        weakSelf.dIsSearching = NO;
                    }];
                }];
                
                if(weakSelf.projectSearchBarInputResult){
                    weakSelf.projectSearchBarInputResult(obj);
                }
            }
        };
        
        
        list.cancelClick = ^{
            [weakSelf.searchView animate_end:^{
                [weakSelf.searchView removeFromSuperview];
                weakSelf.searchView = nil;
                weakSelf.dIsSearching = NO;
            }];
        };
        
        self.searchView = list;
        
    }
    else{
        
        [self.searchView animate_end:^{
            [weakSelf.searchView removeFromSuperview];
            weakSelf.searchView = nil;
            weakSelf.dIsSearching = NO;
        }];
        
    }
}

- (void)addTap{
    UITapGestureRecognizer * tapGestureRecognizer=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapGesture:)];
    
    [self addGestureRecognizer:tapGestureRecognizer];
}

-(void)tapGesture:(UITapGestureRecognizer *)tap{
    [self endEditing:YES];
}

- (void)resignKeyBoard{
    if(_searchText && [_searchText isKindOfClass:[UITextField class]]){
        [_searchText resignFirstResponder];
    }
}
    
- (void)getSearchOriginData:(id(^)(void))getDataInvocation{
    self.getDataInvocation = getDataInvocation;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
