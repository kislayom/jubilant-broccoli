/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package siprobotphn;
import java.io.File;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
/**
 *
 * @author KISLAY
 */
public class SIPROBOTPHN {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] argv) throws Exception {
    AudioInputStream stream = AudioSystem.getAudioInputStream(new File("E:\\funky.wav"));
//    stream = AudioSystem.getAudioInputStream(new URL(
  //      "http://hostname/audiofile"));

    AudioFormat format = stream.getFormat();
    if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
      format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format
          .getSampleRate(), format.getSampleSizeInBits() * 2, format
          .getChannels(), format.getFrameSize() * 2, format.getFrameRate(),
          true); // big endian
      stream = AudioSystem.getAudioInputStream(format, stream);
    }

    SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, stream
        .getFormat(), ((int) stream.getFrameLength() * format.getFrameSize()));
    SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
    line.open(stream.getFormat());
    line.start();

    int numRead = 0;
    byte[] buf = new byte[line.getBufferSize()];
    while ((numRead = stream.read(buf, 0, buf.length)) >= 0) {
      int offset = 0;
      while (offset < numRead) {
        offset += line.write(buf, offset, numRead - offset);
      }
    }
    line.drain();
    line.stop();
  }
}
