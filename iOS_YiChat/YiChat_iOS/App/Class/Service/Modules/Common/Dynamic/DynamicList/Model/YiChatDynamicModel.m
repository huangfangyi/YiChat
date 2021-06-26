//
//  YiChatDynamicModel.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/6.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatDynamicModel.h"

@interface YiChatDynamicModel ()

@end

@implementation YiChatDynamicModel

- (id)initWithDic:(NSDictionary *)dic{
    self = [super initWithDic:dic];
    if(self){
        [self setValuesForKeysWithDictionary:dic];
        
        NSArray *praise = dic[@"praiseList"];
        NSArray *coment = dic[@"commentList"];
        
        [self inittialPraiselistWithArray:praise];
        [self initialCommandListWithArray:coment];
        
        
    }
    return self;
}

- (void)inittialPraiselistWithArray:(NSArray *)praiseList{
    if(praiseList && [praiseList isKindOfClass:[NSArray class]]){
        if(praiseList.count == 0){
            _praiseList = @[];
        }
        else{
            NSMutableArray *praise = [NSMutableArray arrayWithCapacity:0];
            for (int i = 0; i < praiseList.count; i ++) {
                NSDictionary *dic = praiseList[i];
                if(dic && [dic isKindOfClass:[NSDictionary class]]){
                    YiChatDynamicPraiseEntityModel *model = [[YiChatDynamicPraiseEntityModel alloc] initWithDic:dic];
                    if(model && [model isKindOfClass:[YiChatDynamicPraiseEntityModel class]] ){
                        [praise addObject:model];
                    }
                }
            }
            if(praise.count > 0){
                _praiseList = praise;
            }
            else{
                _praiseList = @[];
            }
        }
    }
    else{
        _praiseList = @[];
    }
}

- (void)initialCommandListWithArray:(NSArray *)commandList{
    
    if(commandList && [commandList isKindOfClass:[NSArray class]]){
        if(commandList.count == 0){
            _commentList = @[];
        }
        else{
            NSMutableArray *command = [NSMutableArray arrayWithCapacity:0];
            for (int i = 0; i < commandList.count; i ++) {
                NSDictionary *dic = commandList[i];
                if(dic && [dic isKindOfClass:[NSDictionary class]]){
                    YiChatDynamicCommitEntityModel *model = [[YiChatDynamicCommitEntityModel alloc] initWithDic:dic];
                    if(model && [model isKindOfClass:[YiChatDynamicCommitEntityModel class]] ){
                        [command addObject:model];
                    }
                }
            }
            if(command.count > 0){
                _commentList = command;
            }
            else{
                _commentList = @[];
            }
        }
    }
    else{
        _commentList = @[];
    }
}

- (void)setValue:(id)value forUndefinedKey:(NSString *)key{
    
}

@end


@implementation YiChatDynamicCommitEntityModel

- (id)initWithDic:(NSDictionary *)dic{
    self = [super initWithDic:dic];
    if(self){
        [self setValuesForKeysWithDictionary:dic];
    }
    return self;
}

@end

@implementation YiChatDynamicPraiseEntityModel

- (id)initWithDic:(NSDictionary *)dic{
    self = [super initWithDic:dic];
    if(self){
        [self setValuesForKeysWithDictionary:dic];
    }
    return self;
}

@end
