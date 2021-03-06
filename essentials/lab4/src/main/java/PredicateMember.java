import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.Predicates;

import java.util.HashSet;
import java.util.Set;

import static com.hazelcast.query.Predicates.*;

public class PredicateMember {

    private HazelcastInstance hz = Hazelcast.newHazelcastInstance();
    private IMap<String, Person> personMap = hz.getMap("personMap");

    public static void main(String[] args) {
        new PredicateMember().run();
    }

    public void run() {
        personMap.put("1", new Person("Peter", true, 36));
        personMap.put("2", new Person("John", true, 50));
        personMap.put("3", new Person("Marry", false, 20));
        personMap.put("4", new Person("Mike", true, 35));
        personMap.put("5", new Person("Rob", true, 60));
        personMap.put("6", new Person("Jane", false, 43));

        Set s = hz.getSet("foo");
        Person p = new Person("Peter", true, 37);
        s.add(p);
        Person p1 = (Person) s.iterator().next();
        Person p2 = (Person) s.iterator().next();

        System.out.println("Get with name Peter");
        for (Person person : getWithName("Peter")) {
            System.out.println(person);
        }

        System.out.println("Get not with name Peter");
        for (Person person : getNotWithName("Peter")) {
            System.out.println(person);
        }

        System.out.println("Find name Peter and age 36");
        for (Person person : getWithNameAndAge("Peter", 36)) {
            System.out.println(person);
        }

        System.out.println("Find name Peter or age 60");
        for (Person person : getWithNameOrAge("Peter", 60)) {
            System.out.println(person);
        }
    }

    public Set<Person> getWithNameNaive(String name) {
        Set<Person> result = new HashSet<Person>();
        for (Person person : personMap.values()) {
            if (person.name.equals(name)) {
                result.add(person);
            }
        }

        return result;
    }

    public Set<Person> getNotWithName(String name) {
        Predicate namePredicate = equal("name", name);
        Predicate predicate = not(namePredicate);
        return (Set<Person>) personMap.values(predicate);
    }

    public Set<Person> getWithName(String name) {
        Predicate predicate = Predicates.equal("name", name);
        return (Set<Person>) personMap.values(predicate);
    }

    public Set<Person> getWithNameAndAgeSimplified(String name, int age) {
        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate predicate = e.get("name").equal(name).and(e.get("age").equal(age));
        return (Set<Person>) personMap.values(predicate);
    }

    public Set<Person> getWithNameAndAge(String name, int age) {
        Predicate namePredicate = equal("name", name);
        Predicate agePredicate = equal("age", age);
        Predicate predicate = and(namePredicate, agePredicate);
        return (Set<Person>) personMap.values(predicate);
    }

    public Set<Person> getWithNameOrAge(String name, int age) {
        Predicate namePredicate = equal("name", name);
        Predicate agePredicate = equal("age", age);
        Predicate predicate = or(namePredicate, agePredicate);
        return (Set<Person>) personMap.values(predicate);
    }
}
