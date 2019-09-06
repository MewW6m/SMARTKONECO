package yoshihirof.smartkoneco;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.View;

import yoshihirof.smartkoneco.NewsDB;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/* KONECOの連絡事項をスクレイピングしDBに格納するプログラム */
public class GetNewsData extends AsyncTask<Void, Void, Integer> {

    private Context mContext; private View mView; private boolean mBoolean;
    public ProgressDialog mProgressDialog;
    public GetNewsData (Context context, View v, boolean tf){ mContext = context; mView = v; mBoolean = tf;}

    /* バックグラウンドプログラムを実行する前に */
    @Override
    protected void onPreExecute() {
        if(mBoolean){
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("連絡事項を更新中...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        }
    }

    /* アプリとは別にバックグラウンドで実行する */
    @Override
    protected Integer doInBackground(Void... nothing) {
        SharedPreferences predata = mContext.getSharedPreferences("PreData", Context.MODE_PRIVATE); // 設定ファイルの読み込み
        String username = predata.getString("username", ""), password = predata.getString("password", ""); 
        String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36 ";
        Connection.Response res1 = null, res2 = null, res3 = null;
        Map<String, String> cooky = null; String bbsall = null;
        
        /* 設定ファイルのユーザー名とパスワードをもとにKONECOにログインしスクレイピングをする */
        try { // トップページへ
            res1 = Jsoup.connect("https://koneco.komazawa-u.ac.jp/").userAgent(ua).timeout(10 * 1000).execute();
            Log.i("res1", String.valueOf(res1.parse().select("title")));
        } catch (Exception e) {
            e.printStackTrace(); Log.i("Connection_error", "サーバーが機能していません。"); return 1;
        }
        try { // ログイン後のトップページへ
            res2 = Jsoup.connect("https://koneco.komazawa-u.ac.jp/portal").userAgent(ua).timeout(10 * 1000).cookies(res1.cookies())
                    .data("name", username).data("pass", password).data("form_id", "user_login_block")
                    .data("form_build_id", String.valueOf(res1.parse().select("[name=form_build_id]").attr("value")))
                    .method(Connection.Method.POST).execute();
            Log.i("res2", String.valueOf(res2.parse().select("title")));
        } catch (Exception e) {
            e.printStackTrace(); Log.i("Connection_error", "ログインできません。"); return 1;
        }
        try {
            cooky = res2.cookies(); // ログイン中のクッキー
            /* 連絡事項一覧のURL */
            bbsall = "https://koneco.komazawa-u.ac.jp"+ res2.parse().select(".news_area").get(1).select("div > a").get(0).attr("href");
        } catch (Exception e){ Log.i("String_error", "抽出できません"); return 1;}
        res3 = connect_try(bbsall, cooky); // 連絡事項一覧のページへ
        if(res3 == null){ Log.i("connection_error", "bbs_allに接続できません"); return 1; }
        
        /* データベースへ格納の準備 */
        final Realm mRealm = Realm.getDefaultInstance();
        final RealmResults<NewsDB> allrecord = mRealm.where(NewsDB.class).findAll();
        final Connection.Response Res3 = res3;
        final Map<String, String> Cooky = cooky;
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                allrecord.deleteAllFromRealm();
                Connection.Response res4 = null;
                try{
                    /* 連絡事項一覧の記事ごとにデータベースに格納していく */
                    for (Element con : Res3.parse().select(".news")) {
                        res4 = null;
                        // 連絡事項の記事へ接続
                        res4 = connect_try("https://koneco.komazawa-u.ac.jp" + con.select(".news_txt a").attr("href"), Cooky);
                        if (res4 == null) {
                            Log.i("connection_error", "bbs_allに接続できません"); continue;
                        }
                        /* 記事id、日にち、件名と本文の組み合わせのデータをNewsDBテーブルに格納(NewsDB.java) */
                        NewsDB record = mRealm.createObject(NewsDB.class, allrecord.size());
                        record.s_id(con.select(".news_txt a").attr("href").split("/")[4]);
                        record.s_date(con.select(".date").get(0).text());
                        record.s_kenmei(con.select(".news_txt").get(0).text());
                        record.s_content(res4.parse().select("#detail_editor").get(0).text().replace("<br />", "\n").replaceAll("<(\".*?\"|'.*?'|[^'\"])*?>", ""));
                    }
                } catch (Exception e){
                    e.printStackTrace(); Log.i("String_error", "抽出エラー");
                }
            }
        });
        /* ログアウト */
        try {
            Jsoup.connect("https://koneco.komazawa-u.ac.jp/user/logout?current=portal").userAgent(ua).timeout(10 * 1000).cookies(res2.cookies()).execute();
        } catch (Exception e) { return 1; }

        /*
        // debug
        Log.i("Realm-NewsDB", "-------------------------------------------------------------------------");
        for (int i = 0; i < allrecord.size(); i++) {
            String t0 = String.valueOf(allrecord.get(i).article_id), t1 = String.valueOf(allrecord.get(i).date),
                    t2 = String.valueOf(allrecord.get(i).kenmei), t3 = allrecord.get(i).content;
            Log.i(String.valueOf(allrecord.get(i).id), t0 + "|" + t1 + " | " + t2 + " | " + t3);
        }
        */
        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        switch (result){
            case 0:
                if(mBoolean){ mProgressDialog.dismiss();
                BottomNavigationView navigation = (BottomNavigationView)mView.getRootView().findViewById(R.id.navigation);
                navigation.setSelectedItemId(R.id.news);}
                break;
            case 1:
                if(mBoolean) {
                    mProgressDialog.dismiss(); new AlertDialog.Builder(mContext).setMessage("更新に失敗しました。").create().show();
                }
                break;
        }
    }

    private Connection.Response connect_try(String url ,Map<String, String> cooky){
        String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36 ";
        try {
            return Jsoup.connect(url).userAgent(ua).timeout(10 * 1000).cookies(cooky).execute();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                Log.i("connection_error", String.valueOf(e));
                Jsoup.connect("https://koneco.komazawa-u.ac.jp/user/logout?current=portal").userAgent(ua).timeout(10 * 1000).cookies(cooky).execute();
                return null;
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }
}
