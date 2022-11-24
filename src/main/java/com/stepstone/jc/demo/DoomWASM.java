package com.stepstone.jc.demo;

/**
 * Interface exposing methods from inside of DOOM WASM module
 */
interface DoomWASM {
    /**
     * Calls main() method, that sets up game.
     * Doom does not expect any command line arguments, so it's safe to call this method as main(0,0)
     * @param argc number of arguments
     * @param argvPtr pointer to argv array
     */
    void main(int argc, int argvPtr);

    /**
     * Executes single run of internal game loop.
     * This method should be called periodically every few milliseconds.
     */
    void doom_loop_step();

    /**
     * Allows to pass key events from host language to doom.
     * See {@link DoomKeyListener}
     * @param type 0 for key down, 1 for key up
     * @param keyCode doom key code
     */
    void add_browser_event(int type, int keyCode);
}
