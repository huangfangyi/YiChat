//
//  YiChatInvitePhoneConnectionVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatInvitePhoneConnectionVC.h"
#import "YiChatPhoneConnectionModel.h"
#import "ServiceGlobalDef.h"
#import "ProjectSearchBarView.h"
#import "YiChatPhoneConnectionSelecteCell.h"
#import "YiChatPhoneConnectionIndexView.h"
#import "YiChatConnectionInviteBar.h"
#import <MessageUI/MessageUI.h>

@interface YiChatInvitePhoneConnectionVC ()<UITableViewDelegate,UITableViewDataSource,MFMessageComposeViewControllerDelegate>

@property (nonatomic,strong) ProjectSearchBarView *searchBar;

@property (nonatomic,strong) YiChatPhoneConnectionModel *requestModel;

@property (nonatomic,strong) NSArray *connectPersonEntityArr;

@property (nonatomic,strong) YiChatPhoneConnectionIndexView *indexView;

@property (nonatomic,strong) YiChatConnectionInviteBar *inviteBar;

@property (nonatomic,strong) NSMutableArray *selecteArr;

@end


#define YiChatInvitePhoneConnectionVC_CellH 60.0f
@implementation YiChatInvitePhoneConnectionVC

+ (id)initialVC{
    
    return [self initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"invitePhoneContact") leftItem:nil rightItem:nil];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _selecteArr = [NSMutableArray arrayWithCapacity:0];
    
    [self loadConnectionData:YES];
    // Do any additional setup after loading the view.
}


- (void)loadConnectionData:(BOOL)isNeedFetch{
    [ProjectHelper helper_getGlobalThread:^{
        WS(weakSelf);
        [self.requestModel fetchPhoneConnectionDataSuccess:^(NSArray * _Nonnull connectEntityArr) {
            
            
            [[weakSelf.requestModel class] matchConnectionEntitys:connectEntityArr withCharactersUp:^(NSArray * _Nonnull connectEntityDicArr) {
                
                weakSelf.connectPersonEntityArr = connectEntityDicArr;
                
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf makeTable];
                    [weakSelf.view addSubview:weakSelf.indexView];
                }];
            }];
            
            
        } fail:^(NSString * _Nonnull errorMsg, NSInteger code) {
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:errorMsg];
        } isNeedFectch:isNeedFetch];
        
    }];
   
}

- (YiChatPhoneConnectionModel *)requestModel{
    if(!_requestModel){
        _requestModel = [[YiChatPhoneConnectionModel alloc] init];
    }
    return _requestModel;
}

- (ProjectSearchBarView *)searchBar{
    if(!_searchBar){
        _searchBar = [[ProjectSearchBarView alloc] initWithFrame:CGRectMake(0, 0,self.view.frame.size.width, ProjectUIHelper_SearchBarH)];
        _searchBar.placeHolder =  PROJECT_TEXT_LOCALIZE_NAME(@"phoneContactSearchPlaceHolderInvite");
        [_searchBar initialSearchType:1];
        [_searchBar createUI];
    }
    return _searchBar;
}

- (void)makeTable{
    
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < self.connectPersonEntityArr.count; i ++) {
        NSDictionary *dic = self.connectPersonEntityArr[i];
        
        if([dic isKindOfClass:[NSDictionary class]]){
            NSArray *arr = dic[dic.allKeys.lastObject];
            [tmp addObject:[NSNumber numberWithInteger:arr.count]];
        }
    }
    [tmp insertObject:[NSNumber numberWithInteger:0] atIndex:0];
    self.tableStyle = 0;
    self.sectionsRowsNumSet = [tmp copy];
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, self.cTable.frame.origin.y, self.cTable.frame.size.width, self.view.frame.size.height - self.cTable.frame.origin.y);
    [self.view addSubview:self.inviteBar];
    
}

- (YiChatPhoneConnectionIndexView *)indexView{
    if(!_indexView){
        
        NSArray *characters = [self getCharacterArrs];
        
        WS(weakSelf);
        
        _indexView = [[YiChatPhoneConnectionIndexView alloc] initWithData:characters bgView:self.view];
        _indexView.yichatIndexViewClick = ^(NSInteger clickIndex) {
            if(weakSelf.sectionsRowsNumSet.count - 1 >= (clickIndex + 1)){
                 [weakSelf.cTable scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:clickIndex + 1] atScrollPosition:UITableViewScrollPositionTop animated:YES];
            }
        };
    }
    return _indexView;
}

- (void)updateIndexView{
    NSArray *characters = [self getCharacterArrs];
    [self.indexView updateUIWithData:characters];
}

- (NSArray *)getCharacterArrs{
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < _connectPersonEntityArr.count; i ++) {
        NSDictionary *dic = _connectPersonEntityArr[i];
        if([dic isKindOfClass:[NSDictionary class]]){
            id key = dic.allKeys.lastObject;
            if(key){
                [arr addObject:key];
            }
        }
    }
    return arr;
}

