package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util;

import android.content.Context;
import android.content.Intent;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.PlanDayDetailActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.PlanFinishActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.PlansDetailActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.PlansGridListActivity;

/**
 * Created by zhangfei on 11/7/16.
 */
public class PlanUtil {
    //go to plan detail page.
    public static void gotoPlanDetailPage(Context context, long serverQueryId, int dayCount, String title, String bigImageUrl, String iconUrl) {
        if (context == null || serverQueryId == 0) {
            return;
        }
        context.startActivity(new Intent(context, PlansDetailActivity.class)
                .putExtra(Constants.KEY_SERVER_QUERY_PLAN_ID, serverQueryId)
                .putExtra(Constants.KEY_PLAN_NAME, title)
                .putExtra(Constants.KEY_PLAN_DAY, dayCount)
                .putExtra(Constants.KEY_PLAN_BIG_IMAGE, bigImageUrl)
                .putExtra(Constants.KEY_PLAN_ICON, iconUrl)
        );

    }

    //go to my completed plan list page.
    public static void gotoPlanCompletedListPage(Context context) {
        if (context == null) {
            return;
        }
        context.startActivity(new Intent(context, PlansGridListActivity.class)
                .putExtra(Constants.KEY_PLAN_GRID_LIST_TYPE, Constants.TYPE_COMPLETE_PLANS));

    }

    //go to my plan progress page.
    public static void gotoPlanProgressDetailPage(Context context, long planId) {
        if (context == null) {
            return;
        }
        context.startActivity(PlanDayDetailActivity.createIntent(context, planId));
    }

    //go to my plan finish page.
    public static void gotoPlanFinishPage(Context context, int dayIndex, int allDayCount, boolean isFinishTotalPlan, long planId) {
        if (context == null) {
            return;
        }
        context.startActivity(PlanFinishActivity.createIntent(context, dayIndex + 1, allDayCount, isFinishTotalPlan, planId));
    }

}
