<p>
    <span style="box-sizing: border-box; font-weight: 700; color: rgb(51, 51, 51); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">凡信3.0 更新介绍</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">&nbsp;对于凡信项目的背景在此不再赘述，不了解的前往：</span><a href="https://github.com/huangfangyi/FanXin2.0_IM" target="_blank">https://github.com/huangfangyi/FanXin2.0_IM</a><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">或者:</span><a href="http://www.imgeek.org/article/825307627">http://www.imgeek.org/article/825307627</a><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="box-sizing: border-box; font-weight: 700; color: rgb(51, 51, 51); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">前言:</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">&nbsp; &nbsp;由于凡信的1.0和2.0都是基于环信SDK 2.x系列开发，而当前环信官方力推的是3.x的系列SDK，在此背景下，作者决定将凡信迁移至3.x的demo上。迁移的同时，对存储机制和网络接口做了一定的优化。与此同时，针对时下火热的直播APP，结合环信的聊天室功能和ucloud，做了两个模块-观看直播和进行直播;针对IM场景中常见的发红包/抢红包，集成了由云账户提供的红包SDK（</span><a href="https://www.yunzhanghu.com/" target="_blank">https://www.yunzhanghu.com</a><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">）,对于想做红包以及账户管理的开发者，是一种非常值得推荐的解决方案，一是开发者不用头疼于安全问题，以及开发中逻辑不严谨导致的资金转移丢包的问题。</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="box-sizing: border-box; font-weight: 700; color: rgb(51, 51, 51); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">资源相关：</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/>
</p>
<ul style="box-sizing: border-box; padding: 0px 40px; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);" class=" list-paddingleft-2">
    <li>
        <p>
            APK体验地址：<a href="http://fir.im/wy79" target="_blank">http://fir.im/wy79</a>
        </p>
    </li>
    <li>
        <p>
            github工程源码（主要更新源）：<a href="https://github.com/huangfangyi/FanXin3.0" target="_blank">https://github.com/huangfangyi/FanXin3.0</a>
        </p>
    </li>
    <li>
        <p>
            二维码安装：
        </p>
        <p>
            <a href="http://www.imgeek.org/uploads/article/20160717/7d706304e3c4a2258bd50d8a3305d07f.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/7d706304e3c4a2258bd50d8a3305d07f.png" title="下载.png" alt="下载.png"/></a>
        </p>
    </li>
    <li>
        <p>
            <span style="box-sizing: border-box; font-weight: 700; color: rgb(51, 51, 51);">作者QQ:84543217</span>(QQ也只处理外包开发需求)
        </p>
    </li>
    <li>
        <p>
            凡信开发者讨论群：<span style="box-sizing: border-box; font-weight: 700; color: rgb(51, 51, 51);">366135448</span>
        </p>
    </li>
    <li>
        <p>
            APP红包功能讨论群（android+ios）<span style="box-sizing: border-box; font-weight: 700; color: rgb(51, 51, 51);">:437758366</span>（1500名小伙伴等着你哦！）
        </p>
    </li>
</ul>
<p>
    <br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">此次更新分三个部分进行详细介绍</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/>
</p>
<ul style="box-sizing: border-box; padding: 0px 40px; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);" class=" list-paddingleft-2">
    <li>
        <p>
            直播功能
        </p>
    </li>
    <li>
        <p>
            红包功能
        </p>
    </li>
    <li>
        <p>
            对凡信2.0已有功能的重构以及优化
        </p>
    </li>
</ul>
<p>
    <span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">&nbsp;</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="box-sizing: border-box; font-weight: 700; color: rgb(51, 51, 51); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">一.直播功能：</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">直播间的开发涉及三个要点：</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/>
</p>
<ul style="box-sizing: border-box; padding: 0px 40px; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);" class=" list-paddingleft-2">
    <li>
        <p>
            一是观看的视频来源（拉流）
        </p>
    </li>
    <li>
        <p>
            二是主播直播的实时视频的推送（推流）
        </p>
    </li>
    <li>
        <p>
            三是直播间：主播与粉丝之间、粉丝和粉丝之间的字幕互动
        </p>
    </li>
