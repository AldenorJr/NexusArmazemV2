package br.com.nexus.armazem.main.model;

public class ItemArmazemModel {

    public ItemArmazemModel(String name, String ID, int amount, int price) {
        this.name = name;
        this.ID = ID;
        this.amount = amount;
        this.price = price;
    }

    private String name;
    private String ID;
    private int amount;
    private int price;

    public void addQuantidade(int amount) {
        this.amount = this.amount+amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
