package boot;

import org.springframework.data.repository.*;

import java.util.*;
import javax.persistence.*;
import org.springframework.data.jpa.domain.support.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private long createdDate;
 
    @Column(name = "modified_date")
    @LastModifiedDate
    private long modifiedDate;    

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
 
    @Column(name = "modified_by")
    @LastModifiedBy
    private String modifiedBy;   
 
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

    public long getCreatedDate() {
        return createdDate;
    }

    public long getModifiedDate() {
        return modifiedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    // public String toString() {
    //     return "[Id: "+id+", Name: "+name+"]";
    // }
}
