package yoshihirof.smartkoneco;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

/* 成績表のタブ切り替えプログラム */
public class Record extends Fragment {
    public static Record newInstance() {
        return new Record();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.record, container, false); // レイアウトファイル"record.xml"を取得
        //FragmentManagerの取得
        FragmentManager mFragmentManager = getChildFragmentManager();
        final FragmentTabHost tabHost = (FragmentTabHost)v.findViewById(android.R.id.tabhost);
        //ContextとFragmentManagerと、FragmentがあたるViewのidを渡してセットアップ
        tabHost.setup(getActivity().getApplicationContext(), mFragmentManager, R.id.recordcontent);
        //String型の引数には任意のidを渡す
        //今回は2つのFragmentをFragmentTabHostから切り替えるため、2つのTabSpecを用意する
        TabHost.TabSpec mTabSpec1 = tabHost.newTabSpec("tab1");
        TabHost.TabSpec mTabSpec2 = tabHost.newTabSpec("tab2");
        //Tab上に表示する文字を渡す
        mTabSpec1.setIndicator("単位習得状況");
        mTabSpec2.setIndicator("履修科目一覧");
        //RecordUnitとRecordCourseフラグメントのタブ切り替えを実装
        tabHost.addTab(mTabSpec1, RecordUnit.class, null);
        tabHost.addTab(mTabSpec2, RecordCourse.class, null);
        // 色切り替え
        tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.cell1); // init
        tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#ebebeb")); // init

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String arg0) {
                for (int i = 0; i < 2; i++) { // set style in all tab
                        tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#ebebeb"));
                } // set style in selected tab
                tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.cell1);
            }
        });

        return v;
    }
}