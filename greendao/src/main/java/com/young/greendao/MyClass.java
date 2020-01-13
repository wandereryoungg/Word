package com.young.greendao;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class MyClass {
    public static void main(String[] args) {
        Schema schema = new Schema(1,"com.young.word.entity");
        addNote(schema);
        try {
            new DaoGenerator().generateAll(schema,"./app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addNote(Schema schema) {
        Entity entity1 = schema.addEntity("WisdomEntity");
        entity1.addIdProperty().autoincrement().primaryKey();
        entity1.addStringProperty("english");
        entity1.addStringProperty("china");
        Entity entity2 = schema.addEntity("CET4Entity");
        entity2.addIdProperty().autoincrement().primaryKey();
        entity2.addStringProperty("word");
        entity2.addStringProperty("english");
        entity2.addStringProperty("china");
        entity2.addStringProperty("sign");
    }
}
