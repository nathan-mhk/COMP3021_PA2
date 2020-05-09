package pa2;

import java.util.ArrayList;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import pa1.GameEngine;
import pa1.departments.Department;
import pa1.Player;
import pa1.doctors.Doctor;
import pa1.exceptions.DeficitException;
import java.util.List;


public class Main extends Application {

    GameEngine game = new GameEngine();

    //Thead for players
    private Thread playerThread;

    //Three player
    Player humanPlayer;
    Player computerPlayer_1;
    Player computerPlayer_2;

    //my gold
    private Label lbMyMoney;

    // Doctor listview, showing all the doctor information of the human player
    private ListView<String> listViewDoctor;
    private ObservableList<String> listViewDoctorItems = FXCollections.observableArrayList();
    private String listViewSelectedDoctor = null;

    //listViewHospital, showing all the department information of the human player
    private ListView<String> listViewHospital;
    // listViewHospitalItems, this list contains the array of strings, these strings stores the info of the
    // departments in the hospital.  Once this list is filled with correct department info by the
    // updateListViewHospitalItems() method, it will be set to the listViewHospital by
    // listViewHospital.setItems(listViewHospitalItems), and the information of this list will be displayed to the UI
    private ObservableList<String> listViewHospitalItems = FXCollections.observableArrayList();
    private String listViewSelectedHospital= null;

    //Command Buttons at the upper right corner of the UI
    private Button bt_restart = new Button("Restart");
    private Button bt_recruit_doctor = new Button("Recruit Doctor");
    private Button bt_get_training = new Button("Get Training");
    private Button bt_raise_fund = new Button("Raise Fund");
    private Button bt_transfer_department = new Button("Transfer Department");
    private Button bt_upgrade = new Button("Upgrade Department");
    private TextField tfRecruit;

    // Computer players, refer to the picture on page 7 of the description for the details where
    // the following listviews and labels are shown on the UI
    // ListView showing the department info of computer player 1's hospital
    private ListView<String> listViewHospital_1;
    private ObservableList<String> listViewHospitalItems_1 = FXCollections.observableArrayList();
    // label showing the money amount of computer player 1
    private Label lb_money_1;
    // ListView showing the department info of computer player 2's hospital
    private ListView<String> listViewHospital_2;
    private ObservableList<String> listViewHospitalItems_2 = FXCollections.observableArrayList();
    // label showing the money amount of computer player 1
    private Label lb_money_2;

    //Log window
    private ListView<String> listViewMessage;
    private ObservableList<String> listViewMessageItems = FXCollections.observableArrayList();

    // image for displaying the hospital building picture for each player
    // an imageview will be added to the panes for each player (through getChildren().add() in the load_figure() method below)
    private Pane imageHumanPlayer = new Pane();
    private Pane imageComputerPlayer_1 = new Pane();
    private Pane imageComputerPlayer_2 = new Pane();

    // This ArrayList "printResult" stores all the message (strings) to display in the log window at the bottom of the UI
    public static ArrayList<String> printResult = new ArrayList<>(); //Store the output in the log window


    @Override
    public void start(Stage primaryStage) throws Exception{

        // init the Human Player pane. that is the upper part of the UI showing the human player's money, department info and doctor info.
        Pane panePlayer = initHumanPlayerPane();
        // init the log window, this is the lower part of the UI showing game message for all the players
        Pane log_window = initLogWindow();
        // init the computer player pane, this pane shows is the middle part of the UI showing the two computer player info
        Pane pane_computer_player = initComputerPlayer();


        // put the human player pane, computer player pane, and log window into a vbox
        // see page 6 of the description for the diagram
        VBox pane = new VBox();
        pane.getChildren().addAll(panePlayer,pane_computer_player,log_window);

        // we are using stack pane here so that the background.jpg stays in the background, covered by the human player
        // pane, computer player panes, the log window and also all the buttons at the upper right corner of the UI
        StackPane upper = new StackPane();

        // put the background.jpg to be on the bottom layer of the StackPane "upper"
        Image image = new Image("file:background.jpg");
        ImageView imageView_3 = new ImageView(image);
        upper.getChildren().add(imageView_3);
        // put the  human player pane, computer player panes, the log window on the top of the background.jpg in the
        // StackPane "upper"
        upper.getChildren().add(pane);

        // we have a scene graph with the StackPane "upper" as the root, create a Scene object, put the Scene object
        // to the stage (the window showing the UI), and show the stage to display the window
        Scene scene = new Scene(upper, 1800, 1000);
        primaryStage.setTitle("Hospital Quest GUI version");
        primaryStage.setScene(scene);
        primaryStage.show();

        // load the players from "players.txt" file
        game.gameData.loadGameData("players.txt");
        humanPlayer = game.gameData.getPlayers().get(0);
        computerPlayer_1 = game.gameData.getPlayers().get(1);
        computerPlayer_2 = game.gameData.getPlayers().get(2);

        printResult.add("------------------------------------- Human player starts -------------------------------------");

        // Update the listViewHospitalItems list that stores the human player's departments information
        // that list contains an array of strings, these strings store the info of the
        // departments in the hospital.  Once this list is filled with correct department info by the
        // updateListViewHospitalItems() method, it can be set to the listViewHospital by
        // listViewHospital.setItems(listViewHospitalItems), and the information of this list will be displayed to the UI
        updateListViewHospitalItems();

        // Update the listViewDoctorItems list that stores the human player's doctor information
        // similar to the listViewHospitalItems list above, it contains an array of strings, these strings store the
        // info of the doctors of the human player. Once this list is filled with correct department info by the
        // updateListViewDoctorItems() method, it can be set to the listViewDoctor
        // listViewDoctor.setItems(listViewDoctorItems), and the information of this list will be displayed to the UI
        updateListViewDoctorItems();

        // update the listViewHospitalItems_1 and listViewHospitalItems_2 lists which hold the department info of
        // the two computer players. Once these two lists are updated by  updateListViewComputerPlayer(), they
        // will be set by listViewHospital_1.setItems(listViewHospitalItems_1), and
        // listViewHospital_1.setItems(listViewHospitalItems_1), and the information of the two lists will be displayed
        // to the UI
        updateListViewComputerPlayer();

        // update the listViewMessageItems with the log messages in the printResult list, and show it to the UI
        updateListViewLogWindow();

        // Initialize the listeners for the two listViews, listViewHospital and listViewDoctor of the human player.
        // These two listViews are displayed on the UI for the human player. It is event-driven, whenever a user
        // selects a row from each of these listViews, the selecte row from the listViews will be copied
        // listViewSelectedHospital, and listViewSelectedDoctor.
        // For example if you select using mouse under "My Hospital" in the UI the item "Fever 0 0 10 100 1000"
        // then listViewSelectedHospital will be holding the string "Fever 0 0 10 100 1000".
        // If you select using mouse under "My Doctors" in the UI the item "Joe Minister 0 500 Medical False"
        // then listViewSelectedDoctor will be holding the string "Joe Minister 0 500 Medical False"
        initListView();

        // get the human player money through humanPlayer.getMoney();
        // then set the label for displaying money with this money amount (i.e. lbMyMoney.setText("My money: " + money))
        updateHumanPlayerMoney();

        // get the computer player money through computerPlayer_1.getMoney() and computerPlayer_2.getMoney()
        // then set the labels for displaying money of the computer players accordingly
        // (i.e. lb_money_1.setText("My money: " + money), lb_money_2.setText("My money: " + money))  )
        updateComputerPlayerMoney();

        // load the appropriate hospital building figure for the human player, computer player 1 and computer player 2
        // see the description of the load_figure() method below for the details
        load_figure();

        startTurn(); // to start the game,
    }


