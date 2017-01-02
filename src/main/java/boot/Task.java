package boot;

import org.springframework.data.repository.*;

import java.util.*;
import javax.persistence.*;

@Entity
public class Task {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    // Oracle requires creating sequence and table
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq_gen")
    @SequenceGenerator(name="seq_gen", sequenceName="task_seq", allocationSize=25)
    private Long id;
    
    private String name;

    public Task() {
    }

    public Task(String name) {
        this.name = name;
    }

    public Task(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // getter needed for JSON
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // public String toString() {
    //     return "[Id: "+id+", Name: "+name+"]";
    // }
}
