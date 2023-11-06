package ku.cs.controllers;

import com.github.saacsos.FXRouter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import ku.cs.services.DBConnector;
import ku.cs.services.Effect;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AllTenantsController {

    @FXML
    private Label addressTextField;

    @FXML
    private Label depositTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label idTextField;

    @FXML
    private Label nameTextField;

    @FXML
    private Label owedLabel;

    @FXML
    private Label phoneTextField;

    @FXML
    private Label rentTextField;

    @FXML
    private ListView<String> roomListView;

    @FXML
    private MenuButton roomNumMenuBtn;

    @FXML
    private Pane saveSuccessfulPane;

    private String roomNumber;

    private String room;

    private MenuItem item;

    private Effect effect;

    public void initialize() {
        showListView();
        handleSelectedListView();
        showRoomMenu();
        effect = new Effect();
        saveSuccessfulPane.setDisable(true);
        saveSuccessfulPane.setOpacity(0);
    }
    public void showListView() {
        try {
            Connection connection = DBConnector.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT room_number FROM customer");
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
                        room = t1;
                    }
                }
        );
    }

    public void showRoomMenu() {
        try {
            Connection con = DBConnector.getConnection();
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT room_number FROM room_for_rent WHERE occupancy_status = 'available'");
            while (resultSet.next()) {
                String room_number = resultSet.getString(1);
                String listOut = room_number;
                item = new MenuItem(listOut);
                roomNumMenuBtn.getItems().add(item);
                item.setOnAction((ActionEvent e)-> {
                    roomNumPick(listOut);
                    roomNumMenuBtn.setText(listOut);
                });

            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void roomNumPick(String roomNum) {
        this.roomNumber = roomNum;
    }
    public void showDataFromDB(String t1) {

        try {
            Connection connection = DBConnector.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customer WHERE room_number = "+t1+";");
            while (resultSet.next()) {
                nameTextField.setText(resultSet.getString("name"));
                idTextField.setText(resultSet.getString("IDcard"));
                addressTextField.setText(resultSet.getString("Address"));
                rentTextField.setText(resultSet.getString("rental_period"));
                roomNumMenuBtn.setText(resultSet.getString("room_number"));
                depositTextField.setText(String.format("%,.2f",resultSet.getFloat("insurance_money")));
                owedLabel.setText(String.format("%,.2f",resultSet.getFloat("overdue_amount")));
                phoneTextField.setText(resultSet.getString("phone_number"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void saveBtn(ActionEvent actionEvent) {
        try {
            int roomNum = Integer.parseInt(roomNumber);
            updateCustomerToDB(roomNum);
            roomListView.getItems().clear();
            roomNumMenuBtn.getItems().clear();
            showListView();
            showRoomMenu();
            saveSuccessfulPane.setOpacity(1);
            saveSuccessfulPane.setDisable(false);
            errorLabel.setText("");
        } catch (NumberFormatException e) {
            System.out.println(e);
            errorLabel.setText("กรุณาเลือกห้อง");
        }
        effect.fadeOutLabelEffect(errorLabel,3);
    }

    public void updateCustomerToDB(int roomNum) {
        try {
            Connection connection = DBConnector.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE customer SET room_number = ? WHERE room_number = "+room);
            PreparedStatement preparedStatement1 = connection.prepareStatement("UPDATE room_for_rent SET occupancy_status = ? WHERE room_number = "+room);
            PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE room_for_rent SET occupancy_status = ? WHERE room_number = "+roomNumber);
            preparedStatement.setInt(1,roomNum);
            preparedStatement1.setString(1,"available");
            preparedStatement2.setString(1, "unavailable");
            preparedStatement.executeUpdate();
            preparedStatement1.executeUpdate();
            preparedStatement2.executeUpdate();
            preparedStatement.close();
            preparedStatement1.close();
            preparedStatement2.close();
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

    @FXML
    public void handleCloseCalculateSuccess() {
        saveSuccessfulPane.setOpacity(0);
        saveSuccessfulPane.setDisable(true);
    }
}
