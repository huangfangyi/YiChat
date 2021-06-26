//
//  ProjectRequestHelper.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/22.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ProjectRequestParameterModel.h"
#import <UIKit/UIKit.h>
#import "ProjectRequestModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef void(^RequestHelperRequestSuccessHandle)(NSData * data , NSHTTPURLResponse * response);

typedef void(^RequestHelperRequestGetRequestIdentify)(NSString * identify);

typedef void(^RequestHelperRequestFailHandle)(NSString *error,NSString *identify);

typedef id(^RequestHelperRequestGetProgress)(NSString *title);

typedef id(^RequestHelperRequestGetIdentifier)(NSString *title);

#define Request_Progress(a) progress(a)

#define Request_ProgressNoTitle progress(@"")

#define Request_Identifier(a) [ProjectRequestHelper requestHelper_productRequestNameWithIdentifier:a]

@interface ProjectRequestHelper : NSObject

+ (NSString *)requestHelper_productRequestNameWithIdentifier:(NSString *)identifier;

+ (void)requestHelper_feltRequestData:(NSData *)data response:(NSHTTPURLResponse *)response handle:(void(^)(id obj,BOOL isNeedLogin))handle;

+ (void)requestHelper_feltRequestDataWithCode:(NSData *)data response:(NSHTTPURLResponse *)response handle:(void(^)(id obj,BOOL isNeedLogin,NSInteger code))handle;

+ (void)progressShow:(id)progress;

+ (void)progressHidden:(id)progress;

+ (void)advertisementMainWithParameters:(NSDictionary *)parameters
headerParameters:(NSDictionary *)headerParameters
        progress:(id)progress
        isScrete:(BOOL)screteState
          isAsyn:(BOOL)isAsyn
       identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
   successHandle:(RequestHelperRequestSuccessHandle)sucess
            fail:(RequestHelperRequestFailHandle)fail;

+ (void)getSystemConfigWithParameters:(NSDictionary *)parameters
                     headerParameters:(NSDictionary *)headerParameters
                             progress:(id)progress
                             isScrete:(BOOL)screteState
                               isAsyn:(BOOL)isAsyn
                            identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                        successHandle:(RequestHelperRequestSuccessHandle)sucess
                                 fail:(RequestHelperRequestFailHandle)fail;

+ (void)sendCertifyWithPhoneNum:(NSString *)phone
             headerParameters:(NSDictionary *)headerParameters
                     progress:(id)progress
                     isScrete:(BOOL)screteState
                       isAsyn:(BOOL)isAsyn
                    identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                successHandle:(RequestHelperRequestSuccessHandle)sucess
                         fail:(RequestHelperRequestFailHandle)fail;

+ (void)registeWithParameters:(NSDictionary *)parameters
            headerParameters:(NSDictionary *)headerParameters
                    progress:(id)progress
                    isScrete:(BOOL)screteState
                      isAsyn:(BOOL)isAsyn
                   identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
               successHandle:(RequestHelperRequestSuccessHandle)sucess
                        fail:(RequestHelperRequestFailHandle)fail;

+ (void)loginWithParameters:(NSDictionary *)parameters
             headerParameters:(NSDictionary *)headerParameters
                     progress:(id)progress
                     isScrete:(BOOL)screteState
                       isAsyn:(BOOL)isAsyn
                    identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                successHandle:(RequestHelperRequestSuccessHandle)sucess
                         fail:(RequestHelperRequestFailHandle)fail;
    
+ (void)webLoginWithInterface:(NSString *)interface
             headerParameters:(NSDictionary *)headerParameters
                     progress:(id)progress
                     isScrete:(BOOL)screteState
                       isAsyn:(BOOL)isAsyn
                    identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                successHandle:(RequestHelperRequestSuccessHandle)sucess
                         fail:(RequestHelperRequestFailHandle)fail;

