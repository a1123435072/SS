package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model;

/**
 * Created by zhangfei on 3/7/17.
 */

public class ReminderBean {
    public int type;
    public String title;
    public String content;
    public String time;
    public String timeValue;
    public String catName;
    public String catId;

    public ReminderBean() {
    }

    public ReminderBean(int type, String catName, String time, String timeValue) {
        this.type = type;
        this.time = time;
        this.timeValue = timeValue;
        this.catName = catName;
    }

    public ReminderBean(int type, String title, String content, String time, String timeValue) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.time = time;
        this.timeValue = timeValue;
    }
}
