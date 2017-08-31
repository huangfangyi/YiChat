package com.htmessage.fanxinht.acitivity.chat.weight.emojicon;


import com.htmessage.fanxinht.R;

public class EmojiconDatas {
    
    private static String[] emojis = new String[]{
        SmileUtils.ee_1,
        SmileUtils.ee_2,
        SmileUtils.ee_3,
        SmileUtils.ee_4,
        SmileUtils.ee_5,
        SmileUtils.ee_6,
        SmileUtils.ee_7,
        SmileUtils.ee_8,
        SmileUtils.ee_9,
        SmileUtils.ee_10,
        SmileUtils.ee_11,
        SmileUtils.ee_12,
        SmileUtils.ee_13,
        SmileUtils.ee_14,
        SmileUtils.ee_15,
        SmileUtils.ee_16,
        SmileUtils.ee_17,
        SmileUtils.ee_18,
        SmileUtils.ee_19,
        SmileUtils.ee_20,
        SmileUtils.ee_21,
        SmileUtils.ee_22,
        SmileUtils.ee_23,
        SmileUtils.ee_24,
        SmileUtils.ee_25,
        SmileUtils.ee_26,
        SmileUtils.ee_27,
        SmileUtils.ee_28,
        SmileUtils.ee_29,
        SmileUtils.ee_30,
        SmileUtils.ee_31,
        SmileUtils.ee_32,
        SmileUtils.ee_33,
        SmileUtils.ee_34,
        SmileUtils.ee_35,
       
    };
    
    private static int[] icons = new int[]{
            R.drawable.ee_0,
        R.drawable.ee_1,  
        R.drawable.ee_2,
        R.drawable.ee_3,  
        R.drawable.ee_4,  
        R.drawable.ee_5,  
        R.drawable.ee_6,  
        R.drawable.ee_7,  
        R.drawable.ee_8,  
        R.drawable.ee_9,  
        R.drawable.ee_10,  
        R.drawable.ee_11,  
        R.drawable.ee_12,  
        R.drawable.ee_13,  
        R.drawable.ee_14,  
        R.drawable.ee_15,  
        R.drawable.ee_16,  
        R.drawable.ee_17,  
        R.drawable.ee_18,  
        R.drawable.ee_19,  
        R.drawable.ee_20,  
        R.drawable.ee_21,  
        R.drawable.ee_22,  
        R.drawable.ee_23,  
        R.drawable.ee_24,  
        R.drawable.ee_25,  
        R.drawable.ee_26,  
        R.drawable.ee_27,  
        R.drawable.ee_28,  
        R.drawable.ee_29,  
        R.drawable.ee_30,  
        R.drawable.ee_31,  
        R.drawable.ee_32,  
        R.drawable.ee_33,  
        R.drawable.ee_34,  

    };
    
    
    private static final Emojicon[] DATA = createData();
    
    private static Emojicon[] createData(){
        Emojicon[] datas = new Emojicon[icons.length];
        for(int i = 0; i < icons.length; i++){
            datas[i] = new Emojicon(icons[i], emojis[i], Emojicon.Type.NORMAL);
        }
        return datas;
    }
    
    public static Emojicon[] getData(){
        return DATA;
    }
}
