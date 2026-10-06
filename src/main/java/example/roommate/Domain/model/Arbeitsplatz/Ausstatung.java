package example.roommate.Domain.model.Arbeitsplatz;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;

 public class Ausstatung {



    private String name;

      public String getName() {
        return name;
    }

     void setName(String name) {
        this.name = name;
    }

    public Ausstatung(String name) {
        this.name=name;
    }
}
