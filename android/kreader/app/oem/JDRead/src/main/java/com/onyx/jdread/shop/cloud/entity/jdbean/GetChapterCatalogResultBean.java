package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by jackdeng on 2018/3/6.
 */

public class GetChapterCatalogResultBean extends BaseResultBean {

    public DataBean data;

    public static class DataBean {
        public boolean has_volume;
        public List<VolumesBean.ChaptersBean> chapters;
        public List<VolumesBean> volumes;

        public static class VolumesBean {
            public long id;
            public String title;
            public List<ChaptersBean> chapters;

            public static class ChaptersBean {
                public String id;
                public String title;
                public int words;
                public int vip_flag;
                public int price;
                public boolean buy;
            }
        }
    }
}
