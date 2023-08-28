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