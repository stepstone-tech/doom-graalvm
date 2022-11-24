package com.stepstone.jc.demo;

import org.graalvm.polyglot.Value;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;

/**
 * Exposes objects and methods that will be called from inside of Doom WASM module.
 * It's java copy of <a href="https://github.com/diekmann/wasm-fizzbuzz/blob/main/doom/main.js#L80">Javascript javascript bindings</a>.
 */
public class Bindings {

    public final JSBindings js;
    public final Env env;

    public Bindings(Value memory, Consumer<BufferedImage> drawCallback) {
        env = new Env(memory);
        js = new JSBindings(memory, drawCallback);
    }

    /**
     * Exposes methods available for WASM module
     */
    @SuppressWarnings("unused")
    public static class JSBindings {
        private final long start;
        private final Value memory;
        private final Consumer<BufferedImage> drawCallback;

        public JSBindings(Value memory, Consumer<BufferedImage> drawCallback) {
            this.start = System.currentTimeMillis();
            this.memory = memory;
            this.drawCallback = drawCallback;
        }

        /**
         * Used by game to track flow of time, to trigger events
         *
         * @return milliseconds from start of game
         */
        public int js_milliseconds_since_start() {
            return (int) (System.currentTimeMillis() - start);
        }

        public void js_console_log(int offset, int length) {
            System.out.println(readWasmString(offset, length));
        }

        public void js_stdout(int offset, int length) {
            System.out.print(readWasmString(offset, length));
        }

        public void js_stderr(int offset, int length) {
            System.err.print(readWasmString(offset, length));
        }

        /**
         * Called when game draws to screen.
         * Fortunately doom screen buffer can be copied directly into {@link BufferedImage} buffer
         *
         * @param ptr pointer to beginning of data in wasm {@link JSBindings#memory}
         */
        public void js_draw_screen(int ptr) {
            int max = Doom.doomScreenWidth * Doom.doomScreenHeight * 4;
            int[] screenData = new int[max];
            for (int i = 0; i < max; i++) {
                byte pixelComponent = memory.readBufferByte(i + ptr);
                screenData[i] = pixelComponent;
            }
            BufferedImage bufferedImage = new BufferedImage(Doom.doomScreenWidth, Doom.doomScreenHeight, TYPE_4BYTE_ABGR);
            bufferedImage.getRaster().setPixels(0, 0, Doom.doomScreenWidth, Doom.doomScreenHeight, screenData);
            drawCallback.accept(bufferedImage);
        }

        private String readWasmString(int offset, int length) {
            byte[] bytes = new byte[length];
            for (int i = offset; i < offset + length; i++) {
                bytes[i - offset] = (memory.readBufferByte(i));
            }
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    public static class Env {
        /**
         * WASM memory that is used to quickly share data between java and WASM
         */
        public final Value memory;

        public Env(Value memory) {
            this.memory = memory;
        }
    }
}
