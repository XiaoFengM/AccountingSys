/**
 * Description: Database, and various methods to modify the database
 */

package PostgreSQLJDBC;
import ProductAndUser.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Database {

    private Connection conn;
    private String SQL;
    private PreparedStatement pstmt;
    private ResultSet rs;


    // datebase connect  and  initialize

/**
 *  Database connection

 * @return java.sql.Connection
 */
    public Connection connect() {
        conn = null;
        try {
           String password = "succeed20";
           String user = "graduate";
           String url = "jdbc:postgresql://pgm-d7olkz6xz875231q3o.pgsql.eu-west-1.rds.aliyuncs.com/postgres";
           conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to database");
        }
        return conn;
    }

/**
 *  Initialize the database, establish the tables and administrator account

 * @return void
 */
    public void initialize() {
        SQL = "CREATE TABLE users" +
                "(UserID		VARCHAR(12)  	NOT NULL," +
                "password		CHAR(40)		NOT NULL," +
                "userType	    VARCHAR(12)	    NOT NULL," +
                "PRIMARY KEY(UserID) );" +
                "Create table Product_information " +
                "(SerialNumber      int   not null, " +
                "PurchasePrice      numeric(10,2)    not null, " +
                "LabelPrice         numeric(10,2)    not null, " +
                "SalePrice          numeric(10,2)     , " +
                "Weight             numeric(8,2)    not null, " +
                "GoldQuality        Char (3)        not null, " +
                "StorageDate        date            not null  default current_date, " +
                "UserID             VARCHAR(12)     , " +
                "SaleDate           date  ," +
                "Primary key (SerialNumber)); " +
                "Create table  Cost " +
                "(id serial not null," +
                "ValueOfCost  numeric(12,2) not null, " +
                "DateOfCost  date    not null  default current_date, " +
                "Notes   varchar (32)   not null," +
                "PRIMARY key (id));" +
                "CREATE table Salary_structure" +
                "(userType          VARCHAR(12)     not null," +
                "baseSalary         int             not null," +
                "perfectAttendance  int             not null," +
                "sale14K            numeric(2,1)    not null," +
                "sale18K            numeric(2,1)    not null," +
                "sale22K            numeric(2,1)    not null," +
                "sale24K            numeric(2,1)    not null," +
                "primary key (userType));" +
                "CREATE table salary" +
                "(UserID         VARCHAR(12)     not null," +
                "year               int             not null," +
                "month              int             not null," +
                "baseSalary         int             not null," +
                "perfectAttendance  int             default 0," +
                "pushSalary14K      numeric(6,1)    default 0," +
                "pushSalary18K      numeric(6,1)    default 0," +
                "pushSalary22K      numeric(6,1)    default 0," +
                "pushSalary24K      numeric(6,1)    default 0," +
                "overtimeSalary     numeric(6,1)    default 0," +
                "deduction          numeric(6,1)    default 0," +
                "Note               varchar(120)," +
                "TotalSalary        numeric(7,1)   default 0," +
                "primary key (UserID,year,month));" +
                "create table employeeAttendance" +
                "(UserID            VARCHAR(12)     not null," +
                "year               int             not null," +
                "month              int             not null," +
                "lastAttendance     int             default 0," +
                "TotalAttendance    int             default 0," +
                "primary key (UserID,year,month));" +
                "INSERT INTO users(UserID,password,userType) " +
                "values('admin','d033e22ae348aeb5660fc2140aec35850c4da997','Admin');" +
                "insert into salary_structure(userType,baseSalary,perfectAttendance,sale14K,sale18K,sale22K,sale24K)" +
                "values ('Clerk',2200,300,0.6,0.8,1,1.2);" +
                "insert into salary_structure(userType,baseSalary,perfectAttendance,sale14K,sale18K,sale22K,sale24K)" +
                "values ('Manager',2800,300,0.6,0.8,1,1.2);";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("wrong to create table");
            e.printStackTrace();
        }
    }

    // login  interface