</ul>
<p>
    <span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">&nbsp;</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">凡信中的解决方案是：</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/>
</p>
<ul style="box-sizing: border-box; padding: 0px 40px; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);" class=" list-paddingleft-2">
    <li>
        <p>
            观看直播（拉流）和开始直播（推流）只需要传入一个rtmp视频流即可，本项目用了一个香港卫视的电台直播视频流方便大家测试，另外一种就是通过设置直播间id进行推流，然后输入对应id进行拉流的方式，开发者可以通过两部手机进行测试。前提是保证网络环境良好。
        </p>
    </li>
    <li>
        <p>
            直播间的互动，一般直播直播平台会有打字弹幕、赠送礼物、私聊等功能，综合起来也就是通信问题。目前凡信中基于环信的聊天室功能，完成了打字互动的部分，其他部分可以依据这个原理实现，无非就是对消息进行分类（聊天消息、礼物消息、私聊消息）。
        </p>
    </li>
</ul>
<p>
    <br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">相关APP截图如下：</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/b0c6847ca3e7896778826d9a16842658.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/b0c6847ca3e7896778826d9a16842658.png" title="QQ图片20160716231610.png" alt="QQ图片20160716231610.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/32ae4d0c21ac7073b0d8c89c30a22411.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/32ae4d0c21ac7073b0d8c89c30a22411.png" title="QQ图片20160716231623.png" alt="QQ图片20160716231623.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/f742b8da4404a6a86dc0be37c4ed4223.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/f742b8da4404a6a86dc0be37c4ed4223.png" title="QQ图片20160716231631.png" alt="QQ图片20160716231631.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/66b107b5a310180f2b21a3c00b0668d6.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/66b107b5a310180f2b21a3c00b0668d6.png" title="Screenshot_20160716-233111.png" alt="Screenshot_20160716-233111.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/16fc92c018b4bf1e7b6624655b605725.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/16fc92c018b4bf1e7b6624655b605725.png" title="Screenshot_20160716-233114.png" alt="Screenshot_20160716-233114.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/85e8eb15c08a24ae0831c3466efd043d.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/85e8eb15c08a24ae0831c3466efd043d.png" title="Screenshot_20160716-233118.png" alt="Screenshot_20160716-233118.png"/></a>
