package org.example.service;

import org.example.entities.Person;
import org.example.repository.PersonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonService {

    private final PersonDao personDao;

    @Autowired
    public PersonService(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Transactional
    public void savePerson(Person person) {
        personDao.save(person);
    }

    @Transactional
    public Person getPersonById(int id) {
        return personDao.getById(id).orElseThrow();
    }

    @Transactional
    public List<Person> showAllPeople() {
        return personDao.getAll();
    }
}
