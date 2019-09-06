package yoshihirof.smartkoneco;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import io.realm.Realm;
import io.realm.RealmResults;

/* 空き教室のwebサイトをスクレイピングしDBに格納するプログラム */
public class GetAkiData extends AsyncTask<Void, Void, Elements> {
    /* 初期化(インスタンス) */
    public static GetAkiData newInstance() { return new GetAkiData(); }

    /* アプリとは別にバックグラウンドで実行する */
    protected Elements doInBackground(Void... nothing) {
        String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36 ";
        /* データベースへ格納の準備 */
        final Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<RoomSearchDB> allrecord = mRealm.where(RoomSearchDB.class).findAll();
                if(allrecord.size()>0){ allrecord.deleteAllFromRealm(); } // 一度テーブルを初期化
                try {
                    allrecord.deleteAllFromRealm(); // これいらないかも
                    Connection.Response res1 = Jsoup.connect("http://gms.gdl.jp/~yoshihiro/zenki.xml").parser(Parser.xmlParser()).execute();
                    Connection.Response res2 = Jsoup.connect("http://gms.gdl.jp/~yoshihiro/kouki.xml").parser(Parser.xmlParser()).execute();
                    String[] youbilist = {"月", "火", "水", "木", "金", "土"};
                    String[] zikanlist = {"1", "2", "3", "4", "5", "6", "7"};
                    /* 曜日、時限、タームと教室の組み合わせのデータをRoomSearchDBテーブルに格納(RoomSearchDB.java) */
                    for (int i1 = 0; i1 < youbilist.length; i1++) {
                        for (int i2 = 0; i2 < zikanlist.length; i2++) {
                            String[] roomlist1 = res1.parse().select("aki[youbi=" + youbilist[i1] + "][zikan=" + zikanlist[i2] + "]").text().split(",", 0);
                            for (int i3 = 0; i3 < roomlist1.length; i3++) {
                                RoomSearchDB record = mRealm.createObject(RoomSearchDB.class, allrecord.size());
                                record.syoubi(youbilist[i1]); record.szikan(zikanlist[i2]); record.sterm("前期"); record.sroom(roomlist1[i3]);
                            }
                            String[] roomlist2 = res2.parse().select("aki[youbi=" + youbilist[i1] + "][zikan=" + zikanlist[i2] + "]").text().split(",", 0);
                            for (int i3 = 0; i3 < roomlist2.length; i3++) {
                                RoomSearchDB record = mRealm.createObject(RoomSearchDB.class, allrecord.size());
                                record.syoubi(youbilist[i1]); record.szikan(zikanlist[i2]); record.sterm("後期"); record.sroom(roomlist2[i3]);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*
                // debug
                Log.i("Realm-RoomSearchDB", "-------------------------------------------------------------------------");
                for (int i = 0; i < allrecord.size(); i++) {
                    String t0 = String.valueOf(allrecord.get(i).youbi), t1 = String.valueOf(allrecord.get(i).zikan),
                            t2 = String.valueOf(allrecord.get(i).term), t3 = allrecord.get(i).room;
                    Log.i(String.valueOf(allrecord.get(i).id), t0 + "|" + t1 + " | " + t2 + " | " + t3);
                }
                */
            }
        });
        return null;
    }
}
