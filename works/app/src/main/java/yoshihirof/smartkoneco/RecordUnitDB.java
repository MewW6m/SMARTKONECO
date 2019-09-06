package yoshihirof.smartkoneco;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/* 成績表単位表データベース */
public class RecordUnitDB extends RealmObject {
    @PrimaryKey
    public int id;
    public String subject; // 科目分野名
    public String need; // 卒業必要単位数
    public String getted; // 修得単位数
    public String shortage; // 不足単位数
    public String getted_future; // 修得見込単位数
    public String shortage_future; // 不足見込単位数

    public void sId(int id){ this.id = id; }
    public void ssubject(String subject){ this.subject = subject; }
    public void sneed(String need){ this.need = need; }
    public void sgetted(String getted){ this.getted = getted; }
    public void sshortage(String shortage){ this.shortage = shortage; }
    public void sg_future(String getted_future){ this.getted_future = getted_future; }
    public void ss_future(String shortage_future){ this.shortage_future = shortage_future; }

    /* example
    |id|subject|need|getted|shortage|getted_future|shortage_future|
    |1|人文|4|2|2|2|0|
    |2|社会|2|2|2|0|0|
    */
}
