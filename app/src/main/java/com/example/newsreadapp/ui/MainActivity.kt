package com.example.newsreadapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.newsreadapp.R
import com.example.newsreadapp.db.ArticleDatabase
import com.example.newsreadapp.repository.NewsRepository
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository= NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory= NewsViewModelProviderFactory(application, repository)
        viewModel= ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
        val navHostFragment= supportFragmentManager.findFragmentById(R.id.newsNavHostFrag) as NavHostFragment
        val navController= navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)

    }
}