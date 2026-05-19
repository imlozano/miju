package com.julian.miju2.DashboardModule

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class DashboardViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("users")
}
