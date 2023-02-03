
import java.awt.Dimension;

import org.json.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

/**
 * The ClientGui class is a GUI frontend that displays an image grid, an input
 * text box, a button, and a text area for status.
 *
 * Methods of Interest ---------------------- show(boolean modal) - Shows the
 * GUI frame with the current state -> modal means that it opens the GUI and
 * suspends background processes. Processing still happens in the GUI. If it is
 * desired to continue processing in the background, set modal to false.
 * newGame(int dimension) - Start a new game with a grid of dimension x
 * dimension size insertImage(String filename, int row, int col) - Inserts an
 * image into the grid appendOutput(String message) - Appends text to the output
 * panel submitClicked() - Button handler for the submit button in the output
 * panel
 *
 * Notes ----------- > Does not show when created. show() must be called to show
 * he GUI.
 *
 */
public class ClientGui implements OutputPanel.EventHandlers {

    JDialog frame;
    PicturePanel picturePanel;
    static OutputPanel outputPanel;
    boolean gameStarted = false;
    String currentMessage;
    static Socket sock;
    static OutputStream out;
    static InputStream in;
    static ObjectOutputStream out1;
    static ObjectInputStream in1;
    String ans;
    int count = 10;
    JSONObject request = null;
    JSONObject response;
    String selectedCity;
    String selectedCountry;
    
    
    
    

    /**
     * Construct dialog
     *
     * @throws IOException
     */
    public ClientGui() throws IOException {
        frame = new JDialog();
        frame.setLayout(new GridBagLayout());
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // setup the top picture frame
        picturePanel = new PicturePanel();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.25;
        frame.add(picturePanel, c);

        // setup the input, button, and output area
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.75;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        outputPanel = new OutputPanel();
        outputPanel.addEventHandlers(this);
        frame.add(outputPanel, c);

        picturePanel.newGame(1);
        insertImage("img/hi.png", 0, 0);
        //conn();

    }

    /**
     * Shows the current state in the GUI
     *
     * @param makeModal - true to make a modal window, false disables modal
     * behavior
     */
    public void show(boolean makeModal) {
        frame.pack();
        frame.setModal(makeModal);
        frame.setVisible(true);
    }

    /**
     * Insert an image into the grid at position (col, row)
     *
     * @param filename - filename relative to the root directory
     * @param row - the row to insert into
     * @param col - the column to insert into
     * @return true if successful, false if an invalid coordinate was provided
     * @throws IOException An error occured with your image file
     */
    public boolean insertImage(String filename, int row, int col) throws IOException {
        System.out.println("Image insert");
        String error = "";
        try {
            // insert the image
            if (picturePanel.insertImage(filename, row, col)) {
                // put status in output
                //outputPanel.appendOutput("Inserting " + filename + " in position (" + row + ", " + col + ")"); // you can of course remove this
                //outputPanel.appendOutput("What is your namr.....?");
                return true;
            }
            error = "File(\"" + filename + "\") not found.";
        } catch (PicturePanel.InvalidCoordinateException e) {
            // put error in output
            error = e.toString();
        }
        outputPanel.appendOutput(error);
        return false;
    }

