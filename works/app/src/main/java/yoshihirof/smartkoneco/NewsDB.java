package yoshihirof.smartkoneco;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/* 連絡事項データベース */
public class NewsDB extends RealmObject {
    @PrimaryKey
    public int id; // 連番の固有id
    public String article_id; // 記事ごとに割り当てられている固有のid
    public String date;
    public String kenmei;
    public String content;
    public void s_id(String article_id){ this.article_id = article_id; }
    public void s_date(String date){ this.date = date; }
    public void s_kenmei(String kenmei){ this.kenmei = kenmei; }
    public void s_content(String content){ this.content = content; }

    /* example
    |id|article_id|date|kenmei|content|
    |1|12345678|4/10|件名テスト1||
    |2|12345678|4/21|件名テスト2||
    */
}
