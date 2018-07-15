package com.bowmanidris.courses.model;

/*
SimpleCourseIdeaDAO.java

This class in called simple it is the model object.
This class implements the CourseIdeaDAO interface.
This class is responsible to get data from a data source which can be database / xml or any other storage mechanism.
Remember that this is the simple way of doing things,but not the perfered way
We are doing this project using data structures but it is perfeed to use a database
 */

import java.util.ArrayList;
import java.util.List;

public class SimpleCourseIdeaDAO implements CourseIdeaDAO {

    private List<CourseIdea> ideas;

    public SimpleCourseIdeaDAO() {
        ideas = new ArrayList<>();

        }

    @Override
    public boolean add(CourseIdea idea) {
        return ideas.add(idea);
    }

    @Override
    public List<CourseIdea> findAll() {
        return new ArrayList<>(ideas); //This is not the list but a copy
    }

    @Override
    public CourseIdea findBySlug(String slug) {
        //look at each idea if the slug equals the one we are looking for return or else throw a exception
        return ideas.stream()
                .filter(idea -> idea.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }


}//end class SimpleCourseIdeaDAO
