package com.nec.lib.android.loadmoreview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页数据结构的封装结构
 * @param <T>
 */
public class PageContent<T> implements Serializable {

    public int page = 0;    //zero-base
    public int pageCount = 0;
    public int pageSize = 24;
    public int recordCount = 0;
    public boolean hasMore = false;
    public List<T> datas = new ArrayList<T>();

    public PageContent() {
    }

    public PageContent(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }
}
