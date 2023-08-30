# AndroidUtil

## 1. [Permission Helper](app/src/main/java/com/nakul/androidutil/permission_helper)

### Step 1. Extend the fragment you want to request permission in, with abstract class [PermissionFragment.kt](app/src/main/java/com/nakul/androidutil/permission_helper/PermissionFragment.kt) and pass fragment layout in constructor.

    class SamplePermissionFragment : PermissionFragment(R.layout.fragment_sample_permission)
    {
        ...
        override fun getPermissionData(): PermissionData {
            val permissions = ArrayList<String>()   
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
            }
            return PermissionData(
                permissions = permissions,          // List of permissions required
                alertMessage = "Permission is required", // Rationale Text message 
                disabledMessage = "Permission is disabled" // Text message in case of permission disabled                   
            )
        }
    
        override fun onPermissionGranted() {
            Log.e(this.javaClass.name, "onPermissionGranted")
        }

    }            

###  Step 2. Enjoy


#### Scenarios Handled:
* Permission Rejected
* Permission Permanently Rejected
* Rationale is required
* Permission requested but not given in Manifest




## 2. [Location Helper](app/src/main/java/com/nakul/androidutil/location_helper)


### Step 1. Add dependency
    //Google Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

### Step 2. Download [LocationService.kt](app/src/main/java/com/nakul/androidutil/location_helper/LocationService.kt)

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