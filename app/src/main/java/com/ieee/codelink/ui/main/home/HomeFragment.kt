package com.ieee.codelink.ui.main.home

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ieee.codelink.R
import com.ieee.codelink.common.extension.onBackPress
import com.ieee.codelink.common.openZoomableImage
import com.ieee.codelink.common.setImageUsingGlide
import com.ieee.codelink.core.BaseFragment
import com.ieee.codelink.core.ResponseState
import com.ieee.codelink.data.remote.BASE_URL_FOR_IMAGE
import com.ieee.codelink.databinding.FragmentHomeBinding
import com.ieee.codelink.domain.models.Comment
import com.ieee.codelink.domain.models.CreatePostModel
import com.ieee.codelink.domain.models.LikeData
import com.ieee.codelink.domain.models.Post
import com.ieee.codelink.domain.models.responses.CommentsResponse
import com.ieee.codelink.domain.models.responses.CreatePostResponse
import com.ieee.codelink.domain.models.responses.LikesResponse
import com.ieee.codelink.domain.models.responses.PostsResponse
import com.ieee.codelink.domain.models.responses.ShareResponse
import com.ieee.codelink.domain.models.toProfileUser
import com.ieee.codelink.ui.adapters.PostsAdapter
import com.ieee.codelink.ui.dialogs.CommentsDialogFragment
import com.ieee.codelink.ui.dialogs.CreatePostDialogFragment
import com.ieee.codelink.ui.dialogs.LikesDialogFragment
import com.ieee.codelink.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override val viewModel: MainViewModel by activityViewModels<MainViewModel>()
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var createPostDialog: CreatePostDialogFragment
    private lateinit var commentsScreen : CommentsDialogFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        loadData()
        setOnClicks()
        setObservers()
    }

    private fun loadData() {
        if (viewModel.isFirstCall()) {
            callData()
        }else{
            viewModel.loadPosts()
        }
    }

    private fun setViews() {
        binding.apply {
            setImageUsingGlide(
                view = binding.addPostBar.ivUserImage,
                image = BASE_URL_FOR_IMAGE + viewModel.getUser().imageUrl,
            )

        }
    }

    private fun setOnClicks() {

        onBackPress {
            requireActivity().finish()
        }

        binding.frameAddPost.setOnClickListener {
            createPostDialog = CreatePostDialogFragment(createPost)
            createPostDialog.show(childFragmentManager, "create_post")
        }
    }

    private fun callData() {
        lifecycleScope.launch {
            viewModel.getHomePosts()
        }
    }

    private fun setObservers() {

        viewModel.postsRequestState.awareCollect { state ->
            postsObserver(state)
        }

        viewModel.createPostsRequestState.awareCollect { state ->
            createPostsObserver(state)
        }

        viewModel.postLikesRequestState.awareCollect { state ->
            postLikesObserver(state)
        }

        viewModel.postCommentsRequestState.awareCollect { state ->
            postCommentsObserver(state)
        }

        viewModel.createCommentsRequestState.awareCollect { state ->
            createComment(state)
        }

        viewModel.sharePostRequestState.awareCollect { state ->
            postSharedObserver(state)
        }

    }
    private fun postSharedObserver(state: ResponseState<ShareResponse>) {
        when (state) {
            is ResponseState.Empty,
            is ResponseState.NotAuthorized,
            is ResponseState.UnKnownError -> {
            }

            is ResponseState.NetworkError -> {
                showToast(getString(R.string.network_error))
            }

            is ResponseState.Error -> {
                com.ieee.codelink.common.showToast(state.message.toString(), requireContext())
                viewModel.sharePostRequestState.value = ResponseState.Empty()
            }

            is ResponseState.Loading -> {
                //todo : if there is time add loading bars to the app
            }

            is ResponseState.Success -> {
                state.data?.let { response ->
                    lifecycleScope.launch {
                        val postId = response.data.post_id
                        postsAdapter.increaseSharesforPost(postId)
                        com.ieee.codelink.common.showToast(getString(R.string.shared),requireContext())
                    }
                }
            }

        }
    }
    private fun createComment(state: ResponseState<CommentsResponse>) {
        when (state) {
            is ResponseState.Empty,
            is ResponseState.NotAuthorized,
            is ResponseState.UnKnownError -> {
            }

            is ResponseState.NetworkError -> {
                showToast(getString(R.string.network_error))
            }

            is ResponseState.Error -> {
                com.ieee.codelink.common.showToast(state.message.toString(), requireContext())
                viewModel.createCommentsRequestState.value = ResponseState.Empty()
            }

            is ResponseState.Loading -> {
                //todo : if there is time add loading bars to the app
            }

            is ResponseState.Success -> {
                state.data?.let { response ->
                    lifecycleScope.launch {
                        val newComment = response.data.comments.last()
                        postsAdapter.increaseCommentCount(newComment.post_id)
                        commentsScreen.addCommentToList(newComment)
                    }
                }
            }

        }
    }
    private fun postCommentsObserver(state: ResponseState<CommentsResponse>) {
        when (state) {
            is ResponseState.Empty,
            is ResponseState.NotAuthorized,
            is ResponseState.UnKnownError -> {
            }

            is ResponseState.NetworkError -> {
                showToast(getString(R.string.network_error))
            }

            is ResponseState.Error -> {
                com.ieee.codelink.common.showToast(state.message.toString(), requireContext())
                viewModel.postLikesRequestState.value = ResponseState.Empty()
            }

            is ResponseState.Loading -> {
                //todo : if there is time add loading bars to the app
            }

            is ResponseState.Success -> {
                state.data?.let { response ->
                    lifecycleScope.launch {
                        openCommentsScreen(response.data.comments , postId = viewModel.openedPostId)
                    }
                }
            }

        }
    }
    private fun postLikesObserver(state: ResponseState<LikesResponse>) {
        when (state) {
            is ResponseState.Empty,
            is ResponseState.NotAuthorized,
            is ResponseState.UnKnownError -> {
            }

            is ResponseState.NetworkError -> {
                showToast(getString(R.string.network_error))
            }

            is ResponseState.Error -> {
                com.ieee.codelink.common.showToast(state.message.toString(), requireContext())
                viewModel.postLikesRequestState.value = ResponseState.Empty()
            }

            is ResponseState.Loading -> {
                //todo : if there is time add loading bars to the app
            }

            is ResponseState.Success -> {
                state.data?.let {response ->
                    lifecycleScope.launch {
                       openLikesScreen(response.data.likeData)
                    }
                }
            }

        }
    }
    private fun createPostsObserver(state: ResponseState<CreatePostResponse>) {
        when (state) {
            is ResponseState.Empty,
            is ResponseState.NotAuthorized,
            is ResponseState.UnKnownError -> {
            }

            is ResponseState.NetworkError -> {
                showToast(getString(R.string.network_error))
            }

            is ResponseState.Error -> {
                com.ieee.codelink.common.showToast(state.message.toString(), requireContext())
                viewModel.createPostsRequestState.value = ResponseState.Empty()
            }

            is ResponseState.Loading -> {
                //todo : if there is time add loading bars to the app
            }

            is ResponseState.Success -> {
                state.data?.let { response ->
                    lifecycleScope.launch {
                        dismissDialog()
                        addPostToList(response.data.post)
                    }
                }
            }

        }
    }

    private fun postsObserver(state: ResponseState<PostsResponse>) {
        when (state) {
            is ResponseState.Empty,
            is ResponseState.NotAuthorized,
            is ResponseState.UnKnownError -> {
                stopLoadingAnimation()
            }

            is ResponseState.NetworkError -> {
                showToast(getString(R.string.network_error))
            }

            is ResponseState.Error -> {
                stopLoadingAnimation()
                com.ieee.codelink.common.showToast(state.message.toString(), requireContext())
                viewModel.postsRequestState.value = ResponseState.Empty()
            }

            is ResponseState.Loading -> {
                startLoadingAnimation()
            }

            is ResponseState.Success -> {
                stopLoadingAnimation()
                state.data?.let { response ->
                    lifecycleScope.launch {
                        setPostsRV(response.data.postData)
                    }
                }
            }

        }
    }
    private fun addPostToList(post: Post) {
       postsAdapter.addPost(post)
    }
    private fun setPostsRV(list : List<Post>) {
        postsAdapter = PostsAdapter(
            list as MutableList<Post>,
            likeClicked = {
                likePost(it)
            },
            commentsClicked = {
                lifecycleScope.launch {
                    viewModel.getPostComments(it)
                }
            },
            sharesClicked = {
               lifecycleScope.launch {
                   viewModel.sharePost(it.id)
               }
            },
            blockClicked = {
                showToast("block")
            },
            saveClicked = {
                showToast("save")
            },
            deleteClicked = {
                showToast("delete")
            },
            openPostImage = { imgUrl, iv ->
                imgUrl?.let {
                    openImageView(
                        imgUrl,
                        iv,
                        requireActivity()
                    )
                }
            },
            showComments = {
                lifecycleScope.launch {
                    viewModel.getPostComments(it)
                }
            },
            showLikes = {
                lifecycleScope.launch {
                    viewModel.getPostLikes(it)
                }
            }
        )
        binding.rvPosts.adapter = postsAdapter
    }

    private fun likePost(post: Post) {
        lifecycleScope.launch {
            val isLiked = viewModel.likePost(post)
            postsAdapter.like(post , isLiked)
        }
    }

    private val createPost: (CreatePostModel) -> Unit = { createPostModel ->
        lifecycleScope.launch {
            viewModel.createPost(createPostModel)
            callData()
            postsAdapter.notifyDataSetChanged()
        }
    }

    private fun dismissDialog() = try {
        createPostDialog.dismiss()
    } catch (_: Exception) {
    }

    private val openImageView : (String, ImageView, Activity) -> Unit = {url,iv,activity ->
        openZoomableImage(
            url,
            activity,
            iv
        )
    }

    private fun openLikesScreen(likeData: List<LikeData>) {
        val likesScreen = LikesDialogFragment(
            likeData as MutableList<LikeData>,
            openProfile = {
                openUserProfile(it)
            },
            followAction = {
                showToast("Follow")
            }
        )
        likesScreen.show(childFragmentManager, "likesScreen")
    }

    private fun openCommentsScreen(comments: List<Comment>,postId: Int?) {
        postId?.let {
             commentsScreen = CommentsDialogFragment(
                comments = comments as MutableList<Comment>,
                postId = postId,
                addComment = { postId, content ->
                    lifecycleScope.launch {
                        viewModel.addComment(postId, content)
                    }
                }
             )
            commentsScreen.show(childFragmentManager, "commentsScreen")
        }
    }

    private fun openUserProfile(likeData: LikeData) {
        val user = likeData.toProfileUser()
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOthersProfile(user))
    }


    private fun startLoadingAnimation() {
        binding.animationView.apply {
            isGone = false
            playAnimation()
        }
    }

    private fun stopLoadingAnimation() {
        binding.animationView.apply {
            isGone = true
            cancelAnimation()
        }
    }
}