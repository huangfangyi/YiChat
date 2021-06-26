//
//  YiChatPersonalInfoVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/28.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatPersonalInfoVC.h"
#import "ServiceGlobalDef.h"
#import "YiChatPersonalInfoCell.h"
#import "ProjectCommonCellModel.h"
#import "ProjectRequestHelper.h"
#import "YiChatUserManager.h"
#import "ProjectUIHelper.h"

@interface YiChatPersonalInfoVC ()

@property (nonatomic,strong) NSArray *toolCellData;

@end

#define YiChatPersonalInfoVC_Icon 80.0f
#define YiChatPersonalInfoVC_Common PROJECT_SIZE_COMMON_CELLH

@implementation YiChatPersonalInfoVC

+ (id)initialVC{
    YiChatPersonalInfoVC *info = [YiChatPersonalInfoVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"userInfo") leftItem:nil rightItem:nil];
    return info;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeTable];
    // Do any additional setup after loading the view.
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self loadUserData];
}

- (void)loadUserData{
    
    [[YiChatUserManager defaultManagaer] updateUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull model, NSString * _Nonnull error) {
        
        if(model && [model isKindOfClass:[YiChatUserModel class]]){
            [self updateDataWithModel:model];
        }
        else{
            if([error isKindOfClass:[NSString class]] && error){
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
            }
        }
    }];
}

- (void)updateDataWithModel:(YiChatUserModel *)modelUser{
    if(modelUser && [modelUser isKindOfClass:[YiChatUserModel class]]){
        
        for (int i = 0; i < self.sectionsRowsNumSet.count; i ++) {
            NSNumber *num = self.sectionsRowsNumSet[i];
            for (int j = 0; j <num.integerValue ; j ++) {
                ProjectCommonCellModel *model = [self getModelWithIndex:[NSIndexPath indexPathForRow:j inSection:i]];
                if(i == 0 && j == 0){
                    model.contentUrl = [modelUser userIcon];
                }
                else if(i == 0 && j == 1){
                    model.contentStr = [modelUser userPhone];
                }
                else if(i == 0 && j == 2){
                    model.contentStr = [modelUser appearName];
                }
                else if(i == 0 && j == 3){
                    if([YiChatUserInfo_AppId isKindOfClass:[NSString class]])
                    {
                        model.contentStr = YiChatUserInfo_AppId;
                    }
                }
                else if(i == 1 && j == 0){
                    model.contentStr = [modelUser userGendar];
                }
            }
        }
        
        [ProjectHelper helper_getMainThread:^{
            [self.cTable reloadData];
        }];
    }
}


- (void)loadSystemData{
    NSMutableArray *tool = [NSMutableArray arrayWithCapacity:0];
    NSMutableArray *num = [NSMutableArray arrayWithCapacity:0];
    NSArray *textArr = @[@[@"头像",@"电话",@"昵称",[NSString stringWithFormat:@"%@%@",PROJECT_TEXT_APPNAME,@"号"],@"我的二维码"],@[@"性别"]];
    for (int i = 0; i < textArr.count; i ++) {
        
        if([textArr[i] isKindOfClass:[NSArray class]]){
            NSArray *value = textArr[i];
            if([value isKindOfClass:[NSArray class]]){
                NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
                for (int j = 0; j < value.count; j ++) {
                    ProjectCommonCellModel *model = [[ProjectCommonCellModel alloc] init];
                    model.titleStr = value[j];
                    
                    if(model){
                        [arr addObject:model];
                    }
                }
                if(arr.count != 0){
                    [tool addObject:arr];
                    [num addObject:[NSNumber numberWithInteger:arr.count]];
                }
            }
        }
    }
    _toolCellData = tool;
    self.sectionsRowsNumSet = [num copy];
}

- (void)makeTable{
    dispatch_group_t group = dispatch_group_create();
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_group_async(group, queue, ^{
        [self loadSystemData];
    });
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        [self.view addSubview:self.cTable];
        self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, self.cTable.frame.origin.y, self.cTable.frame.size.width, self.view.frame.size.height - self.cTable.frame.origin.y);
    });
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    if(index.section == 0 && index.row == 0){
        return YiChatPersonalInfoVC_Icon;
    }
    else{
        return YiChatPersonalInfoVC_Common;
    }
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    return 10.0f;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.cTable.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
    back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    return back;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatPersonalInfoCell *cell =  nil;
    
    CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
    
    ProjectCommonCellModel *dataModel = [self getModelWithIndex:indexPath];
    
    if([dataModel.titleStr isEqualToString:@"头像"] || [dataModel.titleStr isEqualToString:@"我的二维码"]){
        
        static NSString *str = @"YiChatPersonalCell_Icon";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatPersonalInfoCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:0];
        }
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:YES] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:cellH]];
        
        [cell updateType:0];
    }
    else{
        static NSString *str = @"YiChatPersonalCell_Info";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatPersonalInfoCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:1];
        }
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:YES] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:cellH]];
        
        [cell updateType:1];
    }
    cell.cellModel = dataModel;
    
    return cell;
}

