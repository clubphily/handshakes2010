/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.marginal.brigit.pngplayer;

/**
 *
 * @author Administrator
 */
public interface IImageExchange {
    public void startSharingImage();
    public void startReceivingImage();
    //public void finishedSelling();
    public void boughtNewImage(final int imageID);
}
