// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    //Newly added line
    id("com.google.gms.google-services") version "4.4.1" apply false
    //End of newly added line
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}