+ (void)loginoutWithParameters:(NSDictionary *)parameters
           headerParameters:(NSDictionary *)headerParameters
                   progress:(id)progress
                   isScrete:(BOOL)screteState
                     isAsyn:(BOOL)isAsyn
                  identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
              successHandle:(RequestHelperRequestSuccessHandle)sucess
                       fail:(RequestHelperRequestFailHandle)fail;

+ (void)getUserInfoWithParameters:(NSDictionary *)parameters
           headerParameters:(NSDictionary *)headerParameters
                   progress:(id)progress
                   isScrete:(BOOL)screteState
                     isAsyn:(BOOL)isAsyn
                  identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
              successHandle:(RequestHelperRequestSuccessHandle)sucess
                       fail:(RequestHelperRequestFailHandle)fail;

+ (void)getUpdateInfoWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail;

+ (void)searchUserWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail;

+ (void)resetPasswordWithParameters:(NSDictionary *)parameters
                headerParameters:(NSDictionary *)headerParameters
                        progress:(id)progress
                        isScrete:(BOOL)screteState
                          isAsyn:(BOOL)isAsyn
                       identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                   successHandle:(RequestHelperRequestSuccessHandle)sucess
                            fail:(RequestHelperRequestFailHandle)fail;

+ (void)getFriendListWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail;

+ (void)addFriendWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail;

+ (void)fetchFriendApplyNumWithParameters:(NSDictionary *)parameters
               headerParameters:(NSDictionary *)headerParameters
                       progress:(id)progress
                       isScrete:(BOOL)screteState
                         isAsyn:(BOOL)isAsyn
                      identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                  successHandle:(RequestHelperRequestSuccessHandle)sucess
                           fail:(RequestHelperRequestFailHandle)fail;

/**
 *  审核好友申请
 */
+ (void)checkFriendApplyWithParameters:(NSDictionary *)parameters
               headerParameters:(NSDictionary *)headerParameters
                       progress:(id)progress
                       isScrete:(BOOL)screteState
                         isAsyn:(BOOL)isAsyn
                      identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                  successHandle:(RequestHelperRequestSuccessHandle)sucess
                           fail:(RequestHelperRequestFailHandle)fail;
/**
 *  待审核申请列表
 */
+ (void)checkFriendApplyDataListWithParameters:(NSDictionary *)parameters
                      headerParameters:(NSDictionary *)headerParameters
                              progress:(id)progress
                              isScrete:(BOOL)screteState
                                isAsyn:(BOOL)isAsyn
                             identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                         successHandle:(RequestHelperRequestSuccessHandle)sucess
                                  fail:(RequestHelperRequestFailHandle)fail;

+ (void)deleteFriendWithParameters:(NSDictionary *)parameters
                              headerParameters:(NSDictionary *)headerParameters
                                      progress:(id)progress
                                      isScrete:(BOOL)screteState
                                        isAsyn:(BOOL)isAsyn
                                     identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                                 successHandle:(RequestHelperRequestSuccessHandle)sucess
                                          fail:(RequestHelperRequestFailHandle)fail;

+ (void)setFriendsRemarkNameWithParameters:(NSDictionary *)parameters
                          headerParameters:(NSDictionary *)headerParameters
                                  progress:(id)progress
                                  isScrete:(BOOL)screteState
                                    isAsyn:(BOOL)isAsyn
                                 identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                             successHandle:(RequestHelperRequestSuccessHandle)sucess
                                      fail:(RequestHelperRequestFailHandle)fail;

+ (void)judgeFriendStatusWithParameters:(NSDictionary *)parameters
                  headerParameters:(NSDictionary *)headerParameters
                          progress:(id)progress
                          isScrete:(BOOL)screteState
                            isAsyn:(BOOL)isAsyn
                         identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                     successHandle:(RequestHelperRequestSuccessHandle)sucess
                              fail:(RequestHelperRequestFailHandle)fail;

