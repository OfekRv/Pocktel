package horizonstudio.apps.pocktel.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface.OnClickListener
import horizonstudio.apps.pocktel.R

class ErrorDialog {
    companion object {
        fun errorDialog(message: String,context: Context) {
            errorDialog(message, null, context)
        }

        fun errorDialog(error: String, listener: OnClickListener?, context: Context) {
            AlertDialog.Builder(context)
                .setTitle(context.resources.getString(R.string.errorDialogTitle))
                .setNegativeButton(context.resources.getString(R.string.errorDialogOk), listener)
                .setMessage(error)
                .show()
        }
    }
}