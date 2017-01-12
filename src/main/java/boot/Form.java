package boot;

import org.springframework.data.repository.*;

import java.util.*;
import javax.persistence.*;

@Entity
public class Form {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    // Oracle requires creating sequence and table
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq_gen")
    @SequenceGenerator(name="seq_gen", sequenceName="form_seq", allocationSize=25)
    private Long id;
    
    private String name;
    private String title;
    private String authorId;

    @Column(length = 10240)
    private String schema;

    public Form() {
    }

    public Form(String name) {
        this.name = name;
    }

    public Form(Long id, String name) {
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

    public String getTitle() {
        return title;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getSchema() {
        return schema;
    }

    // public String toString() {
    //     return "[Id: "+id+", Name: "+name+"]";
    // }
}
