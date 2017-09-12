package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response;

import java.util.List;

/**
 * Created by Mr_ZY on 17/3/3.
 */

public class DevotionSitesResponse {


    /**
     * code : 0
     * msg : ok
     * data : [{"id":1,"name":"Site 1","imageUrl":"http://xxxx/sites/1.jpg","isUpdated":false,"subscribe":230},{"id":2,"name":"Site 2","imageUrl":"http://xxxx/sites/2.jpg","isUpdated":true,"subscribe":221},{"id":3,"name":"Site 3","imageUrl":"http://xxxx/sites/3.jpg","isUpdated":true,"subscribe":130}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * name : Site 1
         * imageUrl : http://xxxx/sites/1.jpg
         * isUpdated : false
         * subscribe : 230
         */

        private int id;
        private String name;
        private String imageUrl;
        private boolean isUpdated;
        private int subscribe;

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

        public boolean getIsUpdated() {
            return isUpdated;
        }

        public void setIsUpdated(boolean isUpdated) {
            this.isUpdated = isUpdated;
        }

        public int getSubscribe() {
            return subscribe;
        }

        public void setSubscribe(int subscribe) {
            this.subscribe = subscribe;
        }
    }
}
