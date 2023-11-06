package ku.cs.controllers;

import com.github.saacsos.FXRouter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ku.cs.models.Invoice;
import ku.cs.services.DBConnector;
import ku.cs.services.NumberMap;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InvoiceController {

    @FXML
    private Label ElectPriceTotalText;

    @FXML
    private Label ElectUnit;

    @FXML
    private Label InvoiceNumText;

    @FXML
    private Label RoomText;

    @FXML
    private Label invoiceDateText;

    @FXML
    private Label nameText;

    @FXML
    private Label owedText;

    @FXML
    private Label owedTotalText;

    @FXML
    private Label roomPriceText;

    @FXML
    private Label roomPriceTotalText;

    @FXML
    private Label totalPriceText;

    @FXML
    private Label waterPriceTotalText;

    @FXML
    private Label waterUnitText;

    @FXML
    private Label numTextLabel;

    private Invoice invoice = (Invoice) FXRouter.getData();

    public void initialize() {
        showInvoiceDetail();
    }

    public void showInvoiceDetail() {

        try {
            Connection con = DBConnector.getConnection();
            Statement statement = con.createStatement();
            Statement statement1 = con.createStatement();
            Statement statement2 = con.createStatement();
            Statement statement3 = con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT invoice_number,date_invoice, net_amount FROM invoice WHERE (room_number,date_invoice) IN (SELECT room_number, MAX(date_invoice) FROM invoice WHERE room_number = "+invoice.getRoomNumStr()+");");
            ResultSet resultSet1 = statement1.executeQuery("SELECT name,overdue_amount FROM customer WHERE room_number = "+invoice.getRoomNumStr());
            ResultSet resultSet2 = statement2.executeQuery("SELECT current_m_water_meter,current_m_electric_meter,electric_price,water_price FROM calMeter WHERE (room_number,date_of_register) IN ( SELECT room_number, MAX(date_of_register) FROM calMeter WHERE room_number = "+invoice.getRoomNumStr()+");");
            ResultSet resultSet3 = statement3.executeQuery("SELECT room_number,price FROM roomm_for_rent WHERE room_number = "+invoice.getRoomNumStr());

            while (resultSet.next() & resultSet1.next() & resultSet2.next() & resultSet3.next()){
                RoomText.setText(resultSet3.getString("room_number"));
                InvoiceNumText.setText(resultSet.getString("invoice_number"));
                invoiceDateText.setText(dateString(resultSet.getDate("date_invoice").toLocalDate()));
                nameText.setText(resultSet1.getString("name"));
                waterUnitText.setText(resultSet2.getString("current_m_water_meter"));
                ElectUnit.setText(String.format(resultSet2.getString("current_m_electric_meter")));
                ElectPriceTotalText.setText(String.format("%,.2f",resultSet2.getFloat("electric_price")));
                waterPriceTotalText.setText(String.format("%,.2f", resultSet2.getFloat("water_price")));
                roomPriceText.setText(String.format("%,.2f",resultSet3.getFloat("price")));
                roomPriceTotalText.setText(String.format("%,.2f",resultSet3.getFloat("price")));
                owedText.setText(String.format("%,.2f",resultSet1.getFloat("overdue_amount")));
                owedTotalText.setText(String.format("%,.2f",resultSet1.getFloat("overdue_amount")));
                totalPriceText.setText(String.format("%,.2f",resultSet.getFloat("net_amount")));
                numTextLabel.setText(new NumberMap().getText(Float.toString(resultSet.getFloat("net_amount"))));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void cancelBtn(ActionEvent actionEvent) {
        try {
            FXRouter.goTo("CreateInvoice");
        } catch (IOException e) {
            System.err.println("ไปที่หน้า login_detail ไม่ได้");
            System.err.println("ให้ตรวจสอบการกำหนด route");
        }
    }

    public String dateString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        return date.format(formatter);
    }

}
