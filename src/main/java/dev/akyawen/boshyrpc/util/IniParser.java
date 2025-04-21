package dev.akyawen.boshyrpc.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class IniParser {
	private final Properties properties;

    public IniParser(String configString) {
        this.properties = new Properties();
        loadFromString(configString);
    }

    private void loadFromString(String configString) {
        try (StringReader reader = new StringReader(configString)) {
            properties.load(reader);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке конфигурации из стринга: " + e.getMessage());
        }
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }
}
