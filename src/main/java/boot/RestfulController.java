package boot;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;

import org.slf4j.*;

import org.springframework.context.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;

import org.springframework.data.repository.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.config.*;

import org.springframework.jdbc.core.*;

// needed for CRUD repo in nested class
@EnableJpaRepositories(considerNestedRepositories=true)
// needed for cross domain Ajax
@CrossOrigin(origins="http://s.codepen.io")
@RestController
public class RestfulController {

    private static final Logger log = LoggerFactory.getLogger(WebController.class);

    @Autowired
    JdbcTemplate jdbc;

    // TODO: create CRUD repository
    public interface FormRepo extends CrudRepository<Form, Long> {

        @Query(value = "SELECT * FROM form WHERE name LIKE ?1%", nativeQuery = true)
        List<Form> findByName(String name);

    }    
    @Autowired
    RestfulController.FormRepo formRepo;

    // TODO: initialize controller
    @PostConstruct
    public void initialize() {
        formRepo.save(new Form("FormOne"));

        log.info("Added 1 form");
   	}

    // Form CRUD
    @RequestMapping(value="/forms", method=RequestMethod.GET)
    public Iterable<Form> searchForm(
        @RequestParam(value="name", required=false) String[] names) {
        // name=val1&name=val2 passed as an array
        if (names != null) {
            ArrayList<Form> forms = new ArrayList<Form>();
            for (String name: names) 
                forms.addAll(formRepo.findByName(name));
            return forms;
        } else {
            return formRepo.findAll();
        }
    }

    @RequestMapping(value="/forms/latest", method=RequestMethod.GET)
    public List<Form> latestForm() {
        // jdbc directly with row mapper
        return jdbc.query(
            "SELECT TOP 2 id, name FROM form ORDER BY id DESC", new Object[] {},
            (rs, rowNum) -> new Form(rs.getLong("id"), rs.getString("name"))
        );
    }

    // Having Task as input will make Spring do the following
    // 1. create a Task object and 
    // 2. map client input to object attribute with same name
    @RequestMapping(value="/forms", method=RequestMethod.POST)
    public Form createForm(@RequestBody Form form) {
        log.info(form.getName());
        log.info(form.getSchema());
        return formRepo.save(form);
    }

    @RequestMapping(value="/forms/{id}", method=RequestMethod.GET)
    public Form getForm(@PathVariable Long id) {
        return formRepo.findOne(id);
    }

    @RequestMapping(value="/forms", method=RequestMethod.PUT)
    public Form updateForm(@RequestBody Form form) {
        // json object must have id field 
        return formRepo.save(form);
    }

    @RequestMapping(value="/forms/{id}", method=RequestMethod.DELETE)
    public Form deleteForm(@PathVariable Long id) {
        Form f = formRepo.findOne(id);
        formRepo.delete(id);
        // some js framework need return deleted data
        return f;
    }

}
