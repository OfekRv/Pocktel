package horizonstudio.apps.pocktel.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface.OnClickListener
import horizonstudio.apps.pocktel.configurations.Constants.ERROR_DIALOG_TITLE

class ErrorDialog {
    companion object {
        fun errorDialog(context: Context, message: String) {
            errorDialog(context, message, null)
        }

        fun errorDialog(context: Context, error: String, listener: OnClickListener?) {
            AlertDialog.Builder(context)
                .setTitle(ERROR_DIALOG_TITLE)
                .setNegativeButton("Ok", listener)
                .setMessage(error)
                .show()
        }
    }
}