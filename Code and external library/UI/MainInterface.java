/*
 * Description:The main interface, is completed with the help of JFormdesigner plugins.
 * Because there are too many components, naming and typesetting are a bit confusing
 */
package UI;
import PostgreSQLJDBC.Database;
import ProductAndUser.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;


public class MainInterface extends JFrame {
    private String userID;
    private String userType;
    private Database database;
    Utility utility = new Utility();
    SalaryStructure salaryStructure;
    int flag = 0;
    String serialNumber;
    String start;
    String end;
    String currentYear = utility.currentDate(3);
    String currentMonth = utility.currentDate(2);
    String lastMonth = "" + (Integer.parseInt(currentMonth) - 1);
    String currentDay = utility.currentDate(1);

//  The constructor of admin
    public MainInterface(String userID, String userType, Database database) {
        this.userID = userID;
        this.userType = userType;
        this.database = database;
        initComponents();
        homePanel.remove(salemoudule);
    }

    // The constructor of manager and clerk
    public MainInterface(String userID, String userType, Database database, SalaryStructure salaryStructure) {
        this.userID = userID;
        this.userType = userType;
        this.database = database;
        this.salaryStructure = salaryStructure;
        initComponents();

        //If the salary table exists for current month, then register for attendance
        if (database.ifSalaryTableExist(userID, currentYear, currentMonth)) {
            if (database.getLastAttendance(userID, currentYear, currentMonth) < Integer.parseInt(currentDay)) {
                database.recordAttendance(userID, currentYear, currentMonth, currentDay);
            }
        }
        //If the current month's salary table does not exist, create the current month's salary table and register attendance
        if (!database.ifSalaryTableExist(userID, currentYear, currentMonth)) {
            database.createSalaryTable(userID, currentYear, currentMonth, currentDay, salaryStructure);
            //If the salary table of last month exists, the salary of the current user in the last month is calculated
            if (database.ifSalaryTableExist(userID, currentYear, lastMonth)) {
                int lastMonthAttendance = database.getTotalAttendance(userID, currentYear, lastMonth);
                if (lastMonthAttendance >= 20) {
                    database.perfectAttendanceReward(userID, currentYear, lastMonth, salaryStructure);
                    database.calculateSalary(userID, currentYear, lastMonth);
                } else {

                    database.calculateAbsence(userID, currentYear, lastMonth, lastMonthAttendance);
                    database.calculateSalary(userID, currentYear, lastMonth);
                }
            }
        }
            //Different interfaces are generated according to user types
            if (userType.equals("Clerk")) {
                homePanel.remove(productMoudle);
                homePanel.remove(CostRegister);
                homePanel.remove(profitDisplay);
                homePanel.remove(accountManagement);
                homePanel.remove(salaryMoudle);
            }
            if (userType.equals("Manager")) {
                homePanel.remove(accountManagement);
                salaryMoudle.remove(salaryStructureBuuton);
            }
    }

    private void SaleMouseClicked(MouseEvent e) {
        saleInfoRegister.setVisible(true);
    }

    private void productAdd(MouseEvent e) {
        productInfoRegister.setVisible(true);
    }
    // Submit product information
    private void submitInfo(MouseEvent e) {
        // Verify that the length of the serial number is 8 digits
        if (serialNumber1.getText().length() == 8) {
            //Verify that the serial number has been registered
            if (database.ifProductExist(serialNumber1.getText())) {
                //Modification or deletion of product registration information
                existLabel.setVisible(true);
                update.setVisible(true);
                delete.setVisible(true);
                Product existProduct = new Product();
                database.getProductInfo(existProduct, serialNumber1.getText());
                purchasePrice1.setText("" + existProduct.getPurchasePrice());
                laberPrice1.setText("" + existProduct.getLabelPrice());
                weight1.setText("" + existProduct.getWeight());
                goldQuality1.setSelectedItem(existProduct.getGoldQuality());
                serialNumber1.setEditable(false);
                purchasePrice1.setEditable(false);
                laberPrice1.setEditable(false);
                weight1.setEditable(false);
                goldQuality1.setEnabled(false);
                digitsNote.setVisible(false);
            } else {
                //Product registration
                database.productAdd(serialNumber1.getText(), purchasePrice1.getText(),
                        laberPrice1.getText(), weight1.getText(), goldQuality1.getSelectedItem().toString());

                String note = "Goods Payment: " + serialNumber1.getText();
                database.costAdd(purchasePrice1.getText(), note);
                serialNumber1.setText("");
                purchasePrice1.setText("");
                laberPrice1.setText("");
                weight1.setText("");
                digitsNote.setVisible(false);
            }
        }else{
            digitsNote.setVisible(true);
        }
    }

    // Generate product information table
    private void ProductInfoRefresh(MouseEvent e) {
        start = startTextfield2.getText();
        end = endTextfield2.getText();
        if (start.equals("")){
            start = "2020-1-1";
        }
        if(end.equals("")){
            end = "9999-12-31";
        }
        JTable productTable = new JTable();
        DefaultTableModel myTable;
        myTable = new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "Serial number", "Purchase Price", "Label Price", "Sale Price", "Weight", "Gold Quality", "Storage Date", "SalesmanID","Sale Date"
                }
        ) {
            Class<?>[] columnTypes = new Class<?>[] {
                    Integer.class, Double.class, Double.class, Double.class, Double.class, String.class, Date.class, String.class,Date.class
            };
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        };
        productTable.setModel(myTable);
        productTable.setEnabled(false);
        productInfo.setViewportView(productTable);
        List list = database.getProductInfo(start,end);
        for (int i = 0; i < list.size(); i++) {
            Product product = (Product) list.get(i);
            myTable.addRow(new Object[] { product.getSerialNumber(), product.getPurchasePrice(), product.getLabelPrice(),
                    product.getSalePrice(), product.getWeight(),product.getGoldQuality(),
                    product.getStorageDate(),product.getSalemanID(),product.getSaleDate() });
        }
    }

    // When the product is selling, the product serial number entered is inspected
    private void checkInfoMouseClicked(MouseEvent e) {
        String serialNumber = serialNumber2.getText();
        //If the product has been sold
        if(database.ifProductExist(serialNumber)){
            notExistNote.setVisible(false);
            Product saleProduct = new Product();
            database.getProductInfo(saleProduct,serialNumber);
            labelPrice2.setText(""+saleProduct.getLabelPrice());
            weight2.setText(""+saleProduct.getWeight());
            goldQuality2.setText(saleProduct.getGoldQuality());
            salePrice.setText(""+saleProduct.getSalePrice());
            //And it is sold by the current account, then you have the right to modify the selling price
        if (!salePrice.getText().equals("0.0") && saleProduct.getSalemanID().equals(userID)) {
            correct.setVisible(true);
            salePrice.setEditable(true);
            wrongAccountNote.setVisible(false);
            submitSale.setVisible(false);
        }
        //If it is not sold by the current account, it will not have the right to modify it, and there will be a reminder
        if (!salePrice.getText().equals("0.0") && !saleProduct.getSalemanID().equals(userID)) {
            wrongAccountNote.setVisible(true);
            salePrice.setEditable(false);
            correct.setVisible(false);
            submitSale.setVisible(false);
            }
        //If it is not sold, it can be modified directly
        if (salePrice.getText().equals("0.0")) {
              salePrice.setEditable(true);
              submitSale.setVisible(true);
            }
        }
        //Serial number does not exist, and there is a reminder
        if(!database.ifProductExist(serialNumber)){
            notExistNote.setVisible(true);
            correct.setVisible(false);
            salePrice.setEditable(false);
            submitSale.setVisible(false);
        }
    }

    // Submit information about products sold
    private void SubmitSaleMouseClicked(MouseEvent e) {
        database.productSale(Integer.parseInt(serialNumber2.getText()),salePrice.getText(),userID);
        database.submitPushSalary(userID,currentYear,currentMonth,salaryStructure,goldQuality2.getText());
        saleInfoRegister.dispose();
    }

    // Generate sale product information table
    private void bSaleInfoRefreshMouseClicked(MouseEvent e) {
        start = startTextfield.getText();
        end = endTextfield.getText();
        if (start.equals("")){
            start = "2020-1-1";
        }
        if(end.equals("")){
            end = "9999-12-31";
        }
        JTable saleTable = new JTable();
        DefaultTableModel myTable;
        myTable = new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "Serial number", "Label price", "Sale price", "Weight", "Gold quality", "SalesmanID","Sale Date"
                }
        ) {
            Class<?>[] columnTypes = new Class<?>[] {
                    Integer.class, Double.class, Double.class, Double.class, String.class, String.class,Date.class
            };
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        };
        saleTable.setModel(myTable);
        saleTable.setEnabled(false);
        saleRecord.setViewportView(saleTable);
        List list = database.getProductInfo(userID,start,end);
        for (int i = 0; i < list.size(); i++) {
            Product product = (Product) list.get(i);
            myTable.addRow(new Object[]{product.getSerialNumber(),  product.getLabelPrice(),
                    product.getSalePrice(), product.getWeight(), product.getGoldQuality(),
                     product.getSalemanID(), product.getSaleDate()});
        }
    }

    // Generate cost information table
    private void costInfoRefreshMouseClicked(MouseEvent e) {
        start = startTextfield3.getText();
        end = endTextfield3.getText();
        if (start.equals("")){
            start = "2020-1-1";
        }
        if(end.equals("")){
            end = "9999-12-31";
        }
        JTable costTable = new JTable();
        DefaultTableModel myTable;
        myTable = new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "ID", "Value", "Date", "Note"
                }
        ) {
            Class<?>[] columnTypes = new Class<?>[] {
                    Integer.class, Double.class, Date.class, String.class
            };
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        };
        costTable.setModel(myTable);
        costTable.setEnabled(false);
        costInfo.setViewportView(costTable);
        List list = database.getCostInfo(start,end);
        for (int i = 0; i < list.size(); i++) {
            CostInfo costInfo = (CostInfo) list.get(i);
            myTable.addRow(new Object[]{costInfo.getId(),  costInfo.getValue(),
                    costInfo.getDate(), costInfo.getNote()});
        }

    }

    // add cost button
    private void addCostMouseClicked(MouseEvent e) {
        dateTextfield.setText(utility.currentDate(4));
        costRegister.setVisible(true);
    }

    //submit cost info. Check the input time cannot exceed the current day
    private void costInfoSubmitMouseClicked(MouseEvent e) {
        int monthTypeIn = utility.getMonthAndYearFromDate(dateTextfield.getText(),1);
        int yearTypeIn = utility.getMonthAndYearFromDate(dateTextfield.getText(),2);
        if(monthTypeIn <= Integer.parseInt(currentMonth) && yearTypeIn <= Integer.parseInt(currentYear)){
            database.costAdd(value.getText(),dateTextfield.getText(),note.getSelectedItem().toString());
            value.setText("");
            wrongDateNote.setVisible(false);
        }else{
        wrongDateNote.setVisible(true);
        }
    }



    private void info1KeyTyped(KeyEvent e) {
        utility.serialNumLimit(serialNumber1,e);
        utility.lengthLimit(serialNumber1,8,e);
    }


    private void iSerialNumberKeyTyped(KeyEvent e) {
        utility.serialNumLimit(serialNumber2,e);
        utility.lengthLimit(serialNumber2,8,e);
    }

    private void info2KeyTyped(KeyEvent e) {
        utility.valueLimit(purchasePrice1,e);
        utility.lengthLimit(purchasePrice1,10,e);
    }

    private void info3KeyTyped(KeyEvent e) {
        utility.valueLimit(laberPrice1,e);
        utility.lengthLimit(laberPrice1,10,e);
    }

    private void info4KeyTyped(KeyEvent e) {
        utility.valueLimit(weight1,e);
        utility.lengthLimit(weight1,8,e);
    }

    private void tValueKeyTyped(KeyEvent e) {
        utility.valueLimit(value,e);
        utility.lengthLimit(value,10,e);
    }

    //Calculate various financial information within the specified  period
    private void queryButtonMouseClicked(MouseEvent e) {
        String start = timeBegin.getText();
        String end = timeEnd.getText();
        if (start.equals("")){
            start = "2020-01-01";
        }
        if(end.equals("")){
            end = "9999-12-31";
        }
        String startMonth = ""+ (utility.getMonthAndYearFromDate(start,1)-1);
        String startYear = ""+ utility.getMonthAndYearFromDate(start,2);
        String endMonth = ""+ (utility.getMonthAndYearFromDate(end,1)-1);
        String endYear = ""+ utility.getMonthAndYearFromDate(end,2);
        String note1 = "%Goods%";
        String note2 = "Electric Charge";
        String note3 = "Rent";
        String note4 = "Shop Decoration";
        String note5 = "Other";

        totalCost.setText(database.totalCost(start,end));
        goodPaymentsCost.setText(database.calculateCost(start,end,note1));
        electricCost.setText(database.calculateCost(start,end,note2));
        rentCost.setText(database.calculateCost(start,end,note3));
        shopDecorationCost.setText(database.calculateCost(start,end,note4));
        otherCost.setText(database.calculateCost(start,end,note5));
        saleTotal.setText(database.calculateSale(start,end));
        salaryTextField.setText(database.calculateSalaryCost(startYear,startMonth,endYear,endMonth));
        profitTextField.setText(database.profit(start,end,startYear,startMonth,endYear,endMonth));
    }

    //Generate account information table
    private void accoutRefreshMouseClicked(MouseEvent e) {
        JTable accountTable = new JTable();
        DefaultTableModel myTable;
        myTable = new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "Accout ID", "Accout Type"
                }
        ) {
            Class<?>[] columnTypes = new Class<?>[] {
                    String.class, String.class
            };
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        };
        accountTable.setModel(myTable);
        accountTable.setEnabled(false);
        accoutInfo.setViewportView(accountTable);
        List list = database.getUserInfo();
        for (int i = 0; i < list.size(); i++) {
            User user = (User) list.get(i);
            myTable.addRow(new Object[]{user.getUserID(),user.getUserType()});
        }
    }


