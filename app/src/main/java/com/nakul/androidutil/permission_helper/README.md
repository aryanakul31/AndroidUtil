# AndroidUtil

## [Permission Helper](app/src/main/java/com/nakul/androidutil/permission_helper)


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