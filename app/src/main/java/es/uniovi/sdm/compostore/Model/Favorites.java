package es.uniovi.sdm.compostore.Model;

public class Favorites {

    private String ComponentId, ComponentName, ComponentPrice,
            ComponentCategoryId, ComponentImage, ComponentDiscount, ComponentDescription, UserPhone;


    public Favorites() {

    }


    public Favorites(String componentId, String componentName, String componentPrice, String componentCategoryId,
                     String componentImage, String componentDiscount, String componentDescription, String userPhone) {
        ComponentId = componentId;
        ComponentName = componentName;
        ComponentPrice = componentPrice;
        ComponentCategoryId = componentCategoryId;
        ComponentImage = componentImage;
        ComponentDiscount = componentDiscount;
        ComponentDescription = componentDescription;
        UserPhone = userPhone;
    }


    public String getComponentId() {
        return ComponentId;
    }

    public void setComponentId(String componentId) {
        ComponentId = componentId;
    }

    public String getComponentName() {
        return ComponentName;
    }

    public void setComponentName(String componentName) {
        ComponentName = componentName;
    }

    public String getComponentPrice() {
        return ComponentPrice;
    }

    public void setComponentPrice(String componentPrice) {
        ComponentPrice = componentPrice;
    }

    public String getComponentCategoryId() {
        return ComponentCategoryId;
    }

    public void setComponentCategoryId(String componentCategoryId) {
        ComponentCategoryId = componentCategoryId;
    }

    public String getComponentImage() {
        return ComponentImage;
    }

    public void setComponentImage(String componentImage) {
        ComponentImage = componentImage;
    }

    public String getComponentDiscount() {
        return ComponentDiscount;
    }

    public void setComponentDiscount(String componentDiscount) {
        ComponentDiscount = componentDiscount;
    }

    public String getComponentDescription() {
        return ComponentDescription;
    }

    public void setComponentDescription(String componentDescription) {
        ComponentDescription = componentDescription;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }
}
