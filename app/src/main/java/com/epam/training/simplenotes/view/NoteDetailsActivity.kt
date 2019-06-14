package com.epam.training.simplenotes.view

import android.app.Activity
import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.epam.training.simplenotes.R
import com.epam.training.simplenotes.action.DialogViewAction
import com.epam.training.simplenotes.action.EditingNoteState
import com.epam.training.simplenotes.action.NoteFillViewAction
import com.epam.training.simplenotes.entity.VisibleNote
import com.epam.training.simplenotes.util.isOnline
import com.epam.training.simplenotes.viewmodel.NoteDetailsViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class NoteDetailsActivity : AppCompatActivity() {

    private companion object {
        private const val CHOOSE_IMAGE_REQUEST_CODE = 1
        private const val INTENT_TYPE_IMAGE = "image/*"

        private const val IMAGE_URI = "IMAGE_URI"
    }

    private val noteViewModel: NoteDetailsViewModel by viewModel()

    private val toolbar: Toolbar by lazy {
        findViewById<Toolbar>(R.id.note_details_toolbar)
    }
    private val noteTitle: EditText by lazy {
        findViewById<EditText>(R.id.note_details_title)
    }
    private val noteText: EditText by lazy {
        findViewById<EditText>(R.id.note_details_text)
    }
    private val noteImage: ImageView by lazy {
        findViewById<ImageView>(R.id.note_details_image)
    }
    private val dialog: ProgressDialog? by lazy {
        ProgressDialog(this).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setTitle(getString(R.string.dialog_loading_title))
            setMessage(getString(R.string.dialog_auth_loading_text))
            create()
        }
    }

    private var currentImageUri: Uri? = null
    private var currentBitmap: Bitmap? = null
    private var currentNoteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        setSupportActionBar(toolbar)

        if (savedInstanceState == null && isModeEdit()) {
            currentNoteId = intent.getStringExtra(MainActivity.NOTE_ID)?.apply {
                noteViewModel.loadNote(this)
            }
        }

        Log.d("DialogHash", dialog.hashCode().toString())
        noteViewModel.editingNoteState.observe(this, Observer {
            when (it) {
//                EditingNoteState.Start -> {
//                    //start progress bar animation
//                    dialog?.show()
//                }
                EditingNoteState.Success -> {
//                    dialog?.hide()
//                    dialog?.dismiss()
                    finish()
                }
                EditingNoteState.Error -> {
                    Toast.makeText(this, R.string.failed, Toast.LENGTH_SHORT).show()
                }
            }
        })

        noteViewModel.dialogViewAction.observe(this, Observer {
            when (it) {
                DialogViewAction.Show -> dialog?.show()
                DialogViewAction.Hide -> dialog?.dismiss()
            }
        })

        noteViewModel.noteFillViewAction.observe(this, Observer {
            when (it) {
                is NoteFillViewAction.FillNote -> {
                    renderNote(it.note)
                    currentBitmap = it.note.imageBitmap
                }
            }
        })
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        currentImageUri?.let {
            outState?.putString(IMAGE_URI, it.toString())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState?.getString(IMAGE_URI)?.let { uriInString ->
            currentImageUri = Uri.parse(uriInString)
            currentBitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageUri)
            noteImage.setImageBitmap(currentBitmap)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note_details, menu)

        if (isModeEdit().not()) {
            menu?.findItem(R.id.menu_action_delete_note)?.isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when (item?.itemId) {
            R.id.menu_action_save -> {
                if (isOnline(this)) {
                    val note = VisibleNote(
                        id = noteViewModel.getNoteId(),
                        title = noteTitle.text.toString(),
                        text = noteText.text.toString(),
                        imageBitmap = currentBitmap
                    )
                    noteViewModel.saveNote(note)
                } else {
                    Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show()
                }

                true
            }
            R.id.menu_action_attach_image -> {
                pickImage()

                true
            }
            R.id.menu_action_delete_note -> {
                if (isOnline(this)) {
                    noteViewModel.onDeleteButtonClicked()
                } else {
                    Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = INTENT_TYPE_IMAGE
        }

        intent.resolveActivity(packageManager)?.let {
            startActivityForResult(intent, CHOOSE_IMAGE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHOOSE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            currentImageUri = data?.data
            currentBitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageUri)
            noteImage.setImageBitmap(currentBitmap)
        } else {
            Toast.makeText(this, R.string.unable_to_load_image, Toast.LENGTH_SHORT).show()
        }

    }

    private fun renderNote(visibleNote: VisibleNote) {
        noteTitle.setText(visibleNote.title)
        noteText.setText(visibleNote.text)
        noteImage.setImageBitmap(visibleNote.imageBitmap)
    }

    private fun isModeEdit() = intent.extras != null
}