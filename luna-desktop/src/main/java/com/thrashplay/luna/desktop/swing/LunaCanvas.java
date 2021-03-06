package com.thrashplay.luna.desktop.swing;

import com.thrashplay.luna.geom.Rectangle;
import com.thrashplay.luna.graphics.FrameManager;
import com.thrashplay.luna.graphics.LunaGraphics;
import com.thrashplay.luna.graphics.RenderCoordinateMapping;
import com.thrashplay.luna.desktop.graphics.Java2DGraphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 * @author Sean Kleinjung
 */
public class LunaCanvas extends Canvas implements FrameManager {
    // the buffer strategy used to handle page flipping
    private BufferStrategy bufferStrategy;

    // the graphics object used to render to the current buffer
    private Graphics2D graphics;

    // the frame buffer our scene is rendered to, will be scaled and displayed on the canvas
    private BufferedImage frameBuffer;

    private int sceneWidth;
    private int sceneHeight;

    public LunaCanvas() {
        this(640, 480);
    }

    public LunaCanvas(int sceneWidth, int sceneHeight) {
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;

        setPreferredSize(new Dimension(sceneWidth, sceneHeight));
    }

    /**
     * Initialize the canvas. This method must be called after it has been made visible.
     */
    public void initialize() {
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
    }

    @Override
    public LunaGraphics beginFrame() {
        if (graphics == null) {
            if (frameBuffer == null) {
                frameBuffer = new BufferedImage(sceneWidth, sceneHeight, BufferedImage.TYPE_4BYTE_ABGR);
            }
            //graphics = (Graphics2D) frameBuffer.getGraphics();

            Rectangle sceneBounds = new RenderCoordinateMapping(sceneWidth, sceneHeight, getWidth(), getHeight()).getSceneBoundsInScreenCoordinates();
            float scale = (float) sceneBounds.getWidth() / sceneWidth;
            AffineTransform tx = AffineTransform.getTranslateInstance(sceneBounds.getLeft(), sceneBounds.getTop());
            tx.scale(scale, scale);

            graphics = (Graphics2D) bufferStrategy.getDrawGraphics();
            graphics.setColor(Color.GRAY);
            graphics.fillRect(0, 0, getWidth(), getHeight());
            graphics.setTransform(tx);

            RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHints(rh);

            // set the clip, so the canvas dimensions can be accessed by Drawable instances
            graphics.setClip(0, 0, sceneWidth, sceneHeight);
        }
        return new Java2DGraphics(graphics);
    }

    @Override
    public void endFrame() {
        if (graphics != null) {
            graphics.dispose();

//            Rectangle sceneBounds = new RenderCoordinateMapping(sceneWidth, sceneHeight, getWidth(), getHeight()).getSceneBoundsInScreenCoordinates();
//            float scale = (float) sceneBounds.getWidth() / sceneWidth;
//            AffineTransform tx = AffineTransform.getTranslateInstance(sceneBounds.getLeft(), sceneBounds.getTop());
//            tx.scale(scale, scale);
//
//            Graphics2D backBufferGraphics = (Graphics2D) bufferStrategy.getDrawGraphics();
//            backBufferGraphics.setColor(Color.GRAY);
//            backBufferGraphics.fillRect(0, 0, getWidth(), getHeight());
//            backBufferGraphics.drawImage(frameBuffer, tx, null);
//            backBufferGraphics.dispose();

            bufferStrategy.show();
            graphics = null;
        }
    }
}