+ (void)getGroupMemberListWithParameters:(NSDictionary *)parameters
                  headerParameters:(NSDictionary *)headerParameters
                          progress:(id)progress
                          isScrete:(BOOL)screteState
                            isAsyn:(BOOL)isAsyn
                         identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                     successHandle:(RequestHelperRequestSuccessHandle)sucess
                              fail:(RequestHelperRequestFailHandle)fail;

+ (void)getGroupInfoWithParameters:(NSDictionary *)parameters
                  headerParameters:(NSDictionary *)headerParameters
                          progress:(id)progress
                          isScrete:(BOOL)screteState
                            isAsyn:(BOOL)isAsyn
                         identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                     successHandle:(RequestHelperRequestSuccessHandle)sucess
                              fail:(RequestHelperRequestFailHandle)fail;

+ (void)setGroupManagerRequestWithParameters:(NSDictionary *)parameters
                            headerParameters:(NSDictionary *)headerParameters
                                    progress:(id)progress
                                    isScrete:(BOOL)screteState
                                      isAsyn:(BOOL)isAsyn
                                   identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                               successHandle:(RequestHelperRequestSuccessHandle)sucess
                                        fail:(RequestHelperRequestFailHandle)fail;

+ (void)setGroupSilenceRequestWithParameters:(NSDictionary *)parameters
                            headerParameters:(NSDictionary *)headerParameters
                                    progress:(id)progress
                                    isScrete:(BOOL)screteState
                                      isAsyn:(BOOL)isAsyn
                                   identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                               successHandle:(RequestHelperRequestSuccessHandle)sucess
                                        fail:(RequestHelperRequestFailHandle)fail;
    
+ (void)setGroupMemberSilenceRequestWithParameters:(NSDictionary *)parameters
                                  headerParameters:(NSDictionary *)headerParameters
                                          progress:(id)progress
                                          isScrete:(BOOL)screteState
                                            isAsyn:(BOOL)isAsyn
                                         identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                                     successHandle:(RequestHelperRequestSuccessHandle)sucess
                                              fail:(RequestHelperRequestFailHandle)fail;

+ (void)getGroupManagerListWithParameters:(NSDictionary *)parameters
                         headerParameters:(NSDictionary *)headerParameters
                                 progress:(id)progress
                                 isScrete:(BOOL)screteState
                                   isAsyn:(BOOL)isAsyn
                                identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                            successHandle:(RequestHelperRequestSuccessHandle)sucess
                                     fail:(RequestHelperRequestFailHandle)fail;

//获取用户在群组中的角色
+ (void)getUserGroupRoleWithParameters:(NSDictionary *)parameters
                         headerParameters:(NSDictionary *)headerParameters
                                 progress:(id)progress
                                 isScrete:(BOOL)screteState
                                   isAsyn:(BOOL)isAsyn
                                identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                            successHandle:(RequestHelperRequestSuccessHandle)sucess
                                     fail:(RequestHelperRequestFailHandle)fail;

//群禁言
+ (void)setGroupManagerShutSendMsgWithParameters:(NSDictionary *)parameters
                                headerParameters:(NSDictionary *)headerParameters
                                        progress:(id)progress
                                        isScrete:(BOOL)screteState
                                          isAsyn:(BOOL)isAsyn
                                       identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                                   successHandle:(RequestHelperRequestSuccessHandle)sucess
                                            fail:(RequestHelperRequestFailHandle)fail;

//群禁言状态
+ (void)getGroupShutSendMsgStateWithParameters:(NSDictionary *)parameters
                              headerParameters:(NSDictionary *)headerParameters
                                      progress:(id)progress
                                      isScrete:(BOOL)screteState
                                        isAsyn:(BOOL)isAsyn
                                     identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                                 successHandle:(RequestHelperRequestSuccessHandle)sucess
                                          fail:(RequestHelperRequestFailHandle)fail;

+ (void)uploadMessageWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail;

+ (void)updateMessageWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail;

