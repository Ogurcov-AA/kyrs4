package sample;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class appController {

    @FXML
    private AnchorPane mainPanel;

    @FXML
    private Label introText;

    @FXML
    private Button homeButton;

    @FXML
    private Button publicChatButton;

    @FXML
    private Button searchPeopleButton;

    @FXML
    private Button privateChatButton;

    @FXML
    private Label loginLabel;

    @FXML
    private ImageView gearImage;

    @FXML
    private AnchorPane HomePanel;

    @FXML
    private Label EmailLabel;

    @FXML
    private Label countryLabel;

    @FXML
    private Label bDayLabel;

    @FXML
    private ImageView userPicture;

    @FXML
    private Label NameLabel;

    @FXML
    private AnchorPane publicChatPanel;

    @FXML
    private TextArea log;

    @FXML
    private TextField sayTextField;

    @FXML
    private Button SendMesButton;

    @FXML
    private MenuButton chatSettings;

    @FXML
    private AnchorPane searchPeoplePanel;

    @FXML
    private ListView<String> PeopleListView;

    @FXML
    private TextField searchTextField;

    @FXML
    private TextField searchNameTextFielt;

    @FXML
    private TextField searchSurnameTextField;

    @FXML
    private TextField searchCoyntryTextField;

    @FXML
    private Button backAdvantagesButton;

    @FXML
    private AnchorPane privateChatPanel;

    @FXML
    private TextArea privateMsgTextArea;

    @FXML
    private Label privateMSGLoginLabel;

    @FXML
    private TextField sendPrivateMsg;

    @FXML
    private Button sendPrivateMsgButton;

    @FXML
    private Button backPrivateMsgButton;

    @FXML
    private AnchorPane UserInfoPanel;

    @FXML
    private Label UserNameLabel;

    @FXML
    private Label UserEmailLabel;

    @FXML
    private Label UsercountryLabel;

    @FXML
    private Label UserbDayLabel;

    @FXML
    private ImageView userInfoPicture;

    @FXML
    private Label UserLoginLabel;

    @FXML
    private Button backUnfoUserButton;

    @FXML
    private Button privateDialogButton;

    PrivateMessage privateDialogs = new PrivateMessage();
    ArrayList<String> currentDialog = new ArrayList<>();
    MessageHandling tt = new MessageHandling();
    Timer timer = new Timer();
    Timer timerToPrivateMSG;
    String TwoLogins;

    @FXML
    void initialize() {
        loginLabel.setText(UserInfo.currentUser.getLogin());
        NameLabel.setText(UserInfo.currentUser.getSurname() + " " + UserInfo.currentUser.getName());
        countryLabel.setText(UserInfo.currentUser.getCountry());
        EmailLabel.setText(UserInfo.currentUser.getMail());
        bDayLabel.setText(UserInfo.currentUser.getbDay());
        loginLabel.setAlignment(Pos.CENTER_RIGHT);
        homeButton.setDisable(true);
        Image image = new Image(UserInfo.currentUser.getImgPath(),true);
        userPicture.setImage(image);
        SetButtonToSettingChat();
    }

    public void SetListView(){
       PeopleListView.getItems().clear();
        for (int i = 0; i< UserInfo.UserList.size();i++) {
            if(searchTextField.getText().trim().equals("") && searchNameTextFielt.getText().trim().equals("")
                                                           && searchSurnameTextField.getText().trim().equals("")
                                                           && searchCoyntryTextField.getText().trim().equals("")) {

                PeopleListView.getItems().add(UserInfo.UserList.get(i).getLogin());
            }
            else {
                if(UserInfo.UserList.get(i).getLogin().lastIndexOf(searchTextField.getText().trim())!=-1
                && UserInfo.UserList.get(i).getName().lastIndexOf(searchNameTextFielt.getText().trim())!=-1
                && UserInfo.UserList.get(i).getSurname().lastIndexOf(searchSurnameTextField.getText().trim())!=-1
                && UserInfo.UserList.get(i).getCountry().lastIndexOf(searchCoyntryTextField.getText().trim())!=-1
                )
                PeopleListView.getItems().add(UserInfo.UserList.get(i).getLogin());
            }
        }
        SetImageToListView();
        SetActionToClickListView();
    }

    private void SetImageToListView(){
        PeopleListView.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setImage(new Image("https://yt3.ggpht.com/a/AGF-l7_ZiuKY6WVNZwQME7rjmVrs33DQ6OoAYGuqOw=s900-c-k-c0xffffffff-no-rj-mo"));
                    imageView.fitWidthProperty().setValue(64);
                    imageView.fitHeightProperty().setValue(64);
                    setText(name);
                    setGraphic(imageView);
                }
            }
        });
    }

    private void SetActionToClickListView(){
        PeopleListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        PeopleListView.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                for (int i =0;i<UserInfo.UserList.size();i++) {
                    if(UserInfo.UserList.get(i).getLogin().equals(PeopleListView.getSelectionModel().getSelectedItem())) {
                        UserLoginLabel.setText(UserInfo.UserList.get(i).getLogin());
                        UserNameLabel.setText(UserInfo.UserList.get(i).getName() + " " + UserInfo.UserList.get(i).getSurname());
                        UserEmailLabel.setText(UserInfo.UserList.get(i).getMail());
                        UsercountryLabel.setText(UserInfo.UserList.get(i).getCountry());
                        UserbDayLabel.setText(UserInfo.UserList.get(i).getbDay());
                        UserInfoPanel.setVisible(true);
                    }
                }
            }
        });
    }

    public void SetButtonToSettingChat(){
        chatSettings.getItems().clear();
        chatSettings.getItems().add(new MenuItem("Очистить поле"));
        chatSettings.getItems().add(new MenuItem("Отключиться"));
        chatSettings.getItems().add(new MenuItem("Подключиться"));
        chatSettings.getItems().get(0).setOnAction(logClearEvent);
        chatSettings.getItems().get(1).setOnAction(disconnectEvent);
        chatSettings.getItems().get(2).setOnAction(connectEvent);
    }

    EventHandler<ActionEvent> logClearEvent = new EventHandler<ActionEvent>() {

        public void handle(ActionEvent e) {
            log.clear();
        }
    };
    EventHandler<ActionEvent> disconnectEvent = new EventHandler<ActionEvent>() {

        public void handle(ActionEvent e) {
            tt.Disconnection(tt.connection);
        }
    };
    EventHandler<ActionEvent> connectEvent = new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e) {
            tt.Disconnection(tt.connection);
            tt.CreateConnection(loginLabel.getText().trim());
        }
    };

    @FXML
    public void HomeButtonClick() {
        homeButton.setDisable(true);
        if(publicChatButton.isDisable()) {
            tt.Disconnection(tt.connection);
            publicChatButton.setDisable(false);
        }
        searchPeopleButton.setDisable(false);
        publicChatButton.setDisable(false);
        searchPeopleButton.setDisable(false);
        HomePanel.setVisible(true);
        publicChatPanel.setVisible(false);
        searchPeoplePanel.setVisible(false);
        UserInfoPanel.setVisible(false);
        privateChatPanel.setVisible(false);
        if(privateChatButton.isDisable())
        StopPrivateChat();
        privateChatButton.setDisable(false);

    }

    @FXML
    public void EnterPrivateTextField(){
        String msg = sendPrivateMsg.getText().trim();
        if(msg.equals("")) return;
        sendPrivateMsg.setText("");
        privateDialogs.SendMes(UserInfo.currentUser.getLogin().trim() + " : " + msg);
        privateMsgTextArea.appendText(UserInfo.currentUser.getLogin().trim() + " : " + msg + System.getProperty("line.separator"));
    }

    @FXML
    public void PublicChatButtonClick() {
        homeButton.setDisable(false);
        publicChatPanel.setVisible(true);
        searchPeoplePanel.setVisible(false);
        HomePanel.setVisible(false);
        searchPeopleButton.setDisable(false);
        publicChatButton.setDisable(true);
        CheckInputMsg(false);
        log.setWrapText(true);
        tt.CreateConnection(loginLabel.getText().trim());
        CheckInputMsg(true);
        UserInfoPanel.setVisible(false);
        privateChatPanel.setVisible(false);
        if(privateChatButton.isDisable())
        StopPrivateChat();
        privateChatButton.setDisable(false);

    }

    @FXML
    public void SearchButton(){
        searchPeoplePanel.setVisible(true);
        searchPeopleButton.setDisable(true);
        homeButton.setDisable(false);
        UserInfoPanel.setVisible(false);
        if(publicChatButton.isDisable()) {
            tt.Disconnection(tt.connection);
            publicChatButton.setDisable(false);
        }
        publicChatPanel.setVisible(false);
        HomePanel.setVisible(false);
        SetListView();
        privateChatPanel.setVisible(false);
        if(privateChatButton.isDisable())
        StopPrivateChat();
        privateChatButton.setDisable(false);

    }

    @FXML
    public void privateChatButtonClick(){
        if(privateMSGLoginLabel.getText().equals("") || !searchPeopleButton.isDisable()){
            return;
        }
        privateChatPanel.setVisible(true);
        privateChatButton.setDisable(true);
        homeButton.setDisable(false);
        if(publicChatButton.isDisable()) {
            tt.Disconnection(tt.connection);
            publicChatButton.setDisable(false);
        }
        privateMsgTextArea.setWrapText(true);
        privateMsgTextArea.setStyle("-fx-highlight-fill: blue; -fx-highlight-text-fill: firebrick; -fx-font-size: 12px;");
        searchPeopleButton.setDisable(false);
        publicChatButton.setDisable(false);
        searchPeopleButton.setDisable(false);
        HomePanel.setVisible(false);
        publicChatPanel.setVisible(false);
        searchPeoplePanel.setVisible(false);
        UserInfoPanel.setVisible(false);
    }

    @FXML
    public void privateBackButtonClick(){
        privateMSGLoginLabel.setText(null);
        CheckInputPrivateMsg(false);
        SearchButton();
        UserInfoPanel.setVisible(true);
    }


    @FXML
    public void AdvancedSettingButtonClick(){
    advantagesMenu(true);
    }

    @FXML
    public void backAdvantagesButtonClick(){
        advantagesMenu(false);
    }

    @FXML
    public void backUnfoUserButtonClick(){
        UserInfoPanel.setVisible(false);
    }

    @FXML
    public void privateDialogsButtonClick(){
        privateDialogs.CreateConnection();
        TwoLogins = UserLoginLabel.getText().trim();
      privateDialogs.SendLoginToGetKey(UserInfo.currentUser.getLogin().trim(),TwoLogins);
      CheckInputPrivateMsg(true);
      privateMSGLoginLabel.setText(UserLoginLabel.getText().trim());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        privateDialogs.InputArrayWithFile(currentDialog);
      for(var temp : currentDialog)
      privateMsgTextArea.appendText(temp + System.getProperty("line.separator"));
      privateChatButtonClick();
    }

    public void advantagesMenu(boolean flag){
        searchNameTextFielt.setVisible(flag);
        searchSurnameTextField.setVisible(flag);
        searchCoyntryTextField.setVisible(flag);
        backAdvantagesButton.setVisible(flag);
    }

    public void CheckInputMsg(boolean flag) {
            Runnable task = () -> {
                timer.schedule(new TimerTask() {
                    public void run() {
                        if(flag == false)
                            return;
                        if(tt.logs.size()!=0){
                            for(int i= 0;i<tt.logs.size();i++){
                                printMsg(tt.logs.get(i));
                                tt.logs.remove(tt.logs.get(i));
                            }
                        }
                    }
                }, 0, 200);
            };
            Thread myThread = new Thread(task, "timer");
            myThread.start();
        }

    public void CheckInputPrivateMsg(boolean flag) {
       timerToPrivateMSG  = new Timer();
        Runnable task = () -> {
            timerToPrivateMSG.schedule(new TimerTask() {
                public void run() {
                    if(flag == false)
                        return;
                    if(privateDialogs.logs.size()!=0){
                        for(int i= 0;i<privateDialogs.logs.size();i++){
                            if(privateDialogs.logs.get(i).trim().equals("/yOuRloGin")){
                                privateDialogs.sendMyLogin("/mYlOGin " + UserInfo.currentUser.getLogin());
                                privateDialogs.logs.remove(privateDialogs.logs.get(i));
                                i--;
                            }
                           else if(privateDialogs.logs.get(i).substring(0,privateDialogs.logs.get(i).indexOf(' ')).equals("/getPrivateMSGkey")){
                                privateDialogs.CheckDialogKey(privateDialogs.logs.get(i).substring(privateDialogs.logs.get(i).indexOf(' ')+1));
                                privateDialogs.logs.remove(privateDialogs.logs.get(i));
                                i--;
                            }
                            else if(privateDialogs.logs.get(i).substring(0,privateDialogs.logs.get(i).indexOf(' ')).equals("/pmLogToKey")){
                              String msg = privateDialogs.logs.get(i).substring(privateDialogs.logs.get(i).indexOf(' ')+1);

                              printPrivateMsg(msg.substring(msg.indexOf(' ')+1));
                                privateDialogs.InputLogFile(msg);
                                privateDialogs.logs.remove(privateDialogs.logs.get(i));
                                i--;
                            }else if(privateDialogs.logs.get(i).substring(0,privateDialogs.logs.get(i).indexOf(' ')).equals("/pmLogToKeySuccess")){
                                privateDialogs.logs.remove(privateDialogs.logs.get(i));
                                i--;
                              }
                            else if(privateDialogs.logs.get(i).substring(0,privateDialogs.logs.get(i).indexOf(' ')).equals("/pm")){
                                String tempMsg = privateDialogs.logs.get(i).substring(privateDialogs.logs.get(i).indexOf(' ')+1);

                                if(privateChatPanel.isVisible()
                                        && privateDialogs.GetCurrentkey().equals(tempMsg.substring(0,tempMsg.indexOf(' ')))) {
                                    printPrivateMsg(tempMsg.substring(tempMsg.indexOf(' ') + 1));
                                    privateDialogs.InputLogFile(privateDialogs.logs.get(i));
                                }
                                privateDialogs.logs.remove(privateDialogs.logs.get(i));
                                i--;
                            }
                            else if(privateDialogs.logs.get(i).equals("ok") || privateDialogs.logs.get(i).equals("error")){
                                privateDialogs.logs.remove(privateDialogs.logs.get(i));
                                i--;

                            }
                        }
                    }
                }
            }, 0, 200);
        };
        Thread myThread = new Thread(task, "timer");
        myThread.start();
    }

    public synchronized void printMsg(String msg) {

        Task<Void> task = new Task<>() {
            @Override
            public Void call() {
                log.appendText(msg + System.getProperty("line.separator"));
                return null;
            }
        };
        new Thread(task).start();
    }

    public synchronized void printPrivateMsg(String msg) {

        Task<Void> task = new Task<>() {
            @Override
            public Void call() {
                privateMsgTextArea.appendText(msg + System.getProperty("line.separator"));
                return null;
            }
        };
        new Thread(task).start();
    }

    public void StopPrivateChat(){
        CheckInputPrivateMsg(false);
   //     privateDialogs.CheckInputMsg(false);
        privateMsgTextArea.clear();
        privateMSGLoginLabel.setText("");
        timerToPrivateMSG.cancel();
        privateDialogs.timerS.cancel();
        privateDialogs.connection.disconnected();
    }

    public void EnterTextField(){
        String msg = sayTextField.getText().trim();
        if(msg.equals("")) return;
        sayTextField.setText("");
        tt.SendMes(UserInfo.currentUser.getLogin().trim() + " : " + msg);
    }

    public void EnterTextFieldSearchUser(){
        SetListView();
    }


}
