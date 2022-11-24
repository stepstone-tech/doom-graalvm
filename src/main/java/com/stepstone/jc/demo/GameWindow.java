package com.stepstone.jc.demo;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static com.stepstone.jc.demo.Doom.doomScreenHeight;
import static com.stepstone.jc.demo.Doom.doomScreenWidth;

public class GameWindow extends JFrame {
    private final JPanel innerPanel = new JPanel();
    private final DoomKeyListener keyListener = new DoomKeyListener();

    public GameWindow() throws HeadlessException {
        super("Doom on GraalVM");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setContentPane(innerPanel);
        addKeyListener(keyListener);

        innerPanel.setPreferredSize(new Dimension(doomScreenWidth, doomScreenHeight));
        innerPanel.setBackground(Color.BLACK);
        pack();
    }

    void drawImage(Image image) {
        innerPanel.getGraphics().drawImage(image, 0, 0, innerPanel.getWidth(), innerPanel.getHeight(), null);
    }

    public void drainKeyEvents(Consumer<int[]> action) {
        keyListener.drainEvents(action);
    }
}
