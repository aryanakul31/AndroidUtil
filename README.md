# AndroidUtil

## [Location Helper](app/src/main/java/com/nakul/androidutil/location_helper)


### Step 1. Add dependency
    //Google Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

### Step 2. Download [LocationService.kt]


### Step 3. Implement interface [ILocationHelper.kt](app/src/main/java/com/nakul/androidutil/location_helper/ILocationHelper.kt) in fragment/activity where location is required.

### Step 4. Create a registerForActivityResult to request turning on GPS
        private val gpsEnabler =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { _ -> }

### Step 4. Override abstract functions
        
        override fun getGPSRequester(): ActivityResultLauncher<IntentSenderRequest> = gpsEnabler // Return GPS requester 
        locationUpdated(location:Location)     // Receive location updates in here

### Step 5. Add below lines of code in AndroidManifest
    
        <manifest 
            ...>
                <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /> 
                <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
                <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" /> //OPTIONAL => Use when location is required in background
                <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
                <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
            
            <application 
                    ...>
                <service android:name=".location_helper.LocationService"/>
    
            </application>
        </manifest>