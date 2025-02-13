package com.klipy.demoapp.presentation.features.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klipy.demoapp.data.infrastructure.Measurements
import com.klipy.demoapp.data.infrastructure.ScreenMeasurementsProvider
import com.klipy.demoapp.domain.KlipyRepository
import com.klipy.demoapp.domain.models.Category
import com.klipy.demoapp.domain.models.MediaItem
import com.klipy.demoapp.domain.models.isAD
import com.klipy.demoapp.presentation.features.conversation.model.ClipMessage
import com.klipy.demoapp.presentation.features.conversation.model.GifMessage
import com.klipy.demoapp.presentation.features.conversation.model.MediaType
import com.klipy.demoapp.presentation.features.conversation.model.TextMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConversationViewModel(
    conversationId: String?,
    private val klipyRepository: KlipyRepository,
    private val screenMeasurementsProvider: ScreenMeasurementsProvider
) : ViewModel() {

    private val _viewState = MutableStateFlow(ConversationViewState(conversationId))
    val viewState: StateFlow<ConversationViewState> = _viewState.asStateFlow()

    private var interactor: MediaTypeDataInteractor? = null

    fun onScreenMeasured(
        containerWidthDp: Int,
        containerHeightDp: Int,
        screenWidthPx: Int,
        screenHeightPx: Int
    ) {
        screenMeasurementsProvider.apply {
            mediaSelectorContainer = Measurements(containerWidthDp, containerHeightDp)
            device = Measurements(screenWidthPx, screenHeightPx)
        }
    }

    fun fetchInitialData() {
        setLoading()
        viewModelScope.launch {
            val data = klipyRepository.getAvailableMediaTypes()
            val chosenMediaType = data.firstOrNull()
            _viewState.update {
                it.copy(
                    mediaTypes = data,
                    chosenMediaType = chosenMediaType
                )
            }
            chosenMediaType?.let {
                interactor = createMediaTypeInteractor(it)
                interactor?.fetchInitialData()
            }
        }
    }

    fun onMessageTextChanged(text: String) {
        _viewState.update {
            it.copy(messageText = text)
        }
    }

    fun onSendClicked() {
        if (_viewState.value.messageText.isNotBlank()) {
            _viewState.update {
                val textMessage = TextMessage(
                    isFromCurrentUser = true,
                    text = it.messageText
                )
                it.copy(
                    messageText = "",
                    messages = it.messages + textMessage
                )
            }
        }
    }

    fun onMediaTypeClicked(mediaType: MediaType) {
        searchJob?.cancel()
        _viewState.update {
            it.copy(
                chosenMediaType = mediaType,
                mediaItems = emptyList(),
                searchInput = ""
            )
        }
        interactor = createMediaTypeInteractor(mediaType)
        setLoading()
        interactor?.fetchInitialData()
    }

    fun onLoadMoreItems() {
        setLoading()
        interactor?.fetchMoreData()
    }

    fun onCategoryClicked(category: Category) {
        if (category != _viewState.value.chosenCategory) {
            searchJob?.cancel()
            _viewState.update {
                it.copy(
                    chosenCategory = category,
                    mediaItems = emptyList(),
                    isLoading = true,
                    searchInput = ""
                )
            }
            interactor?.onCategoryChosen(category)
        }
    }

    fun onSearchInputEntered(input: String) {
        _viewState.update {
            it.copy(
                searchInput = input
            )
        }
        viewModelScope.debounceSearch {
            if (input.isNotBlank()) {
                _viewState.update {
                    it.copy(
                        chosenCategory = null,
                        mediaItems = emptyList(),
                        isLoading = true
                    )
                }
                interactor?.onSearchInputEntered(input)
            }
        }
    }

    fun onMediaItemClicked(mediaItem: MediaItem) {
        viewModelScope.launch {
            _viewState.update {
                val message = if (mediaItem.mediaType == MediaType.CLIP) {
                    ClipMessage(
                        isFromCurrentUser = true,
                        url = mediaItem.highQualityMetaData!!.url,
                        width = mediaItem.highQualityMetaData.width,
                        height = mediaItem.highQualityMetaData.height
                    )
                } else {
                    GifMessage(
                        isFromCurrentUser = true,
                        url = mediaItem.lowQualityMetaData!!.url,
                        width = mediaItem.lowQualityMetaData.width,
                        height = mediaItem.lowQualityMetaData.height
                    )
                }
                it.copy(
                    messages = it.messages + message
                )
            }
            klipyRepository.triggerShare(_viewState.value.chosenMediaType!!, mediaItem.id)
        }
    }

    fun onMediaItemSelected(mediaItem: MediaItem) {
        viewModelScope.launch {
            klipyRepository.triggerView(_viewState.value.chosenMediaType!!, mediaItem.id)
        }
        _viewState.update {
            it.copy(
                selectedMediaItem = mediaItem,
                playingClip = null
            )
        }
    }

    fun report(mediaItem: MediaItem, reason: String) {
        viewModelScope.launch {
            klipyRepository.report(_viewState.value.chosenMediaType!!, mediaItem.id, reason)
        }
    }

    fun hideFromRecent(mediaItem: MediaItem) {
        viewModelScope.launch {
            klipyRepository.hideFromRecent(_viewState.value.chosenMediaType!!, mediaItem.id)
                .onSuccess {
                    _viewState.update {
                        val newList = it.mediaItems.toMutableList()
                        newList.remove(mediaItem)
                        if (newList.all { it.isAD() }) {
                            newList.clear()
                        }
                        it.copy(
                            mediaItems = newList
                        )
                    }
                }
        }
    }

    fun onClipMessageClicked(clipMessage: ClipMessage) {
        _viewState.update {
            it.copy(
                playingClip = if (it.playingClip == clipMessage) null else clipMessage
            )
        }
    }

    private fun setLoading() {
        _viewState.update {
            it.copy(isLoading = true)
        }
    }

    private fun createMediaTypeInteractor(mediaType: MediaType) = MediaTypeDataInteractor(
        mediaType = mediaType,
        klipyRepository = klipyRepository,
        coroutineScope = viewModelScope,
        onInitialDataFetched = { categories, chosenCategory, data ->
            viewModelScope.launch(
                Dispatchers.Default
            ) {
                withContext(Dispatchers.Main) {
                    _viewState.update {
                        it.copy(
                            isLoading = false,
                            categories = categories,
                            chosenCategory = chosenCategory,
                            mediaItems = data
                        )
                    }
                }
            }
        },
        onMoreDataFetched = { data ->
            viewModelScope.launch(
                Dispatchers.Default
            ) {
                withContext(Dispatchers.Main) {
                    _viewState.update {
                        it.copy(
                            isLoading = false,
                            mediaItems = it.mediaItems + data
                        )
                    }
                }
            }
        }
    )

    private var searchJob: Job? = null
    private fun CoroutineScope.debounceSearch(
        action: () -> Unit
    ) {
        searchJob?.cancel()
        searchJob = launch {
            delay(ONE_SECOND)
            action.invoke()
        }
    }

    private companion object {
        const val ONE_SECOND = 1_000L
    }
}