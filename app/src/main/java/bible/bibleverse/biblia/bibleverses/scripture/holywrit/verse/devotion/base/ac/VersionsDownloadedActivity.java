package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionDb;
import yuku.afw.V;
import yuku.afw.widget.EasyAdapter;

/**
 * Created by Mr_ZY on 16/11/17.
 */

public class VersionsDownloadedActivity extends BaseActivity {
    private Toolbar mToolbar;
    private ListView mListView;
    private List<MVersionDb> mVersionDbList;
    private DownloadedAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_versions_delete);
        initToolbar();

        initView();
    }

    private void initToolbar() {
        mToolbar = V.get(this, R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.delete_versions));
            mToolbar.setNavigationIcon(R.drawable.ic_back_black);
        }
    }

    private void initView() {
        mListView = V.get(this, R.id.list_view);
        mAdapter = new DownloadedAdapter();
        mListView.setAdapter(mAdapter);
    }

    private class DownloadedAdapter extends EasyAdapter {
        DownloadedAdapter() {
            reload();
        }

        @Override
        public View newView(int position, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.item_version_delete, parent, false);
            return view;
        }

        @Override
        public void bindView(View view, int position, ViewGroup parent) {
            final MVersionDb mVersionDb = (MVersionDb) getItem(position);
            final LinearLayout itemView = V.get(view, R.id.item_view);
            final ImageView delBtn = V.get(view, R.id.version_delete);
            final TextView name = V.get(view, R.id.version_name);
            name.setText(mVersionDb.longName);
            if (mVersionDb.preset_name.equals("en-kjv")) {
                delBtn.setVisibility(View.GONE);
                itemView.setOnClickListener(null);
            } else {
                delBtn.setVisibility(View.VISIBLE);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new MaterialDialog.Builder(VersionsDownloadedActivity.this)
                                .contentColor(getResources().getColor(R.color.black))
                                .content(getString(R.string.delete_version_confirm))
                                .positiveText(R.string.delete)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        S.getDb().deleteVersion(mVersionDb);
                                        new File(mVersionDb.filename).delete();
                                        mAdapter.reload();
                                        App.getLbm().sendBroadcast(new Intent(VersionsActivity.ACTION_RELOAD));
                                    }
                                })
                                .negativeText(R.string.cancel)
                                .show();
                    }
                });
            }
        }

        @Override
        public Object getItem(int position) {
            return mVersionDbList.get(position);
        }

        @Override
        public int getCount() {
            return mVersionDbList.size();
        }

        public void reload() {
            mVersionDbList = S.getDb().listAllVersions();
            notifyDataSetChanged();
        }
    }
}
