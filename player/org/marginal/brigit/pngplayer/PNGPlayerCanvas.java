package org.marginal.brigit.pngplayer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.nokia.mid.ui.DeviceControl;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import org.marginal.brigit.bluetooth.Client;
import org.marginal.brigit.bluetooth.Server;

/**
 *
 * @author phi
 */
public class PNGPlayerCanvas extends GameCanvas
        implements Runnable {
    private static final Vector filmRepo = new Vector();
    private static final Random r = new Random();
    // Possible actions
    private static final int WAIT = 0;
    private static final int PLAY = 1;
    private static final int STARTSHARING = 2;
    private static final int STARTRECEIVING = 3;
    private static final int TRYTOSHARE = 4;
    private static final int TRYTORECEIVE = 5;
    private static final int SHARE = 6;
    private static final int RECEIVE = 7;
    private static final int PAINTSHARERECEIVE = 8;
    private static final int START_ORGASM = 9;
    private static final int ORGASM = 10;
    private static final String memoryCardUri =
            System.getProperty("fileconn.dir.memorycard") + "PLAYR/";
    private static final int uriLength = memoryCardUri.length();
    private static final StringBuffer filename = new StringBuffer();
    private static final int CHUNK_SIZE = 1024;
    private static final int MAX_WAIT_TIME = 100;
    private static final int AMOUNT_FILMS = 9;
    private static final int LENGTH_AUGE_LINKS_LOOP = 29;
    private static final int LENGTH_AUGE_RECHTS_LOOP = 29;
    private static final int CONST_HEART = 0;
    private static final int CONST_DOLLAR = 1;

    private volatile boolean mShowing;
    private static volatile int action, newestImage;

    public PNGPlayerCanvas() {
        super(true);
        filename.append(memoryCardUri);

        resetFilmRepo();

        setFullScreenMode(true);
    }

    private final static Image loadImage(final String myFilename) {
        Image resultImg = null;
        try {
            final FileConnection fileConn = (FileConnection) Connector.open(myFilename, Connector.READ);
            // load the image data in memory
            // Read data in CHUNK_SIZE chunks
            final InputStream fis = fileConn.openInputStream();
            final long overallSize = fileConn.fileSize();
            int length = 0;
            byte[] imageData = new byte[0];
            while (length < overallSize) {
                byte[] data = new byte[CHUNK_SIZE];
                int readAmount = fis.read(data, 0, CHUNK_SIZE);
                byte[] newImageData = new byte[imageData.length + CHUNK_SIZE];
                System.arraycopy(imageData, 0, newImageData, 0, length);
                System.arraycopy(data, 0, newImageData, length, readAmount);
                imageData = newImageData;
                length += readAmount;
            }
            fis.close();
            fileConn.close();
            resultImg = Image.createImage(imageData, 0, length);
        } catch (IOException ie) {
        } catch (Exception ie) {
        }
        return resultImg;
    }

    private final static void resetFilmRepo() {
        filmRepo.removeAllElements();
        Integer i = null;
        while (filmRepo.size() < 3) {
            i = new Integer(r.nextInt(AMOUNT_FILMS));
            if (!filmRepo.contains(i)) {
                filmRepo.addElement(i);
            }
        }
    }

    public void start() {
        mShowing = true;
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        final Graphics g = getGraphics();

        final int[] filmLengths = {70, 89, 100, 98, 102, 110, 233, 161, 147};
        int currentFilmLength = 0, duration, randPosInFilmRepo,
                framesPerStep = 1, excitement = 0, differentFilmsInARow = 1,
                currentFrame = 0, currentFilm = 0, lastFilm = 0, counter = 0,
                timeStep = 100;

        long start, end;
        double framesPerStepDouble;
        int emoticon = 0;
        Client btClient = null;
        Server btServer = null;

        action = WAIT;
        newestImage = -1;

        while (mShowing) {
            start = System.currentTimeMillis();

            DeviceControl.setLights(0, 100);
            switch (action) {
                case WAIT:
                    currentFrame = 0;
                    // Orgasm
                    if (excitement > 4000) {
                        action = START_ORGASM;
                        break;
                    }
                    // Connect if boring
                    if (excitement > 20 && excitement < 1500 && differentFilmsInARow == 1) {
                        if (r.nextDouble() > 0.5) {
                            action = STARTSHARING;
                        } else {
                            action = STARTRECEIVING;
                        }
                        break;
                    }
                    lastFilm = currentFilm;
                    if (newestImage > -1) {
                        currentFilm = newestImage;
                        filmRepo.addElement(new Integer(newestImage));
                        newestImage = -1;
                    } else {
                        randPosInFilmRepo = r.nextInt(filmRepo.size() - 1);
                        currentFilm = ((Integer) filmRepo.elementAt(randPosInFilmRepo)).intValue();
                    }
                    currentFilmLength = filmLengths[currentFilm];
                    if (currentFilm == lastFilm) {
                        differentFilmsInARow = 1;
                    } else {
                        differentFilmsInARow++;
                    }
                    excitement += differentFilmsInARow * 10;
                    framesPerStepDouble = Math.ceil(((double) excitement + 1) / 100);
                    framesPerStep = (int) framesPerStepDouble;
                    action = PLAY;
                    // LOG
                    // logDebug("Action: WAIT");
                    // logDebug("fps: " + framesPerStep);
                    // logDebug("exc: " + excitement);
                    // logDebug("currentFilm: " + currentFilm);
                    // logDebug("diff: " + differentFilmsInARow);
                    break;

                case PLAY:
                    getFilename(currentFilm, currentFrame);
                    drawOnScreenImage(g);

                    currentFrame += framesPerStep;
                    if (currentFrame > currentFilmLength) {
                        action = WAIT;
                    }

                    // LOG
                    // logDebug("Action: PLAY");
                    // logDebug("fps: " + framesPerStep);
                    break;

                case STARTSHARING:
                    btServer = new Server();
                    currentFilm = 777;
                    counter = 0;
                    btServer.start(filmRepo);
                    action = TRYTOSHARE;
                    break;
                case STARTRECEIVING:
                    btClient = new Client();
                    currentFilm = 888;
                    counter = 0;
                    btClient.start(filmRepo);
                    action = TRYTORECEIVE;
                    break;
                case TRYTOSHARE:
                    if (counter++ > MAX_WAIT_TIME) {
                        btServer.stop();
                        btServer = null;
                        action = WAIT;
                        differentFilmsInARow = 2;
                    }
                    currentFrame = counter % LENGTH_AUGE_LINKS_LOOP;
                    getFilename(currentFilm, currentFrame);
                    drawOnScreenImage(g);
                    break;

                case TRYTORECEIVE:
                    if (counter++ > MAX_WAIT_TIME) {
                        btClient.stop();
                        btClient = null;
                        action = WAIT;
                        differentFilmsInARow = 2;
                    }
                    currentFrame = counter % LENGTH_AUGE_RECHTS_LOOP;
                    getFilename(currentFilm, currentFrame);
                    drawOnScreenImage(g);
                    break;

                case SHARE:
                    emoticon = CONST_HEART;
                    counter = 0;
                    differentFilmsInARow = 2;
                    action = PAINTSHARERECEIVE;
                    break;
                case RECEIVE:
                    emoticon = CONST_DOLLAR;
                    counter = 0;
                    differentFilmsInARow = 2;
                    action = PAINTSHARERECEIVE;
                    break;
                case PAINTSHARERECEIVE:
                    if (counter % 12 == 0) {
                        drawSymbol(g, emoticon, counter / 12);
                    }
                    if (counter++ == 60) {
                        action = WAIT;
                    }
                    break;
                case START_ORGASM:
                    excitement = 0;
                    differentFilmsInARow = 0;
                    DeviceControl.startVibra(100, 20000);
                    currentFilm = 666;
                    resetFilmRepo();
                    action = ORGASM;
                    break;
                case ORGASM:
                    if (currentFrame++ > 15) {
                        DeviceControl.startVibra(0,0);
                        action = WAIT;
                    }
                    getFilename(currentFilm, currentFrame);
                    drawOnScreenImage(g);
                    break;
            }

            end = System.currentTimeMillis();
            duration = (int) (end - start);

            if (duration < timeStep) {
                try {
                    Thread.sleep(timeStep - duration);
                } catch (InterruptedException ie) {
                    stop();
                }
            }
        }
        if (btServer != null) {
            btServer.stop();
        }
        if (btClient != null) {
            btClient.stop();
        }
    }

    private final static void getFilename(final int currentFilm, final int currentFrame) {
        switch (currentFilm) {
            case 777:
                // Auge links encoded as 777
                filename.append("augelinks");
                break;
            case 888:
                // Auge rechts encoded as 888
                filename.append("augerechts");
                break;
            case 666:
                // Orgasmus encoded as 666
                filename.append("mund");
                break;
            default:
                filename.append(currentFilm + 1);
                break;
        }
        if (currentFrame < 10) {
            filename.append("000");
        } else if (currentFrame < 100) {
            filename.append("00");
        } else if (currentFrame < 1000) {
            filename.append("0");
        }
        filename.append(currentFrame);
        filename.append(".PNG");
    }

    private final void drawOnScreenImage(final Graphics g) {
        final Image mImage = loadImage(filename.toString());
        filename.delete(uriLength, filename.length());
        g.drawImage(mImage, 0, 0, Graphics.TOP | Graphics.LEFT);
        flushGraphics();
    }

    private final void drawSymbol(final Graphics g, final int emoticon, final int num) {

        // we assuem that an image is 30 x 30 px
        final int rectHeight = getHeight()/3;
        final int rectY = getHeight() - rectHeight;
        g.fillRect(0, rectY, getWidth(), rectHeight);
        final Image emoImage = emoticon == CONST_HEART ?
            loadImage(memoryCardUri + "heart.png") :
            loadImage(memoryCardUri + "dollar.png");
        int x = 0;
        for (int i = 0; i < num; i++) {
            x = 15 + i * 45;
            g.drawImage(emoImage, x, rectY + rectHeight/2, Graphics.TOP | Graphics.LEFT);
        }
        flushGraphics();
    }

    public final void stop() {
        mShowing = false;
    }

    public final static void startSharingImage() {
        action = SHARE;
    }

    public final static void startReceivingImage() {
        action = RECEIVE;
    }

    public final static void boughtNewImage(final int imageID) {
        if (imageID > -1) {
            newestImage = imageID;
        }
    }
}
