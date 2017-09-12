package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yzq on 2017/3/1.
 */

public class PrayerResponse {


    /**
     * code : 0
     * msg : success
     * data : {"info":{"id":1,"name":"Children","imageUrl":"http://img.freereadbible.com/category/top/1.jpg"},"lists":[{"id":69,"title":"Prayer of Parents for Their Children # 2","content":"","source":"catholic.org","view":10},{"id":68,"title":"Prayer of Parents for Their Children # 1","content":"","source":"catholic.org","view":10},{"id":67,"title":"Prayer of Parents for Their Children # 1","content":"","source":"catholic.org","view":10},{"id":66,"title":"Prayer of Parents for Their Children # 1","content":"","source":"catholic.org","view":10},{"id":65,"title":"A Parent's Prayer for Their Children","content":"","source":"catholic.org","view":10},{"id":64,"title":"Parents' Prayer for Their Children","content":"","source":"catholic.org","view":10},{"id":63,"title":"A Parent's Prayer for Their Children","content":"","source":"catholic.org","view":10},{"id":61,"title":"Parents' Prayer for Their Children","content":"","source":"catholic.org","view":10},{"id":60,"title":"A Parent's Prayer for Their Children","content":"","source":"catholic.org","view":10},{"id":58,"title":"My Greatest Gift","content":"","source":"catholic.org","view":10}]}
     */

    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        /**
         * info : {"id":1,"name":"Children","imageUrl":"http://img.freereadbible.com/category/top/1.jpg"}
         * lists : [{"id":69,"title":"Prayer of Parents for Their Children # 2","content":"","source":"catholic.org","view":10},{"id":68,"title":"Prayer of Parents for Their Children # 1","content":"","source":"catholic.org","view":10},{"id":67,"title":"Prayer of Parents for Their Children # 1","content":"","source":"catholic.org","view":10},{"id":66,"title":"Prayer of Parents for Their Children # 1","content":"","source":"catholic.org","view":10},{"id":65,"title":"A Parent's Prayer for Their Children","content":"","source":"catholic.org","view":10},{"id":64,"title":"Parents' Prayer for Their Children","content":"","source":"catholic.org","view":10},{"id":63,"title":"A Parent's Prayer for Their Children","content":"","source":"catholic.org","view":10},{"id":61,"title":"Parents' Prayer for Their Children","content":"","source":"catholic.org","view":10},{"id":60,"title":"A Parent's Prayer for Their Children","content":"","source":"catholic.org","view":10},{"id":58,"title":"My Greatest Gift","content":"","source":"catholic.org","view":10}]
         */

        @SerializedName("info")
        private Info info;
        @SerializedName("lists")
        private List<PrayerBean> prays;

        public Info getInfo() {
            return info;
        }

        public void setInfo(Info info) {
            this.info = info;
        }

        public List<PrayerBean> getPrays() {
            return prays;
        }

        public void setPrays(List<PrayerBean> prays) {
            this.prays = prays;
        }

        public static class Info {
            /**
             * id : 1
             * name : Children
             * imageUrl : http://img.freereadbible.com/category/top/1.jpg
             */

            @SerializedName("id")
            private int id;
            @SerializedName("name")
            private String name;
            @SerializedName("imageUrl")
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
