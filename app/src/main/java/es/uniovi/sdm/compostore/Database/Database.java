package es.uniovi.sdm.compostore.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import es.uniovi.sdm.compostore.Model.Favorites;
import es.uniovi.sdm.compostore.Model.Order;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME="CompoStoreDB-SQLite.db";
    private static final int DB_VER =2; //Version de nuestra base de datos


    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public boolean checkComponentExists(String componentId, String userPhone){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From OrderDetail WHERE UserPhone='%s' AND ProductId='%s'", userPhone,componentId);
        cursor = db.rawQuery(SQLQuery, null);
        if(cursor.getCount() > 0){
            flag = true;
        }else{
            flag = false;
        }
        cursor.close();
        return flag;
    }


    public List<Order> getCarts(String userPhone){

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone", "ProductName", "ProductId", "Quantity", "Price", "Discount", "Image"};
        String sqlTable= "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                result.add(new Order(
                        c.getString(c.getColumnIndex("UserPhone")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image"))));
            }while(c.moveToNext());
        }
        return result;
    }



    public void addToCart(Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetail(UserPhone, ProductId,ProductName,Quantity,Price,Discount,Image) VALUES ('%s', '%s', '%s','%s','%s','%s','%s');",
                order.getUserPhone(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());
        db.execSQL(query);
    }

    public void cleanCart(String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s'", userPhone);
        db.execSQL(query);
    }

    public int getCountCart(String userPhone){
        int count =0;

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail WHERE UserPhone='%s'", userPhone);
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                count = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCart (Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= %s WHERE UserPhone = %s AND ProductId='%s'", order.getQuantity(), order.getUserPhone(),order.getProductId());
        db.execSQL(query);
    }

    public void increaseCart (String userPhone, String componentId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= Quantity+1  WHERE UserPhone = %s AND ProductId='%s'", userPhone , componentId);
        db.execSQL(query);
    }

    public void removeFromCart(String productId, String phone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s' and ProductId='%s'", phone, productId);
        db.execSQL(query);
    }


    //Favoritos
    public  void addToFavorites(Favorites component){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO FavoritesActivity (" +
                "ComponentId, ComponentName, ComponentPrice, ComponentCategoryId, ComponentImage, ComponentDiscount, ComponentDescription, UserPhone) " +
                "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                component.getComponentId(),
                component.getComponentName(),
                component.getComponentPrice(),
                component.getComponentCategoryId(),
                component.getComponentImage(),
                component.getComponentDiscount(),
                component.getComponentDescription(),
                component.getUserPhone());

        db.execSQL(query);
    }

    public  void removeFromFavorites(String componentId, String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM FavoritesActivity WHERE ComponentId='%s' and UserPhone='%s';",componentId,userPhone);
        db.execSQL(query);
    }

    public  boolean isFavorite(String componentId, String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE ComponentId='%s' and UserPhone='%s';", componentId, userPhone);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }

    public List<Favorites> getAllFavorites(String userPhone){

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone", "ComponentId", "ComponentName", "ComponentPrice", "ComponentCategoryId", "ComponentImage", "ComponentDiscount", "ComponentDescription"};
        String sqlTable= "Favorites";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final List<Favorites> result = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                result.add(new Favorites(
                        c.getString(c.getColumnIndex("ComponentId")),
                        c.getString(c.getColumnIndex("ComponentName")),
                        c.getString(c.getColumnIndex("ComponentPrice")),
                        c.getString(c.getColumnIndex("ComponentCategoryId")),
                        c.getString(c.getColumnIndex("ComponentImage")),
                        c.getString(c.getColumnIndex("ComponentDiscount")),
                        c.getString(c.getColumnIndex("ComponentDescription")),
                        c.getString(c.getColumnIndex("UserPhone"))));
            }while(c.moveToNext());
        }
        return result;
    }

}
