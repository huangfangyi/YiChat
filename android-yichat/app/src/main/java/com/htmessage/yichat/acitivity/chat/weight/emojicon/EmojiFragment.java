package com.htmessage.yichat.acitivity.chat.weight.emojicon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import com.htmessage.yichat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2017/7/5.
 * qq 84543217
 */

public class EmojiFragment extends Fragment {
    private ViewPager viewPager;
    private TabLayout tableLayout;
    private List<View> views;
    private List<Emojicon> emojicons;
    private int emojiconColumns = 7;
    private int emojiconRows = 3;
    private OnEmojiListener onEmojiListener;


    public void setOnEmojiListener(OnEmojiListener onEmojiListener) {

        this.onEmojiListener = onEmojiListener;
    }

//    public void init(List<Emojicon> emojicons, int emojiconColumns, int emojiconRows) {
//        this.emojicons = emojicons;
//        this.emojiconColumns = emojiconColumns;
//        this.emojiconRows = emojiconRows;
//
//    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_emoji, container, false);
        viewPager = (ViewPager) root.findViewById(R.id.viewpager_emoji);
        tableLayout = (TabLayout) root.findViewById(R.id.tabLayout_dot);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final List<View> views = getViews();
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public Object instantiateItem(ViewGroup arg0, int arg1) {
                ((ViewPager) arg0).addView(views.get(arg1));
                return views.get(arg1);
            }


            @Override
            public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(views.get(arg1));

            }
        });
        tableLayout.setupWithViewPager(viewPager);
        TabLayout.Tab[] tabs = new TabLayout.Tab[views.size()];

        for (int i = 0; i < views.size(); i++) {
            tabs[i] = tableLayout.getTabAt(i);
            ImageButton imageView = new ImageButton(getContext());
            imageView.setBackground(null);
            imageView.setImageResource(R.drawable.dot_emoji);
            tabs[i].setCustomView(imageView);

        }

    }

    private List<View> getViews() {
        emojicons = getArguments().getParcelableArrayList("emojicons");

        emojiconColumns = getArguments().getInt("emojiconColumns", 7);
        emojiconRows = getArguments().getInt("emojiconRows", 3);
        int itemSize = emojiconColumns * emojiconRows - 1;
        int totalSize = emojicons.size();
        Emojicon.Type emojiType = Emojicon.Type.NORMAL;
        if (totalSize != 0) {
            emojiType = emojicons.get(0).getType();
        }

        if (emojiType == Emojicon.Type.BIG_EXPRESSION) {
            itemSize = emojiconColumns * emojiconRows;
        }
        int pageSize = totalSize % itemSize == 0 ? totalSize / itemSize : totalSize / itemSize + 1;
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < pageSize; i++) {
            View view = View.inflate(getContext(), R.layout.emoji_gridview, null);
            GridView gv = (GridView) view.findViewById(R.id.gridview);
            gv.setNumColumns(emojiconColumns);
            List<Emojicon> list = new ArrayList<Emojicon>();
            if (i != pageSize - 1) {
                list.addAll(emojicons.subList(i * itemSize, (i + 1) * itemSize));
            } else {
                list.addAll(emojicons.subList(i * itemSize, totalSize));
            }
            if (emojiType != Emojicon.Type.BIG_EXPRESSION) {
                Emojicon deleteIcon = new Emojicon();
                deleteIcon.setEmojiText(SmileUtils.DELETE_KEY);
                list.add(deleteIcon);
            }
            final EmojiconGridAdapter gridAdapter = new EmojiconGridAdapter(getContext(), 1, list, emojiType);
            gv.setAdapter(gridAdapter);
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Emojicon emojicon = gridAdapter.getItem(position);
                    if (onEmojiListener != null) {
                        String emojiText = emojicon.getEmojiText();
                        if (emojiText != null && emojiText.equals(SmileUtils.DELETE_KEY)) {
                            onEmojiListener.onDeleteImageClicked();
                        } else {
                            onEmojiListener.onExpressionClicked(emojicon);
                        }

                    }

                }
            });

            views.add(view);
        }
        return views;
    }

    public interface OnEmojiListener {
        void onDeleteImageClicked();

        void onExpressionClicked(Emojicon emojicon);

    }
}
