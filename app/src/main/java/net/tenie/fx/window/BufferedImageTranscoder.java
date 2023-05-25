package net.tenie.fx.window;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

//import org.apache.batik.transcoder.TranscoderException;
//import org.apache.batik.transcoder.TranscoderInput;
//import org.apache.batik.transcoder.TranscoderOutput;
//import org.apache.batik.transcoder.image.ImageTranscoder;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BufferedImageTranscoder { //extends ImageTranscoder 
//    private BufferedImage img = null;
//    @Override
//    public BufferedImage createImage(int width, int height) {
//        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//    }
//    @Override
//    public void writeImage(BufferedImage img, TranscoderOutput to) throws TranscoderException {
//        this.img = img;
//    }
//    public BufferedImage getBufferedImage() {
//        return img;
//    }
//    
//    
//    public ImageView img() {
//    	ImageView githubImage = new ImageView();
//    	BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
//    	try (InputStream file = getClass().getResourceAsStream("/image/github.svg")) {
//    	    TranscoderInput transIn = new TranscoderInput(file);
//    	    try {
//    	        transcoder.transcode(transIn, null);
//    	        Image img = SwingFXUtils.toFXImage(transcoder.getBufferedImage(), null);
//    	        githubImage.setImage(img);
//    	    } catch (TranscoderException ex) {
//    	        ex.printStackTrace();
//    	    }
//    	}
//    	catch (IOException io) {
//    	    io.printStackTrace();
//    	}
//    	return githubImage;
//    }
//    
//    public Image img2() {
//    	Image img = null;
//    	BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
//    	try (InputStream file = getClass().getResourceAsStream("/image/github.svg")) {
//    	    TranscoderInput transIn = new TranscoderInput(file);
//    	    try {
//    	        transcoder.transcode(transIn, null);
//    	         img = SwingFXUtils.toFXImage(transcoder.getBufferedImage(), null);
//    	    } catch (TranscoderException ex) {
//    	        ex.printStackTrace();
//    	    }
//    	}
//    	catch (IOException io) {
//    	    io.printStackTrace();
//    	}
//    	return img;
//    }
}