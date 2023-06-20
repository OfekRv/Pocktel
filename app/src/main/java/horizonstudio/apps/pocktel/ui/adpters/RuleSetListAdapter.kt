package horizonstudio.apps.pocktel.ui.adpters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import horizonstudio.apps.pocktel.R
import horizonstudio.apps.pocktel.bl.RuleSetBl
import horizonstudio.apps.pocktel.dal.entities.RuleSet
import horizonstudio.apps.pocktel.dal.repositories.RuleSetRepository
import horizonstudio.apps.pocktel.ui.dialogs.ErrorDialog
import horizonstudio.apps.pocktel.ui.dialogs.ErrorDialog.Companion.errorDialog

class RuleSetListAdapter(
    // TODO: Consider remove the item list and just use the bl
    private val context: Context, private val bl: RuleSetBl, private var itemList: List<RuleSet>
) : BaseAdapter() {
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
        viewHolder.removeButton.setOnClickListener {
            deleteRuleSet(item)
        }

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
        val removeButton: Button = view.findViewById(R.id.remove)
    }

    private fun deleteRuleSet(item: RuleSet) {
        if (bl.delete(item)) {
            itemList = bl.findAll()
            notifyDataSetChanged()
        } else {
            errorDialog("Could not delete rule set", context)
        }
    }
}