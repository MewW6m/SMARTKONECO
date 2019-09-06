package yoshihirof.smartkoneco;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import io.realm.Realm;

/* 時間割を表示するプログラム */
public class TimeTable extends Fragment {
    public static TimeTable newInstance() {
        return new TimeTable();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.time_table, container, false); // レイアウトファイル"time_table.xml"を取得
        final Realm mRealm = Realm.getDefaultInstance();

        /* 一行目の日付のセット */
        Calendar c = GregorianCalendar.getInstance(Locale.JAPAN);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        if(c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
            if(c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){ // Thu, Wed, Thr, Fri, Sat
                while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) { c.add(Calendar.DATE, -1); }
            } else { // Sun
                c.add(Calendar.DATE, 1);
            }
        } else {} // Mon
        DateFormat df = new SimpleDateFormat("M/d(E)", Locale.getDefault());
        final ArrayList<String> daylist = new ArrayList<String>();
        for(int i=0;i<6;i++){ daylist.add(df.format(c.getTime())); c.add(Calendar.DATE, 1); }

        /* 時限をセット */
        Calendar n = GregorianCalendar.getInstance(Locale.JAPAN);
        int nowdate = daylist.indexOf(df.format(n.getTime()));
        int nowtime = n.get(Calendar.HOUR_OF_DAY)*60+n.get(Calendar.MINUTE);
        final TableLayout timetable = (TableLayout) v.findViewById(R.id.timetable);
        String[] zigenlists = {"１限", "２限", "３限", "４限", "５限", "６限", "７限"};
        int timelist[] = {530, 630, 770, 870, 970, 1070, 1170}, nowzigen = -1;
        if(nowtime > 730 && nowtime < 770){} else if(nowtime < 530){} else if(nowtime > 1270){}
        else{ for(int t = 0; t<timelist.length; t++){
            if(timelist[t] < nowtime){ nowzigen = t; }
        }}
        int[][] zigenpattern = {{1,2,3,4,5,6}, {1,2,3,4,5}, {2,3,4,5,6}, {3, 4, 5, 6, 7}, {2,3,4,5,6,7}};
        SharedPreferences predata = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE); // 設定ファイルを開く
        DisplayMetrics displaymetrics = new DisplayMetrics(); getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int windowheight = displaymetrics.heightPixels; int s; int bikouheight; 
        int frameheight = (int) getResources().getDimension(R.dimen.frame);
        if(predata.getBoolean("saturday", true)){ s = 7; } else { s = 6;} // 土曜日を表示するかどうか
        if(predata.getBoolean("bikou", true)){ // 備考を表示するかどうか
            bikouheight = (int) getResources().getDimension(R.dimen.thead) + (int) getResources().getDimension(R.dimen.bikou);
        } else {bikouheight = (int) getResources().getDimension(R.dimen.thead);}

        /* 一行目をレイアウトに挿入 */
        TableRow row = new TableRow(this.getActivity());
        for (int y = 0; y < s; y++) {
            TextView txt = new TextView(this.getActivity());
            if(y == nowdate+1 && !holidaycheck(mRealm, daylist, y)){ txt.setBackgroundResource(R.drawable.cell14);
            } else if(holidaycheck(mRealm, daylist, y)) { txt.setBackgroundResource(R.drawable.cell16);
            } else { txt.setBackgroundResource(R.drawable.cell3);}
            txt.setGravity(Gravity.CENTER);
            if (y == 0) {
                txt.setWidth((int) getResources().getDimension(R.dimen.narrow2));
                txt.setText("\\"); row.addView(txt); continue;
            }
            monthholiday(mRealm, daylist, y, null, txt);
            txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.smalltxt));
            txt.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));
            txt.setText(daylist.get(y - 1)); row.addView(txt);
        }
        timetable.addView(row);

        // 時間割をもとに行を追加していく
        for (int x : zigenpattern[predata.getInt("zigen", 0)]) {
            row = new TableRow(this.getActivity()); TextView t = new TextView(getActivity());
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f));
            t.setText(zigenlists[x-1]); t.setBackgroundResource(R.drawable.cell2);
            t.setGravity(Gravity.CENTER);
            t.setWidth((int) getResources().getDimension(R.dimen.narrow2));
            t.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.smalltxt2));
            t.setHeight((windowheight - frameheight - bikouheight)/zigenpattern[predata.getInt("zigen", 0)].length);
            row.addView(t); // 一列目の枠を追加
            for (int y = 1; y < s; y++) { // 二列目以降の枠を追加
                LinearLayout col = new LinearLayout(this.getActivity());
                col.setOrientation(LinearLayout.VERTICAL); col.setPadding(5,5,5,5);
                col.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));
                if(y == nowdate+1){
                    if(x == nowzigen+1){col.setBackgroundResource(R.drawable.selectcell3);}
                    else { col.setBackgroundResource(R.drawable.selectcell2);}
                } else { col.setBackgroundResource(R.drawable.selectcell); }
                TextView txt1 = new TextView(getActivity()), txt2 = new TextView(getActivity()), txt3 = new TextView(getActivity());
                txt1.setMaxLines(2); txt2.setMaxLines(1); txt3.setMaxLines(1);
                txt1.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.smalltxt));
                txt2.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.smalltxt));
                txt3.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.smalltxt));
                txt1.setEllipsize(TextUtils.TruncateAt.END); txt2.setEllipsize(TextUtils.TruncateAt.END); txt3.setEllipsize(TextUtils.TruncateAt.END);
                String month = daylist.get(y - 1).split("/")[0];
                String day = daylist.get(y - 1).split("/")[1].split("\\(")[0];
                TimeTableDB data = null;
                try{ switch(month){ // 現日付の月をもとに長期休みの場合に赤にする
                    case "2":
                    case "3":
                    case "8":
                        col.setBackgroundResource(R.drawable.cell16); break;
                    case "1":
                        String date1 = mRealm.where(SchoolPlanDB.class).contains("content", "冬季休業最終日").findFirst().date.split("/")[1].split("\\(")[0];
                        String date2 = mRealm.where(SchoolPlanDB.class).contains("content", "後期授業最終日").findFirst().date.split("/")[1].split("\\(")[0];
                        if(Integer.valueOf(date1) >= Integer.valueOf(day) || Integer.valueOf(day) > Integer.valueOf(date2)){
                            col.setBackgroundResource(R.drawable.cell16); break;}
                        data = mRealm.where(TimeTableDB.class).equalTo("time", x).equalTo("youbi", y).findAll().get(1); break;
                    case "4":
                        String day1 = mRealm.where(SchoolPlanDB.class).contains("content", "前期授業開始(").findFirst().date.split("/")[1].split("\\(")[0];
                        if(Integer.valueOf(day) < Integer.valueOf(day1)){
                            col.setBackgroundResource(R.drawable.cell16); break; }
                        data = mRealm.where(TimeTableDB.class).equalTo("time", x).equalTo("youbi", y).findAll().get(0); break;
                    case "9":
                        String day2 = mRealm.where(SchoolPlanDB.class).contains("content", "後期授業開始(").findFirst().date.split("/")[1].split("\\(")[0];
                        if(Integer.valueOf(day) < Integer.valueOf(day2)){
                            col.setBackgroundResource(R.drawable.cell16); break; }
                        data = mRealm.where(TimeTableDB.class).equalTo("time", x).equalTo("youbi", y).findAll().get(1); break;
                    case "7":
                        String day3 = mRealm.where(SchoolPlanDB.class).contains("content", "前期授業最終日").findFirst().date.split("/")[1].split("\\(")[0];
                        if(Integer.valueOf(day3) < Integer.valueOf(day)){
                            col.setBackgroundResource(R.drawable.cell16); break; }
                        data = mRealm.where(TimeTableDB.class).equalTo("time", x).equalTo("youbi", y).findAll().get(0); break;
                    case "12":
                        String day4 = mRealm.where(SchoolPlanDB.class).contains("content", "冬季休業(").findFirst().date.split("/")[1].split("\\(")[0];
                        if(Integer.valueOf(day4) <= Integer.valueOf(day) ){
                            col.setBackgroundResource(R.drawable.cell16); break; }
                        data = mRealm.where(TimeTableDB.class).equalTo("time", x).equalTo("youbi", y).findAll().get(1); break;
                    case "5":
                    case "6":
                        data = mRealm.where(TimeTableDB.class).equalTo("time", x).equalTo("youbi", y).findAll().get(0); break;
                    case "10":
                    case "11":
                        data = mRealm.where(TimeTableDB.class).equalTo("time", x).equalTo("youbi", y).findAll().get(1); break;
                } }catch(Exception e){/*e.printStackTrace();*/}

                // holiday check
                if(holidaycheck(mRealm, daylist, y)) { col.setBackgroundResource(R.drawable.cell16); data=null; }

                if(data!=null) { // 枠をセット
                    final TimeTableDB fdata = data;
                    try {
                        txt1.setText(fdata.course_name);
                        txt2.setText(fdata.person);
                        txt3.setText(fdata.room);
                        boolean kyukolog = false;
                        try{
                            KyukoDB kyuko = mRealm.where(KyukoDB.class).equalTo("date", daylist.get(y - 1)).findAll().where().equalTo("zigen", zigenlists[x - 1]).findAll().where().equalTo("teacher", fdata.person).findFirst();
                            if(kyuko!=null){
                            txt1.setTextColor(Color.RED); txt2.setTextColor(Color.RED); txt3.setTextColor(Color.RED);
                            txt1.setPaintFlags(txt1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            txt2.setPaintFlags(txt1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            txt3.setPaintFlags(txt1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); kyukolog = true;
                        } else {}} catch(Exception ignored){}
                        final int fx = x;
                        final int fy = y;
                        final boolean fkyukolog = kyukolog;
                        if (fdata.course_name != null && !fdata.course_name.isEmpty()) {
                            col.setOnClickListener(new View.OnClickListener() { // 枠をタップしたときにダイアログを表示
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder log = new AlertDialog.Builder(getActivity());
                                    TextView txta = new TextView(getActivity()), txtb = new TextView(getActivity()), txtc = new TextView(getActivity());
                                    TextView txt1 = new TextView(getActivity()), txt2 = new TextView(getActivity()), txt3 = new TextView(getActivity());
                                    txta.setText("授業名："); txtb.setText("教師名："); txtc.setText("教室名：");
                                    txt1.setText(fdata.course_name); txt2.setText(fdata.person); txt3.setText(fdata.room);
                                    txt1.setTextSize(20); txt2.setTextSize(20); txt3.setTextSize(20);
                                    txta.setPadding(0, 30, 0, 10); txt1.setPadding(0, 10, 0, 10); txtb.setPadding(0, 10, 0, 10);
                                    txt2.setPadding(0, 10, 0, 10); txtc.setPadding(0, 10, 0, 10); txt3.setPadding(0, 10, 0, 10);
                                    txt1.setTextIsSelectable(true); txt2.setTextIsSelectable(true); txt3.setTextIsSelectable(true);
                                    LinearLayout layout = new LinearLayout(getActivity()); layout.setPadding(80, 0, 80, 0);
                                    layout.setOrientation(LinearLayout.VERTICAL); layout.setGravity(Gravity.CENTER_HORIZONTAL);
                                    if(fkyukolog){TextView k = new TextView(getActivity()); k.setText("休講");k.setTextColor(Color.RED);layout.addView(k);}
                                    layout.addView(txta); layout.addView(txt1); layout.addView(txtb);
                                    layout.addView(txt2); layout.addView(txtc); layout.addView(txt3);
                                    log.setTitle(daylist.get(fy - 1) + " " + String.valueOf(fx) + "時限目").setView(layout);
                                    if(fdata.course_id  != null && !fdata.course_id.isEmpty()){
                                        log.setPositiveButton("シラバスへ", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) { // シラバスを表示
                                            Intent intent = new Intent(getActivity(), WebViewActivity.class);
                                            intent.putExtra("URI", "https://www.komazawa-u.ac.jp/~kyoumu/syllabus_html/detail/" + fdata.course_id + ".html");
                                            startActivity(intent);
                                        }
                                    });}
                                    log.setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).create().show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        row.addView(col);
                        continue;
                    }
                }
                col.addView(txt1); col.addView(txt2); col.addView(txt3); row.addView(col); // 枠を追加
            }
            timetable.addView(row); // 行を追加
        }

        // 備考の行をセット
        if(predata.getBoolean("bikou", true)){
            row = new TableRow(this.getActivity()); TextView t = new TextView(getActivity());
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f));
            t.setText(R.string.bikou); t.setBackgroundResource(R.drawable.cell2);
            t.setGravity(Gravity.CENTER);
            t.setWidth((int) getResources().getDimension(R.dimen.narrow2));
            t.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.smalltxt2));
            t.setHeight((int) getResources().getDimension(R.dimen.bikou)); t.setTextIsSelectable(true);
            row.addView(t);
            for (int y = 1; y < s; y++) {
                final int fy = y; final TextView ft = new TextView(getActivity()); SchoolPlanDB data = null;
                LinearLayout col = new LinearLayout(this.getActivity());
                col.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));
                if(y == nowdate+1 && !holidaycheck(mRealm, daylist, y)){ col.setBackgroundResource(R.drawable.selectcell2);
                } else if(holidaycheck(mRealm, daylist, y)){col.setBackgroundResource(R.drawable.cell16);
                }else { col.setBackgroundResource(R.drawable.selectcell); }

                monthholiday(mRealm, daylist, y, col, null);

                try{ data = mRealm.where(SchoolPlanDB.class).equalTo("date", daylist.get(fy-1)).findAll().get(0); } catch(Exception e){}
                if(data != null) {
                    ft.setText(data.content);
                    ft.setMaxLines(4);
                    ft.setEllipsize(TextUtils.TruncateAt.END);
                    col.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder log = new AlertDialog.Builder(getActivity());
                            LinearLayout layout = new LinearLayout(getActivity());
                            TextView txtb = new TextView(getActivity());
                            txtb.setText(ft.getText().toString().replace("、", "、\n"));
                            txtb.setWidth(800);
                            txtb.setTextSize(20);
                            txtb.setPadding(0, 20, 0, 10);
                            layout.setPadding(80, 0, 80, 0);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setGravity(Gravity.CENTER_HORIZONTAL);
                            layout.addView(txtb);
                            log.setTitle(daylist.get(fy - 1) + " " + "備考").setView(layout);
                            log.setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create().show();
                        }
                    });
                    col.addView(ft);
                }
                col.setPadding(5,5,5,5); row.addView(col);
            }
            timetable.addView(row);
        }
        return v;
    }
    /* 休講情報をもとに休日か否かを示す */
    private boolean holidaycheck(Realm mRealm, ArrayList<String> daylist,int y){
        boolean sp = false; int sp2 = -1;
        try{ sp = mRealm.where(SchoolPlanDB.class).equalTo("date", daylist.get(y-1)).findAll().get(0).holiday; }catch(Exception e){}
        try{ sp2 = mRealm.where(SchoolPlanDB.class).equalTo("date", daylist.get(y-1)).findAll().get(0).content.indexOf("オータムフェスティバル"); }catch(Exception e){}
        if(sp2!=-1){sp = true;}
        return sp;
    }
    /* 長期休みであれば背景を変える */
    private void monthholiday(Realm mRealm, ArrayList<String> daylist, int y, LinearLayout col, TextView txt){
        String month = daylist.get(y-1).split("/")[0];
        String day = daylist.get(y-1).split("/")[1].split("\\(")[0];
        try{
        switch(month){
            case "2":
            case "3":
            case "8":
                if(txt==null) { col.setBackgroundResource(R.drawable.cell16); break;
                } else if(col==null) { txt.setBackgroundResource(R.drawable.cell16); break;}
            case "1":
                String date1 = mRealm.where(SchoolPlanDB.class).contains("content", "冬季休業最終日").findFirst().date.split("/")[1].split("\\(")[0];
                String date2 = mRealm.where(SchoolPlanDB.class).contains("content", "後期授業最終日").findFirst().date.split("/")[1].split("\\(")[0];
                if(Integer.valueOf(date1) >= Integer.valueOf(day) || Integer.valueOf(day) > Integer.valueOf(date2)){
                    if(txt==null) { col.setBackgroundResource(R.drawable.cell16); break;
                    } else if(col==null) { txt.setBackgroundResource(R.drawable.cell16); break;}}
            case "4":
                String day1 = mRealm.where(SchoolPlanDB.class).contains("content", "前期授業開始(").findFirst().date.split("/")[1].split("\\(")[0];
                if(Integer.valueOf(day) < Integer.valueOf(day1)){
                    if(txt==null) { col.setBackgroundResource(R.drawable.cell16); break;
                    } else if(col==null) { txt.setBackgroundResource(R.drawable.cell16); break;}}
            case "9":
                String day2 = mRealm.where(SchoolPlanDB.class).contains("content", "後期授業開始(").findFirst().date.split("/")[1].split("\\(")[0];
                if(Integer.valueOf(day) < Integer.valueOf(day2)){
                    if(txt==null) { col.setBackgroundResource(R.drawable.cell16); break;
                    } else if(col==null) { txt.setBackgroundResource(R.drawable.cell16); break;}}
            case "7":
                String day3 = mRealm.where(SchoolPlanDB.class).contains("content", "前期授業最終日").findFirst().date.split("/")[1].split("\\(")[0];
                if(Integer.valueOf(day3) < Integer.valueOf(day)){
                    if(txt==null) { col.setBackgroundResource(R.drawable.cell16); break;
                    } else if(col==null) { txt.setBackgroundResource(R.drawable.cell16); break;} }
            case "12":
                String day4 = mRealm.where(SchoolPlanDB.class).contains("content", "冬季休業(").findFirst().date.split("/")[1].split("\\(")[0];
                if(Integer.valueOf(day4) <= Integer.valueOf(day) ){
                    if(txt==null) { col.setBackgroundResource(R.drawable.cell16); break;
                    } else if(col==null) { txt.setBackgroundResource(R.drawable.cell16); break;}}
        }} catch(Exception e){e.printStackTrace();}
    }
}