- (ProjectCommonCellModel *)getModelWithIndex:(NSIndexPath *)indexPath{
    ProjectCommonCellModel *dataModel = nil;
    if(indexPath.section <= (_toolCellData.count - 1)){
        NSArray *tmp = _toolCellData[indexPath.section];
        if([tmp isKindOfClass:[NSArray class]]){
            if((tmp.count - 1) >= indexPath.row){
                ProjectCommonCellModel *model = tmp[indexPath.row];
                if(model){
                    dataModel = model;
                }
            }
        }
    }
    return dataModel;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    ProjectCommonCellModel *cellModel = [self getModelWithIndex:indexPath];
    if([cellModel isKindOfClass:[ProjectCommonCellModel class]]){
        if([cellModel.titleStr isEqualToString:@"头像"]){
            [ProjectUIHelper projectActionSheetWithListArr:@[@"相机",@"相册"] click:^(NSInteger row) {
                if(row == 0){
                    
                    [ProjectUIHelper projectPhotoVideoPickerWWithType:5 invocation:^(YRPickerManager * _Nonnull manager, UINavigationController * _Nonnull nav) {
                        manager.yrPickerManagerDidTakeImages = ^(UIImage * _Nonnull originIcon, UIImage * _Nonnull editedIcon, BOOL isCancle) {
                            if(editedIcon && [editedIcon isKindOfClass:[UIImage class]]){
                                [self uploadImage:editedIcon];
                            }
                        };
                        [self presentViewController:nav animated:YES completion:nil];
                    }];
                    
                }
                else if(row == 1){
                    
                    [ProjectUIHelper projectPhotoVideoPickerWWithType:6 pickNum:1 invocation:^(YRPickerManager * _Nonnull manager, UINavigationController * _Nonnull nav) {
                        
                        manager.yrPickerManagerDidPickerImages = ^(NSArray<UIImage *> * _Nonnull images, NSArray * _Nonnull assets, BOOL isSelectOriginalPhoto) {
                            if(images && [images isKindOfClass:[NSArray class]]){
                                if(images.count == 1){
                                     [self uploadImage:images.firstObject];
                                }
                            }
                        };
                         [self presentViewController:nav animated:YES completion:nil];
                    }];
                    
                }
            }];
        }
        else if([cellModel.titleStr isEqualToString:@"电话"]){
             [self pushVCWithName:@"YiChatChangePhoneNumVC"];
        }
        else if([cellModel.titleStr isEqualToString:@"昵称"]){
            [self pushVCWithName:@"YiChatChangeNickNameVC"];
        }
        else if([cellModel.titleStr isEqualToString:[NSString stringWithFormat:@"%@%@",PROJECT_TEXT_APPNAME,@"号"]]){
            [self pushVCWithName:@"YiChatChangeUserIdVC"];
        }
        else if([cellModel.titleStr isEqualToString:@"我的二维码"]){
            [self pushVCWithName:@"YiChatMyQRCodeVC"];
            
        }
        else if([cellModel.titleStr isEqualToString:@"性别"]){
            [self pushVCWithName:@"YiChatChangeSexVC"];
        }
    }
    
}

- (void)uploadImage:(UIImage *)image{
    if(image && [image isKindOfClass:[UIImage class]]){
        
        id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
        
        [ProjectRequestHelper uploadImageWithImage:image userId:YiChatUserInfo_UserIdStr token:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progressBlock:^(CGFloat progress) {
            
        } andMessageId:nil andSendResult:^(BOOL isSuccess, NSString * _Nonnull remotePath) {
            
            [ProjectHelper helper_getMainThread:^{
                if([progress respondsToSelector:@selector(hidden)]){
                    [progress performSelector:@selector(hidden)];
                }
            }];
            
            if(isSuccess){
                [self loadUserData];
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"上传头像失败"];
            }
        }];
    }
   
}

- (void)pushVCWithName:(NSString *)name{
    if([name isKindOfClass:[NSString class]]){
        if(name){
            UIViewController *vc = [ProjectHelper helper_getVCWithName:name initialMethod:@selector(initialVC)];
            if(vc){
                [self.navigationController pushViewController:vc animated:YES];
            }
        }
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
