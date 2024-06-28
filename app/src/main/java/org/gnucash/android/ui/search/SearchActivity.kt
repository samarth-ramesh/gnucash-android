package org.gnucash.android.ui.search

import android.os.Bundle
import android.view.View
import android.widget.EditText
import org.gnucash.android.R
import org.gnucash.android.ui.common.BaseDrawerActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.consume
import org.gnucash.android.db.adapter.TransactionsDbAdapter
import org.gnucash.android.model.Transaction


class SearchActivity : BaseDrawerActivity() {
    var transactionNoteSearchBox: EditText? = null


    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        transactionNoteSearchBox = findViewById(R.id.editTextText4)
    }

    override fun getContentView(): Int {
        return R.layout.activity_search
    }

    override fun getTitleRes(): Int {
        return R.string.title_search
    }

    private suspend fun getTransactionsMatchingString(query: String, channel: Channel<List<Transaction>>) {
        val transactionDb = TransactionsDbAdapter.getInstance()
        (Dispatchers.IO) {
            val rv = transactionDb.getAllTransactionsHavingString(query)
            channel.send(rv)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun doSearchClick(view: View) {
        if (transactionNoteSearchBox == null) {
            return
        }
        val query = transactionNoteSearchBox!!.text.toString()
        val chan = Channel<List<Transaction>>(CONFLATED)
        GlobalScope.launch {
            getTransactionsMatchingString(query, chan);
            println(chan.receive())
        }


    }
}