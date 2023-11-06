package ku.cs.controllers;

import com.github.saacsos.FXRouter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ku.cs.models.Invoice;
import ku.cs.services.DBConnector;
import ku.cs.services.Effect;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.Callable;

public class ReceiptController {

    @FXML
    private ListView<String> roomNumList;

    @FXML
    private ListView<String> statusList;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label errorLabel;

    private Invoice invoice;

    private String roomNum;

    private Effect effect;

    public void initialize() {
        effect = new Effect();
        showDataONTable();
        handleSelectedListView();
    }

    public void showDataONTable()  {
        try {
            Connection connection = DBConnector.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT room_number,payment_status FROM invoice WHERE (room_number,date_invoice) IN ( SELECT room_number, MAX(date_invoice) FROM invoice WHERE payment_status BETWEEN 1 AND 2 GROUP BY room_number);");
            while (resultSet.next()) {
                String เลขที่ห้องเช่า = resultSet.getString(1);
                String listOut = เลขที่ห้องเช่า;
                String สถานะการชำระเงิน = callStatus(resultSet.getString(2));
                String listOut1 = สถานะการชำระเงิน;
                roomNumList.getItems().add(listOut);
                statusList.getItems().add(listOut1);
                statusList.setMouseTransparent(true);
                statusList.setFocusTraversable(false);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void handleSelectedListView() {
        roomNumList.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                        keepDataToCreateReceipt(t1);
                        roomNum = t1;
                    }
                }
        );
    }

    public void keepDataToCreateReceipt(String roomNum) {
        try {
            Connection connection = DBConnector.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM invoice WHERE (room_number,date_invoice) IN ( SELECT room_number, MAX(date_invoice) FROM invoice WHERE room_number = "+roomNum+");");
            while (resultSet.next()) {
                invoice = new Invoice(resultSet.getInt("room_number"),resultSet.getLong("invoice_number"),resultSet.getDate("date_invoice").toLocalDate(),resultSet.getFloat("net_amount"),resultSet.getFloat("paymennt_amount"),resultSet.getInt("payment_status"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public String callStatus (String status) {
        if (status.equals("1")) {
            return "ชำระเงินเต็มจำนวน";
        }
        return "ชำระเงินไม่เต็มจำนวน";
    }

    public void printReceiptBtn(ActionEvent actionEvent) {
        if (roomNum == null) {
            errorLabel.setText("กรุณาเลือกห้อง");
        }
        else if (datePicker.getValue() == null) {
            errorLabel.setText("กรุณาเลือกวันที่");
        }
        else {
            addReceiptDate(datePicker.getValue());
            errorLabel.setText("");
            try {
                FXRouter.goTo("ReceiptPrint", invoice);
            } catch (IOException e) {
                System.err.println("ไปที่หน้า login_detail ไม่ได้");
                System.err.println("ให้ตรวจสอบการกำหนด route");
            }
        }
        effect.fadeOutLabelEffect(errorLabel,3);
    }

    public void addReceiptDate(LocalDate date) {
        try {
            Connection connection = DBConnector.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO receipt (room_number,invoice_number,receipt_number,date_receipt)" +
                    "VALUE (?,?,?,?)");
            preparedStatement.setInt(1, invoice.getRoomNum());
            preparedStatement.setLong(2, invoice.getInvoiceNum());
            preparedStatement.setLong(3, Long.parseLong(invoice.getRoomNumStr()+genInvoiceNum(datePicker.getEditor().getText())));
            preparedStatement.setDate(4, Date.valueOf(date));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @FXML
    public void clickToBack(Event event) {
        try {
            FXRouter.goTo ( "Home" );
        } catch (IOException e) {
            System.err.println ( "ไปที่หน้า Home ไม่ด้" );
        }
    }

    public String genInvoiceNum(String date) {
        String[] splDate = date.split("/", 3);
        return splDate[0]+splDate[1]+splDate[2];
    }

}
