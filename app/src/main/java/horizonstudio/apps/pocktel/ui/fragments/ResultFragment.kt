package horizonstudio.apps.pocktel.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import horizonstudio.apps.pocktel.R
import horizonstudio.apps.pocktel.configurations.Constants.FILENAME_ARGUMENT_NAME
import horizonstudio.apps.pocktel.configurations.Constants.HASH_ARGUMENT_NAME
import horizonstudio.apps.pocktel.configurations.Constants.HASH_COPY_CLIPBOARD_LABEL
import horizonstudio.apps.pocktel.configurations.Constants.RESULT_ARGUMENT_NAME
import horizonstudio.apps.pocktel.configurations.Constants.VT_HASH_SUMMARY_ROUTE
import horizonstudio.apps.pocktel.configurations.Constants.VT_HASH_URL
import horizonstudio.apps.pocktel.databinding.FragmentResultBinding
import horizonstudio.apps.pocktel.dto.ScanResultContract
import horizonstudio.apps.pocktel.exceptions.PocktelInvalidArgumentsException
import horizonstudio.apps.pocktel.ui.adpters.MatchesListAdapter


class ResultFragment : Fragment() {
    private lateinit var filename: String
    private lateinit var hash: String
    private lateinit var scanResult: ScanResultContract

    private var _binding: FragmentResultBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments == null) {
            throw PocktelInvalidArgumentsException("No arguments received")
        }

        filename = arguments?.getString(FILENAME_ARGUMENT_NAME)!!
        hash = arguments?.getString(HASH_ARGUMENT_NAME)!!
        scanResult = arguments?.getParcelable(RESULT_ARGUMENT_NAME)!!
        (activity as AppCompatActivity).supportActionBar?.title = filename
        binding.hash.text = hash

        binding.copyHashButton.setOnClickListener {
            context?.let {
                val clipboard = it.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(HASH_COPY_CLIPBOARD_LABEL, hash)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(
                    it,
                    it.resources.getString(R.string.hashCopyToast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.vtButton.setOnClickListener {
            // TODO: extract to VT service
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$VT_HASH_URL$hash/$VT_HASH_SUMMARY_ROUTE")))
        }

        binding.matchesList.adapter =
            MatchesListAdapter(requireContext(), scanResult.matches.toList())
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}