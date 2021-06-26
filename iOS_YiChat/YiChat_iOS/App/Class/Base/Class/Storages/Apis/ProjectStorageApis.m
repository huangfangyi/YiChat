//
//  ProjectStorageApis.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/29.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectStorageApis.h"

@implementation ProjectStorageApis

/**
 *  创建文件夹
 */
+ (BOOL)projectStorageApis_CreateItemWithPath:(NSString *)path{
    NSFileManager *fileManager=[NSFileManager defaultManager];
    NSError *error=nil;
    if(![fileManager fileExistsAtPath:path])
    {
        BOOL listCreate= [fileManager createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:&error];
        if(listCreate){
            NSLog(@"文件夹创建成功");
            NSLog(@"%@",path);
            return YES;
        }
        else{
            NSLog(@"%@",error.description);
            NSLog(@"%@",path);
            return NO;
        }
    }
    NSLog(@"%@",path);
    return YES;
}

/**
 *  判断文件是否存在
 */
+ (BOOL)projectStorageApis_JudgeFileIsExistsInPath:(NSString *)path{
    return  [[NSFileManager defaultManager] fileExistsAtPath:path];
}

/**
 *  判断文件夹是否存在
 */
+ (BOOL)projectStorageApis_JudgeItemIsExistsInPath:(NSString *)path{
    BOOL isExist;
    BOOL directory = [[NSFileManager defaultManager] fileExistsAtPath:path isDirectory:&isExist];
    if(isExist == YES){
        //文件夹
        return directory;
    }
    else{
        return NO;
    }
}

+ (BOOL)projectStorageApis_removeItemAtPath:(NSString *)path{
    NSFileManager *fileManage = [NSFileManager defaultManager];
    if ([fileManage fileExistsAtPath:path]) {
        
        // 删除
        
        BOOL isSuccess = [fileManage removeItemAtPath:path error:nil];
        
        return isSuccess;
        
    }else{
        
        return YES;
    }
}

/**
 *  获取目录下所有文件
 */
+ (NSArray *)XYStorageApi_GetAllFilesNameIntoItem:(NSString *)path{
    return  [[NSFileManager defaultManager] contentsOfDirectoryAtPath:path error:nil];
}

+ (NSString *)projectStorageApis_getHomeDirectoryPath{
    return NSHomeDirectory();
}

+ (NSString *)projectStorageApis_getTempDirectoryPath{
    return NSTemporaryDirectory();
}

+ (NSString *)projectStorageApis_getDocumentDirecoryPath{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask,YES);
    
    NSString *path = [paths objectAtIndex:0];
    
    return path;
}

+ (NSString *)projectStorageApis_getPeferenceDirecoryPath{
    NSArray * paths4 = NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES);
    NSString * preferencePath = [[paths4 lastObject] stringByAppendingPathComponent:@"Preferences"];
    return preferencePath;
}

+ (NSString *)projectStorageApis_getLibraryDirectoryPath{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES);
    
    NSString *path = [paths objectAtIndex:0];
    
    return path;
}

+ (NSString *)projectStorageApis_getCacheDirectoryPath{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory,NSUserDomainMask,YES);
    
    NSString *path = [paths objectAtIndex:0];
    return path;
}

@end
