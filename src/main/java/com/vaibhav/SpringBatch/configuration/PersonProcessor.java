package com.vaibhav.SpringBatch.configuration;

import com.vaibhav.SpringBatch.entity.Person;
import org.springframework.batch.item.ItemProcessor;

public class PersonProcessor implements ItemProcessor<Person, Person> {


    @Override
    public Person process(Person person) throws Exception {

        person.setFirst_name(person.getFirst_name().toUpperCase());
        person.setLast_name(person.getFirst_name().toUpperCase());

        return person;
    }
}
