package com.example.notestrack.core.data.repository

import com.example.notestrack.core.domain.repository.SessionPref


class FakeSessionPrefImplTest:SessionPref {

    override val userId: Long = 0

    override fun setUserId(userId: Long) {

    }
}