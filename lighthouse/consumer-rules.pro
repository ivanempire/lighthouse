# Basic R8 rules for overall project structure
-allowaccessmodification
-flattenpackagehierarchy
-mergeinterfacesaggressively

# Remove all log statements but Log.e()
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int w(...);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Remove intrinsic assertions
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkExpressionValueIsNotNull(...);
    public static void checkNotNullExpressionValue(...);
    public static void checkParameterIsNotNull(...);
    public static void checkNotNullParameter(...);
    public static void checkFieldIsNotNull(...);
    public static void checkReturnedValueIsNotNull(...);
}