// Check the input account information, if correct, register the account
    private void accountSubmitMouseClicked(MouseEvent e) {
        String accountID = accoutIDTextfield.getText();
        String password = new String(passwordField1.getPassword());
        String rePassword = new String(passwordField2.getPassword());

        if (password.length() < 6){
            lengthNote.setVisible(true);
        }else if (!password.equals(rePassword)){
            wrongNote.setVisible(true);
        }else {
            database.accoutRegister(accountID,utility.shaEncrypt(password),accountType.getSelectedItem().toString());
            accoutRegister.dispose();
        }
    }

// Button to change the price of the product sold
    private void correctMouseClicked(MouseEvent e) {
        database.productSale(serialNumber2.getText(),salePrice.getText());
        serialNumber2.setText("");
        salePrice.setText("");
        goldQuality2.setText("");
        labelPrice2.setText("");
        weight2.setText("");
        correct.setVisible(false);
        submitSale.setVisible(true);
    }

// Modify product information of registered products
    private void updateMouseClicked(MouseEvent e) {
        if (flag == 0 ){
            serialNumber = serialNumber1.getText();
            serialNumber1.setEditable(true);
            purchasePrice1.setEditable(true);
            laberPrice1.setEditable(true);
            weight1.setEditable(true);
            goldQuality1.setEnabled(true);
            existLabel.setVisible(false);
            flag = 1;
        } else{
            String oriNote = "Goods Payment: " +serialNumber;
            String newNote = "Goods Payment: " +serialNumber1.getText();
            String newValue = purchasePrice1.getText();
            database.correctProduct(serialNumber,serialNumber1.getText(),purchasePrice1.getText(),
                    laberPrice1.getText(),weight1.getText(),goldQuality1.getSelectedItem().toString());
            database.correctCost(oriNote,newNote,newValue);
            serialNumber1.setText("");
            purchasePrice1.setText("");
            laberPrice1.setText("");
            weight1.setText("");
            correct.setVisible(false);
            delete.setVisible(false);
            update.setVisible(false);
            flag = 0;
        }
    }

    //  Delete product information of registered products
    private void deleteMouseClicked(MouseEvent e) {
        database.deleteProduct(serialNumber1.getText());
        String note = "Goods Payment: "+serialNumber1.getText();
        database.deleteCostAuto(note);
        serialNumber1.setText("");
        serialNumber1.setEditable(true);
        purchasePrice1.setText("");
        purchasePrice1.setEditable(true);
        laberPrice1.setText("");
        laberPrice1.setEditable(true);
        weight1.setText("");
        weight1.setEditable(true);
        correct.setVisible(false);
        delete.setVisible(false);
        existLabel.setVisible(false);
    }

    //Check the ID of the input cost information and display the corresponding data
    private void checkInfo2MouseClicked(MouseEvent e) {
        CostInfo costInfo = new CostInfo();
        String ID = costID.getText();
        database.getCostInfo(costInfo,ID);
        if(costInfo.getValue() == 0){
            wrongNote2.setVisible(true);
            productCostNote.setVisible(false);
        }else{
            if(costInfo.getNote().contains("Goods Payment:")){
                productCostNote.setVisible(true);
            }else {
                productCostNote.setVisible(false);
                wrongNote2.setVisible(false);
                value2.setText("" + costInfo.getValue());
                dateTextfield2.setText("" + costInfo.getDate());
                note2.setSelectedItem(costInfo.getNote());
                value2.setEditable(true);
                dateTextfield2.setEditable(true);
                note2.setEditable(false);
                costID.setEditable(false);
                note2.setEnabled(true);
                update2.setVisible(true);
                delete2.setVisible(true);
            }
        }
    }

    private void editorMouseClicked(MouseEvent e) {
        costInfoModify.setVisible(true);
    }
// Delete registered cost information
    private void delete2MouseClicked(MouseEvent e) {
        String ID = costID.getText();
        database.deleteCost(ID);
        costID.setEditable(true);
        value2.setEditable(false);
        dateTextfield2.setEditable(false);
        note2.setEditable(false);
        costID.setText("");
        value2.setText("");
        dateTextfield2.setText("");
    }

    //Modify registered cost information
    private void update2MouseClicked(MouseEvent e) {
        int monthTypeIn = utility.getMonthAndYearFromDate(dateTextfield2.getText(),1);
        int yearTypeIn = utility.getMonthAndYearFromDate(dateTextfield2.getText(),2);
        if(monthTypeIn <= Integer.parseInt(currentMonth) && yearTypeIn <= Integer.parseInt(currentYear)){
            String ID = costID.getText();
            database.correctCost(ID,value2.getText(),dateTextfield2.getText(),note2.getSelectedItem().toString());
            costInfoModify.dispose();
        }else{
            wrongDateNote2.setVisible(true);
        }
    }

    private void startTextfieldKeyTyped(KeyEvent e) {
        utility.dateLimit(startTextfield,e);
        utility.lengthLimit(startTextfield,10,e);
    }

    private void endTextfieldKeyTyped(KeyEvent e) {
        utility.dateLimit(endTextfield,e);
        utility.lengthLimit(endTextfield,10,e);
    }

    private void startTextfield2KeyTyped(KeyEvent e) {
        utility.dateLimit(startTextfield2,e);
        utility.lengthLimit(startTextfield2,10,e);
    }

    private void endTextfield2KeyTyped(KeyEvent e) {
        utility.dateLimit(endTextfield2,e);
        utility.lengthLimit(endTextfield2,10,e);
    }

    private void startTextfield3KeyTyped(KeyEvent e) {
        utility.dateLimit(startTextfield3,e);
        utility.lengthLimit(startTextfield3,10,e);
    }

    private void endTextfield3KeyTyped(KeyEvent e) {
        utility.dateLimit(endTextfield3,e);
        utility.lengthLimit(endTextfield3,10,e);
    }

    private void timeEndKeyTyped(KeyEvent e) {
        utility.dateLimit(timeEnd,e);
        utility.lengthLimit(timeEnd,10,e);
    }

    private void timeBeginKeyTyped(KeyEvent e) {
        utility.dateLimit(timeBegin,e);
        utility.lengthLimit(timeBegin,10,e);
    }

    private void labelPrice2KeyTyped(KeyEvent e) {
        utility.valueLimit(labelPrice2,e);
        utility.lengthLimit(labelPrice2,6,e);
    }

    private void weight2KeyTyped(KeyEvent e) {
        utility.valueLimit(weight2,e);
        utility.lengthLimit(weight2,7,e);
    }

    private void salePriceKeyTyped(KeyEvent e) {
        utility.valueLimit(salePrice,e);
        utility.lengthLimit(salePrice,10,e);
    }

    private void dateTextfieldKeyTyped(KeyEvent e) {
        utility.dateLimit(dateTextfield,e);
        utility.lengthLimit(dateTextfield,10,e);
    }

    private void dateTextfield2KeyTyped(KeyEvent e) {
        utility.dateLimit(dateTextfield2,e);
        utility.lengthLimit(dateTextfield2,10,e);
    }

    // Close the product information registration window and restore the default state
    private void productInfoRegisterWindowClosed(WindowEvent e) {
        serialNumber1.setText("");
        serialNumber1.setEditable(true);
        purchasePrice1.setText("");
        purchasePrice1.setEditable(true);
        laberPrice1.setText("");
        laberPrice1.setEditable(true);
        weight1.setText("");
        weight1.setEditable(true);
        goldQuality1.setEnabled(true);
        update.setVisible(false);
        delete.setVisible(false);
        existLabel.setVisible(false);
        digitsNote.setVisible(false);
    }

    //Close the sale product information registration window and restore the default state
    private void saleInfoRegisterWindowClosed(WindowEvent e) {
        serialNumber2.setText("");
        salePrice.setText("");
        labelPrice2.setText("");
        weight2.setText("");
        goldQuality2.setText("");
        correct.setVisible(false);
        salePrice.setEditable(false);
        wrongAccountNote.setVisible(false);
        notExistNote.setVisible(false);
    }

    private void costRegisterWindowClosed(WindowEvent e) {
        value.setText("");
        wrongDateNote.setVisible(false);
    }

    //Close the cost information modify window and restore the default state
    private void costInfoModifyWindowClosed(WindowEvent e) {
        costID.setText("");
        costID.setEditable(true);
        value2.setText("");
        value2.setEditable(false);
        dateTextfield2.setText("");
        dateTextfield2.setEditable(false);
        note2.setEnabled(false);
        wrongNote2.setVisible(false);
        productCostNote.setVisible(false);
        delete2.setVisible(false);
        update2.setVisible(false);
        wrongDateNote2.setVisible(false);
    }

    private void accoutIDTextfieldKeyTyped(KeyEvent e) {
        utility.accountIDLimit(accoutIDTextfield,e);
        utility.lengthLimit(accoutIDTextfield,12,e);
    }

