package com.stepstone.jc.demo;

import org.graalvm.polyglot.Value;

/**
 * Exposes GraalVM's WASM methods.
 * See <a href="https://github.com/oracle/graal/blob/master/wasm/src/org.graalvm.wasm/src/org/graalvm/wasm/api/WebAssembly.java">WebAssembly source</a>
 */
public interface WASMModule {
    /**
     * Loads WASM module from bytes
     * @param src source
     * @return WebAssembly module (underneath it's <a href="https://github.com/oracle/graal/blob/master/wasm/src/org.graalvm.wasm/src/org/graalvm/wasm/WasmModule.java">WasmModule</a>)
     */
    Value module_decode(byte[] src);

    /**
     * Creates instance of WASM module.
     * It's equivalent of <a href="https://developer.mozilla.org/en-US/docs/WebAssembly/JavaScript_interface/instantiate">WebAssembly.instantiate(bufferSource, importObject)</a>
     * @param module WASM module loaded by {@link #module_decode(byte[])}
     * @param importObject object with java bindings, that can be accessed by WASM code
     * @return WASM module instance (<a href="https://github.com/oracle/graal/blob/master/wasm/src/org.graalvm.wasm/src/org/graalvm/wasm/WasmInstance.java">WasmInstance</a>)
     */
    Value module_instantiate(Value module, Object importObject);

    /**
     * Allocates new WASM Memory
     * @param initial initial size
     * @param maximum max size
     * @return created memory (<a href="https://github.com/oracle/graal/blob/master/wasm/src/org.graalvm.wasm/src/org/graalvm/wasm/memory/WasmMemory.java">WasmMemory</a>)
     */
    Value mem_alloc(int initial, int maximum);
}
