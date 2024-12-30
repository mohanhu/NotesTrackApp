package com.example.notestrack.addmenu.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.notestrack.addmenu.MainCoroutineRule
import com.example.notestrack.addmenu.data.model.local.CategoryTableEntity
import com.example.notestrack.addmenu.data.repository.FakeCategoryRepositoryTest
import com.example.notestrack.core.data.repository.FakeSessionPrefImplTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddCategoryViewModelTest{

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var addCategoryViewModel: AddCategoryViewModel

    @Before
    fun setUp(){
        addCategoryViewModel = AddCategoryViewModel(
            FakeCategoryRepositoryTest(), FakeSessionPrefImplTest()
        )
    }

    @Test
    fun `check insert logic if done or error`(){
        val categoryTableEntity = CategoryTableEntity(categoryId = 1, userId = 2, categoryName = "de")
        val result = addCategoryViewModel.insertCategory(categoryTableEntity)
        assertThat(result).contains(categoryTableEntity)
    }

    @Test
    fun `check insert list if not Empty`(){
        val categoryTableEntity = CategoryTableEntity(categoryId = 1, userId = 2, categoryName = "de")
        val result = addCategoryViewModel.insertCategory(categoryTableEntity)
        assertThat(result).isNotEmpty()
    }
}