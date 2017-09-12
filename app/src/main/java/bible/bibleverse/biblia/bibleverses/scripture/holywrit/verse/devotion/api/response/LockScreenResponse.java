package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response;

import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.alkitab.util.Ari;

/**
 * Created by Mr_ZY on 17/3/10.
 */

public class LockScreenResponse {

    /**
     * code : 0
     * msg : ok
     * data : {"lists":[{"id":1422,"date":"20170303","title":"Title 1","type":1,"content":"This is content1","imageUrl":"http://xxxx/devtion/1.jpg","linkUrl":"http://xxxxx/xxxxx.html","siteId":1,"cateId":1,"source":"catholic.org","view":125,"bookid":13,"chapterid":2,"verseid":"10","like":51,"share":103,"contentType":1}]}
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
        private List<ListsBean> lists;

        public List<ListsBean> getLists() {
            return lists;
        }

        public void setLists(List<ListsBean> lists) {
            this.lists = lists;
        }

        public static class ListsBean {
            /**
             * id : 1422
             * date : 20170303
             * title : Title 1
             * type : 1
             * content : This is content1
             * imageUrl : http://xxxx/devtion/1.jpg
             * linkUrl : http://xxxxx/xxxxx.html
             * siteId : 1
             * cateId : 1
             * source : catholic.org
             * view : 125
             * bookid : 13
             * chapterid : 2
             * verseid : 10
             * like : 51
             * share : 103
             * contentType : 1
             */

            private int id;
            private String date;
            private String title;
            private int type;
            private String content;
            private String imageUrl;
            private String linkUrl;
            private int siteId;
            private int cateId;
            private String source;
            private int view;
            private int isHot;
            private int bookid;
            private int chapterid;
            private String verseid;
            private String shareLink;

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

            private String quote;
            private String quoteRefer;
            private int like;
            private int share;
            private int contentType;

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

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
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

            public int getCateId() {
                return cateId;
            }

            public void setCateId(int cateId) {
                this.cateId = cateId;
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

            public int getBookid() {
                return bookid;
            }

            public void setBookid(int bookid) {
                this.bookid = bookid;
            }

            public int getChapterid() {
                return chapterid;
            }

            public void setChapterid(int chapterid) {
                this.chapterid = chapterid;
            }

            public String getVerseid() {
                return verseid;
            }

            public void setVerseid(String verseid) {
                this.verseid = verseid;
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

            public int getContentType() {
                return contentType;
            }

            public void setContentType(int contentType) {
                this.contentType = contentType;
            }

            public int getIsHot() {
                return isHot;
            }

            public void setIsHot(int isHot) {
                this.isHot = isHot;
            }

            private boolean hasAri;
            private int verseCount;
            private int ari;

            private void generateAri() {
                int iFrom = Utility.parseVerseId(verseid, false);
                int iEnd = Utility.parseVerseId(verseid, true);
                if (iFrom != -1 && iEnd != -1) {
                    ari = Ari.encode(bookid, chapterid, iFrom);
                    verseCount = iEnd - iFrom + 1;
                } else {
                    ari = 0;
                    verseCount = 0;
                }
                hasAri = true;
            }

            public int getAri() {
                if (!hasAri) {
                    generateAri();
                }

                return ari;
            }

            public int getVerseCount() {
                if (!hasAri) {
                    generateAri();
                }
                return verseCount;
            }

            public String getReference() {
                if (!hasAri) {
                    generateAri();
                }
                if (S.activeVersion == null) {
                    Utility.initDeafaultVersion();
                }
                return S.getVerseReference(ari, verseCount);
            }

            public String getShareLink() {
                return shareLink;
            }

            public void setShareLink(String shareLink) {
                this.shareLink = shareLink;
            }

            public DevotionBean convertToDevotionListBean() {
                DevotionBean listsBean = new DevotionBean();
                listsBean.setId(id);
                listsBean.setDate(date);
                listsBean.setTitle(title);
                listsBean.setType(type);
                listsBean.setContent(content);
                listsBean.setImageUrl(imageUrl);
                listsBean.setLinkUrl(linkUrl);
                listsBean.setSource(source);
                listsBean.setView(view);
                listsBean.setSiteId(siteId);
                listsBean.setIsHot(isHot);
                listsBean.setShareLink(shareLink);
                return listsBean;
            }
        }
    }
}
