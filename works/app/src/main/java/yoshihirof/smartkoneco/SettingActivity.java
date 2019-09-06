package yoshihirof.smartkoneco;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.jakewharton.threetenabp.AndroidThreeTen;
import org.jsoup.Jsoup;
import java.util.ArrayList;
import java.util.Calendar;
import io.realm.Realm;
import io.realm.RealmResults;

/* 設定画面 */
public class SettingActivity extends AppCompatActivity {
    /* 設定画面を生成 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_setting);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SettingHeader()).commit();
    }
    /* どこかをタップすると文字入力パレットを下げる */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try{
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e){}
        return true;
    }

    /* 設定画面の一つ目の画面を生成 */
    public static class SettingHeader extends Fragment implements View.OnClickListener {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.preference_header, container, false);
            TextView preheader = (TextView)getActivity().findViewById(R.id.preheaderchild);
            preheader.setText("");
            ArrayList<RelativeLayout> li = new ArrayList<RelativeLayout>();
            li.add((RelativeLayout) v.findViewById(R.id.presync));
            li.add((RelativeLayout) v.findViewById(R.id.precustomize));
            li.add((RelativeLayout) v.findViewById(R.id.prenortification));
            li.add((RelativeLayout) v.findViewById(R.id.pretimetable));
            li.add((RelativeLayout) v.findViewById(R.id.prerecord));
            li.add((RelativeLayout) v.findViewById(R.id.preerror));
            li.add((RelativeLayout) v.findViewById(R.id.prerule));
            for (int i = 0; i < li.size(); i++) {
                li.get(i).setOnClickListener(this);
            }
            SharedPreferences predata = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE);
            if(predata.getString("username", "").isEmpty()) { // 初回(アクティベートへ)
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingSync()).addToBackStack(null).commit();
            }
            return v;
        }
        @Override
        public void onClick(View v) {
            FragmentTransaction s = getFragmentManager().beginTransaction();
            switch (v.getId()) { // ○○を押したら○○のページへ移動(二つ目の画面へ)
                case R.id.presync: s.replace(R.id.fragment_container, new SettingSync()).addToBackStack(null).commit(); break;
                case R.id.precustomize: s.replace(R.id.fragment_container, new SettingCustomize()).addToBackStack(null).commit(); break;
                case R.id.prenortification: s.replace(R.id.fragment_container, new SettingNortification()).addToBackStack(null).commit(); break;
                case R.id.pretimetable: s.replace(R.id.fragment_container, new SettingTimetable()).addToBackStack(null).commit(); break;
                case R.id.prerecord: s.replace(R.id.fragment_container, new SettingRecord()).addToBackStack(null).commit(); break;
                case R.id.preerror: s.replace(R.id.fragment_container, new SettingError()).addToBackStack(null).commit(); break;
                case R.id.prerule: s.replace(R.id.fragment_container, new SettingRule()).addToBackStack(null).commit(); break;
                default: break;
            }
        }
        /* アクティベート */
        public static class SettingSync extends Fragment {
            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
                final View view = inflater.inflate(R.layout.preference_sync, container, false);
                SharedPreferences predata = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE);
                if(predata.getString("username", "").isEmpty()) { // 初回
                    AlertDialog.Builder log = new AlertDialog.Builder(getActivity());
                    log.setTitle("初回アクティベート").setMessage("このアプリとkonecoの時間割、成績表を同期してください。\n\n注意１：すべての端末上でkonecoからログアウトされているかを必ず確認してください。\n注意２：入力されたユーザー名とパスワードは、アクティベートや連絡事項の同期に関して、konecoにログインする際にのみ使用し、決してネットワークを通じて他の場所には送信しないこと(機密保持)を約束します。");
                    log.setNegativeButton("閉じる", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) {}}).create().show();
                } else { // 2回目以降
                    AlertDialog.Builder log = new AlertDialog.Builder(getActivity());
                    log.setTitle("諸注意").setMessage("注意１：すべての端末上でkonecoからログアウトされているかを必ず確認してください。\n注意２：元の時間割や成績表は全て上書きしてしまいます。\n注意３：入力されたユーザー名とパスワードは、アクティベートや連絡事項の同期に関して、konecoにログインする際にのみ使用し、決してネットワークを通じて他の場所には送信しないこと(機密保持)を約束します。");
                    log.setNegativeButton("閉じる", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) {}}).create().show();
                }
                TextView preheader = (TextView)getActivity().findViewById(R.id.preheaderchild);
                preheader.setText("アクティベート");
                EditText username = (EditText) view.findViewById(R.id.user_name);
                EditText password = (EditText) view.findViewById(R.id.pass_word);
                username.setText(predata.getString("username", ""), TextView.BufferType.EDITABLE);
                password.setText(predata.getString("password", ""), TextView.BufferType.EDITABLE);
                final EditText fusername = username, fpassword = password;
                final Button syncnow = (Button) view.findViewById(R.id.syncnow);
                syncnow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        if(fusername.getText().length()==0 || fpassword.getText().length()==0) {
                            Toast.makeText(getActivity(), "必ず件名と内容を埋めてください。", Toast.LENGTH_LONG).show();
                        } else {
                            final GetKonecoData konecodata = new GetKonecoData(getActivity(), view);
                            konecodata.username = fusername.getText().toString();
                            konecodata.password = fpassword.getText().toString();
                            GetKonecoData tes = (GetKonecoData) konecodata.execute();
                            syncnow.setEnabled(false); syncnow.setTextColor(Color.TRANSPARENT); syncnow.setBackgroundColor(Color.TRANSPARENT);
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    syncnow.setEnabled(true); syncnow.setTextColor(Color.WHITE); syncnow.setBackgroundResource(R.drawable.selectbutton);
                                }
                            }, 10000L);
                        }
                    }
                });
                return view;
            }
        }
        /* カスタマイズ */
        public static class SettingCustomize extends Fragment {
            private int n = -1;
            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
                View v = inflater.inflate(R.layout.preference_customize, container, false);
                TextView preheader = (TextView) getActivity().findViewById(R.id.preheaderchild);
                preheader.setText("カスタマイズ");
                SharedPreferences predata = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE);
                final String[] zigenitems = {"１, ２, ３, ４, ５, ６限", "１, ２, ３, ４, ５限", "２, ３, ４, ５, ６限",
                        "３, ４, ５, ６, ７限", "２, ３, ４, ５, ６, ７限"};
                final String[] frequencys = {"毎日","毎週月曜日","毎週火曜日","毎週水曜日","毎週木曜日","毎週金曜日","毎週土曜日","毎週日曜日","月初日"};
                RelativeLayout saturday = (RelativeLayout) v.findViewById(R.id.saturday);
                RelativeLayout bikoumemo = (RelativeLayout) v.findViewById(R.id.bikoumemo);
                RelativeLayout zigen = (RelativeLayout) v.findViewById(R.id.zigen);
                RelativeLayout newsreload = (RelativeLayout) v.findViewById(R.id.newsreload);
                RelativeLayout reloadfrequency = (RelativeLayout) v.findViewById(R.id.reloadfrequency);
                RelativeLayout reloadtime = (RelativeLayout) v.findViewById(R.id.reloadtime);
                final ToggleButton t1 = (ToggleButton) v.findViewById(R.id.toggle1);
                final ToggleButton t2 = (ToggleButton) v.findViewById(R.id.toggle2);
                final TextView t3 = (TextView) v.findViewById(R.id.selectzigen);
                final ToggleButton t4 = (ToggleButton) v.findViewById(R.id.toggle3);
                final TextView t5 = (TextView) v.findViewById(R.id.frequency);
                final TextView t6 = (TextView) v.findViewById(R.id.time);
                t1.setChecked(predata.getBoolean("saturday", true));
                t2.setChecked(predata.getBoolean("bikou", true));
                t3.setText(zigenitems[predata.getInt("zigen", 0)]);
                t4.setChecked(predata.getBoolean("reload", true));
                t5.setText(frequencys[predata.getInt("reloadfrequency", 7)]);
                t6.setText(String.format("%02d", predata.getInt("ReloadTimeHour",0)) +":"+ String.format("%02d", predata.getInt("ReloadTimeMinutes",0)));
                saturday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        t1.performClick();
                    }
                });
                bikoumemo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        t2.performClick();
                    }
                });
                newsreload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        t4.performClick();
                    }
                });
                t1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor predataedit = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE).edit();
                        if (t1.isChecked()) { predataedit.putBoolean("saturday", true);
                        } else { predataedit.putBoolean("saturday", false); }
                        predataedit.apply();
                    }
                });
                t2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor predataedit = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE).edit();
                        if (t2.isChecked()) { predataedit.putBoolean("bikou", true);
                        } else { predataedit.putBoolean("bikou", false); }
                        predataedit.apply();
                    }
                });
                t4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor predataedit = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE).edit();
                        if (t4.isChecked()) { predataedit.putBoolean("reload", true);
                        } else { predataedit.putBoolean("reload", false); }
                        predataedit.apply();
                    }
                });
                zigen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { onClickZigen(t3, zigenitems); }
                });
                reloadfrequency.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { onClickReloadFrequency(t5, frequencys); }
                });
                reloadtime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { onClickReloadTime(t6); }
                });
                return v;
            }
    
            private void onClickZigen(final TextView t3, final String[] zigenitems) {
                SharedPreferences predata = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE);
                AlertDialog.Builder zigenDlg = new AlertDialog.Builder(getActivity());
                zigenDlg.setTitle("表示する時限の変更");
                zigenDlg.setSingleChoiceItems(zigenitems, n = predata.getInt("zigen", 0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        n = which;
                    }
                });
                zigenDlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE).edit().putInt("zigen", n).apply();
                        t3.setText(zigenitems[getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE).getInt("zigen", 0)]);
                        Toast.makeText(getActivity(), String.format("%s に設定しました。", zigenitems[n]), Toast.LENGTH_LONG).show();
                    }
                });
                zigenDlg.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}}).show();
            }

            private void onClickReloadFrequency(final TextView t5, final String[] frequency) {
                SharedPreferences predata = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE);
                AlertDialog.Builder frequencyDlg = new AlertDialog.Builder(getActivity());
                frequencyDlg.setTitle("更新頻度の変更");
                frequencyDlg.setSingleChoiceItems(frequency, n = predata.getInt("reloadfrequency", 0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        n = which;
                    }
                });
                frequencyDlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE).edit().putInt("reloadfrequency", n).apply();
                        t5.setText(frequency[getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE).getInt("reloadfrequency", 0)]);
                        Toast.makeText(getActivity(), String.format("%s に設定しました。", frequency[n]), Toast.LENGTH_LONG).show();
                    }
                });
                frequencyDlg.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}}).show();
            }

            private void onClickReloadTime(final TextView t6) {
                Calendar calendar = Calendar.getInstance(); calendar.set(Calendar.HOUR, 0); calendar.set(Calendar.MINUTE, 0);
                int minute = calendar.get(Calendar.MINUTE); int hour = calendar.get(Calendar.HOUR);
                TimePickerDialog timeDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker timePicker, int h, int m){
                                SharedPreferences predata = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE);
                                predata.edit().putInt("ReloadTimeHour", h).putInt("ReloadTimeMinutes", m).apply();
                                t6.setText(String.format("%02d", h) +":"+ String.format("%02d", m));
                            }
                        },hour,minute,true);
                timeDialog.show();
            }
        }
    }
    /* 通知 */
    public static class SettingNortification extends Fragment {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.preference_nortification, container, false);
            TextView preheader = (TextView)getActivity().findViewById(R.id.preheaderchild);
            preheader.setText("通知");
            final ToggleButton t3 = (ToggleButton)v.findViewById(R.id.toggle3);
            final ToggleButton t4 = (ToggleButton)v.findViewById(R.id.toggle4);
            SharedPreferences predata = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE);
            t3.setChecked(predata.getBoolean("tenNortification", true));
            t4.setChecked(predata.getBoolean("NoClassNortification", true));
            RelativeLayout tenbefore = (RelativeLayout)v.findViewById(R.id.tenbefore);
            RelativeLayout kyukou = (RelativeLayout)v.findViewById(R.id.kyukou);
            tenbefore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    t3.performClick();
                }
            });
            t3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor predataedit = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE).edit();
                    if (t3.isChecked()) { predataedit.putBoolean("tenNortification", true); }
                    else { predataedit.putBoolean("tenNortification", false); }
                    predataedit.apply();
                    Log.i("test", String.valueOf(getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE).getBoolean("tenNortification", false)));
                }
            });
            kyukou.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    t4.performClick();
                }
            });
            t4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor predataedit = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE).edit();
                    if (t4.isChecked()) { predataedit.putBoolean("NoClassNortification", true); }
                    else { predataedit.putBoolean("NoClassNortification", false); }
                    predataedit.apply();
                    Log.i("test", String.valueOf(getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE).getBoolean("NoClassNortification", false)));
                }
            });
            TextView gettime = (TextView)v.findViewById(R.id.gettime);
            gettime.setText(String.format("%02d", predata.getInt("KyukoHour",6)) +":"+ String.format("%02d", predata.getInt("KyukoMinutes",0)));
            RelativeLayout pretimeset = (RelativeLayout)v.findViewById(R.id.pretimeset);
            pretimeset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance(); calendar.set(Calendar.HOUR, 6); calendar.set(Calendar.MINUTE, 0);
                    int minute = calendar.get(Calendar.MINUTE); int hour = calendar.get(Calendar.HOUR);
                    TimePickerDialog timeDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener(){
                                @Override
                                public void onTimeSet(TimePicker timePicker, int h, int m){
                                    SharedPreferences predata = getActivity().getSharedPreferences("PreData", Context.MODE_PRIVATE);
                                    predata.edit().putInt("KyukoHour", h).putInt("KyukoMinutes", m).apply();
                                    TextView gettime = (TextView)getActivity().findViewById(R.id.gettime);
                                    gettime.setText(String.format("%02d", h) +":"+ String.format("%02d", m));
                                }
                            },hour,minute,true);
                    timeDialog.show();
                }
            });
            return v;
        }
    }
    /* 時間割編集 */
    public static class SettingTimetable extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.preference_timetable, container, false);
            try{
            TextView preheader = (TextView)getActivity().findViewById(R.id.preheaderchild);
            preheader.setText("時間割編集");
            FragmentManager mFragmentManager = getChildFragmentManager();
            final FragmentTabHost tabHost = (FragmentTabHost)v.findViewById(android.R.id.tabhost);
            tabHost.setup(getActivity().getApplicationContext(), mFragmentManager, R.id.timetablecontent);
            TabHost.TabSpec mTabSpec1 = tabHost.newTabSpec("tab1");
            TabHost.TabSpec mTabSpec2 = tabHost.newTabSpec("tab2");
            mTabSpec1.setIndicator("前期");
            mTabSpec2.setIndicator("後期");
            tabHost.addTab(mTabSpec1, PreTimeTable.class, null);  // このファイルの後ろにある
            tabHost.addTab(mTabSpec2, PostTimeTable.class, null); // このファイルの後ろにある
            tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.cell1);
            tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#ebebeb")); // init
            tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                public void onTabChanged(String arg0) {
                    for (int i = 0; i < 2; i++) { // set style in all tab
                        tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#ebebeb"));
                    } // set style in selected tab
                    tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.cell1);
                }
            }); }catch(Exception e){ e.printStackTrace();  }
            return v;
        }
    }
    /* 成績表編集 */
    public static class SettingRecord extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.preference_record, container, false);
            TextView preheader = (TextView)getActivity().findViewById(R.id.preheaderchild);
            preheader.setText("成績表編集");
            RelativeLayout bar = (RelativeLayout)getActivity().findViewById(R.id.preheaderbar);
            Button save = new Button(getActivity());  save.setId(R.id.save);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); save.setLayoutParams(params);
            save.setBackgroundResource(R.color.transparent); save.setPadding(0,5,0,0); bar.addView(save);
            try{
                FragmentManager mFragmentManager = getChildFragmentManager();
                final FragmentTabHost tabHost = (FragmentTabHost)v.findViewById(android.R.id.tabhost);
                tabHost.setup(getActivity().getApplicationContext(), mFragmentManager, R.id.editrecord);
                TabHost.TabSpec mTabSpec1 = tabHost.newTabSpec("tab1");
                TabHost.TabSpec mTabSpec2 = tabHost.newTabSpec("tab2");
                mTabSpec1.setIndicator("単位習得状況");
                mTabSpec2.setIndicator("履修科目一覧");
                tabHost.addTab(mTabSpec1, PreRecordUnit.class, null); // このファイルの後ろにある
                tabHost.addTab(mTabSpec2, PreRecordCourse.class, null); // このファイルの後ろにある
                tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.cell1);
                tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#ebebeb")); // init
                tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                    public void onTabChanged(String arg0) {
                        for (int i = 0; i < 2; i++) { // set style in all tab
                            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#ebebeb"));
                        } // set style in selected tab
                        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.cell1);
                    }
                }); }catch(Exception e){ e.printStackTrace();  }
            return v;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            RelativeLayout bar = (RelativeLayout)getActivity().findViewById(R.id.preheaderbar);
            Button save = (Button)getActivity().findViewById(R.id.save);
            bar.removeView(save);
        }

    }
    /* エラー報告 */
    public static class SettingError extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.preference_error, container, false);
            TextView preheader = (TextView)getActivity().findViewById(R.id.preheaderchild);
            Button senderror = (Button)v.findViewById(R.id.senderror);
            preheader.setText("エラー報告");
            senderror.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                    EditText ek = (EditText)getActivity().findViewById(R.id.ekenmei);
                    EditText ec = (EditText)getActivity().findViewById(R.id.econtent);
                    if(ek.getText().length()==0 || ec.getText().length()==0) {
                        Toast.makeText(getActivity(), "必ず件名と内容を埋めてください。", Toast.LENGTH_LONG).show();
                    } else {
                        final String fek = ek.getText().toString() + " " + Build.MODEL + " " + Build.VERSION.SDK_INT;
                        final String fec = ec.getText().toString();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("送信します。よろしいですか？").setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AsyncTask<Void, Void, String>() {
                                    @Override
                                    protected String doInBackground(Void... params) {
                                        try { Jsoup.connect("http://gms.gdl.jp/~yoshihiro/contact.php").data("test1", fek).data("test2", fec).post();
                                        } catch (Exception e) { e.printStackTrace(); }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(String result) {
                                        Toast.makeText(getActivity(), "送信しました。", Toast.LENGTH_LONG).show();
                                        EditText t1 = (EditText)getActivity().findViewById(R.id.ekenmei);
                                        EditText t2 = (EditText)getActivity().findViewById(R.id.econtent);
                                        t1.getText().clear(); t2.getText().clear();
                                    }
                                }.execute();
                            }
                        }).create().show();}}catch(Exception e){e.printStackTrace();
                    }
                }
            });
            return v;
        }
    }
    /* このアプリの使い方 */
    public static class SettingRule extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            TextView preheader = (TextView)getActivity().findViewById(R.id.preheaderchild);
            preheader.setText("利用規約・使い方");
            return inflater.inflate(R.layout.preference_rule, container, false);
        }
    }

    /* 時間割編集の前期 */
    public static class PreTimeTable extends Fragment {
        public static PreTimeTable newInstance() { return new PreTimeTable(); }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            final Realm mRealm = Realm.getDefaultInstance();
            View v = inflater.inflate(R.layout.time_table, container, false);
            FragmentActivity activity = getActivity(); Resources resource = getResources();
            edittable(v, 0,  activity, resource);
            return v;
        }
    }
    /* 時間割編集の後期 */
    public static class PostTimeTable extends Fragment {
        public static PostTimeTable newInstance() {
            return new PostTimeTable();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.time_table, container, false);
            FragmentActivity activity = getActivity(); Resources resource = getResources();
            edittable(v, 1,  activity, resource);
            return v;
        }
    }
    /* 時間割編集の画面を生成 */
    private static void edittable(View v, int table, final FragmentActivity activity, Resources resources){
        final Realm mRealm = Realm.getDefaultInstance();
        TableLayout timetable = (TableLayout) v.findViewById(R.id.timetable);
        String[] timelist = {"１限", "２限", "３限", "４限", "５限", "６限", "7限"};
        final String[] youbilist = {"月", "火", "水", "木", "金", "土"};
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int windowheight = displaymetrics.heightPixels;
        int frameheight = (int) resources.getDimension(R.dimen.frame);
        for (int x = 0; x < 8; x++) {
            if (x == 0) { // 一行目
                TableRow row = new TableRow(activity);
                for (int y = 0; y < 7; y++) {
                    TextView txt = new TextView(activity);
                    txt.setBackgroundResource(R.drawable.cell3);
                    txt.setGravity(Gravity.CENTER);
                    if (y == 0) {
                        txt.setWidth((int) resources.getDimension(R.dimen.narrow2));
                        txt.setText("\\");
                        row.addView(txt);
                    } else {
                        txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) resources.getDimension(R.dimen.smalltxt));
                        txt.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));
                        txt.setText(youbilist[y - 1]);
                        row.addView(txt);
                    }
                }
                timetable.addView(row);
            } else {
                TableRow row = new TableRow(activity);
                TextView t = new TextView(activity);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f));
                t.setText(timelist[x - 1]); t.setBackgroundResource(R.drawable.cell2);
                t.setGravity(Gravity.CENTER); t.setWidth((int) resources.getDimension(R.dimen.narrow2));
                t.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) resources.getDimension(R.dimen.smalltxt2));
                t.setHeight((windowheight - frameheight - ((int) resources.getDimension(R.dimen.narrow2))*2) / 7);
                row.addView(t);
                for (int y = 1; y < 7; y++) {
                    LinearLayout col = new LinearLayout(activity);
                    col.setOrientation(LinearLayout.VERTICAL); col.setBackgroundResource(R.drawable.selectcell);
                    col.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));
                    col.setPadding(5, 5, 5, 5);
                    TextView txt1 = new TextView(activity), txt2 = new TextView(activity), txt3 = new TextView(activity);
                    txt1.setMaxLines(2); txt2.setMaxLines(1); txt3.setMaxLines(1);
                    txt1.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) resources.getDimension(R.dimen.smalltxt));
                    txt2.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) resources.getDimension(R.dimen.smalltxt));
                    txt3.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) resources.getDimension(R.dimen.smalltxt));
                    txt1.setEllipsize(TextUtils.TruncateAt.END); txt2.setEllipsize(TextUtils.TruncateAt.END); txt3.setEllipsize(TextUtils.TruncateAt.END);
                    TimeTableDB data = mRealm.where(TimeTableDB.class).equalTo("time", x).equalTo("youbi", y).findAll().get(table);
                    final int fx = x, fy = y; final TimeTableDB fdata = data;
                    final TextView ftxt1 = txt1; final TextView ftxt2 = txt2; final TextView ftxt3 = txt3;
                    txt1.setText(data.course_name); txt2.setText(data.person); txt3.setText(data.room);
                    col.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder log = new AlertDialog.Builder(activity);
                            TextView txta = new TextView(activity), txtb = new TextView(activity), txtc = new TextView(activity);
                            final EditText txt1 = new EditText(activity), txt2 = new EditText(activity), txt3 = new EditText(activity);
                            txta.setText("授業名："); txtb.setText("教師名："); txtc.setText("教室名：");
                            txt1.setText(fdata.course_name); txt1.setWidth(800); txt1.setSelection(txt1.getText().length());
                            txt2.setText(fdata.person); txt2.setWidth(800); txt3.setText(fdata.room); txt3.setWidth(800);
                            txt1.setSingleLine(); txt2.setSingleLine(); txt3.setSingleLine(); txt1.setSelection(txt1.getText().length());
                            TextView txtd = new TextView(activity); final EditText txt4 = new EditText(activity);  txt4.setSingleLine();
                            txtd.setText("授業番号(6桁)："); txt4.setText(fdata.course_id); txt4.setWidth(800); txt4.setHint("シラバスを開くのに利用します");
                            LinearLayout layout = new LinearLayout(activity);
                            layout.setPadding(80,0,80,0); layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setGravity(Gravity.CENTER_HORIZONTAL); layout.addView(txta); layout.addView(txt1);
                            layout.addView(txtb); layout.addView(txt2); layout.addView(txtc); layout.addView(txt3); layout.addView(txtd); layout.addView(txt4);
                            log.setTitle("編集").setMessage(youbilist[fy - 1] + "曜日" + String.valueOf(fx) + "時限目").setView(layout);
                            log.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if(txt1.getText().length()>0&&txt2.getText().length()>0&&txt3.getText().length()>0) {
                                        try {
                                            mRealm.beginTransaction();
                                            fdata.sc_name(txt1.getText().toString());
                                            fdata.sperson(txt2.getText().toString());
                                            fdata.sroom(txt3.getText().toString());
                                            fdata.sc_id(txt4.getText().toString());
                                            mRealm.commitTransaction();
                                            mRealm.close();
                                        } catch (Exception e) {
                                            Log.i("test", String.valueOf(e));
                                        } finally {
                                            Toast.makeText(activity, "更新が完了しました。", Toast.LENGTH_LONG).show();
                                            ftxt1.setText(txt1.getText().toString());
                                            ftxt2.setText(txt2.getText().toString());
                                            ftxt3.setText(txt3.getText().toString());
                                        }
                                    } else {Toast.makeText(activity, "授業名,教師名,教室名を全て埋めてください。", Toast.LENGTH_LONG).show();}
                                }
                            }).setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}}).create().show();
                        }
                    });
                    col.addView(txt1); col.addView(txt2); col.addView(txt3);
                    row.addView(col);
                }
                timetable.addView(row);
            }
        }
    }
    /* 成績表編集の単位修得状況 */
    public static class PreRecordUnit extends Fragment {
        public static PreRecordUnit newInstance() { return new PreRecordUnit(); }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.record_unit, container, false);
            final Realm mRealm = Realm.getDefaultInstance();
            final RealmResults<RecordUnitDB> allrecord = mRealm.where(RecordUnitDB.class).findAll();
            final Button save = (Button)getActivity().findViewById(R.id.save);
            save.setText("単位保存"); save.setTextColor(Color.parseColor("#9f969696"));
            RelativeLayout bar = (RelativeLayout)getActivity().findViewById(R.id.preheaderbar);
            TableLayout ll = (TableLayout) v.findViewById(R.id.unittable);
            for(int i=0; i < allrecord.size(); i++) {
                TableRow row = new TableRow(this.getActivity());
                TextView txt1 = new TextView(this.getActivity());
                EditText txt2 = new EditText(this.getActivity()), txt3 = new EditText(this.getActivity()),
                        txt4 = new EditText(this.getActivity()), txt5 = new EditText(this.getActivity()), txt6 = new EditText(this.getActivity());
                EditText[] txts = {txt2, txt3, txt4, txt5, txt6};
                txt1.setBackgroundResource(R.drawable.cell5); txt1.setGravity(Gravity.CENTER);
                txt1.setHeight((int) getResources().getDimension(R.dimen.unitcellheight)); txt1.setTextSize(15);
                txt1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT ,1f));
                txt1.setText(String.valueOf(allrecord.get(i).subject)); txt2.setText(String.valueOf(allrecord.get(i).need));
                txt3.setText(String.valueOf(allrecord.get(i).getted)); txt4.setText(String.valueOf(allrecord.get(i).shortage));
                txt5.setText(String.valueOf(allrecord.get(i).getted_future)); txt6.setText(String.valueOf(allrecord.get(i).shortage_future));
                for(EditText t : txts){
                    t.setBackgroundResource(R.drawable.cell1); t.setGravity(Gravity.CENTER); t.setTextSize(15);
                    t.setHeight((int) getResources().getDimension(R.dimen.unitcellheight)); t.setSingleLine(true);
                    t.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT ,1f));
                }
                /* 枠の中にフォーカスした瞬間右上に保存ボタンが点灯し、それを押すと保存するという流れ */
                txt2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override public void onFocusChange(View v, boolean f) {
                        save.setTextColor(Color.WHITE); save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                save.setEnabled(false); save.setText("更新中...");
                                new Handler().postDelayed(new Runnable() { public void run() {
                                    save.setEnabled(true); save.setText("単位保存"); save.setTextColor(Color.parseColor("#9f969696"));} }, 3000L);
                                final FragmentActivity view = getActivity(); unitsave(view);
                            }
                        });
                    }
                });
                txt3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override public void onFocusChange(View v, boolean f) {
                        save.setTextColor(Color.WHITE); }});  save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save.setEnabled(false); save.setText("更新中...");
                        new Handler().postDelayed(new Runnable() { public void run() {
                            save.setEnabled(true); save.setText("単位保存"); save.setTextColor(Color.parseColor("#9f969696"));} }, 3000L);
                        final FragmentActivity view = getActivity(); unitsave(view);
                    }
                });
                txt4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override public void onFocusChange(View v, boolean f) {
                        save.setTextColor(Color.WHITE);}});  save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save.setEnabled(false); save.setText("更新中...");
                        new Handler().postDelayed(new Runnable() { public void run() {
                            save.setEnabled(true); save.setText("単位保存"); save.setTextColor(Color.parseColor("#9f969696"));} }, 3000L);
                        final FragmentActivity view = getActivity(); unitsave(view);
                    }
                });
                txt5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override public void onFocusChange(View v, boolean f) {
                        save.setTextColor(Color.WHITE);}});  save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save.setEnabled(false); save.setText("更新中...");
                        new Handler().postDelayed(new Runnable() { public void run() {
                            save.setEnabled(true); save.setText("単位保存"); save.setTextColor(Color.parseColor("#9f969696"));} }, 3000L);
                        final FragmentActivity view = getActivity(); unitsave(view);
                    }
                });
                txt6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override public void onFocusChange(View v, boolean f) {
                        save.setTextColor(Color.WHITE);}});  save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save.setEnabled(false); save.setText("更新中...");
                        new Handler().postDelayed(new Runnable() { public void run() {
                            save.setEnabled(true); save.setText("単位保存"); save.setTextColor(Color.parseColor("#9f969696"));} }, 3000L);
                        final FragmentActivity view = getActivity(); unitsave(view);
                    }
                });
                row.addView(txt1);row.addView(txt2);row.addView(txt3);row.addView(txt4);
                row.addView(txt5); row.addView(txt6); ll.addView(row);
            }
            return v;
        }
        /* 保存は変更箇所のみでなく、全体を丸ごと保存する */
        void unitsave(FragmentActivity view) {
            Realm mRealm = null;
            try {
                mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();
                TableLayout ll = (TableLayout) view.findViewById(R.id.unittable);
                Log.i("test", String.valueOf(ll));
                for (int x = 1; x < ll.getChildCount(); x++) {
                    Log.i("x", String.valueOf(x));
                    TableRow row = (TableRow) ll.getChildAt(x);
                    for (int y = 1; y < 6; y++) {
                        EditText col = (EditText) row.getChildAt(y);
                        RecordUnitDB data = mRealm.where(RecordUnitDB.class).equalTo("id", x - 1).findAll().get(0);
                        // Log.i("test", col.getText().toString());
                        switch (y) {
                            case 1: data.sneed(col.getText().toString()); break;
                            case 2: data.sgetted(col.getText().toString()); break;
                            case 3: data.sshortage(col.getText().toString()); break;
                            case 4: data.sg_future(col.getText().toString()); break;
                            case 5: data.ss_future(col.getText().toString()); break;
                        }
                    }
                }
                mRealm.commitTransaction();
            } catch (Exception e) { e.printStackTrace(); mRealm.cancelTransaction(); } finally {
                Toast.makeText(getActivity(), "単位習得状況を更新しました。", Toast.LENGTH_LONG).show();
            }
        }
    }
    /* 成績表編集の履修科目一覧 */
    public static class PreRecordCourse extends Fragment {
        public static PreRecordCourse newInstance() { return new PreRecordCourse(); }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.record_course, container, false);
            final Realm mRealm = Realm.getDefaultInstance();
            final RealmResults<RecordCourseDB> allrecord = mRealm.where(RecordCourseDB.class).findAll().sort("year");
            final Button save = (Button)getActivity().findViewById(R.id.save);
            save.setText("履修保存"); save.setTextColor(Color.parseColor("#9f969696"));
            int windowwidth = new DisplayMetrics().widthPixels;
            final TableLayout ll = (TableLayout) v.findViewById(R.id.coursetable);
            for(int i=0; i < allrecord.size(); i++){
                TableRow row = new TableRow(this.getActivity());
                EditText txt1 = new EditText(this.getActivity()), txt2 = new EditText(this.getActivity()),
                        txt3 = new EditText(this.getActivity()), txt4 = new EditText(this.getActivity());
                EditText txts[] = {txt1, txt2, txt3, txt4};
                txt1.setText(String.valueOf(allrecord.get(i).year)); txt2.setText(String.valueOf(allrecord.get(i).course_name));
                txt3.setText(String.valueOf(allrecord.get(i).unit)); txt4.setText(String.valueOf(allrecord.get(i).value));
                int x = 0;
                for(EditText t : txts){
                    t.setTextSize(15); t.setBackgroundResource(R.drawable.cell1);
                    t.setHeight((int) getResources().getDimension(R.dimen.unitcellheight));
                    t.setSingleLine(true); t.setGravity(Gravity.CENTER);
                    if(x == 1){
                        t.setWidth(windowwidth - (int) getResources().getDimension(R.dimen.unitcellwidth)*3);
                        t.setPadding(0,0,20,0); t.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
                    } else {
                        t.setWidth((int) getResources().getDimension(R.dimen.unitcellwidth));
                    }
                    final EditText ftxt1 = txt1, ftxt2 = txt2, ftxt3 = txt3, ftxt4 = txt4; final int fi=i;
                    /* 枠の中にフォーカスした瞬間右上に保存ボタンが点灯し、それを押すと保存するという流れ */
                    t.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override public void onFocusChange(View v, boolean f) {
                            save.setTextColor(Color.WHITE);
                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    save.setEnabled(false);
                                    save.setEnabled(false); save.setText("更新中...");
                                    new Handler().postDelayed(new Runnable() { public void run() {
                                        save.setEnabled(true); save.setText("履修保存"); save.setTextColor(Color.parseColor("#9f969696"));} }, 3000L);
                                    try {
                                        mRealm.beginTransaction();
                                        Log.i("test", String.valueOf(ll));
                                        for (int x = 1; x < ll.getChildCount(); x++) {
                                            Log.i("x", String.valueOf(x));
                                            TableRow row = (TableRow) ll.getChildAt(x);
                                            for (int y = 0; y < 4; y++) {
                                                EditText col = (EditText) row.getChildAt(y);
                                                RecordCourseDB data = mRealm.where(RecordCourseDB.class).equalTo("id", x-1).findAll().get(0);
                                                // Log.i("test", col.getText().toString());
                                                switch (y) {
                                                    case 0: data.syear(Integer.parseInt(col.getText().toString())); break;
                                                    case 1: data.sc_name(col.getText().toString()); break;
                                                    case 2: data.sunit(Integer.parseInt(col.getText().toString())); break;
                                                    case 3: data.svalue(col.getText().toString()); break;
                                                }
                                            }
                                        }
                                        mRealm.commitTransaction();
                                    } catch(Exception e){e.printStackTrace(); mRealm.cancelTransaction();} finally{
                                        Toast.makeText(getActivity(), "履修科目一覧を更新しました。", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }}); x++;
                } row.addView(txt1); row.addView(txt2); row.addView(txt3); row.addView(txt4); ll.addView(row);
            }
            return v;
        }
    }
}