package horizonstudio.apps.pocktel.ui.adpters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import horizonstudio.apps.pocktel.R
import horizonstudio.apps.pocktel.dto.YARAMatch

class MatchesListAdapter(
    private val context: Context, private val itemList: List<YARAMatch>
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val viewHolder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.match_row, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val item = getItem(position) as YARAMatch
        viewHolder.titleTextView.text = item.rule
        viewHolder.contentTextView.text = item.strings.joinToString(separator = System.lineSeparator())

        return view!!
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return itemList.size
    }

    private class ViewHolder(view: View) {
        val titleTextView: TextView = view.findViewById(R.id.ruleTitle)
        val contentTextView: TextView = view.findViewById(R.id.matchStrings)
    }
}