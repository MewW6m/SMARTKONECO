package yoshihirof.smartkoneco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import java.lang.reflect.Field;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Main extends AppCompatActivity {
    /* 下のタブ切り替えをセットする関数*/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.timetable:
                    loadTimeTable();
                    return true;
                case R.id.record:
                    loadRecord();
                    return true;
                case R.id.roomsearch:
                    loadRoomSearch();
                    return true;
                case R.id.news:
                    loadNews();
                    return true;
            }
            return false;
        }

    };

    /* アプリを起動したとき */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this); // DBを初期化 
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        /* アプリ起動時の紫画面から本アプリ画面へ移行 */
        try {
            Thread.sleep(100); // ここで1秒間スリープし、スプラッシュを表示させたままにする。
        } catch (Exception e) {}
        setTheme(R.style.AppTheme); 
        setContentView(R.layout.main); // スプラッシュthemeを通常themeに変更する
 
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState == null) {
            SharedPreferences predata = getSharedPreferences("PreData", Context.MODE_PRIVATE);
            if(predata.getString("username", "").isEmpty()){ // アクティベートをしていない場合
                Intent intent = new Intent(getApplication(), SettingActivity.class);
                startActivity(intent);
            } else {
                loadTimeTable(); // アプリ起動時に時間割を表示
            }
        }
    }

    /* アプリを開きなおしたとき */
    @Override
    public void onResume(){
        super.onResume();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        switch (navigation.getSelectedItemId()){
            case R.id.timetable: loadTimeTable(); break;
            case R.id.record: loadRecord(); break;
            case R.id.roomsearch: loadRoomSearch(); break;
            case R.id.news: loadNews(); break;
        }
    }
    /* (歯車をクリックした場合)設定画面へ移動 */
    public void onClick(View view){
        Intent intent = new Intent(getApplication(), SettingActivity.class);
        startActivity(intent);
    }
    /* 下の切り替えタブの設定 */
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }
    /* 時間割画面へ移動する関数 */
    private void loadTimeTable() {
        TimeTable fragment = TimeTable.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, fragment);
        ft.commit();
    }
    /* 成績表画面へ移動する関数 */
    private void loadRecord() {
        Record fragment = Record.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, fragment);
        ft.commitNow();
        getSupportFragmentManager().executePendingTransactions();
    }
    /* 空き教室画面へ移動する関数 */
    private void loadRoomSearch() {
        RoomSearch fragment = RoomSearch.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, fragment);
        ft.commit();
    }
    /* 連絡事項画面へ移動する関数 */
    private void loadNews() {
        News fragment = News.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, fragment);
        ft.commit();
    }
}