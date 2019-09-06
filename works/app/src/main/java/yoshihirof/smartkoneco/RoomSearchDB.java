package yoshihirof.smartkoneco;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/* 空き教室データベース */
public class RoomSearchDB extends RealmObject {
    @PrimaryKey
    public int id;
    public String youbi;
    public String zikan;
    public String term;
    public String room;

    public String gyoubi(String youbi){ return youbi; }
    public void syoubi(String youbi){ this.youbi = youbi; }
    public String gzikan(String zikan){ return zikan; }
    public void szikan(String zikan){ this.zikan = zikan; }
    public String gterm(String term){ return term; }
    public void sterm(String term){ this.term = term; }
    public String groom(String room){ return room; }
    public void sroom(String room){ this.room = room; }

    /* example
    |id|youbi|zikan|term|room|
    |1|月|1|前期|1-101|
    |2|月|1|前期|1-102|
    */
}