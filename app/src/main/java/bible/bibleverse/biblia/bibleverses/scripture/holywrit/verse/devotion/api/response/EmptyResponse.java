package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yzq on 2017/3/1.
 */

public class EmptyResponse {


    /**
     * code : 0
     * msg : ok
     * data : []
     */

    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private List<?> data;

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

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}
