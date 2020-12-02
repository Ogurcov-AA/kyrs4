package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class LoginWin extends InputOutputData {

    CheckValidDate validDate = new CheckValidDate();
    protected UserInfo userUser = new UserInfo();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane SavePassRadioButton;

    @FXML
    private AnchorPane LoginPanel;

    @FXML
    private TextField loginTextField;

    @FXML
    private TextField passTextField;

    @FXML
    private Label loginLabel;

    @FXML
    private Label passLabel;

    @FXML
    private Button registrationButton;

    @FXML
    private Button authorizationButton;

    @FXML
    private AnchorPane RegistrPanel;

    @FXML
    private PasswordField passFieldR;

    @FXML
    private TextField nameTextBoxR;

    @FXML
    private TextField surnameTextBoxR;

    @FXML
    private DatePicker bDayR;

    @FXML
    private ComboBox<String> selectedCountryR;

    @FXML
    private PasswordField passFieldTwoR;

    @FXML
    private Button backButton;

    @FXML
    private ProgressBar loadProc;

    @FXML
    private Button acceptRegistryButton;

    @FXML
    private TextField emailTextFieldR;

    @FXML
    private RadioButton genderButtonMaleR;

    @FXML
    private RadioButton genderButtonWomanR;

    @FXML
    private Label genderLabel;

    @FXML
    private Label loadLabel;

    @FXML
    private TextField lognTextFieldR;

    @FXML
    void RegistrationButton() {
        LoginPanel.setVisible(false);
        RegistrPanel.setVisible(true);
    }

    @FXML
    void BackButtonClick() {
        LoginPanel.setVisible(true);
        RegistrPanel.setVisible(false);
    }

    public static void showErrorMessage(String Teg, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ошибка");
        alert.setHeaderText(Teg);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    void LoginButtonClick() {
        autorization();
    }

    public void autorization() {
        log.clear();
        String login = loginTextField.getText().trim();
        String pass = passTextField.getText().trim();

        if (!CheckTextField(loginTextField)) {
            return;
        }
        if (!CheckTextField(passTextField)) {
            return;
        }
        GetKeySendConnection(login);
        TimerLogin("getkey");
    //    loadings(true);
    //    loadLabel.setVisible(true);

        if (flag == false) {
            showErrorMessage("Error", "Неверный логин или пароль");
            return;
        }
        if (SetPass()) {
            if (GetUserInfo(loginTextField.getText().trim())) {
                ShowAndWaitNewWindow();
            } else showErrorMessage("Error", "Ошибка");
        } else
            showErrorMessage("Error", "Ошибка авторизации");
    }

  /*  public synchronized void loadings(boolean flag) {
                if (flag == true) {
                    loadProc.setVisible(true);
                    loginTextField.setDisable(true);
                    passTextField.setDisable(true);
                    authorizationButton.setDisable(true);
                    registrationButton.setDisable(true);
                } else {
                    loadProc.setVisible(false);
                    loginTextField.setDisable(false);
                    passTextField.setDisable(false);
                    authorizationButton.setDisable(false);
                    registrationButton.setDisable(false);
                }
    }

   */

    protected boolean SetPass() {
        try {
            String pas = validDate.bytetoString(validDate.getHashWithSalt(passTextField.getText().trim(), validDate.stringToByte(currentKey)));
          return LoginUserSendConnection(loginTextField.getText().trim(), pas);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        }
        return false;
    }

    @FXML
    void RegButtonClick() {
        LocalDate currentDate = LocalDate.now();
        if(CheckField() == false)
            return;
        if (!checkFieldIsEmty())
            return;
        if (!passFieldR.getText().trim().equals(passFieldTwoR.getText().trim()))
        {
            showErrorMessage("Ошибка ввода","Пароли не совпадают");
            return;
        }
        String pass;
        String msg = "";
        String salt;
        try {
            salt =  validDate.generateSalt();
            pass = validDate.bytetoString(validDate.getHashWithSalt(passFieldR.getText().trim(), validDate.stringToByte(salt)));
            msg = lognTextFieldR.getText().trim() + " " + pass + " " + salt + " " +
             nameTextBoxR.getText().trim() + " " + surnameTextBoxR.getText().trim() + " " +
             selectedCountryR.getValue() + " " + emailTextFieldR.getText().trim() + " " +
             bDayR.getValue() + " " + currentDate + " ";

       if (genderButtonMaleR.isSelected()) {
                msg += genderButtonMaleR.getText();
            } else msg += genderButtonWomanR.getText();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
            if (OutputDate(msg)){
                BackButtonClick();
            }
            else showErrorMessage("Error", "Ошибка регистрации");
    }

    boolean checkFieldIsEmty() {
        if (CheckTextField(lognTextFieldR)) {
            if (CheckTextField(nameTextBoxR)) {
                if (CheckTextField(surnameTextBoxR)) {
                    if (CheckTextField(emailTextFieldR)) {
                        if (CheckTextField(passFieldR)) {
                            if (CheckTextField(passFieldTwoR)) {
                                if (selectedCountryR.getItems() != null) {
                                    if (bDayR.getValue() != null) {
                                        bDayR.setStyle("-fx-border-width: 0");
                                        return true;
                                    } else {
                                        bDayR.setStyle("-fx-border-color: red");
                                        showErrorMessage("Ошибка ввода", "Не все поля заполнены");
                                        return false;
                                    }
                                } else {
                                    showErrorMessage("Ошибка ввода", "Не все поля заполнены");
                                    return false;
                                }
                            } else {
                                showErrorMessage("Ошибка ввода", "Не все поля заполнены");
                                return false;
                            }
                        } else {
                            showErrorMessage("Ошибка ввода", "Не все поля заполнены");
                            return false;
                        }
                    } else {
                        showErrorMessage("Ошибка ввода", "Не все поля заполнены");
                        return false;
                    }
                } else {
                    showErrorMessage("Ошибка ввода", "Не все поля заполнены");
                    return false;
                }
            } else {
                showErrorMessage("Ошибка ввода", "Не все поля заполнены");
                return false;
            }
        } else {
            showErrorMessage("Ошибка ввода", "Не все поля заполнены");
            return false;
        }
    }

    public boolean CheckField() {
        if (CheckTextField(lognTextFieldR)) {
            validDate.CheckRepeatLogin(lognTextFieldR.getText().trim());
            if (!TimerLogin("validLogin")) {
                lognTextFieldR.setStyle("-fx-border-color: red; -fx-border-radius: 15; -fx-background-radius: 15");
                showErrorMessage("Ошибка ввода", "Пользователь с таким логином существует");
                return false;
            }
        } else {
            showErrorMessage("Ошибка ввода", "Не все поля заполнены");
            return false;
        }
        if (CheckTextField(emailTextFieldR)) {
            validDate.CheckRepeatEmail(emailTextFieldR.getText());
            if (TimerLogin("validEmail") == false) {
                emailTextFieldR.setStyle("-fx-border-color: red; -fx-border-radius: 15; -fx-background-radius: 15");
                showErrorMessage("Ошибка ввода", "Пользователь с таким email существует");
                return false;
            }
        } else {
            showErrorMessage("Ошибка ввода", "Не все поля заполнены");
            return false;
        }
        if (!validDate.CheckCorrectEmail(emailTextFieldR.getText().trim())) {
            lognTextFieldR.setStyle("-fx-border-color: red; -fx-border-radius: 15; -fx-background-radius: 15");
            showErrorMessage("Ошибка ввода", "Формат ввода ivanov@gmail.com");
            return false;
        } else {
            emailTextFieldR.setStyle("-fx-border-width: 0; -fx-border-radius: 15; -fx-background-radius: 15");
            lognTextFieldR.setStyle("-fx-border-width: 0; -fx-border-radius: 15; -fx-background-radius: 15");
            return true;
        }
    }

    public void ShowAndWaitNewWindow(){
        authorizationButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/app.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        NextThreadToGetUserList();
    }

    boolean CheckTextField(Object object) {
        if (object instanceof TextField) {
            if ((((TextField) object).getText().trim().isEmpty())) {
                ((TextField) object).setStyle("-fx-border-color: red; -fx-border-radius: 15; -fx-background-radius: 15");
                return false;
            } else {
                ((TextField) object).setStyle("-fx-border-width: 0; -fx-border-radius: 15; -fx-background-radius: 15");
                return true;
            }
        }
        return false;
    }

    @FXML
    void initialize() {
        selectedCountryR.getItems().addAll("Беларусь", "Россия", "Украина");
        ToggleGroup group = new ToggleGroup();
        genderButtonMaleR.setToggleGroup(group);
        genderButtonWomanR.setToggleGroup(group);
        genderButtonMaleR.setSelected(true);
        selectedCountryR.setValue("Беларусь");
        loadProc.setVisible(false);
    }
}
