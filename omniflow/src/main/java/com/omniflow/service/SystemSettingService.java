package com.omniflow.service;

import com.omniflow.model.SystemSetting;
import com.omniflow.repository.SystemSettingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SystemSettingService {

    private final SystemSettingRepository settingRepository;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public SystemSettingService(SystemSettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @PostConstruct
    public void init() {
        seedDefaultsIfMissing();
        refreshCache();
    }

    public synchronized void refreshCache() {
        cache.clear();
        for (SystemSetting s : settingRepository.findAll()) {
            cache.put(s.getSettingKey(), s.getSettingValue());
        }
    }

    public String getSetting(String key, String defaultValue) {
        return cache.getOrDefault(key, defaultValue);
    }

    @Transactional
    public void updateSetting(String key, String value) {
        SystemSetting s = settingRepository.findBySettingKey(key)
                .orElse(new SystemSetting(key, value, "System Configuration", "GENERAL"));
        s.setSettingValue(value != null ? value.trim() : "");
        settingRepository.save(s);
        cache.put(key, s.getSettingValue());
    }

    @Transactional
    public void updateAll(Map<String, String> updates) {
        for (Map.Entry<String, String> entry : updates.entrySet()) {
            updateSetting(entry.getKey(), entry.getValue());
        }
    }

    public Map<String, String> getAllSettings() {
        return new HashMap<>(cache);
    }

    private void seedDefaultsIfMissing() {
        Map<String, String[]> defaults = new HashMap<>();
        defaults.put("APP_NAME", new String[]{"OmniFlow Logistics ERP", "Main application title", "BRANDING"});
        defaults.put("APP_TAGLINE", new String[]{"Intelligent Supply Chain & Verifiable Inventory", "Short branding subtitle", "BRANDING"});
        defaults.put("APP_LOGO_URL", new String[]{"", "Custom Logo URL (leave empty for built-in modern SVG logo)", "BRANDING"});
        defaults.put("PRIMARY_THEME", new String[]{"theme-indigo", "Theme styling: theme-indigo, theme-emerald, theme-violet, theme-crimson, theme-dark", "BRANDING"});
        defaults.put("CURRENCY_SYMBOL", new String[]{"$", "Base financial currency symbol ($ , € , £ , ৳ , ₹)", "FINANCIAL"});
        defaults.put("COMPANY_NAME", new String[]{"OmniFlow Global Solutions Ltd.", "Full registered company name", "BRANDING"});
        defaults.put("COMPANY_ADDRESS", new String[]{"742 Evergreen Terrace, Logistics Hub, Suite 400", "Official business address", "BRANDING"});
        defaults.put("COMPANY_EMAIL", new String[]{"support@omniflow.io", "Official contact email", "BRANDING"});
        defaults.put("COMPANY_PHONE", new String[]{"+1 (800) 555-0199", "Official contact hotline", "BRANDING"});
        defaults.put("DEFAULT_TAX_PERCENT", new String[]{"7.5", "Standard sales tax percentage (%)", "FINANCIAL"});
        defaults.put("MAINTENANCE_MODE", new String[]{"false", "Maintenance mode active banner", "SYSTEM"});

        for (Map.Entry<String, String[]> entry : defaults.entrySet()) {
            if (settingRepository.findBySettingKey(entry.getKey()).isEmpty()) {
                settingRepository.save(new SystemSetting(
                        entry.getKey(),
                        entry.getValue()[0],
                        entry.getValue()[1],
                        entry.getValue()[2]
                ));
            }
        }
    }
}

