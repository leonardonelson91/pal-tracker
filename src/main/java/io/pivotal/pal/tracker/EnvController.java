package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {

    private final String port;
    private final String memoryLimit;
    private final String cfInstanceIndex;
    private final String cfInstanceAddr;

    private Map<String, String> environmentMap;

    public EnvController(@Value("${PORT:NOT SET}") String port,
                         @Value("${MEMORY_LIMIT:not defined}") String memoryLimit,
                         @Value("${CF_INSTANCE_INDEX: not defined}") String cfInstanceIndex,
                         @Value("${CF_INSTANCE_ADDR:not defined}") String cfInstanceAddr) {
        this.port = port;
        this.memoryLimit = memoryLimit;
        this.cfInstanceIndex = cfInstanceIndex;
        this.cfInstanceAddr = cfInstanceAddr;

        environmentMap = new HashMap<>();
        environmentMap.put("PORT", port);
        environmentMap.put("MEMORY_LIMIT", memoryLimit);
        environmentMap.put("CF_INSTANCE_INDEX", cfInstanceIndex);
        environmentMap.put("CF_INSTANCE_ADDR", cfInstanceAddr);
    }

    @GetMapping("/env")
    public Map<String, String> getEnv() {
        return environmentMap;
    }
}
