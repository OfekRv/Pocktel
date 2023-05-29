package horizonstudio.apps.pocktel.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import horizonstudio.apps.pocktel.R
import horizonstudio.apps.pocktel.dto.RuleSetMeta

class RuleSetDialog {
    companion object {
        fun chooseRuleSetNameDialog(context: Context, listener: View.OnClickListener): Dialog {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.choose_rule_set_name)
            dialog.findViewById<Button>(R.id.ok_button).setOnClickListener {
                it.tag = dialog.findViewById<EditText>(R.id.rule_set_name).text.toString()
                listener.onClick(it)
                dialog.dismiss()
            }
            dialog.show()
            return dialog
        }

        fun chooseRuleSetUrlDialog(context: Context, listener: View.OnClickListener): Dialog {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.choose_rule_set_url)
            dialog.findViewById<Button>(R.id.ok_button).setOnClickListener {
                val name: String = dialog.findViewById<EditText>(R.id.rule_set_name).text.toString()
                val url: String = dialog.findViewById<EditText>(R.id.rule_set_url).text.toString()
                it.tag = RuleSetMeta(name, url)
                listener.onClick(it)
                dialog.dismiss()
            }
            dialog.show()
            return dialog
        }
    }
}