package ProductAndUser;

import java.util.Date;

public class Product {
    private int serialNumber;// Serial number of product
    private double purchasePrice;// Merchant purchase price
    private double labelPrice;//Tag price
    private double salePrice;//Selling price
    private double weight;// Weight of product
    private String goldQuality;// The quality of gold
    private Date storageDate;// Date of product storage
    private String salemanID;//Sales person's account number
    private Date saleDate;//Date of sale

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getLabelPrice() {
        return labelPrice;
    }

    public void setLabelPrice(double labelPrice) {
        this.labelPrice = labelPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getGoldQuality() {
        return goldQuality;
    }

    public void setGoldQuality(String goldQuality) {
        this.goldQuality = goldQuality;
    }

    public Date getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(Date storageDate) {
        this.storageDate = storageDate;
    }

    public String getSalemanID() {
        return salemanID;
    }

    public void setSalemanID(String salemanID) {
        this.salemanID = salemanID;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }
}
