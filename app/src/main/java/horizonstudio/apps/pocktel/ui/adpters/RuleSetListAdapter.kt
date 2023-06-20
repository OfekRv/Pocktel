package horizonstudio.apps.pocktel.ui.adpters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.SpinnerAdapter
import android.widget.TextView
import horizonstudio.apps.pocktel.R
import horizonstudio.apps.pocktel.bl.RuleSetBl
import horizonstudio.apps.pocktel.dal.entities.RuleSet
import horizonstudio.apps.pocktel.ui.dialogs.ErrorDialog.Companion.errorDialog

class RuleSetListAdapter(
    private val context: Context, private val bl: RuleSetBl, private var itemList: List<RuleSet>
) : BaseAdapter(), SpinnerAdapter {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val chosenRule: TextView

        if (view == null) {
            view = LayoutInflater.from(context)
                .inflate(android.R.layout.simple_spinner_item, parent, false)
            chosenRule = view.findViewById(android.R.id.text1)

            view.tag = chosenRule
        } else {
            chosenRule = view.tag as TextView
        }

        chosenRule.text = (getItem(position) as RuleSet).name
        return view!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        val viewHolder: RuleSetViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.rule_set_row, parent, false)
            viewHolder = RuleSetViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as RuleSetViewHolder
        }

        val item = getItem(position) as RuleSet
        viewHolder.titleTextView.text = item.name
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

    private class RuleSetViewHolder(view: View) {
        val titleTextView: TextView = view.findViewById(R.id.ruleSetTitle)
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