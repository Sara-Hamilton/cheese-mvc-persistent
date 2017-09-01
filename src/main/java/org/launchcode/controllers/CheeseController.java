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


    // Bonus mission for Part 2
    // Ability to display all cheeses in the same category at /cheese/category/{categoryId}
    @RequestMapping(value = "category/{categoryId}", method = RequestMethod.GET)
    public String category (Model model, @PathVariable int categoryId) {

        Category category = categoryDao.findOne(categoryId);

        ArrayList<Cheese> cheeses = new ArrayList<>();
        for (Cheese cheese : cheeseDao.findAll())
            if (cheese.getCategory().equals(category)) {
                cheeses.add(cheese);
            }

        model.addAttribute("cheeses", cheeses);
        model.addAttribute("title", "Cheese in Category: " + category.getName());

        return "cheese/index";
    }

    //Bonus mission for Part 3
    // Ability to edit a cheese
    @RequestMapping(value = "edit/{cheeseId}", method = RequestMethod.GET)
    public String displayEditForm(Model model, @PathVariable int cheeseId) {

        Cheese cheese = cheeseDao.findOne(cheeseId);

        model.addAttribute("cheese", cheese);
        model.addAttribute("categories", categoryDao.findAll());
        model.addAttribute("title", "Edit Cheese " + cheese.getName() + " ID " + cheeseId );

        return "cheese/edit";
    }

    @RequestMapping(value = "edit", method = {RequestMethod.POST})
    public String processEditForm(@ModelAttribute @Valid Cheese cheese, Errors errors,
                                  int cheeseId, String name, String description, @RequestParam int category, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("cheese", cheese);
            model.addAttribute("categories", categoryDao.findAll());
            model.addAttribute("title", "Edit Cheese " + cheese.getName() + " ID " + cheeseId );
            return "cheese/edit";
        }

        Category cat = categoryDao.findOne(category);
        cheese.setCategory(cat);cheese.setName(name);
        cheese.setDescription(description);

        cheeseDao.save(cheese);

        return "redirect:";
    }

}
