package horizonstudio.apps.pocktel.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface.OnClickListener

class ErrorDialog {
    companion object {
        fun errorDialog(context: Context, message: String) {
            errorDialog(context, message, null)
        }

        fun errorDialog(context: Context, error: String, listener: OnClickListener?) {
            AlertDialog.Builder(context)
                .setTitle("Error")
                .setNegativeButton("Ok", listener)
                .setMessage(error)
                .show()
        }
    }
}