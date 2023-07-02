package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCorotineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var Repo: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel

    @get:Rule
    var coroutineHandle = MainCorotineRule()
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setupfunction() {
        Repo = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), Repo)
    }

    @After
    fun ended() {
        stopKoin()
    }


    @Test
    fun check_Loading(){
        val rem = ReminderDataItem("Location", "Make butiful", "El Nozha", 5.52934, 6.45566)
        coroutineHandle.pauseDispatcher()
        viewModel.saveReminder(rem)
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()
        coroutineHandle.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
    }

    @Test
    fun empty_Title_Update(){
        val rem = ReminderDataItem("", "Buitiful place", "El nozha",5.52934, 6.45566)
        assertThat(viewModel.validateEnteredData(rem)).isFalse()
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_enter_title)
    }

    @Test
    fun empty_Location_Update(){
        val rem = ReminderDataItem("Map", "Buitiful place", "",5.52934, 6.45566)
        assertThat(viewModel.validateEnteredData(rem)).isFalse()
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_select_location)
    }







}