/**
 *  It is used to verify whether the account password is correct
 * @param userID : Account number entered by the user
 * @param password :  Password entered by the user
 * @return boolean
 */
    public boolean accountCheck(String userID, String password) {
        SQL = "SELECT password AS secret FROM users WHERE userID = ?";
        String rightPassword;
        boolean result = false;
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, userID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                rightPassword = rs.getString("secret");
                if (rightPassword.equals(password)) {
                    result = true;
                } else {
                    result = false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error to get info of account!");
            e.printStackTrace();
        }
        return result;
    }

/**
 *  Return the user account type according to the user account number
 * @param userID :  user account number
 * @return java.lang.String
 */
    public String authorization(String userID) {
        SQL = "SELECT userType AS type FROM users WHERE userID = ?";
        String userType = "";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, userID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                userType = rs.getString("type");
            }
        } catch (SQLException e) {
            System.err.println("Error to get info of account!");
            e.printStackTrace();
        }
        return userType;
    }


    // Product register module

/**
 *  Check whether the product is registered according to the serial number
 * @param serialNumber :  Serial number entered
 * @return boolean
 */
    public boolean ifProductExist(String serialNumber) {
        SQL = "SELECT * FROM product_information where serialNumber = '" + serialNumber + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

/**
 *  Registered product information
 * @param serialNumber : product ID, 8 digits
 * @param purchasePrice : Merchant purchase price
 * @param labelPrice : Product label price
 * @param weight :  Product weight
 * @param goldQuality :  Purity of gold, 14K - 22K, Low - high
 * @return void
 */
    public void productAdd(String serialNumber, String purchasePrice, String labelPrice,
                           String weight, String goldQuality) {
        SQL = "INSERT INTO Product_information(SerialNumber,PurchasePrice,LabelPrice,Weight,GoldQuality) " +
                "values('" + serialNumber + "','" + purchasePrice + "','" + labelPrice + "','" + weight + "','" + goldQuality + "')";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error to add product!");
            e.printStackTrace();
        }
    }

/**
 *  After product registration, it is used to register product cost
 * @param value : product cost, goods payment
 * @param note :  Note the serial number of the product
 * @return void
 */
    public void costAdd(String value, String note) {
        SQL = "insert into cost(ValueOfCost,Notes) values('"+value+"','"+note+"')";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error to add cost!");
            e.printStackTrace();
        }
    }

/**
 *  Modify product information
 * @param oriSerialNumber : Entered serial number
 * @param newSerialNumber : Updated serial number
 * @param purchasePrice : Updated merchant purchase price
 * @param labelPrice : Updated product label price
 * @param weight : Updated product weight
 * @param goldQuality :  Updated product gold quality
 * @return void
 */
    public void correctProduct(String oriSerialNumber, String newSerialNumber, String purchasePrice, String labelPrice, String weight, String goldQuality) {
        SQL = "UPDATE product_information SET serialnumber ='" + newSerialNumber + "',purchasePrice = '" + purchasePrice + "',labelPrice ='" + labelPrice + "',weight='" + weight + "',goldQuality = '" + goldQuality + "' where serialnumber ='" + oriSerialNumber + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error to correct product!");
            e.printStackTrace();
        }
    }

/**
 *  After modifying the product information, it is used to modify the product cost
 * @param oriNote : Original serial number note
 * @param newNote : New serial number note
 * @param newValue :  Updated cost
 * @return void
 */
    public void correctCost(String oriNote, String newNote, String newValue) {
        SQL = "UPDATE Cost SET valueOfCOst = '" + newValue + "',notes = '" + newNote + "' where notes = '" + oriNote + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error to modify cost!");
            e.printStackTrace();
        }
    }

/**
 *  Used to delete products
 * @param serialNumber :  Serial number of the product to be deleted
 * @return void
 */
    public void deleteProduct(String serialNumber) {
        SQL = "DELETE FROM PRODUCT_INFORMATION WHERE serialNumber = '" + serialNumber + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to delete product");
        }
    }

/**
 *  After deleting a product, the corresponding cost is also removed by this method
 * @param note :  Note containing the deleted product serial number
 * @return void
 */
    public void deleteCostAuto(String note) {
        SQL = "DELETE FROM COST WHERE notes = '" + note + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error to delete cost!");
            e.printStackTrace();
        }
    }

