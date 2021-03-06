// Generated by the protocol buffer compiler.  DO NOT EDIT!

package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.alkitab.util.Ari;

public final class BPProto {
    private BPProto() {
    }

    public static final class BP extends
            com.google.protobuf.micro.MessageMicro {
        public BP() {
        }

        public static final class SingleDayPlan extends
                com.google.protobuf.micro.MessageMicro {
            public SingleDayPlan() {
            }

            public static final class Verse extends
                    com.google.protobuf.micro.MessageMicro {
                public Verse() {
                    hasAri = false;
                    ari = 0;
                    verseCount = 0;
                }

                private boolean hasAri;
                private int verseCount;
                private int ari;

                private void generateAri() {
                    int iFrom = Utility.parseVerseId(verseId_, false);
                    int iEnd = Utility.parseVerseId(verseId_, true);
                    if (iFrom != -1 && iEnd != -1) {
                        ari = Ari.encode(bookId_, chapterId_, iFrom);
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

                // optional int32 bookId = 1;
                public static final int BOOKID_FIELD_NUMBER = 1;
                private boolean hasBookId;
                private int bookId_ = 0;

                public int getBookId() {
                    return bookId_;
                }

                public boolean hasBookId() {
                    return hasBookId;
                }

                public Verse setBookId(int value) {
                    hasBookId = true;
                    bookId_ = value;
                    return this;
                }

                public Verse clearBookId() {
                    hasBookId = false;
                    bookId_ = 0;
                    return this;
                }

                // optional int32 chapterId = 2;
                public static final int CHAPTERID_FIELD_NUMBER = 2;
                private boolean hasChapterId;
                private int chapterId_ = 0;

                public int getChapterId() {
                    return chapterId_;
                }

                public boolean hasChapterId() {
                    return hasChapterId;
                }

                public Verse setChapterId(int value) {
                    hasChapterId = true;
                    chapterId_ = value;
                    return this;
                }

                public Verse clearChapterId() {
                    hasChapterId = false;
                    chapterId_ = 0;
                    return this;
                }

                // optional string verseId = 3;
                public static final int VERSEID_FIELD_NUMBER = 3;
                private boolean hasVerseId;
                private String verseId_ = "";

                public String getVerseId() {
                    return verseId_;
                }

                public boolean hasVerseId() {
                    return hasVerseId;
                }

                public Verse setVerseId(String value) {
                    hasVerseId = true;
                    verseId_ = value;
                    return this;
                }

                public Verse clearVerseId() {
                    hasVerseId = false;
                    verseId_ = "";
                    return this;
                }

                public final Verse clear() {
                    clearBookId();
                    clearChapterId();
                    clearVerseId();
                    cachedSize = -1;
                    return this;
                }

                public final boolean isInitialized() {
                    return true;
                }

                public void writeTo(com.google.protobuf.micro.CodedOutputStreamMicro output)
                        throws java.io.IOException {
                    if (hasBookId()) {
                        output.writeInt32(1, getBookId());
                    }
                    if (hasChapterId()) {
                        output.writeInt32(2, getChapterId());
                    }
                    if (hasVerseId()) {
                        output.writeString(3, getVerseId());
                    }
                }

                private int cachedSize = -1;

                public int getCachedSize() {
                    if (cachedSize < 0) {
                        // getSerializedSize sets cachedSize
                        getSerializedSize();
                    }
                    return cachedSize;
                }

                public int getSerializedSize() {
                    int size = 0;
                    if (hasBookId()) {
                        size += com.google.protobuf.micro.CodedOutputStreamMicro
                                .computeInt32Size(1, getBookId());
                    }
                    if (hasChapterId()) {
                        size += com.google.protobuf.micro.CodedOutputStreamMicro
                                .computeInt32Size(2, getChapterId());
                    }
                    if (hasVerseId()) {
                        size += com.google.protobuf.micro.CodedOutputStreamMicro
                                .computeStringSize(3, getVerseId());
                    }
                    cachedSize = size;
                    return size;
                }

                public Verse mergeFrom(
                        com.google.protobuf.micro.CodedInputStreamMicro input)
                        throws java.io.IOException {
                    while (true) {
                        int tag = input.readTag();
                        switch (tag) {
                            case 0:
                                return this;
                            default: {
                                if (!parseUnknownField(input, tag)) {
                                    return this;
                                }
                                break;
                            }
                            case 8: {
                                setBookId(input.readInt32());
                                break;
                            }
                            case 16: {
                                setChapterId(input.readInt32());
                                break;
                            }
                            case 26: {
                                setVerseId(input.readString());
                                break;
                            }
                        }
                    }
                }

                public Verse parseFrom(byte[] data)
                        throws com.google.protobuf.micro.InvalidProtocolBufferMicroException {
                    return (Verse) (new Verse().mergeFrom(data));
                }

                public Verse parseFrom(
                        com.google.protobuf.micro.CodedInputStreamMicro input)
                        throws java.io.IOException {
                    return (Verse) (new Verse().mergeFrom(input));
                }

            }

            // repeated .bean.BP.SingleDayPlan.Verse verse = 1;
            public static final int VERSE_FIELD_NUMBER = 1;
            private java.util.List<Verse> verse_ =
                    java.util.Collections.emptyList();

            public java.util.List<Verse> getVerseList() {
                return verse_;
            }

            public int getVerseCount() {
                return verse_.size();
            }

            public Verse getVerse(int index) {
                return verse_.get(index);
            }

            public SingleDayPlan setVerse(int index, Verse value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                verse_.set(index, value);
                return this;
            }

            public SingleDayPlan addVerse(Verse value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                if (verse_.isEmpty()) {
                    verse_ = new java.util.ArrayList<Verse>();
                }
                verse_.add(value);
                return this;
            }

            public SingleDayPlan clearVerse() {
                verse_ = java.util.Collections.emptyList();
                return this;
            }

            public final SingleDayPlan clear() {
                clearVerse();
                cachedSize = -1;
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public void writeTo(com.google.protobuf.micro.CodedOutputStreamMicro output)
                    throws java.io.IOException {
                for (Verse element : getVerseList()) {
                    output.writeMessage(1, element);
                }
            }

            private int cachedSize = -1;

            public int getCachedSize() {
                if (cachedSize < 0) {
                    // getSerializedSize sets cachedSize
                    getSerializedSize();
                }
                return cachedSize;
            }

            public int getSerializedSize() {
                int size = 0;
                for (Verse element : getVerseList()) {
                    size += com.google.protobuf.micro.CodedOutputStreamMicro
                            .computeMessageSize(1, element);
                }
                cachedSize = size;
                return size;
            }

            public SingleDayPlan mergeFrom(
                    com.google.protobuf.micro.CodedInputStreamMicro input)
                    throws java.io.IOException {
                while (true) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            return this;
                        default: {
                            if (!parseUnknownField(input, tag)) {
                                return this;
                            }
                            break;
                        }
                        case 10: {
                            Verse value = new Verse();
                            input.readMessage(value);
                            addVerse(value);
                            break;
                        }
                    }
                }
            }

            public SingleDayPlan parseFrom(byte[] data)
                    throws com.google.protobuf.micro.InvalidProtocolBufferMicroException {
                return (SingleDayPlan) (new SingleDayPlan().mergeFrom(data));
            }

            public SingleDayPlan parseFrom(
                    com.google.protobuf.micro.CodedInputStreamMicro input)
                    throws java.io.IOException {
                return (SingleDayPlan) (new SingleDayPlan().mergeFrom(input));
            }

        }

        // optional int32 planId = 1;
        public static final int PLANID_FIELD_NUMBER = 1;
        private boolean hasPlanId;
        private int planId_ = 0;

        public int getPlanId() {
            return planId_;
        }

        public boolean hasPlanId() {
            return hasPlanId;
        }

        public BP setPlanId(int value) {
            hasPlanId = true;
            planId_ = value;
            return this;
        }

        public BP clearPlanId() {
            hasPlanId = false;
            planId_ = 0;
            return this;
        }

        // optional string iconUrl = 2;
        public static final int ICONURL_FIELD_NUMBER = 2;
        private boolean hasIconUrl;
        private String iconUrl_ = "";

        public String getIconUrl() {
            return iconUrl_;
        }

        public boolean hasIconUrl() {
            return hasIconUrl;
        }

        public BP setIconUrl(String value) {
            hasIconUrl = true;
            iconUrl_ = value;
            return this;
        }

        public BP clearIconUrl() {
            hasIconUrl = false;
            iconUrl_ = "";
            return this;
        }

        // optional string title = 3;
        public static final int TITLE_FIELD_NUMBER = 3;
        private boolean hasTitle;
        private String title_ = "";

        public String getTitle() {
            return title_;
        }

        public boolean hasTitle() {
            return hasTitle;
        }

        public BP setTitle(String value) {
            hasTitle = true;
            title_ = value;
            return this;
        }

        public BP clearTitle() {
            hasTitle = false;
            title_ = "";
            return this;
        }

        // optional string planDes = 4;
        public static final int PLANDES_FIELD_NUMBER = 4;
        private boolean hasPlanDes;
        private String planDes_ = "";

        public String getPlanDes() {
            return planDes_;
        }

        public boolean hasPlanDes() {
            return hasPlanDes;
        }

        public BP setPlanDes(String value) {
            hasPlanDes = true;
            planDes_ = value;
            return this;
        }

        public BP clearPlanDes() {
            hasPlanDes = false;
            planDes_ = "";
            return this;
        }

        // optional string imageUrl = 5;
        public static final int IMAGEURL_FIELD_NUMBER = 5;
        private boolean hasImageUrl;
        private String imageUrl_ = "";

        public String getImageUrl() {
            return imageUrl_;
        }

        public boolean hasImageUrl() {
            return hasImageUrl;
        }

        public BP setImageUrl(String value) {
            hasImageUrl = true;
            imageUrl_ = value;
            return this;
        }

        public BP clearImageUrl() {
            hasImageUrl = false;
            imageUrl_ = "";
            return this;
        }

        // optional int32 categoryId = 6;
        public static final int CATEGORYID_FIELD_NUMBER = 6;
        private boolean hasCategoryId;
        private int categoryId_ = 0;

        public int getCategoryId() {
            return categoryId_;
        }

        public boolean hasCategoryId() {
            return hasCategoryId;
        }

        public BP setCategoryId(int value) {
            hasCategoryId = true;
            categoryId_ = value;
            return this;
        }

        public BP clearCategoryId() {
            hasCategoryId = false;
            categoryId_ = 0;
            return this;
        }

        // optional int32 daysCount = 7;
        public static final int DAYSCOUNT_FIELD_NUMBER = 7;
        private boolean hasDaysCount;
        private int daysCount_ = 0;

        public int getDaysCount() {
            return daysCount_;
        }

        public boolean hasDaysCount() {
            return hasDaysCount;
        }

        public BP setDaysCount(int value) {
            hasDaysCount = true;
            daysCount_ = value;
            return this;
        }

        public BP clearDaysCount() {
            hasDaysCount = false;
            daysCount_ = 0;
            return this;
        }

        // repeated .bean.BP.SingleDayPlan planDaysList = 8;
        public static final int PLANDAYSLIST_FIELD_NUMBER = 8;
        private java.util.List<SingleDayPlan> planDaysList_ =
                java.util.Collections.emptyList();

        public java.util.List<SingleDayPlan> getPlanDaysListList() {
            return planDaysList_;
        }

        public int getPlanDaysListCount() {
            return planDaysList_.size();
        }

        public SingleDayPlan getPlanDaysList(int index) {
            return planDaysList_.get(index);
        }

        public BP setPlanDaysList(int index, SingleDayPlan value) {
            if (value == null) {
                throw new NullPointerException();
            }
            planDaysList_.set(index, value);
            return this;
        }

        public BP addPlanDaysList(SingleDayPlan value) {
            if (value == null) {
                throw new NullPointerException();
            }
            if (planDaysList_.isEmpty()) {
                planDaysList_ = new java.util.ArrayList<SingleDayPlan>();
            }
            planDaysList_.add(value);
            return this;
        }

        public BP clearPlanDaysList() {
            planDaysList_ = java.util.Collections.emptyList();
            return this;
        }

        public final BP clear() {
            clearPlanId();
            clearIconUrl();
            clearTitle();
            clearPlanDes();
            clearImageUrl();
            clearCategoryId();
            clearDaysCount();
            clearPlanDaysList();
            cachedSize = -1;
            return this;
        }

        public final boolean isInitialized() {
            return true;
        }

        public void writeTo(com.google.protobuf.micro.CodedOutputStreamMicro output)
                throws java.io.IOException {
            if (hasPlanId()) {
                output.writeInt32(1, getPlanId());
            }
            if (hasIconUrl()) {
                output.writeString(2, getIconUrl());
            }
            if (hasTitle()) {
                output.writeString(3, getTitle());
            }
            if (hasPlanDes()) {
                output.writeString(4, getPlanDes());
            }
            if (hasImageUrl()) {
                output.writeString(5, getImageUrl());
            }
            if (hasCategoryId()) {
                output.writeInt32(6, getCategoryId());
            }
            if (hasDaysCount()) {
                output.writeInt32(7, getDaysCount());
            }
            for (SingleDayPlan element : getPlanDaysListList()) {
                output.writeMessage(8, element);
            }
        }

        private int cachedSize = -1;

        public int getCachedSize() {
            if (cachedSize < 0) {
                // getSerializedSize sets cachedSize
                getSerializedSize();
            }
            return cachedSize;
        }

        public int getSerializedSize() {
            int size = 0;
            if (hasPlanId()) {
                size += com.google.protobuf.micro.CodedOutputStreamMicro
                        .computeInt32Size(1, getPlanId());
            }
            if (hasIconUrl()) {
                size += com.google.protobuf.micro.CodedOutputStreamMicro
                        .computeStringSize(2, getIconUrl());
            }
            if (hasTitle()) {
                size += com.google.protobuf.micro.CodedOutputStreamMicro
                        .computeStringSize(3, getTitle());
            }
            if (hasPlanDes()) {
                size += com.google.protobuf.micro.CodedOutputStreamMicro
                        .computeStringSize(4, getPlanDes());
            }
            if (hasImageUrl()) {
                size += com.google.protobuf.micro.CodedOutputStreamMicro
                        .computeStringSize(5, getImageUrl());
            }
            if (hasCategoryId()) {
                size += com.google.protobuf.micro.CodedOutputStreamMicro
                        .computeInt32Size(6, getCategoryId());
            }
            if (hasDaysCount()) {
                size += com.google.protobuf.micro.CodedOutputStreamMicro
                        .computeInt32Size(7, getDaysCount());
            }
            for (SingleDayPlan element : getPlanDaysListList()) {
                size += com.google.protobuf.micro.CodedOutputStreamMicro
                        .computeMessageSize(8, element);
            }
            cachedSize = size;
            return size;
        }

        public BP mergeFrom(
                com.google.protobuf.micro.CodedInputStreamMicro input)
                throws java.io.IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    default: {
                        if (!parseUnknownField(input, tag)) {
                            return this;
                        }
                        break;
                    }
                    case 8: {
                        setPlanId(input.readInt32());
                        break;
                    }
                    case 18: {
                        setIconUrl(input.readString());
                        break;
                    }
                    case 26: {
                        setTitle(input.readString());
                        break;
                    }
                    case 34: {
                        setPlanDes(input.readString());
                        break;
                    }
                    case 42: {
                        setImageUrl(input.readString());
                        break;
                    }
                    case 48: {
                        setCategoryId(input.readInt32());
                        break;
                    }
                    case 56: {
                        setDaysCount(input.readInt32());
                        break;
                    }
                    case 66: {
                        SingleDayPlan value = new SingleDayPlan();
                        input.readMessage(value);
                        addPlanDaysList(value);
                        break;
                    }
                }
            }
        }

        public static BP parseFrom(byte[] data)
                throws com.google.protobuf.micro.InvalidProtocolBufferMicroException {
            return (BP) (new BP().mergeFrom(data));
        }

        public static BP parseFrom(
                com.google.protobuf.micro.CodedInputStreamMicro input)
                throws java.io.IOException {
            return (BP) (new BP().mergeFrom(input));
        }

    }

}
