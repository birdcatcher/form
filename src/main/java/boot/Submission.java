package boot;

import org.springframework.data.repository.*;

import java.util.*;
import javax.persistence.*;

@Entity
public class Submission {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    // Oracle requires creating sequence and table
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq_gen")
    @SequenceGenerator(name="seq_gen", sequenceName="submission_seq", allocationSize=25)
    private Long id;

    // By defaul it will be FORM_ID column name 
    // Make column name exact as property name, add the following in property file
    // spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    private String formId;

    private String authorId;

    @Column(length=10240)
    private String data;

    public Submission() {
    }

    public Submission(String data) {
        this.data = data;
    }

    public Submission(Long id, String data) {
        this.id = id;
        this.data = data;
    }

    // getter needed for JSON
    public Long getId() {
        return id;
    }

    public String getFormId() {
        return formId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getData() {
        return data;
    }

    // public String toString() {
    //     return "[Id: "+id+", Data: "+data+"]";
    // }
}
