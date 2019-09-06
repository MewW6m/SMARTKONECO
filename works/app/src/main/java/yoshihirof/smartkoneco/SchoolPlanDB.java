package yoshihirof.smartkoneco;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/* 行事日程データベース */
public class SchoolPlanDB extends RealmObject {
    @PrimaryKey
    public int id;
    public String date;
    public String content;
    public boolean holiday;
    public String gdate(String date){ return date; }
    public void sdate(String date){ this.date = date; }
    public String gcontent(String content){ return content; }
    public void scontent(String content){ this.content = content; }
    public boolean gholiday(boolean holiday){ return holiday; }
    public void sholiday(boolean holiday){ this.holiday = holiday; }

    /* example
    |id|date|content|holiday|
    |1|4/7|入学式|true|
    |2|4/10|授業開始|false|
    */
}