/**
 *  Used to obtain all product information in the database
 * @param start : Storage time of products begin
 * @param end :  Storage time of products  end
 * @return java.util.List
 */
    public List getProductInfo(String start, String end) {
        SQL = "select * from Product_information where StorageDate between '" + start + "' and '" + end + "'";
        List list = new ArrayList<Product>();
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setSerialNumber(rs.getInt(1));
                product.setPurchasePrice(rs.getDouble(2));
                product.setLabelPrice(rs.getDouble(3));
                product.setSalePrice(rs.getDouble(4));
                product.setWeight(rs.getDouble(5));
                product.setGoldQuality(rs.getString(6));
                product.setStorageDate(rs.getDate(7));
                product.setSalemanID(rs.getString(8));
                product.setSaleDate(rs.getDate(9));
                list.add(product);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Wrong to get product list!");
            return null;
        }
    }


    // Sale register module

/**
 *  Get product information for a specific product
 * @param product : The object of the product used to obtain information
 * @param serialNumber :  The serial number of the product for which information is required
 * @return void
 */
    public void getProductInfo(Product product, String serialNumber) {
        SQL = "select PurchasePrice,labelPrice,Weight,GoldQuality,SalePrice,userid  from Product_information where SerialNumber = '" + serialNumber + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                product.setPurchasePrice(rs.getDouble(1));
                product.setLabelPrice(rs.getDouble(2));
                product.setWeight(rs.getDouble(3));
                product.setGoldQuality(rs.getString(4));
                product.setSalePrice(rs.getDouble(5));
                product.setSalemanID(rs.getString(6));
            }
        } catch (SQLException e) {
            System.err.println("Failed to obtain the information of the product");
            e.printStackTrace();
        }

    }

/**
 *  It is used to obtain the product information of the products sold by the current user
 * @param salemanID : Account number of the current user
 * @param start : Start date
 * @param end :  end date
 * @return java.util.List
 */
    public List getProductInfo(String salemanID, String start, String end) {
        SQL = "select * from Product_information where userid = '" + salemanID + "' and SaleDate between '" + start + "' and '" + end + "'";
        List list = new ArrayList<Product>();
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setSerialNumber(rs.getInt(1));
                product.setPurchasePrice(rs.getDouble(2));
                product.setLabelPrice(rs.getDouble(3));
                product.setSalePrice(rs.getDouble(4));
                product.setWeight(rs.getDouble(5));
                product.setGoldQuality(rs.getString(6));
                product.setStorageDate(rs.getDate(7));
                product.setSalemanID(rs.getString(8));
                product.setSaleDate(rs.getDate(9));
                list.add(product);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to obtain the information of sold products! ");
            return null;
        }
    }

/**
 *  Used to register product sales information
 * @param serialNumber : product ID
 * @param salePrice : Selling price
 * @param salemanID :  Sales person Id
 * @return void
 */
    public void productSale(int serialNumber, String salePrice, String salemanID) {
        SQL = "UPDATE product_information SET salePrice = '" + salePrice + "',userID = '" + salemanID + "',saledate = current_date where serialNumber = '" + serialNumber + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failure to sell product!");
            e.printStackTrace();
        }

    }

/**
 *  Used to modify product sales information
 * @param serialNumber : Serial number of the product to be modified
 * @param salePrice :  Updated sale price
 * @return void
 */
    public void productSale(String serialNumber, String salePrice) {
        SQL = "UPDATE product_information SET salePrice = '"+salePrice+"' where serialNumber = '"+serialNumber+"'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to update sales information!");
            e.printStackTrace();
        }
    }

    // Cost register module

/**
 *  Used to add cost information manually
 * @param value : the value of cost
 * @param date : the date of cost
 * @param note :  the note of cost
 * @return void
 */
    public void costAdd(String value, String date, String note) {
        SQL = "insert into cost(ValueOfCost,DateOfCost,Notes) values('" + value + "','" + date + "','" + note + "')";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add cost information");
        }
    }

