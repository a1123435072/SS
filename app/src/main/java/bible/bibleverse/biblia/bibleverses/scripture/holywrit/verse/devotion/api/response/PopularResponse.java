package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yzq on 2017/3/17.
 */

public class PopularResponse {


    /**
     * code : 0
     * msg : success
     * data : {"devotion":[{"id":51793,"date":"20170316","type":1,"title":"Having It All \u2013 March 16, 2017","content":"Jesus\u2019 story of the rich man and Lazarus in the Gospel of Luke chapter 16 is a great illustration of the truths in Psalm 73.\nFrom an earthly standpoint, the rich man had it all. According to estimates,","imageUrl":"http://img.freereadbible.com/devotion/15/1b334c96c421f5d52dff4f9c3efb0250_720x720.jpg","linkUrl":"https://wels.net/dev-daily/dd20170316/","siteId":15,"source":"WELS","view":1906,"isHot":1},{"id":51721,"date":"20170314","type":1,"title":"Open Arms","content":"The day my husband, Dan, and I began our caregiving journey with our aging parents, we linked arms and felt as if we were plunging off a cliff. We didn\u2019t know that in the process of caregiving the hardest","imageUrl":"http://img.freereadbible.com/devotion/11/59bd27cb19dcb49d42d028858fb210c9_720x720.jpg","linkUrl":"https://odb.org/2017/03/14?calendar-redirect=true&post-type=post","siteId":11,"source":"Our Daily Bread","view":1556,"isHot":1},{"id":51589,"date":"20170310","type":1,"title":"Unconditional Love \u2013 March 10, 2017","content":"Little Sara spent the afternoon watching video of her parents\u2019 wedding, marveling at how beautiful Mommy looked in her dress and how young and handsome Daddy was too. Her mother smiled over her shoulder","imageUrl":"http://img.freereadbible.com/devotion/15/f2e066c2181f1d3116639e93906cf086_720x720.jpg","linkUrl":"https://wels.net/dev-daily/dd20170310/","siteId":15,"source":"WELS","view":2562,"isHot":1},{"id":51725,"date":"20170314","type":1,"title":"Picnic Table \u2013 March 14, 2017","content":"In your imagination, take a trip to a place that really exists. The place is a small, modest park covered in green grass. In that park are a few picnic tables. One of the picnic tables is under a shade","imageUrl":"http://img.freereadbible.com/devotion/15/9483be511dfb13f8155f887eed6ce732_720x720.jpg","linkUrl":"https://wels.net/dev-daily/dd20170314/","siteId":15,"source":"WELS","view":1446,"isHot":1}],"prayer":[{"id":353,"title":"Arising from Sleep","content":"O Master and holy God, who are beyond our understanding: at your word, light came forth out of darkness. In your mercy, you gave us rest through night-long sleep, and raised us up to glorify your goodness and to offer our supplication to You. Now, in your own tender love, accept us who adore You and give thanks to You with all our heart. Grant us all our requests, if they lead to salvation; give us the grace of manifesting that we are children of light and day, and heirs to your eternal reward. In the abundance of your mercies, O Lord, remember all your people; all those present who pray with us; all our brethren on land, at sea, or in the air, in every place of Your domain, who call upon your love for mankind. Upon all, pour down your great mercy, that we, saved in body and in soul, may persevere unfailingly; and that, in our confidence, we may extol your exalted and blessed Name, Father, Son, and Holy Spirit, always, now and forever. Amen.","cateId":9,"source":"catholic.org","view":1838},{"id":359,"title":"A Morning Prayer From the Roman Brivary # 2","content":"O Lord God, King of heaven and earth,  may it please Thee this day to order and to hallow,  to rule and to govern our hearts and our bodies,  our thoughts, our words and our works,  according to Thy law and in the doing of Thy commandments,  that we, being helped by Thee,  may here and hereafter worthily be saved and delivered by Thee,  O Saviour of the world,  who livest and reignest forever and ever.<br>  Amen.","cateId":9,"source":"catholic.org","view":2095}]}
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
        @SerializedName("devotion")
        private List<DevotionBean> devotion;
        @SerializedName("prayer")
        private List<PrayerBean> prayer;

        public List<DevotionBean> getDevotion() {
            return devotion;
        }

        public void setDevotion(List<DevotionBean> devotion) {
            this.devotion = devotion;
        }

        public List<PrayerBean> getPrayer() {
            return prayer;
        }

        public void setPrayer(List<PrayerBean> prayer) {
            this.prayer = prayer;
        }
    }
}
