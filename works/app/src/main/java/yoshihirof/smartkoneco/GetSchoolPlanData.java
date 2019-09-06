package yoshihirof.smartkoneco;

import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import io.realm.Realm;
import io.realm.RealmResults;

/* 駒澤大学の行事日程のwebサイトをスクレイピングしDBに格納するプログラム */
public class GetSchoolPlanData extends AsyncTask<Void, Void, Elements> {
    /* 初期化(インスタンス) */
    public static GetSchoolPlanData newInstance() { return new GetSchoolPlanData(); }

    /* アプリとは別にバックグラウンドで実行する */
    protected Elements doInBackground(Void... nothing) {
        String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36 ";
        /* データベースへ格納の準備 */
        final Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<SchoolPlanDB> allrecord = mRealm.where(SchoolPlanDB.class).findAll();
                /* 日にち、休みornot、行事内容の組み合わせのデータをSchoolPlanDBテーブルに格納(RoomSearchDB.java) */
                try {
                    if(allrecord.size()>0){ allrecord.deleteAllFromRealm(); }
                    Connection.Response res1 = Jsoup.connect("http://gms.gdl.jp/~yoshihiro/school_plan.xml").parser(Parser.xmlParser()).execute();
                    for(Element plan : res1.parse().select("plan")){
                        SchoolPlanDB record = mRealm.createObject(SchoolPlanDB.class, allrecord.size());
                        record.sdate(plan.attr("date")); record.scontent(plan.attr("content"));
                        if(plan.attr("holiday").equals("0")){record.sholiday(false);} else {record.sholiday(true);}
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // debug
                /*
                Log.i("Realm-SchoolPlanDB", "-------------------------------------------------------------------------");
                for (int i = 0; i < allrecord.size(); i++) {
                    String t0 = String.valueOf(allrecord.get(i).date), t1 = String.valueOf(allrecord.get(i).content),  t2 = String.valueOf(allrecord.get(i).holiday);
                    Log.i(String.valueOf(allrecord.get(i).id), t0 + "|" + t1 + "|" + t2);
                }*/
            }
        });

        return null;
    }
}
