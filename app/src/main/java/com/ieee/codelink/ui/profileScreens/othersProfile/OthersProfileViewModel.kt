package com.ieee.codelink.ui.profileScreens.othersProfile

import android.content.Context
import android.net.Uri
import com.ieee.codelink.common.cacheImageToFile
import com.ieee.codelink.common.createMultipartBodyPartFromFile
import com.ieee.codelink.common.getImageFileFromRealPath
import com.ieee.codelink.core.BaseViewModel
import com.ieee.codelink.core.ResponseState
import com.ieee.codelink.data.repository.ProfileRepository
import com.ieee.codelink.data.repository.UserRepository
import com.ieee.codelink.domain.models.User
import com.ieee.codelink.domain.models.responses.AuthResponse
import com.ieee.codelink.domain.models.responses.ProfileUserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class OthersProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val userRepository: UserRepository,
    private val context: Context
) : BaseViewModel() {

    var imageUri: Uri? = null

    val updateProfileState: MutableStateFlow<ResponseState<AuthResponse>?> =
        MutableStateFlow(null)

    val profileUserState: MutableStateFlow<ResponseState<ProfileUserResponse>?> =
        MutableStateFlow(null)

    fun getCachedUser() = userRepository.getCachedUser()
    suspend fun editProfile(
        userId: Int,
        name: String?,
        track: String?,
        bio: String?
    ) {
        val imgPart = if (imageUri != null) {
            val path = cacheImageToFile(context, imageUri!!)
            val file = getImageFileFromRealPath(path)
            createMultipartBodyPartFromFile(file, "imageUrl")
        } else {
            null
        }
        updateProfileState.value = ResponseState.Loading()
        val response = profileRepository.updateProfile(
            userId = userId,
            track = track,
            name = name,
            bio = bio,
            imgPart = imgPart
        )
        updateProfileState.value = handleResponse(response)
    }

    suspend fun getProfileUser(userId: Int) {
        profileUserState.value = ResponseState.Loading()
        val response = profileRepository.getProfileUser(userId)
        profileUserState.value = handleResponse(response)
    }

    fun validateNewData(user: User, name: String, bio: String, track: String): Boolean {
      return name != user.name || bio != user.track || track != user.track || imageUri != null
    }

    fun cacheUser(oldUser: User, newUser: User) {
      newUser.token = oldUser.token
      userRepository.cacheUser(newUser)
    }

}