+ (void)getMessageListWithParameters:(NSDictionary *)parameters
                    headerParameters:(NSDictionary *)headerParameters
                            progress:(id)progress
                            isScrete:(BOOL)screteState
                              isAsyn:(BOOL)isAsyn
                           identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                       successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail;

+ (void)sendSingleRedPackageWithParameters:(NSDictionary *)parameters
                          headerParameters:(NSDictionary *)headerParameters
                                  progress:(id)progress
                                  isScrete:(BOOL)screteState
                                    isAsyn:(BOOL)isAsyn
                                 identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                             successHandle:(RequestHelperRequestSuccessHandle)sucess
                                      fail:(RequestHelperRequestFailHandle)fail;

+ (void)sendGroupRedPackageWithParameters:(NSDictionary *)parameters
                         headerParameters:(NSDictionary *)headerParameters
                                 progress:(id)progress
                                 isScrete:(BOOL)screteState
                                   isAsyn:(BOOL)isAsyn
                                identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                            successHandle:(RequestHelperRequestSuccessHandle)sucess
                                     fail:(RequestHelperRequestFailHandle)fail;

+ (void)receiveRedPacketWithParameters:(NSDictionary *)parameters
                      headerParameters:(NSDictionary *)headerParameters
                              progress:(id)progress
                              isScrete:(BOOL)screteState
                                isAsyn:(BOOL)isAsyn
                             identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                         successHandle:(RequestHelperRequestSuccessHandle)sucess
                                  fail:(RequestHelperRequestFailHandle)fail;

+ (void)searchBalanceWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail;

+ (void)setPayPassWord:(NSDictionary *)parameters
      headerParameters:(NSDictionary *)headerParameters
              progress:(id)progress
              isScrete:(BOOL)screteState
                isAsyn:(BOOL)isAsyn
             identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
         successHandle:(RequestHelperRequestSuccessHandle)sucess
                  fail:(RequestHelperRequestFailHandle)fail;

+ (void)getMessageListWithParameters:(NSDictionary *)parameters
                    headerParameters:(NSDictionary *)headerParameters
                            progress:(id)progress
                            isScrete:(BOOL)screteState
                              isAsyn:(BOOL)isAsyn
                           identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                       successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail;


+ (void)setBalanceListWithParameters:(NSDictionary *)parameters
                    headerParameters:(NSDictionary *)headerParameters
                            progress:(id)progress
                            isScrete:(BOOL)screteState
                              isAsyn:(BOOL)isAsyn
                           identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                       successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail;

+ (void)sendRedPackageInfoWithParameters:(NSDictionary *)parameters
                        headerParameters:(NSDictionary *)headerParameters
                                progress:(id)progress
                                isScrete:(BOOL)screteState
                                  isAsyn:(BOOL)isAsyn
                               identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                           successHandle:(RequestHelperRequestSuccessHandle)sucess
                                    fail:(RequestHelperRequestFailHandle)fail;

+ (void)sendRedPackageListWithParameters:(NSDictionary *)parameters
                        headerParameters:(NSDictionary *)headerParameters
                                progress:(id)progress
                                isScrete:(BOOL)screteState
                                  isAsyn:(BOOL)isAsyn
                               identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                           successHandle:(RequestHelperRequestSuccessHandle)sucess
                                    fail:(RequestHelperRequestFailHandle)fail;

+ (void)receiveRedPackageInfoWithParameters:(NSDictionary *)parameters
                           headerParameters:(NSDictionary *)headerParameters
                                   progress:(id)progress
                                   isScrete:(BOOL)screteState
                                     isAsyn:(BOOL)isAsyn
                                  identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                              successHandle:(RequestHelperRequestSuccessHandle)sucess
                                       fail:(RequestHelperRequestFailHandle)fail;

+ (void)receiveRedPackageListWithParameters:(NSDictionary *)parameters
                           headerParameters:(NSDictionary *)headerParameters
                                   progress:(id)progress
                                   isScrete:(BOOL)screteState
                                     isAsyn:(BOOL)isAsyn
                                  identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                              successHandle:(RequestHelperRequestSuccessHandle)sucess
                                       fail:(RequestHelperRequestFailHandle)fail;

