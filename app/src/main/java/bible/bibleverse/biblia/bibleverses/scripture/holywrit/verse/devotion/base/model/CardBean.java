package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model;


import java.io.Serializable;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.LockScreenResponse;

/**
 * Created by zhangfei on 11/8/16.
 */
public class CardBean implements Serializable {

    public int mType;
    public boolean mIsNew;
    public LockScreenResponse.DataBean.ListsBean mDevotionBean;
    public String mAdKey;

    public CardBean(int mType, LockScreenResponse.DataBean.ListsBean mDevotionBean) {
        this.mType = mType;
        this.mDevotionBean = mDevotionBean;
    }

    public CardBean(int mType, LockScreenResponse.DataBean.ListsBean mDevotionBean, boolean mIsNew) {
        this.mType = mType;
        this.mIsNew = mIsNew;
        this.mDevotionBean = mDevotionBean;
    }

}
