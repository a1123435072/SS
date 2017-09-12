package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by yzq on 2017/2/27.
 */

public class DevotionListResponse {

    /**
     * code : 0
     * msg : ok
     * data : {"info":{"id":1,"name":"Site 1","imageUrl":"http://xxxx/sites/1.jpg"},"lists":[{"id":1422,"date":"20170303","title":"Title 1","type":1,"content":"This is content1","imageUrl":"http://xxxx/devtion/1.jpg","linkUrl":"http://xxxxx/xxxxx.html","source":"catholic.org","view":125,"isHot": 1,"shareLink":"https://goo.gl/aSerzR"},{"id":1421,"title":"Title 2","date":"20170302","type":1,"content":"This is content2","imageUrl":"http://xxxx/devtion/2.jpg","linkUrl":"http://xxxxx/xxxxx.html","source":"catholic.org","view":89,"isHot": 1,"shareLink":"https://goo.gl/aSerzR"}]}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * info : {"id":1,"name":"Site 1","imageUrl":"http://xxxx/sites/1.jpg"}
         * lists : [{"id":1422,"date":"20170303","title":"Title 1","type":1,"content":"This is content1","imageUrl":"http://xxxx/devtion/1.jpg","linkUrl":"http://xxxxx/xxxxx.html","source":"catholic.org","siteId": 12,"view":125,"isHot": 1,"shareLink":"https://goo.gl/aSerzR"},{"id":1421,"title":"Title 2","date":"20170302","type":1,"content":"This is content2","imageUrl":"http://xxxx/devtion/2.jpg","linkUrl":"http://xxxxx/xxxxx.html","source":"catholic.org","siteId": 12,"view":89,"isHot": 1,"shareLink":"https://goo.gl/aSerzR"}]
         */

        private InfoBean info;
        private List<DevotionBean> lists;

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public List<DevotionBean> getLists() {
            return lists;
        }

        public void setLists(List<DevotionBean> lists) {
            this.lists = lists;
        }

        public static class InfoBean {
            /**
             * id : 1
             * name : Site 1
             * imageUrl : http://xxxx/sites/1.jpg
             */

            private int id;
            private String name;
            private String imageUrl;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }
        }

    }
}
