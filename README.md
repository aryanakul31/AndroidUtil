# AndroidUtil

### 1. [Permission Helper](app/src/main/java/com/nakul/androidutil/permission_helper)

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


### Thank you





### 2. [Location Helper](app/src/main/java/com/nakul/androidutil/location_helper)


#### Step 1. Add dependency
    //Google Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

#### Step 2. Download and save [LocationService.kt](app%2Fsrc%2Fmain%2Fjava%2Fcom%2Fnakul%2Fandroidutil%2Flocation_helper%2FLocationService.kt
*    runOnce
*        true => if location is required only once,
*        false => if location is required repeatedly.
*    interval (Default => 5 secs)

#### Step 3. Download and inherit [BaseLocationFragment.kt](BaseLocationFragment.kt) in fragment where location is required.

#### Step 4. Override abstract functions
    isPermissionGranted() // Check and request permissions
    locationUpdated()     // Receive location updates in here

#### Step 5. Add below lines in AndroidManifest
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
