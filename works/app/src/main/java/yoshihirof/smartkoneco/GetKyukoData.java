package yoshihirof.smartkoneco;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import io.realm.Realm;
import io.realm.RealmResults;

/* 休講情報のwebサイトをスクレイピングしDBに格納するプログラム */
public class GetKyukoData extends AsyncTask<Void, Void, Elements> {
    /* 初期化(インスタンス) */
    public static GetKyukoData newInstance() { return new GetKyukoData(); }

    /* アプリとは別にバックグラウンドで実行する */
    protected Elements doInBackground(Void... nothing) {
        String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36 ";
        /* データベースへ格納の準備 */
        final Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<KyukoDB> allrecord = mRealm.where(KyukoDB.class).findAll();
                /* 日にち、時限、教師の組み合わせのデータをKyukoDBテーブルに格納(RoomSearchDB.java) */
                try {
                    if(allrecord.size()>0){ allrecord.deleteAllFromRealm(); }
                    Connection.Response res1 = Jsoup.connect("http://gms.gdl.jp/~yoshihiro/kyuko.xml").parser(Parser.xmlParser()).execute();
                    for(Element kyuko : res1.parse().select("kyuko")){
                        KyukoDB record = mRealm.createObject(KyukoDB.class, allrecord.size());
                        record.s_date(kyuko.attr("day")); record.s_zigen(kyuko.attr("zigen")); record.s_teacher(kyuko.attr("teacher"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*
                // debug
                Log.i("Realm-KyukoDB", "-------------------------------------------------------------------------");
                for (int i = 0; i < allrecord.size(); i++) {
                    String t0 = String.valueOf(allrecord.get(i).date), t1 = String.valueOf(allrecord.get(i).zigen),  t2 = String.valueOf(allrecord.get(i).teacher);
                    Log.i(String.valueOf(allrecord.get(i).id), t0 + "|" + t1 + "|" + t2);
                }
                */
            }
        });

        return null;
    }
}
