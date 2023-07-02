package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.MainCorotineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var Repo: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel

    @get:Rule
    var coroutineHandle = MainCorotineRule()
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setupfunction() {
        Repo = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), Repo)
    }

    @After
    fun ended() {
        stopKoin()
    }

    @Test
    fun check_listIsNotEmpty() = coroutineHandle.runBlockingTest {
        val rem = ReminderDTO("Location", "myLOCATION", "el Nozha", 81.214367813, 65.212312313)
        Repo.saveReminder(rem)
        viewModel.loadReminders()
        assertThat(viewModel.remindersList.getOrAwaitValue()).isNotEmpty()
    }

    @Test
    fun check_Loading(){
        coroutineHandle.pauseDispatcher()
        viewModel.loadReminders()
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()
        coroutineHandle.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
    }

    @Test
    fun updateErrorMessage()
    {
        coroutineHandle.pauseDispatcher()
        Repo.setReturnError(true)
        viewModel.loadReminders()
        coroutineHandle.resumeDispatcher()
        assertThat(viewModel.showSnackBar.getOrAwaitValue()).isEqualTo("Remeinder not found")

    }



}