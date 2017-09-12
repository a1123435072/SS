package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response;

import java.util.List;

/**
 * Created by zhangfei on 3/9/17.
 */

public class PrayerPeopleResponse {

    /**
     * code : 0
     * msg : success
     * data : {"total":[{"hour":0,"rangeMin":1700,"rangeMax":1900},{"hour":1,"rangeMin":1300,"rangeMax":1500},{"hour":2,"rangeMin":2900,"rangeMax":3100},{"hour":3,"rangeMin":2300,"rangeMax":2500},{"hour":4,"rangeMin":1600,"rangeMax":1800},{"hour":5,"rangeMin":2500,"rangeMax":2700},{"hour":6,"rangeMin":1500,"rangeMax":1700},{"hour":7,"rangeMin":3200,"rangeMax":3400},{"hour":8,"rangeMin":2800,"rangeMax":3000},{"hour":9,"rangeMin":1700,"rangeMax":1900},{"hour":10,"rangeMin":5100,"rangeMax":5300},{"hour":11,"rangeMin":4300,"rangeMax":4500},{"hour":12,"rangeMin":1900,"rangeMax":2100},{"hour":13,"rangeMin":1700,"rangeMax":1900},{"hour":14,"rangeMin":1100,"rangeMax":1300},{"hour":15,"rangeMin":800,"rangeMax":1000},{"hour":16,"rangeMin":1100,"rangeMax":1300},{"hour":17,"rangeMin":700,"rangeMax":900},{"hour":18,"rangeMin":1100,"rangeMax":1300},{"hour":19,"rangeMin":1400,"rangeMax":1600},{"hour":20,"rangeMin":1600,"rangeMax":1800},{"hour":21,"rangeMin":3700,"rangeMax":3900},{"hour":22,"rangeMin":3400,"rangeMax":3600},{"hour":23,"rangeMin":2100,"rangeMax":2300}],"category":[{"id":1,"ratio":0.04},{"id":2,"ratio":0.01},{"id":3,"ratio":0.12},{"id":4,"ratio":0.04},{"id":5,"ratio":0.06},{"id":6,"ratio":0.07},{"id":7,"ratio":0.02},{"id":8,"ratio":0.03},{"id":9,"ratio":0.12},{"id":10,"ratio":0.04},{"id":11,"ratio":0.02},{"id":12,"ratio":0.05},{"id":13,"ratio":0.03},{"id":14,"ratio":0.04},{"id":15,"ratio":0.04},{"id":16,"ratio":0.18},{"id":17,"ratio":0.06},{"id":18,"ratio":0.03}]}
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
        private List<TotalBean> total;
        private List<CategoryRadioBean> category;

        public List<TotalBean> getTotal() {
            return total;
        }

        public void setTotal(List<TotalBean> total) {
            this.total = total;
        }

        public List<CategoryRadioBean> getCategory() {
            return category;
        }

        public void setCategory(List<CategoryRadioBean> category) {
            this.category = category;
        }

        public static class TotalBean {
            /**
             * hour : 0
             * rangeMin : 1700
             * rangeMax : 1900
             */

            private int hour;
            private int rangeMin;
            private int rangeMax;

            public int getHour() {
                return hour;
            }

            public void setHour(int hour) {
                this.hour = hour;
            }

            public int getRangeMin() {
                return rangeMin;
            }

            public void setRangeMin(int rangeMin) {
                this.rangeMin = rangeMin;
            }

            public int getRangeMax() {
                return rangeMax;
            }

            public void setRangeMax(int rangeMax) {
                this.rangeMax = rangeMax;
            }
        }

        public static class CategoryRadioBean {
            /**
             * id : 1
             * ratio : 0.04
             */

            private int id;
            private double ratio;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public double getRatio() {
                return ratio;
            }

            public void setRatio(double ratio) {
                this.ratio = ratio;
            }
        }
    }
}