/**
 *  It is used to obtain a certain cost information
 * @param costInfo : The object used to obtain cost information
 * @param ID :  ID corresponding to cost information
 * @return void
 */
    public void getCostInfo(CostInfo costInfo, String ID) {
        SQL = "SELECT * FROM COST WHERE ID = '" + ID + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                costInfo.setValue(rs.getDouble(2));
                costInfo.setDate(rs.getDate(3));
                costInfo.setNote(rs.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to obtain cost information!");
        }
    }

/**
 *  Used to delete cost information
 * @param ID :  ID of cost information to be deleted
 * @return void
 */
    public void deleteCost(String ID) {
        SQL = "DELETE FROM COST WHERE id = '" + ID + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete cost!");
            e.printStackTrace();
        }
    }

/**
 *  Used to modify cost information
 * @param ID : The ID of the cost information to be modified
 * @param value : Updated values
 * @param date : Updated date
 * @param note :  Updated note
 * @return void
 */
    public void correctCost(String ID, String value, String date, String note) {
        SQL = "UPDATE Cost SET valueOfCost = '" + value + "',notes = '" + note + "',dateOfCost = '" + date + "' where id = '" + ID + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to update cost information!");
            e.printStackTrace();
        }
    }

/**
 *  Get all the cost information in the database
 * @param start : start date
 * @param end :  end date
 * @return java.util.List
 */
    public List getCostInfo(String start, String end) {
        SQL = "select * from cost where dateofcost between '" + start + "' and '" + end + "'";
        List list = new ArrayList<CostInfo>();
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                CostInfo costInfo = new CostInfo();
                costInfo.setId(rs.getInt(1));
                costInfo.setValue(rs.getDouble(2));
                costInfo.setDate(rs.getDate(3));
                costInfo.setNote(rs.getString(4));
                list.add(costInfo);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to obtain cost information");
            return null;
        }
    }

    // Profit display module

/**
 *  Calculate the sum of a certain kind of cost information
 * @param start : start date
 * @param end :  end date
 * @param name :  The name of the cost information
 * @return java.lang.String
 */
    public String calculateCost(String start, String end, String name) {
        SQL = "Select SUM(ValueOfCost) from cost where notes like '" + name + "' and  DateOfCost between '" + start + "' and '" + end + "'";
        String result = "";
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            rs.next();
            result = rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to obtain cost information");
        }
        return result;
    }

/**
 * Get the sum of all cost information
 * @param start : start date
 * @param end :  end date
 * @return java.lang.String
 */
    public String totalCost(String start, String end) {
        SQL = "Select SUM(ValueOfCost) from cost where DateOfCost between '" + start + "' and '" + end + "'";
        String result = "";
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            rs.next();
            result = rs.getString(1);
        } catch (SQLException e) {
            System.err.println("Failed to calculate cost value!");
            e.printStackTrace();
        }
        return result;
    }

/**
 * Calculate total sales
 * @param start : start date
 * @param end :  end date
 * @return java.lang.String
 */
    public String calculateSale(String start, String end) {
        SQL = "Select SUM(SalePrice) from Product_information where SalePrice > 0 and  SaleDate between '" + start + "' and '" + end + "'";
        String result = "";
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            rs.next();
            result = rs.getString(1);
        } catch (SQLException e) {
            System.err.println("Failed to calculate sale value!");
            e.printStackTrace();
        }
        return result;
    }

/**
 * Used to calculate profit over a specified period of time
 * @param start :start date
 * @param end : end date
 * @param startYear :startYear
 * @param startMonth :startMonth
 * @param endYear :endYear
 * @param endMonth :endMonth
 * @return java.lang.String
 */
    public String profit(String start, String end,String startYear,String startMonth,String endYear,String endMonth) {
        SQL = "select (select sum(saleprice) from product_information where saledate between '" + start + "' and '" + end + "') - (select sum(valueofcost) from cost where dateofcost between '" + start + "' and '" + end + "')-(select Sum(totalSalary) from salary where year >= '" + startYear + "'and year <='" + endYear + "'and month >= '" + startMonth + "'and month <= '" + endMonth + "')as profit";
        String profit = "";
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            rs.next();
            profit = rs.getString(1);
        } catch (SQLException e) {
            System.err.println("Failed to calculate profit!");
            e.printStackTrace();
        }
        return profit;
    }

    // User manangerment module

