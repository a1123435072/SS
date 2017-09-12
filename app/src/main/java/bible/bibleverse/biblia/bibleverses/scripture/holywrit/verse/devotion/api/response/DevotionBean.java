package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yzq on 2017/3/18.
 */

public class DevotionBean implements Parcelable {
    /**
     * id : 51793
     * date : 20170316
     * type : 1
     * title : Having It All – March 16, 2017
     * content : Jesus’ story of the rich man and Lazarus in the Gospel of Luke chapter 16 is a great illustration of the truths in Psalm 73.
     * From an earthly standpoint, the rich man had it all. According to estimates,
     * imageUrl : http://img.freereadbible.com/devotion/15/1b334c96c421f5d52dff4f9c3efb0250_720x720.jpg
     * linkUrl : https://wels.net/dev-daily/dd20170316/
     * siteId : 15
     * source : WELS
     * view : 1906
     * isHot : 1
     * shareLink:https://goo.gl/OHkvCz
     */

    public static final int ITEM_LOCK_REMINDER_ID = -2;


    public static DevotionBean createLockScreenGuideItem() {
        DevotionBean lockScreenItem = new DevotionBean();
        lockScreenItem.setId(ITEM_LOCK_REMINDER_ID);
        return lockScreenItem;
    }

    @SerializedName("id")
    private int id;
    @SerializedName("date")
    private String date;
    @SerializedName("type")
    private int type;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("linkUrl")
    private String linkUrl;
    @SerializedName("siteId")
    private int siteId;
    @SerializedName("source")
    private String source;
    @SerializedName("view")
    private int view;
    @SerializedName("isHot")
    private int isHot;
    @SerializedName("shareLink")
    private String shareLink;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getIsHot() {
        return isHot;
    }

    public void setIsHot(int isHot) {
        this.isHot = isHot;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(date);
        dest.writeString(title);
        dest.writeInt(type);
        dest.writeString(content);
        dest.writeString(imageUrl);
        dest.writeString(linkUrl);
        dest.writeString(source);
        dest.writeInt(view);
        dest.writeInt(siteId);
        dest.writeInt(isHot);
        dest.writeString(shareLink);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public DevotionBean createFromParcel(Parcel in) {
            return new DevotionBean(in);
        }

        @Override
        public DevotionBean[] newArray(int size) {
            return new DevotionBean[size];
        }
    };

    public DevotionBean() {
    }

    private DevotionBean(Parcel in) {
        id = in.readInt();
        date = in.readString();
        title = in.readString();
        type = in.readInt();
        content = in.readString();
        imageUrl = in.readString();
        linkUrl = in.readString();
        source = in.readString();
        view = in.readInt();
        siteId = in.readInt();
        isHot = in.readInt();
        shareLink = in.readString();
    }
}

