//
//  YiChatHelpModel.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatHelpModel.h"

@implementation YiChatHelpModel

+ (NSArray *)createModel{
    NSMutableArray *contentModelList = [NSMutableArray arrayWithCapacity:0];
    
    YiChatHelpModel *model = [[YiChatHelpModel alloc] init];
    model.sectionTitle = @"常见问题";
    
    YiChatHelpContentModel *contentModel_redPacket = [[YiChatHelpContentModel alloc] init];
    contentModel_redPacket.contentTitle = @"发红包钱已扣，但是聊天页面未显示红包怎么办？";
    contentModel_redPacket.contentText = @"请不要担心，这种情况可能是手机网络不佳导致的，实际红包并未发出，也不会有人领取，红包将在24小时后退回您的账户。";
    
    YiChatHelpContentModel *contentModel_sendRedPacket = [[YiChatHelpContentModel alloc] init];
    contentModel_sendRedPacket.contentTitle = @"如何发红包";
    contentModel_sendRedPacket.contentText = @"无论是单聊还是群聊，均可以给好友发红包，步骤如下：\r\n 1.在聊天界面点击右下角 + 按钮 – 点击“红包”按钮。 \r\n 2.输入发送红包的金额，填写祝福语（群聊中可选择红包数量，以随机红包形式发放）\r\n3.点击发送即可。\r\n注意：每个红包有效期24小时，超时未领取的部分会自动退回你的钱包账户。";
    
    YiChatHelpContentModel *contentModel_Log = [[YiChatHelpContentModel alloc] init];
    contentModel_Log.contentTitle = @"如何登录？";
    contentModel_Log.contentText = @"在登录界面输入手机号和密码，点击登录即可。";
    
    YiChatHelpContentModel *contentModel_forget = [[YiChatHelpContentModel alloc] init];
    contentModel_forget.contentTitle = @"忘记密码怎么办？";
    contentModel_forget.contentText = @"在登录界面点击忘记密码，输入手机号和新密码即可重置密码。";
    
    YiChatHelpContentModel *contentModel_shenchi = [[YiChatHelpContentModel alloc] init];
    contentModel_shenchi.contentTitle = @"被封号了怎么办？";
    contentModel_shenchi.contentText = @"在登录界面点击申斥解封，在申斥解封页面输入被封的手机，在下面的输入框叙述一下实际情况，即可发起申斥。一般情况下不会随意封号！";
    
    YiChatHelpContentModel *contentModel_addFriend = [[YiChatHelpContentModel alloc] init];
    contentModel_addFriend.contentTitle = @"如何添加好友";
    contentModel_addFriend.contentText = @"您可以在“消息”页面点击右上角+按钮，选择添加好友，或者在“通讯录”页面顶部点击添加好友。在“添加好友”页面中可以通过4种方式添加好友。\r\n 1.搜索查找 \r\n在顶部搜索框里直接搜索对方的手机号/城信好并添加好友。 \r\n 2.扫一扫 \r\n点击扫一扫，直接扫对方的城信二维码放在扫描框中进行识别，然后点击添加好友即可。";
    
    YiChatHelpContentModel *contentModel_addGroup = [[YiChatHelpContentModel alloc] init];
    contentModel_addGroup.contentTitle = @"如何加群";
    contentModel_addGroup.contentText = @"您可以通过扫描群二维码和被群主邀请两种方式进群：\r\n扫描群二维码加群：\r\n无论是在城信还是在微信中，只要收到了朋友发来的城信群二维码，即可直接长按扫码加群。\r\n群主邀请进群：\r\n如果群主关闭了二维码进群验证，或二维码失效，则只能联系群主拉你进群。";
    
    YiChatHelpContentModel *contentModel_createGroup = [[YiChatHelpContentModel alloc] init];
    contentModel_createGroup.contentTitle = @"如何发起群聊";
    contentModel_createGroup.contentText = @"在“消息”页面中点击右上角的+按钮，选择发起群聊，选择您想加入群聊的好友并点击右上角完成即可。如需修改群名，点击群聊页面右上角按钮进入修改。";
    
    YiChatHelpContentModel *contentModel_groupDelegatePerson = [[YiChatHelpContentModel alloc] init];
    contentModel_groupDelegatePerson.contentTitle = @"如何删除群成员";
    contentModel_groupDelegatePerson.contentText = @"1.如果您是群主，可以在聊天详情页直接添加/删除好友。\r\n 2.如果您是普通群成员，可以通过分享群二维码邀请好友进群，删除群成员只可以群主操作。";
    
    YiChatHelpContentModel *contentModel_soundSet = [[YiChatHelpContentModel alloc] init];
    contentModel_soundSet.contentTitle = @"开启声音与振动";
    contentModel_soundSet.contentText = @"一、打开手机的声音与振动 \r\b 依次点击手机“设置-铃声与振动”，在此页面中选择合适的铃声音量，并且下拉找到振动选项，开启振动即可。\r\n 二、允许默聊开启声音与振动 \r\n依次点击手机“设置-通知和状态栏-通知管理”，找到默聊并点击进入，允许通知并将此页面其他选项选择开启即可。";
    
    [contentModelList addObject:contentModel_Log];
    [contentModelList addObject:contentModel_forget];
    [contentModelList addObject:contentModel_addFriend];
    [contentModelList addObject:contentModel_addGroup];
    [contentModelList addObject:contentModel_createGroup];
    [contentModelList addObject:contentModel_groupDelegatePerson];
    [contentModelList addObject:contentModel_sendRedPacket];
    [contentModelList addObject:contentModel_redPacket];
    [contentModelList addObject:contentModel_soundSet];
    [contentModelList addObject:contentModel_shenchi];
    
    model.contentList = contentModelList;
    
    return @[model];
    
}


@end

@implementation YiChatHelpExtensionModel



@end


@implementation YiChatHelpContentModel

@end
