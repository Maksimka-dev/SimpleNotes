package com.epam.training.simplenotes

import android.arch.lifecycle.Observer
import com.epam.training.simplenotes.action.DialogViewAction
import com.epam.training.simplenotes.action.EditingNoteState
import com.epam.training.simplenotes.action.NoteFillViewAction
import com.epam.training.simplenotes.entity.VisibleNote
import com.epam.training.simplenotes.model.NoteDetailsModel
import com.epam.training.simplenotes.viewmodel.NoteDetailsViewModel
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
class NoteDetailsViewModelTest : Spek({
    emulateInstantTaskExecutorRule()

    val detailsModelMock: NoteDetailsModel = mockk(relaxed = true)

    val testMainViewModel = NoteDetailsViewModel(
        detailsModelMock
    )

    val dialogViewActionObserver: Observer<DialogViewAction> = mockk(relaxed = true)
    val editingNoteStateObserver: Observer<EditingNoteState> = mockk(relaxed = true)
    val noteFillViewActionObserver: Observer<NoteFillViewAction> = mockk(relaxed = true)

    describe("NoteDetailsViewModel test") {
        beforeEachTest {
            clearMocks(detailsModelMock)

            testMainViewModel.editingNoteState.observeForever(editingNoteStateObserver)
            testMainViewModel.dialogViewAction.observeForever(dialogViewActionObserver)
            testMainViewModel.noteFillViewAction.observeForever(noteFillViewActionObserver)
        }

//        context("test generating unique id"){
//            Assert.assertEquals()
//        }
        context("loading note: success scenario") {
            val noteId = "testId"
            val note = VisibleNote(id = noteId)

            beforeEachTest {
                every { detailsModelMock.downloadNote(noteId, any(), any()) } answers {
                    (this.arg<(VisibleNote) -> Unit>(1)).invoke(note)
                }
                testMainViewModel.loadNote(noteId)
            }

            it("should notify that loading is started") {
                verify { editingNoteStateObserver.onChanged(EditingNoteState.Start) }
            }

            it("should start loading dialog") {
                verify { dialogViewActionObserver.onChanged(DialogViewAction.Show) }
            }

            it("should fill layout views") {
                verify { noteFillViewActionObserver.onChanged(NoteFillViewAction.FillNote(note)) }
            }

            it("should stop loading dialog") {
                verify { dialogViewActionObserver.onChanged(DialogViewAction.Hide) }
            }
        }

        context("loading note: error scenario") {
            val noteId = "testId"
            val exception = Exception()

            beforeEachTest {
                every { detailsModelMock.downloadNote(noteId, any(), any()) } answers {
                    (this.arg<(Throwable) -> Unit>(2)).invoke(exception)
                }
                testMainViewModel.loadNote(noteId)
            }

            it("should notify that loading is started") {
                verify { editingNoteStateObserver.onChanged(EditingNoteState.Start) }
            }

            it("should start loading dialog") {
                verify { dialogViewActionObserver.onChanged(DialogViewAction.Show) }
            }

            it("should report error") {
                verify { editingNoteStateObserver.onChanged(EditingNoteState.Error) }
            }

            it("should stop loading dialog") {
                verify { dialogViewActionObserver.onChanged(DialogViewAction.Hide) }
            }
        }

        context("saving note: success scenario") {
            val noteId = "testId"
            val note = VisibleNote(id = noteId)

            beforeEachTest {
                every { detailsModelMock.saveNote(note, any(), any()) } answers {
                    (this.arg<() -> Unit>(1)).invoke()
                }
                testMainViewModel.saveNote(note)
            }

            it("should notify that loading is started") {
                verify { editingNoteStateObserver.onChanged(EditingNoteState.Start) }
            }

            it("should start loading dialog") {
                verify { dialogViewActionObserver.onChanged(DialogViewAction.Show) }
            }

            it("should notify that note is saved") {
                verify { editingNoteStateObserver.onChanged(EditingNoteState.Success) }
            }

            it("should stop loading dialog") {
                verify { dialogViewActionObserver.onChanged(DialogViewAction.Hide) }
            }
        }

        context("saving note: error scenario") {
            val noteId = "testId"
            val note = VisibleNote(id = noteId)
            val exception = Exception()

            beforeEachTest {
                every { detailsModelMock.saveNote(note, any(), any()) } answers {
                    (this.arg<(Throwable) -> Unit>(2)).invoke(exception)
                }
                testMainViewModel.saveNote(note)
            }

            it("should notify that loading is started") {
                verify { editingNoteStateObserver.onChanged(EditingNoteState.Start) }
            }

            it("should start loading dialog") {
                verify { dialogViewActionObserver.onChanged(DialogViewAction.Show) }
            }

            it("should report error") {
                verify { editingNoteStateObserver.onChanged(EditingNoteState.Error) }
            }

            it("should stop loading dialog") {
                verify { dialogViewActionObserver.onChanged(DialogViewAction.Hide) }
            }
        }

        context("deleting note: success scenario") {
            var noteId = ""

            beforeEachTest {
                every { detailsModelMock.newNoteId() } returns ("NOTE_${System.currentTimeMillis()}")
                noteId = testMainViewModel.getNoteId()
                every { detailsModelMock.deleteNote(noteId, any(), any()) } answers {
                    (this.arg<() -> Unit>(1)).invoke()
                }
                testMainViewModel.onDeleteButtonClicked()
            }

            it("should notify that loading is started") {
                verify { editingNoteStateObserver.onChanged(EditingNoteState.Start) }
            }

            it("should start loading dialog") {
                verify { dialogViewActionObserver.onChanged(DialogViewAction.Show) }
            }

            it("should notify that note is deleted") {
                verify { editingNoteStateObserver.onChanged(EditingNoteState.Success) }
            }

            it("should stop loading dialog") {
                verify { dialogViewActionObserver.onChanged(DialogViewAction.Hide) }
            }
        }

        context("deleting note: error scenario") {
            var noteId = ""
            val exception = Exception()

            beforeEachTest {
                every { detailsModelMock.newNoteId() } returns ("NOTE_${System.currentTimeMillis()}")
                noteId = testMainViewModel.getNoteId()
                every { detailsModelMock.deleteNote(noteId, any(), any()) } answers {
                    (this.arg<(Throwable) -> Unit>(2)).invoke(exception)
                }
                testMainViewModel.onDeleteButtonClicked()
            }

            it("should notify that loading is started") {
                verify { editingNoteStateObserver.onChanged(EditingNoteState.Start) }
            }

            it("should start loading dialog") {
                verify { dialogViewActionObserver.onChanged(DialogViewAction.Show) }
            }

            it("should report error") {
                verify { editingNoteStateObserver.onChanged(EditingNoteState.Error) }
            }

            it("should stop loading dialog") {
                verify { dialogViewActionObserver.onChanged(DialogViewAction.Hide) }
            }
        }

        afterEachTest {
            testMainViewModel.editingNoteState.removeObserver(editingNoteStateObserver)
            testMainViewModel.dialogViewAction.removeObserver(dialogViewActionObserver)
            testMainViewModel.noteFillViewAction.removeObserver(noteFillViewActionObserver)
        }
    }
})