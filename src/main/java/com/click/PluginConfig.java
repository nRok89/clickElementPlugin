package com.click;

public class PluginConfig {
    private static String clickMethod = "defaultClickMethod"; // Domyślna wartość

    // Getter dla clickMethod
    public static String getClickMethod() {
        return clickMethod;
    }

    // Setter dla clickMethod
    public static void setClickMethod(String newClickMethod) {
        clickMethod = newClickMethod;
    }

    // Metoda do wczytywania konfiguracji z pliku lub innego źródła (jeśli potrzebna)
    public static void loadConfig() {
        // Możesz dodać tutaj kod do wczytywania z pliku lub innego źródła
        // W naszym przypadku, do zmiany clickMethod używamy setterów
    }
}