package com.gentlefit.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gentlefit_prefs")

class UserPreferences @Inject constructor(
    private val context: Context
) {
    companion object {
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_GOAL = stringPreferencesKey("user_goal")
        val SHOW_WEIGHT = booleanPreferencesKey("show_weight")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val USER_HEIGHT = floatPreferencesKey("user_height")
        val USER_BODY_TYPE = stringPreferencesKey("user_body_type")
        val USER_PHOTO_URI = stringPreferencesKey("user_photo_uri")
        val LAST_WEIGHT_DATE = stringPreferencesKey("last_weight_date")
        val USER_WEIGHT = floatPreferencesKey("user_weight")
    }

    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data.map { it[HAS_COMPLETED_ONBOARDING] ?: false }
    val userName: Flow<String> = context.dataStore.data.map { it[USER_NAME] ?: "" }
    val userGoal: Flow<String> = context.dataStore.data.map { it[USER_GOAL] ?: "" }
    val showWeight: Flow<Boolean> = context.dataStore.data.map { it[SHOW_WEIGHT] ?: false }
    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: false }
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[NOTIFICATIONS_ENABLED] ?: true }
    val isPremium: Flow<Boolean> = context.dataStore.data.map { it[IS_PREMIUM] ?: false }
    val userHeight: Flow<Float> = context.dataStore.data.map { it[USER_HEIGHT] ?: 0f }
    val userBodyType: Flow<String> = context.dataStore.data.map { it[USER_BODY_TYPE] ?: "Normale" }
    val userPhotoUri: Flow<String> = context.dataStore.data.map { it[USER_PHOTO_URI] ?: "" }
    val lastWeightDate: Flow<String> = context.dataStore.data.map { it[LAST_WEIGHT_DATE] ?: "" }
    val userWeight: Flow<Float> = context.dataStore.data.map { it[USER_WEIGHT] ?: 0f }

    suspend fun setOnboardingCompleted() { context.dataStore.edit { it[HAS_COMPLETED_ONBOARDING] = true } }
    suspend fun setUserName(name: String) { context.dataStore.edit { it[USER_NAME] = name } }
    suspend fun setUserGoal(goal: String) { context.dataStore.edit { it[USER_GOAL] = goal } }
    suspend fun setShowWeight(show: Boolean) { context.dataStore.edit { it[SHOW_WEIGHT] = show } }
    suspend fun setDarkMode(enabled: Boolean) { context.dataStore.edit { it[DARK_MODE] = enabled } }
    suspend fun setNotificationsEnabled(enabled: Boolean) { context.dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled } }
    suspend fun setPremium(premium: Boolean) { context.dataStore.edit { it[IS_PREMIUM] = premium } }
    suspend fun setUserHeight(height: Float) { context.dataStore.edit { it[USER_HEIGHT] = height } }
    suspend fun setUserBodyType(bodyType: String) { context.dataStore.edit { it[USER_BODY_TYPE] = bodyType } }
    suspend fun setUserPhotoUri(uri: String) { context.dataStore.edit { it[USER_PHOTO_URI] = uri } }
    suspend fun setLastWeightDate(date: String) { context.dataStore.edit { it[LAST_WEIGHT_DATE] = date } }
    suspend fun setUserWeight(weight: Float) { context.dataStore.edit { it[USER_WEIGHT] = weight } }
}
