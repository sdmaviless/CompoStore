package es.uniovi.sdm.compostore.Model;

public class Category {

    private String Name;
    private String Image;

    public Category() {

    }

    public Category(String name, String image) {
        this.Name = name;
        this.Image = image;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
