package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response;

import java.util.List;

/**
 * Created by zhangfei on 3/8/17.
 */

public class VerseListResponse {

    /**
     * code : 0
     * msg : success
     * data : {"count":1,"lists":[{"id":1424,"date":"20170301","quote":"","quoteRefer":"","like":57,"share":20}]}
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
         * count : 1
         * lists : [{"id":1424,"date":"20170301","quote":"","quoteRefer":"","like":57,"share":20}]
         */

        private int count;
        private List<VerseBean> lists;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<VerseBean> getLists() {
            return lists;
        }

        public void setLists(List<VerseBean> lists) {
            this.lists = lists;
        }

        public static class VerseBean {
            /**
             * id : 1424
             * date : 20170301
             * quote :
             * quoteRefer :
             * like : 57
             * share : 20
             */

            private int id;
            private String date;
            private String quote;
            private String quoteRefer;
            private String imageUrl;
            private int like;
            private int share;
            private String title;
            private String imageThumbUrl;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getImageThumbUrl() {
                return imageThumbUrl;
            }

            public void setImageThumbUrl(String imageThumbUrl) {
                this.imageThumbUrl = imageThumbUrl;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }


            public String getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getQuote() {
                return quote;
            }

            public void setQuote(String quote) {
                this.quote = quote;
            }

            public String getQuoteRefer() {
                return quoteRefer;
            }

            public void setQuoteRefer(String quoteRefer) {
                this.quoteRefer = quoteRefer;
            }

            public int getLike() {
                return like;
            }

            public void setLike(int like) {
                this.like = like;
            }

            public int getShare() {
                return share;
            }

            public void setShare(int share) {
                this.share = share;
            }
        }
    }
}
