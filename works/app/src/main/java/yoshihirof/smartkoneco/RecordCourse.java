package yoshihirof.smartkoneco;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

/* 成績表履修一覧を表示するプログラム */
public class RecordCourse extends Fragment {
    static RecordCourse newInstance() {return new RecordCourse();}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final Realm mRealm = Realm.getDefaultInstance();
        final RealmResults<RecordCourseDB> allrecord = mRealm.where(RecordCourseDB.class).findAll().sort("year");
        View v = inflater.inflate(R.layout.record_course, null); // レイアウトファイル"record_course.xml"を取得
        int windowwidth = new DisplayMetrics().widthPixels;
        TableLayout ll = (TableLayout) v.findViewById(R.id.coursetable);
        if(allrecord.size() > 0) { // if(true)...データがある場合 / else...データがない場合 
            for(int i=0; i < allrecord.size(); i++){ // データをもとに行を作る
                TableRow row = new TableRow(this.getActivity()); row.setBackgroundResource(R.drawable.cell1);
                TextView txt1 = new TextView(this.getActivity()), txt2 = new TextView(this.getActivity()),
                        txt3 = new TextView(this.getActivity()), txt4 = new TextView(this.getActivity());
                TextView txts[] = {txt1, txt2, txt3, txt4}; int x = 0;
                for(TextView t : txts){
                    t.setHeight((int) getResources().getDimension(R.dimen.unitcellheight));
                    t.setBackgroundResource(R.drawable.cell8);
                    if(x==1){
                        t.setWidth(windowwidth - (int) getResources().getDimension(R.dimen.unitcellwidth)*3); t.setMaxLines(1);
                        t.setGravity(Gravity.CENTER_VERTICAL); t.setPadding(0,0,20,0); t.setEllipsize(TextUtils.TruncateAt.END);
                    } else {
                        t.setWidth((int) getResources().getDimension(R.dimen.unitcellwidth));
                        t.setGravity(Gravity.CENTER);
                    }
                    x++;
                }
                txt1.setText(String.valueOf(allrecord.get(i).year));
                txt2.setText(String.valueOf(allrecord.get(i).course_name));
                txt3.setText(String.valueOf(allrecord.get(i).unit));
                txt4.setText(String.valueOf(allrecord.get(i).value));
                // 左から行に枠を追加
                row.addView(txt1);row.addView(txt2);row.addView(txt3);row.addView(txt4); row.setId(i+1);
                ll.addView(row); // 行を追加
            }
        } else { // 空の15行を生成
            for(int i=0; i < 15; i++) {
                TableRow row = new TableRow(this.getActivity());
                TextView txt1 = new TextView(this.getActivity()), txt2 = new TextView(this.getActivity()),
                        txt3 = new TextView(this.getActivity()), txt4 = new TextView(this.getActivity());
                TextView txts[] = {txt1, txt2, txt3, txt4};
                for (TextView t : txts) {
                    t.setWidth((int) getResources().getDimension(R.dimen.unitcellwidth));
                    t.setHeight((int) getResources().getDimension(R.dimen.unitcellheight));
                    t.setBackgroundResource(R.drawable.cell8); row.addView(t);
                }
                ll.addView(row);
            }
        }
        for(int i=1; i<ll.getChildCount(); i++){ // タップした場合のイベントのセット
            final int fi = i; final TableLayout fll = ll;
            ll.getChildAt(i).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
                for(int x = 1; x < fll.getChildCount(); x++) {
                    if (fll.getChildAt(x).getId() == fi) {
                        fll.getChildAt(x).setBackgroundResource(R.drawable.cell10);
                    } else { fll.getChildAt(x).setBackgroundResource(R.drawable.cell1); }
                }
            }});
        }
        return v;
    }
}
