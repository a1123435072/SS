package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.fw.basemodules.utils.AndroidUtils;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.BuildConfig;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;


public class AboutUsActivity extends BaseActivity {

    private Toolbar topToolBar;
    private TextView version;
    private TextView feedback;
    private TextView termsAndPrivacy;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        findViewById(R.id.about_view).setBackgroundColor(getResources().getColor(R.color.white));
        initToolBar();
        termsAndPrivacy = (TextView) this.findViewById(R.id.id_terms_privacy);
        version = (TextView) this.findViewById(R.id.version);
        version.setText(getString(R.string.sn_lyric_version_version, BuildConfig.VERSION_NAME));
        feedback = (TextView) this.findViewById(R.id.feedback);
        feedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.sendEmailToOffical(AboutUsActivity.this,
                        AboutUsActivity.this.getString(R.string.feedback_email_subject, AndroidUtils.getAppName(AboutUsActivity.this)) + "(" + Build.BRAND + " " + Build.MODEL + "_"
                                + Build.VERSION.RELEASE + "_" + Utility.getAppVersionName(AboutUsActivity.this)
                                + ")");
            }
        });

        setTermsPrivacyView(termsAndPrivacy);
    }

    private void setTermsPrivacyView(TextView tv) {
        try {
            String terms = getString(R.string.terms);
            String privacy = getString(R.string.privacy);
            String text = getString(R.string.terms_and_privacy, terms, privacy);
            int index1 = text.indexOf(terms);
            int index2 = text.indexOf(privacy);
            SpannableString ss = new SpannableString(text);
            ClickableSpan termsClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TERMS_OF_USER_URL));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(true);
                    ds.setColor(getResources().getColor(R.color.black_999999));
                }
            };
            ClickableSpan privacyClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TERMS_OF_USER_URL));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(true);
                    ds.setColor(getResources().getColor(R.color.black_999999));
                }
            };
            ss.setSpan(termsClickableSpan, index1, index1 + terms.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(privacyClickableSpan, index2, index2 + privacy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv.setText(ss);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setHighlightColor(Color.TRANSPARENT);
        } catch (Exception e) {
        }
    }

    public void initToolBar() {
        topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setNavigationIcon(R.drawable.ic_back_black);
        setTitle(getString(R.string.menuTentang));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        return true;
    }

    public static Intent createIntent() {
        return new Intent(App.context, AboutUsActivity.class);
    }
}
