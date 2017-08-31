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
    public String processAddMenuForm(@ModelAttribute @Valid Menu newMenu,
                                     Errors errors, @RequestParam String name, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            model.addAttribute("menus", menuDao.findAll());
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET) //value = "view/{menuId}"
    public String viewMenu(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);

        model.addAttribute("menu", menu);
        // model.addAttribute("title", "Menu");

        return "menu/view";
    }



    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);
        AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());

        model.addAttribute("form", form);
        model.addAttribute("title", "Add item to menu: " + menu.getName());

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String processAddItemForm(@ModelAttribute @Valid AddMenuItemForm form, Errors errors,
                                     @RequestParam int menuId, @RequestParam int cheeseId, Model model) {

        Menu menu = menuDao.findOne(menuId);

        if (errors.hasErrors()) {
            model.addAttribute("form", form);
            model.addAttribute("title", "Add item to menu: " + menu.getName());
            return "menu/add-item";
        }

        // Menu menu = menuDao.findOne(menuId);
        Cheese cheese = cheeseDao.findOne(cheeseId);

        menu.addItem(cheese);

        menuDao.save(menu);
        cheeseDao.save(cheese);

        return "redirect:view/" + menuId;
    }

}
