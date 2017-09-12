package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response;

import java.util.List;

/**
 * Created by yzq on 2017/3/1.
 */

public class PrayerCategoryResponse {


    /**
     * code : 0
     * msg : success
     * data : [{"id":16,"name":"Daily-Prayer","imageUrl":"http://img.freereadbible.com/category/main/16.jpg","view":6065},{"id":9,"name":"Morning","imageUrl":"http://img.freereadbible.com/category/main/9.jpg","view":6658},{"id":3,"name":"Evening","imageUrl":"http://img.freereadbible.com/category/main/3.jpg","view":6927},{"id":5,"name":"Family","imageUrl":"http://img.freereadbible.com/category/main/5.jpg","view":5227},{"id":17,"name":"Hope","imageUrl":"http://img.freereadbible.com/category/main/17.jpg","view":3443},{"id":15,"name":"Blessings","imageUrl":"http://img.freereadbible.com/category/main/15.jpg","view":4064},{"id":4,"name":"Faith","imageUrl":"http://img.freereadbible.com/category/main/4.jpg","view":4962},{"id":10,"name":"Peace","imageUrl":"http://img.freereadbible.com/category/main/10.jpg","view":3883},{"id":1,"name":"Children","imageUrl":"http://img.freereadbible.com/category/main/1.jpg","view":4618},{"id":14,"name":"Sick","imageUrl":"http://img.freereadbible.com/category/main/14.jpg","view":3307},{"id":6,"name":"Healing","imageUrl":"http://img.freereadbible.com/category/main/6.jpg","view":3881},{"id":12,"name":"Protection","imageUrl":"http://img.freereadbible.com/category/main/12.jpg","view":4176},{"id":11,"name":"Praise","imageUrl":"http://img.freereadbible.com/category/main/11.jpg","view":3394},{"id":7,"name":"Life","imageUrl":"http://img.freereadbible.com/category/main/7.jpg","view":3528},{"id":2,"name":"Christmas","imageUrl":"http://img.freereadbible.com/category/main/2.jpg","view":4277},{"id":8,"name":"Love","imageUrl":"http://img.freereadbible.com/category/main/8.jpg","view":4832},{"id":13,"name":"Repentance","imageUrl":"http://img.freereadbible.com/category/main/13.jpg","view":3706},{"id":18,"name":"Marriage","imageUrl":"http://img.freereadbible.com/category/main/18.jpg","view":5128}]
     */

    private int code;
    private String msg;
    private List<CategoryBean> data;

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

    public List<CategoryBean> getData() {
        return data;
    }

    public void setData(List<CategoryBean> data) {
        this.data = data;
    }

    public static class CategoryBean {
        /**
         * id : 16
         * name : Daily-Prayer
         * imageUrl : http://img.freereadbible.com/category/main/16.jpg
         * view : 6065
         */

        private int id;
        private String name;
        private String imageUrl;
        private int view;

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

        public int getView() {
            return view;
        }

        public void setView(int view) {
            this.view = view;
        }
    }
}
