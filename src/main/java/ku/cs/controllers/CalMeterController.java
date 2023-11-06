package ku.cs.controllers;

import com.github.saacsos.FXRouter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import ku.cs.models.Customer;
import ku.cs.models.Meter;
import ku.cs.services.DBConnector;
import ku.cs.services.Effect;


import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CalMeterController {

    @FXML
    private Label electBillLabel;

    @FXML
    private TextField electMeterTextField;

    @FXML
    private Label electUnitLabel;

    @FXML
    private Label lastMonthElectLabel;

    @FXML
    private Label lastMonthLabel;

    @FXML
    private Label lastMonthLabel1;

    @FXML
    private Label lastMonthWaterLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private ListView<String> roomListView;

    @FXML
    private Label roomNumberLabel;

    @FXML
    private Label waterBillLabel;

    @FXML
    private TextField waterMeterTextField;
    public DatePicker datePicker;
    @FXML
    private Label waterUnitLabel;

    @FXML
    private Pane calculateSuccessfulPane;

    @FXML
    private Label errorLabel;

    private Effect effect;


    private Meter meter;

    public void initialize(){
        showListView();
        handleSelectedListView();
        effect = new Effect();
        calculateSuccessfulPane.setDisable(true);
        calculateSuccessfulPane.setOpacity(0);
        waterMeterTextField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*")) {
                return change;
            }
            return null;
        }));

        electMeterTextField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*")) {
                return change;
            }
            return null;
        }));
    }


    public void showListView() {
        try {
            Connection con = DBConnector.getConnection();
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT room_number FROM room_for_rent WHERE occupancy_status = 'unavailable'");
            while (resultSet.next()) {
                String room_number = resultSet.getString(1);
                String listOut = room_number;
                roomListView.getItems().add(listOut);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void handleSelectedListView() {
        roomListView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                        showDataFromDB(t1);
                        waterBillLabel.setText("");
                        electBillLabel.setText("");
                        waterUnitLabel.setText("");
                        electUnitLabel.setText("");
                    }
                }
        );
    }

    public void showDataFromDB(String t1) {
        try {
            Connection con = DBConnector.getConnection();
            Statement statement1 = con.createStatement();
            Statement statement2 = con.createStatement();
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(date_of_register) AS date_of_register FROM calMeter WHERE room_number = "+t1);
            ResultSet resultSet1 = statement1.executeQuery("SELECT water_meter,electric_meter FROM room_for_rent WHERE room_number = "+t1);
            ResultSet resultSet2 = statement2.executeQuery("SELECT name FROM customer WHERE room_number = "+t1);
            while (resultSet1.next() & resultSet2.next() & resultSet.next()) {
               nameLabel.setText(resultSet2.getString("name"));
               roomNumberLabel.setText(t1);
               lastMonthLabel.setText(dateString(resultSet.getDate("date_of_register").toLocalDate()));
               lastMonthLabel1.setText(dateString(resultSet.getDate("date_of_register").toLocalDate()));
               lastMonthWaterLabel.setText(resultSet1.getString("water_meter"));
               lastMonthElectLabel.setText(resultSet1.getString("electric_meter"));
            }
            statement1.close();
            statement2.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
            clearLabel();

        }
    }

    public float calWater(int oldWater, int thisWater, int waterUnit) {
        return (thisWater - oldWater) * waterUnit;
    }

    public float calElect(int oldElect, int thisElect, int electUnit) {
        return (thisElect - oldElect) * electUnit;
    }

    @FXML
    public void calMeterBtn(ActionEvent actionEvent) {
        if (roomNumberLabel.getText().equals("RoomNumber")) {
            errorLabel.setText("กรุณาเลือกห้อง");
        }
        else if (datePicker.getValue() == null) {
            errorLabel.setText("กรุณาใส่วันที่");
        }
        else if (waterMeterTextField.getText().isEmpty()) {
            errorLabel.setText("กรุณากรอกมิเตอร์น้ำเดือนปัจจุบัน");
        }
        else if (electMeterTextField.getText().isEmpty()) {
            errorLabel.setText("กรุณากรอกมิเตอร์ไฟเดือนปัจจุบัน");
        }
        else if ((waterMeterTextField.getText().length() < 4 || waterMeterTextField.getText().length() > 4) || (electMeterTextField.getText().length() < 5 || electMeterTextField.getText().length() > 5)) {
            errorLabel.setText("กรุณากรอกข้อมูลให้ถูกต้อง");
        }
        else {
            try {
                int thisElect = Integer.parseInt(electMeterTextField.getText());
                int thisWater = Integer.parseInt(waterMeterTextField.getText());
                int electUnit = 7;
                int waterUnit = 25;
                int oldElect = Integer.parseInt(lastMonthElectLabel.getText());
                int oldWater = Integer.parseInt(lastMonthWaterLabel.getText());
                float calWater = calWater(oldWater,thisWater,waterUnit);
                float calElect = calElect(oldElect,thisElect,electUnit);
                int calElectUnit = thisElect - oldElect;
                int calWaterUnit = thisWater - oldWater;
                waterBillLabel.setText(String.format("%,.2f",calWater));
                electBillLabel.setText(String.format("%,.2f",calElect));
                electUnitLabel.setText(Integer.toString(calElectUnit));
                waterUnitLabel.setText(Integer.toString(calWaterUnit));

                addCalMeterToDB(Integer.parseInt(roomNumberLabel.getText()), datePicker.getValue(), waterMeterTextField.getText(),electMeterTextField.getText(), waterUnit, electUnit , calWater, calElect, calWaterUnit, calElectUnit);
                errorLabel.setText("");
                calculateSuccessfulPane.setOpacity(1);
                calculateSuccessfulPane.setDisable(false);
                effect.fadeInPage(calculateSuccessfulPane);

                clearTextField();
            } catch (NumberFormatException e) {
                errorLabel.setText("กรุณาใส่ตัวเลข");
            }
        }
        effect.fadeOutLabelEffect(errorLabel,3);
    }

    public void clearLabel() {
        lastMonthLabel.setText("0000-00-00");
        lastMonthLabel1.setText("0000-00-00");
        lastMonthElectLabel.setText("00000");
        lastMonthWaterLabel.setText("0000");
    }

    public void clearTextField() {
        waterMeterTextField.clear();
        electMeterTextField.clear();
    }

    public void datePicker(ActionEvent actionEvent) {
        datePicker.getValue();
    }

    public void addCalMeterToDB(int roomNum, LocalDate date, String thisWater, String thisElect, int waterUnit, int electUnit, float calWater, float calElect, int calWaterUnit, int calElectUnit) {

        try {
            Connection con = DBConnector.getConnection();
            String sql = "INSERT INTO calMeter (room_number,date_of_register,current_m_water_meter,current_m_electric_meter,water_unit_used,electric_unit_used,price_per_water_unit,price_per_electric_unit,water_price,electric_price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, roomNum);
            preparedStatement.setDate(2, Date.valueOf(date));
            preparedStatement.setString(3, thisWater);
            preparedStatement.setString(4, thisElect);
            preparedStatement.setInt(5, calWaterUnit);
            preparedStatement.setInt(6, calElectUnit);
            preparedStatement.setInt(7, waterUnit);
            preparedStatement.setInt(8, electUnit);
            preparedStatement.setFloat(9, calWater);
            preparedStatement.setFloat(10, calElect);
            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                meter = new Meter(roomNum,date,thisWater,thisElect,calWaterUnit,calElectUnit,waterUnit,electUnit,calWater,calElect);
            }
            PreparedStatement preparedStatement1 = con.prepareStatement("UPDATE room_for_rent SET water_meter = ?,electric_meter = ? WHERE room_number = "+roomNum+";");
            preparedStatement1.setString(1, thisWater);
            preparedStatement1.setString(2, thisElect);
            preparedStatement1.executeUpdate();
            preparedStatement.close();
            preparedStatement1.close();
            con.close();


        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void handleCloseCalculateSuccess() {
        calculateSuccessfulPane.setOpacity(0);
        calculateSuccessfulPane.setDisable(true);
    }

    @FXML
    public void clickToBack(Event event) {
        try {
            FXRouter.goTo ( "Home" );
        } catch (IOException e) {
            System.err.println ( "ไปที่หน้า Home ไม่ด้" );
        }
    }

    public String dateString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        return date.format(formatter);
    }

    public void createInvoiceBtn(ActionEvent actionEvent) {
        try {
            FXRouter.goTo("CreateInvoice");
        } catch (IOException e) {
            System.err.println("ไปที่หน้า login_detail ไม่ได้");
            System.err.println("ให้ตรวจสอบการกำหนด route");
        }
    }
}