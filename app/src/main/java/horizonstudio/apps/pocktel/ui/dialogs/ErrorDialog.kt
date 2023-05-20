package horizonstudio.apps.pocktel.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface.OnClickListener
import horizonstudio.apps.pocktel.configurations.Constants.ERROR_DIALOG_TITLE

class ErrorDialog {
    companion object {
        fun errorDialog(message: String,context: Context) {
            errorDialog(message, null, context)
        }

        fun errorDialog(error: String, listener: OnClickListener?, context: Context) {
            AlertDialog.Builder(context)
                .setTitle(ERROR_DIALOG_TITLE)
                .setNegativeButton("Ok", listener)
                .setMessage(error)
                .show()
        }
    }
}