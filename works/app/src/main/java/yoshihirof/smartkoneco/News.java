package yoshihirof.smartkoneco;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

import static io.realm.Sort.DESCENDING;

/* 連絡事項を表示するプログラム */
public class News extends Fragment {
    public static News newInstance(){ return new News(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.news, container, false); // レイアウトファイル"news.xml"を取得
        final Realm mRealm = Realm.getDefaultInstance();
        final RealmResults<NewsDB> allrecord3 = mRealm.where(NewsDB.class).findAll().sort("date", DESCENDING);
        LinearLayout l = (LinearLayout)v.findViewById(R.id.newslist);
        int pixels = (int) (getResources().getDimension(R.dimen.listheight));
        for (int i = 0; i < allrecord3.size(); i++) { // 枠を一行ずつを追加していく
            final String t0 = String.valueOf(allrecord3.get(i).id), t1 = String.valueOf(allrecord3.get(i).article_id), t2 = allrecord3.get(i).date,
                    t3 = allrecord3.get(i).kenmei, t4 = String.valueOf(allrecord3.get(i).content).replaceAll("\\s{2,}", "\n");
            LinearLayout r = new LinearLayout(getActivity());
            TextView txt1 = new TextView(getActivity()), txt2 = new TextView(getActivity()), txt3 = new TextView(getActivity());
            txt1.setText(t3); txt2.setText(t4.replace("\n", "")); txt3.setText(t2);
            txt1.setTextSize(18); txt1.setMaxLines(1); txt1.setEllipsize(TextUtils.TruncateAt.END);
            txt2.setTextSize(15); txt2.setMaxLines(2); txt2.setEllipsize(TextUtils.TruncateAt.END);
            txt3.setTextSize(10); txt3.setGravity(Gravity.RIGHT); txt2.setPadding(10,0,10,0);
            txt3.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            r.addView(txt1); r.addView(txt2); r.addView(txt3);
            r.setBackgroundResource(R.drawable.listborder); r.setOrientation(LinearLayout.VERTICAL); r.setPadding(25,10,25,0);
            r.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels));
            r.setOnClickListener(new View.OnClickListener() { // タップした枠の詳細をダイアログ表示
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder log = new AlertDialog.Builder(getActivity());
                    ScrollView sview = new ScrollView(getActivity()); sview.setPadding(50,60,50,0);
                    TextView txt = new TextView(getActivity()); txt.setText(t4.replace("\n\n", "\n"));
                    txt.setTextSize(17); txt.setLineSpacing(75f, 0); txt.setTextIsSelectable(true); sview.addView(txt);
                    log.setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) { }
                    });
                    log.setTitle(t3).setView(sview);
                    Dialog d = log.create(); d.show();

                    WindowManager.LayoutParams lp = d.getWindow().getAttributes();
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    d.getWindow().setAttributes(lp);
                }
            });
            l.addView(r);
        }
        /* 下に引っ張ると更新する */
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout)getActivity().findViewById(R.id.swipelayout);
                mSwipeRefreshLayout.setRefreshing(false);
                GetNewsData newsdata = (GetNewsData) new GetNewsData(getActivity(), v, true).execute();
            }
        });
        return v;
    }
}