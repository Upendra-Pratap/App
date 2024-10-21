package  com.pasco.pascocustomer.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


class PreferenceManager(context: Context) {

    private var mPrefs: PreferenceManager? = null


    private val masterKeyAlias: String = MasterKey.DEFAULT_MASTER_KEY_ALIAS
    private val masterKey: MasterKey =
        MasterKey.Builder(context, masterKeyAlias).setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()


    private val prefs = EncryptedSharedPreferences.create(
        context, "myPrefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /* private var masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
         .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
         .build()

     private var prefs = EncryptedSharedPreferences.create(
         context,
         "PromotrEncryptedPreferences",
         masterKey,
         EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
         EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
     )
 */

    private val editor = prefs.edit()

    fun getInstance(context: Context): PreferenceManager {
        if (mPrefs == null) {
            synchronized(PreferenceManager::class.java) {
                if (mPrefs == null) mPrefs = PreferenceManager(context)
            }
        }
        return mPrefs!!
    }

    // region "Getters & Setters"
    var isFirstTime: Boolean
        get() = prefs.getBoolean(IS_FIRST_TIME, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME, isFirstTime)
            editor.apply()
        }

    var appLanguage: String
        get() = prefs.getString(USER_LANG, "") ?: ""
        set(appLanguage) {
            editor.putString(USER_LANG, appLanguage)
            editor.apply()
        }


    var token: String
        get() = prefs.getString(RefreshToken, "") ?: ""
        set(userToken) {
            editor.putString(RefreshToken, userToken)
            editor.apply()
        }

    var bearerToken: String
        get() = prefs.getString(BEARER_USER_TOKEN, "") ?: ""
        set(userToken) {
            editor.putString(BEARER_USER_TOKEN, userToken)
            editor.apply()
        }
    var approvedId: String
        get() = prefs.getString(APPROVEDID, "") ?: ""
        set(approvedId) {
            editor.putString(APPROVEDID, approvedId)
            editor.apply()
        }

    var driverApprovedId: String
        get() = prefs.getString(DRIVERAPPROVEDID, "") ?: ""
        set(driverApprovedId) {
            editor.putString(DRIVERAPPROVEDID, driverApprovedId)
            editor.apply()
        }

    var userId: String
        get() = prefs.getString(USER_ID, "") ?: ""
        set(userId) {
            editor.putString(USER_ID, userId)
            editor.apply()
        }

    var CheckedType: String
        get() = prefs.getString(CHECKED_TYPE, "") ?: ""
        set(checkedType) {
            editor.putString(CHECKED_TYPE, checkedType)
            editor.apply()
        }


    var FCMToken: String
        get() = prefs.getString(FCM_TOKEN, "") ?: ""
        set(userToken) {
            editor.putString(FCM_TOKEN, userToken)
            editor.apply()
        }

    var isNotification: Boolean
        get() = prefs.getBoolean(IS_NOTIFICATION, true)
        set(isNotification) {
            editor.putBoolean(IS_NOTIFICATION, isNotification)
            editor.apply()
        }


    var userType: String
        get() = prefs.getString(UserType, "") ?: ""
        set(userType) {
            editor.putString(UserType, userType)
            editor.apply()
        }

    var profileUpdate: String
        get() = prefs.getString(Profile, "") ?: ""
        set(profile) {
            editor.putString(Profile, profile)
            editor.apply()
        }
    var countryCode: String
        get() = prefs.getString(Code, "") ?: ""
        set(code) {
            editor.putString(Code, code)
            editor.apply()
        }
    companion object {
        // region "Tags"
        private const val IS_FIRST_TIME = "isFirstTime"

        private const val RefreshToken = "RefreshToken"
        private const val Profile = "Profile"

        private const val BEARER_USER_TOKEN = "BEARER_USER_TOKEN"
        private const val firstLogins = "firstLogin"
        private const val exploreSyn = "exploreSyn"
        private const val myPlaceSyn = "myPlaceSyn"
        private const val bookingSyn = "bookingSyn"
        private const val UserType = "UserType"
        private const val DRIVERAPPROVEDID = "DRIVERAPPROVEDID"


        private const val USER_ID = "USER_ID"
        private const val hostTypes = "hostType"
        private const val Code = "Code"
        private const val hostTypeIds = "hostTypeIds"
        private const val status = "status"
        private const val country = "country"
        private const val browse = "browse"
        private const val state = "state"
        private const val email = "email"
        private const val documentUploadeds = "documentUploadeds"
        private const val isVerifieds = "isVerifieds"
        private const val officialDocs = "officialDocs"

        private const val USER_PREFS = "USER_PREFS"

        private const val USER_LANG = "USER_LANG"

        private const val DEVICE_ID = "DEVICE_ID"


        private const val FCM_TOKEN = "FCM_TOKEN"
        private const val APPROVEDID = "APPROVEDID"
        private const val IS_NOTIFICATION = "IS_NOTIFICATION"
        private const val CHECKED_TYPE = "CHECKED_TYPE"
    }

}