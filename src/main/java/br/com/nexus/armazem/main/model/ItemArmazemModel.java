package br.com.nexus.armazem.main.model;

public class ItemArmazemModel {

    public ItemArmazemModel(String name, String ID, int amount, int price, boolean isRecall) {
        this.name = name;
        this.ID = ID;
        this.amount = amount;
        this.price = price;
        this.isRecall = isRecall;
    }

    private String name;
    private String ID;
    private int amount;
    private int price;
    private boolean isRecall;

    public boolean isRecall() {
        return isRecall;
    }

    public void setRecall(boolean recall) {
        isRecall = recall;
    }

    public void addQuantidade(int amount) {
        this.amount = this.amount+amount;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
