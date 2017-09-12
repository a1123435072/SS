
package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.alkitab.util.Ari;

public final class VODLProto {
    private VODLProto() {
    }

    public static final class VODL extends
            com.google.protobuf.micro.MessageMicro {
        public VODL() {
        }

        public static final class VOD extends
                com.google.protobuf.micro.MessageMicro {
            public VOD() {
                hasAri = false;
                verseCount = 0;
                ari = 0;
            }

            // optional string day = 1;
            public static final int DAY_FIELD_NUMBER = 1;
            private boolean hasDay;
            private String day_ = "";

            public String getDay() {
                return day_;
            }

            public boolean hasDay() {
                return hasDay;
            }

            public VOD setDay(String value) {
                hasDay = true;
                day_ = value;
                return this;
            }

            public VOD clearDay() {
                hasDay = false;
                day_ = "";
                return this;
            }

            private boolean hasAri;
            private int verseCount;
            private int ari;

            private void generateAri() {
                int iFrom = Utility.parseVerseId(verseid_, false);
                int iEnd = Utility.parseVerseId(verseid_, true);
                if (iFrom != -1 && iEnd != -1) {
                    ari = Ari.encode(bookid_, chapterid_, iFrom);
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

            // optional int32 bookid = 2;
            public static final int BOOKID_FIELD_NUMBER = 2;
            private boolean hasBookid;
            private int bookid_ = 0;

            public int getBookid() {
                return bookid_;
            }

            public boolean hasBookid() {
                return hasBookid;
            }

            public VOD setBookid(int value) {
                hasBookid = true;
                bookid_ = value;
                return this;
            }

            public VOD clearBookid() {
                hasBookid = false;
                bookid_ = 0;
                return this;
            }

            // optional int32 chapterid = 3;
            public static final int CHAPTERID_FIELD_NUMBER = 3;
            private boolean hasChapterid;
            private int chapterid_ = 0;

            public int getChapterid() {
                return chapterid_;
            }

            public boolean hasChapterid() {
                return hasChapterid;
            }

            public VOD setChapterid(int value) {
                hasChapterid = true;
                chapterid_ = value;
                return this;
            }

            public VOD clearChapterid() {
                hasChapterid = false;
                chapterid_ = 0;
                return this;
            }

            // optional string verseid = 4;
            public static final int VERSEID_FIELD_NUMBER = 4;
            private boolean hasVerseid;
            private String verseid_ = "";

            public String getVerseid() {
                return verseid_;
            }

            public boolean hasVerseid() {
                return hasVerseid;
            }

            public VOD setVerseid(String value) {
                hasVerseid = true;
                verseid_ = value;
                return this;
            }

            public VOD clearVerseid() {
                hasVerseid = false;
                verseid_ = "";
                return this;
            }

            public final VOD clear() {
                clearDay();
                clearBookid();
                clearChapterid();
                clearVerseid();
                cachedSize = -1;
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public void writeTo(com.google.protobuf.micro.CodedOutputStreamMicro output)
                    throws java.io.IOException {
                if (hasDay()) {
                    output.writeString(1, getDay());
                }
                if (hasBookid()) {
                    output.writeInt32(2, getBookid());
                }
                if (hasChapterid()) {
                    output.writeInt32(3, getChapterid());
                }
                if (hasVerseid()) {
                    output.writeString(4, getVerseid());
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
                if (hasDay()) {
                    size += com.google.protobuf.micro.CodedOutputStreamMicro
                            .computeStringSize(1, getDay());
                }
                if (hasBookid()) {
                    size += com.google.protobuf.micro.CodedOutputStreamMicro
                            .computeInt32Size(2, getBookid());
                }
                if (hasChapterid()) {
                    size += com.google.protobuf.micro.CodedOutputStreamMicro
                            .computeInt32Size(3, getChapterid());
                }
                if (hasVerseid()) {
                    size += com.google.protobuf.micro.CodedOutputStreamMicro
                            .computeStringSize(4, getVerseid());
                }
                cachedSize = size;
                return size;
            }

            public VOD mergeFrom(
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
                            setDay(input.readString());
                            break;
                        }
                        case 16: {
                            setBookid(input.readInt32());
                            break;
                        }
                        case 24: {
                            setChapterid(input.readInt32());
                            break;
                        }
                        case 34: {
                            setVerseid(input.readString());
                            break;
                        }
                    }
                }
            }

            public VOD parseFrom(byte[] data)
                    throws com.google.protobuf.micro.InvalidProtocolBufferMicroException {
                return (VOD) (new VOD().mergeFrom(data));
            }

            public VOD parseFrom(
                    com.google.protobuf.micro.CodedInputStreamMicro input)
                    throws java.io.IOException {
                return (VOD) (new VOD().mergeFrom(input));
            }

        }

        // optional int32 count = 1;
        public static final int COUNT_FIELD_NUMBER = 1;
        private boolean hasCount;
        private int count_ = 0;

        public int getCount() {
            return count_;
        }

        public boolean hasCount() {
            return hasCount;
        }

        public VODL setCount(int value) {
            hasCount = true;
            count_ = value;
            return this;
        }

        public VODL clearCount() {
            hasCount = false;
            count_ = 0;
            return this;
        }

        // repeated .bean.VODL.VOD lists = 2;
        public static final int LISTS_FIELD_NUMBER = 2;
        private java.util.List<VOD> lists_ =
                java.util.Collections.emptyList();

        public java.util.List<VOD> getListsList() {
            return lists_;
        }

        public int getListsCount() {
            return lists_.size();
        }

        public VOD getLists(int index) {
            return lists_.get(index);
        }

        public VODL setLists(int index, VOD value) {
            if (value == null) {
                throw new NullPointerException();
            }
            lists_.set(index, value);
            return this;
        }

        public VODL addLists(VOD value) {
            if (value == null) {
                throw new NullPointerException();
            }
            if (lists_.isEmpty()) {
                lists_ = new java.util.ArrayList<VOD>();
            }
            lists_.add(value);
            return this;
        }

        public VODL clearLists() {
            lists_ = java.util.Collections.emptyList();
            return this;
        }

        public final VODL clear() {
            clearCount();
            clearLists();
            cachedSize = -1;
            return this;
        }

        public final boolean isInitialized() {
            return true;
        }

        public void writeTo(com.google.protobuf.micro.CodedOutputStreamMicro output)
                throws java.io.IOException {
            if (hasCount()) {
                output.writeInt32(1, getCount());
            }
            for (VOD element : getListsList()) {
                output.writeMessage(2, element);
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
            if (hasCount()) {
                size += com.google.protobuf.micro.CodedOutputStreamMicro
                        .computeInt32Size(1, getCount());
            }
            for (VOD element : getListsList()) {
                size += com.google.protobuf.micro.CodedOutputStreamMicro
                        .computeMessageSize(2, element);
            }
            cachedSize = size;
            return size;
        }

        public VODL mergeFrom(
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
                        setCount(input.readInt32());
                        break;
                    }
                    case 18: {
                        VOD value = new VOD();
                        input.readMessage(value);
                        addLists(value);
                        break;
                    }
                }
            }
        }

        public static VODL parseFrom(byte[] data)
                throws com.google.protobuf.micro.InvalidProtocolBufferMicroException {
            return (VODL) (new VODL().mergeFrom(data));
        }

        public static VODL parseFrom(
                com.google.protobuf.micro.CodedInputStreamMicro input)
                throws java.io.IOException {
            return (VODL) (new VODL().mergeFrom(input));
        }

    }

}
