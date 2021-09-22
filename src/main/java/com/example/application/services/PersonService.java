package com.example.application.services;

import com.example.application.model.Person;
import com.example.application.model.PersonData;

import java.util.List;
import java.util.stream.Stream;

public class PersonService {

	private PersonData personData = new PersonData();

	public List<Person> fetch(int offset, int limit) {
		int end = offset + limit;
		int size = personData.getPersons().size();
		if (size <= end) {
			end = size;
		}
		return personData.getPersons().subList(offset, end);
	}

	public Stream<Person> fetchPage(int page, int pageSize) {
		return personData.getPersons().stream().skip((long)page * pageSize)
			.limit(pageSize);
	}

	public int count() {
		return personData.getPersons().size();
	}

	public List<Person> fetchAll() {
		return personData.getPersons();
	}
}
