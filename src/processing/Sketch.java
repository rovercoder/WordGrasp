package processing;

import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import processing.core.*;
import TUIO.*;

public class Sketch extends PApplet {

    PImage menuImage;
    PFont welcome;
    int screensizex, screensizey, stage;
    int time;
    int welcomeDelay = 3000;
    ConfigLoaderSaver config = new ConfigLoaderSaver();
    HashMap<String, Multimap<String, String>> currentProfiles = new HashMap<>();
    HashMap<String, String> saves;
    
    //// TUIO ////
    
    TuioProcessing tuioClient;

    float cursor_size = 15;
    float object_size = 60;
    float table_size = 760;
    float scale_factor = 1;
    PFont font;

    boolean verbose = false;
    boolean callback = true;
    
    //////////////
    
    boolean init = false;
    String word = "";

    @Override
    public void settings() {

        fullScreen();

    }

    @Override
    public void setup() {
        stage = 0;
        screensizex = width;
        screensizey = height;
        menuImage = loadImage("menu.jpg");
        image(menuImage, 0, 0, screensizex, screensizey);
        welcome = loadFont("ArialMT-48.vlw");
        textFont(welcome);
        time = millis();
        
        //// TUIO ////
        
        if (!callback) {
          frameRate(60);
          loop();
        } else noLoop();
        
        font = createFont("Arial", 18);
        scale_factor = height/table_size;
        tuioClient  = new TuioProcessing(this);
        
        //////////////
        
    }

    @Override
    public void draw() {

        //// TUIO ////
        
        background(255);
        textFont(font,18*scale_factor);
        float obj_size = object_size*scale_factor; 
        float cur_size = cursor_size*scale_factor; 

        ArrayList<TuioObject> tuioObjectList = tuioClient.getTuioObjectList();
        for (int i=0;i<tuioObjectList.size();i++) {
            TuioObject tobj = tuioObjectList.get(i);
            stroke(0);
            fill(0,0,0);
            pushMatrix();
            translate(tobj.getScreenX(width),tobj.getScreenY(height));
            rotate(tobj.getAngle());
            rect(-obj_size/2,-obj_size/2,obj_size,obj_size);
            popMatrix();
            fill(255);
            text(""+tobj.getSymbolID(), tobj.getScreenX(width), tobj.getScreenY(height));
        }

        ArrayList<TuioCursor> tuioCursorList = tuioClient.getTuioCursorList();
        for (int i=0;i<tuioCursorList.size();i++) {
            TuioCursor tcur = tuioCursorList.get(i);
            ArrayList<TuioPoint> pointList = tcur.getPath();

            if (pointList.size()>0) {
                stroke(0,0,255);
                TuioPoint start_point = pointList.get(0);
                for (int j=0;j<pointList.size();j++) {
                    TuioPoint end_point = pointList.get(j);
                    line(start_point.getScreenX(width),start_point.getScreenY(height),end_point.getScreenX(width),end_point.getScreenY(height));
                    start_point = end_point;
                }

                stroke(192,192,192);
                fill(192,192,192);
                ellipse( tcur.getScreenX(width), tcur.getScreenY(height),cur_size,cur_size);
                fill(0);
                text(""+ tcur.getCursorID(),  tcur.getScreenX(width)-5,  tcur.getScreenY(height)+5);
            }
        }
         
        //////////////
        
        if (stage == 0) {
            textAlign(CENTER);
            text("WELCOME", width / 2, height / 2);
            if ((millis() - time) >= welcomeDelay) {
                stage = 1;
                image(menuImage, 0, 0, screensizex, screensizey);
                textFont(welcome, 24);
                time = millis();//also update the stored time
                //background(1);
                //image(menuImage,0,0,screensizex,screensizey);
            }
        } else if (stage == 1) {
            /*saves = (new ConfigLoader()).load();
             while(totalx<screensizeX && totaly<screensizeY) {


             }*/

        //rect(1,2,200,300);    
        //rect = width/90;
        //rect()
            textAlign(CENTER);
            text("WORDSEARCH \nPress any key to START GAME", width / 2, height / 2);

            if (keyPressed) {
                stage = 2;
            }

        } else if (stage == 2) {

            //choose1Por2PMode();
            FillInTheBlanksGame();

        }

    }

