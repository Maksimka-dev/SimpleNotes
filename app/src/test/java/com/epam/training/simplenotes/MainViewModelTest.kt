package com.epam.training.simplenotes

import android.arch.lifecycle.Observer
import com.epam.training.simplenotes.action.MainActivityViewAction
import com.epam.training.simplenotes.action.RecyclerViewAction
import com.epam.training.simplenotes.action.RefreshingViewAction
import com.epam.training.simplenotes.entity.VisibleNote
import com.epam.training.simplenotes.model.MainModel
import com.epam.training.simplenotes.viewmodel.MainViewModel
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class MainViewModelTest : Spek({
    emulateInstantTaskExecutorRule()

    val mainModelMock: MainModel = mockk(relaxed = true)

    val testMainViewModel = MainViewModel(
        mainModelMock
    )

    val signOutResultObserver: Observer<Boolean> = mockk(relaxed = true)
    val activityViewActionObserver: Observer<MainActivityViewAction> = mockk(relaxed = true)
    val refreshingActionObserver: Observer<RefreshingViewAction> = mockk(relaxed = true)
    val recyclerViewActionObserver: Observer<RecyclerViewAction> = mockk(relaxed = true)

    describe("MainViewModel test") {
        beforeEachTest {
            clearMocks(mainModelMock)
        }

        context("sign out") {
            beforeEachTest {
                testMainViewModel.signOutResult.observeForever(signOutResultObserver)
                every { mainModelMock.signOut(any()) } answers {
                    (this.arg<() -> Unit>(0)).invoke()
                }
                testMainViewModel.signOut()
            }

            it("sign out: success scenario") {
                verify { signOutResultObserver.onChanged(true) }
            }

            afterEachTest {
                testMainViewModel.signOutResult.removeObserver(signOutResultObserver)
            }
        }

        context("create new note") {
            beforeEachTest {
                testMainViewModel.activityViewAction.observeForever(activityViewActionObserver)
                testMainViewModel.onFloatingButtonClicked()
            }

            it("should go to new note details") {
                verify { activityViewActionObserver.onChanged(MainActivityViewAction.GoToNewNoteDetails) }
            }

            afterEachTest {
                testMainViewModel.activityViewAction.removeObserver(activityViewActionObserver)
            }
        }

        context("open existing note") {
            val note = VisibleNote(id = "testId")

            beforeEachTest {
                testMainViewModel.activityViewAction.observeForever(activityViewActionObserver)
                testMainViewModel.onNoteClicked(note)
            }

            it("should go to existing note details") {
                verify { activityViewActionObserver.onChanged(MainActivityViewAction.GoToExistingNoteDetails(note.id)) }
            }

            afterEachTest {
                testMainViewModel.activityViewAction.removeObserver(activityViewActionObserver)
            }
        }

        describe("load all notes") {
            val notes: MutableList<VisibleNote> = mutableListOf()

            beforeEachTest {
                testMainViewModel.refreshingAction.observeForever(refreshingActionObserver)
                testMainViewModel.recyclerViewAction.observeForever(recyclerViewActionObserver)
            }

            context("load all notes: success scenario") {
                beforeEachTest {
                    every { mainModelMock.loadUserNotes(any(), any()) } answers {
                        (this.arg<(MutableList<VisibleNote>) -> Unit>(0)).invoke(notes)
                    }
                    testMainViewModel.loadUserNotes()
                }

                it("should start refreshing animation") {
                    verify { refreshingActionObserver.onChanged(RefreshingViewAction.Start) }
                }

                it("should stop refreshing animation") {
                    verify { refreshingActionObserver.onChanged(RefreshingViewAction.Stop) }
                }

                it("should update RecyclerView with loaded notes") {
                    verify { recyclerViewActionObserver.onChanged(RecyclerViewAction.UpdateItems(notes)) }
                }
            }

            context("load all notes: error scenario") {
                val exception = Exception()

                beforeEachTest {
                    every { mainModelMock.loadUserNotes(any(), any()) } answers {
                        (this.arg<(Throwable) -> Unit>(1)).invoke(exception)
                    }
                    testMainViewModel.loadUserNotes()
                }

                it("should start refreshing animation") {
                    verify { refreshingActionObserver.onChanged(RefreshingViewAction.Start) }
                }

                it("should stop refreshing animation") {
                    verify { refreshingActionObserver.onChanged(RefreshingViewAction.Stop) }
                }
            }

            afterEachTest {
                testMainViewModel.refreshingAction.removeObserver(refreshingActionObserver)
                testMainViewModel.recyclerViewAction.removeObserver(recyclerViewActionObserver)
            }
        }
    }

})