// windows close and restore the default state
    private void accoutRegisterWindowClosed(WindowEvent e) {
        accoutIDTextfield.setText("");
        passwordField1.setText("");
        passwordField1.setEditable(false);
        passwordField2.setText("");
        passwordField2.setEditable(false);
        accoutIDTextfield.setEditable(true);
        accountType.setEnabled(false);
        existLabel2.setVisible(false);
        lengthNote.setVisible(false);
        wrongNote.setVisible(false);
        delete3.setVisible(false);
        update3.setVisible(false);
        accountSubmit.setVisible(true);
        rename.setVisible(false);
        accountLengthNote.setVisible(false);
    }

    //Check whether the input account number exists and whether it is in compliance
    private void checkinfo3MouseClicked(MouseEvent e) {
        existLabel2.setVisible(false);
        accoutIDTextfield.setEditable(false);
        String userID = accoutIDTextfield.getText();

        if (userID.length() >= 5) {
            if (!database.ifAccountExist(userID)) {
                passwordField1.setEditable(true);
                passwordField2.setEditable(true);
                accountType.setEnabled(true);
                accountLengthNote.setVisible(false);
            } else {
                accountType.setEnabled(true);
                passwordField1.setEditable(true);
                passwordField2.setEditable(true);
                existLabel2.setVisible(true);
                accoutIDTextfield.setEditable(false);
                update3.setVisible(true);
                accountLengthNote.setVisible(false);
                if(!userID.equals("admin")){
                rename.setVisible(true);
                delete3.setVisible(true);
                }
            }
        }else{
            accountLengthNote.setVisible(true);
            accoutIDTextfield.setEditable(true);
        }
    }

    //Delete account button
    private void delete3MouseClicked(MouseEvent e) {
        String userID = accoutIDTextfield.getText();
        database.deleteAccount(userID);
        accoutIDTextfield.setText("");
        passwordField1.setText("");
        passwordField2.setText("");
        existLabel2.setVisible(false);
        delete3.setVisible(false);
        update3.setVisible(false);
        passwordField1.setEditable(false);
        passwordField2.setEditable(false);
        accountType.setEnabled(false);
        rename.setVisible(false);

    }

    // Verify  the new password entered is compliant, and if so, modify the password and account type
    private void update3MouseClicked(MouseEvent e) {
        String accountID = accoutIDTextfield.getText();
        String password = new String(passwordField1.getPassword());
        String rePassword = new String(passwordField2.getPassword());
        String userType = accountType.getSelectedItem().toString();
        if (password.length() < 6){
            lengthNote.setVisible(true);
        }else if (!password.equals(rePassword)){
            wrongNote.setVisible(true);
        }else {
            database.correctAccout(accountID,password,userType);
            accoutIDTextfield.setText("");
            passwordField1.setText("");
            passwordField2.setText("");
            passwordField1.setEditable(false);
            passwordField2.setEditable(false);
            accountType.setEnabled(false);
            existLabel2.setVisible(false);
            delete3.setVisible(false);
            update3.setVisible(false);
            accoutRegister.setVisible(false);
            rename.setVisible(false);
 }
    }

    //Button to rename account
    private void renameMouseClicked(MouseEvent e) {
        accoutIDTextfield.setEditable(true);
        rename.setVisible(false);
        update3.setVisible(false);
        delete3.setVisible(false);
    }

   //Submit new salary structure
    private void submitSalaryStructureMouseClicked(MouseEvent e) {
        database.updateSalaryStructure(base.getText(),attendanceReward.getText(),sale14K.getText(),sale18K.getText(),sale22K.getText(),sale24K.getText(),userTypes.getSelectedItem().toString());
        salaryStructureDialog.dispose();
    }


