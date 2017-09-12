package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yzq on 2017/2/28.
 */

public class DailyVerseDetailResponse {


    /**
     * code : 0
     * msg : success
     * data : {"id":1422,"date":"20170228","title":"He Gives Us Life EternalYou Are Not alone","author":"Billy Graham","quote":"These things have I written unto you that believe . . . that ye may know that ye have eternal life . . . ","quoteRefer":"1 John 5:13","content":"Recently I read that it will cost this country a hundred billion dollars to get one man safely to Mars. It cost God the priceless blood of His only Son to get us sinners to heaven. By tasting death for every man, Jesus took over our penalty as He erased our guilt. Now God can forgive. In a moment of thanksgiving, Paul once exclaimed, \u201cHe loved me and gave Himself for me!\u201d Will you repeat these words right now, even as you read? If you do, I believe you will have cause to be thankful too, and that you will experience the love of God in your heart. Try it and see. The Bible teaches that you can be absolutely sure that you are saved.","prayer":"Father, although my finite mind cannot understand all the wonders of the Gospel, I thank You for the assurance of my salvation through Christ.","sourceSite":"billygraham.org","imageUrl":"http://img.freereadbible.com/devotion/1/20170228.jpg"}
     */

    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private Detail detail;

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

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    public static class Detail {
        /**
         * id : 1422
         * date : 20170228
         * title : He Gives Us Life EternalYou Are Not alone
         * author : Billy Graham
         * quote : These things have I written unto you that believe . . . that ye may know that ye have eternal life . . .
         * quoteRefer : 1 John 5:13
         * content : Recently I read that it will cost this country a hundred billion dollars to get one man safely to Mars. It cost God the priceless blood of His only Son to get us sinners to heaven. By tasting death for every man, Jesus took over our penalty as He erased our guilt. Now God can forgive. In a moment of thanksgiving, Paul once exclaimed, “He loved me and gave Himself for me!” Will you repeat these words right now, even as you read? If you do, I believe you will have cause to be thankful too, and that you will experience the love of God in your heart. Try it and see. The Bible teaches that you can be absolutely sure that you are saved.
         * prayer : Father, although my finite mind cannot understand all the wonders of the Gospel, I thank You for the assurance of my salvation through Christ.
         * sourceSite : billygraham.org
         * imageUrl : http://img.freereadbible.com/devotion/1/20170228.jpg
         */

        @SerializedName("id")
        private int id;
        @SerializedName("date")
        private String date;
        @SerializedName("title")
        private String title;
        @SerializedName("author")
        private String author;
        @SerializedName("quote")
        private String quote;
        @SerializedName("quoteRefer")
        private String quoteRefer;
        @SerializedName("content")
        private String content;
        @SerializedName("prayer")
        private String prayer;
        @SerializedName("sourceSite")
        private String sourceSite;
        @SerializedName("imageUrl")
        private String imageUrl;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPrayer() {
            return prayer;
        }

        public void setPrayer(String prayer) {
            this.prayer = prayer;
        }

        public String getSourceSite() {
            return sourceSite;
        }

        public void setSourceSite(String sourceSite) {
            this.sourceSite = sourceSite;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
