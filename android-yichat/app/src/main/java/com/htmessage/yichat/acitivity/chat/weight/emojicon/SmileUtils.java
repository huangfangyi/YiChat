/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htmessage.yichat.acitivity.chat.weight.emojicon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.htmessage.yichat.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SmileUtils {
    public static final String DELETE_KEY = "em_delete_delete_expression";
    
//	public static final String ee_1 = "[):]";
//	public static final String ee_2 = "[:D]";
//	public static final String ee_3 = "[;)]";
//	public static final String ee_4 = "[:-o]";
//	public static final String ee_5 = "[:p]";
//	public static final String ee_6 = "[(H)]";
//	public static final String ee_7 = "[:@]";
//	public static final String ee_8 = "[:s]";
//	public static final String ee_9 = "[:$]";
//	public static final String ee_10 = "[:(]";
//	public static final String ee_11 = "[:-(]";
//	public static final String ee_12 = "[:|]";
//	public static final String ee_13 = "[(a)]";
//	public static final String ee_14 = "[8o|]";
//	public static final String ee_15 = "[8-|]";
//	public static final String ee_16 = "[+o(]";
//	public static final String ee_17 = "[oo)]";
//	public static final String ee_18 = "[|-)]";
//	public static final String ee_19 = "[*-)]";
//	public static final String ee_20 = "[:-#]";
//	public static final String ee_21 = "[:-*]";
//	public static final String ee_22 = "[^o)]";
//	public static final String ee_23 = "[8-)]";
//	public static final String ee_24 = "[(|)]";
//	public static final String ee_25 = "[(u)]";
//	public static final String ee_26 = "[(S)]";
//	public static final String ee_27 = "[(*)]";
//	public static final String ee_28 = "[(#)]";
//	public static final String ee_29 = "[(R)]";
//	public static final String ee_30 = "[({)]";
//	public static final String ee_31 = "[(})]";
//	public static final String ee_32 = "[(k)]";
//	public static final String ee_33 = "[(F)]";
//	public static final String ee_34 = "[(W)]";
//	public static final String ee_35 = "[(D)]";

  	public static final String emoji_001 = "[emoji_1]";
	public static final String emoji_002 = "[emoji_2]";
	public static final String emoji_003 = "[emoji_3]";
	public static final String emoji_004 = "[emoji_4]";
	public static final String emoji_005 = "[emoji_5]";
	public static final String emoji_006 = "[emoji_6]";
	public static final String emoji_007 = "[emoji_7]";
	public static final String emoji_008 = "[emoji_8]";
	public static final String emoji_009 = "[emoji_9]";
	public static final String emoji_010 = "[emoji_10]";
	public static final String emoji_11 = "[emoji_11]";
	public static final String emoji_12 = "[emoji_12]";
	public static final String emoji_13 = "[emoji_13]";
	public static final String emoji_14 = "[emoji_14]";
	public static final String emoji_15 = "[emoji_15]";
	public static final String emoji_16 = "[emoji_16]";
	public static final String emoji_17 = "[emoji_17]";
	public static final String emoji_18 = "[emoji_18]";
	public static final String emoji_19 = "[emoji_19]";
	public static final String emoji_20 = "[emoji_20]";
	public static final String emoji_21 = "[emoji_21]";
	public static final String emoji_22 = "[emoji_22]";
	public static final String emoji_23 = "[emoji_23]";
	public static final String emoji_24 = "[emoji_24]";
	public static final String emoji_25 = "[emoji_25]";
	public static final String emoji_26 = "[emoji_26]";
	public static final String emoji_27 = "[emoji_27]";
	public static final String emoji_28 = "[emoji_28]";
	public static final String emoji_29 = "[emoji_29]";
	public static final String emoji_30 = "[emoji_30]";
	public static final String emoji_31 = "[emoji_31]";
	public static final String emoji_32 = "[emoji_32]";
	public static final String emoji_33 = "[emoji_33]";
	public static final String emoji_34 = "[emoji_34]";
	public static final String emoji_35 = "[emoji_35]";
	public static final String emoji_36 = "[emoji_36]";
	public static final String emoji_37 = "[emoji_37]";
	public static final String emoji_38 = "[emoji_38]";
	public static final String emoji_39 = "[emoji_39]";
	public static final String emoji_40 = "[emoji_40]";
	public static final String emoji_41 = "[emoji_41]";
	public static final String emoji_42 = "[emoji_42]";
	public static final String emoji_43 = "[emoji_43]";

 	public static final String emoji_45 = "[emoji_45]";
	public static final String emoji_46 = "[emoji_46]";
	public static final String emoji_47 = "[emoji_47]";
	public static final String emoji_48 = "[emoji_48]";
	public static final String emoji_49 = "[emoji_49]";
	public static final String emoji_50 = "[emoji_50]";
	public static final String emoji_51 = "[emoji_51]";
	public static final String emoji_52 = "[emoji_52]";
	public static final String emoji_53 = "[emoji_53]";
	public static final String emoji_54 = "[emoji_54]";
	public static final String emoji_55 = "[emoji_55]";
	public static final String emoji_56 = "[emoji_56]";
	public static final String emoji_57 = "[emoji_57]";

	public static final String emoji_58 = "[emoji_58]";
	public static final String emoji_59 = "[emoji_59]";
	public static final String emoji_60 = "[emoji_60]";
	public static final String emoji_61 = "[emoji_61]";
	public static final String emoji_62 = "[emoji_62]";
	public static final String emoji_63 = "[emoji_63]";
	public static final String emoji_64 = "[emoji_64]";
	public static final String emoji_65 = "[emoji_65]";
	public static final String emoji_66 = "[emoji_66]";
	public static final String emoji_67 = "[emoji_67]";
	public static final String emoji_68 = "[emoji_68]";
	public static final String emoji_69 = "[emoji_69]";
	public static final String emoji_70 = "[emoji_70]";
	public static final String emoji_71 = "[emoji_71]";

	public static final String emoji_72 = "[emoji_72]";
	public static final String emoji_73 = "[emoji_73]";
	public static final String emoji_74 = "[emoji_74]";
	public static final String emoji_75 = "[emoji_75]";
	public static final String emoji_76 = "[emoji_76]";
	public static final String emoji_77 = "[emoji_77]";
	public static final String emoji_78 = "[emoji_78]";
	public static final String emoji_79 = "[emoji_79]";
	public static final String emoji_80 = "[emoji_80]";
	public static final String emoji_81 = "[emoji_81]";
	public static final String emoji_82 = "[emoji_82]";
	public static final String emoji_83 = "[emoji_83]";
	public static final String emoji_84 = "[emoji_84]";
	public static final String emoji_85 = "[emoji_85]";

	public static final String emoji_86 = "[emoji_86]";
	public static final String emoji_87 = "[emoji_87]";
	public static final String emoji_88 = "[emoji_88]";
	public static final String emoji_89 = "[emoji_89]";
	public static final String emoji_90 = "[emoji_90]";
	public static final String emoji_91 = "[emoji_91]";
	public static final String emoji_92 = "[emoji_92]";
	public static final String emoji_93 = "[emoji_93]";
	public static final String emoji_94 = "[emoji_94]";
	public static final String emoji_95 = "[emoji_95]";
	public static final String emoji_96 = "[emoji_96]";
	public static final String emoji_97 = "[emoji_97]";
	public static final String emoji_98 = "[emoji_98]";
	public static final String emoji_99 = "[emoji_99]";

	public static final String emoji_100 = "[emoji_100]";
	public static final String emoji_101 = "[emoji_101]";
	public static final String emoji_102 = "[emoji_102]";
	public static final String emoji_103 = "[emoji_103]";
	public static final String emoji_104 = "[emoji_104]";
	public static final String emoji_105 = "[emoji_105]";
	public static final String emoji_106 = "[emoji_106]";
	public static final String emoji_107 = "[emoji_107]";
	public static final String emoji_108 = "[emoji_108]";
	public static final String emoji_109 = "[emoji_109]";
	public static final String emoji_110 = "[emoji_110]";
	public static final String emoji_111 = "[emoji_111]";
	public static final String emoji_112 = "[emoji_112]";
	public static final String emoji_113 = "[emoji_113]";

	public static final String emoji_114 = "[emoji_114]";
	public static final String emoji_115 = "[emoji_115]";
	public static final String emoji_116 = "[emoji_116]";
	public static final String emoji_117 = "[emoji_117]";
	public static final String emoji_118 = "[emoji_118]";
	public static final String emoji_119 = "[emoji_119]";
	public static final String emoji_120 = "[emoji_120]";
	public static final String emoji_121 = "[emoji_121]";
	public static final String emoji_122 = "[emoji_122]";
	public static final String emoji_123 = "[emoji_123]";
	public static final String emoji_124 = "[emoji_124]";
	public static final String emoji_125 = "[emoji_125]";
	public static final String emoji_126 = "[emoji_126]";
	public static final String emoji_127 = "[emoji_127]";
	public static final String emoji_128 = "[emoji_128]";
	public static final String emoji_129 = "[emoji_129]";
	public static final String emoji_130 = "[emoji_130]";
	public static final String emoji_131 = "[emoji_131]";
	public static final String emoji_132 = "[emoji_132]";
	public static final String emoji_133 = "[emoji_133]";
	public static final String emoji_134 = "[emoji_134]";
	public static final String emoji_135 = "[emoji_135]";
	public static final String emoji_136 = "[emoji_136]";
	public static final String emoji_137 = "[emoji_137]";
	public static final String emoji_138 = "[emoji_138]";
	public static final String emoji_139 = "[emoji_139]";


	public static final String gemoji_001 = "[gemoji_1]";
	public static final String gemoji_002 = "[gemoji_2]";
	public static final String gemoji_003 = "[gemoji_3]";
	public static final String gemoji_004 = "[gemoji_4]";
	public static final String gemoji_005 = "[gemoji_5]";
	public static final String gemoji_006 = "[gemoji_6]";
	public static final String gemoji_007 = "[gemoji_7]";
	public static final String gemoji_008 = "[gemoji_8]";
	public static final String gemoji_009 = "[gemoji_9]";
	public static final String gemoji_010 = "[gemoji_10]";
	public static final String gemoji_11 = "[gemoji_11]";
	public static final String gemoji_12 = "[gemoji_12]";
	public static final String gemoji_13 = "[gemoji_13]";
	public static final String gemoji_14 = "[gemoji_14]";
	public static final String gemoji_15 = "[gemoji_15]";
	public static final String gemoji_16 = "[gemoji_16]";
	public static final String gemoji_17 = "[gemoji_17]";
	public static final String gemoji_18 = "[gemoji_18]";
	public static final String gemoji_19 = "[gemoji_19]";
	public static final String gemoji_20 = "[gemoji_20]";
	public static final String gemoji_21 = "[gemoji_21]";
	public static final String gemoji_22 = "[gemoji_22]";
	public static final String gemoji_23 = "[gemoji_23]";
	public static final String gemoji_24 = "[gemoji_24]";
	public static final String gemoji_25 = "[gemoji_25]";
	public static final String gemoji_26 = "[gemoji_26]";
	public static final String gemoji_27 = "[gemoji_27]";
	public static final String gemoji_28 = "[gemoji_28]";
	public static final String gemoji_29 = "[gemoji_29]";
	public static final String gemoji_30 = "[gemoji_30]";
	public static final String gemoji_31 = "[gemoji_31]";
	public static final String gemoji_32 = "[gemoji_32]";
	public static final String gemoji_33 = "[gemoji_33]";
	public static final String gemoji_34 = "[gemoji_34]";
	public static final String gemoji_35 = "[gemoji_35]";
	public static final String gemoji_36 = "[gemoji_36]";
	public static final String gemoji_37 = "[gemoji_37]";
	public static final String gemoji_38 = "[gemoji_38]";
	public static final String gemoji_39 = "[gemoji_39]";
	public static final String gemoji_40 = "[gemoji_40]";
	public static final String gemoji_41 = "[gemoji_41]";
	public static final String gemoji_42 = "[gemoji_42]";
	public static final String gemoji_43 = "[gemoji_43]";

	public static final String gemoji_45 = "[gemoji_45]";
	public static final String gemoji_46 = "[gemoji_46]";
	public static final String gemoji_47 = "[gemoji_47]";
	public static final String gemoji_48 = "[gemoji_48]";
	public static final String gemoji_49 = "[gemoji_49]";
	public static final String gemoji_50 = "[gemoji_50]";
	public static final String gemoji_51 = "[gemoji_51]";
	public static final String gemoji_52 = "[gemoji_52]";
	public static final String gemoji_53 = "[gemoji_53]";
	public static final String gemoji_54 = "[gemoji_54]";
	public static final String gemoji_55 = "[gemoji_55]";
	public static final String gemoji_56 = "[gemoji_56]";
	public static final String gemoji_57 = "[gemoji_57]";

	public static final String gemoji_58 = "[gemoji_58]";
	public static final String gemoji_59 = "[gemoji_59]";
	public static final String gemoji_60 = "[gemoji_60]";
	public static final String gemoji_61 = "[gemoji_61]";
	public static final String gemoji_62 = "[gemoji_62]";
	public static final String gemoji_63 = "[gemoji_63]";
	public static final String gemoji_64 = "[gemoji_64]";
	public static final String gemoji_65 = "[gemoji_65]";
	public static final String gemoji_66 = "[gemoji_66]";
	public static final String gemoji_67 = "[gemoji_67]";
	public static final String gemoji_68 = "[gemoji_68]";
	public static final String gemoji_69 = "[gemoji_69]";
	public static final String gemoji_70 = "[gemoji_70]";
	public static final String gemoji_71 = "[gemoji_71]";

	public static final String gemoji_72 = "[gemoji_72]";
	public static final String gemoji_73 = "[gemoji_73]";
	public static final String gemoji_74 = "[gemoji_74]";
	public static final String gemoji_75 = "[gemoji_75]";
	public static final String gemoji_76 = "[gemoji_76]";
	public static final String gemoji_77 = "[gemoji_77]";
	public static final String gemoji_78 = "[gemoji_78]";
	public static final String gemoji_79 = "[gemoji_79]";
	public static final String gemoji_80 = "[gemoji_80]";
	public static final String gemoji_81 = "[gemoji_81]";
	public static final String gemoji_82 = "[gemoji_82]";
	public static final String gemoji_83 = "[gemoji_83]";
	public static final String gemoji_84 = "[gemoji_84]";
	public static final String gemoji_85 = "[gemoji_85]";

	public static final String gemoji_86 = "[gemoji_86]";
	public static final String gemoji_87 = "[gemoji_87]";
	public static final String gemoji_88 = "[gemoji_88]";
	public static final String gemoji_89 = "[gemoji_89]";
	public static final String gemoji_90 = "[gemoji_90]";
	public static final String gemoji_91 = "[gemoji_91]";
	public static final String gemoji_92 = "[gemoji_92]";
	public static final String gemoji_93 = "[gemoji_93]";
	public static final String gemoji_94 = "[gemoji_94]";
	public static final String gemoji_95 = "[gemoji_95]";
	public static final String gemoji_96 = "[gemoji_96]";
	public static final String gemoji_97 = "[gemoji_97]";
	public static final String gemoji_98 = "[gemoji_98]";
	public static final String gemoji_99 = "[gemoji_99]";

	public static final String gemoji_100 = "[gemoji_100]";
	public static final String gemoji_101 = "[gemoji_101]";
	private static final Factory spannableFactory = Factory
	        .getInstance();
	
	private static final Map<Pattern, Object> emoticons = new HashMap<Pattern, Object>();
	

	static {
	    Emojicon[] emojicons = EmojiconDatas.getData();
	    for(int i = 0; i < emojicons.length; i++){
	        addPattern(emojicons[i].getEmojiText(), emojicons[i].getIcon());
	    }

	    
	}

	/**
	 * add text and icon to the map
	 * @param emojiText-- text of emoji
	 * @param icon -- resource id or local path
	 */
	public static void addPattern(String emojiText, Object icon){
	    emoticons.put(Pattern.compile(Pattern.quote(emojiText)), icon);
	}
	

	/**
	 * replace existing spannable with smiles
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable) {
	    boolean hasChanges = false;
	    for (Entry<Pattern, Object> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
	                Object value = entry.getValue();
	                if(value instanceof String && !((String) value).startsWith("http")){
	                    File file = new File((String) value);
	                    if(!file.exists() || file.isDirectory()){
	                        return false;
	                    }
	                    ImageSpan imageSpan=new ImageSpan(context, Uri.fromFile(file));
						Drawable drawable=imageSpan.getDrawable();
						drawable.setBounds(0,0,25,25);
						imageSpan=new ImageSpan(drawable);
	                    spannable.setSpan(imageSpan,
	                            matcher.start(), matcher.end(),
	                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                }else{
						ImageSpan imageSpan=new ImageSpan(context, (Integer)value);
						Drawable drawable=imageSpan.getDrawable();
						int size=context.getResources().getDimensionPixelSize(R.dimen.emoji_size);
						drawable.setBounds(0,0,size ,size);
						imageSpan=new ImageSpan(drawable);
						spannable.setSpan(imageSpan,
								matcher.start(), matcher.end(),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

	                    spannable.setSpan(imageSpan,
	                            matcher.start(), matcher.end(),
	                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                }
	            }
	        }
	    }
	    
	    return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
	    Spannable spannable = spannableFactory.newSpannable(text);
	    addSmiles(context, spannable);
	    return spannable;
	}
	
	public static boolean containsKey(String key){
		boolean b = false;
		for (Entry<Pattern, Object> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(key);
	        if (matcher.find()) {
	        	b = true;
	        	break;
	        }
		}
		
		return b;
	}
	
	public static int getSmilesSize(){
        return emoticons.size();
    }
    
	
}
