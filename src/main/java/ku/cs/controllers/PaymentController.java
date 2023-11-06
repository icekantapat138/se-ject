package ku.cs.controllers;

import com.github.saacsos.FXRouter;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import ku.cs.services.DBConnector;
import ku.cs.services.Effect;

import java.io.IOException;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class PaymentController {

    @FXML
    private Label balanceLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Label paidLabel;

    @FXML
    private TextField paidTextField;

    @FXML
    private Label roomNumberLabel;

    @FXML
    private TextField roomTextField;

    @FXML
    private Pane saveSuccessfulPane;

    @FXML
    private Label errorLabel;

    private float totalPrice;

    private ArrayList<String> room;

    private float lastPaid;

    private Effect effect;

    private float paid;

    public void initialize() {
        effect = new Effect();
        room = new ArrayList<>();
        saveSuccessfulPane.setDisable(true);
        saveSuccessfulPane.setOpacity(0);
        paidTextField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*")) {
                return change;
            }
            return null;
        }));

        roomTextField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*")) {
                return change;
            }
            return null;
        }));
    }

    public void roomSearchBtn(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnector.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT room_number FROM customer");
            while (resultSet.next()) {
                room.add(resultSet.getString("room_number"));
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("1");
        }
        if (roomTextField.getText().isEmpty()) {
            errorLabel.setText("กรุณาใส่เลขห้อง");
        }
        else if (!room.contains(roomTextField.getText())) {
            errorLabel.setText("ไม่มีเลขห้องนี้ในระบบ");
        }
        else {
            try {
                Integer.parseInt(roomTextField.getText());
                try {
                    Connection con = DBConnector.getConnection();
                    Statement statement = con.createStatement();
                    Statement statement1 = con.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT room_number,net_amount,payment_amount,payment_status FROM invoice WHERE (room_number,date_invoice) IN (SELECT room_number, MAX(date_invoice) FROM invoice WHERE room_number = "+roomTextField.getText()+");");
                    ResultSet resultSet1 = statement1.executeQuery("SELECT name,overdue_amount FROM customer WHERE room_number = "+roomTextField.getText()+";");
                    while (resultSet.next() && resultSet1.next()) {
                        roomNumberLabel.setText(resultSet.getString("room_number"));
                        nameLabel.setText(resultSet1.getString("name"));
                        lastPaid = resultSet.getFloat("payment_amount");
                        if (!room.contains(roomTextField.getText())) {
                            errorLabel.setText("ไม่มีเลขห้องนี้ในระบบ");
                        }
                        if (resultSet.getInt("payment_status") == 0){
                            totalPriceLabel.setText(String.format("%,.2f", resultSet.getFloat("net_amount")));
                        }
                        else {
                            totalPriceLabel.setText(String.format("%,.2f", resultSet1.getFloat("overdue_amount")));
                        }
                        paidLabel.setText("0.00");
                        balanceLabel.setText("0.00");
                        errorLabel.setText("");
                    }
                    statement.close();
                    statement1.close();
                    con.close();
                } catch (Exception e) {
                    System.out.println(e);
                    System.out.println("2");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("กรุณาใส่ตัวเลข");
            }
        }
        effect.fadeOutLabelEffect(errorLabel, 3);
    }

    public void saveBtn(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnector.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT net_amount,payment_amount,payment_status FROM invoice WHERE (room_number,date_invoice) IN (SELECT room_number, MAX(date_invoice) FROM invoice WHERE room_number = "+roomTextField.getText()+");");
            while (resultSet.next()) {
                totalPrice = resultSet.getFloat("net_amount");
                paid = resultSet.getFloat("payment_amount");
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("3");

        }
        if (paidTextField.getText().isEmpty())
        {
            errorLabel.setText("กรุณาใส่จำนวนเงิน");
        }
        else if (roomNumberLabel.getText().equals("")) {
            errorLabel.setText("กรุณาใส่เลขห้อง");
        }
        else if (Float.parseFloat(paidTextField.getText()) > (totalPrice - paid) || Float.parseFloat(paidTextField.getText()) < 0) {
            errorLabel.setText("กรุณาใส่จำนวนเงินให้ถูกต้อง");
            System.out.println((totalPrice - paid));
            System.out.println(Float.parseFloat(paidTextField.getText()));
        }
        else {
            try {
                Connection con = DBConnector.getConnection();
                PreparedStatement preparedStatement = con.prepareStatement("UPDATE invoice SET payment_amount = ?, payment_status = ? WHERE (room_number,date_invoice) IN ( SELECT room_number, MAX(date_invoice) FROM invoice WHERE room_number = " + roomTextField.getText() + ");");
                PreparedStatement preparedStatement1 = con.prepareStatement("UPDATE customer SET overdue_amount = ? WHERE room_number = " + roomTextField.getText() + ";");
                preparedStatement.setFloat(1, Float.parseFloat(paidTextField.getText()) + lastPaid);
                preparedStatement.setInt(2, checkPaidStatus(Float.parseFloat(paidTextField.getText())));
                preparedStatement1.setFloat(1, calPaid(Float.parseFloat(paidTextField.getText())));
                preparedStatement.executeUpdate();
                preparedStatement1.executeUpdate();
                paidLabel.setText(String.format("%,.2f", Float.parseFloat(paidTextField.getText())));
                balanceLabel.setText(String.format("%,.2f",calPaid(Float.parseFloat(paidTextField.getText()))));
                saveSuccessfulPane.setOpacity(1);
                saveSuccessfulPane.setDisable(false);
                errorLabel.setText("");
            } catch (Exception e) {
                System.out.println(e);
                errorLabel.setText("กรุณาใส่จำนวนเงิน");
            }

        }
        effect.fadeOutLabelEffect(errorLabel, 3);
    }

    public float calPaid(float paid) throws SQLException {
        Connection connection = DBConnector.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT payment_amount,net_amount,payment_status FROM invoice WHERE (room_number,date_invoice) IN (SELECT room_number, MAX(date_invoice) FROM invoice WHERE room_number = "+roomTextField.getText()+");");
        while (resultSet.next()) {
            return resultSet.getFloat("net_amount") - (lastPaid + paid);
        }
        return 0;
    }
    public int checkPaidStatus(float paid) throws SQLException {
        Connection connection = DBConnector.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT net_amount FROM invoice WHERE (room_number,date_invoice) IN (SELECT room_number, MAX(date_invoice) FROM invoice WHERE room_number = "+roomTextField.getText()+");");
        while (resultSet.next()) {
            float totalNum = resultSet.getFloat("net_amount");
            if (totalNum == (paid + lastPaid)) {
                return 1;
            }
            else if (totalNum > paid) {
                return 2;
            }
        }

        return 0;
    }

    @FXML
    public void handleCloseCalculateSuccess() {
        saveSuccessfulPane.setOpacity(0);
        saveSuccessfulPane.setDisable(true);
    }

    @FXML
    public void clickToBack(Event event) {
        try {
            FXRouter.goTo ( "Home" );
        } catch (IOException e) {
            System.err.println ( "ไปที่หน้า Home ไม่ด้" );
        }
    }

    public void receiptBtn(ActionEvent actionEvent) {
        try {
            FXRouter.goTo("Receipt");
        } catch (IOException e) {
            System.err.println("ไปที่หน้า login_detail ไม่ได้");
            System.err.println("ให้ตรวจสอบการกำหนด route");
        }
    }
}
