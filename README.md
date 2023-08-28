# AndroidUtil

## [Permission Helper](app/src/main/java/com/nakul/androidutil/permission_helper)

### Step 1. Add dependency

    //Dexter
    implementation ("com.karumi:dexter:6.2.3")

### Step 2. Download [PermissionsUtil.kt](app/src/main/java/com/nakul/androidutil/permission_helper/PermissionsUtil.kt) and [PermissionTypes.kt](app/src/main/java/com/nakul/androidutil/permission_helper/PermissionTypes.kt)

### Step 3. Give a title, Add Required Permissions and their respective rational text message to show

    enum class PermissionTypes(val permissions: ArrayList<String>, val alertMessage: String?) {
        NOTIFICATION(getNotificationPermission(), "Notification permission is required."),
    }
    
    
    private fun getNotificationPermission(): ArrayList<String> {
        val list = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission.POST_NOTIFICATIONS
        }   
        return list
    }

## [Location Helper](app/src/main/java/com/nakul/androidutil/location_helper)


### Step 1. Add dependency
    //Google Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

### Step 2. Download and save [LocationService.kt](app%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnakul%2Fandroidutil%2Flocation_helper%2FLocationService.kt)
*** runOnce
**** true => if location is required only once,
**** false => if location is required repeatedly.
*** interval (Default => 5 secs)

### Step 3. Download and inherit [BaseLocationFragment.kt](BaseLocationFragment.kt) in fragment where location is required.

### Step 4. Override abstract functions
    isPermissionGranted() // Check and request permissions
    locationUpdated()     // Receive location updates in here

### Step 5. Add below lines in AndroidManifest
    <manifest 
        ...>
        <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
        <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
        <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
        
        <application 
                ...>
            <service android:name=".location_helper.LocationService"/>

        </application>
    </manifest>