+ (void)sendSMSCodeWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail;

+ (void)withdrawApplyWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail;

+ (void)withdrawApplyListWithParameters:(NSDictionary *)parameters
                       headerParameters:(NSDictionary *)headerParameters
                               progress:(id)progress
                               isScrete:(BOOL)screteState
                                 isAsyn:(BOOL)isAsyn
                              identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                          successHandle:(RequestHelperRequestSuccessHandle)sucess
                                   fail:(RequestHelperRequestFailHandle)fail;

+ (void)addBankCardWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail;

+ (void)sendDynamicWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail;

+ (void)deleteDynamicWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail;

+ (void)getAllDynamicListWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail;

+ (void)getFriendFynamiclistWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail;

+ (void)dynamicPraiseWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail;

+ (void)dynamicCancelPraiseWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail;

//评论回复评论
+ (void)dynamicCommandWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail;

+ (void)deleteDynamicCommandWithParameters:(NSDictionary *)parameters
                 headerParameters:(NSDictionary *)headerParameters
                         progress:(id)progress
                         isScrete:(BOOL)screteState
                           isAsyn:(BOOL)isAsyn
                        identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                    successHandle:(RequestHelperRequestSuccessHandle)sucess
                             fail:(RequestHelperRequestFailHandle)fail;

+ (void)dynamicCommandListWithParameters:(NSDictionary *)parameters
                          headerParameters:(NSDictionary *)headerParameters
                                  progress:(id)progress
                                  isScrete:(BOOL)screteState
                                    isAsyn:(BOOL)isAsyn
                                 identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                             successHandle:(RequestHelperRequestSuccessHandle)sucess
                                      fail:(RequestHelperRequestFailHandle)fail;

+ (void)dynamicGetBackImageWithParameters:(NSDictionary *)parameters
                         headerParameters:(NSDictionary *)headerParameters
                                 progress:(id)progress
                                 isScrete:(BOOL)screteState
                                   isAsyn:(BOOL)isAsyn
                                identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                            successHandle:(RequestHelperRequestSuccessHandle)sucess
                                     fail:(RequestHelperRequestFailHandle)fail;

+ (void)dynamicSetBackImageWithParameters:(NSDictionary *)parameters
                         headerParameters:(NSDictionary *)headerParameters
                                 progress:(id)progress
                                 isScrete:(BOOL)screteState
                                   isAsyn:(BOOL)isAsyn
                                identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                            successHandle:(RequestHelperRequestSuccessHandle)sucess
                                     fail:(RequestHelperRequestFailHandle)fail;

+ (void)commonUpLoadImage:(UIImage *)image progressBlock:(void(^)(CGFloat progress))progressBlocked sendResult:(void(^)(BOOL isSuccess,NSString *remotePath))resultBlocked;

+ (void)commonUpLoadImageWithoutCrop:(UIImage *)image progressBlock:(void(^)(CGFloat progress))progressBlocked sendResult:(void(^)(BOOL isSuccess,NSString *remotePath))resultBlocked;

+ (void)commonUpLoadVideo:(NSString *)localPath progressBlock:(void(^)(CGFloat progress))progressBlocked sendResult:(void(^)(BOOL isSuccess,NSString *remotePath))resultBlocked;

+ (void)uploadImageWithImage:(UIImage *)image userId:(NSString *)userId token:(NSDictionary *)tokenDic progressBlock:(void(^)(CGFloat progress))progressBlocked andMessageId:(NSString *)messageId andSendResult:(void(^)(BOOL isSuccess,NSString *remotePath))resultBlocked;

+ (void)updateTimeUnixWithParameters:(NSDictionary *)parameters
                    headerParameters:(NSDictionary *)headerParameters
                            progress:(id)progress
                            isScrete:(BOOL)screteState
                              isAsyn:(BOOL)isAsyn
                           identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                       successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail;