/**
 *  Get all the account information in the database
 
 * @return java.util.List
 */
    public List getUserInfo() {
        SQL = "Select userID,userType from users";
        List list = new ArrayList<User>();
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getString(1));
                user.setUserType(rs.getString(2));
                list.add(user);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to obtain account information");
            return null;
        }
    }

/**
 *  Check whether the account exists
 * @param accoutID :Account entered by user
 * @return boolean
 */
    public boolean ifAccountExist(String accoutID) {
        SQL = "SELECT * FROM users where userid = '" + accoutID + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Query failed!");
            return false;
        }
        return false;
    }

/**
 *  Check that the database is initialized  1 for Yes ,0 for not
 
 * @return int
 */
    public int ifInitialize() {
        SQL = "select count(*) from pg_class where relname = 'users'";
        int ifExist = 0;
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
            ifExist = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Query failed!");
            e.printStackTrace();
        }
        return ifExist;
    }

/**
 *  Account registration
 * @param userID : Account number of the new account
 * @param password : Password of the new account
 * @param userType :  Usertype of the new account
 * @return void
 */
    public void accoutRegister(String userID, String password, String userType) {
        SQL = "insert into users(userID,password,userType) values(?,?,?)";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, userID);
            pstmt.setString(2, password);
            pstmt.setString(3, userType);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Account registration failed");
            e.printStackTrace();
        }
    }

    /**
     *  Delete account
     * @param userID :  ID of the account to be deleted
     * @return void
     */
    public void deleteAccount(String userID) {
        SQL = "delete from users where userID = '" + userID + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete cost!");
            e.printStackTrace();
        }
    }

    /**
     *  Change account password or type
     * @param userID : Account number to be modified
    * @param password :new password
    * @param userType :new usertype
     * @return void
     */
    public void correctAccout(String userID, String password, String userType) {
        SQL = "UPDATE users SET password = '" + password + "',usertype = '" + userType + "'where userid = '" + userID + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to modify account information!");
            e.printStackTrace();
        }
    }


    // *******************Salary module***********************
    /**
     *  Check if the salary table exists
     * @param userID : Account ID
     * @param year : the year of  salary table
    * @param month :the month of  salary table
     * @return boolean
     */
    public boolean ifSalaryTableExist(String userID, String year, String month) {
        SQL = "SELECT * FROM salary where userid = '" + userID + "'and year = '" + year + "' and month = '" + month + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to check whether the salary table exists!");
        }
        return false;
    }

    /**
     *  Get salary structure in database
     * @param slaryStructure : The object used to get data
     * @param usertype :Employee type of salary structure to be obtained
     * @return void
     */
    public void getSalaryStructure(SalaryStructure slaryStructure, String usertype) {
        SQL = "SELECT * FROM salary_structure WHERE usertype = '" + usertype + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                slaryStructure.setBaseSalary(rs.getInt(2));
                slaryStructure.setPerfectAttendance(rs.getInt(3));
                slaryStructure.setSale14K(rs.getDouble(4));
                slaryStructure.setSale18K(rs.getDouble(5));
                slaryStructure.setSale22K(rs.getDouble(6));
                slaryStructure.setSale24K(rs.getDouble(7));
            }
        } catch (SQLException e) {
            System.err.println("Failed to obtain salary structure!");
            e.printStackTrace();
        }
    }
    
    /**
     *  Create salary table and attendance table
     * @param userID : User ID
 * @param year : year of the salary table
 * @param month : month of the salary table
 * @param day : day of login
 * @param salaryStructure :  The salary structure on which the salary table is created
     * @return void
     */
    public void createSalaryTable(String userID, String year, String month, String day, SalaryStructure salaryStructure) {
        SQL = "insert into salary(userid,year,month,basesalary) values('" + userID + "','" + year + "','" + month + "','" + salaryStructure.getBaseSalary() + "'); " +
                "insert into employeeAttendance(userID,year,month,lastAttendance,TotalAttendance) values('" + userID + "','" + year + "','" + month + "','" + day + "',1);";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to add salary table or attendance table!");
            e.printStackTrace();
        }
    }

