
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.json.*;

public class TCPServer {
    
    static int points = 0;
    static int points2 = 10;

    /*
   * request: { "selected": <int: 1=joke, 2=quote, 3=image, 4=random> }
   * 
   * response: {"datatype": <int: 1-string, 2-byte array>, "type": <"joke",
   * "quote", "image">, "data": <thing to return> }
   * 
   * error response: {"error": <error string> }
     */
    
     
     
     public static JSONObject selectOption(String name) {
        JSONObject json = new JSONObject();
        json.put("selected", 2);
        json.put("welcome", "hello "+name+" Would you like a City, or a Country, or a Leader Board?");
        return json;
    }
//select random city pictures
    public static ImageIcon randomImage() throws IOException {
        Random rand = new Random();
        int random = rand.nextInt(4);
        //JSONObject json = new JSONObject();
        
        if (random == 0) {
           ImageIcon phoenix = new ImageIcon("img//city//phoenix.jpg");
           return phoenix;
        } else if (random == 1) {
            ImageIcon paris = new ImageIcon("img//city//paris.jpg");
            return paris;
        } else if (random == 2) {
            ImageIcon berlin = new ImageIcon("img//city//berlin.jpg");
            return berlin;
        }else if(random == 3){
          ImageIcon rome = new ImageIcon("img//city//rome.jpg");
          return rome;
        } 
        else{
            return new ImageIcon("not found");
        }
        
       
       
    }
    
    //select random country picturs
    public static ImageIcon randomImage2() throws IOException {
        Random rand = new Random();
        int random = rand.nextInt(3);
        //JSONObject json = new JSONObject();
        
        if (random == 0) {
           ImageIcon germany = new ImageIcon("img//country//germany.jpg");
           return germany;
        } else if (random == 1) {
            ImageIcon ireland = new ImageIcon("img//country//ireland.jpg");
            return ireland;
        } else if (random == 2) {
            ImageIcon southafrica = new ImageIcon("img//city//southafrica.jpg");
            return southafrica;
        }
        else{
            return new ImageIcon("not found");
        }
    }

//return select city picture
     public static JSONObject selectedPicture() throws IOException {
        JSONObject json = new JSONObject();
        json.put("picture", randomImage());
        return json;
    }
    //return select country picture 
      public static JSONObject selectedPicture2() throws IOException {
        JSONObject json = new JSONObject();
        json.put("Picture2", randomImage2());
        return json;
    }
     
     public static JSONObject checkAnswer(int point, String comment) throws IOException {
        JSONObject json = new JSONObject();
        json.put("point", point);
        json.put("comment", comment);
        return json;
    }
     
     
     public static JSONObject checkAnswer2(int point, String comment) throws IOException {
        JSONObject json = new JSONObject();
        json.put("point2", point);
        json.put("comment2", comment);
        return json;
    }
     
     public static JSONObject board(String point) {
        JSONObject json = new JSONObject();
        json.put("score", point);
        return json;
    }
     
     
   
    public static JSONObject error(String err) {
        JSONObject json = new JSONObject();
        json.put("error", err);
        return json;
    }
    
   

    public static void main(String[] args) throws IOException {
        int Pport=8000;
      if (args.length >= 1){
       Pport = Integer.valueOf(args[0]);
    }  
        ServerSocket serv = null;
        try {
            serv = new ServerSocket(Pport);
            // NOTE: SINGLE-THREADED, only one connection at a time

            while (true) {
                Socket sock = null;
                try {
                    System.out.println("Server connected==>");
                    sock = serv.accept(); // blocking wait
                    OutputStream out = sock.getOutputStream();
                    InputStream in = sock.getInputStream();
                    ObjectOutputStream st = new ObjectOutputStream(out);
                    ObjectInputStream sn = new ObjectInputStream(in);
                    st.writeObject("hello what is your name?");
                    st.flush();

                    while (true) {
                        JSONObject returnMessage=null;
                        
                       
                        byte[] messageBytes = NetworkUtils.Receive(in);
                        JSONObject message = JsonUtils.fromByteArray(messageBytes);
                        
                        
                        
                        
                       if(message.has("name")){
                           returnMessage = selectOption(message.getString("name"));
                       }else if(message.has("option")){
                           if(message.getString("option").equals("city")){
                               returnMessage =selectedPicture();
                           }
                       }
                       
                       else if(message.has("option2")){
                           if(message.getString("option2").equals("country")){
                               returnMessage =selectedPicture2();
                           }
                       }
                       
                       else if(message.has("answer")){
                           //String translate=selectedPicture();
                           if(message.getString("answer").equals(selectedPicture())){
                             returnMessage =  checkAnswer(points+5,"Correct");
                           }
                           
                           else{
                               returnMessage =  checkAnswer(points-5,"Incorrect");
                           }
                       }
                       
                       
                       else if(message.has("answer2")){
                           //String translate=selectedPicture();
                           if(message.getString("answer2").equals(selectedPicture())){
                             returnMessage =  checkAnswer2(points+5,"Correct");
                           }
                           
                           else{
                               returnMessage =  checkAnswer2(points-5,"Incorrect");
                           }
                       }
                       
                       else if(message.has("data")){
                           
                               returnMessage =board("your score is: "+points);
                           
                       }
                       
                       
                       
                         
                        byte[] output = JsonUtils.toByteArray(returnMessage);
                        NetworkUtils.Send(out, output);
                      
                    //}
                       
                       
                    }
                } catch (Exception e) {
                    System.out.println("Client disconnect");
                } finally {
                    if (sock != null) {
                        sock.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serv != null) {
                serv.close();
            }
        }
    }
}
