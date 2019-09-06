package yoshihirof.smartkoneco;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

import static yoshihirof.smartkoneco.R.id.t2;

/* 空き教室を表示するプログラム */
public class RoomSearch extends Fragment {
    public static RoomSearch newInstance() {
        return new RoomSearch();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.room_search, container, false); // レイアウトファイル"room_search.xml"を取得
        final String[] zigenlist = new String[]{"1限", "2限", "3限", "4限", "5限", "6限", "7限"};
        String nowterm = "", nowyoubi = "", nowzigen = "";
        ImageView search = (ImageView)v.findViewById(R.id.roomsearchbutton);
        TextView headertxt = (TextView)v.findViewById(R.id.roomheadertext);
        /* 虫眼鏡ボタンにイベントを追加 */
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new AlertFragment();
                dialog.show(getChildFragmentManager(), "test");
            }
        });
        /* 今日の日付、時間をもとに初期表示のターム、曜日、時限を指定 */
        Date now = GregorianCalendar.getInstance(Locale.JAPAN).getTime();
        Calendar d1 = GregorianCalendar.getInstance(Locale.JAPAN);
        d1.set(Calendar.MONTH, 3); d1.set(Calendar.DATE, 1);
        Calendar d2 = GregorianCalendar.getInstance(Locale.JAPAN);
        d2.set(Calendar.MONTH, 8); d2.set(Calendar.DATE, 15);
        nowterm = (now.after(d1.getTime()) && now.before(d2.getTime())) ? "前期" : "後期";
        Calendar n = GregorianCalendar.getInstance(Locale.JAPAN);
        int nowtime = n.get(Calendar.HOUR_OF_DAY)*60+n.get(Calendar.MINUTE);
        nowyoubi = new SimpleDateFormat("E曜", Locale.JAPANESE).format(now);// Hours * 60 + Minutes
        if(nowyoubi.equals("日曜")){ // 日曜の場合は月曜を表示 
            nowyoubi = "月曜"; nowzigen = "1限";
        } else { // 現時刻をもとに時限を決める
            int timelist[] = {530, 630, 770, 870, 970, 1070, 1170}, nz = -1;
            if (nowtime > 730 && nowtime < 770) {  nz = 2; // 昼休み
            } else if (nowtime < 530) { nz = 0; // 1限前
            } else if (nowtime > 1270) { nz = 0; // 7限以降
            } else {
                for (int t = 0; t < timelist.length; t++) {
                    if (timelist[t] < nowtime) { nz = t; }
                }
            }
            nowzigen = zigenlist[nz];
        }
        setclick(v, nowterm, nowyoubi, nowzigen);
        headertxt.setText(nowterm + " " + nowyoubi + " " + nowzigen);
        return v;
    }
    /* 号館をタップしたときに発生するイベントをセット */
    private void setclick(View view, String term, String youbi, String zigen){
        final View fview = view;
        RelativeLayout goukan8 = (RelativeLayout)view.findViewById(R.id.t1); RelativeLayout goukan1 = (RelativeLayout)view.findViewById(t2);
        RelativeLayout goukan3 = (RelativeLayout)view.findViewById(R.id.t3); RelativeLayout goukan9 = (RelativeLayout)view.findViewById(R.id.t4);
        RelativeLayout goukan4 = (RelativeLayout)view.findViewById(R.id.t5); RelativeLayout goukan7 = (RelativeLayout)view.findViewById(R.id.t6);
        ViewSwitcher vs1 = (ViewSwitcher)view.findViewById(R.id.vs1); ViewSwitcher vs3 = (ViewSwitcher)view.findViewById(R.id.vs3);
        ViewSwitcher vs4 = (ViewSwitcher)view.findViewById(R.id.vs4); ViewSwitcher vs7 = (ViewSwitcher)view.findViewById(R.id.vs7);
        ViewSwitcher vs8 = (ViewSwitcher)view.findViewById(R.id.vs8); ViewSwitcher vs9 = (ViewSwitcher)view.findViewById(R.id.vs9);

        ArrayList<String> list8, list1, list3, list9, list4, list7;
        list8 = new ArrayList<String>(); list1 = new ArrayList<String>(); list3 = new ArrayList<String>();
        list9 = new ArrayList<String>(); list4 = new ArrayList<String>(); list7 = new ArrayList<String>();
        RealmResults<RoomSearchDB> allrecord = Realm.getDefaultInstance().where(RoomSearchDB.class).equalTo("youbi", String.valueOf(youbi.charAt(0))).equalTo("zikan", String.valueOf(zigen.charAt(0))).equalTo("term",term).findAll();
        // debug
        for (int i = 0; i < allrecord.size(); i++) {
            String room = allrecord.get(i).room;
            switch(String.valueOf(room.charAt(0))){
                case "１": list1.add(room); break;
                case "３": list3.add(room); break;
                case "４": list4.add(room); break;
                case "７": list7.add(room); break;
                case "８": list8.add(room); break;
                case "９": list9.add(room); break;
            }
        }
        final ArrayList<String> flist1, flist3, flist4, flist7, flist8, flist9;
        flist1=list1; flist3=list3; flist4=list4; flist7=list7; flist8=list8; flist9=list9;
        if(list1.size() > 0){ vs1.setDisplayedChild(1); goukan1.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { makeroomdialog(fview, flist1); }});}
        if(list3.size() > 0){ vs3.setDisplayedChild(1); goukan3.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { makeroomdialog(fview, flist3); }});}
        if(list4.size() > 0){ vs4.setDisplayedChild(1); goukan4.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { makeroomdialog(fview, flist4); }});}
        if(list7.size() > 0){ vs7.setDisplayedChild(1); goukan7.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { makeroomdialog(fview, flist7); }});}
        if(list8.size() > 0){ vs8.setDisplayedChild(1); goukan8.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { makeroomdialog(fview, flist8); }});}
        if(list9.size() > 0){ vs9.setDisplayedChild(1); goukan9.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { makeroomdialog(fview, flist9); }});}
    }
    /* 教室一覧のダイアログを生成する関数 */
    private void makeroomdialog(View view, ArrayList<String> flist){
        AlertDialog.Builder log = new AlertDialog.Builder(view.getContext());
        ScrollView sview = new ScrollView(view.getContext());
        LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL); layout.setGravity(Gravity.CENTER_HORIZONTAL); layout.setPadding(80, 0, 80, 0);
        if((int) flist.get(0).charAt(0) != 3) { // 3号館は構造が不明なため。
            ArrayList<String> list1, list2, list3, list4, list5;
            list1 = new ArrayList<String>(); list2 = new ArrayList<String>(); list3 = new ArrayList<String>();
            list4 = new ArrayList<String>(); list5 = new ArrayList<String>();
            for (int i = 0; i < flist.size(); i++) {
                switch (String.valueOf(flist.get(i).charAt(2))) {
                    case "１": list1.add(flist.get(i)); break;
                    case "２": list2.add(flist.get(i)); break;
                    case "３": list3.add(flist.get(i)); break;
                    case "４": list4.add(flist.get(i)); break;
                    case "５": list5.add(flist.get(i)); break;
                }
            }
            if(list1.size() > 0){ maketxt(view, layout, list1, 1); }
            if(list2.size() > 0){ maketxt(view, layout, list2, 2); }
            if(list3.size() > 0){ maketxt(view, layout, list3, 3); }
            if(list4.size() > 0){ maketxt(view, layout, list4, 4); }
            if(list5.size() > 0){ maketxt(view, layout, list5, 5); }
        }
        sview.addView(layout);
        log.setTitle(String.valueOf(flist.get(0).charAt(0))+"号館の教室").setView(sview);
        log.setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        Dialog d = log.create(); d.show();
    }
    /* 階数をセット */
    private void maketxt(View view, LinearLayout layout, ArrayList<String> addcontent, int kai){
        TextView t1 = new TextView(view.getContext()), t2 = new TextView(view.getContext()); Collections.sort(addcontent);
        t1.setText(String.valueOf(kai) + "階"); t2.setText(addcontent.toString().replace("[","").replace("]",""));
        t1.setTextSize(20); t2.setTextSize(20); t1.setPadding(0, 30, 0, 10); t2.setPadding(0, 10, 0, 10);
        layout.addView(t1); layout.addView(t2);
    }
    /* 検索ダイアログの生成 */
    public static class AlertFragment extends DialogFragment {
        public static String term = "";
        public static String youbi = "";
        public static String zigen = "";
        public static AlertDialog adialog;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final String[] terms = new String[]{"前期", "後期"};
            final String[] youbis = new String[]{"月曜", "火曜", "水曜", "木曜", "金曜", "土曜"};
            final String[] zigens = new String[]{"1限", "2限", "3限", "4限", "5限", "6限", "7限"};
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, terms);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, youbis);
            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, zigens);
            final ListView listView1 = new ListView(getActivity()), listView2 = new ListView(getActivity()), listView3 = new ListView(getActivity());
            listView2.setSelector(R.drawable.listborder); listView3.setSelector(R.drawable.listborder);
            listView1.setAdapter(adapter1); listView2.setAdapter(adapter2); listView3.setAdapter(adapter3);
            // adapter1, listView1は使用していない(元々タームを選択できるようにする予定だったため)
            final AlertDialog dialog = makedialog(listView2, "曜日"); // 一つ目のダイアログを表示
            listView2.setOnItemClickListener(new AdapterView.OnItemClickListener(){ // 一つ目のダイアログの選択肢をタップしたとき
                public void onItemClick(AdapterView<?> items, View view, int position, long id) {
                    youbi = youbis[position];
                    getDialog().dismiss();
                    adialog = makedialog(listView3, "時限"); // 二つ目のダイアログを表示
                    adialog.show();
                }
            });
            listView3.setOnItemClickListener(new AdapterView.OnItemClickListener(){ // 二つ目のダイアログの選択肢をタップしたとき
                public void onItemClick(AdapterView<?> items, View view, int position, long id) {
                    zigen = zigens[position];
                    RoomSearch rs = RoomSearch.newInstance();
                    View v = getParentFragment().getView();
                    TextView tv = (TextView)v.findViewById(R.id.roomheadertext);
                    term = String.valueOf(tv.getText()).substring(0,2);
                    RelativeLayout goukan8 = (RelativeLayout)v.findViewById(R.id.t1); RelativeLayout goukan1 = (RelativeLayout)v.findViewById(t2);
                    RelativeLayout goukan3 = (RelativeLayout)v.findViewById(R.id.t3); RelativeLayout goukan9 = (RelativeLayout)v.findViewById(R.id.t4);
                    RelativeLayout goukan4 = (RelativeLayout)v.findViewById(R.id.t5); RelativeLayout goukan7 = (RelativeLayout)v.findViewById(R.id.t6);
                    ViewSwitcher vs1 = (ViewSwitcher)v.findViewById(R.id.vs1); ViewSwitcher vs3 = (ViewSwitcher)v.findViewById(R.id.vs3);
                    ViewSwitcher vs4 = (ViewSwitcher)v.findViewById(R.id.vs4); ViewSwitcher vs7 = (ViewSwitcher)v.findViewById(R.id.vs7);
                    ViewSwitcher vs8 = (ViewSwitcher)v.findViewById(R.id.vs8); ViewSwitcher vs9 = (ViewSwitcher)v.findViewById(R.id.vs9);
                    vs1.setDisplayedChild(0); vs3.setDisplayedChild(0); vs4.setDisplayedChild(0);
                    vs7.setDisplayedChild(0); vs8.setDisplayedChild(0); vs9.setDisplayedChild(0);
                    goukan1.setOnClickListener(null); goukan3.setOnClickListener(null); goukan4.setOnClickListener(null);
                    goukan7.setOnClickListener(null); goukan8.setOnClickListener(null); goukan9.setOnClickListener(null);
                    tv.setText(term + " " + youbi + " " + zigen);
                    rs.setclick(v, term, youbi, zigen);
                    adialog.dismiss();
                }
            });
            return dialog;
        }
        /* 選択肢の一覧を表示する */
        private AlertDialog makedialog(View v, String message){
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("空き教室検索").setMessage(message).setView(v);
            builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {}});
            return builder.create();
        }
    }
}