/**
 *  Get total attendance
 * @param userid : Corresponding account ID
 * @param year : year of the attendance
 * @param month :  month of the attendance
 * @return int
 */
    public int getTotalAttendance(String userid, String year, String month) {
        SQL = "select totalAttendance from employeeAttendance where userid = '" + userid + "'and year ='" + year + "' and month = '" + month + "'";
        int totalAttendance = 0;
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                totalAttendance = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to get attendance!");
        }
        return totalAttendance;
    }

/**
 *  Get the day of the last login
 * @param userid : Corresponding account ID
 * @param year : year of attendance
 * @param month :  month of attendance
 * @return int
 */
    public int getLastAttendance(String userid, String year, String month) {
        SQL = "select lastAttendance from employeeAttendance where userid = '" + userid + "'and year ='" + year + "'and month = '" + month + "'";
        int lastAttendance = 0;
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                lastAttendance = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to get last login day!");
        }
        return lastAttendance;
    }

/**
 *  Record attendance
 * @param userID : Corresponding account number
 * @param year : year of attendance table
 * @param month : month of attendace table
 * @param day :  used to record last attendance
 * @return void
 */    
    public void recordAttendance(String userID, String year, String month, String day) {
        SQL = "UPDATE employeeAttendance SET lastAttendance = '" + day + "',TotalAttendance = TotalAttendance + 1  where userID = '" + userID + "'and year ='" + year + "' and month = '" + month + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failure to record attendance");
            e.printStackTrace();
        }
    }
    
/**
 *  For full attendance award
 * @param userID : Employee account number
 * @param year : year of salary table
 * @param month : month of salary table
 * @param salaryStructure :  Salary structure corresponding to employee type
 * @return void
 */    
    public void perfectAttendanceReward(String userID, String year, String month, SalaryStructure salaryStructure) {
        SQL = "UPDATE salary SET perfectattendance = '" + salaryStructure.getPerfectAttendance() + "' where userID = '" + userID + "'and year = '" + year + "' and month = '" + month + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failure to distribute full attendance award!");
        }
    }

/**
 *  Update salary structure
 * @param base : base salary
 * @param attendanceReward : number of full attendance reward
 * @param sale14K : Commission coefficient of 14K sales
 * @param sale18K : Commission coefficient of 18K sales
 * @param sale22K : Commission coefficient of 22K sales
 * @param sale24K : Commission coefficient of 24K sales
 * @param userType :  User type of account
 * @return void
 */
    public void updateSalaryStructure(String base, String attendanceReward, String sale14K, String sale18K, String sale22K, String sale24K, String userType) {
        SQL = "UPDATE salary_structure set baseSalary = '" + base + "',perfectAttendance ='" + attendanceReward + "',sale14k='" + sale14K + "',sale18k='" + sale18K + "',sale22k='" + sale22K + "',sale24k='" + sale24K + "' where usertype ='" + userType + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update salary structure!");
        }
    }

/**
 *  Calculation of salary deducted from absence
 * @param userID : Deduction account number
 * @param year : Year of deduction
 * @param month : Month of deduction
 * @param attendance :  Days of attendance
 * @return void
 */
    public void calculateAbsence(String userID, String year, String month, int attendance) {
        int num = 100 * (20 - attendance);
        String value = "" + num;
        SQL = "update salary set deduction = deduction + '" + value + "', note = 'Absence:" + value + "' where userid ='" + userID + "' and year ='" + year + "' and month ='" + month + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to deduct salary");
        }
//
    }

/**
 * Get salary information
 * @param year : Year of salary table
 * @param month :  Month of salary table
 * @return java.util.List
 */    
    public List getSalaryInfo(String year, String month) {
            SQL = "Select * from salary where year = '" + year + "' and month = '" + month + "' ";
        List list = new ArrayList<Salary>();
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Salary salary = new Salary();
                salary.setUserid(rs.getString(1));
                salary.setYear(rs.getString(2));
                salary.setMonth(rs.getString(3));
                salary.setBaseSalary(rs.getString(4));
                salary.setPerfectAttendance(rs.getString(5));
                salary.setSale14K(rs.getString(6));
                salary.setSale18K(rs.getString(7));
                salary.setSale22K(rs.getString(8));
                salary.setSale24K(rs.getString(9));
                salary.setOverTimeSalary(rs.getString(10));
                salary.setDeduction(rs.getString(11));
                salary.setNote(rs.getString(12));
                salary.setTotalSalary(rs.getString(13));
                list.add(salary);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to obtain salary information!");
            return null;
        }
    }