</p>
<p>
    <br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="box-sizing: border-box; font-weight: 700; color: rgb(51, 51, 51); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">二、红包功能：-采用云账户提供的红包SDK实现</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">&nbsp; 红包在社交APP中的需求越来越多，对于这块的自主开发涉及问题至少会包含以下两点：一是安全问题--支付安全和资金安全。二是丢包问题，如红包玩法中，发红包的资金涉及冻结资金，领取的要结算，过期的要回账，还例如领取状态的通知回调等等。越是复杂高频的收发红包及转账场景，怎么在移动端及服务端保证每笔交易都有完善的跟踪和容错机制，都是开发者必须要考虑的问题。然而设计一个完整的解决方案并研发成功，需要投入的技术成本和时间成本是非常高的，然而红包仅仅是APP的一个辅助模块而已，占据过高的成本得不偿失。为此，云账户推出了适用各种应用场景的红包SDK，帮助解决这个效率问题，开发者可以利用简单的几个小时完成一套红包解决方案。目前，云账户针对主流的SaaS平台推出了集成红包后的demo，具体详情请见：</span><a href="https://www.yunzhanghu.com/download.html" target="_blank">https://www.yunzhanghu.com/download.html</a><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">&nbsp;（目前得知的是已完成环信、融云、Leancloud、亲加、容联等平台demo的集成，后面几个近期即将发布。）如果有小伙伴对红包或者账户资金管理功能感兴趣的，可以加入红包功能讨论QQ群：366135448，一起探讨APP变现模式,以及红包的新玩法。</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">&nbsp;</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">部分APP截图如下：</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/b49efb082da022653bce55ba8799f52e.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/b49efb082da022653bce55ba8799f52e.png" title="Screenshot_20160717-013204.png" alt="Screenshot_20160717-013204.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/138e677897036353be7d3919f4b8c8b8.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/138e677897036353be7d3919f4b8c8b8.png" title="Screenshot_20160717-013228.png" alt="Screenshot_20160717-013228.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/d6099ccc1aedad232fc7d31727b2def8.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/d6099ccc1aedad232fc7d31727b2def8.png" title="Screenshot_20160717-013339.png" alt="Screenshot_20160717-013339.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/7b27a00aafaf98d701838d5931eab30f.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/7b27a00aafaf98d701838d5931eab30f.png" title="Screenshot_20160717-013859.png" alt="Screenshot_20160717-013859.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/9ab02e3d4c01e132a1b59ea709f53eba.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/9ab02e3d4c01e132a1b59ea709f53eba.png" title="Screenshot_20160717-013902.png" alt="Screenshot_20160717-013902.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/d8823a47292d1c2bb202b5bcc6e699e9.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/d8823a47292d1c2bb202b5bcc6e699e9.png" title="Screenshot_20160717-013910.png" alt="Screenshot_20160717-013910.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/1b6144d80cca6b3e2473afe0b062030a.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/1b6144d80cca6b3e2473afe0b062030a.png" title="Screenshot_20160717-014025.png" alt="Screenshot_20160717-014025.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/b14e33e26587097534ad996370a59c9e.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/b14e33e26587097534ad996370a59c9e.png" title="Screenshot_20160717-014032.png" alt="Screenshot_20160717-014032.png"/></a>
</p>
<p>
    <br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="box-sizing: border-box; font-weight: 700; color: rgb(51, 51, 51); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">三、针对凡信2.0已有功能的优化</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/>
</p>
<ol style="box-sizing: border-box; padding: 0px 40px; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);" class=" list-paddingleft-2">
    <li>
        <p>
            注册接口中，注册环信账号采用授权注册，放置于后端处理
        </p>
    </li>
    <li>
        <p>
            原先的凡信服务端接口的访问机制改成用okhttp处理。
        </p>
    </li>
    <li>
        <p>
            环信SDK由2.x系列转至3.x,采用的版本（版本 V3.1.3 R2 2016-6-15）
        </p>
    </li>
    <li>
        <p>
            朋友圈：a.点击头像查看用户资料。b.大图加载库从ImageLoader转至Glide。c.点击评论图标弹出的popwindow错位问题修复。
        </p>
    </li>
    <li>
        <p>
            个人中心-&gt;资料更新接口合并
        </p>
    </li>
    <li>
        <p>
            增加二维码、扫一扫
        </p>
    </li>
    <li>
        <p>
            钱包--原先设置全部删除，采用云账户的红包SDK处理
        </p>
    </li>
    <li>
        <p>
            当前用户资料处理机制----封装在JSONObject类中，在内存层和Perference双层管
        </p>
    </li>
    <li>
        <p>
            群头像--由5种组合转至9种组合。
        </p>
    </li>
    <li>
        <p>
            好友资料管理增加userInfo字段，用于可扩展的用户资料系统
        </p>
    </li>
    <li>
        <p>
            提取出几个工具类
        </p>
    </li>
    <li>
        <p>
            凡信相关的核心代码目录：com.fanxin.app.main.*;资源相关以fx_为前缀
        </p>
    </li>
