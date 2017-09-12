package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote;

import android.content.Context;


import java.util.ArrayList;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.AESencrp;

/**
 * Created by zhangfei on 11/18/16.
 */

public class SkuItemHelp {
    //normal product
    public final static String SKU_199 = "devote_199";
    public final static String SKU_999 = "devote_999";
    public final static String SKU_1999 = "devote_1999";
    public final static String SKU_2999 = "devote_2999";

    public final static String PBK = "vUS7t27YNODAF3FOS4V1DK0jRAAZzKXf5A0QU7iLJ6O/gqVHk4KIW0yN2BtwfTWDLRF0VT+lYk653oKaK73US2FIhoaYprt8abYuV5uK2CqH8qmwWWrDBEOtNt5HhbPYvB5pQ6Tfb2BQnhYJICDwCYfxfvpAetXkoqvHsDOXyaWmz3hFo+E4pxXV7QjpHHq8mVhai7LkNUooAdzjL1qviGkHgTpQdPSJ1oGmC5/j/2fUKwkJM9X8KNa6GuQvsfluAItiP4WXSOtvquF2dgZ8LoQ1LNs6i3wSOtlNkxohRe+5N/M208PHIoQePcDSZqR4RptHLzZiGz3qSUsObCjxi4czhF/Apw1f1l3e3nIFd9EvqdZXs45WskCEawWGgBZQ2fvZ32NjmXmwGBHEBdr6ecOkJKNacDOand9aoe5qon5eEN4TspbyRpgm6lyv4iQIgqEtaMiLeJ1FJcGUjD53qdOC1T9WzWr3jbv0DxUpEKrh3heYmPNzLZjhFSeGD6F7DEMEo70rMDLdHdCuBrBCnQ==";

    //subscibe
//    public static final String SKU_99_MONTHLY = "ad_remove_99";
    public static final String SKU_199_MONTHLY = "ad_remove_monthly_199";
    public static final String SKU_799_SIX_MONTHLY = "ad_remove_half_year_799";
    public static final String SKU_1199_YEARLY = "ad_remove_one_year_1199";


    public static String getPublicKey() {
        return AESencrp.decrypt(PBK);
    }

    public static List<String> getSkuItemList() {
        List<String> moreItemSkus = new ArrayList<>();
        moreItemSkus.add(SKU_199);
        moreItemSkus.add(SKU_999);
        moreItemSkus.add(SKU_1999);
        moreItemSkus.add(SKU_2999);
        return moreItemSkus;
    }

    public static List<String> getSubsSkuList() {
        List<String> moreSubsSkus = new ArrayList<>();
//        moreSubsSkus.add(SKU_99_MONTHLY);
        moreSubsSkus.add(SKU_199_MONTHLY);
        moreSubsSkus.add(SKU_799_SIX_MONTHLY);
        moreSubsSkus.add(SKU_1199_YEARLY);
        return moreSubsSkus;
    }

    public static boolean isSubscribeSuccess(Context context, Inventory inventory) {
        boolean isSubcirbionPurchased = false;
//        Purchase skuTestMonthly = inventory.getPurchase(SKU_99_MONTHLY);
        Purchase skuMonthly = inventory.getPurchase(SKU_199_MONTHLY);
        Purchase skuHalfYear = inventory.getPurchase(SKU_799_SIX_MONTHLY);
        Purchase skuYearly = inventory.getPurchase(SKU_1199_YEARLY);
        if (skuMonthly != null) {
            isSubcirbionPurchased = true;
//            subcribeType = getString(R.string.one_month_short_name);
        } else if (skuHalfYear != null) {
            isSubcirbionPurchased = true;
//            subcribeType = getString(R.string.six_months_short_name);
        } else if (skuYearly != null) {
            isSubcirbionPurchased = true;
//            subcribeType = getString(R.string.one_year_short_name);
        } /*else if (skuTestMonthly != null && skuTestMonthly.isAutoRenewing()) {
            isSubcirbionPurchased = true;
//            subcribeType = "TEST";
        }*/
        return isSubcirbionPurchased;
    }

}
