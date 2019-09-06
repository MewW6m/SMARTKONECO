package yoshihirof.smartkoneco;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/* 時間割データベース */
public class TimeTableDB extends RealmObject {
    @PrimaryKey
    public int id;
    public int youbi;
    public int time;
    public String course_id; // 履修科目それぞれに振られてる履修科目id
    public String course_name; // 履修科目名
    public String person; // 教師名
    public String room; // 教室番号
    public int gyoubi(int youbi){ return youbi; }
    public void syoubi(int youbi){ this.youbi = youbi; }
    public int gtime(int time){ return time; }
    public void stime(int time){ this.time = time; }
    public String gc_id(String course_id){ return course_id; }
    public void sc_id(String course_id){ this.course_id = course_id; }
    public String gc_name(String course__name){ return course_name; }
    public void sc_name(String course_name){ this.course_name = course_name; }
    public String gperson(String course_id){ return person; }
    public void sperson(String person){ this.person = person; }
    public String groom(String room){ return room; }
    public void sroom(String room){ this.room = room; }

    /* example
    |id|youbi|time|course_id|course_name|person|room|
    |1|月|1|26472318|数学1|山多太郎|１－４２３|
    |2|月|2|48213914|数学2|林小太郎|４－１０１|
    */
}
