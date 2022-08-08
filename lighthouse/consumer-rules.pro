# Remove all log statements but Log.e()
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int w(...);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}