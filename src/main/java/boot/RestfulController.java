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
    public interface TaskRepo extends CrudRepository<Task, Long> {

        @Query(value = "SELECT * FROM task WHERE name LIKE ?1%", nativeQuery = true)
        List<Task> findByName(String name);

    }    
    @Autowired
    RestfulController.TaskRepo repo;

    // TODO: initialize controller
    @PostConstruct
    public void initialize() {
        repo.save(new Task("TaskOne"));
        repo.save(new Task("TaskTwo"));
        repo.save(new Task("TaskThree"));

        // jdbc directly, table and sequence are created via schema.sql
        List<Object[]> values = Arrays.asList("TaskFour", "TaskFive")
            .stream().map(value -> value.split("$"))
            .collect(Collectors.toList());
        jdbc.batchUpdate(
            "INSERT INTO task (id, name) VALUES (task_seq.nextval, ?)", values);

        log.info("Added 5 tasks");
   	}

	@RequestMapping(value="/task", method=RequestMethod.GET)
    public Iterable<Task> searchTask(
    	@RequestParam(value="name", required=false) String[] names) {
        // name=val1&name=val2 passed as an array
        if (names != null) {
            ArrayList<Task> tasks = new ArrayList<Task>();
            for (String name: names) 
                tasks.addAll(repo.findByName(name));
            return tasks;
        } else {
            return repo.findAll();
        }
    }

    @RequestMapping(value="/task/latest", method=RequestMethod.GET)
    public List<Task> latestTask() {
        // jdbc directly with row mapper
        return jdbc.query(
            "SELECT TOP 2 id, name FROM task ORDER BY id DESC", new Object[] {},
            (rs, rowNum) -> new Task(rs.getLong("id"), rs.getString("name"))
        );
    }

    // Having Task as input will make Spring do the following
    // 1. create a Task object and 
    // 2. map client input to object attribute with same name
	@RequestMapping(value="/task", method=RequestMethod.POST)
    public Task createTask(@RequestBody Task task) {
    	return repo.save(task);
    }

	@RequestMapping(value="/task/{id}", method=RequestMethod.GET)
    public Task getTask(@PathVariable Long id) {
    	return repo.findOne(id);
    }

	@RequestMapping(value="/task/{id}", method=RequestMethod.PUT)
    public Task updateTask(@RequestBody Task task) {
        return repo.save(task);
    }

	@RequestMapping(value="/task/{id}", method=RequestMethod.DELETE)
    public Task deleteTask(@PathVariable Long id) {
        Task t = repo.findOne(id);
    	repo.delete(id);
        // some js framework need return deleted data
        return t;
    }
}