+ (void)checkTokenWithParameters:(NSDictionary *)parameters
                headerParameters:(NSDictionary *)headerParameters
                        progress:(id)progress
                        isScrete:(BOOL)screteState
                          isAsyn:(BOOL)isAsyn
                       identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                   successHandle:(RequestHelperRequestSuccessHandle)sucess
                            fail:(RequestHelperRequestFailHandle)fail;

+ (void)thidLoginWithParameters:(NSDictionary *)parameters
                    headerParameters:(NSDictionary *)headerParameters
                            progress:(id)progress
                            isScrete:(BOOL)screteState
                              isAsyn:(BOOL)isAsyn
                           identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                       successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail;

+ (void)searchMsgListWithParameters:(NSDictionary *)parameters
                   headerParameters:(NSDictionary *)headerParameters
                           progress:(id)progress
                           isScrete:(BOOL)screteState
                             isAsyn:(BOOL)isAsyn
                          identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                      successHandle:(RequestHelperRequestSuccessHandle)sucess
                               fail:(RequestHelperRequestFailHandle)fail;

+ (void)receiveRedPacketDetailWithParameters:(NSDictionary *)parameters
                            headerParameters:(NSDictionary *)headerParameters
                                    progress:(id)progress
                                    isScrete:(BOOL)screteState
                                      isAsyn:(BOOL)isAsyn
                                   identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                               successHandle:(RequestHelperRequestSuccessHandle)sucess
                                        fail:(RequestHelperRequestFailHandle)fail;

+ (void)bankCardListWithParameters:(NSDictionary *)parameters
                  headerParameters:(NSDictionary *)headerParameters
                          progress:(id)progress
                          isScrete:(BOOL)screteState
                            isAsyn:(BOOL)isAsyn
                         identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                     successHandle:(RequestHelperRequestSuccessHandle)sucess
                              fail:(RequestHelperRequestFailHandle)fail;

+ (void)deleteBankCardWithParameters:(NSDictionary *)parameters
                    headerParameters:(NSDictionary *)headerParameters
                            progress:(id)progress
                            isScrete:(BOOL)screteState
                              isAsyn:(BOOL)isAsyn
                           identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                       successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail;

+ (void)signWithParameters:(NSDictionary *)parameters
          headerParameters:(NSDictionary *)headerParameters
                  progress:(id)progress
                  isScrete:(BOOL)screteState
                    isAsyn:(BOOL)isAsyn
                 identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
             successHandle:(RequestHelperRequestSuccessHandle)sucess
                      fail:(RequestHelperRequestFailHandle)fail;

+ (void)weChatPayWithParameters:(NSDictionary *)parameters
               headerParameters:(NSDictionary *)headerParameters
                       progress:(id)progress
                       isScrete:(BOOL)screteState
                         isAsyn:(BOOL)isAsyn
                      identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                  successHandle:(RequestHelperRequestSuccessHandle)sucess
                           fail:(RequestHelperRequestFailHandle)fail;

+ (void)aliPayWithParameters:(NSDictionary *)parameters
            headerParameters:(NSDictionary *)headerParameters
                    progress:(id)progress
                    isScrete:(BOOL)screteState
                      isAsyn:(BOOL)isAsyn
                   identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
               successHandle:(RequestHelperRequestSuccessHandle)sucess
                        fail:(RequestHelperRequestFailHandle)fail;

+ (void)groupNoticeListWithParameters:(NSDictionary *)parameters
                     headerParameters:(NSDictionary *)headerParameters
                             progress:(id)progress
                             isScrete:(BOOL)screteState
                               isAsyn:(BOOL)isAsyn
                            identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                        successHandle:(RequestHelperRequestSuccessHandle)sucess
                                 fail:(RequestHelperRequestFailHandle)fail;