    public void MainMenu() {

        //choose game
        //1) Fill In the Blanks
        //2) Story Mode
        //3) Vocabulary List
    }

    public void choose1Por2PMode() {

        int boxSide = 300;

        rect((width / 2) - boxSide - 40, (height / 2) - (boxSide / 2), boxSide, boxSide, 30);
        rect((width / 2) + 40, (height / 2) - (boxSide / 2), boxSide, boxSide, 30);

    }

    public void chooseProfile() {

        HashMap<String, Multimap<String, String>> profiles = config.loadProfiles();

        //print profile names with letters
        //user can scroll through profile names to choose or create a new profile
        //points will be written under each profile
        
        currentProfiles.clear();
        //for each chosen profile do -> currentProfiles.put(chosenProfile,profiles.get(chosenProfile));
    }

    public void createProfile() {

        //Place fiducials on table to create name
        String profile = "";

        //Save text file with new info
        config.createProfile(profile);

    }

    public void FillInTheBlanksGame() {

        int state = 0;
        
        PImage letterBackDefault = null;
        PImage letterBackCorrect = null;
        PImage letterBackTurn = null;
        PImage letterBackIncorrect = null;
        
        int letterBackSize = 100;
        
        if (!init) {
        
            init = true;
            
            HashMap<String, HashMap<String, String>> themeImageWords = config.loadFillInTheBlanks();

            //Choose theme
            //Start game

            String game = "fillintheblanks";
            String theme = "animals";

            HashMap<String, String> wordImages = themeImageWords.get(theme);

            word = "cat";

            int imageFrameWidth = 300;
            int imageFrameHeight = 300;

            PImage img = loadImage(wordImages.get(word));
            if (img.width > img.height) imageFrameHeight = (int)((float)((float)imageFrameWidth/img.width)*img.height);
            else if (img.width < img.height) imageFrameWidth = (int)((float)((float)imageFrameHeight/img.height)*img.width);

            image(img, (width/2)-imageFrameWidth, (height/2)-(imageFrameHeight/2)-80, imageFrameWidth, imageFrameHeight);

            letterBackDefault = loadImage("/images/letter_background_default.png");
            letterBackCorrect = loadImage("/images/letter_background_correct.png");
            letterBackTurn = loadImage("/images/letter_background_turn.png");
            letterBackIncorrect = loadImage("/images/letter_background_incorrect.png");
            
        }
        
        for(int i = 0; i < word.length(); i++) {

            state = checkState(i,word);
            
            switch(state) {

                case 0: image(letterBackDefault, (width/2)-(letterBackSize/2)-(letterBackSize*(word.length()/2))+(letterBackSize*i), (height/2)-(letterBackSize/2)+100, letterBackSize, letterBackSize);
                break;
                case 1: image(letterBackCorrect, (width/2)-(letterBackSize/2)-(letterBackSize*(word.length()/2))+(letterBackSize*i), (height/2)-(letterBackSize/2)+100, letterBackSize, letterBackSize);
                break;
                case 2: image(letterBackTurn, (width/2)-(letterBackSize/2)-(letterBackSize*(word.length()/2))+(letterBackSize*i), (height/2)-(letterBackSize/2)+100, letterBackSize, letterBackSize);
                break;
                case 3: image(letterBackIncorrect, (width/2)-(letterBackSize/2)-(letterBackSize*(word.length()/2))+(letterBackSize*i), (height/2)-(letterBackSize/2)+100, letterBackSize, letterBackSize);
                break;
                    
            }


        }
        
        
        //Display word and image (preferably not already in vocab list)
        //Check if word is correct
        //If 2P Mode then check which fiducials belong to whom
        //Increment points accordingly and save to text file
        //Next word (10 words per session) + Extra bonus points for completing session
        
    }

