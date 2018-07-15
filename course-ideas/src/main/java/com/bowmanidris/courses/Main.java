package com.bowmanidris.courses;
/*
Go to http://localhost:4567/ to see website
req=Request
res=Response
Note: if you don't put a path is will happen for every (req,res)
Note: Before-filters are evaluated before each request, and can read the request and read/modify the response.
*/

import com.bowmanidris.courses.model.CourseIdea;
import com.bowmanidris.courses.model.CourseIdeaDAO;
import com.bowmanidris.courses.model.NotFoundException;
import com.bowmanidris.courses.model.SimpleCourseIdeaDAO;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {

    private static final String FLASH_MESSAGE_KEY = "flash_message";

    public static void main(String[] args) {
        /*
        TODO - One way you to get around the duplication is by taking advantage of the request.attributes
         and creating a model object in a global before filter.
         That would allow you to do whatever injecting of the model that was common to all requests.
          In the controller you just then pull out request.attributes[“model”] and add specific information,
          but it would have the flash message.
         */

        //make sure Spark knows where to find the files
        staticFileLocation("/public");

        CourseIdeaDAO dao = new SimpleCourseIdeaDAO(); //make model object that hold the data

        //if the username is in the cookies assign it to a attribute
        before((req,res) -> {
            if (req.cookie("username") != null){
                // sets value of attribute username to t
                req.attribute("username" , req.cookie("username"));
            }

        });

        //visiting the /ideas.hbs requires a known username if not redirect the user
        before("/ideas", (req,res) -> {
            //if username is not in the cookies redirect the user to home page
            if (req.attribute("username") == null){
                setFlashMessage(req,"Whoops, please sign in first");
                res.redirect("/");//redirect to home page
                halt();
            }
        });
//*********************************************************************************************************************

        // capture the post request  for the username from index.hbs and store the username in a cookie
        get("/",(req,res) -> {
            Map<String, String> model = new HashMap<>();
            model.put("username", req.attribute("username"));
            model.put("flashMessage",captureFlashMessage(req));
            return new ModelAndView(model, "index.hbs" );
        }, new HandlebarsTemplateEngine());

        //this handler allows user to create username
        post("/sign-in",(req,res) -> {
            Map<String, String> model = new HashMap<>();
            String username = req.queryParams("username");

            res.cookie("username", username);//put the user name in a cookie
            model.put("username", username);//get the username data from user and put it into the Map
            res.redirect("/");
            return null;
        });

        //this handler get the list CourseIdeas
        get("/ideas",(req,res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("ideas", dao.findAll());
            model.put("flashMessage",captureFlashMessage(req));//will show message then disappear
            return new ModelAndView(model, "ideas.hbs");//pass in a list of course objects into ModelAndView
        }, new HandlebarsTemplateEngine());

        //this handler will allow the user to create a new CourseIdea object
        post("/ideas",(req,res) -> {
            String title = req.queryParams("title");//get the name of the title from user input
            CourseIdea courseIdea = new CourseIdea(title,
                    req.attribute("username"));

            //add the new course idea to the list and redirect the user back to /ideas
            dao.add(courseIdea);
            res.redirect("/ideas");
            return null;
        });

        //this handler will show the details of the selected CourseIdea Note: :slug will be the name of the current slug
        get("/ideas/:slug" , (req,res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("idea", dao.findBySlug(req.params("slug")));
            return  new ModelAndView(model,"idea.hbs");
        }, new HandlebarsTemplateEngine());

        //this handler will tack the vote on the CourseIdea objects
        post("ideas/:slug/vote" , (req,res) -> {
            CourseIdea idea = dao.findBySlug(req.params("slug"));
            boolean added = idea.addVoter(req.attribute("username")); //add a vote to the CourseIdea by username

            //give the user a flashMessage to let them know that their vote was added
            if (added){
                setFlashMessage(req, "Thanks for your vote");
            }else {
                setFlashMessage(req, "You already voted");
            }
            res.redirect("/ideas");//after the vote redirect the user to /ideas
            return null;
        });

        //exception handler: if something is not found sent code 404
        exception(NotFoundException.class, (exc, req, res) -> {
            res.status(404);
            HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();
            String html = engine.render(new ModelAndView(null, "not-found.hbs"));

            res.body(html);//set the body

        });

    }//end main method

    private static void setFlashMessage(Request req, String message) {
        req.session().attribute(FLASH_MESSAGE_KEY, message);
    }//end setFlashMessage()

    private static String getFlashMessage(Request req) {

        //If there is no session
        if (req.session(false) == null){
            return null;
        }
        if (!req.session().attributes().contains(FLASH_MESSAGE_KEY)){
            return null;
        }
        return (String) req.session().attribute(FLASH_MESSAGE_KEY);
    }//end getFlashMessage()

    private static String captureFlashMessage(Request req) {
        String message = getFlashMessage(req);
        //if the flashMessage exist remove it
        if (message != null){
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;

    }//end captureFlashMessage()

}//end Main class