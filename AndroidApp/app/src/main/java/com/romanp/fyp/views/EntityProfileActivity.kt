package com.romanp.fyp.views

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.romanp.fyp.R
import com.romanp.fyp.repositories.BookRepository
import com.romanp.fyp.utils.ToastUtils


class EntityProfileActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "EntityProfileActivity"
        const val BOOK_ID = "Book Id Extra"
        const val ENTITY_NAME = "Character Index"
        const val ENTITY_TYPE = "Entity Type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bookId = intent.getLongExtra(BOOK_ID, -1)
        val entityNameID = intent.getStringExtra(ENTITY_NAME)
        val entityType = intent.getIntExtra(ENTITY_TYPE, -1)
        Log.i(TAG, "Opening bookID: $bookId entity name: $entityNameID")

        if (entityType == -1) {
            ToastUtils.toast(applicationContext, "Problem opening entity")
            return
        }

        val bookInfo = BookRepository.getInstance().getBookInfo(applicationContext, bookId)

        val entity = when (EntityType.values()[entityType]) {
            EntityType.CHARACTER -> bookInfo.characters
            EntityType.LOCATION -> bookInfo.locations
        }.find { it.name == entityNameID }

        if (entity == null) {
            ToastUtils.toast(applicationContext, "Problem opening entity")
            return
        }

        Log.i(TAG, "Character: $entity")
        setContentView(R.layout.activity_entity_profile)
        val entityName = findViewById<TextView>(R.id.entityName)
        val entityNer = findViewById<TextView>(R.id.entityNer)
        val chapterMentionsTV = findViewById<TextView>(R.id.chapterMentionsTV)
        entityName.text = entity.name
        entityNer.text = entity.ner
        var chapterMentionsText = ""
        entity.byChapterMentions.forEachIndexed { index, chapter ->
            chapterMentionsText += "${bookInfo.chapters[index].chapterTitle} - ${chapter.size} mentions\n"
        }
        chapterMentionsTV.apply {
            text = chapterMentionsText
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    enum class EntityType(val message: Int) {
        CHARACTER(0),
        LOCATION(1)
    }

}