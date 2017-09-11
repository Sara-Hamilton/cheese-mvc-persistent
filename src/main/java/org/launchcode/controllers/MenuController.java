package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index (Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {

        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        model.addAttribute("menus", menuDao.findAll());

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(Model model, @ModelAttribute @Valid Menu menu,
                                     Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);

        model.addAttribute("menu", menu);

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);
        AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());

        model.addAttribute("form", form);
        model.addAttribute("title", "Add Cheese to Menu: " + menu.getName());

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String processAddItemForm(@ModelAttribute @Valid AddMenuItemForm form, Errors errors,
                                     @RequestParam int menuId, @RequestParam int cheeseId, Model model) {

        Menu menu = menuDao.findOne(menuId);
        Cheese cheese = cheeseDao.findOne(cheeseId);
        form = new AddMenuItemForm(menu, cheeseDao.findAll());
        // check if cheese is already on the menu to avoid duplication
        boolean cheeseOnMenu = menu.cheeseOnMenu(cheese, menu);

        if (errors.hasErrors() || cheeseOnMenu) {
            if(cheeseOnMenu){
                model.addAttribute("selected", cheese.getId());
                model.addAttribute("form", form);
                model.addAttribute("errorMessage", "That cheese is already on the menu.");
                model.addAttribute("title", "Add Cheese to Menu: " + menu.getName());
            } else if (errors.hasErrors()) {
                model.addAttribute("menu", menu);
                model.addAttribute("form", form);
                model.addAttribute("title", "Add Cheese to Menu: " + menu.getName());
            }
            return "menu/add-item";
        } else {
            menu.addItem(cheese);
            menuDao.save(menu);
            return "redirect:view/" + menuId;
            }
    }
}
