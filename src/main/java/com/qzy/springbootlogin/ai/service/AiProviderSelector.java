package com.qzy.springbootlogin.ai.service;

import com.qzy.springbootlogin.ai.config.AiProviderConfig;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiProviderSelector {

    private static final Logger log = LoggerFactory.getLogger(AiProviderSelector.class);

    @Autowired
    private AiProviderConfig config;

    @Autowired(required = false)
    private List<AiProviderService> providers;

    private AiProviderService activeProvider;

    @PostConstruct
    public void init() {
        if (providers == null || providers.isEmpty()) {
            log.warn("No AI providers available");
            return;
        }
        String activeName = config.getActiveProvider();
        for (AiProviderService p : providers) {
            if (p.getProviderName().equalsIgnoreCase(activeName) && p.isAvailable()) {
                activeProvider = p;
                log.info("Active AI provider: {}", activeName);
                return;
            }
        }
        // fallback: first available
        for (AiProviderService p : providers) {
            if (p.isAvailable()) {
                activeProvider = p;
                log.info("Fallback AI provider: {}", p.getProviderName());
                return;
            }
        }
        log.warn("No available AI provider found");
    }

    public AiProviderService getProvider() {
        if (activeProvider == null) {
            throw new RuntimeException("No AI provider available. Configure ai.tongyi.api-key or ai.wenxin.api-key");
        }
        return activeProvider;
    }

    public boolean isAvailable() {
        return activeProvider != null;
    }
}