+ (void)groupNoticePublishWithParameters:(NSDictionary *)parameters
                        headerParameters:(NSDictionary *)headerParameters
                                progress:(id)progress
                                isScrete:(BOOL)screteState
                                  isAsyn:(BOOL)isAsyn
                               identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                           successHandle:(RequestHelperRequestSuccessHandle)sucess
                                    fail:(RequestHelperRequestFailHandle)fail;

+ (void)groupAuthCreateWithParameters:(NSDictionary *)parameters
                     headerParameters:(NSDictionary *)headerParameters
                             progress:(id)progress
                             isScrete:(BOOL)screteState
                               isAsyn:(BOOL)isAsyn
                            identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                        successHandle:(RequestHelperRequestSuccessHandle)sucess
                                 fail:(RequestHelperRequestFailHandle)fail;

+ (void)xcxWithParameters:(NSDictionary *)parameters
         headerParameters:(NSDictionary *)headerParameters
                 progress:(id)progress
                 isScrete:(BOOL)screteState
                   isAsyn:(BOOL)isAsyn
                identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
            successHandle:(RequestHelperRequestSuccessHandle)sucess
                     fail:(RequestHelperRequestFailHandle)fail;

+ (void)checkVersionWithParameters:(NSDictionary *)parameters
                  headerParameters:(NSDictionary *)headerParameters
                          progress:(id)progress
                          isScrete:(BOOL)screteState
                            isAsyn:(BOOL)isAsyn
                         identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                     successHandle:(RequestHelperRequestSuccessHandle)sucess
                              fail:(RequestHelperRequestFailHandle)fail;

+ (void)friendApplyDeleteWithParameters:(NSDictionary *)parameters
                       headerParameters:(NSDictionary *)headerParameters
                               progress:(id)progress
                               isScrete:(BOOL)screteState
                                 isAsyn:(BOOL)isAsyn
                              identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                          successHandle:(RequestHelperRequestSuccessHandle)sucess
                                   fail:(RequestHelperRequestFailHandle)fail;

+(void)requestWithRequestModel:(ProjectRequestModel *)requestModel identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                 successHandle:(RequestHelperRequestSuccessHandle)sucess
                          fail:(RequestHelperRequestFailHandle)fail;

+ (void)withdrawConfigParameters:(NSDictionary *)parameters
                headerParameters:(NSDictionary *)headerParameters
                        progress:(id)progress
                        isScrete:(BOOL)screteState
                          isAsyn:(BOOL)isAsyn
                       identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                   successHandle:(RequestHelperRequestSuccessHandle)sucess
                            fail:(RequestHelperRequestFailHandle)fail;

+ (void)feedbackWithParameters:(NSDictionary *)parameters
              headerParameters:(NSDictionary *)headerParameters
                      progress:(id)progress
                      isScrete:(BOOL)screteState
                        isAsyn:(BOOL)isAsyn
                     identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                 successHandle:(RequestHelperRequestSuccessHandle)sucess
                          fail:(RequestHelperRequestFailHandle)fail;

+ (void)deleteGroupNoticeWithParameters:(NSDictionary *)parameters
                       headerParameters:(NSDictionary *)headerParameters
                               progress:(id)progress
                               isScrete:(BOOL)screteState
                                 isAsyn:(BOOL)isAsyn
                              identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
                          successHandle:(RequestHelperRequestSuccessHandle)sucess
                                   fail:(RequestHelperRequestFailHandle)fail;

+ (void)payTradeStatusWithParameters:(NSDictionary *)parameters
headerParameters:(NSDictionary *)headerParameters
        progress:(id)progress
        isScrete:(BOOL)screteState
          isAsyn:(BOOL)isAsyn
       identider:(RequestHelperRequestGetRequestIdentify)identifierBlock
   successHandle:(RequestHelperRequestSuccessHandle)sucess
                                fail:(RequestHelperRequestFailHandle)fail;
@end

NS_ASSUME_NONNULL_END