    public void StoryModeGame() {

        HashMap<String, HashMap<String, String>> themeImageWords = config.loadFillInTheBlanks();

        //Choose theme
        //Start game
        //Display word and image (preferably not already in vocab list)
        //Check if word is correct
        //If 2P Mode then check which fiducials belong to whom
        //Increment points and save to text file
        //Next word (10 words per session)
    }

    public void VocabList() {

        //User scrolls through his own vocab list
        //He can view associated picture and an example sentence where that word is used
    }

    public int checkState(int index, String word) {
        
        //checkFiducials();
        
        return 0;
        
    }
    
    public List<String> checkFiducial() {

        //check it
        return Arrays.asList("LETTER", "A", "RED"); //eg

    }

    public HashMap<int[], List<String>> checkFiducials() {

        //check it
        int[] coords = new int[2];

        coords[0] = 0; //width
        coords[1] = 0; //height

        List<String> fiducial = Arrays.asList("LETTER", "A", "RED");

        HashMap<int[], List<String>> result = new HashMap<>();
        result.put(coords, fiducial);

        return result; //eg

    }
    
    //// TUIO ////
    
    public void addTuioObject(TuioObject tobj) {
      if (verbose) println("add obj "+tobj.getSymbolID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle());
    }

    public void updateTuioObject (TuioObject tobj) {
      if (verbose) println("set obj "+tobj.getSymbolID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle()
              +" "+tobj.getMotionSpeed()+" "+tobj.getRotationSpeed()+" "+tobj.getMotionAccel()+" "+tobj.getRotationAccel());
    }

    public void removeTuioObject(TuioObject tobj) {
      if (verbose) println("del obj "+tobj.getSymbolID()+" ("+tobj.getSessionID()+")");
    }

    public void addTuioCursor(TuioCursor tcur) {
      if (verbose) println("add cur "+tcur.getCursorID()+" ("+tcur.getSessionID()+ ") " +tcur.getX()+" "+tcur.getY());
      //redraw();
    }

    public void updateTuioCursor (TuioCursor tcur) {
      if (verbose) println("set cur "+tcur.getCursorID()+" ("+tcur.getSessionID()+ ") " +tcur.getX()+" "+tcur.getY()
              +" "+tcur.getMotionSpeed()+" "+tcur.getMotionAccel());
      //redraw();
    }

    public void removeTuioCursor(TuioCursor tcur) {
      if (verbose) println("del cur "+tcur.getCursorID()+" ("+tcur.getSessionID()+")");
      //redraw()
    }
    
    public void addTuioBlob(TuioBlob tblb) {
      if (verbose) println("add blb "+tblb.getBlobID()+" ("+tblb.getSessionID()+") "+tblb.getX()+" "+tblb.getY()+" "+tblb.getAngle()+" "+tblb.getWidth()+" "+tblb.getHeight()+" "+tblb.getArea());
      //redraw();
    }

    public void updateTuioBlob (TuioBlob tblb) {
      if (verbose) println("set blb "+tblb.getBlobID()+" ("+tblb.getSessionID()+") "+tblb.getX()+" "+tblb.getY()+" "+tblb.getAngle()+" "+tblb.getWidth()+" "+tblb.getHeight()+" "+tblb.getArea()
              +" "+tblb.getMotionSpeed()+" "+tblb.getRotationSpeed()+" "+tblb.getMotionAccel()+" "+tblb.getRotationAccel());
      //redraw()
    }

    public void removeTuioBlob(TuioBlob tblb) {
      if (verbose) println("del blb "+tblb.getBlobID()+" ("+tblb.getSessionID()+")");
      //redraw()
    }

    public void refresh(TuioTime frameTime) {
      if (verbose) println("frame #"+frameTime.getFrameID()+" ("+frameTime.getTotalMilliseconds()+")");
      if (callback) redraw();
    }

    //////////////
    
}
