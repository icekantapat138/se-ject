package ku.cs.controllers;

import com.github.saacsos.FXRouter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import ku.cs.models.Invoice;
import ku.cs.services.DBConnector;
import ku.cs.services.Effect;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CreateInvoiceController {

    public DatePicker datePicker;
    @FXML
    private Label roomText;
    @FXML
    private Label totalPriceText;
    @FXML
    private Label roomPriceText;
    @FXML
    private Label owedText;
    @FXML
    private Label waterUnitText;
    @FXML
    private Label elecUnitText;
    @FXML
    private Label roomPriceTotalText;
    @FXML
    private Label waterPriceTotalText;
    @FXML
    private Label elecPriceTotalText;
    @FXML
    private Label owedTotalText;
    @FXML
    private ListView<String> showRoomListView;
    @FXML
    private TextField invoiceNumberTextField;
    @FXML
    private Label errorLabel;

    @FXML
    private Pane calculateSuccessfulPane;

    private Invoice invoice;

    private Effect effect;

    private String roomNum;


    public void initialize() {
        showListViewInCreateInv();
        handleSelectedListView();
        effect = new Effect();
        calculateSuccessfulPane.setDisable(true);
        calculateSuccessfulPane.setOpacity(0);
        invoiceNumberTextField.setEditable(true);
    }

    public void showListViewInCreateInv() {

        try {
            Connection con = DBConnector.getConnection();
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT room_number FROM room_for_rent WHERE occupancy_status = 'unavailable'");
            while (resultSet.next()) {
                String room_number = resultSet.getString(1);
                String listOut = room_number;
                showRoomListView.getItems().add(listOut);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void handleSelectedListView() {
        showRoomListView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                        showDataFromDB(t1);
                        roomNum = t1;
                        invoiceNumberTextField.setText("");
                    }
                }
        );
    }

    public void showDataFromDB(String t1) {
        try {
            Connection con = DBConnector.getConnection();
            Statement statement1 = con.createStatement();
            Statement statement2 = con.createStatement();
            Statement statement3 = con.createStatement();
            ResultSet resultSet1 = statement1.executeQuery("SELECT room_number,overdue_amount FROM customer WHERE room_number = " + t1);
            ResultSet resultSet2 = statement2.executeQuery("SELECT water_unit_used,electric_unit_used,water_price,electric_price FROM calMeter WHERE (room_number,date_of_register) IN (SELECT room_number,MAX(date_of_register) FROM calMeter WHERE room_number = "+t1+");");
            ResultSet resultSet3 = statement3.executeQuery("SELECT price FROM room_for_rent WHERE room_number = " + t1);

            while (resultSet1.next() & resultSet2.next() & resultSet3.next()) {
                roomText.setText(resultSet1.getString("room_number"));
                owedText.setText(String.format("%,.2f",resultSet1.getFloat("overdue_amount")));
                owedTotalText.setText(String.format("%,.2f",resultSet1.getFloat("overdue_amount")));
                waterUnitText.setText(resultSet2.getString("water_unit_used"));
                elecUnitText.setText(resultSet2.getString("electric_unit_used"));
                waterPriceTotalText.setText(String.format("%,.2f",resultSet2.getFloat("water_price")));
                elecPriceTotalText.setText(String.format("%,.2f",resultSet2.getFloat("electric_price")));
                roomPriceText.setText(String.format("%,.2f",resultSet3.getFloat("price")));
                roomPriceTotalText.setText(String.format("%,.2f",resultSet3.getFloat("price")));
                statement1.close();
                statement2.close();
                statement3.close();
                con.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public float calInvoice(float roomPrice, float waterPrice, float electPrice, float owedPrice) {
        return (roomPrice + waterPrice + electPrice + owedPrice);
    }

    @FXML
    public void calInvoiceBtn(ActionEvent actionEvent) {
        if (roomText.getText().equals("RoomNumber")) {
            errorLabel.setText("กรุณาเลือกห้อง");
        }
        else if (datePicker.getValue() == null) {
            errorLabel.setText("กรุณาระบุวันที่");
        }
        else {
            try {
                Connection connection = DBConnector.getConnection();
                Statement statement = connection.createStatement();
                Statement statement1 = connection.createStatement();
                Statement statement2 = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT price FROM room_for_rent WHERE room_number ="+roomNum);
                ResultSet resultSet1 = statement1.executeQuery("SELECT water_price,electric_price FROM calMeter WHERE (room_number,date_of_register) IN (SELECT room_number,MAX(date_of_register) FROM calMeter WHERE room_number = "+roomNum+");");
                ResultSet resultSet2 = statement2.executeQuery("SELECT overdue_amount FROM customer WHERE room_number ="+roomNum);
                while (resultSet.next() && resultSet1.next() && resultSet2.next()) {
                    float calInvoice = calInvoice(resultSet.getFloat("price"), resultSet1.getFloat("water_price"), resultSet1.getFloat("electric_price"), resultSet2.getFloat("overdue_amount"));
                    totalPriceText.setText(String.format("%,.2f",calInvoice));
                    invoiceNumberTextField.setText(roomText.getText()+genInvoiceNum(datePicker.getEditor().getText()));
                    long invoiceNumber = Long.parseLong(roomText.getText()+genInvoiceNum(datePicker.getEditor().getText()));
                    addCalInvoiceToDB(Integer.parseInt(roomText.getText()), invoiceNumber, datePicker.getValue(), calInvoice, 0, 0);
                }
                errorLabel.setText("");
                calculateSuccessfulPane.setOpacity(1);
                calculateSuccessfulPane.setDisable(false);
                effect.fadeInPage(calculateSuccessfulPane);

            } catch (Exception e) {
                System.out.println(e);
                System.out.println("this1");
                System.out.println(roomText.getText()+genInvoiceNum(datePicker.getEditor().getText()));
            }
        }
        effect.fadeOutLabelEffect(errorLabel, 3);
    }

    @FXML
    public void handleCloseCalculateSuccess() {
        calculateSuccessfulPane.setOpacity(0);
        calculateSuccessfulPane.setDisable(true);
    }

    public void addCalInvoiceToDB(int roomNum, long invoiceNum, LocalDate date, float calInvoice, float paid, int paidStatus) {

        try {
            Connection con = DBConnector.getConnection();
            Statement statement = con.createStatement();
            String sql = "INSERT INTO invoice(room_number, invoice_number, date_invoice, net_amount, payment_amount, payment_status)" +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, roomNum);
            preparedStatement.setLong(2, invoiceNum);
            preparedStatement.setDate(3, Date.valueOf(date));
            preparedStatement.setFloat(4, calInvoice);
            preparedStatement.setFloat(5,paid);
            preparedStatement.setInt(6, paidStatus);

            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                invoice = new Invoice(roomNum, invoiceNum, date, calInvoice, paid, paidStatus);
            }
            statement.close();
            con.close();

        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Thid");
        }
    }

    public void datePicker(ActionEvent actionEvent) {
        datePicker.getValue();
    }


    @FXML
    public void printBtn(ActionEvent actionEvent) {

        if (roomText.getText().equals("RoomNumber")) {
            errorLabel.setText("กรุณาเลือกห้อง");
        }
        else if (datePicker.getValue() == null) {
            errorLabel.setText("กรุณาระบุวันที่");
        }
        else if (totalPriceText.getText().equals("")) {
            errorLabel.setText("กรุณากดคำนวณ");
        }
        else {
                try {
                    FXRouter.goTo("Invoice", invoice);
                } catch (Exception e) {
                    System.err.println("ไปที่หน้า Invoice ไม่ได้");
                    System.err.println("ตรวจสอบการกำหนด route");
                }
        } effect.fadeOutLabelEffect(errorLabel, 3);
    }

    @FXML
    public void clickToBack(Event event) {
        try {
            FXRouter.goTo ( "Home" );
        } catch (IOException e) {
            System.err.println ( "ไปที่หน้า Home ไม่ได้" );
        }
    }

    public String genInvoiceNum(String date) {
        String[] splDate = date.split("-", 3);
        return splDate[0]+splDate[1]+splDate[2];
    }

}



