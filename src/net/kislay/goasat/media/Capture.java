/*
 This file is part of Peers, a java SIP softphone.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
 Copyright 2008, 2009, 2010, 2011 Yohann Martineau 
 */
package net.kislay.goasat.media;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import javax.sound.sampled.*;

import net.kislay.goasat.Logger;
import net.kislay.goasat.gui.MainFrame;

public class Capture implements Runnable {

    private static int sampleRate = 8000;
    public static final int SAMPLE_SIZE = 16;
    public static final int BUFFER_SIZE = SAMPLE_SIZE * 20;
    private PipedOutputStream rawData;
    private boolean isStopped;
    private SoundManager soundManager;
    private Logger logger;
    private CountDownLatch latch;
    private static AudioFormat audioFormat;// = new AudioFormat(sampleRate, 16, 1, true, false);
    private SourceDataLine line;

    public Capture(PipedOutputStream rawData, SoundManager soundManager,
            Logger logger, CountDownLatch latch) throws FileNotFoundException {
        this.rawData = rawData;
        this.soundManager = soundManager;
        this.logger = logger;
        this.latch = latch;
        isStopped = false;

        try {
            getLine();
        } catch (LineUnavailableException ex) {
            java.util.logging.Logger.getLogger(Capture.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getLine() throws LineUnavailableException {
        audioFormat = new AudioFormat(sampleRate, 16, 1, true, false);
        line = AudioSystem.getSourceDataLine(audioFormat);
        line.open(audioFormat);
        line.start();
    }

    public void run() {
        byte[] buffer;

        while (!isStopped) {
            if (!MainFrame.MIC_ON) {// this condition is added by kislay to manipulate microphone and automated voice service..
                try {
                    byte[] byteArray = getArrayFromFile();
                    rawData.write(byteArray);
                    line.write(byteArray, 0, byteArray.length);
                    Thread.sleep(1);
                    rawData.flush();
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(Capture.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                buffer = soundManager.readData();
                try {
                    if (buffer == null) {
                        break;
                    }
                    rawData.write(buffer);
                    rawData.flush();
                } catch (IOException e) {
                    logger.error("input/output error", e);
                    return;
                }
            }
        }

    }

    public synchronized void setStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }

    private static byte[] getArrayFromFile() throws IOException {
        int size = 18;
        //  System.out.println(size);
        byte[] byteArray = new byte[size];
        MainFrame.filein.read(byteArray, 0, size);
//        filein.read(byteArray, size, size);
//        for (int i = 0; i < size; i++) {
//            byteArray[i] = (byte) (Math.random() * 200f);
//        }
        return byteArray;
    }
}