</ol>
<p>
    <span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">&nbsp;</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">部分APP相关截图</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="box-sizing: border-box; font-weight: 700; color: rgb(51, 51, 51); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">1.朋友圈相关</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/8cf74ae03b69c0d70d6df8d6bb7d6812.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/8cf74ae03b69c0d70d6df8d6bb7d6812.png" title="Screenshot_20160717-180219.png" alt="Screenshot_20160717-180219.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/54e375994e3cf372b58134b0d22c174c.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/54e375994e3cf372b58134b0d22c174c.png" title="Screenshot_20160717-180517.png" alt="Screenshot_20160717-180517.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/1a0efded1ad9f81b02ddbe76ff07eae5.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/1a0efded1ad9f81b02ddbe76ff07eae5.png" title="Screenshot_20160717-180529.png" alt="Screenshot_20160717-180529.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/445bf919e3e746cdd7f6c7b2d0c7bbcb.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/445bf919e3e746cdd7f6c7b2d0c7bbcb.png" title="Screenshot_20160717-180539.png" alt="Screenshot_20160717-180539.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/3e4cce31f897e829fdde5dea4f20970a.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/3e4cce31f897e829fdde5dea4f20970a.png" title="Screenshot_20160717-180628.png" alt="Screenshot_20160717-180628.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/919ea402878d035196d20619751f9bf4.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/919ea402878d035196d20619751f9bf4.png" title="Screenshot_20160717-180638.png" alt="Screenshot_20160717-180638.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/e4b987c2eaa7f6ce2d2cda60f26d0db5.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/e4b987c2eaa7f6ce2d2cda60f26d0db5.png" title="Screenshot_20160717-180955.png" alt="Screenshot_20160717-180955.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/c0594ddbe7cc20badbbeeac45578e404.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/c0594ddbe7cc20badbbeeac45578e404.png" title="Screenshot_20160717-181131.png" alt="Screenshot_20160717-181131.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/73d7383f9c8d01679932112ac62be5ba.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/73d7383f9c8d01679932112ac62be5ba.png" title="Screenshot_20160717-181155.png" alt="Screenshot_20160717-181155.png"/></a>
</p>
<p>
    <br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">&nbsp;</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><span style="box-sizing: border-box; font-weight: 700; color: rgb(51, 51, 51); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);">2.主页相关</span><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/><br style="box-sizing: border-box; color: rgb(102, 102, 102); font-family: &quot;Helvetica Neue&quot;, STHeiti, 微软雅黑, &quot;Microsoft YaHei&quot;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 22.4px; white-space: normal; background-color: rgb(255, 255, 255);"/>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/3c9c8160cde2e0cb2a50606020c5e485.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/3c9c8160cde2e0cb2a50606020c5e485.png" title="Screenshot_20160717-173709.png" alt="Screenshot_20160717-173709.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/ab6c0447be60a855de45d54f70895381.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/ab6c0447be60a855de45d54f70895381.png" title="Screenshot_20160717-173720.png" alt="Screenshot_20160717-173720.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/2abcdb3f92ad0ca618d559b8e750efcd.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/2abcdb3f92ad0ca618d559b8e750efcd.png" title="Screenshot_20160717-174859.png" alt="Screenshot_20160717-174859.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/5487d02a097cdcf21eafbb16b2dd0f3c.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/5487d02a097cdcf21eafbb16b2dd0f3c.png" title="Screenshot_20160717-174904.png" alt="Screenshot_20160717-174904.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/762f16e890e8f8155c1709b59c913384.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/762f16e890e8f8155c1709b59c913384.png" title="Screenshot_20160717-181229.png" alt="Screenshot_20160717-181229.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/62476a68b78d922a1c71ed8e26e013f5.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/62476a68b78d922a1c71ed8e26e013f5.png" title="Screenshot_20160717-170834.png" alt="Screenshot_20160717-170834.png"/></a>
</p>
<p>
    <a href="http://www.imgeek.org/uploads/article/20160717/e78a5d94e4f7eeacdf1f634f2b0c7e5a.png" target="_blank"><img src="http://www.imgeek.org/uploads/article/20160717/e78a5d94e4f7eeacdf1f634f2b0c7e5a.png" title="Screenshot_20160717-181215.png" alt="Screenshot_20160717-181215.png"/></a>
</p>
<p>
    <br/>
</p>