    public void conn() throws IOException {
        try {
            sock = new Socket("localhost", 8000);
            //outputPanel.appendOutput("Connected...");
            out = sock.getOutputStream();
            in = sock.getInputStream();
            out1 = new ObjectOutputStream(out);
            in1 = new ObjectInputStream(in);
            String msg =(String) in1.readObject();
            outputPanel.appendOutput(msg);
           

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    
     public JSONObject name(String name) {
        JSONObject request = new JSONObject();
        request.put("header", "ok");
        request.put("name", name);
        
        return request;
    }
     
     //requset a city
     public JSONObject requestCity(String city) {
        JSONObject request = new JSONObject();
        request.put("header", "ok");
        request.put("option", city);
        return request;
    }
     
     //reques country
     public JSONObject requestCountry(String country) {
        JSONObject request = new JSONObject();
        request.put("header", "ok");
        request.put("option2", country);
        return request;
    }
     
     
     
     public JSONObject answer(String ans) {
        JSONObject request = new JSONObject();
        request.put("selected", 1);
        request.put("answer", "img//city//"+ans+"jpg");
        return request;
    }
     
     public JSONObject answer2(String ans) {
        JSONObject request = new JSONObject();
        request.put("selected", 1);
        request.put("answer2", "img//country//"+ans+"jpg");
        return request;
    }
     
     public JSONObject leaderBoard() {
        JSONObject request = new JSONObject();
        request.put("header", "ok");
        request.put("data", "123");
        return request;
    }



   
  

    /**
     * Submit button handling
     *
     * TODO: This is where your logic will go or where you will call appropriate
     * methods you write. Right now this method opens and closes the connection
     * after every interaction, if you want to keep that or not is up to you.
     */
    @Override
    public void submitClicked() {
        String input =outputPanel.getInputText();

        
        try {
           
            request = null;
   
            switch(input){
                case "city":
                   request =  requestCity(input);
                   break;
                case "phoenix":
                    request = answer(input);
                    break;
                case "berlin":
                    request = answer(input);
                    break; 
                case "parise":
                    request = answer(input);
                    break; 
                case "rome":
                    request = answer(input);
                    break; 
                case "country": 
                    request = requestCountry(input);
                    break;
                case "germany":
                    request = answer2(input);
                    break;
                case "ireland":
                    request = answer2(input);
                    break; 
                case "southafrica":
                    request = answer2(input);
                    break;
                case "leader":
                    request = leaderBoard();
                    break;
                case "quit":    
                    sock.close();
                    out.close();
                    in.close();
                    System.exit(0);
                    break;
                  
                default:
                    request = name(input);
                    break;
            }
            
           
            if(request != null){
                 byte[] output = JsonUtils.toByteArray(request);
                        NetworkUtils.Send(out, output);
            }
            byte[] messageBytes = NetworkUtils.Receive(in);
             response = JsonUtils.fromByteArray(messageBytes);
             if(response.has("welcome")){
               outputPanel.appendOutput(response.getString("welcome"));
               request = requestCity(input);
               outputPanel.setInputText("");
            }else if(response.has("picture")){
                picturePanel.insertImage(response.getString("picture"), 0, 0);
                outputPanel.setBlanks("-----");
                outputPanel.appendOutput(input);
                outputPanel.setPoints(count);
                outputPanel.setInputText("");
            }else if(response.has("point")){
                outputPanel.setBlanks(input);
                outputPanel.appendOutput(response.getString("comment"));
                outputPanel.setPoints(response.getInt("point")+count);
                outputPanel.setInputText("");
            }
             else if(response.has("Picture2")){
                picturePanel.insertImage(response.getString("Picture2"), 0, 0);
                outputPanel.setBlanks("-----");
                outputPanel.appendOutput(input);
                outputPanel.setPoints(10);
                outputPanel.setInputText("");
            }
             
             else if(response.has("point2")){
                outputPanel.setBlanks(input);
                outputPanel.appendOutput(response.getString("comment2"));
                outputPanel.setPoints(response.getInt("point2")+count);
                outputPanel.setInputText("");
            }
             
             else if(response.has("score")){
                //outputPanel.setBlanks(input);
                
                outputPanel.appendOutput(response.getString("score"));
                //outputPanel.setPoints(response.getInt("point2"));
                outputPanel.setInputText("");
            }
            
           
                        
                        
        } catch (Exception ex) {
            Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
        }
                        

            //String input = outputPanel.getInputText();

            
    }

    /**
     * Key listener for the input text box
     *
     * Change the behavior to whatever you need
     */
    @Override
    public void inputUpdated(String input) {
        if (input.equals("surprise")) {
            try {
                insertImage(response.getString("path") + response.getString("image") + ".jpg", 0, 0);
            } catch (IOException ex) {
                Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
            }
            outputPanel.setBlanks("----");
            outputPanel.setPoints(100);
            outputPanel.appendOutput(outputPanel.getInputText());
        }
    }

    public static void main(String[] args) throws IOException {
        String Phost="54.167.126.26";
        int Pport=8000;
    if (args.length >= 1){ 
      Phost=args[0];
    }
    if (args.length >= 2){
       Pport = Integer.valueOf(args[1]);
    }
    
    try {
            sock = new Socket(Phost, Pport);
            //outputPanel.appendOutput("Connected...");
            out = sock.getOutputStream();
            in = sock.getInputStream();
            out1 = new ObjectOutputStream(out);
            in1 = new ObjectInputStream(in);
            //String msg =(String) in1.readObject();
            //outputPanel.appendOutput(msg);
            String msg =(String) in1.readObject();
            outputPanel.appendOutput(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // create the frame

        try {
            ClientGui main = new ClientGui();
            main.show(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
