package com.epam.training.simplenotes.view

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.epam.training.simplenotes.R
import com.epam.training.simplenotes.action.MainActivityViewAction
import com.epam.training.simplenotes.action.RecyclerViewAction
import com.epam.training.simplenotes.action.RefreshingViewAction
import com.epam.training.simplenotes.entity.VisibleNote
import com.epam.training.simplenotes.viewmodel.MainViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        const val NOTE_ID = "NOTE_ID"
    }

    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: NotesRecyclerViewAdapter
    private lateinit var swipeOnRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_coordinator)

        initUI()
        setSupportActionBar(toolbar)

        setRecyclerView()
        observeViewModel()

        fab.setOnClickListener {
            mainViewModel.onFloatingButtonClicked()
        }
    }

    private fun initSwipeOnRefresh() {
        swipeOnRefresh = findViewById(R.id.swipe_refresh)
        swipeOnRefresh.setOnRefreshListener(this)
    }

    override fun onStart() {
        super.onStart()

        mainViewModel.loadUserNotes()
    }

    private fun initUI() {
        recyclerView = findViewById(R.id.recycler_view)
        fab = findViewById(R.id.fab)
        toolbar = findViewById(R.id.toolbar)
        initSwipeOnRefresh()
    }

    private fun setRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = NotesRecyclerViewAdapter(mutableListOf())

        adapter.noteListener = object : NotesRecyclerViewAdapter.NoteListener {
            override fun onNoteClicked(position: Int, note: VisibleNote, view: View) {
                mainViewModel.onNoteClicked(note)
            }
        }

        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        mainViewModel.recyclerViewAction.observe(this, Observer {
            when (it) {
                is RecyclerViewAction.UpdateItems -> {
                    adapter.items = it.items
                    adapter.notifyDataSetChanged()
                }
            }
        })

        mainViewModel.signOutResult.observe(this, Observer {
            when (it) {
                true -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                false -> {
                    Toast.makeText(this, R.string.sign_out_failed, Toast.LENGTH_SHORT).show()
                }
            }
        })

        mainViewModel.activityViewAction.observe(this, Observer {
            when (it) {
                MainActivityViewAction.GoToNewNoteDetails -> {
                    val intent = Intent(this, NoteDetailsActivity::class.java)
                    startActivity(intent)
                }
                is MainActivityViewAction.GoToExistingNoteDetails -> {
                    val intent = Intent(this, NoteDetailsActivity::class.java)
                    intent.putExtra(NOTE_ID, it.noteId)
                    startActivity(intent)
                }
            }
        })

        mainViewModel.refreshingAction.observe(this, Observer {
            when (it) {
                RefreshingViewAction.Start -> swipeOnRefresh.isRefreshing = true
                RefreshingViewAction.Stop -> swipeOnRefresh.isRefreshing = false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_sign_out -> {
                mainViewModel.signOut()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRefresh() {
        mainViewModel.loadUserNotes()
    }
}