    private Pane initHumanPlayerPane()
    {
        /*TODO: initialize the layout of human players:
              refer to the steps 1-16 below for the details
        */

        // step 1. create a Label object referred by the reference variable "lbMyMoney" declared earlier.
        //      This label is for displaying the Money amount of the human player
        // you can set the money amount to be an arbitrary value now, later it will be updated by the updateHumanPlayerMoney()
        lbMyMoney = new Label("HEY YOUR CODE IS BROKEN, YOU SHOULDN'T BE ABLE TO SEE THIS LINE OF TEXT");


        // step 2. create a ListView object referred by the reference variable "listViewHospital" declared earlier.
        // (i.e. listViewHospital = new ListView<String>();)
        // This ListView object will be showing all the strings in the listViewHospitalItems list
        // one by one on the UI. listViewHospitalItems holds strings describing each of the departments in the hospital
        listViewHospital = new ListView<String>();
        listViewHospital.setItems(listViewHospitalItems);
        listViewHospital.setPrefSize(200, 200);


        // step 3. create a Label object to show the string "My Hospital" above the ListView
        // This label is for displaying the string "My Hospital" on the top of the human player department information
        // table
        Label lbMyHospital = new Label("My Hospital");



        // step 4. create a Label object to show the string "Department     Waiting      Cured      Capacity      Fee     Upgrade-cost per bed"
        // This label is for displaying the string ""Department     Waiting      Cured      Capacity      Fee     Upgrade-cost per bed""
        // on the top of the human player department information table
        Label lbHospitalColInfo = new Label("Department     Waiting      Cured      Capacity      Fee     Upgrade-cost per bed");
        lbHospitalColInfo.setPadding(new Insets(10,10,10,10));

        // step 5. create a Vbox()
        VBox vbHospitalDetails = new VBox();


        // step 6. put the label holding the string "Department     Waiting   ..." created above together with the ListView
        // listViewHospital into the empty vbox created above (i.e. vboxName.getChildren().addALL(lbMyHospitalTitle,listViewHospital),
        // where vboxName is the name of the Vbox() object you created above.
        vbHospitalDetails.getChildren().addAll(lbHospitalColInfo, listViewHospital);


        // step 7. create another Vbox()
        // put the label holding the string "My Hospital" and the above Vbox object into this new Vbox object
        // setpadding, and setAlignment to the label(s) and the vbox as needed so that they are displayed
        // like the UI in the executable jar program
        VBox vbHospitalInfo = new VBox();
        vbHospitalInfo.getChildren().addAll(lbMyHospital, vbHospitalDetails);
        vbHospitalInfo.setPadding(new Insets(50, 30, 20, 30));
        vbHospitalInfo.setAlignment(Pos.CENTER);



        // step 8. The above steps shows you how to create the UI display for the "My Hospital" part at the upper left of the UI
        // use a similar approach to create the UI display for the "My Doctors" part at the right of the UI
        // the ListView for displaying the doctors is "listViewDoctor", the list holding all the doctor information in the form
        // of a string array is "listViewDoctorItems".

        // "Department     Waiting      Cured      Capacity      Fee     Upgrade-cost per bed"
        // "Name     Speciality      Skill Level      Salary      Affiliation     Occupied"
        Label lbDoctorColInfo = new Label("Name     Speciality      Skill Level      Salary      Affiliation     Occupied");
        lbDoctorColInfo.setPadding(new Insets(10,10,10,10));

        listViewDoctor = new ListView<String>();
        listViewDoctor.setItems(listViewDoctorItems);
        listViewDoctor.setPrefSize(200, 200);

        VBox vbDoctorsDetails = new VBox();
        vbDoctorsDetails.getChildren().addAll(lbDoctorColInfo, listViewDoctor);

        Label lbMyDoctors = new Label("My Doctors");

        VBox vbDoctorsInfo = new VBox();
        vbDoctorsInfo.getChildren().addAll(lbMyDoctors, vbDoctorsDetails);
        vbDoctorsInfo.setPadding(new Insets(50, 30, 20, 30));
        vbDoctorsInfo.setAlignment(Pos.CENTER);



        // set up the handler to handle the click action on the restart button ( bt_restart)
        // given code
        bt_restart.setOnAction(e -> {
            try {
                handleRestart();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        // set up the handler to handle click action on the "Recruit Doctor" button (bt_recruit_doctor)
        // given code
        bt_recruit_doctor.setOnAction(e -> handleRecruitDoctor());

        // step 9. set up the handlers for
        // a. bt_get_training button, (handled by handleGetTraining() )
        // b. bt_raise_fund button (handled by handleRaiseFund() )
        // c. bt_transfer_department button (handled by handleTransferDepartment())
        // d. bt_upgrade button (handled by handleUpgrade())
        bt_get_training.setOnAction(e -> handleGetTraining());
        bt_raise_fund.setOnAction(e -> handleRaiseFund());
        bt_transfer_department.setOnAction(e -> handleTransferDepartment());
        bt_upgrade.setOnAction(e -> handleUpgrade());



        // step 10. create a new TextField object referenced by the tfRecruit variable declared earlier
        // this is the text field for holding the name of the new recruited doctor by the human player
        tfRecruit = new TextField();
        tfRecruit.setPadding(new Insets(5,10,5,10));


        // step 11. setPadding to all the buttons created above, so that they look like that of the executable jar
        bt_restart.setPadding(new Insets(5, 10, 5, 10));
        bt_recruit_doctor.setPadding(new Insets(5, 10, 5, 10));
        bt_get_training.setPadding(new Insets(5, 10, 5, 10));
        bt_raise_fund.setPadding(new Insets(5, 10, 5, 10));
        bt_transfer_department.setPadding(new Insets(5, 10, 5, 10));
        bt_upgrade.setPadding(new Insets(5, 10, 5, 10));


        // step 12. set the widths of all the buttons above to be 150 pixels
        // you can call the setPrefWidth() method. For example if you wish to set
        // the width of bt_restart button to be 150 pixels, you can do
        // bt_restart.setPrefWidth(150)
        bt_restart.setPrefWidth(150);
        bt_recruit_doctor.setPrefWidth(150);
        bt_get_training.setPrefWidth(150);
        bt_raise_fund.setPrefWidth(150);
        bt_transfer_department.setPrefWidth(150);
        bt_upgrade.setPrefWidth(150);



        // step 13. set the heights of all the buttons above to be 20 pixels
        // you can call the setPrefHeight() method. For example if you wish to set
        // the height of bt_restart button to be 20 pixels, you can do
        // bt_restart.setPrefHeight(20)
        bt_restart.setPrefHeight(20);
        bt_recruit_doctor.setPrefHeight(20);
        bt_get_training.setPrefHeight(20);
        bt_raise_fund.setPrefHeight(20);
        bt_transfer_department.setPrefHeight(20);
        bt_upgrade.setPrefHeight(20);


        // step 14. create a Hbox object
        // add tfRecruit, bt_recruit_doctor to this Hbox object, so that the name of the recruit doctor and the
        // recruit button are in the same horizon row
        // then create a Vbox object to hold all the above buttons together with the Hbox object you just created
        // vertically
        HBox hbRecruit = new HBox(10);
        hbRecruit.getChildren().addAll(tfRecruit, bt_recruit_doctor);

        VBox vbControls = new VBox();
        vbControls.getChildren().addAll(bt_restart, hbRecruit, bt_get_training, bt_raise_fund, bt_transfer_department, bt_upgrade);
        vbControls.setAlignment(Pos.CENTER_RIGHT);


        // step 15. create a Hbox object
        // add the hospital image (imageHumanPlayer), the Vbox in step 7, and a similar Vbox in step 8, together with
        // the Vbox in step 14 for holding all the buttons to this new Hbox object
        HBox hbHumanPlayerPane = new HBox();
        hbHumanPlayerPane.getChildren().addAll(imageHumanPlayer, vbHospitalDetails, vbDoctorsInfo, vbControls);


        // step 16. create a Vbox object
        // add the lbMyMoney label and the Hbox created in step 15 to this Vbox
        // setAlignment and setPadding to this Hbox as deemed needed
        // return this Vbox object as the return value of this method.
        // mind that a Vbox is also a Pane, so it is consistent with the return value declaration in the method header
        // if it works correctly, then congratulations! You have created the UI for the human player using JavaFX code
        // if it does not work, please patiently spend the time to carefully go through all the 16 steps. In particular for step 8,
        // that single step is in fact a big step consists similar code to steps 2-7.
        VBox vbHumanPlayerPane = new VBox();
        vbHumanPlayerPane.getChildren().addAll(lbMyMoney, hbHumanPlayerPane);

        return vbHumanPlayerPane;
    }


    private Pane initComputerPlayer()
    {

        // The below creates the Computer player pane for computer player 1
        lb_money_1 = new Label("Fund: 10000"); // this fund value is arbitrary, as it will be updated by updateComputerPlayerMoney() in the middle of the game
        lb_money_1.setPadding(new Insets(0, 0, 20, 0));

        // set the label for displaying the string "Computer player I" on the UI
        Label lb_1 = new Label("Computer player I");
        lb_1.setPadding(new Insets(10,10,10,10));

        // displays the departments of the hospital of computer player 1
        listViewHospital_1 = new ListView<String>();
        listViewHospital_1.setItems(listViewHospitalItems_1);
        listViewHospital_1.setPrefSize(200, 200);
        // display the string "Department    Waiting     Cured     Capacity    Fee    Upgrade-cost per bed" for this computer player 1 in the UI
        Label lbMyHospitalTitle_1 = new Label("Department    Waiting     Cured     Capacity    Fee    Upgrade-cost per bed");
        lbMyHospitalTitle_1.setPadding(new Insets(0,0,10,0));

        // arrange the various items to display properly in the UI
        VBox paneMyHospital_1 = new VBox();
        paneMyHospital_1.getChildren().addAll(lbMyHospitalTitle_1,listViewHospital_1);

        VBox container_1 = new VBox();
        container_1.getChildren().addAll(lb_money_1, paneMyHospital_1);
        container_1.setAlignment(Pos.CENTER_LEFT);

        VBox pane_player_1 = new VBox();
        pane_player_1.getChildren().addAll(lb_1,container_1);
        pane_player_1.setAlignment(Pos.CENTER);
        pane_player_1.setPadding(new Insets(50, 50, 20, 50));


        // TODO: initialize the layout of computer player 2 according to the steps 1-3 below
        //  step 1. by referring to the above steps/code for computer player 1, create the UI for computer player 2
        //      hint: a. label for displaying the money amount of computer player 2 should be "lb_money_2"
        //            b. the ListView to display the department info to the UI should be listViewHospital_2
        //               (instead of listViewHospital_1)
        //            c. the List containing an array of strings holding the department information is in
        //               listViewHospitalItems_2 (instead of listViewHospitalItems_1)

        lb_money_2 = new Label("HEY YOUR CODE IS BROKEN AGAIN YOU SHOULDN'T BE ABLE TO SEE THIS LINE OF TEXT");
        lb_money_2.setPadding(new Insets(0, 0, 20, 0));

        Label lb_2 = new Label("Computer player II");
        lb_2.setPadding(new Insets(10,10,10,10));

        listViewHospital_2 = new ListView<String>();
        listViewHospital_2.setItems(listViewHospitalItems_2);
        listViewHospital_2.setPrefSize(200, 200);

        Label lbMyHospitalTitle_2 = new Label("Department    Waiting     Cured     Capacity    Fee    Upgrade-cost per bed");
        lbMyHospitalTitle_2.setPadding(new Insets(0,0,10,0));

        VBox paneMyHospital_2 = new VBox();
        paneMyHospital_2.getChildren().addAll(lbMyHospitalTitle_2, listViewHospital_2);

        VBox container_2 = new VBox();
        container_2.getChildren().addAll(lb_money_2, paneMyHospital_2);
        container_2.setAlignment(Pos.CENTER_LEFT);

        VBox pane_player_2 = new VBox();
        pane_player_2.getChildren().addAll(lb_2, container_2);
        pane_player_2.setAlignment(Pos.CENTER);
        pane_player_2.setPadding(new Insets(50, 50, 20, 50));


        // step 2. create a new Hbox object
        HBox hbCPUPane = new HBox();


        // step 3. put the imageComputerPlayer_1, pane_player_1 for computer player 1, imageComputerPlayer_2, and
        // the final Vbox created in step 1 for computer player 2 to the Hbox created in step 2
        // return this Hbox as the return value of this method.
        hbCPUPane.getChildren().addAll(imageComputerPlayer_1, pane_player_1, imageComputerPlayer_2, pane_player_2);

        return hbCPUPane;
    }



    private Pane initLogWindow()
    {
        /*
           Initialize the layout of the log windows. Some hints are as follows:
         */
        //Log windows
        listViewMessage = new ListView<String>();
        listViewMessage.setMaxSize(470,300);
        listViewMessage.setItems(listViewMessageItems);

        VBox log_window = new VBox();
        Label lbLog = new Label("Log window");
        lbLog.setPadding(new Insets(5,5,5,5));
        log_window.getChildren().addAll(lbLog, listViewMessage);
        log_window.setPadding(new Insets(50, 50, 50, 50));
        log_window.setAlignment(Pos.CENTER);

        return log_window;
    }


    private void load_figure()
    {
        /*
        TODO: The function is used to load the proper hospital figures according to the number of beds the hospital has.
              The proper hospital figures will be loaded for each player and will be displayed in the UI.
                 Some hints are as follows:
                 1. Three panes imageHumanPlayer,imageComputerPlayer_1, imageComputerPlayer_2 is used to contain images.
                    These three panes have been created earlier, so you can use them for free here.
                 2. Remember to call imageHumanPlayer.getChildren().clear(), imageComputerPlayer_1.getChildren().clear(),  imageComputerPlayer_2.getChildren().clear() for each pane at the beginning;
                 3. Using int capacity = humanPlayer.getHospitalScale() to obtain the scale of the hospital;
                    the getHospitalScale() method in the player.java will rate the hospital according to the total number of beds.
                    If bed number <50, it will be rated a class 1 hospital (small hospital). If bed number >50 but less
                    than 80, it will be rated a class 2 hospital (medium size hospital). If the bed number >80, then
                    this will be rated a class 3 hospital (large scale hospital).
                    The images for class 1, class 2, class 3 hospitals are hospital_1.png, hospital_2.png and hospital_3.png respectively
                 4a.  Use Image image_1 = new Image("file:hospital_" + capacity + ".png") to create a Image for the human player
                 4b.  Use image_2 , image_3 as the references to the Image objects of computer players 1 and 2 respectively, and do the same operation as in 4a to the computer players
                 5a.  Use  ImageView imageView_1 = new ImageView(image_1) to create a imageview for the human player
                 5b.  use imageView_2, imageView_3 as the references to the ImageView objects of computer players 1 and 2 respectively, and do the same operation as in 5a to the computer players
                 6a.  use imageHumanPlayer.getChildren().add(imageView_1) to add the image in a pane.
                 6b.  use the panes imageComputerPlayer_1, imageComputerPlayer_1  instead of imageHumanPlayer, and do the same operation as in 6a to the computer players
         */

        imageHumanPlayer.getChildren().clear();
        imageComputerPlayer_1.getChildren().clear();
        imageComputerPlayer_2.getChildren().clear();

        int capacity = humanPlayer.getHospitalScale();
        Image image_1 = new Image("file:hospital_" + capacity + ".png");
        ImageView imageView_1 = new ImageView(image_1);
        imageHumanPlayer.getChildren().add(imageView_1);

        capacity = computerPlayer_1.getHospitalScale();
        Image image_2 = new Image("file:hospital_" + capacity + ".png");
        ImageView imageView_2 = new ImageView(image_2);
        imageComputerPlayer_1.getChildren().add(imageView_2);

        capacity = computerPlayer_2.getHospitalScale();
        Image image_3 = new Image("file:hospital_" + capacity + ".png");
        ImageView imageView_3 = new ImageView(image_3);
        imageComputerPlayer_2.getChildren().add(imageView_3);
    }

    private void handleRestart()  throws Exception
    {
        /* When the button "Restart" is clicked, this method will be executed to restart the game as a new game.
            Some hints are as follows:
            1.  using game.gameData.loadGameData("players.txt") to reload the game information;
            2. update the listviews and related information.

         */

        String result = "The game is restarted.\n"; // this message will be output to the log window
        printResult.clear();//clear the log window messages
        printResult.add(result); // output to the log window
        listViewSelectedHospital = null; // no department is selected for the human player in the UI
        listViewSelectedDoctor = null;   // no doctor is selected for the human player in the UI
        game.gameData.loadGameData("players.txt"); // reload data from the players.txt file
        humanPlayer = game.gameData.getPlayers().get(0);    // get the human player from the loaded data
        computerPlayer_1 = game.gameData.getPlayers().get(1); // get computer player 1 from the loaded data
        computerPlayer_2 = game.gameData.getPlayers().get(1); // get computer player 1 from the loaded data

        // init the various listviews with the loaded data, these lists will be updated to the UI
        update();

        // set up the listener to check the mouse selection on department and doctor in the displayed ListViews
        initListView();


    }

    private void handleRecruitDoctor()
    {
        // When the button "Recruite Doctor" is clicked, this function will be executed.


        // get the newly recruited doctor's name entered by the user into "name"
        String name = tfRecruit.getText();
        // selectDoctor() will return the doctor object that has been selected by the player to recruit
        // if this doctor is occupied, selectDoctor will return null!!
        Doctor selectedDoctor = selectDoctor(); // will be null if the doctor is occupied

        // TODO:
        //  step 1. Check If the selected doctor (accessed by the selectedDoctor reference above) is occupied and
        //         if the player has input a new doctor's name (i.e. name.length()!=0).
        //         if either of the conditions is true, add the string
        //         "You have to select a unoccupied doctor or input the new doctor's name.\n" into printResult ArrayList
        //          and then call  updateListViewLogWindow() to output the message to the log window, and then return from this method
        //      2. if the selected doctor is not occupied and the player has input a non-empty string for the new
        //          doctor's name, then:
        //          a. call selectedDoctor.recruitDoctor(humanPlayer, name) to recruit a new doctor, that will create
        //             a new doctor object and put this object to the doctors list of the player
        //          b. put a string saying that a new doctor is recruited into printResult ArrayList
        //             (check the jar executable for the exact form of this string), call updateListViewLogWindow() to
        //             output the string to the log window.
        //          c. end the turn for the selectedDoctor by setting him/her to occupied
        //          d. update the listViewDoctorItems, this List holds the information of all the doctors in the doctors
        //             list of the player. after updating this listViewDoctorItems, the new doctor's info will be added
        //             to the listViewDoctorItems List. To do this update you can call   updateListViewDoctorItems()
        //          e. run updateHumanPlayerMoney() to update the money the player still has, and get this new amount
        //             displayed to the UI
        //          f. set listViewSelectedDoctor to null, this is a string holding all the name, specialty, skill level,
        //             salary etc information of the doctor being selected to recruit a new doctor. The selectDoctor()
        //             method extract the name to search for the doctor object from the doctors list of the player.
        //             WE ASSUME DOCTOR NAMES ARE UNIQUE, if this is not the case, selectDoctor() will not work correctly

        if ((selectedDoctor == null) || (name.length() == 0)) {
            printResult.add("You have to select a unoccupied doctor or input the new doctor's name.\n");
            updateListViewLogWindow();
            return;
        }
        selectedDoctor.recruitDoctor(humanPlayer, name);

        printResult.add("A new doctor is recruited " + selectedDoctor);
        updateListViewLogWindow();

        selectedDoctor.endTurn();

        updateListViewDoctorItems();

        updateHumanPlayerMoney();

        listViewSelectedDoctor = null;
    }



    private void handleGetTraining()
    {

        // When the button "Get Trainning" is clicked, this function will be executed. Some hints are as follows:

        // get the selected doctor object for the training
        Doctor selectedDoctor = selectDoctor(); // will be null if the doctor is occupied

        // TODO:
        //  step 1. check if the doctor is occupied, if the doctor is occupied selectedDoctor will be null. If the doctor is occupied
        //  add "You have to select a unoccupied doctor.\n" into the printResult ArrayList, call updateListViewLogWindow() to display the message
        //  and then return from this method
        //  step 2. if the selectedDoctor is not occupied,
        //          a. call selectedDoctor.goTraining(humanPlayer)
        //          b. and then end the turn for the selectedDoctor
        //          c. add the string "GetTraining" to printResult ArrayList
        //          d. add the string "After training:\n"+selectedDoctor+"\n" to the printResult ArrayList
        //          e. output the printResult ArrayList by calling updateListViewLogWindow()
        //          f. update the human player's money displayed in the UI by calling updateHumanPlayerMoney()
        //          g. set listViewSelectedDoctor to null, this is a string holding all the name, specialty, skill level,
        //             salary etc information of the doctor being selected to get training. The selectDoctor()
        //             method extract the name to search for the doctor object from the doctors list of the player.
        //             WE ASSUME DOCTOR NAMES ARE UNIQUE, if this is not the case, selectDoctor() will not work correctly

        if (selectedDoctor == null) {
            printResult.add("You have to select a unoccupied doctor.\n");
            updateListViewLogWindow();
            return;
        }
        selectedDoctor.goTraining(humanPlayer);

        selectedDoctor.endTurn();

        printResult.add("GetTraining");

        printResult.add("After training:\n" + selectedDoctor + "\n");

        updateListViewLogWindow();

        updateHumanPlayerMoney();

        listViewSelectedDoctor = null;
    }

    private void handleRaiseFund()
    {
        // When the button "Raise Fund" is clicked, this function will be executed. Some hints are as follows:

        Doctor selectedDoctor = selectDoctor(); // will be null if the doctor is occupied

        // TODO:
        //  step 1. check to see if selectedDoctor is occupied, if the selectedDoctor is occupied, add the string
        //  "Please select a unoccupied doctor.\n" to the printResult ArrayList and output the ArrayList to the log
        //   window by calling updateListViewLogWindow(), and then return from this method
        //  step 2.  if the selectedDoctor is not occupied,
        //           a. use the doctor object to call raiseFund(), add the returned value to the money of the human
        //              player using humanPlayer.collectMoney()
        //           b. end the turn for the selectedDoctor
        //           c. add the string selectedDoctor.getName() + " raises "+fundAmount+" successfully.\n" to
        //              printResult where fundAmount is the amount returned by raiseFund() in step 2a
        //           d. output the printResult by updateListViewLogWindow()
        //           e. now after the selecteddoctor raised the fund, the money of the player is changed, and also the
        //              status of the selectedDoctor is also changed (from unoccupied to occupied). So we need to update
        //              the display in UI to reflect the changes. To do that we call updateHumanPlayerMoney() and
        //              updateListViewDoctorItems()
        //          f. set listViewSelectedDoctor to null, this is a string holding all the name, specialty, skill level,
        //             salary etc information of the doctor being selected to raise fund. The selectDoctor()
        //             method extract the name to search for the doctor object from the doctors list of the player.
        //             WE ASSUME DOCTOR NAMES ARE UNIQUE, if this is not the case, selectDoctor() will not work correctly

        if (selectedDoctor == null) {
            printResult.add("Please select a unoccupied doctor.\n");
            updateListViewLogWindow();
            return;
        }
        int fundAmount = selectedDoctor.raiseFund();
        humanPlayer.collectMoney(fundAmount);

        selectedDoctor.endTurn();

        printResult.add(selectedDoctor.getName() + " raises " + fundAmount + " successfully.\n");

        updateListViewLogWindow();

        updateHumanPlayerMoney();
        updateListViewDoctorItems();

        listViewSelectedDoctor = null;
    }

    private void handleTransferDepartment()
    {

        // When the button "Transfer Department" is clicked, this function will be executed. Some hints are as follows:
        // implementation provided for your reference

        Department selectedDepartment = selectDepartment();
        Doctor selectedDoctor = selectDoctor();
        if(selectedDoctor != null && !selectedDoctor.isOccupied() && selectedDepartment != null)
        {
            if(selectedDepartment.getName().equals(selectedDoctor.get_affiliation()))
            {
                String result = "The doctor has already in the department.\n";
                printResult.add(result);
                updateListViewLogWindow();
            }
            else
            {
                selectedDoctor.transferToDepartment(selectedDepartment);
                selectedDoctor.endTurn();
                updateListViewDoctorItems();
                String result = selectedDoctor.getName() + " transfers to " + selectedDepartment.getName() + "\n";
                printResult.add(result);
                listViewSelectedDoctor = null;
                listViewSelectedHospital = null;
            }

        }
        else
        {
            String result = "You have to select a unoccupied doctor or a department.\n";
            printResult.add(result);
            updateListViewLogWindow();
        }

    }


    private void handleUpgrade()
    {
        // When the button "Upgrade Department" is clicked, this function will be executed.

        // get the selectedDepartment object according to the player selection from the UI
        Department selectedDepartment = selectDepartment();
        // get the selectedDoctor object according to the player selection from the UI
        Doctor selectedDoctor = selectDoctor();


        // TODO:
        //  step 1. check to see if selectedDoctor is occupied, if the selectedDoctor is occupied, add the string
        //  ""You have to select a unoccupied doctor or a department.\n" to the printResult ArrayList and output the
        //   ArrayList to the log window by calling updateListViewLogWindow(), and then return from this method
        //  step 2.  if the selectedDoctor is not occupied,
        //           a. use the doctor object to call upgradeDepartment() for the humanPlayer and the selectedDepartment
        //           b. end the turn for the selectedDoctor
        //           c. add the string  selectedDepartment.getName() + " is upgraded." to printResult
        //           d. output the printResult by updateListViewLogWindow()
        //           e. now call updateHumanPlayerMoney(), updateListViewDoctorItems(), updateListViewHospitalItems()
        //              to update the information for the player's money, doctor status, and hospital status
        //              (bed number increased).
        //           e. call load_figure() because once the bed number has been increase in this upgrade action, the
        //              hospital could be upgraded to another rank of hospital, load_figure() will load the correct
        //              hospital png file to reflect this upgrade.
        //          f. set listViewSelectedDoctor and listViewSelectedHospital to null, they are strings holding all info of
        //             the selected doctor, and holding all the info of the selected department for the upgrade
        //             WE ASSUME DOCTOR NAMES ARE UNIQUE, if this is not the case, selectDoctor() will not work correctly

        if (selectedDoctor == null) {
            printResult.add("You have to select a unoccupied doctor or a department.\n");
            updateListViewLogWindow();
            return;
        }
        selectedDoctor.upgradeDepartment(humanPlayer, selectedDepartment);

        selectedDoctor.endTurn();

        printResult.add(selectedDepartment.getName() + " is upgraded.");

        updateListViewLogWindow();

        updateHumanPlayerMoney();
        updateListViewDoctorItems();
        updateListViewHospitalItems();

        load_figure();

        listViewSelectedDoctor = null;
        listViewSelectedHospital = null;
    }


    private void updateHumanPlayerMoney()
    {
        /*
            TODO: update the human player's money.
         */

        // step 1. set lbMyMoney to be ""My money: " + humanPlayer.getMoney(), using setText() of the label
        lbMyMoney.setText("My money: " + humanPlayer.getMoney());
    }

    private void updateComputerPlayerMoney()
    {
        /*
            TODO: update the computer players' money.
         */
        // step 1. set lb_money_1 to be ""My money: " + computerPlayer_1.getMoney(), using setText() of the label
        lb_money_1.setText("My money: " + computerPlayer_1.getMoney());

        // step 2. set lb_money_2 to be ""My money: " + computerPlayer_2.getMoney(), using setText() of the label
        lb_money_2.setText("My money: " + computerPlayer_2.getMoney());
    }

    private void updateListViewLogWindow()
    {
        /*
             Update the listview of log window.
         */
        listViewMessageItems.clear();

        for(int i =0;i<= printResult.size() - 1;i++)
        {
            listViewMessageItems.add(printResult.get(i));
        }

    }


    private void updateListViewComputerPlayer() {
        // update the listview of the two listview of computer players.

        // formulate the department information into a proper string, add the string to the listviews
        // listViewHospitalItems_1. Do the same for every single department of the computer player
        // The listViews will be displayed to the GUI.
        listViewHospitalItems_1.clear();

        List<Department> departments = computerPlayer_1.getDepartments();
        for(int i = 0;i < departments.size(); i++)
        {
            Department department = departments.get(i);
            String name = department.getName();
            int waiting_count = department.getWaitingPatientCount();
            int cured_count = department.getCuredPatientCount();
            int capacity = department.getBedCapacity();
            int fee = department.getFee();
            int cost = department.getUpgradeCost();
            if(name.equals("Fever")) //do some padding to make sure this short "Fever" string will align well in the UI
            {
                name = "    Fever";
            }
            String str_waiting_count = calibrate(waiting_count);
            String str_cured_count = calibrate(cured_count);
            String str_capacity = calibrate(capacity);
            String str_fee = calibrate(fee);
            String str_cost = calibrate(cost);

            listViewHospitalItems_1.add("     " + name + "        " + str_waiting_count + "          " + str_cured_count + "         " + str_capacity + "         " + str_fee + "         " + str_cost );

        }

        //TODO: by referring to the above, do exactly the same string formatting of the department information for computer player 2
        //      the ListView holding the strings of departments is in listViewHospitalItems_2 (instead of listViewHospitalItems_1)
        //      for computer player 2

        listViewHospitalItems_2.clear();

        departments = computerPlayer_2.getDepartments();

        for (int i = 0; i < departments.size(); ++i) {
            Department department = departments.get(i);
            String name = department.getName();
            int waiting_count = department.getWaitingPatientCount();
            int cured_count = department.getCuredPatientCount();
            int capacity = department.getBedCapacity();
            int fee = department.getFee();
            int cost = department.getUpgradeCost();
            if(name.equals("Fever")) //do some padding to make sure this short "Fever" string will align well in the UI
            {
                name = "    Fever";
            }
            String str_waiting_count = calibrate(waiting_count);
            String str_cured_count = calibrate(cured_count);
            String str_capacity = calibrate(capacity);
            String str_fee = calibrate(fee);
            String str_cost = calibrate(cost);

            listViewHospitalItems_2.add("     " + name + "        " + str_waiting_count + "          " + str_cured_count + "         " + str_capacity + "         " + str_fee + "         " + str_cost );
        }
    }


    private String calibrate(int count)
    {
        /*
            Calibrate the text in the listview view, so that each string have the same length.
         */
        String str = "";
        if(count < 10)
        {
            str = "   " + Integer.toString(count);
        }
        else if(count < 100)
        {
            str = "  " + Integer.toString(count);
        }
        else if(count < 1000)
        {
            str = " " + Integer.toString(count);
        }
        else
        {
            str = Integer.toString(count);
        }

        return str;
    }



    private void updateListViewHospitalItems()
    {
        // Update the listview of human player's departments information.
        // code provided for your reference

        listViewHospitalItems.clear();

        List<Department> departments = humanPlayer.getDepartments();
        for(int i = 0; i < departments.size(); i++)
        {
            Department department = departments.get(i);
            String name = department.getName();
            int waiting_count = department.getWaitingPatientCount();
            int cured_count = department.getCuredPatientCount();
            int capacity = department.getBedCapacity();
            int fee = department.getFee();
            int cost = department.getUpgradeCost();
            String str_waiting = calibrate(waiting_count);
            String str_cured_count = calibrate(cured_count);
            String str_capacity = calibrate(capacity);
            String str_fee = calibrate(fee);
            String str_cost = calibrate(cost);

            if(name.equals("Fever"))
            {
                name = "    Fever";
            }
            listViewHospitalItems.add("        " + name + "           " + str_waiting + "            " + str_cured_count + "         " + str_capacity + "         " + str_fee + "         " + str_cost );
        }
    }

    private void updateListViewDoctorItems()
    {
        // Update the listview of human player's doctors information.

        // clear the listViewDoctorItems
        listViewDoctorItems.clear();

        // TODO: Update the listview of human player's doctors information.
        //  Hints: 1. An example in shown in the function updateListViewHospitalItems()
        //         2. You have to work on listViewDoctorItems;

        List<Doctor> doctors = humanPlayer.getDoctors();

        for (int i = 0; i < doctors.size(); ++i) {
            Doctor doctor = doctors.get(i);

            String name = doctor.getName();
            String specialty = doctor.getSpecialty();
            int skillLv = doctor.getSpecialSkillLevel();
            int salary = doctor.getSalary();
            String dept = doctor.get_affiliation();
            String occupied = doctor.isOccupied() ? "True" : "False";

            if (specialty.equals("Fever")) {
                specialty = "    Fever";
            }
            listViewDoctorItems.add("        " + name + "        " + specialty + "        " + skillLv + "        " + salary + "        " + dept + "        " + occupied);
        }
    }


    private void initListView()
    {
        /**
         * Initialize the listeners for the two human player listviews in the UI; when a row of listview is selected by
         * the mouse in the UI, the text in the row will be stored in a string:
         * 1. in the string listViewSelectedHospital if a department in the hospital is selected from the UI
         * 2. in the string listViewSelectedDoctor if a doctor is selected from the UI
         */

        listViewHospital.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()
        {
            public void changed(ObservableValue<? extends String> ov, String old_val, String new_val)
            {
                if (new_val != null)
                {
                    //when a row in the listview is clicked, the listViewSelectedGeneral will be set as the content of that row
                    listViewSelectedHospital = new_val;


                }
            }
        });

        listViewDoctor.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()
        {
            public void changed(ObservableValue<? extends String> ov, String old_val, String new_val)
            {
                if (new_val != null)
                {
                    listViewSelectedDoctor = new_val;

                }
            }
        });

    }


    private Doctor selectDoctor()
    {
        // When a row of the listview of the doctors is selected, calling this function will return the selected doctor object.
        // IMPORTANT: 1. We assume doctor name is unique, and use this unique name to search for the doctor object selected
        // IMPORTANT: 2. when a doctor selected is "occupied" this method will return null
        // code provided for your reference
        Doctor selectedDoctor = null;

        // listViewSelectedDoctor holding the string description the doctor being selected
        if(listViewSelectedDoctor!=null)
        {
            listViewSelectedDoctor = listViewSelectedDoctor.trim();
            String doctorName = listViewSelectedDoctor.split(" ")[0]; //extract the doctor name from the string
            selectedDoctor = getDoctor(doctorName);// search for the doctor object using the name extracted above

        }
        return selectedDoctor;
    }

    private Doctor getDoctor(String doctorName)
    {
        // Given the name of the doctor, this method returns the corresponding doctor object of the human player
        // that has the same name

        // code provided for your reference
        for(Doctor doctor:humanPlayer.getDoctors())
        {
            if(doctor.getName().equals(doctorName) && !doctor.isOccupied())
            {

                return doctor;
            }
        }
        return null;

    }



    private Department selectDepartment()
    {
        /* When a row of the listview of my hospital is clicked, calling this function will return the selected department.
         */


        //TODO:  by referring to the implementation of selectDoctor(), finish the implementation here
        //  The listView of a hospital contains strings regarding the department information. They are displayed on the GUI.
        //  When the user select a department from the GUI using the mouse, a string containing the information of the
        //  department will be returned. But that's just a string. We extract the department name from the string (listViewSelectedHospital)
        //  use that name (departmentName) to search for the department object of the player
        //  (i.e. getDepartment(departmentName)), and return this department object
        // In that way, whenever the user chooses a department from the GUI, we can find the corresponding department
        // object for processing through this method.

        Department selectedDept = null;

        if (listViewSelectedHospital != null) {
            listViewSelectedHospital = listViewSelectedHospital.trim();
            String deptName = listViewSelectedHospital.split(" ")[0];
            selectedDept = getDepartment(deptName);
        }
        return selectedDept;
    }


    private Department getDepartment(String departmentName)
    {
        // Given the name of department, you have to return the department of the human player that has the same name;

        //TODO:  by referring to the implementation of getDoctor(), finish the implementation here

        for (Department dept : humanPlayer.getDepartments()) {
            if (dept.getName().equals(departmentName)) {
                return dept;
            }
        }
        return null;
    }



    private void startTurn() {
        //This function is to start the thread playerThread that will start the turns of the game.

        // IMPORTANT: you won't know the below before we start our discussion on multi-threading in ~4 classes,
        //  they are note for your reference
        //   step 1. we create a new Thread() object
        //   step 2. pass the Thread() constructor a runnable object
        //   step 3. the runnable object is constructed using anonymous inner class
        //   step 4. to make the anonymous inner class we need to override the run() method from the Runnable class
        //   step 5. we override the run() method by running the method processPlayerTurns() in it
        //   step 6. the processPlayerTurns() method would throw a *checked* exception the "DeficitException"
        //            we use try and catch to handle that exception, if the exception is throw and caught, we call
        //            printStackTrace() method to display the trace.

        playerThread = new Thread(new Runnable()
        {
            @Override
            public void run() {
                try {
                    processPlayerTurns();
                } catch (DeficitException e) {
                    e.printStackTrace();
                }
            }
        });
        playerThread.start(); // start the new thread with the provided run() method

    }

    private void processComputerPlayerTurns(Player computerPlayer)
    {
        /*
            TODO: BONUS
             This function is to process the turn of the given computer player. Some hints are as follows.
             Hints:
                1. in this implementation, for each doctor of the computer player, we generate a random int to select
                   what action to carry out. This is a random action with no intelligence!
                2. improve this part by letting the computer player plays more intelligently
                3. up to 6 points out of 100 will be provided as bonus point. You need to explain your approach as
                   comments in this method, otherwise our TA may not be able to know how you improve the intelligence
                   to the computer player.
                4. if your original mark for this PA is 99/100, and if you get 5/6 points, for this bonus part,
                   then you will have 99+5=104, but the ceiling of this PA is capped at 100, so you will not get 104,
                   instead you will get 100.
                5. this bonus is to ensure that even in the case you have careless mistakes, there is still the
                   good chance for you to get 100/100
                6. to facilitate easy marking for this part, please add the comment to the first line of this file
                   main.java, otherwise our TA may not mark this bonus part for you:
                   "I ATTEMPTED THE BONUS PART processComputerPlayerTurns(Player computerPlayer)"
                7. please also *attach a report in your submission* to briefly introduce your implement of the smarter
                   strategy for the computer player, please be precise in the report. We will not be able to consider
                   your bonus work if you do not attach a report.
         */
        Random random = new Random();
        String result;
        int doctor_size =  computerPlayer.getDoctors().size();
        for(int i = 0; i < doctor_size; i++)
        {
            Doctor doctor =  computerPlayer.getDoctors().get(i);
            int actionIndex = random.nextInt(5);
            switch(actionIndex)
            {
                case 0:
                    doctor.goTraining(computerPlayer);
                    result = "Computer player : " + "After training:\n" + doctor + "\n";
                    printResult.add(result);
                    doctor.endTurn();
                    break;

                case 1:
                    String name = computerPlayer.getName()+"Doctor"+game.turns; //newly recruited doctor name will be the string playerName+"Doctor"+turnNumber, for example "JaneDoctor3".
                    doctor.recruitDoctor( computerPlayer, name);
                    int size =  computerPlayer.getDoctors().size();
                    result = "Computer player : " + "A new doctor is recruited " +  computerPlayer.getDoctors().get(size - 1) + "\n";
                    printResult.add(result);
                    doctor.endTurn();
                    break;
                case 2:
                    int dept_num = random.nextInt(3);
                    Department dept =   computerPlayer.getDepartments().get(dept_num);
                    if (dept != null)
                    {
                        doctor.upgradeDepartment( computerPlayer, dept);
                        result = "Computer player : " + doctor.getName() + " upgraded " + dept.getName() + "\n";
                        printResult.add(result);
                    }
                    doctor.endTurn();
                    break;
                case 3:
                    int dept_num_3 = random.nextInt(3);
                    Department dept_3 =   computerPlayer.getDepartments().get(dept_num_3);
                    if (dept_3 != null)
                    {
                        doctor.transferToDepartment(dept_3);
                        result = "Computer player : " + doctor.getName() + " transfers to " + dept_3.getName() + "\n";
                        printResult.add(result);
                    }
                    doctor.endTurn();
                    break;
                case 4:
                    computerPlayer.collectMoney(doctor.raiseFund());
                    doctor.endTurn();
                    result = "Computer player : " +  doctor.getName() + " raise fund";
                    printResult.add(result);
                    break;
            }
            doctor.endTurn();

        }
    }


    private void update()
    {

        // This function is to update the content of listviews. And then have the updated listviews displayed to the UI.
        // We have to use Platform.runLater() since we have to update the UI using the JavaFX thread.
        // to update the UI components, we need to put the updating tasks to the JavaFX thread, only the JavaFX thread can update UI.
        // to let the JavaFX thread doing the updates, we need to use Platform.runLater() and pass it with the tasks defined in the run() method
        // this part will require the multithreading knowledge that we will go through in May to understand completely.

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //System.out.println("UPDATE");
                updateHumanPlayerMoney();
                updateListViewDoctorItems();
                updateListViewHospitalItems();
                updateListViewLogWindow();
                updateListViewComputerPlayer();
                load_figure();
                updateComputerPlayerMoney();
            }
        });
    }

    private void processPlayerTurns() throws DeficitException {
        // This method is to process the turns for human player and computer players. Some explanation of this method is given below
        //    1. The selection of human player is finished by clicked button and listview. So firstly, you have to whether check all human player's doctors are occupied.
        //    2. If all human player's doctors are occupied, call humanPlayer.processAtEndOfTurn();
        //    3. Then it is the turn of computer players, call processComputerPlayerTurns();
        //    4. After human player and computer players finish their selection, call processAtEndOfTurn() for each player and call update() to update UI.
        //    5. Call processAtStartOfTurn() to start a new turn for each player.
        //    6. Call showGameOver() when game over.

        while(!game.isGameOver())
        {
            if(humanPlayer.allDoctorOccupied())
            {

                game.turns ++;
                humanPlayer.processAtEndOfTurn();
                humanPlayer.processAtStartOfTurn(); //prepare for the next turn/season
                update();
                printResult.add("-------------------------------------  Human player ends  -------------------------------------");


                printResult.add("-------------------------------------  Computer player 1 starts-------------------------------------");
                processComputerPlayerTurns(computerPlayer_1);
                computerPlayer_1.processAtEndOfTurn();
                computerPlayer_1.processAtStartOfTurn();
                update();
                printResult.add("-------------------------------------  Computer player 1 ends  -------------------------------------");

                printResult.add("-------------------------------------  Computer player 2 starts------------------------------------- ");
                processComputerPlayerTurns(computerPlayer_2);
                computerPlayer_2.processAtEndOfTurn();
                computerPlayer_2.processAtStartOfTurn();
                update();
                printResult.add("-------------------------------------  Computer player 2 ends  ------------------------------------- ");


            }
        }
        showGameOver();

    }

    private void showGameOver()
    { //This method will be ran when the game is over
        /*
            Showing Gameover.
         */
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //Todo:
                //  step 1. get the winner name using game.getWinner()
                //  step 2. put the string player.getName()+" won the game!" to the string reference called "result"

                Player winner = game.getWinner();
                String result = winner.getName() + " won the game!";


                // pass the string referred by "result" to make an alert window
                // check the bottom of page 9 of the PA description for the appearance of this alert window
                Alert alert = new Alert(AlertType.CONFIRMATION, result, ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES)
                {
                    Platform.exit();
                }
            }
        });
    }


    public static void main(String[] args) {

        launch(args);
    }
}
