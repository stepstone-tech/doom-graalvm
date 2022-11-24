package com.stepstone.jc.demo;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * AWT {@link java.awt.event.KeyListener} that queues all key events, that later on can be passed to game.
 * See {@link DoomWASM#add_browser_event(int, int)}
 */
class DoomKeyListener extends KeyAdapter {
    private final Queue<int[]> keyEvents = new LinkedList<>();

    @Override
    public void keyPressed(KeyEvent e) {
        keyEvents.add(new int[]{0, toDoomKeyCode(e.getKeyCode())});
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyEvents.add(new int[]{1, toDoomKeyCode(e.getKeyCode())});
    }

    synchronized public void drainEvents(Consumer<int[]> action) {
        keyEvents.forEach(action);
        keyEvents.clear();
    }

    private int toDoomKeyCode(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_ESCAPE:
                return 127; // KEY_BACKSPACE
            case KeyEvent.VK_ENTER:
                return 13; // ENTER
            case 17:
                return (0x80 + 0x1d); // KEY_RCTRL
            case 18:
                return (0x80 + 0x38); // KEY_RALT
            case 37:
                return 0xac; // KEY_LEFTARROW
            case 38:
                return 0xad; // KEY_UPARROW
            case 39:
                return 0xae; // KEY_RIGHTARROW
            case 40:
                return 0xaf; // KEY_DOWNARROW
            default:
                if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
                    return keyCode + 32; // ASCII to lower case
                }
                if (keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F12) {
                    return keyCode + 75; // KEY_F1
                }
                return keyCode;
        }
    }
}
