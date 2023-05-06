package horizonstudio.apps.pocktel.ui.adpters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import horizonstudio.apps.pocktel.R
import horizonstudio.apps.pocktel.dal.entities.RuleSet

class RuleSetListAdapter(
    private val context: Context, private val itemList: List<RuleSet>
) : BaseAdapter() {

    //TODO: add remove button
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val viewHolder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.rule_set_row, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val item = getItem(position) as RuleSet
        viewHolder.titleTextView.text = item.name
        viewHolder.resourceTextView.text = item.path ?: item.url

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
        val titleTextView: TextView = view.findViewById(R.id.ruleSetTitle)
        val resourceTextView: TextView = view.findViewById(R.id.resource)
    }
}