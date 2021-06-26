package com.htmessage.update.uitls;

/**
 * Created by huangfangyi on 2019/8/10.
 * qq 84543217
 */
public class RedWalletUtils {
    private static RedWalletUtils redWalletUtils;

    public static RedWalletUtils getInstance() {
        if (redWalletUtils == null) {
            redWalletUtils = new RedWalletUtils();
        }
        return redWalletUtils;
    }



}