// Get the current salary structure and display it
    private void checkInfo4MouseClicked(MouseEvent e) {
        SalaryStructure salaryStructure = new SalaryStructure();
        database.getSalaryStructure(salaryStructure,userTypes.getSelectedItem().toString());
        base.setText(""+salaryStructure.getBaseSalary());
        base.setEditable(true);
        attendanceReward.setText(""+salaryStructure.getPerfectAttendance());
        attendanceReward.setEditable(true);
        sale14K.setText(""+salaryStructure.getSale14K());
        sale14K.setEditable(true);
        sale18K.setText(""+salaryStructure.getSale18K());
        sale18K.setEditable(true);
        sale22K.setText(""+salaryStructure.getSale22K());
        sale22K.setEditable(true);
        sale24K.setText(""+salaryStructure.getSale24K());
        sale24K.setEditable(true);
        submitSalaryStructure.setVisible(true);

    }

    //Close the window and return to the default state
    private void salaryStructureWindowClosed(WindowEvent e) {
        userTypes.setEnabled(true);
        base.setText("");
        base.setEditable(false);
        attendanceReward.setText("");
        attendanceReward.setEditable(false);
        sale14K.setText("");
        sale14K.setEditable(false);
        sale18K.setText("");
        sale18K.setEditable(false);
        sale22K.setText("");
        sale22K.setEditable(false);
        sale24K.setText("");
        sale24K.setEditable(false);
        checkInfo4.setEnabled(true);
        submitSalaryStructure.setVisible(false);
    }



    private void salaryStructureBuutonMouseClicked(MouseEvent e) {
       salaryStructureDialog.setVisible(true);
    }

    private void baseKeyTyped(KeyEvent e) {
        utility.serialNumLimit(base,e);
        utility.lengthLimit(base,4,e);
    }

    private void attendanceRewardKeyTyped(KeyEvent e) {
        utility.serialNumLimit(attendanceReward,e);
        utility.lengthLimit(attendanceReward,3,e);
    }

    private void sale14KKeyTyped(KeyEvent e) {
        utility.valueLimit(sale14K,e);
        utility.lengthLimit(sale14K,3,e);
    }

    private void sale18KKeyTyped(KeyEvent e) {
        utility.valueLimit(sale18K,e);
        utility.lengthLimit(sale18K,3,e);
    }

    private void sale22KKeyTyped(KeyEvent e) {
        utility.valueLimit(sale22K,e);
        utility.lengthLimit(sale22K,3,e);
    }

    private void sale24KKeyTyped(KeyEvent e) {
        utility.valueLimit(sale24K,e);
        utility.lengthLimit(sale24K,3,e);
    }

    //Generate salary table and display
    private void salaryRefreshMouseClicked(MouseEvent e) {
        String year = "";
        String month ="";
        year += year1.getText();
        month += month1.getText();
        if (year.equals("") || month.equals("")){
            errorDialog.setVisible(true);
        }else{
        JTable salaryTable = new JTable();
        DefaultTableModel myTable;
        myTable = new DefaultTableModel(
                new Object[][]{
                },
                new String[]{
                        "UserID", "Base", "PerfectAttn", "14K", "18K", "22K", "24K", "OverTime", "Deduction", "Note", "Total"
                }
        ) {
            Class<?>[] columnTypes = new Class<?>[]{
                    String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        };
        salaryTable.setModel(myTable);
        salaryTable.setEnabled(false);
        salaryInfo.setViewportView(salaryTable);
        List list = database.getSalaryInfo(year,month);
        for (int i = 0; i < list.size(); i++) {
            Salary salary = (Salary) list.get(i);
            myTable.addRow(new Object[]{salary.getUserid(), salary.getBaseSalary(),salary.getPerfectAttendance(),salary.getSale14K(),salary.getSale18K(),salary.getSale22K(),salary.getSale24K(),salary.getOverTimeSalary(),salary.getDeduction(),salary.getNote(),salary.getTotalSalary()});
        }
    }
    }

    //Check whether the entered account number exists
    private void checkUserIDMouseClicked(MouseEvent e) {
        if(database.ifAccountExist(userID2.getText())){
            overtimeOrVacation.setEnabled(true);
            daysOrOvertimeSalary.setEditable(true);
            submitVacationOrOvertime.setVisible(true);
            userID2.setEditable(false);
        }else {
            notExistLabel.setVisible(true);
        }
    }


//Approve overtime pay or leave according to different types of selectionApprove overtime pay or leave according to different types of selection
        private void submitVacationOrOvertimeMouseClicked(MouseEvent e) {
            String year = utility.currentDate(3);
            String month = utility.currentDate(2);

            if(overtimeOrVacation.getSelectedItem().toString().equals("Overtime")){
                database.submitOvertimeSalary(userID2.getText(),daysOrOvertimeSalary.getText(),year,month);
            }else{
                database.submitVacation(userID2.getText(),daysOrOvertimeSalary.getText(),year,month);
            }
            overtimeOrVacationDialog.dispose();
        }

        ////Close the window and return to the default state
        private void overtimeOrVacationDialogWindowClosed(WindowEvent e) {
            userID2.setText("");
            userID2.setEditable(true);
            daysOrOvertimeSalary.setText("");
            daysOrOvertimeSalary.setEditable(false);
            overtimeOrVacation.setEnabled(false);
            submitVacationOrOvertime.setVisible(false);
            notExistLabel.setVisible(false);

        }

        private void overtimeOrVacationButtonMouseClicked(MouseEvent e) {
            overtimeOrVacationDialog.setVisible(true);
        }

        private void addAlterAccountMouseClicked(MouseEvent e) {
            accoutRegister.setVisible(true);
        }

        //According to the type of selection, there are different restrictions on the input
        private void daysOrOvertimeSalaryKeyTyped(KeyEvent e) {
            String type = overtimeOrVacation.getSelectedItem().toString();
            if (type.equals("Overtime")){
                utility.lengthLimit(daysOrOvertimeSalary,4,e);
                utility.serialNumLimit(daysOrOvertimeSalary,e);
            }else{
                utility.lengthLimit(daysOrOvertimeSalary,2,e);
                utility.serialNumLimit(daysOrOvertimeSalary,e);
            }
        }

        private void closeMouseClicked(MouseEvent e) {
            errorDialog.dispose();
        }









    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        homePanel = new JTabbedPane();
        welcome = new JPanel();
        lWelcome = new JLabel();
        salemoudule = new JPanel();
        addSale = new JButton();
        saleRecord = new JScrollPane();
        bSaleInfoRefresh = new JButton();
        startTextfield = new JTextField();
        endTextfield = new JTextField();
        fromLabel1 = new JLabel();
        toLabel1 = new JLabel();
        dateSample1 = new JLabel();
        productMoudle = new JPanel();
        addProduct = new JButton();
        productInfo = new JScrollPane();
        productInfoRefresh = new JButton();
        fromLabel2 = new JLabel();
        startTextfield2 = new JTextField();
        toLabel2 = new JLabel();
        endTextfield2 = new JTextField();
        dateSample2 = new JLabel();
        CostRegister = new JPanel();
        addCost = new JButton();
        costInfo = new JScrollPane();
        costInfoRefresh = new JButton();
        fromLabel3 = new JLabel();
        startTextfield3 = new JTextField();
        toLabel3 = new JLabel();
        endTextfield3 = new JTextField();
        dateSample3 = new JLabel();
        editor = new JButton();
        profitDisplay = new JPanel();
        fromLabel = new JLabel();
        timeBegin = new JTextField();
        toLabel = new JLabel();
        timeEnd = new JTextField();
        queryButton = new JButton();
        totalCostLabel = new JLabel();
        saleLabel = new JLabel();
        goodsPaymentsLabel = new JLabel();
        rentLabel = new JLabel();
        electricFeeLabel = new JLabel();
        shopDecorationLabel = new JLabel();
        otherLabel = new JLabel();
        totalCost = new JTextField();
        goodPaymentsCost = new JTextField();
        rentCost = new JTextField();
        electricCost = new JTextField();
        shopDecorationCost = new JTextField();
        otherCost = new JTextField();
        saleTotal = new JTextField();
        profitLabel = new JLabel();
        profitTextField = new JTextField();
        salaryLabel = new JLabel();
        salaryTextField = new JTextField();
        salaryMoudle = new JPanel();
        salaryRefresh = new JButton();
        salaryInfo = new JScrollPane();
        salaryStructureBuuton = new JButton();
        year = new JLabel();
        month = new JLabel();
        year1 = new JTextField();
        month1 = new JTextField();
        overtimeOrVacationButton = new JButton();
        accountManagement = new JPanel();
        accoutInfo = new JScrollPane();
        accoutRefresh = new JButton();
        addAlterAccount = new JButton();
        productInfoRegister = new JDialog();
        serialNumber1Label = new JLabel();
        purchasePrice1Label = new JLabel();
        labelPrice1Label = new JLabel();
        weight1Label = new JLabel();
        goldQuality1Label = new JLabel();
        serialNumber1 = new JTextField();
        purchasePrice1 = new JTextField();
        laberPrice1 = new JTextField();
        weight1 = new JTextField();
        submitProduct = new JButton();
        goldQuality1 = new JComboBox<>();
        existLabel = new JLabel();
        update = new JButton();
        delete = new JButton();
        digitsNote = new JLabel();
        saleInfoRegister = new JDialog();
        serialNumber2Label = new JLabel();
        serialNumber2 = new JTextField();
        checkInfo = new JButton();
        labelPrice2Label = new JLabel();
        labelPrice2 = new JTextField();
        weight2Label = new JLabel();
        weight2 = new JTextField();
        goldQuality2 = new JTextField();
        goldQuality2Label = new JLabel();
        salePriceLabel = new JLabel();
        salePrice = new JTextField();
        submitSale = new JButton();
        correct = new JButton();
        wrongAccountNote = new JLabel();
        notExistNote = new JLabel();
        costRegister = new JDialog();
        valueLabel = new JLabel();
        value = new JTextField();
        dateLabel = new JLabel();
        noteLabel = new JLabel();
        submitCost = new JButton();
        note = new JComboBox<>();
        dateTextfield = new JTextField();
        wrongDateNote = new JLabel();
        accoutRegister = new JDialog();
        accoutIDLabel = new JLabel();
        passwordLabel = new JLabel();
        password2Label = new JLabel();
        accoutTypeLabel = new JLabel();
        accoutIDTextfield = new JTextField();
        accountType = new JComboBox<>();
        accountSubmit = new JButton();
        wrongNote = new JLabel();
        lengthNote = new JLabel();
        passwordField1 = new JPasswordField();
        passwordField2 = new JPasswordField();
        existLabel2 = new JLabel();
        checkInfo3 = new JButton();
        delete3 = new JButton();
        update3 = new JButton();
        rename = new JButton();
        accountLengthNote = new JLabel();
        costInfoModify = new JDialog();
        lSerialNumber3 = new JLabel();
        costID = new JTextField();
        checkInfo2 = new JButton();
        value2 = new JTextField();
        valueLabel2 = new JLabel();
        dateLabel2 = new JLabel();
        dateTextfield2 = new JTextField();
        noteLabel2 = new JLabel();
        note2 = new JComboBox<>();
        update2 = new JButton();
        delete2 = new JButton();
        wrongNote2 = new JLabel();
        productCostNote = new JLabel();
        wrongDateNote2 = new JLabel();
        salaryStructureDialog = new JDialog();
        salaryList = new JLabel();
        userTypes = new JComboBox<>();
        base = new JTextField();
        attendanceReward = new JTextField();
        sale14K = new JTextField();
        sale18K = new JTextField();
        sale22K = new JTextField();
        sale24K = new JTextField();
        submitSalaryStructure = new JButton();
        checkInfo4 = new JButton();
        overtimeOrVacationDialog = new JDialog();
        overtimeOrVacation = new JComboBox<>();
        userID1 = new JLabel();
        userID2 = new JTextField();
        checkUserID = new JButton();
        label3 = new JLabel();
        daysOrOvertimeSalary = new JTextField();
        submitVacationOrOvertime = new JButton();
        notExistLabel = new JLabel();
        errorDialog = new JDialog();
        error = new JLabel();
        close = new JButton();

        //======== this ========
        setTitle("Accounting System");
        setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== homePanel ========
        {
            homePanel.setFont(homePanel.getFont().deriveFont(homePanel.getFont().getSize() + 5f));

            //======== welcome ========
            {
                welcome.setBorder ( new javax . swing. border .CompoundBorder ( new javax . swing. border .TitledBorder ( new javax . swing. border .
                EmptyBorder ( 0, 0 ,0 , 0) ,  "JFor\u006dDesi\u0067ner \u0045valu\u0061tion" , javax. swing .border . TitledBorder. CENTER ,javax . swing
                . border .TitledBorder . BOTTOM, new java. awt .Font ( "Dia\u006cog", java .awt . Font. BOLD ,12 ) ,
                java . awt. Color .red ) ,welcome. getBorder () ) ); welcome. addPropertyChangeListener( new java. beans .PropertyChangeListener ( )
                { @Override public void propertyChange (java . beans. PropertyChangeEvent e) { if( "bord\u0065r" .equals ( e. getPropertyName () ) )
                throw new RuntimeException( ) ;} } );
                welcome.setLayout(null);

                //---- lWelcome ----
                lWelcome.setText("Welcome,");
                lWelcome.setBackground(new Color(204, 204, 204));
                lWelcome.setOpaque(true);
                lWelcome.setFont(lWelcome.getFont().deriveFont(lWelcome.getFont().getSize() + 21f));
                welcome.add(lWelcome);
                lWelcome.setBounds(60, 45, 725, 275);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < welcome.getComponentCount(); i++) {
                        Rectangle bounds = welcome.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = welcome.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    welcome.setMinimumSize(preferredSize);
                    welcome.setPreferredSize(preferredSize);
                }
            }
            homePanel.addTab("Home", welcome);

            //======== salemoudule ========
            {
                salemoudule.setLayout(null);

                //---- addSale ----
                addSale.setText("ADD+    |    Correct ");
                addSale.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        SaleMouseClicked(e);
                    }
                });
                salemoudule.add(addSale);
                addSale.setBounds(20, 15, 150, 40);
                salemoudule.add(saleRecord);
                saleRecord.setBounds(0, 75, 1060, 420);

                //---- bSaleInfoRefresh ----
                bSaleInfoRefresh.setText("Refresh");
                bSaleInfoRefresh.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        bSaleInfoRefreshMouseClicked(e);
                    }
                });
                salemoudule.add(bSaleInfoRefresh);
                bSaleInfoRefresh.setBounds(855, 15, 90, 40);

                //---- startTextfield ----
                startTextfield.setAutoscrolls(false);
                startTextfield.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        startTextfieldKeyTyped(e);
                    }
                });
                salemoudule.add(startTextfield);
                startTextfield.setBounds(475, 20, 110, 35);

                //---- endTextfield ----
                endTextfield.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        endTextfieldKeyTyped(e);
                    }
                });
                salemoudule.add(endTextfield);
                endTextfield.setBounds(610, 20, 110, 35);

                //---- fromLabel1 ----
                fromLabel1.setText("From:");
                fromLabel1.setFont(fromLabel1.getFont().deriveFont(fromLabel1.getFont().getSize() + 1f));
                salemoudule.add(fromLabel1);
                fromLabel1.setBounds(430, 20, 60, 30);

                //---- toLabel1 ----
                toLabel1.setText("to");
                toLabel1.setFont(toLabel1.getFont().deriveFont(toLabel1.getFont().getSize() + 1f));
                salemoudule.add(toLabel1);
                toLabel1.setBounds(590, 20, 60, 30);

                //---- dateSample1 ----
                dateSample1.setText("(YYYY-MM-DD)");
                dateSample1.setFont(dateSample1.getFont().deriveFont(dateSample1.getFont().getSize() + 1f));
                salemoudule.add(dateSample1);
                dateSample1.setBounds(730, 20, 115, 35);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < salemoudule.getComponentCount(); i++) {
                        Rectangle bounds = salemoudule.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = salemoudule.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    salemoudule.setMinimumSize(preferredSize);
                    salemoudule.setPreferredSize(preferredSize);
                }
            }
            homePanel.addTab("Sale Register", salemoudule);

            //======== productMoudle ========
            {
                productMoudle.setDoubleBuffered(false);
                productMoudle.setFocusable(false);
                productMoudle.setLayout(null);

                //---- addProduct ----
                addProduct.setText("ADD+    |    Correct");
                addProduct.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        productAdd(e);
                    }
                });
                productMoudle.add(addProduct);
                addProduct.setBounds(20, 15, 150, 40);
                productMoudle.add(productInfo);
                productInfo.setBounds(0, 75, 1060, 420);

                //---- productInfoRefresh ----
                productInfoRefresh.setText("Refresh");
                productInfoRefresh.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ProductInfoRefresh(e);
                    }
                });
                productMoudle.add(productInfoRefresh);
                productInfoRefresh.setBounds(855, 15, 90, 40);

                //---- fromLabel2 ----
                fromLabel2.setText("From:");
                fromLabel2.setFont(fromLabel2.getFont().deriveFont(fromLabel2.getFont().getSize() + 1f));
                productMoudle.add(fromLabel2);
                fromLabel2.setBounds(430, 20, 60, 30);

                //---- startTextfield2 ----
                startTextfield2.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        startTextfield2KeyTyped(e);
                    }
                });
                productMoudle.add(startTextfield2);
                startTextfield2.setBounds(475, 20, 110, 35);

                //---- toLabel2 ----
                toLabel2.setText("to");
                toLabel2.setFont(toLabel2.getFont().deriveFont(toLabel2.getFont().getSize() + 1f));
                productMoudle.add(toLabel2);
                toLabel2.setBounds(590, 20, 60, 30);

                //---- endTextfield2 ----
                endTextfield2.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        endTextfield2KeyTyped(e);
                    }
                });
                productMoudle.add(endTextfield2);
                endTextfield2.setBounds(610, 20, 110, 35);

                //---- dateSample2 ----
                dateSample2.setText("(YYYY-MM-DD)");
                dateSample2.setFont(dateSample2.getFont().deriveFont(dateSample2.getFont().getSize() + 1f));
                productMoudle.add(dateSample2);
                dateSample2.setBounds(730, 20, 115, 35);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < productMoudle.getComponentCount(); i++) {
                        Rectangle bounds = productMoudle.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = productMoudle.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    productMoudle.setMinimumSize(preferredSize);
                    productMoudle.setPreferredSize(preferredSize);
                }
            }
            homePanel.addTab("Product Register", productMoudle);

            //======== CostRegister ========
            {
                CostRegister.setLayout(null);

                //---- addCost ----
                addCost.setText("ADD+");
                addCost.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        addCostMouseClicked(e);
                    }
                });
                CostRegister.add(addCost);
                addCost.setBounds(20, 15, 90, 40);
                CostRegister.add(costInfo);
                costInfo.setBounds(0, 75, 1060, 420);

                //---- costInfoRefresh ----
                costInfoRefresh.setText("Refresh");
                costInfoRefresh.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        costInfoRefreshMouseClicked(e);
                    }
                });
                CostRegister.add(costInfoRefresh);
                costInfoRefresh.setBounds(855, 15, 90, 40);

                //---- fromLabel3 ----
                fromLabel3.setText("From:");
                fromLabel3.setFont(fromLabel3.getFont().deriveFont(fromLabel3.getFont().getSize() + 1f));
                CostRegister.add(fromLabel3);
                fromLabel3.setBounds(430, 20, 60, 30);

                //---- startTextfield3 ----
                startTextfield3.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        startTextfield3KeyTyped(e);
                    }
                });
                CostRegister.add(startTextfield3);
                startTextfield3.setBounds(475, 20, 110, 35);

                //---- toLabel3 ----
                toLabel3.setText("to");
                toLabel3.setFont(toLabel3.getFont().deriveFont(toLabel3.getFont().getSize() + 1f));
                CostRegister.add(toLabel3);
                toLabel3.setBounds(590, 20, 60, 30);

                //---- endTextfield3 ----
                endTextfield3.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        endTextfield3KeyTyped(e);
                    }
                });
                CostRegister.add(endTextfield3);
                endTextfield3.setBounds(610, 20, 110, 35);

                //---- dateSample3 ----
                dateSample3.setText("(YYYY-MM-DD)");
                dateSample3.setFont(dateSample3.getFont().deriveFont(dateSample3.getFont().getSize() + 1f));
                CostRegister.add(dateSample3);
                dateSample3.setBounds(730, 20, 115, 35);

                //---- editor ----
                editor.setText("Correct");
                editor.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        editorMouseClicked(e);
                    }
                });
                CostRegister.add(editor);
                editor.setBounds(120, 15, 90, 40);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < CostRegister.getComponentCount(); i++) {
                        Rectangle bounds = CostRegister.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = CostRegister.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    CostRegister.setMinimumSize(preferredSize);
                    CostRegister.setPreferredSize(preferredSize);
                }
            }
            homePanel.addTab("Cost Register", CostRegister);

            //======== profitDisplay ========
            {
                profitDisplay.setLayout(null);

                //---- fromLabel ----
                fromLabel.setText("From:");
                fromLabel.setFont(fromLabel.getFont().deriveFont(fromLabel.getFont().getSize() + 3f));
                profitDisplay.add(fromLabel);
                fromLabel.setBounds(155, 30, 55, 30);

                //---- timeBegin ----
                timeBegin.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        timeBeginKeyTyped(e);
                    }
                });
                profitDisplay.add(timeBegin);
                timeBegin.setBounds(215, 30, 155, 30);

                //---- toLabel ----
                toLabel.setText("to:");
                toLabel.setFont(toLabel.getFont().deriveFont(toLabel.getFont().getSize() + 3f));
                profitDisplay.add(toLabel);
                toLabel.setBounds(380, 30, 55, 30);

                //---- timeEnd ----
                timeEnd.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        timeEndKeyTyped(e);
                    }
                });
                profitDisplay.add(timeEnd);
                timeEnd.setBounds(420, 30, 155, 30);

                //---- queryButton ----
                queryButton.setText("Query");
                queryButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        queryButtonMouseClicked(e);
                    }
                });
                profitDisplay.add(queryButton);
                queryButton.setBounds(640, 30, 100, queryButton.getPreferredSize().height);

                //---- totalCostLabel ----
                totalCostLabel.setText("Cost:");
                totalCostLabel.setFont(totalCostLabel.getFont().deriveFont(totalCostLabel.getFont().getSize() + 3f));
                profitDisplay.add(totalCostLabel);
                totalCostLabel.setBounds(130, 100, 55, 35);

                //---- saleLabel ----
                saleLabel.setText("Sale:");
                saleLabel.setFont(saleLabel.getFont().deriveFont(saleLabel.getFont().getSize() + 3f));
                profitDisplay.add(saleLabel);
                saleLabel.setBounds(420, 100, 55, 35);

                //---- goodsPaymentsLabel ----
                goodsPaymentsLabel.setText("Goods Payments:");
                goodsPaymentsLabel.setFont(goodsPaymentsLabel.getFont().deriveFont(goodsPaymentsLabel.getFont().getSize() + 3f));
                profitDisplay.add(goodsPaymentsLabel);
                goodsPaymentsLabel.setBounds(130, 200, 140, 35);

                //---- rentLabel ----
                rentLabel.setText("Rent:");
                rentLabel.setFont(rentLabel.getFont().deriveFont(rentLabel.getFont().getSize() + 3f));
                profitDisplay.add(rentLabel);
                rentLabel.setBounds(130, 250, 125, 35);

                //---- electricFeeLabel ----
                electricFeeLabel.setText("Electric Charge:");
                electricFeeLabel.setFont(electricFeeLabel.getFont().deriveFont(electricFeeLabel.getFont().getSize() + 3f));
                profitDisplay.add(electricFeeLabel);
                electricFeeLabel.setBounds(130, 300, 125, 35);

                //---- shopDecorationLabel ----
                shopDecorationLabel.setText("Shop Decoration:");
                shopDecorationLabel.setFont(shopDecorationLabel.getFont().deriveFont(shopDecorationLabel.getFont().getSize() + 3f));
                profitDisplay.add(shopDecorationLabel);
                shopDecorationLabel.setBounds(130, 350, 135, 35);

                //---- otherLabel ----
                otherLabel.setText("Other:");
                otherLabel.setFont(otherLabel.getFont().deriveFont(otherLabel.getFont().getSize() + 3f));
                profitDisplay.add(otherLabel);
                otherLabel.setBounds(130, 400, 55, 35);

                //---- totalCost ----
                totalCost.setEditable(false);
                profitDisplay.add(totalCost);
                totalCost.setBounds(280, 100, 105, 35);

                //---- goodPaymentsCost ----
                goodPaymentsCost.setEditable(false);
                profitDisplay.add(goodPaymentsCost);
                goodPaymentsCost.setBounds(280, 200, 105, 35);

                //---- rentCost ----
                rentCost.setEditable(false);
                profitDisplay.add(rentCost);
                rentCost.setBounds(280, 250, 105, 35);

                //---- electricCost ----
                electricCost.setEditable(false);
                profitDisplay.add(electricCost);
                electricCost.setBounds(280, 300, 105, 35);

                //---- shopDecorationCost ----
                shopDecorationCost.setEditable(false);
                profitDisplay.add(shopDecorationCost);
                shopDecorationCost.setBounds(280, 350, 105, 35);

                //---- otherCost ----
                otherCost.setEditable(false);
                profitDisplay.add(otherCost);
                otherCost.setBounds(280, 400, 105, 35);

                //---- saleTotal ----
                saleTotal.setEditable(false);
                profitDisplay.add(saleTotal);
                saleTotal.setBounds(470, 100, 105, 35);

                //---- profitLabel ----
                profitLabel.setText("Profit:");
                profitLabel.setFont(profitLabel.getFont().deriveFont(profitLabel.getFont().getSize() + 3f));
                profitDisplay.add(profitLabel);
                profitLabel.setBounds(635, 100, 55, 35);

                //---- profitTextField ----
                profitTextField.setEditable(false);
                profitDisplay.add(profitTextField);
                profitTextField.setBounds(695, 100, 105, 35);

                //---- salaryLabel ----
                salaryLabel.setText("Salary:");
                salaryLabel.setFont(salaryLabel.getFont().deriveFont(salaryLabel.getFont().getSize() + 3f));
                profitDisplay.add(salaryLabel);
                salaryLabel.setBounds(130, 150, 80, 35);

                //---- salaryTextField ----
                salaryTextField.setEditable(false);
                profitDisplay.add(salaryTextField);
                salaryTextField.setBounds(280, 150, 105, 35);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < profitDisplay.getComponentCount(); i++) {
                        Rectangle bounds = profitDisplay.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = profitDisplay.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    profitDisplay.setMinimumSize(preferredSize);
                    profitDisplay.setPreferredSize(preferredSize);
                }
            }
            homePanel.addTab("Profit Display", profitDisplay);

            //======== salaryMoudle ========
            {
                salaryMoudle.setLayout(null);

                //---- salaryRefresh ----
                salaryRefresh.setText("Refresh");
                salaryRefresh.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        salaryRefreshMouseClicked(e);
                    }
                });
                salaryMoudle.add(salaryRefresh);
                salaryRefresh.setBounds(855, 15, 90, 40);
                salaryMoudle.add(salaryInfo);
                salaryInfo.setBounds(0, 75, 1085, 420);

                //---- salaryStructureBuuton ----
                salaryStructureBuuton.setText("Salary Structure");
                salaryStructureBuuton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        salaryStructureBuutonMouseClicked(e);
                    }
                });
                salaryMoudle.add(salaryStructureBuuton);
                salaryStructureBuuton.setBounds(220, 15, 150, 40);

                //---- year ----
                year.setText("Year\uff1a");
                year.setFont(year.getFont().deriveFont(year.getFont().getSize() + 2f));
                salaryMoudle.add(year);
                year.setBounds(525, 20, 65, 30);

                //---- month ----
                month.setText("Month:");
                month.setFont(month.getFont().deriveFont(month.getFont().getSize() + 2f));
                salaryMoudle.add(month);
                month.setBounds(650, 20, 65, 30);
                salaryMoudle.add(year1);
                year1.setBounds(570, 20, 70, 30);
                salaryMoudle.add(month1);
                month1.setBounds(705, 20, 70, 30);

                //---- overtimeOrVacationButton ----
                overtimeOrVacationButton.setText("OverTime    |    Vacation");
                overtimeOrVacationButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        overtimeOrVacationButtonMouseClicked(e);
                    }
                });
                salaryMoudle.add(overtimeOrVacationButton);
                overtimeOrVacationButton.setBounds(15, 15, 180, 40);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < salaryMoudle.getComponentCount(); i++) {
                        Rectangle bounds = salaryMoudle.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = salaryMoudle.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    salaryMoudle.setMinimumSize(preferredSize);
                    salaryMoudle.setPreferredSize(preferredSize);
                }
            }
            homePanel.addTab("Salary", salaryMoudle);

            //======== accountManagement ========
            {
                accountManagement.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        accountManagementMouseClicked(e);
                    }
                });
                accountManagement.setLayout(null);
                accountManagement.add(accoutInfo);
                accoutInfo.setBounds(0, 75, 1060, 420);

                //---- accoutRefresh ----
                accoutRefresh.setText("Refresh");
                accoutRefresh.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        accoutRefreshMouseClicked(e);
                    }
                });
                accountManagement.add(accoutRefresh);
                accoutRefresh.setBounds(855, 15, 90, 40);

                //---- addAlterAccount ----
                addAlterAccount.setText("ADD+    |     Alter ");
                addAlterAccount.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        addAlterAccountMouseClicked(e);
                    }
                });
                accountManagement.add(addAlterAccount);
                addAlterAccount.setBounds(40, 15, 150, 40);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < accountManagement.getComponentCount(); i++) {
                        Rectangle bounds = accountManagement.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = accountManagement.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    accountManagement.setMinimumSize(preferredSize);
                    accountManagement.setPreferredSize(preferredSize);
                }
            }
            homePanel.addTab("Account Management", accountManagement);
        }
        contentPane.add(homePanel);
        homePanel.setBounds(0, 0, 1060, 530);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        setSize(1060, 560);
        setLocationRelativeTo(null);

        //======== productInfoRegister ========
        {
            productInfoRegister.setTitle("ProductInfo Register");
            productInfoRegister.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            productInfoRegister.setResizable(false);
            productInfoRegister.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    productInfoRegisterWindowClosed(e);
                }
            });
            var productInfoRegisterContentPane = productInfoRegister.getContentPane();
            productInfoRegisterContentPane.setLayout(null);

            //---- serialNumber1Label ----
            serialNumber1Label.setText("Serial Number\uff1a");
            serialNumber1Label.setFont(serialNumber1Label.getFont().deriveFont(serialNumber1Label.getFont().getSize() + 3f));
            productInfoRegisterContentPane.add(serialNumber1Label);
            serialNumber1Label.setBounds(30, 30, 130, 35);

            //---- purchasePrice1Label ----
            purchasePrice1Label.setText("Purchase Price\uff1a");
            purchasePrice1Label.setFont(purchasePrice1Label.getFont().deriveFont(purchasePrice1Label.getFont().getSize() + 3f));
            productInfoRegisterContentPane.add(purchasePrice1Label);
            purchasePrice1Label.setBounds(30, 80, 130, 35);

            //---- labelPrice1Label ----
            labelPrice1Label.setText("Label Price\uff1a");
            labelPrice1Label.setFont(labelPrice1Label.getFont().deriveFont(labelPrice1Label.getFont().getSize() + 3f));
            productInfoRegisterContentPane.add(labelPrice1Label);
            labelPrice1Label.setBounds(30, 130, 130, 35);

            //---- weight1Label ----
            weight1Label.setText("Weight\uff1a");
            weight1Label.setFont(weight1Label.getFont().deriveFont(weight1Label.getFont().getSize() + 3f));
            productInfoRegisterContentPane.add(weight1Label);
            weight1Label.setBounds(30, 180, 130, 35);

            //---- goldQuality1Label ----
            goldQuality1Label.setText("Gold Quality\uff1a");
            goldQuality1Label.setFont(goldQuality1Label.getFont().deriveFont(goldQuality1Label.getFont().getSize() + 3f));
            productInfoRegisterContentPane.add(goldQuality1Label);
            goldQuality1Label.setBounds(30, 230, 130, 35);

            //---- serialNumber1 ----
            serialNumber1.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            serialNumber1.setNextFocusableComponent(purchasePrice1);
            serialNumber1.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    info1KeyTyped(e);
                }
            });
            productInfoRegisterContentPane.add(serialNumber1);
            serialNumber1.setBounds(155, 30, 150, 35);

            //---- purchasePrice1 ----
            purchasePrice1.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    info2KeyTyped(e);
                }
            });
            productInfoRegisterContentPane.add(purchasePrice1);
            purchasePrice1.setBounds(155, 80, 150, 35);

            //---- laberPrice1 ----
            laberPrice1.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    info3KeyTyped(e);
                }
            });
            productInfoRegisterContentPane.add(laberPrice1);
            laberPrice1.setBounds(155, 130, 150, 35);

            //---- weight1 ----
            weight1.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    info4KeyTyped(e);
                }
            });
            productInfoRegisterContentPane.add(weight1);
            weight1.setBounds(155, 180, 150, 35);

            //---- submitProduct ----
            submitProduct.setText("Submit");
            submitProduct.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    submitInfo(e);
                }
            });
            productInfoRegisterContentPane.add(submitProduct);
            submitProduct.setBounds(225, 310, 85, 35);

            //---- goldQuality1 ----
            goldQuality1.setModel(new DefaultComboBoxModel<>(new String[] {
                "24K",
                "22K",
                "18K",
                "14K"
            }));
            productInfoRegisterContentPane.add(goldQuality1);
            goldQuality1.setBounds(155, 230, 150, 35);

            //---- existLabel ----
            existLabel.setText("This Serial Number already exist! Please check it!");
            existLabel.setVisible(false);
            existLabel.setForeground(Color.red);
            productInfoRegisterContentPane.add(existLabel);
            existLabel.setBounds(30, 275, 325, 30);

            //---- update ----
            update.setText("Update");
            update.setVisible(false);
            update.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    updateMouseClicked(e);
                }
            });
            productInfoRegisterContentPane.add(update);
            update.setBounds(25, 310, 85, 35);

            //---- delete ----
            delete.setText("Delete");
            delete.setVisible(false);
            delete.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    deleteMouseClicked(e);
                }
            });
            productInfoRegisterContentPane.add(delete);
            delete.setBounds(125, 310, 85, 35);

            //---- digitsNote ----
            digitsNote.setText("The Serial number is not 8 digits!");
            digitsNote.setForeground(Color.red);
            digitsNote.setVisible(false);
            productInfoRegisterContentPane.add(digitsNote);
            digitsNote.setBounds(25, 270, 270, 28);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < productInfoRegisterContentPane.getComponentCount(); i++) {
                    Rectangle bounds = productInfoRegisterContentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = productInfoRegisterContentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                productInfoRegisterContentPane.setMinimumSize(preferredSize);
                productInfoRegisterContentPane.setPreferredSize(preferredSize);
            }
            productInfoRegister.setSize(385, 415);
            productInfoRegister.setLocationRelativeTo(null);
        }

        //======== saleInfoRegister ========
        {
            saleInfoRegister.setTitle("SaleInfo  Register");
            saleInfoRegister.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            saleInfoRegister.setResizable(false);
            saleInfoRegister.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    saleInfoRegisterWindowClosed(e);
                }
            });
            var saleInfoRegisterContentPane = saleInfoRegister.getContentPane();
            saleInfoRegisterContentPane.setLayout(null);

            //---- serialNumber2Label ----
            serialNumber2Label.setText("Serial Number:");
            serialNumber2Label.setFont(serialNumber2Label.getFont().deriveFont(serialNumber2Label.getFont().getSize() + 3f));
            saleInfoRegisterContentPane.add(serialNumber2Label);
            serialNumber2Label.setBounds(20, 35, 130, 35);

            //---- serialNumber2 ----
            serialNumber2.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    iSerialNumberKeyTyped(e);
                }
            });
            saleInfoRegisterContentPane.add(serialNumber2);
            serialNumber2.setBounds(135, 35, 150, 35);

            //---- checkInfo ----
            checkInfo.setText("Check");
            checkInfo.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    checkInfoMouseClicked(e);
                }
            });
            saleInfoRegisterContentPane.add(checkInfo);
            checkInfo.setBounds(290, 35, 85, 35);

            //---- labelPrice2Label ----
            labelPrice2Label.setText("Label Price\uff1a");
            labelPrice2Label.setFont(labelPrice2Label.getFont().deriveFont(labelPrice2Label.getFont().getSize() + 3f));
            saleInfoRegisterContentPane.add(labelPrice2Label);
            labelPrice2Label.setBounds(20, 85, 130, 35);

            //---- labelPrice2 ----
            labelPrice2.setEditable(false);
            labelPrice2.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    labelPrice2KeyTyped(e);
                }
            });
            saleInfoRegisterContentPane.add(labelPrice2);
            labelPrice2.setBounds(135, 85, 150, 35);

            //---- weight2Label ----
            weight2Label.setText("Weight\uff1a");
            weight2Label.setFont(weight2Label.getFont().deriveFont(weight2Label.getFont().getSize() + 3f));
            saleInfoRegisterContentPane.add(weight2Label);
            weight2Label.setBounds(20, 135, 130, 35);

            //---- weight2 ----
            weight2.setEditable(false);
            weight2.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    weight2KeyTyped(e);
                }
            });
            saleInfoRegisterContentPane.add(weight2);
            weight2.setBounds(135, 135, 150, 35);

            //---- goldQuality2 ----
            goldQuality2.setEditable(false);
            saleInfoRegisterContentPane.add(goldQuality2);
            goldQuality2.setBounds(135, 185, 150, 35);

            //---- goldQuality2Label ----
            goldQuality2Label.setText("Gold Quality\uff1a");
            goldQuality2Label.setFont(goldQuality2Label.getFont().deriveFont(goldQuality2Label.getFont().getSize() + 3f));
            saleInfoRegisterContentPane.add(goldQuality2Label);
            goldQuality2Label.setBounds(20, 185, 130, 35);

            //---- salePriceLabel ----
            salePriceLabel.setText("Sale Price");
            salePriceLabel.setFont(salePriceLabel.getFont().deriveFont(salePriceLabel.getFont().getSize() + 3f));
            saleInfoRegisterContentPane.add(salePriceLabel);
            salePriceLabel.setBounds(20, 235, 130, 35);

            //---- salePrice ----
            salePrice.setEditable(false);
            salePrice.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    salePriceKeyTyped(e);
                }
            });
            saleInfoRegisterContentPane.add(salePrice);
            salePrice.setBounds(135, 235, 150, 35);

            //---- submitSale ----
            submitSale.setText("Submit");
            submitSale.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    SubmitSaleMouseClicked(e);
                }
            });
            saleInfoRegisterContentPane.add(submitSale);
            submitSale.setBounds(290, 315, 85, 35);

            //---- correct ----
            correct.setText("Correct");
            correct.setVisible(false);
            correct.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    correctMouseClicked(e);
                }
            });
            saleInfoRegisterContentPane.add(correct);
            correct.setBounds(195, 285, 85, 35);

            //---- wrongAccountNote ----
            wrongAccountNote.setText("The product has been sold, but not by the previous account!");
            wrongAccountNote.setForeground(Color.red);
            wrongAccountNote.setVisible(false);
            saleInfoRegisterContentPane.add(wrongAccountNote);
            wrongAccountNote.setBounds(20, 280, 365, 35);

            //---- notExistNote ----
            notExistNote.setText("Serial number doesn't exist!");
            notExistNote.setForeground(Color.red);
            notExistNote.setVisible(false);
            saleInfoRegisterContentPane.add(notExistNote);
            notExistNote.setBounds(35, 280, 220, 35);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < saleInfoRegisterContentPane.getComponentCount(); i++) {
                    Rectangle bounds = saleInfoRegisterContentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = saleInfoRegisterContentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                saleInfoRegisterContentPane.setMinimumSize(preferredSize);
                saleInfoRegisterContentPane.setPreferredSize(preferredSize);
            }
            saleInfoRegister.setSize(405, 390);
            saleInfoRegister.setLocationRelativeTo(null);
        }

        //======== costRegister ========
        {
            costRegister.setTitle("Cost Info Register");
            costRegister.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            costRegister.setResizable(false);
            costRegister.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    costRegisterWindowClosed(e);
                }
            });
            var costRegisterContentPane = costRegister.getContentPane();
            costRegisterContentPane.setLayout(null);

            //---- valueLabel ----
            valueLabel.setText("Value:");
            valueLabel.setFont(valueLabel.getFont().deriveFont(valueLabel.getFont().getSize() + 3f));
            costRegisterContentPane.add(valueLabel);
            valueLabel.setBounds(50, 40, 70, 40);

            //---- value ----
            value.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    tValueKeyTyped(e);
                }
            });
            costRegisterContentPane.add(value);
            value.setBounds(115, 40, 170, 35);

            //---- dateLabel ----
            dateLabel.setText("Date:");
            dateLabel.setFont(dateLabel.getFont().deriveFont(dateLabel.getFont().getSize() + 3f));
            costRegisterContentPane.add(dateLabel);
            dateLabel.setBounds(50, 100, 70, 40);

            //---- noteLabel ----
            noteLabel.setText("Note:");
            noteLabel.setFont(noteLabel.getFont().deriveFont(noteLabel.getFont().getSize() + 3f));
            costRegisterContentPane.add(noteLabel);
            noteLabel.setBounds(50, 160, 70, 40);

            //---- submitCost ----
            submitCost.setText("Submit");
            submitCost.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    costInfoSubmitMouseClicked(e);
                }
            });
            costRegisterContentPane.add(submitCost);
            submitCost.setBounds(205, 230, 85, 35);

            //---- note ----
            note.setModel(new DefaultComboBoxModel<>(new String[] {
                "Electric Charge",
                "Goods Payment",
                "Rent",
                "Shop Decoration",
                "Other"
            }));
            costRegisterContentPane.add(note);
            note.setBounds(115, 160, 170, 35);

            //---- dateTextfield ----
            dateTextfield.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    dateTextfieldKeyTyped(e);
                }
            });
            costRegisterContentPane.add(dateTextfield);
            dateTextfield.setBounds(115, 100, 170, 35);

            //---- wrongDateNote ----
            wrongDateNote.setText("Incorrect date input!");
            wrongDateNote.setForeground(Color.red);
            wrongDateNote.setVisible(false);
            costRegisterContentPane.add(wrongDateNote);
            wrongDateNote.setBounds(45, 205, 210, 25);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < costRegisterContentPane.getComponentCount(); i++) {
                    Rectangle bounds = costRegisterContentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = costRegisterContentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                costRegisterContentPane.setMinimumSize(preferredSize);
                costRegisterContentPane.setPreferredSize(preferredSize);
            }
            costRegister.setSize(360, 310);
            costRegister.setLocationRelativeTo(null);
        }

        //======== accoutRegister ========
        {
            accoutRegister.setTitle("Accout Register");
            accoutRegister.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            accoutRegister.setResizable(false);
            accoutRegister.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    accoutRegisterWindowClosed(e);
                }
            });
            var accoutRegisterContentPane = accoutRegister.getContentPane();
            accoutRegisterContentPane.setLayout(null);

            //---- accoutIDLabel ----
            accoutIDLabel.setText("Accout ID:");
            accoutIDLabel.setFont(accoutIDLabel.getFont().deriveFont(accoutIDLabel.getFont().getSize() + 3f));
            accoutIDLabel.setToolTipText("Less than 12 char");
            accoutRegisterContentPane.add(accoutIDLabel);
            accoutIDLabel.setBounds(40, 50, 85, 35);

            //---- passwordLabel ----
            passwordLabel.setText("Password:");
            passwordLabel.setFont(passwordLabel.getFont().deriveFont(passwordLabel.getFont().getSize() + 3f));
            accoutRegisterContentPane.add(passwordLabel);
            passwordLabel.setBounds(40, 100, 85, 35);

            //---- password2Label ----
            password2Label.setText("Password:");
            password2Label.setFont(password2Label.getFont().deriveFont(password2Label.getFont().getSize() + 3f));
            accoutRegisterContentPane.add(password2Label);
            password2Label.setBounds(40, 150, 85, 35);

            //---- accoutTypeLabel ----
            accoutTypeLabel.setText("Accout Type:");
            accoutTypeLabel.setFont(accoutTypeLabel.getFont().deriveFont(accoutTypeLabel.getFont().getSize() + 3f));
            accoutRegisterContentPane.add(accoutTypeLabel);
            accoutTypeLabel.setBounds(40, 200, 115, 35);

            //---- accoutIDTextfield ----
            accoutIDTextfield.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    accoutIDTextfieldKeyTyped(e);
                }
            });
            accoutRegisterContentPane.add(accoutIDTextfield);
            accoutIDTextfield.setBounds(140, 50, 150, 35);

            //---- accountType ----
            accountType.setModel(new DefaultComboBoxModel<>(new String[] {
                "Clerk",
                "Manager",
                "Admin"
            }));
            accountType.setEnabled(false);
            accoutRegisterContentPane.add(accountType);
            accountType.setBounds(140, 200, 150, 35);

            //---- accountSubmit ----
            accountSubmit.setText("Submit");
            accountSubmit.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    accountSubmitMouseClicked(e);
                }
            });
            accoutRegisterContentPane.add(accountSubmit);
            accountSubmit.setBounds(295, 300, 85, 35);

            //---- wrongNote ----
            wrongNote.setText("Two passwords are inconsistent!");
            wrongNote.setForeground(Color.red);
            wrongNote.setEnabled(false);
            wrongNote.setVisible(false);
            accoutRegisterContentPane.add(wrongNote);
            wrongNote.setBounds(35, 240, 205, 25);

            //---- lengthNote ----
            lengthNote.setText("The length of the password should be greater than 6 digits");
            lengthNote.setForeground(Color.red);
            lengthNote.setEnabled(false);
            lengthNote.setVisible(false);
            accoutRegisterContentPane.add(lengthNote);
            lengthNote.setBounds(5, 270, 375, lengthNote.getPreferredSize().height);

            //---- passwordField1 ----
            passwordField1.setEditable(false);
            accoutRegisterContentPane.add(passwordField1);
            passwordField1.setBounds(140, 100, 150, 35);

            //---- passwordField2 ----
            passwordField2.setEditable(false);
            accoutRegisterContentPane.add(passwordField2);
            passwordField2.setBounds(140, 150, 150, 35);

            //---- existLabel2 ----
            existLabel2.setText("ID already exsit!");
            existLabel2.setForeground(Color.red);
            existLabel2.setVisible(false);
            accoutRegisterContentPane.add(existLabel2);
            existLabel2.setBounds(45, 250, 255, 35);

            //---- checkInfo3 ----
            checkInfo3.setText("Check");
            checkInfo3.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    checkinfo3MouseClicked(e);
                }
            });
            accoutRegisterContentPane.add(checkInfo3);
            checkInfo3.setBounds(295, 50, 85, 35);

            //---- delete3 ----
            delete3.setText("Delete");
            delete3.setVisible(false);
            delete3.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    delete3MouseClicked(e);
                }
            });
            accoutRegisterContentPane.add(delete3);
            delete3.setBounds(45, 300, 85, 35);

            //---- update3 ----
            update3.setText("Update");
            update3.setVisible(false);
            update3.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    update3MouseClicked(e);
                }
            });
            accoutRegisterContentPane.add(update3);
            update3.setBounds(165, 300, 85, 35);

            //---- rename ----
            rename.setText("Rename");
            rename.setVisible(false);
            rename.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    renameMouseClicked(e);
                }
            });
            accoutRegisterContentPane.add(rename);
            rename.setBounds(385, 50, 85, 35);

            //---- accountLengthNote ----
            accountLengthNote.setText("The length of accountID must greater than 5 !");
            accountLengthNote.setForeground(Color.red);
            accountLengthNote.setVisible(false);
            accoutRegisterContentPane.add(accountLengthNote);
            accountLengthNote.setBounds(35, 270, 340, 25);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < accoutRegisterContentPane.getComponentCount(); i++) {
                    Rectangle bounds = accoutRegisterContentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = accoutRegisterContentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                accoutRegisterContentPane.setMinimumSize(preferredSize);
                accoutRegisterContentPane.setPreferredSize(preferredSize);
            }
            accoutRegister.setSize(485, 405);
            accoutRegister.setLocationRelativeTo(accoutRegister.getOwner());
        }

        //======== costInfoModify ========
        {
            costInfoModify.setTitle("Cost Info modification");
            costInfoModify.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            costInfoModify.setResizable(false);
            costInfoModify.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    costInfoModifyWindowClosed(e);
                }
            });
            var costInfoModifyContentPane = costInfoModify.getContentPane();
            costInfoModifyContentPane.setLayout(null);

            //---- lSerialNumber3 ----
            lSerialNumber3.setText("ID:");
            lSerialNumber3.setFont(lSerialNumber3.getFont().deriveFont(lSerialNumber3.getFont().getSize() + 3f));
            costInfoModifyContentPane.add(lSerialNumber3);
            lSerialNumber3.setBounds(45, 30, 130, 35);

            //---- costID ----
            costID.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    iSerialNumberKeyTyped(e);
                }
            });
            costInfoModifyContentPane.add(costID);
            costID.setBounds(145, 30, 150, 35);

            //---- checkInfo2 ----
            checkInfo2.setText("Check");
            checkInfo2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    checkInfo2MouseClicked(e);
                }
            });
            costInfoModifyContentPane.add(checkInfo2);
            checkInfo2.setBounds(305, 30, 85, 35);

            //---- value2 ----
            value2.setEditable(false);
            value2.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    info2KeyTyped(e);
                }
            });
            costInfoModifyContentPane.add(value2);
            value2.setBounds(145, 80, 150, 35);

            //---- valueLabel2 ----
            valueLabel2.setText("Value:");
            valueLabel2.setFont(valueLabel2.getFont().deriveFont(valueLabel2.getFont().getSize() + 3f));
            costInfoModifyContentPane.add(valueLabel2);
            valueLabel2.setBounds(45, 80, 70, 40);

            //---- dateLabel2 ----
            dateLabel2.setText("Date:");
            dateLabel2.setFont(dateLabel2.getFont().deriveFont(dateLabel2.getFont().getSize() + 3f));
            costInfoModifyContentPane.add(dateLabel2);
            dateLabel2.setBounds(45, 130, 70, 40);

            //---- dateTextfield2 ----
            dateTextfield2.setEditable(false);
            dateTextfield2.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    dateTextfield2KeyTyped(e);
                }
            });
            costInfoModifyContentPane.add(dateTextfield2);
            dateTextfield2.setBounds(145, 130, 150, 35);

            //---- noteLabel2 ----
            noteLabel2.setText("Note:");
            noteLabel2.setFont(noteLabel2.getFont().deriveFont(noteLabel2.getFont().getSize() + 3f));
            costInfoModifyContentPane.add(noteLabel2);
            noteLabel2.setBounds(45, 180, 70, 40);

            //---- note2 ----
            note2.setModel(new DefaultComboBoxModel<>(new String[] {
                "Electric Charge",
                "Goods Payment",
                "Rent",
                "Shop Decoration",
                "Other"
            }));
            note2.setEnabled(false);
            costInfoModifyContentPane.add(note2);
            note2.setBounds(145, 185, 150, 35);

            //---- update2 ----
            update2.setText("Update");
            update2.setVisible(false);
            update2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    update2MouseClicked(e);
                }
            });
            costInfoModifyContentPane.add(update2);
            update2.setBounds(305, 265, 85, 35);

            //---- delete2 ----
            delete2.setText("Delete");
            delete2.setVisible(false);
            delete2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    delete2MouseClicked(e);
                }
            });
            costInfoModifyContentPane.add(delete2);
            delete2.setBounds(195, 265, 85, 35);

            //---- wrongNote2 ----
            wrongNote2.setText("ID doesn't exsit!");
            wrongNote2.setForeground(Color.red);
            wrongNote2.setFont(wrongNote2.getFont().deriveFont(wrongNote2.getFont().getSize() + 2f));
            wrongNote2.setVisible(false);
            costInfoModifyContentPane.add(wrongNote2);
            wrongNote2.setBounds(45, 230, 375, 30);

            //---- productCostNote ----
            productCostNote.setText("The cost of the Product should be modified from the Product Register!");
            productCostNote.setForeground(Color.red);
            productCostNote.setVisible(false);
            costInfoModifyContentPane.add(productCostNote);
            productCostNote.setBounds(5, 275, 540, 30);

            //---- wrongDateNote2 ----
            wrongDateNote2.setText("Wrong date input\uff01");
            wrongDateNote2.setForeground(Color.red);
            wrongDateNote2.setVisible(false);
            costInfoModifyContentPane.add(wrongDateNote2);
            wrongDateNote2.setBounds(35, 225, 245, 35);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < costInfoModifyContentPane.getComponentCount(); i++) {
                    Rectangle bounds = costInfoModifyContentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = costInfoModifyContentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                costInfoModifyContentPane.setMinimumSize(preferredSize);
                costInfoModifyContentPane.setPreferredSize(preferredSize);
            }
            costInfoModify.pack();
            costInfoModify.setLocationRelativeTo(costInfoModify.getOwner());
        }

        //======== salaryStructureDialog ========
        {
            salaryStructureDialog.setTitle("Salary Structure");
            salaryStructureDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            salaryStructureDialog.setResizable(false);
            salaryStructureDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    salaryStructureWindowClosed(e);
                }
            });
            var salaryStructureDialogContentPane = salaryStructureDialog.getContentPane();
            salaryStructureDialogContentPane.setLayout(null);

            //---- salaryList ----
            salaryList.setText("Base       AttendanceReward       Sale14K       Sale18K       Sale22K       Sale24K   ");
            salaryStructureDialogContentPane.add(salaryList);
            salaryList.setBounds(145, 30, 495, 40);

            //---- userTypes ----
            userTypes.setModel(new DefaultComboBoxModel<>(new String[] {
                "Clerk",
                "Manager"
            }));
            salaryStructureDialogContentPane.add(userTypes);
            userTypes.setBounds(15, 70, 90, 30);

            //---- base ----
            base.setEditable(false);
            base.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    baseKeyTyped(e);
                }
            });
            salaryStructureDialogContentPane.add(base);
            base.setBounds(125, 70, 60, 30);

            //---- attendanceReward ----
            attendanceReward.setEditable(false);
            attendanceReward.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    attendanceRewardKeyTyped(e);
                }
            });
            salaryStructureDialogContentPane.add(attendanceReward);
            attendanceReward.setBounds(215, 70, 60, 30);

            //---- sale14K ----
            sale14K.setEditable(false);
            sale14K.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    sale14KKeyTyped(e);
                }
            });
            salaryStructureDialogContentPane.add(sale14K);
            sale14K.setBounds(325, 70, 60, 30);

            //---- sale18K ----
            sale18K.setEditable(false);
            sale18K.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    sale18KKeyTyped(e);
                }
            });
            salaryStructureDialogContentPane.add(sale18K);
            sale18K.setBounds(400, 70, 60, 30);

            //---- sale22K ----
            sale22K.setEditable(false);
            sale22K.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    sale22KKeyTyped(e);
                }
            });
            salaryStructureDialogContentPane.add(sale22K);
            sale22K.setBounds(475, 70, 60, 30);

            //---- sale24K ----
            sale24K.setEditable(false);
            sale24K.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    sale24KKeyTyped(e);
                }
            });
            salaryStructureDialogContentPane.add(sale24K);
            sale24K.setBounds(550, 70, 60, 30);

            //---- submitSalaryStructure ----
            submitSalaryStructure.setText("Submit");
            submitSalaryStructure.setVisible(false);
            submitSalaryStructure.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    submitSalaryStructureMouseClicked(e);
                }
            });
            salaryStructureDialogContentPane.add(submitSalaryStructure);
            submitSalaryStructure.setBounds(530, 120, 85, 35);

            //---- checkInfo4 ----
            checkInfo4.setText("Check");
            checkInfo4.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    checkInfo4MouseClicked(e);
                }
            });
            salaryStructureDialogContentPane.add(checkInfo4);
            checkInfo4.setBounds(630, 65, 85, 35);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < salaryStructureDialogContentPane.getComponentCount(); i++) {
                    Rectangle bounds = salaryStructureDialogContentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = salaryStructureDialogContentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                salaryStructureDialogContentPane.setMinimumSize(preferredSize);
                salaryStructureDialogContentPane.setPreferredSize(preferredSize);
            }
            salaryStructureDialog.setSize(735, 210);
            salaryStructureDialog.setLocationRelativeTo(salaryStructureDialog.getOwner());
        }

        //======== overtimeOrVacationDialog ========
        {
            overtimeOrVacationDialog.setTitle("Overtime   or  Vacation");
            overtimeOrVacationDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            overtimeOrVacationDialog.setResizable(false);
            overtimeOrVacationDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    overtimeOrVacationDialogWindowClosed(e);
                }
            });
            var overtimeOrVacationDialogContentPane = overtimeOrVacationDialog.getContentPane();
            overtimeOrVacationDialogContentPane.setLayout(null);

            //---- overtimeOrVacation ----
            overtimeOrVacation.setModel(new DefaultComboBoxModel<>(new String[] {
                "Overtime",
                "Vacation"
            }));
            overtimeOrVacation.setEnabled(false);
            overtimeOrVacationDialogContentPane.add(overtimeOrVacation);
            overtimeOrVacation.setBounds(30, 110, 90, 35);

            //---- userID1 ----
            userID1.setText("UserID:");
            overtimeOrVacationDialogContentPane.add(userID1);
            userID1.setBounds(35, 30, 75, 35);
            overtimeOrVacationDialogContentPane.add(userID2);
            userID2.setBounds(90, 30, 130, 35);

            //---- checkUserID ----
            checkUserID.setText("Check");
            checkUserID.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    checkUserIDMouseClicked(e);
                }
            });
            overtimeOrVacationDialogContentPane.add(checkUserID);
            checkUserID.setBounds(240, 30, 85, 35);

            //---- label3 ----
            label3.setText("Days / OvertimeSalary:");
            overtimeOrVacationDialogContentPane.add(label3);
            label3.setBounds(135, 110, 165, 35);

            //---- daysOrOvertimeSalary ----
            daysOrOvertimeSalary.setEditable(false);
            daysOrOvertimeSalary.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    daysOrOvertimeSalaryKeyTyped(e);
                }
            });
            overtimeOrVacationDialogContentPane.add(daysOrOvertimeSalary);
            daysOrOvertimeSalary.setBounds(285, 110, 80, 35);

            //---- submitVacationOrOvertime ----
            submitVacationOrOvertime.setText("Submit");
            submitVacationOrOvertime.setVisible(false);
            submitVacationOrOvertime.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    submitVacationOrOvertimeMouseClicked(e);
                }
            });
            overtimeOrVacationDialogContentPane.add(submitVacationOrOvertime);
            submitVacationOrOvertime.setBounds(285, 160, 85, 35);

            //---- notExistLabel ----
            notExistLabel.setText("UserID not exist!");
            notExistLabel.setForeground(Color.red);
            notExistLabel.setVisible(false);
            overtimeOrVacationDialogContentPane.add(notExistLabel);
            notExistLabel.setBounds(40, 65, 130, 40);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < overtimeOrVacationDialogContentPane.getComponentCount(); i++) {
                    Rectangle bounds = overtimeOrVacationDialogContentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = overtimeOrVacationDialogContentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                overtimeOrVacationDialogContentPane.setMinimumSize(preferredSize);
                overtimeOrVacationDialogContentPane.setPreferredSize(preferredSize);
            }
            overtimeOrVacationDialog.setSize(480, 250);
            overtimeOrVacationDialog.setLocationRelativeTo(overtimeOrVacationDialog.getOwner());
        }

        //======== errorDialog ========
        {
            errorDialog.setTitle("Error!");
            errorDialog.setResizable(false);
            var errorDialogContentPane = errorDialog.getContentPane();
            errorDialogContentPane.setLayout(null);

            //---- error ----
            error.setText("Please enter the year and month\uff01");
            error.setFont(error.getFont().deriveFont(error.getFont().getSize() + 3f));
            errorDialogContentPane.add(error);
            error.setBounds(15, 25, 295, 40);

            //---- close ----
            close.setText("Close");
            close.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    closeMouseClicked(e);
                }
            });
            errorDialogContentPane.add(close);
            close.setBounds(new Rectangle(new Point(205, 75), close.getPreferredSize()));

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < errorDialogContentPane.getComponentCount(); i++) {
                    Rectangle bounds = errorDialogContentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = errorDialogContentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                errorDialogContentPane.setMinimumSize(preferredSize);
                errorDialogContentPane.setPreferredSize(preferredSize);
            }
            errorDialog.setSize(330, 165);
            errorDialog.setLocationRelativeTo(errorDialog.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void accountManagementMouseClicked(MouseEvent e) {
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JTabbedPane homePanel;
    private JPanel welcome;
    private JLabel lWelcome;
    private JPanel salemoudule;
    private JButton addSale;
    private JScrollPane saleRecord;
    private JButton bSaleInfoRefresh;
    private JTextField startTextfield;
    private JTextField endTextfield;
    private JLabel fromLabel1;
    private JLabel toLabel1;
    private JLabel dateSample1;
    private JPanel productMoudle;
    private JButton addProduct;
    private JScrollPane productInfo;
    private JButton productInfoRefresh;
    private JLabel fromLabel2;
    private JTextField startTextfield2;
    private JLabel toLabel2;
    private JTextField endTextfield2;
    private JLabel dateSample2;
    private JPanel CostRegister;
    private JButton addCost;
    private JScrollPane costInfo;
    private JButton costInfoRefresh;
    private JLabel fromLabel3;
    private JTextField startTextfield3;
    private JLabel toLabel3;
    private JTextField endTextfield3;
    private JLabel dateSample3;
    private JButton editor;
    private JPanel profitDisplay;
    private JLabel fromLabel;
    private JTextField timeBegin;
    private JLabel toLabel;
    private JTextField timeEnd;
    private JButton queryButton;
    private JLabel totalCostLabel;
    private JLabel saleLabel;
    private JLabel goodsPaymentsLabel;
    private JLabel rentLabel;
    private JLabel electricFeeLabel;
    private JLabel shopDecorationLabel;
    private JLabel otherLabel;
    private JTextField totalCost;
    private JTextField goodPaymentsCost;
    private JTextField rentCost;
    private JTextField electricCost;
    private JTextField shopDecorationCost;
    private JTextField otherCost;
    private JTextField saleTotal;
    private JLabel profitLabel;
    private JTextField profitTextField;
    private JLabel salaryLabel;
    private JTextField salaryTextField;
    private JPanel salaryMoudle;
    private JButton salaryRefresh;
    private JScrollPane salaryInfo;
    private JButton salaryStructureBuuton;
    private JLabel year;
    private JLabel month;
    private JTextField year1;
    private JTextField month1;
    private JButton overtimeOrVacationButton;
    private JPanel accountManagement;
    private JScrollPane accoutInfo;
    private JButton accoutRefresh;
    private JButton addAlterAccount;
    private JDialog productInfoRegister;
    private JLabel serialNumber1Label;
    private JLabel purchasePrice1Label;
    private JLabel labelPrice1Label;
    private JLabel weight1Label;
    private JLabel goldQuality1Label;
    private JTextField serialNumber1;
    private JTextField purchasePrice1;
    private JTextField laberPrice1;
    private JTextField weight1;
    private JButton submitProduct;
    private JComboBox<String> goldQuality1;
    private JLabel existLabel;
    private JButton update;
    private JButton delete;
    private JLabel digitsNote;
    private JDialog saleInfoRegister;
    private JLabel serialNumber2Label;
    private JTextField serialNumber2;
    private JButton checkInfo;
    private JLabel labelPrice2Label;
    private JTextField labelPrice2;
    private JLabel weight2Label;
    private JTextField weight2;
    private JTextField goldQuality2;
    private JLabel goldQuality2Label;
    private JLabel salePriceLabel;
    private JTextField salePrice;
    private JButton submitSale;
    private JButton correct;
    private JLabel wrongAccountNote;
    private JLabel notExistNote;
    private JDialog costRegister;
    private JLabel valueLabel;
    private JTextField value;
    private JLabel dateLabel;
    private JLabel noteLabel;
    private JButton submitCost;
    private JComboBox<String> note;
    private JTextField dateTextfield;
    private JLabel wrongDateNote;
    private JDialog accoutRegister;
    private JLabel accoutIDLabel;
    private JLabel passwordLabel;
    private JLabel password2Label;
    private JLabel accoutTypeLabel;
    private JTextField accoutIDTextfield;
    private JComboBox<String> accountType;
    private JButton accountSubmit;
    private JLabel wrongNote;
    private JLabel lengthNote;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;
    private JLabel existLabel2;
    private JButton checkInfo3;
    private JButton delete3;
    private JButton update3;
    private JButton rename;
    private JLabel accountLengthNote;
    private JDialog costInfoModify;
    private JLabel lSerialNumber3;
    private JTextField costID;
    private JButton checkInfo2;
    private JTextField value2;
    private JLabel valueLabel2;
    private JLabel dateLabel2;
    private JTextField dateTextfield2;
    private JLabel noteLabel2;
    private JComboBox<String> note2;
    private JButton update2;
    private JButton delete2;
    private JLabel wrongNote2;
    private JLabel productCostNote;
    private JLabel wrongDateNote2;
    private JDialog salaryStructureDialog;
    private JLabel salaryList;
    private JComboBox<String> userTypes;
    private JTextField base;
    private JTextField attendanceReward;
    private JTextField sale14K;
    private JTextField sale18K;
    private JTextField sale22K;
    private JTextField sale24K;
    private JButton submitSalaryStructure;
    private JButton checkInfo4;
    private JDialog overtimeOrVacationDialog;
    private JComboBox<String> overtimeOrVacation;
    private JLabel userID1;
    private JTextField userID2;
    private JButton checkUserID;
    private JLabel label3;
    private JTextField daysOrOvertimeSalary;
    private JButton submitVacationOrOvertime;
    private JLabel notExistLabel;
    private JDialog errorDialog;
    private JLabel error;
    private JButton close;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