/**
 *  Vacation approval
 * @param userID : Approved account number
 * @param days : Approved days
 * @param year : Year of attendance
 * @param month :  Month of attendance
 * @return void
 */    
    public void submitVacation(String userID, String days, String year, String month) {
        SQL = "update employeeattendance set totalattendance = totalattendance + '" + days + "' where userid ='" + userID + "' and year ='" + year + "' and month ='" + month + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to approval vacation!");
        }
    }

/**
 *  Approve overtime salary
 * @param userID : Approved account number
 * @param overtimeSalary : Approved overtime payment amount
 * @param year : Year of salary table
 * @param month :  Month of salary table
 * @return void
 */    
    public void submitOvertimeSalary(String userID, String overtimeSalary, String year, String month) {
        SQL = "update salary set overtimesalary = overtimesalary + '" + overtimeSalary + "' where userid ='" + userID + "' and year ='" + year + "' and month ='" + month + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to approve overtime payment!");
        }
    }
    
/**
 *  When the product is sold out, the sales commission is issued
 * @param userID : Account number for obtaining Commission
 * @param year : Year of salary table
 * @param month : Month of salary table
 * @param salaryStructure : Salary structure corresponding to account number
 * @param goldQuality :  Gold quality of products sold
 * @return void
 */    
    public void submitPushSalary(String userID, String year, String month, SalaryStructure salaryStructure, String goldQuality) {
        double pushSalary = 0;
        if (goldQuality.equals("14K")) {
            pushSalary = salaryStructure.getSale14K();
            SQL = "update salary set pushsalary14k = pushsalary14k + '" + pushSalary + "' where userid ='" + userID + "' and year ='" + year + "' and month ='" + month + "'";
        }
        if (goldQuality.equals("18K")) {
            pushSalary = salaryStructure.getSale18K();
            SQL = "update salary set pushsalary18k = pushsalary18k + '" + pushSalary + "' where userid ='" + userID + "' and year ='" + year + "' and month ='" + month + "'";
        }
        if (goldQuality.equals("22K")) {
            pushSalary = salaryStructure.getSale22K();
            SQL = "update salary set pushsalary22k = pushsalary22k + '" + pushSalary + "' where userid ='" + userID + "' and year ='" + year + "' and month ='" + month + "'";
        }
        if (goldQuality.equals("24K")) {
            pushSalary = salaryStructure.getSale24K();
            SQL = "update salary set pushsalary24k = pushsalary24k + '" + pushSalary + "' where userid ='" + userID + "' and year ='" + year + "' and month ='" + month + "'";
        }
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Commission failure!");
        }
    }

/**
 *  Calculate total salary
 * @param userID : Account number to be calculated
 * @param year : Year of salary table
 * @param month :  Month of salary table
 * @return void
 */    
    public void calculateSalary(String userID, String year, String month) {
        SQL = "update salary set TOTALsalary = basesalary + overtimesalary + perfectattendance + pushsalary14k +pushsalary18k +pushsalary22k +pushsalary24k - deduction where userid ='" + userID + "' and year ='" + year + "' and month ='" + month + "'";
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to calculate total salary!");
        }
    }

/**
 *  Calculate the total salary to be paid within the specified time
 * @param startYear : startYear
 * @param startMonth : startMonth
 * @param endYear : endYear
 * @param endMonth :  endMonth
 * @return java.lang.String
 */    
    public String calculateSalaryCost(String startYear, String startMonth, String endYear, String endMonth) {
        SQL = "select Sum(totalSalary) from salary where year >= '" + startYear + "'and year <='" + endYear + "'and month >= '" + startMonth + "'and month <= '" + endMonth + "'";
        String salaryCost = "";
        try {
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                salaryCost = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Total salary calculation failed!");
        }
        return salaryCost;
    }
}
