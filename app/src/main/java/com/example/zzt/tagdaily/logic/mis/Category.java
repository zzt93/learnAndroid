package com.example.zzt.tagdaily.logic.mis;

import com.example.zzt.tagdaily.R;

import java.util.HashMap;

/**
 * Created by zzt on 10/1/15.
 * <p/>
 * Usage:
 */
public enum Category {
    MUSIC(R.mipmap.ic_launcher, "music", "desc", "音乐", "描述"),
    VIDEO(R.mipmap.ic_launcher, "video", "desc", "视频", "描述"),
    DOC(R.mipmap.ic_launcher, "document", "desc", "文档", "描述"),
    MIS(R.mipmap.ic_launcher, "miscellaneous", "desc", "其他", "...");

    private int id;
    private String name, desc;
    private String c_name, c_desc;
    private static Category[] categories = Category.values();

    Category(int id, String name, String desc, String c_name, String c_desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.c_desc = c_desc;
        this.c_name = c_name;
    }

    public static HashMap<String, String> makeMap(int i) {
        Category category = categories[i];
        HashMap<String, String> map = new HashMap<>();
        map.put(UIFileInfo.LOGO, "" + category.id);
        map.put(UIFileInfo.NAME, category.name);
//        map.put(FolderFragment.LAST_MODIFIED, category.desc);
        return map;
    }

    public static int numCategories() {
        return categories.length;
    }

    public String getName() {
        return name;
    }
}
