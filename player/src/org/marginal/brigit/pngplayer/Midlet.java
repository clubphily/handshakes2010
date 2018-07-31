/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.marginal.brigit.pngplayer;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
/**
 * @author phi
 */
public class Midlet extends MIDlet implements CommandListener {

    private PNGPlayerCanvas pngPlayerCanvas;

    public Midlet(){
        pngPlayerCanvas = new PNGPlayerCanvas();
    }

    public void startApp() {
        if (pngPlayerCanvas != null) {
            pngPlayerCanvas.start();
            Command exitCommand = new Command("Exit", Command.EXIT, 0);
            pngPlayerCanvas.addCommand(exitCommand);
            pngPlayerCanvas.setCommandListener(this);
        }
        Display.getDisplay(this).setCurrent(pngPlayerCanvas);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
         if (pngPlayerCanvas != null)
            pngPlayerCanvas.stop();
    }

     public void commandAction(Command c, Displayable s) {
        if (c.getCommandType() == Command.EXIT) {
            destroyApp(true);
            notifyDestroyed();
        }
    }
}
