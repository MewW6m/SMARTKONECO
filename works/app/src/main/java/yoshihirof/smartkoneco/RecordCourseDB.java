package yoshihirof.smartkoneco;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/* 成績表履修表データベース */
public class RecordCourseDB extends RealmObject {
    @PrimaryKey
    public int id;
    public int year;
    public String course_name;
    public String value; // 評価
    public int unit; // 単位数
    public void syear(int year){ this.year = year; }
    public void sc_name(String course_name){ this.course_name = course_name; }
    public void svalue(String value){ this.value = value; }
    public void sunit(int unit){ this.unit = unit; }

    /* example
    |id|year|course_name|value|unit|
    |1|2017|数学1|A|2|
    |2|2017|数学2|S|2|
    */
}