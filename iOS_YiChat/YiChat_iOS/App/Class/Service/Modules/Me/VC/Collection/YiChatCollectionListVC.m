//
//  YiChatCollectionListVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatCollectionListVC.h"
#import "YiChatCollectionCell.h"
#import "YiChatCollectionEntity.h"
#import "ServiceGlobalDef.h"
@interface YiChatCollectionListVC ()

@property (nonatomic, strong) NSMutableArray *dataArray;

@end

@implementation YiChatCollectionListVC

+ (id)initialVC{
    YiChatCollectionListVC *collctionlist = [YiChatCollectionListVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"collectionList") leftItem:nil rightItem:nil];
    return collctionlist;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeTable];
    
    
    self.dataArray = [NSMutableArray arrayWithCapacity:0];
    // Do any additional setup after loading the view.
}

- (void)loadData{
    
}


- (void)makeTable{
    
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, self.cTable.frame.origin.y, self.cTable.frame.size.width, self.view.frame.size.height - self.cTable.frame.origin.y - PROJECT_SIZE_TABH);
    [self loadData];
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    if((_dataArray.count - 1) >= index.row){
        YiChatCollectionEntity *entity = _dataArray[index.row];
        return entity.sourceSize.height + 10.0 + 30.0;
    }
    return 0.01f;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatCollectionCell *cell = nil;
    YiChatCollectionEntity *entity = nil;
    if((_dataArray.count - 1) >= indexPath.row){
       entity  = _dataArray[indexPath.row];
    }
    NSInteger type = entity.type;
    if(type == 0){
        static NSString *reuse = @"collection_text";
        cell = [tableView dequeueReusableCellWithIdentifier:reuse];
        
        if(!cell){
            cell = [[YiChatCollectionCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuse type:type];
        }
    }
    else if(type == 1){
        static NSString *reuse = @"collection_image";
        cell = [tableView dequeueReusableCellWithIdentifier:reuse];
        
        if(!cell){
            cell = [[YiChatCollectionCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuse type:type];
        }
    }
    else if(type == 2){
        static NSString *reuse = @"collection_voice";
        cell = [tableView dequeueReusableCellWithIdentifier:reuse];
        
        if(!cell){
            cell = [[YiChatCollectionCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuse type:type];
        }
        
        cell.voiceResourceStartPlay = ^(NSString *url,NSIndexPath *indexTmp){
            
        };
        cell.voiceResourceStopPlay = ^(NSString *url,NSIndexPath *indexTmp) {
            
        };
        
    }
    cell.index = indexPath;
    [cell dic:self.dataArray[indexPath.row] cellSize:CGSizeMake(self.cTable.frame.size.width, entity.sourceSize.height + 10.0 + 30.0) sourceSize:entity.sourceSize];
    
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
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
