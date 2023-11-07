# Basic R8 rules for overall project structure
-allowaccessmodification
-flattenpackagehierarchy
-mergeinterfacesaggressively

# Remove intrinsic assertions
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkExpressionValueIsNotNull(...);
    public static void checkNotNullExpressionValue(...);
    public static void checkParameterIsNotNull(...);
    public static void checkNotNullParameter(...);
    public static void checkFieldIsNotNull(...);
    public static void checkReturnedValueIsNotNull(...);
}