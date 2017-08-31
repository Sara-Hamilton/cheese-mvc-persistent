package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.data.CategoryDao;
import org.launchcode.models.data.CheeseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

import static org.launchcode.models.Category.*;

/**
 * Created by LaunchCode
 */
@Controller
@RequestMapping("cheese")
public class CheeseController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private CategoryDao categoryDao;

    // Request path: /cheese
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "My Cheeses");

        return "cheese/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddCheeseForm(Model model) {

        model.addAttribute("title", "Add Cheese");
        model.addAttribute(new Cheese());
        model.addAttribute("categories", categoryDao.findAll());
        return "cheese/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddCheeseForm(@ModelAttribute  @Valid Cheese newCheese,
                                       Errors errors, @RequestParam int categoryId, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Cheese");
            model.addAttribute("categories", categoryDao.findAll());
            return "cheese/add";
        }

        Category cat = categoryDao.findOne(categoryId);
        newCheese.setCategory(cat);

        cheeseDao.save(newCheese);
        return "redirect:";
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemoveCheeseForm(Model model) {
        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "Remove Cheese");
        return "cheese/remove";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveCheeseForm(@RequestParam int[] cheeseIds) {

        for (int cheeseId : cheeseIds) {
            cheeseDao.delete(cheeseId);
        }

        return "redirect:";
    }


    //Bonus mission for Part 2
    @RequestMapping(value = "category/{categoryId}", method = RequestMethod.GET)
    public String category (Model model, @PathVariable int categoryId) {

        Category cat = categoryDao.findOne(categoryId);

        //Make an array list of cheeses
        // for all cheeses in database, if cheese category name matches, add to list
        // Category category =

        //take category id, get category name
        // if cheese category == category name, add to list
        //ArrayList<Category> categories = (ArrayList<Category>) categoryDao.findAll();
        //ArrayList<Category> singleCategory = new ArrayList<>();
        //for (Category category : categories) {
        //    if (category.getId() == categoryId) {
        //        singleCategory.add(category);

        //    }
        //}

        ArrayList<Cheese> cheeses = new ArrayList<>();
        for (Cheese cheese : cheeseDao.findAll())
            if (cheese.getCategory() == cat) {
                cheeses.add(cheese);

            }
        model.addAttribute("cheesesByCategory", cheeses);
        model.addAttribute("title", "Cheeses in Same Category");

        return "cheese/index";

    }

}
