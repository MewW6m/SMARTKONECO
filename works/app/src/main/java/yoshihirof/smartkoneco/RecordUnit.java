package yoshihirof.smartkoneco;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

/* 成績表単位表を表示するプログラム */
public class RecordUnit extends Fragment {
    static RecordUnit newInstance() {
        return new RecordUnit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.record_unit, null); // レイアウトファイル"record_unit.xml"を取得
        final Realm mRealm = Realm.getDefaultInstance();
        final RealmResults<RecordUnitDB> allrecord = mRealm.where(RecordUnitDB.class).findAll(); // 成績表単位データを取得
        TableLayout ll = (TableLayout) v.findViewById(R.id.unittable);
        if(allrecord.size() > 0) { // if(true)...データがある場合 / else...データがない場合 
            for(int i=0; i < allrecord.size(); i++) { // データをもとにテーブルレイアウトに行を代入していく(一行目はすでに挿入済)
                TableRow row = new TableRow(this.getActivity());
                TextView txt1 = new TextView(this.getActivity()), txt2 = new TextView(this.getActivity()), txt3 = new TextView(this.getActivity()),
                        txt4 = new TextView(this.getActivity()), txt5 = new TextView(this.getActivity()), txt6 = new TextView(this.getActivity());
                txt1.setText(String.valueOf(allrecord.get(i).subject)); // 一番左の枠 
                txt1.setBackgroundResource(R.drawable.cell5); txt1.setGravity(Gravity.CENTER);
                txt1.setHeight((int) getResources().getDimension(R.dimen.unitcellheight));
                row.addView(txt1);
                TextView txts[] = {txt2, txt3, txt4, txt5, txt6}; int y= 0;
                txt2.setText(String.valueOf(allrecord.get(i).need)); // 左から2番目の枠
                txt3.setText(String.valueOf(allrecord.get(i).getted)); // 左から3番目の枠
                txt4.setText(String.valueOf(allrecord.get(i).shortage)); // 左から4番目の枠
                txt5.setText(String.valueOf(allrecord.get(i).getted_future)); // 左から5番目の枠
                txt6.setText(String.valueOf(allrecord.get(i).shortage_future)); // 左から6番目の枠
                for (TextView t : txts) { // 枠一つ一つにデザインの適用、タップした時のイベントをセット
                    t.setBackgroundResource(R.drawable.cell1);
                    t.setGravity(Gravity.CENTER);
                    t.setHeight((int) getResources().getDimension(R.dimen.unitcellheight));
                    t.setId(((i+1)*10)+(y+1)); // row * colplace(1~5)
                    final TableLayout fll = ll; final TextView ft = t;
                    t.setOnClickListener(new View.OnClickListener() { @Override  public void onClick(View v) {
                        for(int x=0; x<fll.getChildCount(); x++){
                            TableRow frow = (TableRow) fll.getChildAt(x);
                            for(int y=0;y<6; y++) {
                                TextView col = (TextView) frow.getChildAt(y);
                                if(x == 0){
                                    if (y == ft.getId() % 10) {
                                        col.setBackgroundResource(R.drawable.cell11);
                                    } else {
                                        col.setBackgroundResource(R.drawable.cell4);
                                    }
                                } else {
                                    if (y == 0) {
                                        if (Math.floor(frow.getChildAt(1).getId() / 10) == Math.floor(ft.getId() / 10)) {
                                            col.setBackgroundResource(R.drawable.cell12);
                                        } else {
                                            col.setBackgroundResource(R.drawable.cell5);
                                        }
                                    } else {
                                        if (frow.getChildAt(y).getId() == ft.getId()) {
                                            col.setBackgroundResource(R.drawable.cell10);
                                        } else if (Math.floor(frow.getChildAt(y).getId() / 10) == Math.floor(ft.getId() / 10)) {
                                            col.setBackgroundResource(R.drawable.cell9);
                                        } else if (frow.getChildAt(y).getId() % 10 == ft.getId() % 10) {
                                            col.setBackgroundResource(R.drawable.cell9);
                                        } else {
                                            col.setBackgroundResource(R.drawable.cell1);
                                        }
                                    }
                                }
                            }
                        }
                    }});
                    row.addView(t); // 枠の追加
                    y++;
                }
                ll.addView(row); // 行の追加
            }
        } else {
            for(int i=0; i < 17; i++) { // 空の17行の表を生成
                TableRow row = new TableRow(this.getActivity());
                TextView txt1 = new TextView(this.getActivity()), txt2 = new TextView(this.getActivity()), txt3 = new TextView(this.getActivity()),
                        txt4 = new TextView(this.getActivity()), txt5 = new TextView(this.getActivity()), txt6 = new TextView(this.getActivity());
                TextView txts[] = {txt2, txt3, txt4, txt5, txt6};
                txt1.setBackgroundResource(R.drawable.cell5);
                txt1.setGravity(Gravity.CENTER);
                txt1.setHeight((int) getResources().getDimension(R.dimen.unitcellheight));
                row.addView(txt1);
                for (TextView t : txts) {
                    t.setBackgroundResource(R.drawable.cell1);
                    t.setGravity(Gravity.CENTER);
                    t.setHeight((int) getResources().getDimension(R.dimen.unitcellheight));
                    row.addView(t);
                }
                ll.addView(row);
            }
        }
        return v;
    }
}