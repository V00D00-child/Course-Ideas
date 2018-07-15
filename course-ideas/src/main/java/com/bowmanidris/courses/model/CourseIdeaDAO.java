package com.bowmanidris.courses.model;

/*
CourseIdeaDAO.java

This interface defines the standard operations to be performed on a model object(s).

 */

import java.util.List;

public interface CourseIdeaDAO {

    //this method adds new CourseIdea to the list ideas
    boolean add(CourseIdea idea);

    //This method returns all the CourseIdea objects
    List<CourseIdea> findAll();

    //This method returns a CourseIdea by slug
    CourseIdea findBySlug(String slug);

}//end interface CourseIdeaDAO