- (YiChatConnectionInviteBar *)inviteBar{
    if(!_inviteBar){
        WS(weakSelf);
        
        _inviteBar = [[YiChatConnectionInviteBar alloc] initWithFrame:CGRectMake(0, self.view.frame.size.height - 50.0, self.view.frame.size.width, 50.0)];
        _inviteBar.hidden = YES;
        _inviteBar.yiChatInvitebarClick = ^(NSArray * _Nonnull dataArr) {
            if ([MFMessageComposeViewController canSendText]) {
                
                if([dataArr isKindOfClass:[NSArray class]]){
                    
                    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
                    for (int i = 0; i < dataArr.count; i ++) {
                        YiChatContactEntity *entity = dataArr[i];
                        if([entity isKindOfClass:[YiChatContactEntity class]]){
                            if(entity.phoneNum){
                                [tmp addObject:entity.phoneNum];
                            }
                        }
                    }
                    YiChatUserManager *manager = [YiChatUserManager defaultManagaer];
                    NSString *content = [NSString stringWithFormat:@"%@%@",manager.sharedContent,manager.sharedLink];
                    
                    //  判断一下是否支持发送短信，比如模拟器
                    MFMessageComposeViewController *messageVC = [[MFMessageComposeViewController alloc] init];
                    messageVC.recipients = tmp; //需要发送的手机号数组
                    messageVC.body = content;
                    messageVC.messageComposeDelegate = weakSelf; //指定代理
                    [weakSelf presentViewController:messageVC animated:YES completion:nil];
                }
                else{
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"数据出错"];
                }

                
            } else {
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"设备不支持短信功能"];
            }
        };
    }
    return _inviteBar;
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    if(index.section != 0){
        return YiChatInvitePhoneConnectionVC_CellH;
    }
    return 0.001f;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    if(section == 0){
        return ProjectUIHelper_SearchBarH;
    }
    else{
        return 30.0;
    }
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    if(section == 0){
        return self.searchBar;
    }
    else{
        UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
        back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
        
        UILabel *lab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, 0, back.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, back.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(16.0) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
        
        if((_connectPersonEntityArr.count - 1) >= (section - 1)){
            NSDictionary *dic = _connectPersonEntityArr[section - 1];
            if([dic isKindOfClass:[NSDictionary class]]){
                lab.text = dic.allKeys.lastObject;
            }
        }
        [back addSubview:lab];
        
        return back;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    YiChatPhoneConnectionSelecteCell *cell =  nil;
    CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
    
    if(indexPath.section > 0){
        static NSString *str = @"YiChatConnection_Selecte_Contact";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatPhoneConnectionSelecteCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:0];
        }
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:cellH]];
        
        [cell updateType:0];
        
        if((_connectPersonEntityArr.count - 1) >= (indexPath.section - 1)){
            NSDictionary *dic = _connectPersonEntityArr[indexPath.section - 1];
            if([dic isKindOfClass:[NSDictionary class]]){
                id key = dic.allKeys.lastObject;
                NSArray *arr = dic[key];
                if([arr isKindOfClass:[NSArray class]]){
                    if((arr.count - 1) >= indexPath.row){
                        cell.contactEntity = arr[indexPath.row];
                    }
                }
               
            }
        }
    }
    else{
        static NSString *str = @"YiChatConnection_Selecte_Contact";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatPhoneConnectionSelecteCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:NO] type:1];
        }
        
    }
   
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    if(indexPath.section != 0 && (_connectPersonEntityArr.count - 1) >= (indexPath.section - 1)){
        
        NSDictionary *dic = _connectPersonEntityArr[indexPath.section - 1];
        if([dic isKindOfClass:[NSDictionary class]]){
            id key  = dic.allKeys.lastObject;
            if(key){
                NSArray *arr  = dic[key];
                if([arr isKindOfClass:[NSArray class]]){
                    if((arr.count - 1) >= indexPath.row){
                        YiChatContactEntity *entity = arr[indexPath.row];
                        entity.isSelecte = !entity.isSelecte;
                        if(entity.isSelecte){
                            BOOL isHas = NO;
                            for (int i = 0; i < _selecteArr.count; i ++) {
                                YiChatContactEntity *tmp = _selecteArr[i];
                                if([tmp isKindOfClass:[YiChatContactEntity class]]){
                                    if([tmp.phoneNum isEqualToString:entity.phoneNum]){
                                        isHas = YES;
                                        break;
                                    }
                                }
                            }
                            
                            if(isHas == NO && entity){
                                [_selecteArr addObject:entity];
                            }
                        }
                        else{
                            for (int i = 0; i < _selecteArr.count; i ++) {
                                YiChatContactEntity *tmp = _selecteArr[i];
                                if([tmp isKindOfClass:[YiChatContactEntity class]]){
                                    if([tmp.phoneNum isEqualToString:entity.phoneNum]){
                                        [_selecteArr removeObjectAtIndex:i];
                                        if(i != 0){
                                            i --;
                                        }
                                        continue;
                                    }
                                }
                            }
                        }
                        if(_selecteArr.count > 0){
                            [self.inviteBar updateUIWithDataSource:_selecteArr];
                            if(self.inviteBar.hidden){
                                self.inviteBar.hidden = NO;
                            }
                            self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, self.cTable.frame.origin.y, self.cTable.frame.size.width, self.view.frame.size.height - self.cTable.frame.origin.y - self.inviteBar.frame.size.height);
                        }
                        else{
                            self.inviteBar.hidden = YES;
                              self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, self.cTable.frame.origin.y, self.cTable.frame.size.width, self.view.frame.size.height - self.cTable.frame.origin.y);
                        }
                        [UIView performWithoutAnimation:^{
                             [self.cTable reloadRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:indexPath.row inSection:indexPath.section]] withRowAnimation:UITableViewRowAnimationNone];
                        }];
                    }
                }
            }
        }
        
    }
}

- (void)messageComposeViewController:(MFMessageComposeViewController *)controller didFinishWithResult:(MessageComposeResult)result {
    if (result == MessageComposeResultCancelled) {
        [controller dismissViewControllerAnimated:YES completion:nil];
    } else if (result == MessageComposeResultFailed) {
        [controller dismissViewControllerAnimated:YES completion:^{
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"邀请发送失败，请稍后重试"];
        }];
    } else {
        [controller dismissViewControllerAnimated:YES completion:^{
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"邀请发送成功"];
        }];
    }
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
