package es.uniovi.sdm.compostore.Model;

import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private List<Order> components; //Lista de pedido de componentes

    public Request() {
    }

    public Request(String phone, String name, String address, String total, List<Order> components) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.components = components;
        this.status = "0"; //Por defecto sera 0, 0: Placed, 1:Shipping, 2: Shipped
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getComponents() {
        return components;
    }

    public void setComponents(List<Order> components) {
        this.components = components;
    }
}
