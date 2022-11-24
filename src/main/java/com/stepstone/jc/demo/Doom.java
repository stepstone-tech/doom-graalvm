package com.stepstone.jc.demo;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Doom {
    static int doomScreenWidth = 640;
    static int doomScreenHeight = 400;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final Context context;
    private final GameWindow gameWindow = new GameWindow();

    public Doom() {
        context = Context.newBuilder()
            .allowAllAccess(true)
            .build();
        context.initialize("wasm");
    }

    public static void main(String[] args) throws Exception {
       new Doom().runGame();
    }

    void runGame() throws IOException {
        EventQueue.invokeLater(() -> gameWindow.setVisible(true));

        // we get main GraalVM's WebAssembly bindings so we can run module_instantiate or mem_alloc
        var webAssembly = context.getPolyglotBindings().getMember("WebAssembly").as(WASMModule.class);

        // load WASM module
        byte[] source = loadWasm();
        var mainModule = webAssembly.module_decode(source);

        // create java->WASM bindings and instantiate module
        Value memory = webAssembly.mem_alloc(108, 1000);
        Bindings bindings = new Bindings(memory, gameWindow::drawImage);
        var doomWASM = webAssembly.module_instantiate(mainModule, Value.asValue(bindings)).as(DoomWASM.class);

        // run main() with doommy argc,argv pointers to set up some variables
        doomWASM.main(0, 0);

        // schedule main game loop
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            gameWindow.drainKeyEvents(event -> doomWASM.add_browser_event(event[0], event[1]));
            doomWASM.doom_loop_step();
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    private static byte[] loadWasm() throws IOException {
        try (var doomWasm = Thread.currentThread().getContextClassLoader().getResourceAsStream("doom.wasm")) {
            return Objects.requireNonNull(doomWasm).readAllBytes();
        }
    }

}
