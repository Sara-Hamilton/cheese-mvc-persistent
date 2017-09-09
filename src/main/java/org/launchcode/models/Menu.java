package org.launchcode.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Menu {

    @NotNull
    @Size(min = 3, max = 15)
    private String name;

    @Id
    @GeneratedValue
    private int id;

    @ManyToMany
    private List<Cheese> cheeses;

    public void addItem(Cheese item) {
        cheeses.add(item);
    }

    public void removeItem(Cheese item) { cheeses.remove(item);}

    public Menu() {
    }

    public Menu(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Cheese> getCheeses() {
        return cheeses;
    }

    public static boolean cheeseOnMenu(Cheese cheese, Menu menu) {
        List<Cheese> cheeseMenu = menu.getCheeses();
        boolean onMenu = false;
        for (Cheese item : cheeseMenu) {
            if (item.equals(cheese)) {
                onMenu = true;
                break;
            }
        }
        return onMenu;
    }
}