package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yzq on 2017/3/18.
 */

public class PrayerBean implements Parcelable {
    /**
     * id : 69
     * title : Prayer of Parents for Their Children # 2
     * content :
     * source : catholic.org
     * view : 10
     */

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("cateId")
    private int cateId;
    @SerializedName("source")
    private String source;
    @SerializedName("view")
    private int view;

    public PrayerBean(int id, String title, String content, String source, int view) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.source = source;
        this.view = view;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSource() {
        return source;
    }

    public int getCateId() {
        return cateId;
    }

    public void setCateId(int cateId) {
        this.cateId = cateId;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(source);
        dest.writeInt(view);
        dest.writeInt(cateId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public PrayerBean createFromParcel(Parcel in) {
            return new PrayerBean(in);
        }

        @Override
        public PrayerBean[] newArray(int size) {
            return new PrayerBean[size];
        }
    };

    private PrayerBean(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        source = in.readString();
        view = in.readInt();
        cateId = in.readInt();
    }
}
