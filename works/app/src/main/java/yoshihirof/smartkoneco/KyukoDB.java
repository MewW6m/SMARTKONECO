package yoshihirof.smartkoneco;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/* 休講情報データベース */
public class KyukoDB extends RealmObject {
    @PrimaryKey
    public int id;
    public String date;
    public String zigen;
    public String teacher;
    public void s_date(String date){ this.date = date; }
    public void s_zigen(String zigen){ this.zigen = zigen; }
    public void s_teacher(String teacher){ this.teacher = teacher; }

    /* example
    |id|date|zigen|teacher|
    |1|5/1|1|山田太郎|
    |2|5/1|1|風間雄太|
    */
}
